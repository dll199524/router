package com.example.myrouter_api.core;

import com.example.myrouter_api.extra.ActionWrapper;
import com.example.myrouter_api.interceptor.ActionInterceptor;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

//路由转发
public class RouterForward {
    private ActionWrapper actionWarpper;
    List<ActionInterceptor> interceptors;
    private Map<String, Object> params;

    public RouterForward(ActionWrapper actionWarpper, List<ActionInterceptor> interceptors) {
        this.actionWarpper = actionWarpper;
        this.interceptors = interceptors;
        params = new HashMap<>();
    }


    public RouterForward action(String action) {return null;}
}
