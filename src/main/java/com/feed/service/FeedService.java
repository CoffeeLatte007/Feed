package com.feed.service;

import com.alibaba.fastjson.JSON;
import com.feed.FeedConfig;
import com.feed.KryoUtils;
import com.feed.jedis.JedisClient;
import com.feed.mapper.FeedModelMapper;
import com.feed.mapper.ImageMapper;
import com.feed.mapper.UserMapper;
import com.feed.model.*;
import com.sun.tools.classfile.Opcode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Created by Administrator on 2016/8/1.
 */
@Service
public class FeedService {
    @Autowired
    JedisClient jedisClient;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ImageMapper imageMapper;
    @Autowired
    FeedModelMapper feedModelMapper;
    @Autowired
    RMQService rmqService;
    public MyResult initAllPeople() {
        UserExample userExample = new UserExample();
        List<User> users = userMapper.selectByExample(userExample);
        List<String> uids = new ArrayList<>();
        for (User user : users) {
            String uid = String.valueOf(user.getId())+ FeedConfig.dynamic;
            uids.add(uid);
            jedisClient.zremrangebyrank(uid,0,-1);//全部删除
        }
        //查出每个用户发的动态并进行更新
        for (int i = 0; i < users.size(); i++) {
            int id = users.get(i).getId();
            ImageExample example = new ImageExample();
            ImageExample.Criteria criteria = example.createCriteria();
            criteria.andUserIdEqualTo(id);
            List<Image> images = imageMapper.selectByExample(example);
            Map<String,Double> imageMap = new HashMap<>();
            for (Image image : images) {
                Long time = 0-image.getCreatedDate().getTime();
                Double dtime = Double.valueOf(time);
                imageMap.put(String.valueOf(image.getId()), Double.valueOf(dtime));
//                System.out.println((double)(image.getCreatedDate().getTime()));
            }
            jedisClient.zadd(uids.get(i),imageMap);
        }
        return MyResult.ok();
    }

    /**
     * 采取策略先更新feed,然后在从feed中取
     * @param uuid
     * @return
     */
    public void updateFeed(String uuid) {
        //第一步首先获取该用户的所有关注列表
        Map<String, String> map = jedisClient.hgetAll(uuid + FeedConfig.following);
        //保存上一次的时间
        Double lasttime = Double.valueOf(0000000000000L);
        //获取上一次的时间
        Set<Tuple> set = jedisClient.zrangewithscores(uuid + FeedConfig.feed, Long.valueOf("-1"), Long.valueOf("-1"));
        if (!CollectionUtils.isEmpty(set)) {
            lasttime = set.iterator().next().getScore();
        }
        //记录需要更新的feed
        Map<String, Double> updateFeed = new HashMap<>();

        for (Map.Entry<String, String> e : map.entrySet()) {
            String uuidDy = e.getKey() + FeedConfig.dynamic;
            //根据uuid查询最新的
            Set<Tuple> upset =jedisClient.zrangeByScoreWithScore(uuidDy,"-inf" , String.valueOf(lasttime));
            if (!CollectionUtils.isEmpty(upset)){
                for (Tuple tuple : upset) {
                    updateFeed.put(tuple.getElement(),tuple.getScore());
                }
            }
           }
        jedisClient.zadd(uuid+FeedConfig.feed,updateFeed);
        }

    public MyResult getFeed(String uuid, Long start, Long end) {
        FeedPage feedPage = new FeedPage();
        //获得total
        Long total = jedisClient.zcard(uuid + FeedConfig.feed);
        feedPage.setTotal(total);
        List<FeedModel> feedModels = new ArrayList<>();
        Set<Tuple> set = jedisClient.zrangewithscores(uuid + FeedConfig.feed, --start,--end );
        if (!CollectionUtils.isEmpty(set)){
            for (Tuple tuple: set){
                FeedModel feedModel = null;
                String res = jedisClient.get(tuple.getElement()+FeedConfig.imageInfo);

                if (StringUtils.isBlank(res)){
                    feedModel = feedModelMapper.getFeedList(Integer.valueOf(tuple.getElement()));
                    jedisClient.set(tuple.getElement()+FeedConfig.imageInfo,JSON.toJSONString(feedModel),FeedConfig.imageInfo_expireTime);
                }else {
                    feedModel = JSON.parseObject(res, FeedModel.class);
                }
                feedModels.add(feedModel);
            }
        }
        feedPage.setFeeds(feedModels);
        return new MyResult().ok(feedPage);
    }

    public void updatedy(Image image1) {
        Long time =0-image1.getCreatedDate().getTime();
        jedisClient.zadd(String.valueOf(image1.getUserId())+FeedConfig.dynamic,Double.valueOf(time),String.valueOf(image1.getId()));
    }

    public void updateFollowersFeed(Image image) {
        //发送给mq消费
        rmqService.sendCurrentlyMsg(FeedConfig.RMQ_TOPIC_Feed,KryoUtils.writeKryoObject(image));
    }
}
