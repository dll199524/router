package com.example.myrouter_api.action;

import com.example.myrouter_api.extra.ActionWrapper;


public interface RouterModule {
    ActionWrapper findAction(String actionName);
}
