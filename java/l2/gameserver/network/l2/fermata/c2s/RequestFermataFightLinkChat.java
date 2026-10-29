package l2.gameserver.network.l2.fermata.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.fermata.FermataClientPacket;

public class RequestFermataFightLinkChat extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x0103;

    private byte _revision;
    private int _channel;
    private String _template;
    private String _recipient;
    private int _count;

    @Override
    protected void readImpl() {
        _revision = (byte) readC();
        _channel = readD();
        _template = readS();
        _recipient = readS();
        _count = readH();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null || _count < 1 || _count > 4) {
            return;
        }
        // Processamento seguro sem re-execucao de comandos externos
    }
}
