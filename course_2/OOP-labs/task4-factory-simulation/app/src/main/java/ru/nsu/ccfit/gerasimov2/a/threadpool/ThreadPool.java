package ru.nsu.ccfit.gerasimov2.a.threadpool;

import static java.util.stream.IntStream.range;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {

    private final int numThreads;
    private final BlockingQueue<Runnable> taskQueue;


    
    public ThreadPool(int numThreads) {
        this.numThreads = numThreads;
        taskQueue = new LinkedBlockingQueue<>();
        startThreads();
    }

    private final Runnable threadTask = new Runnable() {
        @Override
        public void run(){
            while(true) {
                try {
                    final Runnable work = taskQueue.take();
                    work.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        
    }; 

    private void startThreads() {
        range(0, numThreads).forEach(i -> new Thread(threadTask).start());
    }



    public void submit(Runnable task) throws InterruptedException {
        taskQueue.put(task);
    }

}
