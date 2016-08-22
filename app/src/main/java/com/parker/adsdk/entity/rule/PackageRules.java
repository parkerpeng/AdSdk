package com.parker.adsdk.entity.rule;

import com.parker.adsdk.entity.Entity;

import org.json.JSONObject;

/**
 * Created by parker on 2016/8/22.completed
 */
public class PackageRules extends Entity {
    private String pkg;
    private int retryTimes;
    private String trigger;

    public PackageRules(JSONObject jsonObject) {
        this.parse(jsonObject);
    }

    public PackageRules parse(JSONObject jsonObject) {
        this.pkg = jsonObject.optString("pkg", null);
        this.retryTimes = jsonObject.optInt("retryTimes", 0);
        this.trigger = jsonObject.optString("trigger", null);
        return this;
    }

    public JSONObject pack() {
        JSONObject result = new JSONObject();
        try {
            result.put("pkg", this.pkg);
            result.put("retryTimes", this.retryTimes);
            result.put("trigger", this.trigger);
        }
        catch(Exception e) {
        }

        return result;
    }
}
