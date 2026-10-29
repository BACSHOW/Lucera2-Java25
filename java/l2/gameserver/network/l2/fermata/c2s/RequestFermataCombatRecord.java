package l2.gameserver.network.l2.fermata.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.s2c.CombatRecordResponse;

public class RequestFermataCombatRecord extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x0101;

    private byte _revision;
    private byte _kind; // 0 = Subscribe, 1 = Unsubscribe
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
        if (_revision != 1) {
            getClient().sendPacket(CombatRecordResponse.refusal(_requestId, (byte) 0)); // Unsupported
            return;
        }
        // Emite recusa gracefully se indisponivel ou confirmação stop
        if (_kind == 1) {
            getClient().sendPacket(CombatRecordResponse.stop(_requestId, 0L, (byte) 2)); // Quiescence / Stop
        } else {
            // Para subscrição de telemetria, responde com Stop limpo caso não haja sessão de combate ativa
            getClient().sendPacket(CombatRecordResponse.stop(_requestId, 1L, (byte) 0));
        }
    }
}
