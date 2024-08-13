package ihp.hacking;

import ihp.streaming.MojangDataOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HackBuilder {
    public static byte[] buildAttributePacket(int eid) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MojangDataOutputStream dos = new MojangDataOutputStream(baos);
        dos.writeVarInt(0x75);
        dos.writeVarInt(eid);
        dos.writeVarInt(1);

        dos.writeVarInt(6);
        dos.writeDouble(10);
        dos.writeVarInt(0);
        return baos.toByteArray();
    }

    public static byte[] buildAbilitiesPacket() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MojangDataOutputStream dos = new MojangDataOutputStream(baos);
        dos.writeVarInt(0x38);
        dos.writeByte(4);
        dos.writeFloat(0.05f);
        dos.writeFloat(0.1f);
        return baos.toByteArray();
    }
}
