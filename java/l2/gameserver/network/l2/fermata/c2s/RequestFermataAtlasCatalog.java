package l2.gameserver.network.l2.fermata.c2s;

import java.util.Collections;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.s2c.AtlasCatalog;

public class RequestFermataAtlasCatalog extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x000B;

    private int _knownRevision;
    private int _page;

    @Override
    protected void readImpl() {
        _knownRevision = readD();
        _page = readH();
    }

    @Override
    protected void runImpl() {
        // Responde com catálogo seguro paginado
        getClient().sendPacket(new AtlasCatalog(1, 0, _page, 0, Collections.emptyList()));
    }
}
