package com.parker.adsdk.entity;

import org.json.JSONObject;

/**
 * Created by parker on 2016/8/9.completed
 */
public class WorkPlan extends Entity {
    private String url;
    private String params;
    private int state;
    private String planMd5;

    public WorkPlan(JSONObject jsonObject) {
        super();
        this.parse(jsonObject);
    }

    public WorkPlan parse(JSONObject jsonObject) {
        this.url = jsonObject.optString("url");
        this.params = jsonObject.optString("params");
        this.state = jsonObject.optInt("state", -1);
        this.planMd5 = jsonObject.optString("planMd5");
        return this;
    }

    public JSONObject pack() {
        JSONObject result = new JSONObject();
        try {
            result.put("url", this.url);
            result.put("params", this.params);
            result.put("state", this.state);
            result.put("planMd5", this.planMd5);
        }
        catch(Exception e) {
        }

        return result;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPlanMd5() {
        return planMd5;
    }

    public void setPlanMd5(String planMd5) {
        this.planMd5 = planMd5;
    }
}
