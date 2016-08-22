package com.parker.adsdk.work;

import android.content.Context;

import java.io.File;

/**
 * Created by thinkpad on 2016/8/9.
 */
public class Work {
    private Context context;
    private File b;
    private File c;
    private String d;
    private File e;
    private long extra_start_time;
    private long extra_end_time;
    private String extra_trigger;
    private int i;

    public Work(Context context) {
        super();
        this.context = context;
        this.i = 0;
    }

    public Work(Context context, long extra_start_time, long extra_end_time, String extra_trigger) {
        this.context = context;
        this.extra_start_time = extra_start_time;
        this.extra_end_time = extra_end_time;
        this.extra_trigger = extra_trigger;
        this.i = 1;
    }

    public void checkRootStatus() {

    }


}
