package com.qianbajin.shortvdhelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @author Administrator
 * @Created at 2018/11/25 0025  16:39
 * @des
 */
class ThreadManager {

    //    private volatile static ScheduledThreadPoolExecutor sScheduledExecutorService;
//    private static final ThreadFactory THREADFACTORY = Executors.defaultThreadFactory();
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 32;
    private static ExecutorService sExecutorService;

//    static ScheduledThreadPoolExecutor getScheduledService() {
//        if (sScheduledExecutorService == null) {
//            synchronized (ThreadManager.class) {
//                if (sScheduledExecutorService == null) {
//                    sScheduledExecutorService = new ScheduledThreadPoolExecutor(15, THREADFACTORY);
//                }
//            }
//        }
//        return sScheduledExecutorService;
//    }

    private static ExecutorService getExecutor() {
        if (sExecutorService == null) {
            synchronized (ThreadManager.class) {
                if (sExecutorService == null) {
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    sExecutorService = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 2,
                            TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);
                }
            }
        }
        return sExecutorService;
    }

    public static void runOnBackground(Runnable runnable) {
        getExecutor().execute(runnable);
    }
}
