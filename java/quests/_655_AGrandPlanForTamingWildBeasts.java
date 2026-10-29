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

public class _655_AGrandPlanForTamingWildBeasts
extends Quest
implements ScriptFile {
    private static final int bIP = 35627;
    private static final int bqF = 8084;
    private static final int bUi = 8293;

    public _655_AGrandPlanForTamingWildBeasts() {
        super(0);
        this.addStartNpc(35627);
        this.addTalkId(35627);
        this.addQuestItem(8084, 8293);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("farm_messenger_q0655_06.htm")) {
            questState.setCond(1);
            questState.setState(2);
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
        ClanHall clanHall = (ClanHall)ResidenceHolder.getInstance().getResidence(63);
        if (((SiegeEvent)clanHall.getSiegeEvent()).isRegistrationOver()) {
            string = null;
            this.showHtmlFile(player, "farm_messenger_q0655_02.htm", false, "%siege_time%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate()));
        } else if (clan == null || player.getObjectId() != clan.getLeaderId()) {
            string = "farm_messenger_q0655_03.htm";
        } else if (player.getObjectId() == clan.getLeaderId() && clan.getLevel() < 4) {
            string = "farm_messenger_q0655_05.htm";
        } else if (((SiegeEvent)clanHall.getSiegeEvent()).getSiegeClan("attackers", player.getClan()) != null) {
            string = "farm_messenger_q0655_07.htm";
        } else if (clan.getHasHideout() > 0) {
            string = "farm_messenger_q0655_04.htm";
        } else if (n == 0) {
            string = "farm_messenger_q0655_01.htm";
        } else if (n == 1 && questState.getQuestItemsCount(8084) < 10L) {
            string = "farm_messenger_q0655_08.htm";
        } else if (n == 1 && questState.getQuestItemsCount(8084) == 10L) {
            questState.setCond(-1);
            questState.takeItems(8084, -1L);
            questState.giveItems(8293, 1L);
            string = "farm_messenger_q0655_10.htm";
        } else if (questState.getQuestItemsCount(8293) == 1L) {
            string = "farm_messenger_q0655_09.htm";
        }
        return string;
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

