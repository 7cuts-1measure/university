package simulation.model.threadpool;

import static java.lang.Thread.interrupted;
import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import simulation.model.factory.Storage;

public class TaskRunner {
    private final int numThreads;
    private final Storage<Runnable> tasks;

    private final List<Thread> threads;
   
    public TaskRunner(int numThreads) {
        this.numThreads = numThreads;
        tasks = new Storage<>(this.numThreads);
        threads = new ArrayList<Thread>(numThreads);
        
        startThreads();
    }

    private final Runnable THREAD_TASK = new Runnable() {
        @Override
        public void run() {
            while (!interrupted()) {
                try {
                    final Runnable work = tasks.take();
                    work.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    };

    private void startThreads() {
        for (int i = 0; i < numThreads; i++) {
            threads.add(i, new Thread(THREAD_TASK));
            threads.get(i).start();
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        tasks.put(task);
    }

    
    public void shutdown() {
        threads.forEach(Thread::interrupt);       
    }
}