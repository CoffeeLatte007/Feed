package com.feed.model;

/**
 * Created by Administrator on 2016/7/31.
 */
public class NumsFollow {
    private Long numFollowers;
    private Long numFollowing;

    public NumsFollow(Long numFollowers, Long numFollowing) {
        this.numFollowers = numFollowers;
        this.numFollowing = numFollowing;
    }

    public Long getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Long numFollowers) {
        this.numFollowers = numFollowers;
    }

    public Long getNumFollowing() {
        return numFollowing;
    }

    public void setNumFollowing(Long numFollowing) {
        this.numFollowing = numFollowing;
    }
}
