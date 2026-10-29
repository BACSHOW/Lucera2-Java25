package l2.gameserver.network.l2.fermata.c2s;

import java.util.Collections;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.s2c.ItemSources;

public class RequestFermataItemSources extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x000C;

    private int _itemId;

    @Override
    protected void readImpl() {
        _itemId = readD();
    }

    @Override
    protected void runImpl() {
        getClient().sendPacket(new ItemSources(1, _itemId, Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
    }
}
