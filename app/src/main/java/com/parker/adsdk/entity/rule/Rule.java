package com.parker.adsdk.entity.rule;

import com.parker.adsdk.entity.Entity;

import org.json.JSONObject;

/**
 * Created by parker on 2016/8/22.completed
 */
public class Rule extends Entity {
    private GlobalRules globalRules;
    private PackageRules pkgRules;
    private int ruleVer;
    private int lifeTime;

    public Rule(JSONObject jsonObject) {
        this.ruleVer = 0;
        this.lifeTime = 0;
        this.parse(jsonObject);
    }

    public Rule parse(JSONObject jsonObject) {
        try {
            this.globalRules = new GlobalRules(jsonObject.optJSONObject("globalRules"));
        }
        catch(Exception e) {
            this.globalRules = null;
        }

        try {
            this.pkgRules = new PackageRules(jsonObject.optJSONObject("pkgRules"));
        }
        catch(Exception e) {
            this.pkgRules = null;
        }

        this.ruleVer = jsonObject.optInt("ruleVer");
        this.lifeTime = jsonObject.optInt("lifeTime");
        return this;
    }

    public JSONObject pack() {
        JSONObject result = new JSONObject();
        try {
            result.put("globalRules", this.globalRules == null ? JSONObject.NULL : this.globalRules.pack());
            result.put("pkgRules", this.pkgRules == null ? JSONObject.NULL : this.pkgRules.pack());
            result.put("ruleVer", this.ruleVer);
            result.put("lifeTime", this.lifeTime);
        } catch (Exception e) {
        }
        return result;
    }

    public GlobalRules getGlobalRules() {
        return this.globalRules;
    }

    public PackageRules getPkgRules() {
        return this.pkgRules;
    }

    public int getRuleVer() {
        return this.ruleVer;
    }
}
