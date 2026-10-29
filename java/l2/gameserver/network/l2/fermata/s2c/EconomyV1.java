package l2.gameserver.network.l2.fermata.s2c;

public class EconomyV1 extends ExFermataEnvelope {

    public static final int SUB_OPCODE = 0x0100;

    public static final byte KIND_BUY_LIST = 0x11;
    public static final byte KIND_SELL_LIST = 0x42;
    public static final byte KIND_MULTI_SELL = (byte) 0xD0;
    public static final byte KIND_WAREHOUSE_DEPOSIT = 0x41;
    public static final byte KIND_WAREHOUSE_WITHDRAW = 0x42;

    public static final int MONSTER_RACE_TICKET_ID = 4443;

    private final byte _nativeRecordKind;
    private final byte[] _widePayload;

    public EconomyV1(byte nativeRecordKind, byte[] widePayload) {
        super(SUB_OPCODE);
        this._nativeRecordKind = nativeRecordKind;
        this._widePayload = widePayload == null ? new byte[0] : widePayload;
    }

    @Override
    public int requiredCapability() {
        return l2.gameserver.network.l2.fermata.FermataCapabilities.BIT_ECONOMY_64;
    }

    @Override
    protected void writePayload() {
        writeC(_nativeRecordKind);
        writeB(_widePayload);
    }
}
