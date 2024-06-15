package practica5;

import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TSocket_base;
import util.TCPSegment;
import static java.lang.Integer.min;

public class TSocket extends TSocket_base {

    // Sender variables:
    protected int MSS;
    protected int snd_sndNxt;
    protected int snd_rcvWnd;
    protected int snd_rcvNxt;
    protected TCPSegment snd_UnacknowledgedSeg;
    protected boolean zero_wnd_probe_ON;

    // Receiver variables:
    protected CircularQueue<TCPSegment> rcv_Queue;
    protected int rcv_SegConsumedBytes;
    protected int rcv_rcvNxt;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        // init sender variables
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        snd_sndNxt = 0;
        // init receiver variables
        //rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        rcv_Queue = new CircularQueue<>(2);
        rcv_rcvNxt = 0;
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;
    }

    // -------------  SENDER PART  ---------------
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            int leftToSend = length;
            int sendedBytes;
            while (leftToSend > 0) {
                sendedBytes = zero_wnd_probe_ON ? 1 : min(MSS, leftToSend);
                
                network.send(snd_UnacknowledgedSeg = segmentize(data, offset, sendedBytes));
                startRTO();
                snd_sndNxt++;
                
                leftToSend -= sendedBytes;
                offset += sendedBytes;
                
                while (snd_rcvNxt != snd_sndNxt)
                    appCV.awaitUninterruptibly();
            }
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setData(data, offset, length);
        seg.setSeqNum(snd_sndNxt);
        seg.setPsh(true);
        seg.setDestinationPort(remotePort);
        seg.setSourcePort(localPort);
        return seg;
    }

    @Override
    protected void timeout() {
        lock.lock();
        try {
            if (snd_UnacknowledgedSeg == null)
                return;
            
            network.send(snd_UnacknowledgedSeg);
            startRTO();
        } finally {
            lock.unlock();
        }
    }

    // -------------  RECEIVER PART  ---------------
    @Override
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lock.lock();
        try {
            while (rcv_Queue.empty())
                appCV.awaitUninterruptibly();
            
            int r = 0;
            while (r < maxlen && !rcv_Queue.empty())
                r += consumeSegment(buf, offset+r, maxlen-r);
            return r;
        } finally {
            lock.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcv_Queue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcv_SegConsumedBytes);
        System.arraycopy(seg.getData(), rcv_SegConsumedBytes, buf, offset, a_agafar);
        rcv_SegConsumedBytes += a_agafar;
        if (rcv_SegConsumedBytes == seg.getDataLength()) {
            rcv_Queue.get();
            rcv_SegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment seg = new TCPSegment();
        seg.setAck(true);
        seg.setAckNum(rcv_rcvNxt);
        seg.setWnd(rcv_Queue.free());
        seg.setDestinationPort(remotePort);
        seg.setSourcePort(localPort);
        network.send(seg);
    }

    // -------------  SEGMENT ARRIVAL  -------------
    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            if (rseg.isAck()) {
                snd_rcvNxt = rseg.getAckNum();
                snd_rcvWnd = rseg.getWnd();
                zero_wnd_probe_ON = (snd_rcvWnd == 0);
                if (snd_rcvNxt != snd_sndNxt)
                    network.send(snd_UnacknowledgedSeg); // mai es dona crec
                else {
                    appCV.signalAll();
                    stopRTO();
                }
            }
            if (rseg.isPsh() && !rcv_Queue.full()) {
                if (rseg.getSeqNum() == rcv_rcvNxt) {
                    rcv_Queue.put(rseg);
                    appCV.signalAll();
                    rcv_rcvNxt++;
                }
                sendAck();
            }
        } finally {
            lock.unlock();
        }
    }
}
