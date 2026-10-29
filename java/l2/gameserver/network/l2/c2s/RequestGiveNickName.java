/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NickNameChanged;
import l2.gameserver.utils.Util;

public class RequestGiveNickName
extends L2GameClientPacket {
    private String abZ;
    private String _title;

    @Override
    protected void readImpl() {
        this.abZ = this.readS(Config.CNAME_MAXLEN);
        this._title = this.readS();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (!this._title.isEmpty() && !Util.isMatchingRegexp(this._title, Config.CLAN_TITLE_TEMPLATE)) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestGiveNickName.IncorrectTittle", player, new Object[0]));
            return;
        }
        if (player.isNoble() && this.abZ.equals(player.getName())) {
            player.setTitle(this._title);
            player.sendPacket((IStaticPacket)Msg.TITLE_HAS_CHANGED);
            player.broadcastPacket(new NickNameChanged(player));
            return;
        }
        if ((player.getClanPrivileges() & 4) != 4) {
            return;
        }
        if (player.getClan().getLevel() < 3) {
            player.sendPacket((IStaticPacket)Msg.TITLE_ENDOWMENT_IS_ONLY_POSSIBLE_WHEN_CLANS_SKILL_LEVELS_ARE_ABOVE_3);
            return;
        }
        UnitMember unitMember = player.getClan().getAnyMember(this.abZ);
        if (unitMember != null) {
            unitMember.setTitle(this._title);
            if (unitMember.isOnline()) {
                unitMember.getPlayer().sendPacket((IStaticPacket)Msg.TITLE_HAS_CHANGED);
                unitMember.getPlayer().sendChanges();
            }
        } else {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestGiveNickName.NotInClan", player, new Object[0]));
        }
    }
}

