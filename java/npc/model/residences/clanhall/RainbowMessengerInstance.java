/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import java.io.Serializable;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.TimeUtils;

public class RainbowMessengerInstance
extends NpcInstance {
    public static final int ITEM_ID = 8034;

    public RainbowMessengerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!RainbowMessengerInstance.canBypassCheck(player, this)) {
            return;
        }
        ClanHall clanHall = this.getClanHall();
        ClanHallMiniGameEvent clanHallMiniGameEvent = (ClanHallMiniGameEvent)clanHall.getSiegeEvent();
        if (string.equalsIgnoreCase("register")) {
            if (clanHallMiniGameEvent.isRegistrationOver()) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti014.htm", new Object[0]);
                return;
            }
            Clan clan = player.getClan();
            if (clan == null || clan.getLevel() < 3 || clan.getAllSize() <= 5) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm", new Object[0]);
                return;
            }
            if (player.getClan().isPlacedForDisband()) {
                player.sendPacket((IStaticPacket)SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
                return;
            }
            if (clan.getLeaderId() != player.getObjectId()) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm", new Object[0]);
                return;
            }
            if (clan.getHasHideout() > 0) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti012.htm", new Object[0]);
                return;
            }
            if (clanHallMiniGameEvent.getSiegeClan("attackers", clan) != null) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti013.htm", new Object[0]);
                return;
            }
            long l = player.getInventory().getCountOf(8034);
            if (l == 0L) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti008.htm", new Object[0]);
            } else {
                if (!player.consumeItem(8034, l)) {
                    return;
                }
                CMGSiegeClanObject cMGSiegeClanObject = new CMGSiegeClanObject("attackers", clan, l);
                clanHallMiniGameEvent.addObject("attackers", cMGSiegeClanObject);
                SiegeClanDAO.getInstance().insert(clanHall, cMGSiegeClanObject);
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti009.htm", new Object[0]);
            }
        } else if (string.equalsIgnoreCase("cancel")) {
            if (clanHallMiniGameEvent.isRegistrationOver()) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti017.htm", new Object[0]);
                return;
            }
            Clan clan = player.getClan();
            if (clan == null || clan.getLevel() < 3) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm", new Object[0]);
                return;
            }
            if (clan.getLeaderId() != player.getObjectId()) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm", new Object[0]);
                return;
            }
            Object s = clanHallMiniGameEvent.getSiegeClan("attackers", clan);
            if (s == null) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti016.htm", new Object[0]);
            } else {
                clanHallMiniGameEvent.removeObject("attackers", (Serializable)s);
                SiegeClanDAO.getInstance().delete(clanHall, (SiegeClanObject)s);
                ItemFunctions.addItem((Playable)player, 8034, ((SiegeClanObject)s).getParam() / 2L, true);
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti005.htm", new Object[0]);
            }
        } else if (string.equalsIgnoreCase("refund")) {
            if (clanHallMiniGameEvent.isRegistrationOver()) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm", new Object[0]);
                return;
            }
            Clan clan = player.getClan();
            if (clan == null || clan.getLevel() < 3) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti011.htm", new Object[0]);
                return;
            }
            if (clan.getLeaderId() != player.getObjectId()) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti010.htm", new Object[0]);
                return;
            }
            Object s = clanHallMiniGameEvent.getSiegeClan("refund", clan);
            if (s == null) {
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti020.htm", new Object[0]);
            } else {
                clanHallMiniGameEvent.removeObject("refund", (Serializable)s);
                SiegeClanDAO.getInstance().delete(clanHall, (SiegeClanObject)s);
                ItemFunctions.addItem((Playable)player, 8034, ((SiegeClanObject)s).getParam(), true);
                this.showChatWindow(player, "residence2/clanhall/messenger_yetti019.htm", new Object[0]);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        ClanHall clanHall = this.getClanHall();
        Clan clan = clanHall.getOwner();
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        if (clan != null) {
            npcHtmlMessage.setFile("residence2/clanhall/messenger_yetti001.htm");
            npcHtmlMessage.replace("%owner_name%", clan.getName());
        } else {
            npcHtmlMessage.setFile("residence2/clanhall/messenger_yetti001a.htm");
        }
        npcHtmlMessage.replace("%siege_date%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }
}

