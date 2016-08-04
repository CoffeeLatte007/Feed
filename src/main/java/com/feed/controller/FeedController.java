package com.feed.controller;

import com.alibaba.fastjson.JSON;
import com.feed.model.Image;
import com.feed.model.MyResult;
import com.feed.service.FeedService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 每个人都保存一个自己的动态表，不设置大小限制无限
 * 每个人都保存一个自己的Feed表，别人发表文章的时候动态的在我们这边，设置一个时间限制第一个版本不设置
 * storm只计算新增加动态，由于人数不多，这里首先写个更新语句
 * Created by Administrator on 2016/8/1.
 */
@Controller
public class FeedController {
    @Autowired
    FeedService feedService;

    /**
     * 更新每个人的动态表
     * @return
     */
    @RequestMapping("/init")
    @ResponseBody
    public MyResult initAllPeople(){
        return   feedService.initAllPeople();
    }
    @RequestMapping("/{uuid:\\d+}/feed")
    @ResponseBody
    public MappingJacksonValue getFeed(@PathVariable String uuid,@RequestParam(value = "start",required = false,defaultValue = "1") Long start,@RequestParam(value = "end",required = false,defaultValue = "10")Long end,String callback){
        if (end<start){
            MyResult result = new MyResult().build(400, "end比start还大,你是他妈的有病？");
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        if (start==1){
            //0的时候需要更新feed
            feedService.updateFeed(uuid);
        }
        MyResult result = feedService.getFeed(uuid, start, end);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        mappingJacksonValue.setJsonpFunction(callback);
        return mappingJacksonValue;
    }
    @RequestMapping("/updatedy/{uuid:\\d+}")
    @ResponseBody
    public MappingJacksonValue updatedy(@PathVariable String uuid,String image,String callback){
        Image image1 = JSON.parseObject(image,Image.class);
        feedService.updatedy(image1);
        feedService.updateFollowersFeed(image1);
        MyResult result = new MyResult().ok();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        mappingJacksonValue.setJsonpFunction(callback);
        return mappingJacksonValue;
    }
}
