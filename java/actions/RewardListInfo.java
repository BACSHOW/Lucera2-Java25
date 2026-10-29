/*
 * Decompiled with CFR 0.152.
 */
package actions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.StringHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardGroup;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.stats.Stats;
import l2.gameserver.utils.HtmlUtils;
import org.apache.commons.lang3.StringUtils;

public class RewardListInfo
extends Functions {
    private static final RewardType[] aT = new RewardType[]{RewardType.RATED_GROUPED, RewardType.SWEEP, RewardType.NOT_RATED_GROUPED, RewardType.NOT_RATED_NOT_GROUPED};
    private static final NumberFormat au = NumberFormat.getPercentInstance(Locale.ENGLISH);
    private static final NumberFormat aU = NumberFormat.getInstance(Locale.ENGLISH);

    private static boolean canBypassCheck(Player player, NpcInstance npcInstance) {
        if (npcInstance == null) {
            player.sendPacket((IStaticPacket)Msg.THAT_IS_THE_INCORRECT_TARGET);
            player.sendActionFailed();
            return false;
        }
        if (!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOVE_WHILE_SITTING);
            player.sendActionFailed();
            return false;
        }
        if (!npcInstance.isInRange(player, 2500L)) {
            player.sendPacket(SystemMsg.POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT, ActionFail.STATIC);
            player.sendActionFailed();
            return false;
        }
        return true;
    }

    public static void showRewardHtml(Player player, NpcInstance npcInstance) {
        RewardListInfo.showRewardHtml(player, npcInstance, 0);
    }

    public static void showRewardHtml(Player player, NpcInstance npcInstance, int n) {
        int n2;
        if (!RewardListInfo.canBypassCheck(player, npcInstance)) {
            return;
        }
        int n3 = npcInstance.calculateLevelDiffForDrop(player.isInParty() ? player.getParty().getLevel() : player.getLevel());
        double d = npcInstance.calcStat(Stats.ITEM_REWARD_MULTIPLIER, 1.0, player, null);
        d *= Experience.penaltyModifier(n3, 9.0);
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, npcInstance);
        npcHtmlMessage.replace("%npc_name%", HtmlUtils.htmlNpcName(npcInstance.getNpcId()));
        if (d <= 0.0 && !player.isGM()) {
            npcHtmlMessage.setFile("actions/rewardlist_to_weak.htm");
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return;
        }
        if (npcInstance instanceof ChestInstance && !player.isGM()) {
            npcHtmlMessage.setFile("actions/rewardlist_no_for_chest.htm");
            player.sendActionFailed();
            return;
        }
        if (npcInstance.getTemplate().getRewards().isEmpty()) {
            npcHtmlMessage.setFile("actions/rewardlist_empty.htm");
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return;
        }
        npcHtmlMessage.setFile("actions/rewardlist_info.htm");
        Map<RewardType, RewardList> map = npcInstance.getTemplate().getRewards();
        ArrayList<String> arrayList = new ArrayList<String>();
        block6: for (RewardType rewardType : aT) {
            RewardList rewardList = map.get((Object)rewardType);
            if (rewardList == null || rewardList.isEmpty()) continue;
            switch (rewardType) {
                case RATED_GROUPED: {
                    arrayList.add(StringHolder.getInstance().getNotNull(player, "drop.rated_grouped"));
                    RewardListInfo.ratedGroupedRewardList(arrayList, npcInstance, rewardList, player, d);
                    continue block6;
                }
                case NOT_RATED_GROUPED: {
                    arrayList.add(StringHolder.getInstance().getNotNull(player, "drop.not_rated_grouped"));
                    RewardListInfo.notRatedGroupedRewardList(arrayList, rewardList, d, player);
                    continue block6;
                }
                case NOT_RATED_NOT_GROUPED: {
                    arrayList.add(StringHolder.getInstance().getNotNull(player, "drop.not_rated_not_grouped"));
                    RewardListInfo.notGroupedRewardList(arrayList, rewardList, 1.0, d, player);
                    continue block6;
                }
                case SWEEP: {
                    arrayList.add(StringHolder.getInstance().getNotNull(player, "drop.sweep"));
                    RewardListInfo.notGroupedRewardList(arrayList, rewardList, Config.RATE_DROP_SPOIL * player.getRateSpoil(), d, player);
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        int n4 = arrayList.size() / Config.ALT_NPC_SHIFTCLICK_ITEM_COUNT;
        n = Math.min(n, n4);
        int n5 = n * Config.ALT_NPC_SHIFTCLICK_ITEM_COUNT;
        int n6 = Math.max(0, Math.min((n + 1) * Config.ALT_NPC_SHIFTCLICK_ITEM_COUNT - 1, arrayList.size() - 1));
        for (n2 = n5; n2 <= n6; ++n2) {
            stringBuilder.append((String)arrayList.get(n2));
        }
        npcHtmlMessage.replace("%info%", stringBuilder.toString());
        stringBuilder.setLength(0);
        stringBuilder.append("<table><tr>");
        for (n2 = 0; n2 <= n4; ++n2) {
            stringBuilder.append("<td>");
            if (n2 == n) {
                stringBuilder.append(n2 + 1);
            } else {
                stringBuilder.append("<a action=\"bypass -h scripts_actions.RewardListInfo:showReward ").append(n2).append("\">").append(n2 + 1).append("</a>");
            }
            stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr></table>");
        npcHtmlMessage.replace("%paging%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public static void ratedGroupedRewardList(List<String> list, NpcInstance npcInstance, RewardList rewardList, Player player, double d) {
        for (RewardGroup rewardGroup : rewardList) {
            double d2;
            List<RewardData> list2 = rewardGroup.getItems();
            double d3 = rewardGroup.getChance();
            double d4 = d;
            double d5 = npcInstance instanceof RaidBossInstance ? Config.RATE_DROP_RAIDBOSS * (double)player.getBonus().getDropRaidItems() : (npcInstance.isSiegeGuard() ? Config.RATE_DROP_SIEGE_GUARD : Config.RATE_DROP_ITEMS * player.getRateItems());
            double d6 = Config.RATE_DROP_ADENA * player.getRateAdena();
            double d7 = Config.RATE_DROP_SEAL_STONES * player.getRateItems();
            if (rewardGroup.isAdena()) {
                if (d6 == 0.0) continue;
                d2 = d6;
                if (d4 > 10.0) {
                    d4 *= rewardGroup.getChance() / 1000000.0;
                }
                d2 *= d4;
            } else if (rewardGroup.isSealStone()) {
                if (d7 == 0.0) continue;
                d2 = d7;
                d2 = rewardGroup.notRate() ? Math.min(d4, 1.0) : (d2 *= d4);
            } else {
                if (d5 == 0.0) continue;
                d2 = d5;
                d2 = rewardGroup.notRate() ? Math.min(d4, 1.0) : (d2 *= d4);
            }
            double d8 = d2;
            list.add(RewardListInfo.a(rewardGroup, player));
            for (RewardData rewardData : list2) {
                list.add(RewardListInfo.a(rewardData, d8, player));
            }
        }
    }

    public static void notRatedGroupedRewardList(List<String> list, RewardList rewardList, double d, Player player) {
        for (RewardGroup rewardGroup : rewardList) {
            List<RewardData> list2 = rewardGroup.getItems();
            list.add(RewardListInfo.a(rewardGroup, player));
            for (RewardData rewardData : list2) {
                list.add(RewardListInfo.a(rewardData, 1.0, player));
            }
        }
    }

    public static void notGroupedRewardList(List<String> list, RewardList rewardList, double d, double d2, Player player) {
        for (RewardGroup rewardGroup : rewardList) {
            List<RewardData> list2 = rewardGroup.getItems();
            double d3 = d2;
            if (d == 0.0) continue;
            double d4 = d;
            d4 = rewardGroup.notRate() ? Math.min(d3, 1.0) : (d4 *= d3);
            double d5 = Math.ceil(d4);
            for (RewardData rewardData : list2) {
                list.add(RewardListInfo.a(rewardData, d5, player));
            }
        }
    }

    private static String a(RewardGroup rewardGroup, Player player) {
        return StringUtils.replace(StringHolder.getInstance().getNotNull(player, "drop.group.html"), "%group_chance%", au.format(rewardGroup.getChance() / 1000000.0));
    }

    private static String a(RewardData rewardData, double d, Player player) {
        String string = rewardData.getItem().getIcon();
        if (string == null || string.equals("")) {
            string = "icon.etc_question_mark_i00";
        }
        return StringUtils.replaceEach(StringHolder.getInstance().getNotNull(player, "drop.rewardData.html"), new String[]{"%icon%", "%item%", "%drop_min%", "%drop_max%", "%chance%"}, new String[]{string, HtmlUtils.htmlItemName(rewardData.getItemId()), String.valueOf(rewardData.getMinDrop()), String.valueOf(Math.round((double)rewardData.getMaxDrop() * (rewardData.notRate() ? 1.0 : d))), au.format(Math.min(1.0, rewardData.getChance() / 1000000.0))});
    }

    public void showReward(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        int n = 0;
        if (stringArray.length > 0) {
            try {
                n = Integer.parseInt(stringArray[0]);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        RewardListInfo.showRewardHtml(player, npcInstance, n);
    }

    static {
        au.setMaximumFractionDigits(4);
        aU.setMinimumFractionDigits(2);
    }
}

