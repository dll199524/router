package com.example.myrouter_api.extra;

import com.example.myrouter_annotation.ThreadMode;
import com.example.myrouter_api.action.RouterAction;

public class ActionWrapper {
    private Class<? extends RouterAction> actionClass;
    private String path;
    private ThreadMode threadMode;
    private boolean extraProcess;
    private RouterAction routerAction;

    public ActionWrapper() {}

    private ActionWrapper(Class<? extends RouterAction> actionClass,
                          String path, ThreadMode threadMode,
                          boolean extraProcess, RouterAction routerAction) {
        this.actionClass = actionClass;
        this.path = path;
        this.threadMode = threadMode;
        this.extraProcess = extraProcess;
        this.routerAction = routerAction;
    }
    public static ActionWrapper bulid(Class<? extends RouterAction> actionClass,
                                      String path, ThreadMode threadMode,
                                      boolean extraProcess, RouterAction routerAction) {
        return new ActionWrapper(actionClass, path, threadMode, extraProcess, routerAction);
    }

    public Class<? extends RouterAction> getActionClass() {return actionClass;}

    public String getPath() {return path;}

    public ThreadMode getThreadMode() {return threadMode;}

    public boolean isExtraProcess() {return extraProcess;}

    public RouterAction getRouterAction() {return routerAction;}

    public void setRouterAction(RouterAction routerAction) {this.routerAction = routerAction;}
}
