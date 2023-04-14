package com.example.myrouter_api.utils;

public interface ILogger {

    void showLog(boolean isShowLog);
    void d(String tag, String msg);
    void i(String tag, String msg);
    void w(String tag, String msg);
    void e(String tag, String msg);
}
