package ihp.treading;

import ihp.observing.enums.PacketDirection;
import ihp.streaming.MojangDataInputStream;
import ihp.streaming.MojangDataOutputStream;
import ihp.streaming.UnresolvedPacket;

import java.io.IOException;
import java.net.Socket;

public class ClientToOriginThread implements Runnable {
    private final ThreadContext threadContext;
    public MojangDataInputStream clientReader;
    public MojangDataOutputStream originWriter;

    public ClientToOriginThread(ihp.treading.ThreadContext threadContext, Socket client, Socket origin) throws IOException {
        this.threadContext = threadContext;
        this.clientReader = new MojangDataInputStream(client.getInputStream());
        this.originWriter = new MojangDataOutputStream(origin.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                UnresolvedPacket packet = clientReader.readPacket();
                originWriter.sendPacket(threadContext.transformPacket(PacketDirection.CLIENT_TO_SERVER, packet));
            }
        } catch (Exception ex) {
            threadContext.handleException(ex);
        }
    }
}
