package l2.gameserver.network.l2.fermata.s2c;

public class CombatRecordResponse extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0101;

    public static final byte KIND_SNAPSHOT = 0;
    public static final byte KIND_TELEMETRY = 1;
    public static final byte KIND_REFUSAL = 2;
    public static final byte KIND_STOP = 3;

    private final byte _revision;
    private final byte _kind;
    private final long _requestId;
    private final byte[] _payload;

    public CombatRecordResponse(byte revision, byte kind, long requestId, byte[] payload) {
        super(SUB_OPCODE);
        this._revision = revision;
        this._kind = kind;
        this._requestId = requestId;
        this._payload = payload == null ? new byte[0] : payload;
    }

    public static CombatRecordResponse refusal(long requestId, byte refusalCode) {
        byte[] p = new byte[]{refusalCode};
        return new CombatRecordResponse((byte) 1, KIND_REFUSAL, requestId, p);
    }

    public static CombatRecordResponse stop(long requestId, long epoch, byte stopCode) {
        java.nio.ByteBuffer b = java.nio.ByteBuffer.allocate(9).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        b.putLong(epoch);
        b.put(stopCode);
        return new CombatRecordResponse((byte) 1, KIND_STOP, requestId, b.array());
    }

    @Override
    protected void writePayload() {
        writeC(_revision);
        writeC(_kind);
        writeQ(_requestId);
        writeB(_payload);
    }
}
