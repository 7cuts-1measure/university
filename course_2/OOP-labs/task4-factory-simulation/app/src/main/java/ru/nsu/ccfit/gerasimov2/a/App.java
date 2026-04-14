package ru.nsu.ccfit.gerasimov2.a;

import java.time.Duration;

import ru.nsu.ccfit.gerasimov2.a.factory.IdGenerator;
import ru.nsu.ccfit.gerasimov2.a.factory.product.Motor;
import ru.nsu.ccfit.gerasimov2.a.factory.storage.Storage;

public class App {
    public static void testStorage() {
        final int MAX_THREADS = 10;
        final int STORAGE_CAP = 5;
        IdGenerator idGen = new IdGenerator();
        Storage<Motor> storage = new Storage<>(STORAGE_CAP);

        Runnable taskPut = new Runnable() {
            @Override
            public void run() {
                Motor motor;
                synchronized(this) {
                    motor = new Motor(idGen.next());
                }
                storage.put(motor);
                System.out.println(Thread.currentThread().getName() + " put motor, ID: " + motor.getID());

            }
        };
        Runnable taskGet = new Runnable() {
            @Override
            public void run() {
                try {
                    var motor = storage.get();
                    System.out.println(Thread.currentThread().getName() + " got motor, ID: " + motor.getID());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        };
        Thread geters[] = new Thread[MAX_THREADS];
        Thread puters[] = new Thread[MAX_THREADS];

        for (int i = 0; i < MAX_THREADS; ++i) {
            geters[i] = new Thread(taskGet, "Geter " + (i + 1));
            puters[i] = new Thread(taskPut, "Puter " + (i + 1));
        }

        Runnable taskSpawnGeters = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                for (Thread geter : geters) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    geter.start();
                }
            }
        };

        Runnable taskSpawnPuters = new Runnable() {
            @Override
            public void run() {
                for (Thread puter : puters) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    puter.start();
                }
            }
        };

        Runnable taskControlStorage = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        System.out.printf("> %d/%d\n", storage.size(), STORAGE_CAP);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.println("MAX_THREADS: " + MAX_THREADS);
        System.out.println("STORAGE_CAPACITY: " + STORAGE_CAP);

        new Thread(taskSpawnGeters).start();
        new Thread(taskSpawnPuters).start();
        new Thread(taskControlStorage).start();
    }

    
    public static void main(String[] args) {
        System.out.println("Start program");
        testStorage();
        System.out.println("End program");
    }


}
