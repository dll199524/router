package com.example.myrouter_api.thread;

import android.content.Context;

import com.example.myrouter_api.extra.ActionWrapper;
import com.example.myrouter_api.result.ActionCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ActionPost {
    private final static List<ActionPost> pendingPostPoll = new ArrayList<>();
    public Context context;
    public ActionWrapper actionWrapper;
    public Map<String, Object> params;
    public ActionCallBack actionCallBack;
    ActionPost next;

    public ActionPost(Context context, ActionWrapper actionWrapper, Map<String, Object> params, ActionCallBack actionCallBack) {
        this.context = context;
        this.actionWrapper = actionWrapper;
        this.params = params;
        this.actionCallBack = actionCallBack;
    }

    public static ActionPost obtainActionPost(Context context, ActionWrapper actionWrapper, Map<String, Object> params, ActionCallBack actionCallBack) {
        synchronized (pendingPostPoll) {
            int size = pendingPostPoll.size();
            if (size > 0) {
                ActionPost actionPost = pendingPostPoll.remove(size - 1);
                actionPost.context = context;
                actionPost.actionWrapper = actionWrapper;
                actionPost.params = params;
                actionPost.next = null;
                actionPost.actionCallBack = actionCallBack;
                return actionPost;
            }
        }
        return new ActionPost(context, actionWrapper, params, actionCallBack);
    }

    public void releasePendingPost() {
        this.context = null;
        this.actionWrapper = null;
        this.next = null;
        this.actionCallBack = null;
        synchronized (pendingPostPoll) {
            if (pendingPostPoll.size() < 10000)
                pendingPostPoll.add(this);
        }
    }

}
