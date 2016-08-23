package com.parker.adsdk.network;

import android.content.Context;

import com.parker.adsdk.entity.Response;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by parker on 2016/8/9.
 */
public class RequestUtil {


    public static String requstInit(Context context) {
        return null;
    }

    public static Response requestFeedback(Context context, int eventType, String url)
    {
        return null;

    }
    public static String requestOta(Context context){
        return null;
    }
    public static void downloadPlus(Context context, int type, String url, File destFile, String md5){

    }
    public static Response reportPara(Context context, String tid, int eventType, JSONObject plan, JSONObject
            info){
        return null;
    }
}
