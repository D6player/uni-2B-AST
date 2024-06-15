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
public class Bridge {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition esperaCanviSentit = lock.newCondition();
    protected Condition esperaPerFerCua = lock.newCondition();
    protected boolean sentit = false;
    protected boolean esperantCanviDeSentit = false;
    protected int cotxesTransitant;
    
    public void entrar(boolean sentitMeu) {
        lock.lock();
        try {
            while (sentit == sentitMeu && esperantCanviDeSentit)
                esperaPerFerCua.awaitUninterruptibly();
            
            while (sentit != sentitMeu && cotxesTransitant > 0) {
                esperantCanviDeSentit = true;
                esperaCanviSentit.awaitUninterruptibly();
            }
            esperaPerFerCua.signalAll();
            esperantCanviDeSentit = false;
            sentit = sentitMeu;
            
            cotxesTransitant++;
        } finally {
            lock.unlock();
        }
    }
    
    public void sortir(boolean sentitMeu) {
        lock.lock();
        try {
            if (--cotxesTransitant == 0)
                esperaCanviSentit.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
