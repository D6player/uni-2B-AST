/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Exercises;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bard
 */
public class CompteEstalvis {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cond = lock.newCondition();
    private double estalvis = 0.0;
    private long ultim = 0;
    private long torn_actual = 0;
    
    public void dipositar(double quantitat) {
        lock.lock();
        try {
            estalvis += quantitat;
            
            cond.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public void extreure(double quantitat) {
        lock.lock();
        try {
            long torn_meu = ultim++;
            while (estalvis < quantitat || torn_meu != torn_actual)
                cond.awaitUninterruptibly();
            
            estalvis -= quantitat;
            torn_actual++;
            cond.signalAll();
        } finally {
            System.out.println("Extret: " + quantitat);
            lock.unlock();
        }
    }
    
    public static void main(String[] args) {
        final CompteEstalvis compt = new CompteEstalvis();
        
        Thread t1 = new Thread(() -> {
            try { Thread.sleep((long) (Math.random() * 2000.0)); } catch (InterruptedException ex) {}
            System.out.println("Intento extreure 10");
            compt.extreure(10.0);
        });
        
        Thread t2 = new Thread(() -> {
            try { Thread.sleep((long) (Math.random() * 2000.0)); } catch (InterruptedException ex) {}
            System.out.println("Intento extreure 5");
            compt.extreure(5.0);
        });
        
        Thread t3 = new Thread(() -> {
            try { Thread.sleep((long) (Math.random() * 2000.0)); } catch (InterruptedException ex) {}
            System.out.println("Intento extreure 1");
            compt.extreure(1.0);
        });
        
        t1.start();
        t2.start();
        t3.start();
        
        try { Thread.sleep(3000); } catch (InterruptedException ex) {}
        compt.dipositar(20000.0);
        
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException ex) {}
    }
}
