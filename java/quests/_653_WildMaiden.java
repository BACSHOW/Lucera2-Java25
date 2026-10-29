/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.ArrayList;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.spawn.PeriodOfDay;

public class _653_WildMaiden
extends Quest
implements ScriptFile {
    public final int SUKI = 32013;
    public final int GALIBREDO = 30181;
    public final int SOE = 736;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _653_WildMaiden() {
        super(0);
        this.addStartNpc(32013);
        this.addTalkId(32013);
        this.addTalkId(30181);
    }

    private NpcInstance c(int n, Player player) {
        NpcInstance npcInstance = null;
        ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
        for (Spawner eventOwner : SpawnManager.getInstance().getSpawners(PeriodOfDay.ALL.name())) {
            if (eventOwner.getCurrentNpcId() != n) continue;
            npcInstance = eventOwner.getLastSpawn();
            arrayList.add(npcInstance);
        }
        for (NpcInstance npcInstance2 : arrayList) {
            if (!player.isInRange(npcInstance2, 1600L)) continue;
            return npcInstance2;
        }
        return npcInstance;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        Player player = questState.getPlayer();
        if (string.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm")) {
            if (questState.getQuestItemsCount(736) > 0L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.takeItems(736, 1L);
                string2 = "spring_girl_sooki_q0653_04a.htm";
                NpcInstance npcInstance2 = this.c(32013, player);
                npcInstance2.broadcastPacket(new MagicSkillUse(npcInstance2, npcInstance2, 2013, 1, 20000, 0L));
                questState.startQuestTimer("suki_timer", 20000L);
            }
        } else if (string.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm")) {
            questState.exitCurrentQuest(false);
            questState.playSound("ItemSound.quest_giveup");
        } else if (string.equalsIgnoreCase("suki_timer")) {
            NpcInstance npcInstance3 = this.c(32013, player);
            npcInstance3.deleteMe();
            string2 = null;
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        if (n == 32013 && n2 == 1) {
            if (questState.getPlayer().getLevel() >= 36) {
                string = "spring_girl_sooki_q0653_01.htm";
            } else {
                string = "spring_girl_sooki_q0653_01a.htm";
                questState.exitCurrentQuest(false);
            }
        } else if (n == 30181 && questState.getCond() == 1) {
            string = "galicbredo_q0653_01.htm";
            questState.giveItems(57, 2553L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string;
    }
}

