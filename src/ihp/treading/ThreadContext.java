package ihp.treading;

import ihp.logging.IHPLogger;
import ihp.observing.PacketListener;
import ihp.observing.enums.PacketDirection;
import ihp.streaming.UnresolvedPacket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadContext {
    public final Thread originToClient;
    public final Thread clientToOrigin;
    public final OriginToClientThread otc;
    public final ClientToOriginThread cto;
    public final PacketListener listener = new PacketListener();
    private boolean isStopped = false;

    public ThreadContext(Socket client, Socket origin) throws IOException {
        try {
            otc = new ihp.treading.OriginToClientThread(this, client, origin);
            cto = new ihp.treading.ClientToOriginThread(this, client, origin);
            AtomicInteger counter = new AtomicInteger(0);
            this.originToClient = Thread.ofVirtual().name("OTC-" + counter.incrementAndGet()).start(otc);
            this.clientToOrigin = Thread.ofVirtual().name("CTO-" + counter.incrementAndGet()).start(cto);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public synchronized void stop() {
        if (isStopped) {
            return;
        }
        isStopped = true;
        originToClient.interrupt();
        clientToOrigin.interrupt();
    }

    public void handleException(Exception ex) {
        if (!isStopped) {
            stop();
            if (!(ex instanceof InterruptedException)) {
                IHPLogger.exception("bridging", ex);
            }
        }
    }

    public UnresolvedPacket transformPacket(PacketDirection d, UnresolvedPacket p) {
        return listener.transform(d, p, this);
    }
}
