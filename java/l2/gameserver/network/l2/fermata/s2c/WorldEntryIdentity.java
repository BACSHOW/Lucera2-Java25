package l2.gameserver.network.l2.fermata.s2c;

import java.util.UUID;

/**
 * S2C 0x0104 do protocolo Fermata 0xFD: World Entry Identity.
 * Emitido logo após a entrada do personagem no mundo para vincular
 * o server_uuid estável ao characterObjectId da sessão.
 */
public class WorldEntryIdentity extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0104;
    public static final byte REVISION = 1;

    private final byte[] _serverUuidBytes;
    private final int _characterObjectId;

    public WorldEntryIdentity(UUID serverUuid, int characterObjectId) {
        super(SUB_OPCODE);
        this._serverUuidBytes = uuidToBytes(serverUuid);
        this._characterObjectId = characterObjectId;
    }

    public WorldEntryIdentity(byte[] serverUuidBytes, int characterObjectId) {
        super(SUB_OPCODE);
        if (serverUuidBytes == null || serverUuidBytes.length != 16) {
            throw new IllegalArgumentException("serverUuidBytes must be exactly 16 bytes");
        }
        this._serverUuidBytes = serverUuidBytes;
        this._characterObjectId = characterObjectId;
    }

    private static byte[] uuidToBytes(UUID uuid) {
        if (uuid == null) {
            return new byte[16];
        }
        byte[] bytes = new byte[16];
        long most = uuid.getMostSignificantBits();
        long least = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) ((most >> (8 * (7 - i))) & 0xFF);
            bytes[8 + i] = (byte) ((least >> (8 * (7 - i))) & 0xFF);
        }
        return bytes;
    }

    @Override
    protected void writePayload() {
        writeC(REVISION);
        writeB(_serverUuidBytes);
        writeD(_characterObjectId);
    }
}
