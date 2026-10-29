package l2.gameserver.network.l2.fermata.s2c;

public class ReportTransferResponse extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0102;

    public static final byte KIND_READY = 0;
    public static final byte KIND_PROGRESS = 1;
    public static final byte KIND_PUBLISHED = 2;
    public static final byte KIND_DOWNLOAD = 3;
    public static final byte KIND_DOWNLOAD_CHUNK = 4;
    public static final byte KIND_FAILED = 5;

    private final byte _revision;
    private final byte _kind;
    private final long _requestId;
    private final byte[] _payload;

    public ReportTransferResponse(byte revision, byte kind, long requestId, byte[] payload) {
        super(SUB_OPCODE);
        this._revision = revision;
        this._kind = kind;
        this._requestId = requestId;
        this._payload = payload == null ? new byte[0] : payload;
    }

    public static ReportTransferResponse failed(long requestId, byte reason) {
        return new ReportTransferResponse((byte) 1, KIND_FAILED, requestId, new byte[]{reason});
    }

    @Override
    protected void writePayload() {
        writeC(_revision);
        writeC(_kind);
        writeQ(_requestId);
        writeB(_payload);
    }
}
