/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;

public class RequestPledgeSetMemberPowerGrade
extends L2GameClientPacket {
    private int Id;
    private String _name;

    @Override
    protected void readImpl() {
        this._name = this.readS(16);
        this.Id = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (this.Id < 1 || this.Id > 9) {
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            return;
        }
        if ((player.getClanPrivileges() & 0x10) == 16) {
            UnitMember unitMember = player.getClan().getAnyMember(this._name);
            if (unitMember != null) {
                if (Clan.isAcademy(unitMember.getPledgeType())) {
                    player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.CantChangeAcademyGrade", player, new Object[0]));
                    return;
                }
                if (this.Id > 5) {
                    unitMember.setPowerGrade(clan.getAffiliationRank(unitMember.getPledgeType()));
                } else {
                    unitMember.setPowerGrade(this.Id);
                }
                if (unitMember.isOnline()) {
                    unitMember.getPlayer().sendUserInfo();
                }
            } else {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.NotBelongClan", player, new Object[0]));
            }
        } else {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeSetMemberPowerGrade.HaveNotAuthority", player, new Object[0]));
        }
    }
}

