/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exercises;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author bard
 */
public class BufferBroadcastUn {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition everyoneConsumed = lock.newCondition();
    protected Condition somethingProduced = lock.newCondition();
    protected Object buffer = null;
    protected boolean[] available;
    protected int N;

    public BufferBroadcastUn(int N) {
        this.N = N;
        this.available = new boolean[N];
        for (int i = 0; i < N; i++)
            this.available[i] = false;
    }
    
    public void putValue(Object val) {
        lock.lock();
        try {
            boolean stillAvailable = stillAvailable();
            
            while (stillAvailable)
                everyoneConsumed.awaitUninterruptibly();
            
            buffer = val;
            setAvailable();
            somethingProduced.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public Object getValue(int id) {
        lock.lock();
        try {
            while (!available[id])
                somethingProduced.awaitUninterruptibly();
            
            available[id] = false;
            if (!stillAvailable())
                everyoneConsumed.signal();
            return buffer;
        } finally {
            lock.unlock();
        }
    }
    
    protected boolean stillAvailable() {
        for (boolean b : available) if (b) return true;
        return false;
    }
    
    protected void setAvailable() {
        for (int i = 0; i < N; i++)
            available[i] = true;
    }
}
