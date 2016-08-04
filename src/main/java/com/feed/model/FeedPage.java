package com.feed.model;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1.
 */
public class FeedPage {
    private Long total;
    private List feeds;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getFeeds() {
        return feeds;
    }

    public void setFeeds(List feed) {
        this.feeds = feed;
    }
}
