package com.feed.controller;

import com.feed.FeedConfig;
import com.feed.model.MyResult;
import com.feed.service.FollowService;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/7/31.
 */
@Controller
public class FollowController {
    @Autowired
    FollowService followService;
    @RequestMapping(value = "/follow")
    @ResponseBody
    public MappingJacksonValue followingSomeone(String userid,String currentuserid,String callback){
        followService.follow(userid,currentuserid);
        MyResult result = MyResult.ok();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        mappingJacksonValue.setJsonpFunction(callback);
        return mappingJacksonValue;
    }
    @RequestMapping("{userid:\\d+}/followers")
    @ResponseBody
    public MappingJacksonValue viewFollowers(@PathVariable String userid ,@RequestParam(value = "page",required = false,defaultValue = "1")Integer page,String callback){
        MyResult result;
        if (page<=0){
            result =new MyResult().build(400,"400","长度不能小于1");
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        result =followService.viewFollow(userid, page, FeedConfig.followers);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        mappingJacksonValue.setJsonpFunction(callback);
        return mappingJacksonValue;
    }
    @RequestMapping("{userid:\\d+}/following")
    @ResponseBody
    public MappingJacksonValue viewFollowing(@PathVariable String userid ,@RequestParam(value = "page",required = false,defaultValue = "1")Integer page,String callback){
        MyResult result;
        if (page<=0){
            result =new MyResult().build(400,"400","长度不能小于1");
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        result =followService.viewFollow(userid,page, FeedConfig.following);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        mappingJacksonValue.setJsonpFunction(callback);
        return mappingJacksonValue;
    }
    @RequestMapping("/numsFollow/{userid:\\d+}")
    @ResponseBody
    public MappingJacksonValue numsFollow(@PathVariable String userid,String callback){
        MyResult result =followService.numsFollow(userid);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        mappingJacksonValue.setJsonpFunction(callback);
        return mappingJacksonValue;
    }
    @RequestMapping("/{currentuserid:\\d+}/isfollowing")
    @ResponseBody
    public MappingJacksonValue isFollowing(@PathVariable String currentuserid,@RequestParam(value = "useruuid",required = true)String useruuid,String callback){
        List<String> uidlist = Arrays.asList(useruuid.split(","));
        MyResult result =followService.isFollowing(currentuserid,uidlist);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
        mappingJacksonValue.setJsonpFunction(callback);
        return mappingJacksonValue;
    }
}
