/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tests;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author bard
 */
public class Cycle3 {
    protected final ReentrantLock lock = new ReentrantLock();
    protected final Condition condB = lock.newCondition();
    protected final Condition condL = lock.newCondition();
    protected boolean leaving = false;
    protected int waiters = 0;
    
    public void barrier() {
        lock.lock();
        try {
            while (leaving)
                condL.awaitUninterruptibly();
            
            waiters++;
            leaving = waiters == 3;
            while (!leaving)
                condB.awaitUninterruptibly();
            condB.signalAll();
            
            waiters--;
            if (waiters == 0) {
                leaving = false;
                condL.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
