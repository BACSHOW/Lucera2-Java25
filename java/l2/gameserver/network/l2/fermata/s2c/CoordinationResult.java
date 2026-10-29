package l2.gameserver.network.l2.fermata.s2c;

public class CoordinationResult extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0015;

    private final int _correlationId;
    private final int _acceptedBeaconCount;

    public CoordinationResult(int correlationId, int acceptedBeaconCount) {
        super(SUB_OPCODE);
        this._correlationId = correlationId;
        this._acceptedBeaconCount = Math.max(0, Math.min(0xFFFF, acceptedBeaconCount));
    }

    @Override
    protected void writePayload() {
        writeD(_correlationId);
        writeH(_acceptedBeaconCount);
    }
}
