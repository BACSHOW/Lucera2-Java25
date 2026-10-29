package l2.gameserver.network.l2.fermata.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.fermata.FermataClientPacket;

public class RequestFermataEconomyV1 extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x0100;

    private byte _nativeRecordKind;
    private int _listId;
    private int _count;
    private int[] _itemIds;
    private long[] _counts;

    @Override
    protected void readImpl() {
        _nativeRecordKind = (byte) readC();
        _listId = readD();
        _count = readD();
        if (_count < 0 || _count > 1000) {
            _count = 0;
            return;
        }
        _itemIds = new int[_count];
        _counts = new long[_count];
        for (int i = 0; i < _count; i++) {
            _itemIds[i] = readD();
            _counts[i] = readQ(); // Leitura de 64-bit inteira segura
        }
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null || _count == 0) {
            return;
        }

        // Validacao de overflow antes de qualquer alocacao ou debito
        try {
            long totalCost = 0L;
            for (int i = 0; i < _count; i++) {
                if (_counts[i] <= 0) {
                    return;
                }
                totalCost = Math.addExact(totalCost, _counts[i]);
            }
        } catch (ArithmeticException ae) {
            // Rejeita tentativas de overflow sem crashar
            return;
        }
    }

    public byte getNativeRecordKind() {
        return _nativeRecordKind;
    }

    public int getListId() {
        return _listId;
    }

    public int getCount() {
        return _count;
    }

    public long[] getCounts() {
        return _counts;
    }
}
