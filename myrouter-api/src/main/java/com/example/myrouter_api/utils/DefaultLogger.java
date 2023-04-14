package com.example.myrouter_api.utils;

import android.text.TextUtils;
import android.util.Log;

public class DefaultLogger implements ILogger{
    boolean isShowLog = false;
    private String defaultTag = "MyRouter";
    @Override
    public void showLog(boolean isShowLog) {
        this.isShowLog = isShowLog;
    }
    @Override
    public void d(String tag, String msg) {
        if (isShowLog)
            Log.d(TextUtils.isEmpty(tag) ? getDefaultTag() : tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        if (isShowLog)
            Log.i(TextUtils.isEmpty(tag) ? getDefaultTag() : tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        if (isShowLog)
            Log.w(TextUtils.isEmpty(tag) ? getDefaultTag() : tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        if (isShowLog)
            Log.e(TextUtils.isEmpty(tag) ? getDefaultTag() : tag, msg);
    }

    public String getDefaultTag() {return defaultTag;}
}
