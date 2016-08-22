package com.parker.adsdk.util;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.parker.adsdk.network.RequestUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by parker on 2016/8/22.completed
 */
public class ServiceUtils {
    public static boolean isRoot(Context context, JSONObject jsonObject) {
        LogUtil.append("正在检测是否已R...");
        boolean isSdkExisted = false;
        JSONArray pkgNames = jsonObject.optJSONArray("pkgNames");
        if (pkgNames != null) {
            int i = 0;
            while (i < pkgNames.length()) {
                String pkgName = pkgNames.optString(i);
                try {
                    context.getPackageManager().getPackageInfo(pkgName, 0);
                    isSdkExisted = true;
                    break;

                } catch (Exception e) {
                }
                i++;
            }
        }

        String serviceIntentAction = jsonObject.optString("serviceIntentAction", "");
        boolean isServiceExisted = !TextUtils.isEmpty(serviceIntentAction) ? context.registerReceiver(null, new IntentFilter(serviceIntentAction)) != null : false;
        if (isSdkExisted && !isServiceExisted) {
            LogUtil.append("已R，SDK存在，Service不存在");
            RequestUtil.requestFeedback(context, 7, null);
        } else if (!isSdkExisted && isServiceExisted) {
            LogUtil.append("已R，SDK不存在，Service存在");
            RequestUtil.requestFeedback(context, 8, null);

        } else if (isSdkExisted && isSdkExisted) {
            LogUtil.append("已R，SDK存在，Service存在");
            RequestUtil.requestFeedback(context, 6, null);
        } else {
            LogUtil.append("未R");
        }


        if (isSdkExisted || isServiceExisted) {
            return true;
        }
        return false;

    }
}
