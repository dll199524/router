package com.example.myrouter_api.thread;

public class ActionPostQueue {
    private ActionPost head;
    private ActionPost tail;

    synchronized void enqueue(ActionPost pendingPost) {
        if (pendingPost == null) throw new NullPointerException("null can not be enqueue");
        if (tail != null) {
            tail.next = pendingPost;
            tail = pendingPost;
        } else if (head == null) {
            head = tail = pendingPost;
        } else {throw new IllegalStateException("head present, but no tail");}
        notifyAll();
    }

    synchronized ActionPost poll() {
        ActionPost pendingPost = head;
        if (head != null) {
            head = head.next;
            if (head == null) tail = null;
        }
        return pendingPost;
    }

    synchronized ActionPost poll(int maxMillsToWait) throws InterruptedException {
        if (head == null) wait(maxMillsToWait);
        return poll();
    }
}
