package practica6;

import java.util.Iterator;
import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

    // Sender variables:
    protected int MSS;
    protected int snd_sndNxt;
    protected int snd_rcvNxt;
    protected int snd_rcvWnd;
    protected int snd_cngWnd;
    protected int snd_minWnd;
    protected CircularQueue<TCPSegment> snd_unacknowledged_segs;
    protected boolean zero_wnd_probe_ON;

    // Receiver variables:
    protected int rcv_rcvNxt;
    protected CircularQueue<TCPSegment> rcv_Queue;
    protected int rcv_SegConsumedBytes;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        // init sender variables:
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;
        snd_cngWnd = 3;
        snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
        snd_unacknowledged_segs = new CircularQueue(snd_cngWnd);
        
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        MSS = 10;
        // init receiver variables:
        rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        rcv_rcvNxt = 0;
    }

    // -------------  SENDER PART  ---------------
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            int toSend;
            TCPSegment seg;
            while (length > 0) {
                stopRTO();
                toSend = zero_wnd_probe_ON ? 1 : Integer.min(MSS, length);
                seg = segmentize(data, offset, toSend);
                this.printSndSeg(seg);
                network.send(seg);
                length -= toSend;
                offset += toSend;

                snd_unacknowledged_segs.put(seg);
                snd_sndNxt++;
                startRTO();
                
                while (snd_sndNxt - snd_rcvNxt >= (zero_wnd_probe_ON ? 1 : snd_minWnd))
                    appCV.awaitUninterruptibly();
            }
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setData(data, offset, length);
        seg.setDestinationPort(remotePort);
        seg.setSourcePort(localPort);
        seg.setSeqNum(snd_sndNxt);
        seg.setPsh(true);
        return seg;
    }

    @Override
    protected void timeout() {
        lock.lock();
        try {
            for (TCPSegment seg : snd_unacknowledged_segs)
                network.send(seg);

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
        seg.setAckNum(rcv_rcvNxt);
        seg.setWnd(rcv_Queue.free());
        seg.setDestinationPort(remotePort);
        seg.setSourcePort(localPort);
        seg.setAck(true);
        this.printSndSeg(seg);
        network.send(seg);
    }

    // -------------  SEGMENT ARRIVAL  -------------
    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            if (rseg.isPsh() && !rcv_Queue.full()) {
                if (rseg.getSeqNum() == rcv_rcvNxt) {
                    this.printRcvSeg(rseg);
                    rcv_Queue.put(rseg);
                    appCV.signal();
                    rcv_rcvNxt++;
                }
                sendAck();
            }
            if (rseg.isAck()) {
                this.printRcvSeg(rseg);
                snd_rcvNxt = rseg.getAckNum();
                snd_rcvWnd = rseg.getWnd();
                snd_minWnd = Integer.min(snd_cngWnd, snd_rcvWnd);
                zero_wnd_probe_ON = (snd_minWnd == 0);
                
                while (!snd_unacknowledged_segs.empty() && 
                        snd_unacknowledged_segs.peekFirst().
                        getSeqNum() < snd_rcvNxt)
                    snd_unacknowledged_segs.get();
                appCV.signalAll();
                stopRTO();
                startRTO();
            }
        } finally {
            lock.unlock();
        }
    }

    private void unacknowledgedSegments_content() {
        Iterator<TCPSegment> ite = snd_unacknowledged_segs.iterator();
        log.printBLACK("\n-------------- content begins  --------------");
        while (ite.hasNext()) {
            log.printBLACK(ite.next().toString());
        }
        log.printBLACK("-------------- content ends    --------------\n");
    }
}
