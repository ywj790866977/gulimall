package com.yanlaoge.gulimall.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.Executors.*;

/**
 * @author 好人
 * @date 2020-06-05 17:28
 **/
public class TreadTest {
    private static ExecutorService executorService = newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("start ...");
        //1
//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程: "+ Thread.currentThread().getId());
//            int i = 10/2;
//            System.out.println("运行结果: "+ i);
//        },executorService);
        //2.
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果: " + i);
//            return i;
//        }, executorService);
//        future.get()
        //3.
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程: " + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果: " + i);
//            return i;
//        }, executorService).whenCompleteAsync((res,exception)->{
//            System.out.println("结果: "+res);
//            System.out.println("异常: "+exception);
//        }).exceptionally(throwable -> {
//            return 10;
//        });
        //4. 处理
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程: " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果: " + i);
            return i;
        }, executorService).handle((res,err)->{
            if(res !=null){
                return res*2;
            }
            return 0;
        });
        System.out.println("end ...");
    }

}
