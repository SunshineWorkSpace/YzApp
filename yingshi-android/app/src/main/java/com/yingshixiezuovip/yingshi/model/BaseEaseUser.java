package com.yingshixiezuovip.yingshi.model;

/**
 * Created by Resmic on 2017/5/20.
 */

public class BaseEaseUser {
    public String token;
    public String head;
    public String nickname;

    public BaseEaseUser(String token, String head, String nickname) {
        this.token = token;
        this.head = head;
        this.nickname = nickname;
    }
}
