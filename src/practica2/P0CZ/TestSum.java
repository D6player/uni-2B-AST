package practica2.P0CZ;

public class TestSum {

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new CounterThread();
        Thread t2 = new CounterThread();
        
        t1.start();
        t2.start();
        
        System.out.println("El comptador es: " + CounterThread.x);
    }
}
