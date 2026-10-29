package l2.gameserver.network.l2.fermata.s2c;

public class CoordinationSnapshot extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0014;
    public static final int MAX_BEACONS = 8;

    private final int _senderObjectId;
    private final int _correlationId;
    private final int _beaconCount;
    private final byte[] _kinds;
    private final int[] _xs;
    private final int[] _ys;
    private final int[] _zs;
    private final byte[] _colors;
    private final int[] _lifetimes;
    private final String _label;

    public CoordinationSnapshot(int senderObjectId, int correlationId, int beaconCount,
                                byte[] kinds, int[] xs, int[] ys, int[] zs,
                                byte[] colors, int[] lifetimes, String label) {
        super(SUB_OPCODE);
        this._senderObjectId = senderObjectId;
        this._correlationId = correlationId;
        int safe = Math.min(MAX_BEACONS, Math.max(0, beaconCount));
        this._beaconCount = safe;
        this._kinds = kinds == null ? new byte[0] : kinds;
        this._xs = xs == null ? new int[0] : xs;
        this._ys = ys == null ? new int[0] : ys;
        this._zs = zs == null ? new int[0] : zs;
        this._colors = colors == null ? new byte[0] : colors;
        this._lifetimes = lifetimes == null ? new int[0] : lifetimes;
        this._label = label == null ? "" : label;
    }

    @Override
    protected void writePayload() {
        writeD(_senderObjectId);
        writeD(_correlationId);
        writeH(_beaconCount);
        for (int i = 0; i < _beaconCount; i++) {
            writeC(_kinds[i] & 0xFF);
            writeD(_xs[i]);
            writeD(_ys[i]);
            writeD(_zs[i]);
            writeC(_colors[i] & 0xFF);
            writeH(Math.max(500, Math.min(120_000, _lifetimes[i])));
        }
        writeS(_label);
    }
}
