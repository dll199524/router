package com.example.myrouter_api.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.example.myrouter_api.action.RouterAction;
import com.example.myrouter_api.extra.ActionWrapper;
import com.example.myrouter_api.result.RouterResult;

/*
处理主线程切换
 */
public class HandlerPoster extends Handler implements Poster {

    private int maxMillisInsideHandleMessage;
    private final ActionPostQueue queue;
    private boolean handleActive;

    protected HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {
        super(looper);
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;
        queue = new ActionPostQueue();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean rescheduled = false;
        try {
            long started = SystemClock.uptimeMillis();
            while (true) {
                ActionPost actionPost = queue.poll();
                if (actionPost == null) {
                    synchronized (this) {
                        if (actionPost == null) {
                            rescheduled = false;
                            return;
                        }
                    }
                }

                ActionWrapper actionWrapper = actionPost.actionWrapper;
                RouterAction routerAction = actionWrapper.getRouterAction();
                RouterResult result = routerAction.invoke(actionPost.context, actionPost.params);
                actionPost.actionCallBack.onResult(result);
                actionPost.releasePendingPost();

                long timeInMethod = SystemClock.uptimeMillis() - started;
                if (timeInMethod >= maxMillisInsideHandleMessage) {
                    if (!sendMessage(obtainMessage()))
                        throw new RuntimeException("could not send handler message");
                    rescheduled = true;
                    return;
                }
            }
        } finally {
            handleActive = rescheduled;
        }
    }

    @Override
    public void enqueue(ActionPost actionPost) {
        queue.enqueue(actionPost);
        if (!handleActive) {
            handleActive = true;
            if (!sendMessage(obtainMessage()))
                throw new RuntimeException("could not send handler message");
        }
    }
}
