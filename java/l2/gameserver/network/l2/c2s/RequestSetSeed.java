/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.CastleManorManager;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.manor.SeedProduction;

public class RequestSetSeed
extends L2GameClientPacket {
    private int _count;
    private int Gh;
    private long[] acR;

    @Override
    protected void readImpl() {
        this.Gh = this.readD();
        this._count = this.readD();
        if (this._count * 12 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1) {
            this._count = 0;
            return;
        }
        this.acR = new long[this._count * 3];
        int n = 0;
        while (n < this.acR.length) {
            int n2 = this.readD();
            long l = this.readD();
            long l2 = this.readD();
            if (n2 < 1 || l < 0L || l2 < 0L) {
                this._count = 0;
                return;
            }
            this.acR[n++] = n2;
            this.acR[n++] = l;
            this.acR[n++] = l2;
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || this._count == 0) {
            return;
        }
        if (player.getClan() == null) {
            player.sendActionFailed();
            return;
        }
        Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this.Gh);
        if (castle == null || castle.getOwnerId() != player.getClanId() || (player.getClanPrivileges() & 0x10000) != 65536) {
            player.sendActionFailed();
            return;
        }
        if (castle.isNextPeriodApproved()) {
            player.sendPacket((IStaticPacket)SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
            player.sendActionFailed();
            return;
        }
        ArrayList<SeedProduction> arrayList = new ArrayList<SeedProduction>(this._count);
        List<Manor.SeedData> list = Manor.getInstance().getSeedsForCastle(this.Gh);
        block0: for (int i = 0; i < this._count; ++i) {
            int n = (int)this.acR[i * 3 + 0];
            long l = this.acR[i * 3 + 1];
            long l2 = this.acR[i * 3 + 2];
            if (n <= 0) continue;
            for (Manor.SeedData seedData : list) {
                long l3;
                if (seedData.getId() != n) continue;
                if (l > seedData.getSeedLimit() || l2 < (l3 = (long)Manor.getInstance().getSeedBasicPrice(n)) * 60L / 100L || l2 > l3 * 10L) continue block0;
                SeedProduction seedProduction = CastleManorManager.getInstance().getNewSeedProduction(n, l, l2, l);
                arrayList.add(seedProduction);
                continue block0;
            }
        }
        castle.setSeedProduction(arrayList, 1);
        castle.saveSeedData(1);
    }
}

