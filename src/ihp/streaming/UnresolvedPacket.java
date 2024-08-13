package ihp.streaming;

import ihp.observing.ResolvedPacket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public record UnresolvedPacket(byte[] data) {
    public ResolvedPacket asResolved() {
        return new ResolvedPacket(data);
    }

    public ResolvedPacket decompressAndResolve() throws IOException {
        MojangDataInputStream mdis = new MojangDataInputStream(new ByteArrayInputStream(data));
        int packetLength = mdis.readVarInt();
        if (packetLength != 0) {
            byte[] packetData = new byte[packetLength];
            Inflater inflater = new Inflater();
            inflater.setInput(mdis.readAllBytes());
            try {
                int len = inflater.inflate(packetData);
                if (len != packetLength) {
                    throw new IOException("length mismatch :/");
                }
                inflater.end();
            } catch (DataFormatException e) {
                throw new IOException(e);
            }
            return new ResolvedPacket(packetData);
        } else {
            return new ResolvedPacket(mdis.readAllBytes());
        }
    }

    public UnresolvedPacket asCompressed(int compressionThreshold) throws IOException {
        if (compressionThreshold < 0) {
            return this;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (data.length > compressionThreshold) {
            MojangDataOutputStream mdos = new MojangDataOutputStream(baos);
            mdos.writeVarInt(data.length);
            Deflater deflater = new Deflater();
            deflater.setInput(data);
            byte[] buffer = new byte[data.length + 10];
            int len = deflater.deflate(buffer, 0, buffer.length, Deflater.FULL_FLUSH);
            deflater.end();
            mdos.write(buffer, 0, len);
        } else {
            baos.write(0);
            baos.write(data);
        }
        return new UnresolvedPacket(baos.toByteArray());
    }
}
