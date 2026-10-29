package l2.gameserver.network.l2.fermata.c2s;

import java.util.Collections;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.s2c.NpcAtlasEntry;
import l2.gameserver.templates.npc.NpcTemplate;

public class RequestFermataNpcAtlasEntry extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x000D;

    private int _npcId;

    @Override
    protected void readImpl() {
        _npcId = readD();
    }

    @Override
    protected void runImpl() {
        NpcTemplate template = NpcHolder.getInstance().getTemplate(_npcId);
        if (template == null) {
            getClient().sendPacket(new NpcAtlasEntry(1, _npcId, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Collections.emptyList()));
            return;
        }

        getClient().sendPacket(new NpcAtlasEntry(1, _npcId, template.level, 0,
                (int) template.baseHpMax, (int) template.baseMpMax,
                (int) template.basePAtk, (int) template.basePDef,
                (int) template.baseMAtk, (int) template.baseMDef,
                (int) template.rewardExp, (int) template.rewardSp,
                60, Collections.emptyList()));
    }
}
