package practica4;

import java.util.Arrays;
import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

    public Protocol(SimNet network) {
        super(network);
    }

    protected void ipInput(TCPSegment seg) {
        TSocket_base socket = getMatchingTSocket(
                seg.getDestinationPort(), seg.getSourcePort());
        if (socket != null)
            socket.processReceivedSegment(seg);
    }

    protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try {
            for (TSocket_base sck : activeSockets)
                if (sck.remotePort == remotePort && sck.localPort == localPort)
                    return sck;
            return null;
        } finally {
            lk.unlock();
        }
    }
}
