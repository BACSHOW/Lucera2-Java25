/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.RankPrivs;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeReceiveUpdatePower;

public class ManagePledgePower
extends L2GameServerPacket {
    private int fj;
    private int CU;
    private int ajI;

    public ManagePledgePower(Player player, int n, int n2) {
        this.CU = player.getClanId();
        this.fj = n;
        RankPrivs rankPrivs = player.getClan().getRankPrivs(n2);
        this.ajI = rankPrivs == null ? 0 : rankPrivs.getPrivs();
        player.sendPacket((IStaticPacket)new PledgeReceiveUpdatePower(this.ajI));
    }

    @Override
    protected final void writeImpl() {
        this.writeC(48);
        this.writeD(this.CU);
        this.writeD(this.fj);
        this.writeD(this.ajI);
    }
}

