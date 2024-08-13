package ihp.streaming;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MojangDataInputStream extends DataInputStream {
    public MojangDataInputStream(InputStream in) {
        super(in);
    }

    public int readVarInt() throws IOException {
        int value = 0;
        int position = 0;
        while (true) {
            byte currentByte = readByte();
            value |= (currentByte & 0x7F) << position;
            if ((currentByte & 0x80) == 0) {
                break;
            }
            position += 7;
            if (position >= 32) {
                throw new IOException("VarInt exceeded 5 bytes");
            }
        }
        return value;
    }

    public String readString() throws IOException {
        int len = readVarInt();
        byte[] bytes = new byte[len];
        while (len > 0) {
            int read = read(bytes, bytes.length - len, len);
            if (read == -1) {
                throw new IOException("End of stream");
            }
            len -= read;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public UnresolvedPacket readPacket() throws IOException, InterruptedException {
        int len;
        try {
            len = readVarInt();
        } catch (Exception ex) {
            throw new InterruptedException();
        }
        byte[] data = new byte[len];
        while (len > 0) {
            int read = read(data, data.length - len, len);
            if (read == -1) {
                throw new IOException("End of stream");
            }
            len -= read;
        }
        return new UnresolvedPacket(data);
    }
}
