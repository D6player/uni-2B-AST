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
public class GroupEx {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition resourceAvailable = lock.newCondition();
    protected Condition threadsToAccess = lock.newCondition();
    protected boolean inUse = false;
    protected int users = 0;
    protected int N;
    
    public GroupEx(int n) {
        N = n;
    }
    
    public void enter() {
        lock.lock();
        try {
            while (inUse)
                resourceAvailable.awaitUninterruptibly();
            
            inUse = (++users == N);
            while (!inUse)
                threadsToAccess.awaitUninterruptibly();
            threadsToAccess.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public void exit() {
        lock.lock();
        try {
            inUse = (--users == 0);
            if (!inUse)
                for (int i = 0; i < N; i++) resourceAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
}
