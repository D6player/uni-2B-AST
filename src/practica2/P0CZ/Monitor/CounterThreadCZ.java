package practica2.P0CZ.Monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CounterThreadCZ extends Thread {

    private final MonitorCZ mon;
    private final int numberOfIterations = 10000;

    public CounterThreadCZ(MonitorCZ monitor) {
        this.mon = monitor;
    }

    @Override
    public void run() {
        delayArtificial(1000);
        for (int i = 0; i < numberOfIterations; i++) {
            mon.inc();
        
        }
    }
    
    private void delayArtificial(long ms){
        try {
            sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(CounterThreadCZ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
