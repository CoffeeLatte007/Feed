package com.feed.controller;

import com.feed.FeedConfig;
import com.feed.model.MyResult;
import com.feed.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public MyResult followingSomeone(String userid,String currentuserid){
        followService.follow(userid,currentuserid);
        return MyResult.ok();
    }
    @RequestMapping("{userid:\\d+}/followers")
    @ResponseBody
    public MyResult viewFollowers(@PathVariable String userid ,@RequestParam(value = "page",required = false,defaultValue = "1")Integer page){
        if (page<=0){
            return new MyResult().build(400,"400","长度不能小于1");
        }
        return followService.viewFollow(userid,page, FeedConfig.followers);
    }
    @RequestMapping("{userid:\\d+}/following")
    @ResponseBody
    public MyResult viewFollowing(@PathVariable String userid ,@RequestParam(value = "page",required = false,defaultValue = "1")Integer page){
        if (page<=0){
            return new MyResult().build(400,"400","长度不能小于1");
        }
        return followService.viewFollow(userid,page, FeedConfig.following);
    }
    @RequestMapping("/numsFollow/{userid:\\d+}")
    @ResponseBody
    public MyResult numsFollow(@PathVariable String userid){
        return followService.numsFollow(userid);
    }
    @RequestMapping("/{currentuserid:\\d+}/isfollowing")
    @ResponseBody
    public MyResult isFollowing(@PathVariable String currentuserid,@RequestParam(value = "useruuid",required = true)String useruuid){
        List<String> uidlist = Arrays.asList(useruuid.split(","));
        return followService.isFollowing(currentuserid,uidlist);
    }
}
