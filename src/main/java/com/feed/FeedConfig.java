package com.feed;

/**
 * Created by Administrator on 2016/7/31.
 */
public interface FeedConfig {
    public static String followers = ":followers";
    public static String following = ":following";
    public static String dynamic = ":dynamic";
    public static String feed  = ":feed";
    public static String imageInfo = ":imageInfo";
    public static int imageInfo_expireTime = 60*60*24;
    public static String RMQ_NAME_SERVER = "119.29.75.199:9876";
    public static String RMQ_TOPIC_Feed = "CalFedd";
}
