package com.example.myrouter_api.interceptor;

import com.example.myrouter_api.thread.ActionPost;

public interface ActionInterceptor {

    void intercept(ActionChain actionChain);
    interface ActionChain {
        void onInterrupt();
        void proceed(ActionPost actionPost);
        ActionPost action();
        String actionPath();
    }
}
