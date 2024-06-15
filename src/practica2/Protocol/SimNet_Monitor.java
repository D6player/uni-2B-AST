package practica2.Protocol;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.SimNet;

public class SimNet_Monitor implements SimNet {

    protected CircularQueue<TCPSegment> queue;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condFull = lock.newCondition();
    private final Condition condFree = lock.newCondition();

    public SimNet_Monitor() {
        queue = new CircularQueue<>(Const.SIMNET_QUEUE_SIZE);
    }

    @Override
    public void send(TCPSegment seg) {
        lock.lock();
        try {
            while (queue.full())
                condFull.awaitUninterruptibly();
            condFree.signalAll();
            //System.out.println("snd --> " + seg);
            queue.put(seg);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TCPSegment receive() {
        lock.lock();
        try {
            while (queue.empty())
                condFree.awaitUninterruptibly();
            condFull.signalAll();
            TCPSegment seg = queue.get();
            //System.out.println("\t\t\t\t\t\t\t\treceived: " + seg);
            return seg;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getMTU() {
        throw new UnsupportedOperationException("Not supported yet. NO cal completar fins a la pràctica 3...");
    }

}
