package l2.gameserver.network.l2.fermata.s2c;

public class PartyPing extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0017;

    private final int _senderObjectId;
    private final boolean _isTargetPing;
    private final int _targetObjectId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _durationMs;
    private final int _color;

    public PartyPing(int senderObjectId, boolean isTargetPing, int targetObjectId,
                     int x, int y, int z, int durationMs, int color) {
        super(SUB_OPCODE);
        this._senderObjectId = senderObjectId;
        this._isTargetPing = isTargetPing;
        this._targetObjectId = targetObjectId;
        this._x = x;
        this._y = y;
        this._z = z;
        this._durationMs = Math.max(500, Math.min(30000, durationMs));
        this._color = color;
    }

    @Override
    protected void writePayload() {
        writeD(_senderObjectId);
        writeC(_isTargetPing ? 0 : 1);
        writeD(_targetObjectId);
        writeD(_x);
        writeD(_y);
        writeD(_z);
        writeH(_durationMs);
        writeC(_color);
    }
}
