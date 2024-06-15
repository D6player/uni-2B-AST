package practica2.P0CZ.Monitor;

public class TestSumCZ {

    public static void main(String[] args) throws InterruptedException {
        MonitorCZ monitor = new MonitorCZ();
        Thread t1 = new CounterThreadCZ(monitor);
        //Thread t2 = new CounterThreadCZ(monitor);
        
        t1.start();
        //t2.start();
        while(monitor.getX3()==0);
        t1.join();
        //t2.join();
        
        System.out.println("x: " + monitor.getX());
    }
}
