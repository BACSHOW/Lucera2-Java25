/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import java.util.List;
import java.util.StringTokenizer;
import l2.commons.collections.CollectionUtils;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.dao.SiegePlayerDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.TimeUtils;
import quests._504_CompetitionForTheBanditStronghold;

public class BanditMessagerInstance
extends NpcInstance {
    public BanditMessagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        ClanHall clanHall = this.getClanHall();
        ClanHallTeamBattleEvent clanHallTeamBattleEvent = (ClanHallTeamBattleEvent)clanHall.getSiegeEvent();
        Clan clan = player.getClan();
        if (string.equalsIgnoreCase("registrationMenu")) {
            if (!this.g(player, true)) {
                return;
            }
            this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_1.htm", new Object[0]);
        } else if (string.equalsIgnoreCase("registerAsClan")) {
            if (!this.g(player, false)) {
                return;
            }
            List list = clanHallTeamBattleEvent.getObjects("attackers");
            CTBSiegeClanObject cTBSiegeClanObject = (CTBSiegeClanObject)clanHallTeamBattleEvent.getSiegeClan("attackers", clan);
            if (cTBSiegeClanObject != null) {
                this.l(player, list.indexOf(cTBSiegeClanObject));
                return;
            }
            QuestState questState = player.getQuestState(_504_CompetitionForTheBanditStronghold.class);
            if (questState == null || questState.getQuestItemsCount(5009) != 1L) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_24.htm", new Object[0]);
                return;
            }
            questState.exitCurrentQuest(true);
            this.am(player);
        } else if (string.equalsIgnoreCase("registerByOffer")) {
            if (!this.g(player, false)) {
                return;
            }
            List list = clanHallTeamBattleEvent.getObjects("attackers");
            CTBSiegeClanObject cTBSiegeClanObject = (CTBSiegeClanObject)clanHallTeamBattleEvent.getSiegeClan("attackers", clan);
            if (cTBSiegeClanObject != null) {
                this.l(player, list.indexOf(cTBSiegeClanObject));
                return;
            }
            if (!player.consumeItem(57, 200000L)) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_26.htm", new Object[0]);
                return;
            }
            this.am(player);
        } else if (string.equalsIgnoreCase("viewNpc")) {
            String string2;
            CTBSiegeClanObject cTBSiegeClanObject = (CTBSiegeClanObject)clanHallTeamBattleEvent.getSiegeClan("attackers", player.getClan());
            if (cTBSiegeClanObject == null) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_7.htm", new Object[0]);
                return;
            }
            switch ((int)cTBSiegeClanObject.getParam()) {
                case 0: {
                    string2 = "residence2/clanhall/agit_oel_mahum_messeger_10.htm";
                    break;
                }
                case 35428: {
                    string2 = "residence2/clanhall/agit_oel_mahum_messeger_11.htm";
                    break;
                }
                case 35429: {
                    string2 = "residence2/clanhall/agit_oel_mahum_messeger_12.htm";
                    break;
                }
                case 35430: {
                    string2 = "residence2/clanhall/agit_oel_mahum_messeger_13.htm";
                    break;
                }
                case 35431: {
                    string2 = "residence2/clanhall/agit_oel_mahum_messeger_14.htm";
                    break;
                }
                case 35432: {
                    string2 = "residence2/clanhall/agit_oel_mahum_messeger_15.htm";
                    break;
                }
                default: {
                    return;
                }
            }
            this.showChatWindow(player, string2, new Object[0]);
        } else if (string.startsWith("formAlliance")) {
            CTBSiegeClanObject cTBSiegeClanObject = (CTBSiegeClanObject)clanHallTeamBattleEvent.getSiegeClan("attackers", player.getClan());
            if (cTBSiegeClanObject == null) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_7.htm", new Object[0]);
                return;
            }
            if (cTBSiegeClanObject.getClan().getLeaderId() != player.getObjectId()) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_10.htm", new Object[0]);
                return;
            }
            StringTokenizer stringTokenizer = new StringTokenizer(string);
            stringTokenizer.nextToken();
            int n = Integer.parseInt(stringTokenizer.nextToken());
            cTBSiegeClanObject.setParam(n);
            SiegeClanDAO.getInstance().update(clanHall, cTBSiegeClanObject);
            this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_9.htm", new Object[0]);
        } else if (string.equalsIgnoreCase("registerAsMember")) {
            CTBSiegeClanObject cTBSiegeClanObject = (CTBSiegeClanObject)clanHallTeamBattleEvent.getSiegeClan("attackers", player.getClan());
            if (cTBSiegeClanObject == null) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_7.htm", new Object[0]);
                return;
            }
            if (cTBSiegeClanObject.getClan().getLeaderId() == player.getObjectId()) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_5.htm", new Object[0]);
                return;
            }
            if (cTBSiegeClanObject.getPlayers().contains(player.getObjectId())) {
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_9.htm", new Object[0]);
            } else {
                if (cTBSiegeClanObject.getPlayers().size() >= 18) {
                    this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_8.htm", new Object[0]);
                    return;
                }
                cTBSiegeClanObject.getPlayers().add(player.getObjectId());
                SiegePlayerDAO.getInstance().insert(clanHall, clan.getClanId(), player.getObjectId());
                this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_9.htm", new Object[0]);
            }
        } else if (string.equalsIgnoreCase("listClans")) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
            npcHtmlMessage.setFile("residence2/clanhall/azit_messenger003.htm");
            List list = clanHallTeamBattleEvent.getObjects("attackers");
            for (int i = 0; i < 5; ++i) {
                CTBSiegeClanObject cTBSiegeClanObject = (CTBSiegeClanObject)CollectionUtils.safeGet(list, i);
                if (cTBSiegeClanObject != null) {
                    npcHtmlMessage.replace("%clan_" + i + "%", cTBSiegeClanObject.getClan().getName());
                } else {
                    npcHtmlMessage.replaceNpcString("%clan_" + i + "%", NpcString.__UNREGISTERED__, new Object[0]);
                }
                npcHtmlMessage.replace("%clan_count_" + i + "%", cTBSiegeClanObject == null ? "" : String.valueOf(cTBSiegeClanObject.getPlayers().size()));
            }
            player.sendPacket((IStaticPacket)npcHtmlMessage);
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    private boolean g(Player player, boolean bl) {
        Clan clan = player.getClan();
        ClanHall clanHall = this.getClanHall();
        ClanHallTeamBattleEvent clanHallTeamBattleEvent = (ClanHallTeamBattleEvent)clanHall.getSiegeEvent();
        List list = clanHallTeamBattleEvent.getObjects("attackers");
        Object s = clanHallTeamBattleEvent.getSiegeClan("attackers", clan);
        if (clanHallTeamBattleEvent.isRegistrationOver()) {
            this.showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_03.htm", "%siege_time%", TimeUtils.toSimpleFormat(clanHall.getSiegeDate()));
            return false;
        }
        if (bl && s != null) {
            return true;
        }
        if (clan == null || player.getObjectId() != clan.getLeaderId()) {
            this.showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_05.htm", new Object[0]);
            return false;
        }
        if (player.getClan().isPlacedForDisband()) {
            player.sendPacket((IStaticPacket)SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
            return false;
        }
        if (player.getObjectId() == clan.getLeaderId() && clan.getLevel() < 4) {
            this.showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_04.htm", new Object[0]);
            return false;
        }
        if (clan.getHasHideout() == clanHall.getId()) {
            this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_22.htm", new Object[0]);
            return false;
        }
        if (clan.getHasHideout() > 0) {
            this.showChatWindow(player, "quests/_504_CompetitionForTheBanditStronghold/azit_messenger_q0504_10.htm", new Object[0]);
            return false;
        }
        if (list.size() >= 5) {
            this.showChatWindow(player, "residence2/clanhall/agit_oel_mahum_messeger_21.htm", new Object[0]);
            return false;
        }
        return true;
    }

    private void am(Player player) {
        Clan clan = player.getClan();
        ClanHall clanHall = this.getClanHall();
        ClanHallTeamBattleEvent clanHallTeamBattleEvent = (ClanHallTeamBattleEvent)clanHall.getSiegeEvent();
        CTBSiegeClanObject cTBSiegeClanObject = new CTBSiegeClanObject("attackers", clan, 0L);
        cTBSiegeClanObject.getPlayers().add(player.getObjectId());
        clanHallTeamBattleEvent.addObject("attackers", cTBSiegeClanObject);
        SiegeClanDAO.getInstance().insert(clanHall, cTBSiegeClanObject);
        SiegePlayerDAO.getInstance().insert(clanHall, clan.getClanId(), player.getObjectId());
        List list = clanHallTeamBattleEvent.getObjects("attackers");
        this.l(player, list.indexOf(cTBSiegeClanObject));
    }

    private void l(Player player, int n) {
        String string = null;
        switch (n) {
            case 0: {
                string = "residence2/clanhall/agit_oel_mahum_messeger_4a.htm";
                break;
            }
            case 1: {
                string = "residence2/clanhall/agit_oel_mahum_messeger_4b.htm";
                break;
            }
            case 2: {
                string = "residence2/clanhall/agit_oel_mahum_messeger_4c.htm";
                break;
            }
            case 3: {
                string = "residence2/clanhall/agit_oel_mahum_messeger_4d.htm";
                break;
            }
            case 4: {
                string = "residence2/clanhall/agit_oel_mahum_messeger_4e.htm";
                break;
            }
            default: {
                return;
            }
        }
        this.showChatWindow(player, string, new Object[0]);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        Clan clan = this.getClanHall().getOwner();
        if (clan != null) {
            this.showChatWindow(player, "residence2/clanhall/azit_messenger001.htm", "%owner_name%", clan.getName());
        } else {
            this.showChatWindow(player, "residence2/clanhall/azit_messenger002.htm", new Object[0]);
        }
    }
}

