package l2.gameserver.network.l2.fermata.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.s2c.ReportTransferResponse;

public class RequestFermataReportTransfer extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x0102;

    private byte _revision;
    private byte _kind;
    private long _requestId;

    @Override
    protected void readImpl() {
        _revision = (byte) readC();
        _kind = (byte) readC();
        _requestId = readQ();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }
        // Emite falha segura se relatório não existir ou expirado
        getClient().sendPacket(ReportTransferResponse.failed(_requestId, (byte) 6)); // Unavailable
    }
}
