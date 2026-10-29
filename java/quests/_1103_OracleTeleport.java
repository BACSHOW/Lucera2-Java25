/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;

public class _1103_OracleTeleport
extends Quest
implements ScriptFile {
    private static final int aEw = 5901;
    private static final Location aEx = new Location(-80157, 111344, -4901);
    private static final Location aEy = new Location(-81261, 86531, -5157);

    public _1103_OracleTeleport() {
        super(0);
        int n;
        for (n = 31078; n < 31092; ++n) {
            this.addStartNpc(n);
        }
        for (n = 31168; n <= 31170; ++n) {
            this.addStartNpc(n);
        }
        for (n = 31692; n <= 31696; ++n) {
            this.addStartNpc(n);
        }
        for (n = 31997; n <= 31999; ++n) {
            this.addStartNpc(n);
        }
        for (n = 31127; n <= 31142; ++n) {
            this.addStartNpc(n);
        }
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        Player player = questState.getPlayer();
        switch (n) {
            case 31078: 
            case 31079: 
            case 31080: 
            case 31081: 
            case 31082: 
            case 31083: 
            case 31084: 
            case 31168: 
            case 31692: 
            case 31694: 
            case 31997: {
                player.setVar("FestivalBackCoords", player.getX() + "," + player.getY() + "," + player.getZ(), -1L);
                player.teleToLocation(aEx);
                break;
            }
            case 31085: 
            case 31086: 
            case 31087: 
            case 31088: 
            case 31089: 
            case 31090: 
            case 31091: 
            case 31169: 
            case 31693: 
            case 31695: 
            case 31998: {
                player.setVar("FestivalBackCoords", player.getX() + "," + player.getY() + "," + player.getZ(), -1L);
                player.teleToLocation(aEy);
                break;
            }
            default: {
                String string = player.getVar("FestivalBackCoords");
                if (string == null) {
                    return "ssq_npc_priest_q507_03.htm";
                }
                if (questState.getQuestItemsCount(5901) > 0L) {
                    questState.takeItems(5901, questState.getQuestItemsCount(5901));
                }
                player.unsetVar("FestivalBackCoords");
                String[] stringArray = string.split(",");
                int n2 = Integer.parseInt(stringArray[0]);
                int n3 = Integer.parseInt(stringArray[1]);
                int n4 = Integer.parseInt(stringArray[2]);
                player.teleToLocation(n2, n3, n4);
            }
        }
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

