/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exercises;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Queue;

/**
 *
 * @author bard
 */
public class BufferOrdre {
    protected ReentrantLock lock = new ReentrantLock();
    protected Queue<Condition> condsPutters = new LinkedList<>();
    protected Queue<Condition> condsGetters = new LinkedList<>();
    protected Object buffer = null;
    
    public void put(Object elem) {
        lock.lock();
        try {
            while (buffer != null) {
                Condition cond = lock.newCondition();
                condsPutters.add(cond);
                cond.awaitUninterruptibly();
            }
            buffer = elem;
            if (!condsGetters.isEmpty())
                condsGetters.remove().signal();
        } finally {
            lock.unlock();
        }
    }
    
    public Object get() {
        lock.lock();
        try {
            while (buffer == null) {
                Condition cond = lock.newCondition();
                condsGetters.add(cond);
                cond.awaitUninterruptibly();
            }
            if (!condsPutters.isEmpty())
                condsPutters.remove().signal();
            return buffer;
        } finally {
            buffer = null;
            lock.unlock();
        }
    }
}
