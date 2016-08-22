package com.parker.adsdk.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thinkpad on 2016/8/9.completed
 */
public class Response extends Entity {
    private Result result;
    private List<Extension> plugins;
    private int vercode;
    private String url;

    public Response(JSONObject jsonObject) {
        this.vercode = -1;
        this.parse(jsonObject);
    }

    public Response() {
        this.vercode = -1;
    }

    public Response parse(JSONObject jsonObject) {
        if(jsonObject.optJSONObject("result") != null) {
            this.result = new Result(jsonObject.optJSONObject("result"));
        }

        if(jsonObject.optJSONArray("plugins") != null) {
            this.plugins = new ArrayList();
            JSONArray jsonArray = jsonObject.optJSONArray("plugins");
            int i = 0;
            try {
                while(i < jsonArray.length()) {
                    this.plugins.add(new Extension(jsonArray.getJSONObject(i)));
                    ++i;
                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }

        if(jsonObject.has("upgrade")) {
            JSONObject upgrade = jsonObject.optJSONObject("upgrade");
            this.vercode = upgrade.optInt("vercode", -1);
            this.url = upgrade.optString("url");
        }

        return this;
    }
}
