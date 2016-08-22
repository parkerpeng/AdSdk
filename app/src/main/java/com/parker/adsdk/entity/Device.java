package com.parker.adsdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by parker on 2016/8/9.completed
 */
public class Device extends Entity {
    private String MemTotal;
    private String linuxversion;
    private CPUInfo cpuinfo;
    private Prop prop;
    private String imei;
    private String macAddr;
    private String androidId;
    private String linuxversion_desc;
    private String networkType;
    private String bluetooth_mac;
    private String imei_sv;
    private String imsi;
    private String iccid;
    private String gsf_id;
    private String adId;

    public Device() {
    }

    public JSONObject pack() throws JSONException {
        JSONObject result = new JSONObject();
        try {
            result.put("MemTotal", this.MemTotal);
            result.put("linuxversion", this.linuxversion);
            result.put("linuxversion_desc", this.linuxversion_desc);
            result.put("cpuinfo", this.cpuinfo.pack());
            result.put("prop", this.prop.pack());
            result.put("imei", this.imei);
            result.put("macAddr", this.macAddr);
            result.put("androidId", this.androidId);
            result.put("networkType", this.networkType);
            result.put("bluetooth_mac", this.bluetooth_mac);
            result.put("imei/sv", this.imei_sv);
            result.put("imsi", this.imsi);
            result.put("iccid", this.iccid);
            result.put("gsf_id", this.gsf_id);
            result.put("adId", this.adId);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void setMemTotal(String memTotal) {
        MemTotal = memTotal;
    }

    public void setLinuxversion(String linuxversion) {
        this.linuxversion = linuxversion;
    }

    public void setCpuinfo(CPUInfo cpuinfo) {
        this.cpuinfo = cpuinfo;
    }

    public void setProp(Prop prop) {
        this.prop = prop;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public void setLinuxversion_desc(String linuxversion_desc) {
        this.linuxversion_desc = linuxversion_desc;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public void setBluetooth_mac(String bluetooth_mac) {
        this.bluetooth_mac = bluetooth_mac;
    }

    public void setImei_sv(String imei_sv) {
        this.imei_sv = imei_sv;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public void setGsf_id(String gsf_id) {
        this.gsf_id = gsf_id;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }
}
