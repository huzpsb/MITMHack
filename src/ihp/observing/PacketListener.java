package ihp.observing;

import ihp.hacking.HackBuilder;
import ihp.logging.IHPLogger;
import ihp.observing.enums.GameState;
import ihp.observing.enums.PacketDirection;
import ihp.streaming.MojangDataOutputStream;
import ihp.streaming.UnresolvedPacket;
import ihp.treading.ThreadContext;

import java.io.ByteArrayOutputStream;

public class PacketListener {
    public int compress = -1;
    public int versionNumber = -1;
    public int entityId = -1;
    private GameState gs = GameState.NONE;

    public UnresolvedPacket transform(PacketDirection d, UnresolvedPacket p, ThreadContext tcx) {
        try {
            ResolvedPacket rp;
            if (compress == -1) {
                rp = p.asResolved();
            } else {
                rp = p.decompressAndResolve();
            }
            int packetId = rp.mdis.readVarInt();

            switch (gs) {
                case NONE -> {
                    if (d == PacketDirection.CLIENT_TO_SERVER) {
                        if (packetId == 0x00) {
                            versionNumber = rp.mdis.readVarInt();
                            String server = rp.mdis.readString();
                            int port = rp.mdis.readUnsignedShort();
                            int next = rp.mdis.readVarInt();
                            IHPLogger.info("C2S Handshake %d %s %d %d", versionNumber, server, port, next);
                            if (next == 1) {
                                gs = GameState.PING;
                            } else if (next == 2) {
                                gs = GameState.LOGIN;
                            }
                        }
                    }
                }
                case LOGIN -> {
                    if (d == PacketDirection.CLIENT_TO_SERVER) {
                        if (packetId == 0x00) {
                            IHPLogger.info("C2S Username ", rp.mdis.readString());
                        }
                    } else {
                        if (packetId == 0x01) {
                            IHPLogger.warn("Online mode is not supported intentionally.");
                            // You are not fixing this by simply remove the line below. ;P
                            tcx.stop();
                        }
                        if (packetId == 0x02) {
                            gs = GameState.PLAY;
                        }
                        if (packetId == 0x03) {
                            compress = rp.mdis.readVarInt();
                        }
                    }
                }
                case PLAY -> {
                    if (d == PacketDirection.CLIENT_TO_SERVER) {
                        if (packetId == 0x06) {
                            String message = rp.mdis.readString();
                            if (message.contains("lbwnb")) {
                                IHPLogger.info("Received trigger message: %s", message);
                                tcx.otc.clientWriter.sendPacket(new UnresolvedPacket(HackBuilder.buildAttributePacket(entityId)).asCompressed(compress));
                                tcx.otc.clientWriter.sendPacket(new UnresolvedPacket((HackBuilder.buildAbilitiesPacket())).asCompressed(compress));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                MojangDataOutputStream mdos = new MojangDataOutputStream(baos);
                                mdos.writeVarInt(0x06);
                                mdos.writeString("OvO");
                                baos.write(rp.mdis.readAllBytes());
                                return new UnresolvedPacket(baos.toByteArray()).asCompressed(compress);
                            }
                        }

                    } else {
                        if (packetId == 0x75) {
                            if (entityId == -1) {
                                entityId = rp.mdis.readVarInt();
                                IHPLogger.info("S2C Entity ID: %d", entityId);
                            }
                        }
                    }
                }
            }
            return p;
        } catch (Exception ex) {
            IHPLogger.exception("bridging", ex);
        }
        return null;
    }
}
