package com.parker.adsdk.entity;

import org.json.JSONObject;

/**
 * Created by thinkpad on 2016/8/9.completed
 */
public class Extension extends Entity {
    private String pkgName;
    private int version;
    private String url;
    private String fileName;

    public Extension(JSONObject jsonObject) {
        super();
        this.a(jsonObject);
    }

    public Extension() {
    }

    public Extension a(JSONObject jsonObject) {
        this.pkgName = jsonObject.optString("pkgName", "");
        this.version = jsonObject.optInt("version", 1);
        this.url = jsonObject.optString("url", "");
        this.fileName = jsonObject.optString("fileName", "");
        return this;
    }
}
