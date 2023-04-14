package com.example.myrouter_api.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PosterSupport {
    private static volatile Poster mainPoster, backgroundPoster, asyncPoster;
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    public static Poster getMainPoster() {

        return mainPoster;
    }
    public static ExecutorService executorService() {return DEFAULT_EXECUTOR_SERVICE;}
}
