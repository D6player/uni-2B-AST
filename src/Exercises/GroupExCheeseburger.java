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
public class GroupExCheeseburger {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition condWaiting = lock.newCondition();
    protected Condition condEntering = lock.newCondition();
    protected boolean inUse = false;
    protected int count = 0;
    protected int n;
    
    public GroupExCheeseburger(int n) {
        this.n = n;
    }
    
    public void enter() {
        lock.lock();
        try {
            while (inUse)
                condEntering.awaitUninterruptibly();
            
            inUse = (++count == n);
            while (!inUse)
                condWaiting.awaitUninterruptibly();
            condWaiting.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public void exit() {
        lock.lock();
        try {
            inUse = !(--count == 0);
            if (!inUse)
                for (int i = 0; i < n; i++)
                    condEntering.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) {
        int N = 9;
        final GroupExCheeseburger mon = new GroupExCheeseburger(3);
        Thread[] threads = new Thread[N];
        for (int i = 0; i < N; i++) {
            final int id = i;
            (threads[i] = new Thread(() -> {
                mon.enter();
                System.out.println("Thread " + id + " ha entrado");
                try { Thread.sleep(200); } catch (InterruptedException ex) {}
                System.out.println("Thread " + id + " ha salido");
                mon.exit();
            })).start();
        }
        
        for (Thread thread : threads)
            try { thread.join(); } catch (InterruptedException ex) {}
    }
}
