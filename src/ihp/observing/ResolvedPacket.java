package ihp.observing;

import ihp.streaming.MojangDataInputStream;

import java.io.ByteArrayInputStream;

public class ResolvedPacket {
    public final MojangDataInputStream mdis;

    public ResolvedPacket(byte[] data) {
        this.mdis = new MojangDataInputStream(new ByteArrayInputStream(data));
    }
}
