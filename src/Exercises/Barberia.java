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
public class Barberia {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition barberAcabi = lock.newCondition();
    protected Condition barberTalli = lock.newCondition();
    protected Condition clientSegui = lock.newCondition();
    protected Condition clientSurti = lock.newCondition();
    protected boolean barberOcupat = false;
    
    public void demanarTall() {
        lock.lock();
        try {
            while (barberOcupat)
                barberAcabi.awaitUninterruptibly();
            barberOcupat = true;
            clientSegui.signal();
            
            while (barberOcupat)
                barberTalli.awaitUninterruptibly();
            
            clientSurti.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public void demanarClient() {
        lock.lock();
        try {
            barberAcabi.signal();
            while (!barberOcupat)
                clientSegui.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }
    }
    
    public void tallAcabat() {
        lock.lock();
        try {
            barberOcupat = false;
            barberTalli.signal();
            clientSurti.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }
    }
}
