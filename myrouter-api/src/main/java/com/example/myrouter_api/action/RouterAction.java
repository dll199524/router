package com.example.myrouter_api.action;

import android.content.Context;

import com.example.myrouter_api.result.RouterResult;

import java.util.Map;

public interface RouterAction {
    RouterResult invoke(Context context, Map<String, Object> requestData);
}
