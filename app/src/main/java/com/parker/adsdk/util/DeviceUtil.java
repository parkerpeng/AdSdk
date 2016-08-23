package com.parker.adsdk.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.parker.adsdk.compat.SystemProperties;
import com.parker.adsdk.entity.CPUInfo;
import com.parker.adsdk.entity.Device;
import com.parker.adsdk.entity.Prop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by parker on 2016/8/23.completed
 */
public class DeviceUtil {

    private static final Uri uri = Uri.parse("content://com.google.android.gsf.gservices");;


    public static Device getDevice(Context context){
        Device device = new Device();
        TelephonyManager teleManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        CPUInfo cpuInfo;
        try {
            HashMap cpuInfoMap = new HashMap();
            IOUtil.cat(new FileInputStream("/proc/cpuinfo") , cpuInfoMap);
            cpuInfo = new CPUInfo(cpuInfoMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cpuInfo = null;
        }

        Prop prop;
        try {
            prop = DeviceUtil.getProps();
        }catch (Exception e)
        {
            prop = null;
        }

        String linuxVersion = "";
        String linuxversion_desc = "";
        try
        {
            String  version = IOUtil.cat(new FileInputStream("/proc/version"), null);
            linuxVersion = DeviceUtil.getLinuxversion(version);
            linuxversion_desc = version;

        }catch (Exception e)
        {
        }




        String memTotal = "";
        HashMap memInfoMap = new HashMap();
        try {
            memInfoMap.put("MemTotal", "");
            IOUtil.cat(new FileInputStream("/proc/meminfo"), ((Map)memInfoMap));
            memTotal = (String) memInfoMap.get("MemTotal");
        }catch (Exception e)
        {
        }

        device.setCpuinfo(cpuInfo);
        device.setProp(prop);
        device.setLinuxversion(linuxVersion);
        device.setLinuxversion_desc(linuxversion_desc);
        device.setMemTotal(((String)memTotal));
        device.setImei(DeviceUtil.getDeviceId(context));
        device.setAndroidId(DeviceUtil.getAndroid_id(context));
        device.setMacAddr(DeviceUtil.getMacAddress(context));
        device.setNetworkType(DeviceUtil.getActiveNetworkType(context));
        device.setBluetooth_mac(BluetoothAdapter.getDefaultAdapter().getAddress());
        device.setImei_sv(teleManager.getDeviceSoftwareVersion());
        device.setImsi(teleManager.getSubscriberId());
        device.setIccid(teleManager.getSimSerialNumber());
        device.setGsf_id(DeviceUtil.getGsf_id(context));
        device.setAdId(DeviceUtil.getAdvertisingId(context));
        return device;
    }

    public static String getDeviceId(Context context) {
        String result;
        try {
            result = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }
        catch(Exception e) {
            e.printStackTrace();
            result = "0";
        }
        return result;
    }

    public static String getAndroid_id(Context context) {
        String result = "0";
        try {
            result = Settings.Secure.getString(context.getContentResolver(), "android_id");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    private static Prop getProps() {
        HashMap map = new HashMap();
        String[] propKeys = Prop.keys();
        int len = propKeys.length;
        for(int i = 0; i < len; ++i) {
            map.put(propKeys[i], "");
        }

        Iterator it = map.entrySet().iterator();
        while(it.hasNext()) {
            Object propKey = it.next();
            ((Map)map).put(((Map.Entry)propKey).getKey(), SystemProperties.getProp((String) ((Map.Entry)propKey)
                    .getKey()));
        }

        return new Prop(((Map)map));
    }

    private static String getLinuxversion(String str) {
        String result = str.toLowerCase();
        if(result.contains("(")) {
            result = result.split("\\(")[0];
            if(result.contains("version")) {
                result = result.split("version")[1];
                if(result.contains("-")) {
                    result = result.split("-")[0];
                }
            }
        }

        return result.trim();
    }

    public static String getMacAddress(Context context) {
        String result = "0";
        try {
            result = ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }



    public static String getActiveNetworkType(Context context) {
        String result;
        NetworkInfo networkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected()) {
            result = "Unknown";
        }
        else {
            String typeName = networkInfo.getTypeName();
            if(typeName.equalsIgnoreCase("WIFI")) {
                result = "wifi";
            }
            else if(typeName.equalsIgnoreCase("MOBILE")) {
                result = networkInfo.getSubtypeName();
            }
            else {
                result = "none";
            }
        }

        return result;
    }


    private static String getGsf_id(Context context) {
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, new String[]{"android_id"}, null);
            if (cursor == null || !cursor.moveToFirst() || cursor.getColumnCount() < 2) {
                return null;
            }
            try {
                String result = Long.toHexString(Long.parseLong(cursor.getString(1)));
                cursor.close();
                return result;
            } catch (NumberFormatException e) {
                return null;
            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e2) {
            return "none";
        }
    }




    private static String getAdvertisingId(Context context) {
        try {
            AdUtils.AdInfo adInfo = AdUtils.getAdvertisingIdInfo(context);
            if(adInfo == null) {
                return "";
            }

            String id = adInfo.getId();
            return id;
        }
        catch(Exception e) {
        }

        return "";
    }

}
