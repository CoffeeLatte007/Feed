package com.feed.controller;

import com.feed.model.MyResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 每个人都保存一个自己的动态表，发表文章的时候可以去数据库查询
 * 每个人都保存一个自己的Feed表，别人发表文章的时候动态的在我们这边
 * storm只计算新增加动态，由于人数不多，这里首先写个更新语句
 * Created by Administrator on 2016/8/1.
 */
public class FeedController {
    @RequestMapping("/init")
    @ResponseBody
    public MyResult initAllpeople(){

    }
}
