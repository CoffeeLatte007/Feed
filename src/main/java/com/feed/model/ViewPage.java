package com.feed.model;

import java.util.List;

/**
 * Created by Administrator on 2016/7/31.
 */
public class ViewPage {
    private int total;
    private List<User> users;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
