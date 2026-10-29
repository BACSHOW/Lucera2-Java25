/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class AdminMammon
implements IAdminCommandHandler {
    List<Integer> npcIds = new ArrayList<Integer>();

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        this.npcIds.clear();
        if (!player.getPlayerAccess().Menu) {
            return false;
        }
        if (string.startsWith("admin_find_mammon")) {
            this.npcIds.add(31113);
            this.npcIds.add(31126);
            this.npcIds.add(31092);
            int n = -1;
            try {
                if (string.length() > 16) {
                    n = Integer.parseInt(string.substring(18));
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.findAdminNPCs(player, this.npcIds, n, -1);
        } else if (string.equals("admin_show_mammon")) {
            this.npcIds.add(31113);
            this.npcIds.add(31126);
            this.findAdminNPCs(player, this.npcIds, -1, 1);
        } else if (string.equals("admin_hide_mammon")) {
            this.npcIds.add(31113);
            this.npcIds.add(31126);
            this.findAdminNPCs(player, this.npcIds, -1, 0);
        } else if (string.startsWith("admin_list_spawns")) {
            int n = 0;
            try {
                n = Integer.parseInt(string.substring(18).trim());
            }
            catch (Exception exception) {
                player.sendMessage("Command format is //list_spawns <NPC_ID>");
            }
            this.npcIds.add(n);
            this.findAdminNPCs(player, this.npcIds, -1, -1);
        } else if (string.startsWith("admin_msg")) {
            player.sendPacket((IStaticPacket)new SystemMessage(Integer.parseInt(string.substring(10).trim())));
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    public void findAdminNPCs(Player player, List<Integer> list, int n, int n2) {
        int n3 = 0;
        for (NpcInstance npcInstance : GameObjectsStorage.getAllNpcsForIterate()) {
            int n4 = npcInstance.getNpcId();
            if (!list.contains(n4)) continue;
            if (n2 == 1) {
                npcInstance.spawnMe();
            } else if (n2 == 0) {
                npcInstance.decayMe();
            }
            if (!npcInstance.isVisible()) continue;
            ++n3;
            if (n > -1) {
                if (n != n3) continue;
                player.teleToLocation(npcInstance.getLoc());
                continue;
            }
            player.sendMessage(n3 + " - " + npcInstance.getName() + " (" + npcInstance.getObjectId() + "): " + npcInstance.getX() + " " + npcInstance.getY() + " " + npcInstance.getZ());
        }
    }

    private static enum Commands {
        admin_find_mammon,
        admin_show_mammon,
        admin_hide_mammon,
        admin_list_spawns;

    }
}

