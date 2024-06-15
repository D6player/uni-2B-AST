package practica2.P0CZ.Monitor;

import java.util.concurrent.locks.ReentrantLock;

public class MonitorCZ {

    private final ReentrantLock lock = new ReentrantLock();
    private volatile int x = 0;

    public void inc() {
        lock.lock();
        try {
            x++;
                System.out.println("CounterThread : "+Thread.currentThread().threadId() + " incremneta " +x  );
        } finally {
            lock.unlock();
        }
    }

    public int getX() {
        lock.lock();
        try {
            return x;
        } finally {
            lock.unlock();
        }
    }

    
    public int getX2() {
        lock.lock();
        int tmp = x;
        lock.unlock();
        return tmp;
    }
    
    public int getX3() {
        return x;
    }
}
