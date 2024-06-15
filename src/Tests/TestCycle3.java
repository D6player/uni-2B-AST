/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tests;

/**
 *
 * @author bard
 */
public class TestCycle3 {
    public static void main(String[] args) {
        final Cycle3 mon = new Cycle3();
        Thread[] threads = new Thread[3];
        for (int i = 0; i < threads.length; i++) {
            final int id = i;
            Thread t = threads[i] = new Thread(() -> {
                for (int j = 0; j < 2; j++) {
                    try {
                        Thread.sleep(2000 + (long) Math.floor(Math.random() * 3000));
                    } catch (InterruptedException ex) {}
                    System.out.println("El thread " + id + " crida barrier.");
                    System.out.flush();
                    mon.barrier();
                    System.out.println("El thread " + id + " surt de la barrera.");
                    System.out.flush();
                }
            });
            t.start();
        }
        
        try {
            for (Thread thread : threads)
                thread.join();
        } catch (InterruptedException ex) {}
    }
}
