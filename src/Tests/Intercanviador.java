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
public class Intercanviador {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition cond = lock.newCondition();
    protected int arribats;
    protected Object recipient;
    
    public Object intercanviar(Object elem) {
        lock.lock();
        try {
            arribats %= 2;
            arribats++;
            if (arribats < 2)
                recipient = elem;
            while (arribats < 2)
                cond.awaitUninterruptibly();
            cond.signalAll();
            return recipient;
        } finally {
            recipient = elem;
            lock.unlock();
        }
    }
}
