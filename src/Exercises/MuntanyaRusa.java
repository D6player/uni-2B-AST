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
public class MuntanyaRusa {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition espera = lock.newCondition();
    protected Condition espera_vago = lock.newCondition();
    protected Condition viatge = lock.newCondition();
    protected int capacitat;
    protected int usuaris;
    protected boolean en_us;
    protected boolean viatge_acabat;
    
    public MuntanyaRusa(int capacitat) {
        this.capacitat = capacitat;
        this.usuaris = 0;
        this.en_us = false;
        this.viatge_acabat = false;
    }
    
    public void pujar() {
        lock.lock();
        try {
            while (en_us)
                espera.awaitUninterruptibly();
            
            en_us = !(++usuaris < capacitat);
            while (!en_us)
                espera_vago.awaitUninterruptibly();
            espera_vago.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public void baixar() {
        lock.lock();
        try {
            while (!viatge_acabat)
                viatge.awaitUninterruptibly();
            
            en_us = (--usuaris == 0);
            if (!en_us) {
                for (int i = 0; i < capacitat; i++)
                    espera.signal();
                viatge_acabat = false;
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void arrencar() {
        lock.lock();
        try {
            while (!en_us)
                espera.awaitUninterruptibly();            
        } finally {
            lock.unlock();
        }
    }
    
    public void arribar() {
        lock.lock();
        try {
            viatge_acabat = true;
            viatge.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
