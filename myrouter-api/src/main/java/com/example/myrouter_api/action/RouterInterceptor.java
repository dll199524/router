package com.example.myrouter_api.action;

import com.example.myrouter_api.interceptor.ActionInterceptor;

import java.util.List;

public interface RouterInterceptor {
    List<ActionInterceptor> getInterceptors();
}
