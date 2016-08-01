package com.feed.service;

import com.feed.FeedConfig;
import com.feed.jedis.JedisClient;
import com.feed.mapper.UserMapper;
import com.feed.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2016/7/31.
 */
@Service
public class FollowService {
    @Autowired
    JedisClient jedisClient;
    @Autowired
    UserMapper userMapper;
    public void follow(String userid, String currentuserid) {
        //添加关注，不需要判断重复
        String time =String.valueOf(new Date().getTime());
        jedisClient.hset(userid+ FeedConfig.followers,currentuserid,time);
        jedisClient.hset(currentuserid+FeedConfig.following,userid,time);
    }

    public MyResult viewFollow(String userid, Integer page,String flag) {
        Map<String,String> map;
        if (flag.equals(FeedConfig.following))
            map = jedisClient.hgetAll(userid+FeedConfig.following);
        else
            map = jedisClient.hgetAll(userid+FeedConfig.followers);
        List<Map.Entry<String,String>> list = new ArrayList<Map.Entry<String,String>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,String>>() {
            //升序排序
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                Long t1 = Long.valueOf(o1.getValue());
                Long t2 = Long.valueOf(o2.getValue());
                if (t1>=t2)
                    return -1;
                return 1;
            }

        });
        ViewPage viewPage = new ViewPage();
        viewPage.setTotal(list.size());
        //只允许10条每页
        int start = (page-1)*10;
        int end   = start+9;
        if (start > list.size()-1){
            return new MyResult().ok(viewPage);
        }
        if (end > list.size()-1)
            end = list.size()-1;

        List<Integer> uids = new ArrayList<>();
        for (int i = start;i<= end;i++){
            uids.add(Integer.valueOf(list.get(i).getKey()));
        }
        UserExample userExample =new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andIdIn(uids);
        List<User> users = userMapper.selectByExample(userExample);
        for (User user : users) {
            user.setPassword(null);
            user.setSalt(null);
        }
        viewPage.setUsers(users);
        return new MyResult().ok(viewPage);
    }

    public MyResult numsFollow(String userid) {
        long numFollowers = jedisClient.hlen(userid+FeedConfig.followers);
        long numFollowing = jedisClient.hlen(userid+FeedConfig.following);

        return new MyResult(new NumsFollow(numFollowers,numFollowing));
    }

    public MyResult isFollowing(String currentuserid, List<String> useruuid) {
        List<Boolean> booleanList = new ArrayList<>();
        for (String uid:useruuid){
            booleanList.add(jedisClient.hexists(currentuserid+FeedConfig.following,uid));
        }
        return new MyResult().ok(booleanList);
    }
}
