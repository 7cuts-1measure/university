package simulation.model.threadpool;

import static java.util.stream.IntStream.range;

import simulation.model.factory.Storage;

public class TaskRunner {
    private final int numThreads;
    private final Storage<Runnable> tasks;

    public TaskRunner(int numThreads) {
        this.numThreads = numThreads;
        tasks = new Storage<>(this.numThreads);
        startThreads();
    }

    private final Runnable THREAD_TASK = new Runnable() {
        @Override
        public void run() {
            while (true) {
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
        range(0, numThreads).forEach(i -> new Thread(THREAD_TASK).start());
    }

    public void submit(Runnable task) throws InterruptedException {
        tasks.put(task);
    }
}