package com.parker.adsdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by parker on 2016/8/9.completed
 */
public class CPUInfo extends Entity {
    private String processorcnt;
    private String modelname;
    private String features;
    private String cpuimplementer;
    private String cpuarchitecture;
    private String cpuvariant;
    private String cpupart;
    private String cpurevision;
    private String hardware;
    private String revision;
    private String serial;

    public CPUInfo(Map<String , String> map) {
        this.processorcnt = "0";
        this.parse(map);
    }

    public CPUInfo() {
        this.processorcnt = "0";
    }

    public JSONObject pack() throws JSONException {
        JSONObject result = new JSONObject();
        try {
            String key = "processorcnt";
            String value = this.processorcnt == null ? "" : this.processorcnt;
            result.put(key, value);
            key = "modelname";
            value = this.modelname == null ? "" : this.modelname;
            result.put(key, value);
            key = "features";
            value = this.features == null ? "" : this.features;
            result.put(key, value);
            key = "cpuimplementer";
            value = this.cpuimplementer == null ? "" : this.cpuimplementer;
            result.put(key, value);
            key = "cpuarchitecture";
            value = this.cpuarchitecture == null ? "" : this.cpuarchitecture;
            result.put(key, value);
            key = "cpuvariant";
            value = this.cpuvariant == null ? "" : this.cpuvariant;
            result.put(key, value);
            key = "cpupart";
            value = this.cpupart == null ? "" : this.cpupart;
            result.put(key, value);
            key = "cpurevision";
            value = this.cpurevision == null ? "" : this.cpurevision;
            result.put(key, value);
            key = "hardware";
            value = this.hardware == null ? "" : this.hardware;
            result.put(key, value);
            key = "revision";
            value = this.revision == null ? "" : this.revision;
            result.put(key, value);
            key = "serial";
            value = this.serial == null ? "" : this.serial;
            result.put(key, value);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private CPUInfo parse(Map<String ,String> map) {
        if (map == null){
            return null;
        }
        Iterator<String> keys = map.keySet().iterator();
        if (keys != null) {
            while (keys.hasNext()) {
                String key = keys.next();
                if ( key.replace(" ", "").toLowerCase().contains("processorcnt") ) {
                    this.processorcnt = map.get(key);
                    continue;
                }

                if ( key.replace(" ", "").toLowerCase().contains("modelname") ) {
                    this.modelname = map.get(key);
                    continue;
                }

                if ( key.replace(" ", "").toLowerCase().contains("features") ) {
                    this.features = map.get(key);
                    continue;
                }

                if ( key.replace(" ", "").toLowerCase().contains("cpuimplementer") ) {
                    this.cpuimplementer = map.get(key);
                    continue;
                }

                if ( key.replace(" ", "").toLowerCase().contains("cpuarchitecture") ) {
                    this.cpuarchitecture = map.get(key);
                    continue;
                }

                if ( key.replace(" ", "").toLowerCase().contains("cpuvariant")) {
                    this.cpuvariant = map.get(key);
                    continue;
                }

                if (key.replace(" ", "").toLowerCase().contains("cpupart")) {
                    this.cpupart = map.get(key);
                    continue;
                }

                if (key.replace(" ", "").toLowerCase().contains("cpurevision")) {
                    this.cpurevision = map.get(key);
                    continue;
                }

                if ( key.replace(" ", "").toLowerCase().contains("hardware")) {
                    this.hardware = map.get(key);
                    continue;
                }

                if (key.replace(" ", "").toLowerCase().contains("revision")) {
                    this.revision = map.get(key);
                    continue;
                }

                if (key.replace(" ", "").toLowerCase().contains("serial")) {
                    this.serial = map.get(key);
                    continue;
                }
            }
            this.processorcnt = String.valueOf(Integer.valueOf(this.processorcnt).intValue() + 1);
        }

        return this;
    }
}
