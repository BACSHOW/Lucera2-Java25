/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.Privilege;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;

public class AuctionedDoormanInstance
extends NpcInstance {
    private int[] _doors;
    private boolean ayA;

    public AuctionedDoormanInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this._doors = npcTemplate.getAIParams().getIntegerArray("doors", ArrayUtils.EMPTY_INT_ARRAY);
        this.ayA = npcTemplate.getAIParams().getBool("elite", false);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!AuctionedDoormanInstance.canBypassCheck(player, this)) {
            return;
        }
        ClanHall clanHall = this.getClanHall();
        if (string.equalsIgnoreCase("openDoors")) {
            if (player.hasPrivilege(Privilege.CH_ENTER_EXIT) && player.getClan().getHasHideout() == clanHall.getId()) {
                for (int n : this._doors) {
                    ReflectionUtils.getDoor(n).openMe();
                }
                this.showChatWindow(player, "residence2/clanhall/agitafterdooropen.htm", new Object[0]);
            } else {
                this.showChatWindow(player, "residence2/clanhall/noAuthority.htm", new Object[0]);
            }
        } else if (string.equalsIgnoreCase("closeDoors")) {
            if (player.hasPrivilege(Privilege.CH_ENTER_EXIT) && player.getClan().getHasHideout() == clanHall.getId()) {
                for (int n : this._doors) {
                    ReflectionUtils.getDoor(n).closeMe(player, true);
                }
                this.showChatWindow(player, "residence2/clanhall/agitafterdoorclose.htm", new Object[0]);
            } else {
                this.showChatWindow(player, "residence2/clanhall/noAuthority.htm", new Object[0]);
            }
        } else if (string.equalsIgnoreCase("banish")) {
            if (player.hasPrivilege(Privilege.CH_DISMISS)) {
                clanHall.banishForeigner();
                this.showChatWindow(player, "residence2/clanhall/agitafterbanish.htm", new Object[0]);
            } else {
                this.showChatWindow(player, "residence2/clanhall/noAuthority.htm", new Object[0]);
            }
        } else if (string.equalsIgnoreCase("Clan_Hall_RideWyvern") && player.isClanLeader() && this.ayA) {
            if (!player.isRiding() || !PetDataTable.isStrider(player.getMountNpcId())) {
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                npcHtmlMessage.setFile("residence2/clanhall/WyvernAgit_not_ready.htm");
                player.sendPacket((IStaticPacket)npcHtmlMessage);
            } else if (player.getInventory().getItemByItemId(1460) == null || player.getInventory().getItemByItemId(1460).getCount() < 10L) {
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                npcHtmlMessage.replace("%npcname%", this.getName());
                player.sendPacket((IStaticPacket)npcHtmlMessage);
            } else if (SevenSigns.getInstance().getCurrentPeriod() == 3 && SevenSigns.getInstance().getCabalHighestScore() == 3) {
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                npcHtmlMessage.setFile("residence2/clanhall/WyvernAgit_no_ride_dusk.htm");
                player.sendPacket((IStaticPacket)npcHtmlMessage);
            } else if (player.getInventory().destroyItemByItemId(1460, 10L)) {
                player.setMount(12621, player.getMountObjId(), player.getMountLevel());
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                npcHtmlMessage.setFile("residence2/clanhall/WyvernAgit_after_ride.htm");
                player.sendPacket((IStaticPacket)npcHtmlMessage);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        ClanHall clanHall = this.getClanHall();
        if (clanHall != null) {
            Clan clan = clanHall.getOwner();
            if (clan != null) {
                Clan clan2 = player.getClan();
                if (clan2 != null && clan2 == clan) {
                    this.showChatWindow(player, this.ayA ? "residence2/clanhall/WyvernAgitJanitorHi.htm" : "residence2/clanhall/AgitJanitorHi.htm", "%owner%", clan2.getName());
                } else {
                    NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setFile("residence2/clanhall/defaultAgitInfo.htm");
                    npcHtmlMessage.replace("<?my_owner_name?>", clan.getLeaderName());
                    npcHtmlMessage.replace("<?my_pledge_name?>", clan.getName());
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                }
            } else {
                this.showChatWindow(player, "residence2/clanhall/noAgitInfo.htm", new Object[0]);
            }
        }
    }

    @Override
    protected boolean canInteractWithKarmaPlayer() {
        return true;
    }
}

