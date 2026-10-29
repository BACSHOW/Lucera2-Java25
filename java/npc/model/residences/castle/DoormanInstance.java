/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.ReflectionUtils;

public class DoormanInstance
extends npc.model.residences.DoormanInstance {
    private Location[] ayz = new Location[2];

    public DoormanInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        for (int i = 0; i < this.ayz.length; ++i) {
            String string = npcTemplate.getAIParams().getString("tele_loc" + i, null);
            if (string == null) continue;
            this.ayz[i] = Location.parseLoc(string);
        }
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!DoormanInstance.canBypassCheck(player, this)) {
            return;
        }
        int n = this.getCond(player);
        switch (n) {
            case 0: {
                int n2;
                Location location;
                if (string.equalsIgnoreCase("openDoors")) {
                    for (int n3 : this._doors) {
                        ReflectionUtils.getDoor(n3).openMe(player, true);
                    }
                    break;
                }
                if (string.equalsIgnoreCase("closeDoors")) {
                    for (int n4 : this._doors) {
                        ReflectionUtils.getDoor(n4).closeMe(player, true);
                    }
                    break;
                }
                if (!string.startsWith("tele") || (location = this.ayz[n2 = Integer.parseInt(string.substring(4, 5))]) == null) break;
                player.teleToLocation(location);
                break;
            }
            case 1: {
                if (string.startsWith("tele")) {
                    int n5 = Integer.parseInt(string.substring(4, 5));
                    Location location = this.ayz[n5];
                    if (location == null) break;
                    player.teleToLocation(location);
                    break;
                }
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, this._siegeDialog, 0));
                break;
            }
            case 2: {
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, this._failDialog, 0));
            }
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        String string = null;
        int n2 = this.getCond(player);
        switch (n2) {
            case 0: 
            case 1: {
                string = this._mainDialog;
                break;
            }
            case 2: {
                string = this._failDialog;
            }
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, string, n));
    }

    @Override
    protected int getCond(Player player) {
        Castle castle = this.getCastle();
        Clan clan = castle.getOwner();
        if (clan != null && player.getClan() == clan && (player.getClanPrivileges() & this.getOpenPriv()) == this.getOpenPriv()) {
            if (((SiegeEvent)castle.getSiegeEvent()).isInProgress()) {
                return 1;
            }
            return 0;
        }
        return 2;
    }

    @Override
    public int getOpenPriv() {
        return 32768;
    }

    @Override
    public Residence getResidence() {
        return this.getCastle();
    }

    @Override
    protected boolean canInteractWithKarmaPlayer() {
        return true;
    }
}

