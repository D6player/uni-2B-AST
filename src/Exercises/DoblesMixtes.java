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
public class DoblesMixtes {
    protected ReentrantLock lock = new ReentrantLock();
    protected Condition gentPerJugar = lock.newCondition();
    protected Condition queEsFormiUnGrup = lock.newCondition();
    protected boolean estaFormantseGrup = false;
    protected int noiesPreparades = 0;
    protected int noisPreparats = 0;
    
    public void noiPreparat() {
        lock.lock();
        try {
            while (estaFormantseGrup || noisPreparats == 2)
                queEsFormiUnGrup.awaitUninterruptibly();
            
            noisPreparats++;
            while (noisPreparats < 2 || noiesPreparades < 2)
                gentPerJugar.awaitUninterruptibly();
            estaFormantseGrup = true;
            noisPreparats--;
            
            gentPerJugar.signalAll();
            if (noisPreparats + noiesPreparades == 0) {
                for (int i = 0; i < 4; i++) queEsFormiUnGrup.signal();
                estaFormantseGrup = false;
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void noiaPreparada() {
        lock.lock();
        try {
            while (estaFormantseGrup || noiesPreparades == 2)
                queEsFormiUnGrup.awaitUninterruptibly();
            
            noiesPreparades++;
            while (noisPreparats < 2 || noiesPreparades < 2)
                gentPerJugar.awaitUninterruptibly();
            estaFormantseGrup = true;
            noiesPreparades--;
            
            gentPerJugar.signalAll();
            if (noisPreparats + noiesPreparades == 0) {
                for (int i = 0; i < 4; i++) queEsFormiUnGrup.signal();
                estaFormantseGrup = false;
            }
        } finally {
            lock.unlock();
        }
    }
}
