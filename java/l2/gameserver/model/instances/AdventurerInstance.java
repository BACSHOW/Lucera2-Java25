/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowQuestInfo;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdventurerInstance
extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(AdventurerInstance.class);

    public AdventurerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!AdventurerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("npcfind_byid")) {
            try {
                int n = Integer.parseInt(string.substring(12).trim());
                switch (RaidBossSpawnManager.getInstance().getRaidBossStatusId(n)) {
                    case ALIVE: 
                    case DEAD: {
                        Spawner spawner = RaidBossSpawnManager.getInstance().getSpawnTable().get(n);
                        Location location = spawner.getCurrentSpawnRange().getRandomLoc(spawner.getReflection().getGeoIndex());
                        player.sendPacket(new RadarControl(2, 2, location), new RadarControl(0, 1, location));
                        break;
                    }
                    case UNDEFINED: {
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2AdventurerInstance.BossNotInGame", player, new Object[0]).addNumber(n));
                    }
                }
            }
            catch (NumberFormatException numberFormatException) {
                _log.warn("AdventurerInstance: Invalid Bypass to Server command parameter.");
            }
        } else if (string.startsWith("raidInfo")) {
            int n = Integer.parseInt(string.substring(9).trim());
            String string2 = "adventurer_guildsman/raid_info/info.htm";
            if (n != 0) {
                string2 = "adventurer_guildsman/raid_info/level" + n + ".htm";
            }
            this.showChatWindow(player, string2, new Object[0]);
        } else if (string.equalsIgnoreCase("questlist")) {
            player.sendPacket((IStaticPacket)ExShowQuestInfo.STATIC);
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        if (n >= 32082 && n <= 32089 || n == 32074) {
            return "adventurer_guildsman/adventure_manager001.htm";
        }
        String string = n2 == 0 ? "" + n : n + "-" + n2;
        return "adventurer_guildsman/" + string + ".htm";
    }
}

