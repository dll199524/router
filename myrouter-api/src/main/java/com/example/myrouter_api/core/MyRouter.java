package com.example.myrouter_api.core;


import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.myrouter_api.action.RouterInterceptor;
import com.example.myrouter_api.action.RouterModule;
import com.example.myrouter_api.exception.InitException;
import com.example.myrouter_api.extra.ActionWrapper;
import com.example.myrouter_api.extra.Consts;
import com.example.myrouter_api.extra.ErrorActionWrapper;
import com.example.myrouter_api.interceptor.ActionInterceptor;
import com.example.myrouter_api.interceptor.ErrorInterceptor;
import com.example.myrouter_api.utils.ClassUtils;
import com.example.myrouter_api.utils.DefaultLogger;
import com.example.myrouter_api.utils.ILogger;
import com.example.myrouter_api.wrapper.ActionWarpper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRouter {
    private static volatile MyRouter instance;
    private Context applicationContext = null;
    public volatile static ILogger logger = new DefaultLogger();
    private volatile static boolean isDebug = false;
    private volatile static boolean hasInit = false;
    // 缓存的 RouterAction RouterModule
    private volatile static Map<String, ActionWrapper> cacheRouterActions = new HashMap<>();
    private volatile static Map<String, RouterModule> cacheRouterModules = new HashMap<>();
    private static List<String> allModuleClassNames;
    private static List<ActionInterceptor> interceptors = new ArrayList<>();

    public static synchronized void openDebug() {
        isDebug = true;
        logger.showLog(true);
        logger.d(Consts.TAG, "MyRouter openDebug");
    }
    private MyRouter() {}
    public static MyRouter getInstance() {
        if (instance == null) {
            synchronized (MyRouter.class) {
                if (instance == null) instance = new MyRouter();
            }
        }
        return instance;
    }


    public void init(Context context) {
        if (hasInit) throw new InitException("myrouter already initialized, it can only be initialized once");
        hasInit = true;
        this.applicationContext = context;
        try {
            allModuleClassNames = ClassUtils.getFileNameByPackageName(context, Consts.ROUTER_MODULE_PACK_NAME);
        } catch (PackageManager.NameNotFoundException | IOException e) {e.printStackTrace();}
        for (String className : allModuleClassNames) {logger.d(Consts.TAG, "扫描到" + className);}
        //扫描所有拦截器
        scanAllInterceptors(context);
    }

    private void scanAllInterceptors(Context context) {

    }

    private RouterForward action(String actionName) {
        // 1. 动态先查找加载 Module
        // actionName 的格式必须是 xxx/xxx
        if (!actionName.contains("/")) {
            String message = "action name  format error -> <" + actionName + ">, like: moduleName/actionName";
            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper(), interceptors);
        }
        // 2.获取 moduleName，实例化 Module，并缓存
        String moudleName = actionName.split("/")[0];
        String moduleClassName = searchModuleClassName(moudleName);
        if (TextUtils.isEmpty(moduleClassName)) {
            String message = String.format("Please check to the action name is correct: according to the <%s> cannot find module %s.", actionName, moduleName);
            debugMessage(message);
            return new RouterForward(new ErrorActionWrapper(), interceptors);
        }
        RouterModule routerModule = cacheRouterModules.get(moduleClassName);
        if (routerModule == null) {
            try {
                Class<? extends RouterModule> clazz = (Class<? extends RouterModule>) Class.forName(moduleClassName);
                routerModule = clazz.newInstance();
                cacheRouterModules.put(moduleClassName, routerModule);
            } catch (Exception e) {
                e.printStackTrace();
                String message = "instance moudle error" + e.getMessage();
                debugMessage(message);
                return new RouterForward(new ErrorActionWrapper(), interceptors);
            }
        }

        ActionWrapper actionWrapper = cacheRouterActions.get(actionName);
        if (actionWrapper == null) {

        }



    }

    private String searchModuleClassName(String moudleName) {
        for (String muduleClassName : allModuleClassNames) {
            if (muduleClassName.contains(moudleName))
                return muduleClassName;
        }
        return null;
    }


    private void debugMessage(String msg) {
        if (isDebug) {
            logger.d(Consts.TAG, msg);
            showToast(msg);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show();
    }


}
