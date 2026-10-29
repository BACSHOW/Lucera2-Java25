/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.TimeUtils;

public class _504_CompetitionForTheBanditStronghold
extends Quest
implements ScriptFile {
    private static final int bIP = 35437;
    private static final int bIQ = 20570;
    private static final int bIR = 20571;
    private static final int bIS = 20572;
    private static final int bIT = 20573;
    private static final int bIU = 20574;
    private static final int bIV = 4332;
    private static final int bIW = 5009;
    private static final int bIX = 4333;

    public _504_CompetitionForTheBanditStronghold() {
        super(2);
        this.addStartNpc(35437);
        this.addTalkId(35437);
        this.addKillId(20570);
        this.addKillId(20571);
        this.addKillId(20572);
        this.addKillId(20573);
        this.addKillId(20574);
        this.addQuestItem(4333, 4332, 5009);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("azit_messenger_q0504_02.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.giveItems(4333, 1L);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        Player player = questState.getPlayer();
        Clan clan = player.getClan();
        ClanHall clanHall = (ClanHall)ResidenceHolder.getInstance().getResidence(35);
        if (((SiegeEvent)clanHall.getSiegeEvent()).isRegistrationOver()) {
            string = null;
            this.showHtmlFile(player, "azit_messenger_q0504_03.htm", false, "%siege_time%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate()));
        } else if (clan == null || player.getObjectId() != clan.getLeaderId()) {
            string = "azit_messenger_q0504_05.htm";
        } else if (player.getObjectId() == clan.getLeaderId() && clan.getLevel() < 4) {
            string = "azit_messenger_q0504_04.htm";
        } else if (((SiegeEvent)clanHall.getSiegeEvent()).getSiegeClan("attackers", player.getClan()) != null) {
            string = "azit_messenger_q0504_06.htm";
        } else if (clan.getHasHideout() > 0) {
            string = "azit_messenger_q0504_10.htm";
        } else if (n == 0) {
            string = "azit_messenger_q0504_01.htm";
        } else if (questState.getQuestItemsCount(4333) == 1L && questState.getQuestItemsCount(4332) < 30L) {
            string = "azit_messenger_q0504_07.htm";
        } else if (questState.getQuestItemsCount(5009) >= 1L) {
            string = "azit_messenger_q0504_07a.htm";
        } else if (questState.getQuestItemsCount(4333) == 1L && questState.getQuestItemsCount(4332) == 30L) {
            questState.takeItems(4332, -1L);
            questState.takeItems(4333, -1L);
            questState.giveItems(5009, 1L);
            questState.playSound("ItemSound.quest_finish");
            questState.setCond(-1);
            string = "azit_messenger_q0504_08.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(4332) < 30L) {
            questState.giveItems(4332, 1L);
            questState.playSound("ItemSound.quest_itemget");
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

