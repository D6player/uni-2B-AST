package practica2.P1Sync.Monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorSync {

    private final int N;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private int next_turn = 0;

    public MonitorSync(int N) {
        this.N = N;
    }

    public void waitForTurn(int id) {
        lock.lock();
        try {
            while (next_turn != id)
                cond.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }
    }

    public void transferTurn() {
        lock.lock();
        try {
            next_turn++;
            next_turn %= N;
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
