package ihp.treading;

import ihp.observing.enums.PacketDirection;
import ihp.streaming.MojangDataInputStream;
import ihp.streaming.MojangDataOutputStream;
import ihp.streaming.UnresolvedPacket;

import java.io.IOException;
import java.net.Socket;

public class OriginToClientThread implements Runnable {
    private final ThreadContext threadContext;
    public MojangDataInputStream originReader;
    public MojangDataOutputStream clientWriter;

    public OriginToClientThread(ihp.treading.ThreadContext threadContext, Socket client, Socket origin) throws IOException {
        this.threadContext = threadContext;
        this.originReader = new MojangDataInputStream(origin.getInputStream());
        this.clientWriter = new MojangDataOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                UnresolvedPacket packet = originReader.readPacket();
                clientWriter.sendPacket(threadContext.transformPacket(PacketDirection.SERVER_TO_CLIENT, packet));
            }
        } catch (Exception ex) {
            threadContext.handleException(ex);
        }
    }
}
