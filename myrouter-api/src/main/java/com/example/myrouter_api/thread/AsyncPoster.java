package com.example.myrouter_api.thread;

import com.example.myrouter_api.action.RouterAction;
import com.example.myrouter_api.extra.ActionWrapper;
import com.example.myrouter_api.result.RouterResult;

public class AsyncPoster implements Runnable, Poster{

    private final ActionPostQueue queue;
    public AsyncPoster() {queue = new ActionPostQueue();}

    @Override
    public void enqueue(ActionPost actionPost) {
        queue.enqueue(actionPost);
        PosterSupport.executorService().execute(this);
    }

    @Override
    public void run() {
        ActionPost actionPost = queue.poll();
        if (actionPost == null) throw new IllegalStateException("no pending post available");
        ActionWrapper actionWrapper = actionPost.actionWrapper;
        RouterAction routerAction = actionWrapper.getRouterAction();
        RouterResult result = routerAction.invoke(actionPost.context, actionPost.params);
        actionPost.actionCallBack.onResult(result);
        actionPost.releasePendingPost();
    }
}
