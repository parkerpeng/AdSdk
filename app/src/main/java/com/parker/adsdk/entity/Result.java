package com.parker.adsdk.entity;

import org.json.JSONObject;

/**
 * Created by parker on 2016/8/9.completed
 */
public class Result extends Entity{
    private int code;
    private String msg;

    public Result() {
    }

    public Result(JSONObject jsonObject) {
        this.parse(jsonObject);
    }

    public Result parse(JSONObject jsonObject) {
        this.code = jsonObject.optInt("code", -1);
        this.msg = jsonObject.optString("msg", "");
        return this;
    }
}
