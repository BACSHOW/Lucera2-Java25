/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.StringTokenizer;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WyvernManagerInstance
extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(WyvernManagerInstance.class);

    public WyvernManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        NpcHtmlMessage npcHtmlMessage;
        if (!WyvernManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
        String string2 = stringTokenizer.nextToken();
        boolean bl = this.Y(player);
        if (string2.equalsIgnoreCase("RideHelp")) {
            npcHtmlMessage = new NpcHtmlMessage(player, this);
            npcHtmlMessage.setFile("wyvern/help_ride.htm");
            npcHtmlMessage.replace("%npcname%", "Wyvern Manager " + this.getName());
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            player.sendActionFailed();
        }
        if (bl) {
            if (string2.equalsIgnoreCase("RideWyvern") && player.isClanLeader()) {
                if (!player.isRiding() || !PetDataTable.isStrider(player.getMountNpcId())) {
                    npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("wyvern/not_ready.htm");
                    npcHtmlMessage.replace("%npcname%", "Wyvern Manager " + this.getName());
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                } else if (player.getInventory().getItemByItemId(1460) == null || player.getInventory().getItemByItemId(1460).getCount() < 10L) {
                    npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("wyvern/havenot_cry.htm");
                    npcHtmlMessage.replace("%npcname%", "Wyvern Manager " + this.getName());
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                } else if (SevenSigns.getInstance().getCurrentPeriod() == 3 && SevenSigns.getInstance().getCabalHighestScore() == 3) {
                    npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("wyvern/no_ride_dusk.htm");
                    npcHtmlMessage.replace("%npcname%", "Wyvern Manager " + this.getName());
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                } else if (player.getInventory().destroyItemByItemId(1460, 10L)) {
                    player.setMount(12621, player.getMountObjId(), player.getMountLevel());
                    npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("wyvern/after_ride.htm");
                    npcHtmlMessage.replace("%npcname%", "Wyvern Manager " + this.getName());
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                }
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        if (!this.Y(player)) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
            npcHtmlMessage.setFile("wyvern/lord_only.htm");
            npcHtmlMessage.replace("%npcname%", "Wyvern Manager " + this.getName());
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            player.sendActionFailed();
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile("wyvern/lord_here.htm");
        npcHtmlMessage.replace("%Char_name%", String.valueOf(player.getName()));
        npcHtmlMessage.replace("%npcname%", "Wyvern Manager " + this.getName());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        player.sendActionFailed();
    }

    private boolean Y(Player player) {
        Residence residence = this.getCastle();
        if (residence != null && residence.getId() > 0 && player.getClan() != null && residence.getOwnerId() == player.getClanId() && player.isClanLeader()) {
            return true;
        }
        residence = this.getClanHall();
        return residence != null && residence.getId() > 0 && player.getClan() != null && residence.getOwnerId() == player.getClanId() && player.isClanLeader();
    }
}

