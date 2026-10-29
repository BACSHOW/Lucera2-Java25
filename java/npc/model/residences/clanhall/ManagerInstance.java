/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.network.l2.s2c.AgitDecoInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.ResidenceManager;

public class ManagerInstance
extends ResidenceManager {
    public ManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected Residence getResidence() {
        return this.getClanHall();
    }

    @Override
    public L2GameServerPacket decoPacket() {
        ClanHall clanHall = this.getClanHall();
        if (clanHall != null) {
            return new AgitDecoInfo(clanHall);
        }
        return null;
    }

    @Override
    protected int getPrivUseFunctions() {
        return 2048;
    }

    @Override
    protected int getPrivSetFunctions() {
        return 16384;
    }

    @Override
    protected int getPrivDismiss() {
        return 8192;
    }

    @Override
    protected int getPrivDoors() {
        return 1024;
    }
}

