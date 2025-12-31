package fr.seeden.javatest;

import java.util.List;
import java.util.concurrent.*;

public class MultithreadingTest {

    static class CustomThread extends Thread {

        private final ExecutorService service;
        private final Runnable callback;
        public CustomThread(String name, ExecutorService executorService, Runnable callback) {
            setName(name);
            this.service = executorService;
            this.callback = callback;
        }

        @Override
        public void run() {
            System.out.println("Run: "+Thread.currentThread().getName());
            for (int i = 0; i < 100; i++) {
                try {
                    sleep((long) (Math.random()*100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Submit callback: "+Thread.currentThread().getName());
            try {
                this.service.submit(callback).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {

        //region ExecutorService tests
        ExecutorService executorService = Executors.newCachedThreadPool();

        Runnable callback = new Runnable() {
            @Override
            public void run() {
                System.out.println("Callback: "+Thread.currentThread().getName());
            }
        };//Replace by Callable<T>
        Callable<List<String>> callable = new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return List.of();
            }
        };

        try {
            System.out.println("Main function:");
            executorService.submit(callback).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        CustomThread t1 = new CustomThread("T1", executorService, callback);
        CustomThread t2 = new CustomThread("T2", executorService, callback);

        t1.start();
        t2.start();
        //endregion

        //region CompletableFuture tests
        CompletableFuture<List<String>> completableFuture = new CompletableFuture<>();

        completableFuture.thenAcceptAsync(list -> {
            System.out.println("Accept: "+Thread.currentThread().getName());
            System.out.println("In accept: "+list);

        });

        System.out.println("ça bloque?");

        new Thread(() -> {
            System.out.println("Call the callback on thread: "+Thread.currentThread().getName());
            completableFuture.complete(List.of(Thread.currentThread().getName()));
        }, "CF-Thread").start();

        try {
            System.out.println(completableFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Hello MT"+Thread.currentThread().getName());

        //endregion
    }
}