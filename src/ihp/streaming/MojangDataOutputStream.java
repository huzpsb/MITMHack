package ihp.streaming;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MojangDataOutputStream extends DataOutputStream {
    public MojangDataOutputStream(OutputStream out) {
        super(out);
    }

    public void writeVarInt(int n) throws IOException {
        while (true) {
            if ((n & ~0x7F) == 0) {
                writeByte(n);
                return;
            }
            writeByte((n & 0x7F) | 0x80);
            n >>>= 7;
        }
    }

    public void writeString(String str) throws IOException {
        writeVarInt(str.length());
        write(str.getBytes(StandardCharsets.UTF_8));
    }

    public void sendPacket(UnresolvedPacket data) throws IOException {
        if (data == null) {
            return;
        }
        writeVarInt(data.data.length);
        write(data.data);
        flush();
    }
}
