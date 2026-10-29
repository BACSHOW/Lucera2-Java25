/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.Arrays;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.CompetitionController;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.model.entity.oly.ParticipantPool;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExHeroList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadManagerInstance
extends NpcInstance {
    private static Logger _log = LoggerFactory.getLogger(OlympiadManagerInstance.class);
    private static final int UM = 31688;
    private static final int[] UN = new int[]{31690, 31769, 31770, 31771, 31772};
    private static final int UO = 6842;
    private static final int[] UP;

    public OlympiadManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    private Player[] T(Player player) {
        Player[] playerArray = new Player[3];
        if (player.getParty() == null) {
            player.sendMessage(new CustomMessage("THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET_TO_PARTICIPATE_IN_A_TEAM", player, new Object[0]));
            return null;
        }
        if (!player.getParty().isLeader(player)) {
            player.sendMessage(new CustomMessage("ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH", player, new Object[0]));
            return null;
        }
        if (!this.a(player, CompetitionType.TEAM_CLASS_FREE)) {
            return null;
        }
        playerArray[0] = player;
        int n = 0;
        for (Player player2 : player.getParty().getPartyMembers()) {
            if (!this.a(player2, CompetitionType.TEAM_CLASS_FREE)) {
                return null;
            }
            if (player2 == player) continue;
            if (++n < 3) {
                playerArray[n] = player2;
                continue;
            }
            player.sendMessage(new CustomMessage("THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET_TO_PARTICIPATE_IN_A_TEAM", player, new Object[0]));
            return null;
        }
        return playerArray;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!OlympiadManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (!Config.OLY_ENABLED) {
            return;
        }
        SystemMessage systemMessage = null;
        if (string.startsWith("oly hwpn_")) {
            boolean bl = false;
            int n = Integer.parseInt(string.substring(9));
            int n2 = 0;
            for (int n3 : HeroController.HERO_WEAPONS) {
                if (player.getInventory().getItemByItemId(n3) != null || player.getWarehouse().getCountOf(n3) > 0L) {
                    bl = true;
                }
                if (n3 != n) continue;
                n2 = n3;
            }
            if (bl) {
                this.showChatWindow(player, 51, new Object[0]);
            }
            if (!player.isHero() || n2 <= 0) return;
            player.getInventory().addItem(n2, 1L);
            player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(n2, 1L, 0));
            return;
        }
        if (string.startsWith("oly ")) {
            int n = Integer.parseInt(string.substring(4));
            if (this.getNpcId() == 31688) {
                switch (n) {
                    case 100: {
                        if (OlyController.getInstance().isRegAllowed()) {
                            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                            if (!ParticipantPool.getInstance().isRegistred(player) && !player.isOlyParticipant()) {
                                npcHtmlMessage.setFile("oly/olympiad_operator100.htm");
                                npcHtmlMessage.replace("%period%", String.valueOf(OlyController.getInstance().getCurrentPeriod()));
                                npcHtmlMessage.replace("%season%", String.valueOf(OlyController.getInstance().getCurrentSeason()));
                                npcHtmlMessage.replace("%particicnt%", String.valueOf(OlyController.getInstance().getPartCount()));
                                npcHtmlMessage.replace("%currpartcnt%", String.valueOf(ParticipantPool.getInstance().getParticipantCount()));
                            } else {
                                npcHtmlMessage.setFile("oly/olympiad_operator110.htm");
                            }
                            player.sendPacket((IStaticPacket)npcHtmlMessage);
                            return;
                        }
                        player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    case 101: {
                        if (OlyController.getInstance().isRegAllowed()) {
                            if (!this.a(player, CompetitionType.CLASS_INDIVIDUAL)) return;
                            player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                            systemMessage = CompetitionController.getInstance().AddParticipationRequest(CompetitionType.CLASS_INDIVIDUAL, new Player[]{player});
                            if (systemMessage == null) return;
                            player.sendPacket((IStaticPacket)systemMessage);
                            return;
                        }
                        player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    case 10001: {
                        if (OlyController.getInstance().isRegAllowed()) {
                            if (!this.a(player, CompetitionType.CLASS_TYPE_INDIVIDUAL)) return;
                            player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                            systemMessage = CompetitionController.getInstance().AddParticipationRequest(CompetitionType.CLASS_TYPE_INDIVIDUAL, new Player[]{player});
                            if (systemMessage == null) return;
                            player.sendPacket((IStaticPacket)systemMessage);
                            return;
                        }
                        player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    case 102: {
                        if (OlyController.getInstance().isRegAllowed()) {
                            if (!this.a(player, CompetitionType.CLASS_FREE) || (systemMessage = CompetitionController.getInstance().AddParticipationRequest(CompetitionType.CLASS_FREE, new Player[]{player})) == null) return;
                            player.sendPacket((IStaticPacket)systemMessage);
                            return;
                        }
                        player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    case 103: {
                        if (OlyController.getInstance().isRegAllowed()) {
                            Player[] playerArray = this.T(player);
                            if (playerArray == null || (systemMessage = CompetitionController.getInstance().AddParticipationRequest(CompetitionType.TEAM_CLASS_FREE, playerArray)) == null) return;
                            player.sendPacket((IStaticPacket)systemMessage);
                            return;
                        }
                        player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    case 104: {
                        if (OlyController.getInstance().isRegAllowed()) {
                            CompetitionType competitionType = ParticipantPool.getInstance().getCompTypeOf(player);
                            if (competitionType == null) return;
                            ParticipantPool.getInstance().removeEntryByPlayer(competitionType, player);
                            player.sendPacket((IStaticPacket)Msg.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
                            return;
                        }
                        player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    case 200: {
                        if (OlyController.getInstance().isRegAllowed()) {
                            CompetitionController.getInstance().showCompetitionList(player);
                            return;
                        }
                        player.sendPacket((IStaticPacket)Msg.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    case 301: {
                        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                        int n4 = NoblesController.getInstance().getNoblessePasses(player);
                        if (n4 > 0) {
                            npcHtmlMessage.setFile(this.getHtmlPath(this.getNpcId(), 301, player));
                            npcHtmlMessage.replace("%points%", String.valueOf(n4));
                            player.getInventory().addItem(Config.OLY_HERO_REWARD_RITEMID, n4);
                            player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(Config.OLY_HERO_REWARD_RITEMID, n4, 0));
                        } else {
                            npcHtmlMessage.setFile(this.getHtmlPath(this.getNpcId(), 302, player));
                        }
                        player.sendPacket((IStaticPacket)npcHtmlMessage);
                        return;
                    }
                    case 1902: 
                    case 1903: {
                        MultiSellHolder.getInstance().SeparateAndSend(n, player, 0.0);
                        return;
                    }
                    default: {
                        if (n >= 588 && n <= 634) {
                            int n5 = n - 500;
                            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                            npcHtmlMessage.setFile("oly/olympiad_operator_rank_class.htm");
                            String[] stringArray = Config.OLY_STATISTIC_PAST_PERIOD ? NoblesController.getInstance().getClassLeaders(n5) : NoblesController.getInstance().getClassLeadersCurr(n5);
                            String string2 = "";
                            String string3 = "";
                            for (int i = 0; i < 15; ++i) {
                                if (i < stringArray.length) {
                                    string2 = stringArray[i];
                                    string3 = String.valueOf(i + 1);
                                } else {
                                    string2 = "";
                                    string3 = "";
                                }
                                npcHtmlMessage.replace("<?Rank" + (i + 1) + "?>", string3);
                                npcHtmlMessage.replace("<?Name" + (i + 1) + "?>", string2);
                            }
                            player.sendPacket((IStaticPacket)npcHtmlMessage);
                            return;
                        }
                        this.showChatWindow(player, n, new Object[0]);
                        return;
                    }
                }
            } else {
                if (Arrays.binarySearch(UN, this.getNpcId()) < 0) return;
                if (n == 1000) {
                    player.sendPacket((IStaticPacket)new ExHeroList());
                    return;
                } else if (n == 2000) {
                    if (OlyController.getInstance().isCalculationPeriod()) {
                        this.showChatWindow(player, 11, new Object[0]);
                        return;
                    } else if (HeroController.getInstance().isInactiveHero(player)) {
                        HeroController.getInstance().activateHero(player);
                        this.showChatWindow(player, 10, new Object[0]);
                        return;
                    } else {
                        this.showChatWindow(player, 1, new Object[0]);
                    }
                    return;
                } else if (n == 3) {
                    if (player.isHero()) {
                        if (player.getInventory().getItemByItemId(6842) != null || player.getWarehouse().getCountOf(6842) > 0L) {
                            this.showChatWindow(player, 55, new Object[0]);
                            return;
                        } else {
                            player.getInventory().addItem(6842, 1L);
                            player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(6842, 1L, 0));
                        }
                        return;
                    } else {
                        this.showChatWindow(player, 3, new Object[0]);
                    }
                    return;
                } else if (n == 4) {
                    if (player.isHero()) {
                        boolean bl = false;
                        for (int n6 : HeroController.HERO_WEAPONS) {
                            if (player.getInventory().getItemByItemId(n6) == null && player.getWarehouse().getCountOf(n6) <= 0L) continue;
                            bl = true;
                        }
                        this.showChatWindow(player, bl ? 51 : 50, new Object[0]);
                        return;
                    } else {
                        this.showChatWindow(player, 4, new Object[0]);
                    }
                    return;
                } else if (n == 5) {
                    if (player.isNoble()) {
                        int n7 = NoblesController.getInstance().getPlayerClassRank(player.getBaseClassId(), player.getObjectId());
                        if (n7 < 0 || n7 >= UP.length) {
                            this.showChatWindow(player, 5, new Object[0]);
                            return;
                        } else {
                            int n8 = UP[n7];
                            if (player.getInventory().getItemByItemId(n8) != null || player.getWarehouse().getCountOf(n8) > 0L) {
                                this.showChatWindow(player, 5, new Object[0]);
                                return;
                            } else {
                                player.getInventory().addItem(n8, 1L);
                                player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(n8, 1L, 0));
                            }
                        }
                        return;
                    } else {
                        this.showChatWindow(player, 5, new Object[0]);
                    }
                    return;
                } else {
                    this.showChatWindow(player, n, new Object[0]);
                }
            }
            return;
        }
        super.onBypassFeedback(player, string);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        if (!(n != 0 && n != 100 || player.isNoble())) {
            super.showChatWindow(player, 900, new Object[0]);
        } else {
            super.showChatWindow(player, n, new Object[0]);
        }
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        return String.format(this.getNpcId() == 31688 ? "oly/olympiad_operator%03d.htm" : "oly/olympiad_monument%03d.htm", n2);
    }

    private boolean a(Player player, CompetitionType competitionType) {
        if (player == null) {
            return false;
        }
        NoblesController.NobleRecord nobleRecord = NoblesController.getInstance().getNobleRecord(player.getObjectId());
        if (nobleRecord.class_based_cnt + nobleRecord.class_free_cnt + nobleRecord.team_cnt > Config.OLY_MAX_TOTAL_MATCHES) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_70);
            return false;
        }
        switch (competitionType) {
            case CLASS_FREE: {
                if (nobleRecord.class_free_cnt > Config.OLY_CF_MATCHES) {
                    player.sendPacket((IStaticPacket)SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                if (!Config.OLY_PAID_REGISTRATION_FOR_CLASS_FREE) break;
                if (ItemFunctions.getItemCount(player, Config.OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_ID) >= (long)Config.OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_COUNT) {
                    ItemFunctions.removeItem(player, Config.OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_ID, Config.OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_COUNT, true);
                    break;
                }
                player.sendMessage(new CustomMessage("oly.class_free_need_pay", player, new Object[0]).addItemName(Config.OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_ID).addNumber(Config.OLY_PAID_REGISTRATION_FOR_CLASS_FREE_ITEM_COUNT));
                return false;
            }
            case CLASS_INDIVIDUAL: {
                if (nobleRecord.class_based_cnt > Config.OLY_CB_MATCHES) {
                    player.sendPacket((IStaticPacket)SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                if (!Config.OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL) break;
                if (ItemFunctions.getItemCount(player, Config.OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_ID) >= (long)Config.OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_COUNT) {
                    ItemFunctions.removeItem(player, Config.OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_ID, Config.OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_COUNT, true);
                    break;
                }
                player.sendMessage(new CustomMessage("oly.class_based_need_pay", player, new Object[0]).addItemName(Config.OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_ID).addNumber(Config.OLY_PAID_REGISTRATION_FOR_CLASS_INDIVIDUAL_ITEM_COUNT));
                return false;
            }
            case TEAM_CLASS_FREE: {
                if (nobleRecord.team_cnt > Config.OLY_TB_MATCHES) {
                    player.sendPacket((IStaticPacket)SystemMsg.THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES);
                    return false;
                }
                if (!Config.OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE) break;
                if (ItemFunctions.getItemCount(player, Config.OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_ID) >= (long)Config.OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_COUNT) {
                    ItemFunctions.removeItem(player, Config.OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_ID, Config.OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_COUNT, true);
                    break;
                }
                player.sendMessage(new CustomMessage("oly.team_free_need_pay", player, new Object[0]).addItemName(Config.OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_ID).addNumber(Config.OLY_PAID_REGISTRATION_FOR_TEAM_CLASS_FREE_ITEM_COUNT));
                return false;
            }
        }
        return true;
    }

    static {
        Arrays.sort(UN);
        UP = new int[]{31274, 31275, 31275};
    }
}

