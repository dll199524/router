package com.example.myrouter_api.thread;

import com.example.myrouter_api.action.RouterAction;
import com.example.myrouter_api.core.MyRouter;
import com.example.myrouter_api.extra.ActionWrapper;
import com.example.myrouter_api.extra.Consts;
import com.example.myrouter_api.result.RouterResult;

public class BackGroundPoster implements Runnable, Poster{

    private final ActionPostQueue queue;
    private volatile boolean excuteRunning;

    public BackGroundPoster() {queue = new ActionPostQueue();}

    @Override
    public void enqueue(ActionPost actionPost) {
        synchronized (this) {
            queue.enqueue(actionPost);
            if (!excuteRunning) {
                excuteRunning = true;
                PosterSupport.executorService().execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
             try {
                while (true) {
                    ActionPost actionPost = queue.poll(1000);
                    if (actionPost == null) {
                        synchronized (this) {
                            if (actionPost == null) {
                                excuteRunning = false;
                                return;
                            }
                        }
                    }
                    ActionWrapper actionWrapper = actionPost.actionWrapper;
                    RouterAction routerAction = actionWrapper.getRouterAction();
                    RouterResult result = routerAction.invoke(actionPost.context, actionPost.params);
                    actionPost.actionCallBack.onResult(result);
                    actionPost.releasePendingPost();
                }
             } catch (InterruptedException e) {
                 MyRouter.logger.e(Consts.TAG, Thread.currentThread().getName() + "was interrupted");
             }
        } finally {
            excuteRunning = false;
        }
    }
}
