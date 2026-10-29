/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ManagePledgePower;

public class RequestPledgePower
extends L2GameClientPacket {
    private int YM;
    private int fj;
    private int YN;

    @Override
    protected void readImpl() {
        this.YM = this.readD();
        this.fj = this.readD();
        if (this.fj == 2) {
            this.YN = this.readD();
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (this.fj == 2) {
            if (this.YM < 1 || this.YM > 9) {
                return;
            }
            if (player.getClan() != null && (player.getClanPrivileges() & 0x10) == 16) {
                if (this.YM == 9) {
                    this.YN = (this.YN & 8) + (this.YN & 0x400) + (this.YN & 0x8000) + (this.YN & 0x800) + (this.YN & 0x40000);
                }
                player.getClan().setRankPrivs(this.YM, this.YN);
                player.getClan().updatePrivsForRank(this.YM);
            }
        } else if (player.getClan() != null) {
            player.sendPacket((IStaticPacket)new ManagePledgePower(player, this.fj, this.YM));
        } else {
            player.sendActionFailed();
        }
    }
}

