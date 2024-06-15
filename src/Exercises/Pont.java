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
public class Pont {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition deixinPassarMeuTipus = lock.newCondition();
    protected Condition queNoVulguinCanviar = lock.newCondition();
    protected Condition queNoHagiTransit = lock.newCondition();
    protected boolean esVolCanviar = false;
    protected char deixantPassar = 'c';
    protected int enTransit = 0;
    
    public void entrar(char tipus) {
        lock.lock();
        try {
            while (deixantPassar == tipus && esVolCanviar)
                queNoVulguinCanviar.awaitUninterruptibly();
            
            while (deixantPassar != tipus)
                deixinPassarMeuTipus.awaitUninterruptibly();
            
            enTransit++;
        } finally {
            lock.unlock();
        }
    }
    
    public void sortir(char tipus) {
        lock.lock();
        try {
            enTransit--;
            if (enTransit == 0)
                queNoHagiTransit.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public void canviar() {
        lock.lock();
        try {
            esVolCanviar = true;
            
            while (enTransit > 0)
                queNoHagiTransit.awaitUninterruptibly();
            
            deixantPassar = deixantPassar == 'c' ? 'b' : 'c';
            esVolCanviar = false;
            
            deixinPassarMeuTipus.signalAll();
            queNoVulguinCanviar.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
