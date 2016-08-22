package com.parker.adsdk.entity.rule;

import com.parker.adsdk.compat.SystemProperties;
import com.parker.adsdk.entity.Entity;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by parker on 2016/8/9.
 */
public class GlobalRules extends Entity {
    private String trigger;
    private Calendar start;
    private Calendar end;

    public GlobalRules(JSONObject jsonObject) {
        this.parse(jsonObject);
    }

    public GlobalRules parse(JSONObject jsonObject) {
        this.trigger = jsonObject.optString("trigger", null);
        this.start = this.int2Calendar(jsonObject.optInt("start", 0));
        this.end = this.int2Calendar(jsonObject.optInt("end", 0));
        if (this.start.before(Calendar.getInstance())) {
            this.start.add(Calendar.DATE, 1);
            this.end.add(Calendar.DATE, 1);
        }
        return this;
    }

    private String calendar2Str(Calendar calendar) {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String strHourOfDay = Integer.toString(hourOfDay);
        String strMinute = Integer.toString(minute);
        if (strMinute.length() < 2) {
            strMinute = "0" + strMinute;
        }
        return String.valueOf(strHourOfDay) + strMinute;
    }

    public static void main(String[] args) {
        System.out.println(int2Calendar(185));

    }

    private static Calendar int2Calendar(int time) {
        String str;
        String strTime = Integer.toString(time);
        if (strTime.length() < 4) {
            str = strTime;
            for (int i = 0; i < 4 - str.length(); ++i) {
                str = "0" + str;
            }
        } else {
            str = strTime;
        }
        int hourOfDay = Integer.parseInt(str.substring(0, 2));
        int minute = Integer.parseInt(str.substring(2, 4));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    public JSONObject pack() {
        JSONObject result = new JSONObject();
        try {
            result.put("trigger", this.trigger);
            result.put("start", this.calendar2Str(this.start));
            result.put("end", this.calendar2Str(this.end));
        } catch (Exception e) {
        }

        return result;
    }

    public String getTrigger() {
        return this.trigger;
    }

    public Calendar getStart() {
        return this.start;
    }

    public Calendar getEnd() {
        return this.end;
    }


}
