package com.parker.adsdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by thinkpad on 2016/8/9.completed
 */
public class Prop extends Entity {
    private String brand;
    private String name;
    private String model;
    private String fingerprint;
    private String sdk;
    private String release;
    private String date;
    private String utc;
    private String cpuid;
    private String vendor;
    private String timezone;
    private String country;
    private String language;
    private String vmlib;
    private String description;
    private String firstboot;
    private String serialno;
    private String device;
    private String hardware;
    private String qemu;
    private String abi;

    public Prop() {
    }

    public Prop(Map map) {
        this.parse(map);
    }

    public Prop parse(Map map) {
        if(map == null){
            return null;
        }
        Iterator keys = map.keySet().iterator();
        if(keys != null) {
            while(keys.hasNext()) {
                Object key = keys.next();
                if(((String)key).equals("ro.product.brand")) {
                    this.brand = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.product.name")) {
                    this.name = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.product.model")) {
                    this.model = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.build.fingerprint")) {
                    this.fingerprint = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.build.version.sdk")) {
                    this.sdk = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.build.version.release")) {
                    this.release = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.build.date")) {
                    this.date = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.build.date.utc")) {
                    this.utc = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.boot.cpuid")) {
                    this.cpuid = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.btconfig.vendor")) {
                    this.vendor = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("persist.sys.timezone")) {
                    this.timezone = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("persist.sys.country")) {
                    this.country = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("persist.sys.language")) {
                    this.language = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("persist.sys.dalvik.vm.lib")) {
                    this.vmlib = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.build.description")) {
                    this.description = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.runtime.firstboot")) {
                    this.firstboot = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.serialno")) {
                    this.serialno = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.product.device")) {
                    this.device = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.hardware")) {
                    this.hardware = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.kernel.qemu")) {
                    this.qemu = (String) map.get(key);
                    continue;
                }

                if(((String)key).equals("ro.product.cpu.abi")) {
                    this.abi = (String) map.get(key);
                    continue;
                }

            }
        }
        return this;
    }

    public JSONObject pack() throws JSONException {
        JSONObject result = new JSONObject();
        try {
            result.put("ro.product.brand", this.brand);
            result.put("ro.product.name", this.name);
            result.put("ro.product.model", this.model);
            result.put("ro.build.fingerprint", this.fingerprint);
            result.put("ro.build.version.sdk", this.sdk);
            result.put("ro.build.version.release", this.release);
            result.put("ro.build.date", this.date);
            result.put("ro.build.date.utc", this.utc);
            result.put("ro.boot.cpuid", this.cpuid);
            result.put("ro.btconfig.vendor", this.vendor);
            result.put("persist.sys.timezone", this.timezone);
            result.put("persist.sys.country", this.country);
            result.put("persist.sys.language", this.language);
            result.put("persist.sys.dalvik.vm.lib", this.vmlib);
            result.put("ro.build.description", this.description);
            result.put("ro.runtime.firstboot", this.firstboot);
            result.put("ro.serialno", this.serialno);
            result.put("ro.product.device", this.device);
            result.put("ro.kernel.qemu", this.qemu);
            result.put("ro.hardware", this.hardware);
            result.put("ro.product.cpu.abi", this.abi);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String[] keys() {
        return new String[]{"ro.product.brand", "ro.product.name", "ro.product.model", "ro.build.fingerprint",
                "ro.build.version.sdk", "ro.build.version.release", "ro.build.date", "ro.build.date.utc",
                "ro.boot.cpuid", "ro.btconfig.vendor", "persist.sys.timezone", "persist.sys.country",
                "persist.sys.language", "persist.sys.dalvik.vm.lib", "ro.build.description", "ro.runtime.firstboot",
                "ro.serialno", "ro.product.device", "ro.hardware", "ro.kernel.qemu", "ro.product.cpu.abi"};
    }
}
