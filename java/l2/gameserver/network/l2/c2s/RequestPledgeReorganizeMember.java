/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestPledgeReorganizeMember
extends L2GameClientPacket {
    int _replace;
    String _subjectName;
    int _targetUnit;
    String _replaceName;

    @Override
    protected void readImpl() {
        this._replace = this.readD();
        this._subjectName = this.readS(16);
        this._targetUnit = this.readD();
        if (this._replace > 0) {
            this._replaceName = this.readS();
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendActionFailed();
            return;
        }
        if (!player.isClanLeader()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.ChangeAffiliations", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        UnitMember unitMember = clan.getAnyMember(this._subjectName);
        if (unitMember == null) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.NotInYourClan", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (unitMember.getPledgeType() == this._targetUnit) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.AlreadyInThatCombatUnit", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (this._targetUnit != 0 && clan.getSubUnit(this._targetUnit) == null) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.NoSuchCombatUnit", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (Clan.isAcademy(this._targetUnit)) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.AcademyViaInvitation", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (Clan.isAcademy(unitMember.getPledgeType())) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CantMoveAcademyMember", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        UnitMember unitMember2 = null;
        if (this._replace > 0) {
            unitMember2 = clan.getAnyMember(this._replaceName);
            if (unitMember2 == null) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongClan", player, new Object[0]));
                player.sendActionFailed();
                return;
            }
            if (unitMember2.getPledgeType() != this._targetUnit) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterNotBelongCombatUnit", player, new Object[0]));
                player.sendActionFailed();
                return;
            }
            if (unitMember2.isSubLeader() != 0) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.CharacterLeaderAnotherCombatUnit", player, new Object[0]));
                player.sendActionFailed();
                return;
            }
        } else {
            if (clan.getUnitMembersSize(this._targetUnit) >= clan.getSubPledgeLimit(this._targetUnit)) {
                if (this._targetUnit == 0) {
                    player.sendPacket((IStaticPacket)new SystemMessage(1835).addString(clan.getName()));
                } else {
                    player.sendPacket((IStaticPacket)Msg.THE_ACADEMY_ROYAL_GUARD_ORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
                }
                player.sendActionFailed();
                return;
            }
            if (unitMember.isSubLeader() != 0) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestPledgeReorganizeMember.MemberLeaderAnotherUnit", player, new Object[0]));
                player.sendActionFailed();
                return;
            }
        }
        SubUnit subUnit = null;
        if (unitMember2 != null) {
            subUnit = unitMember2.getSubUnit();
            subUnit.replace(unitMember2.getObjectId(), unitMember.getPledgeType());
            clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(unitMember2));
            if (unitMember2.isOnline()) {
                unitMember2.getPlayer().updatePledgeClass();
                unitMember2.getPlayer().broadcastCharInfo();
            }
        }
        subUnit = unitMember.getSubUnit();
        subUnit.replace(unitMember.getObjectId(), this._targetUnit);
        clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(unitMember));
        if (unitMember.isOnline()) {
            unitMember.getPlayer().updatePledgeClass();
            unitMember.getPlayer().broadcastCharInfo();
        }
    }
}

