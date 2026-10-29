/*
 * Decompiled with CFR 0.152.
 */
package achievements;

import achievements.Achievement;
import achievements.AchievementInfo;
import achievements.Achievements;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Strings;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

public class AchievementUI
extends Functions
implements ScriptFile {
    static final String SCRIPT_BYPASS_CLASS = "scripts_" + AchievementUI.class.getName();
    private static final int at = 5;
    private static final NumberFormat au = NumberFormat.getPercentInstance(Locale.ENGLISH);

    public void achievements() {
        this.achievements(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public void achievements(String ... stringArray) {
        int n;
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Achievements.getInstance().isEnabled()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        String string = HtmCache.getInstance().getNotNull("scripts/achievements/achievements.htm", player);
        HtmlTabUI htmlTabUI = new HtmlTabUI(HtmlTabUI.TabStyle.inventory);
        List<AchievementInfo.AchievementInfoCategory> list = Achievements.getInstance().getCategories();
        final int n2 = Math.min(Math.max(0, stringArray.length > 0 ? Integer.parseInt(stringArray[0]) : 0), list.size() - 1);
        AchievementInfo.AchievementInfoCategory achievementInfoCategory = null;
        for (n = 0; n < list.size(); ++n) {
            AchievementInfo.AchievementInfoCategory achievementInfoCategory2 = list.get(n);
            if (n2 == n) {
                achievementInfoCategory = achievementInfoCategory2;
                htmlTabUI.addTab(achievementInfoCategory2.getTitle(player), String.format("%s:achievements %d", SCRIPT_BYPASS_CLASS, n), true);
                continue;
            }
            htmlTabUI.addTab(achievementInfoCategory2.getTitle(player), String.format("%s:achievements %d", SCRIPT_BYPASS_CLASS, n), false);
        }
        string = string.replace("%categories_tabs%", htmlTabUI.toHtml());
        if (achievementInfoCategory != null) {
            string = string.replace("%active_category_title%", achievementInfoCategory.getTitle(player));
            n = stringArray.length > 1 ? Integer.parseInt(stringArray[1]) : 0;
            int n3 = stringArray.length > 2 ? Integer.parseInt(stringArray[2]) : -1;
            int n4 = stringArray.length > 3 ? Integer.parseInt(stringArray[3]) : -1;
            Paginator<AchievementInfo> paginator = new Paginator<AchievementInfo>(5, n){

                @Override
                protected String getBypassForPageOrdinal(int n, int n22) {
                    return String.format("%s:achievements %d %d", SCRIPT_BYPASS_CLASS, n2, n);
                }
            };
            List<AchievementInfo> list2 = Achievements.getInstance().getAchievementInfosByCategory(achievementInfoCategory);
            StringBuilder stringBuilder = new StringBuilder();
            if (list2 != null && !list2.isEmpty()) {
                for (AchievementInfo achievementInfo : list2) {
                    paginator.addItem(achievementInfo, achievementInfo.getLevels().size());
                }
                List list3 = paginator.getItems();
                boolean bl = true;
                Iterator iterator = list3.iterator();
                while (iterator.hasNext()) {
                    Pair pair = (Pair)iterator.next();
                    AchievementInfo achievementInfo = (AchievementInfo)pair.getLeft();
                    bl = !bl;
                    stringBuilder.append(this.a(n2, n, n3, n4, player, achievementInfo, (Pair)pair.getRight(), bl));
                }
            }
            string = string.replace("%achievements_list%", stringBuilder.toString());
            string = string.replace("%pagination%", paginator.toHtml());
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        npcHtmlMessage.setHtml(AchievementUI.truncateHtmlTagsSpaces(string));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private String a(int n, int n2, int n3, int n4, Player player, AchievementInfo achievementInfo, Pair<Integer, Integer> pair, boolean bl) {
        Achievement achievement = new Achievement(achievementInfo, player);
        AchievementInfo.AchievementInfoLevel achievementInfoLevel = achievement.getNextLevel();
        String string = achievement.isCompleted() ? HtmCache.getInstance().getNotNull("scripts/achievements/entry.completed.htm", player) : HtmCache.getInstance().getNotNull("scripts/achievements/entry.htm", player);
        string = string.replace("%template_bg_color%", bl ? " bgcolor=000000" : "");
        string = string.replace("%achievement_icon%", achievementInfo.getIcon());
        string = string.replace("%achievement_name%", Strings.bbParse(achievementInfo.getName(player)));
        string = string.replace("%achievement_current_level%", String.valueOf(achievement.isCompleted() ? achievementInfo.getMaxLevel() : achievementInfoLevel.getLevel()));
        string = string.replace("%achievement_max_level%", String.valueOf(achievementInfo.getMaxLevel()));
        List<AchievementInfo.AchievementInfoLevel> list = achievementInfo.getLevels();
        StringBuilder stringBuilder = new StringBuilder();
        int n5 = pair.getRight();
        for (int i = pair.getLeft().intValue(); i < n5; ++i) {
            AchievementInfo.AchievementInfoLevel achievementInfoLevel2 = list.get(i);
            AchFaceLevelDisplayType achFaceLevelDisplayType = AchFaceLevelDisplayType.DISPLAY_DEFAULT;
            if (achievementInfoLevel2 == achievementInfoLevel) {
                achFaceLevelDisplayType = AchFaceLevelDisplayType.DISPLAY_PROGRESSING;
            } else if (achievement.isCompleted() || achievementInfoLevel != null && achievementInfoLevel2.getLevel() < achievementInfoLevel.getLevel()) {
                achFaceLevelDisplayType = achievement.isLevelRewarded(achievementInfoLevel2) ? AchFaceLevelDisplayType.DISPLAY_REWARDED : AchFaceLevelDisplayType.DISPLAY_COMPLETED;
            }
            stringBuilder.append(this.a(n, n2, n3, n4, player, achFaceLevelDisplayType, achievement, achievementInfoLevel2));
        }
        string = string.replace("%achievement_levels_list%", stringBuilder.toString());
        return string.trim();
    }

    private String a(int n, int n2, int n3, int n4, Player player, AchFaceLevelDisplayType achFaceLevelDisplayType, Achievement achievement, AchievementInfo.AchievementInfoLevel achievementInfoLevel) {
        String string = HtmCache.getInstance().getNotNull("scripts/achievements/" + achFaceLevelDisplayType.templateFileName, player);
        string = string.replaceAll("%achievement_id%", String.valueOf(achievement.getAchInfo().getId()));
        string = string.replaceAll("%achievement_level_ordinal%", String.valueOf(achievementInfoLevel.getLevel()));
        string = string.replace("%achievement_level_description%", achievementInfoLevel.getDesc(player).replace("\\n", "<br1>"));
        if (achFaceLevelDisplayType.haveRewardList) {
            if (n3 == achievement.getAchInfo().getId() && n4 == achievementInfoLevel.getLevel()) {
                String string2 = StringHolder.getInstance().getNotNull(player, "achievements.rewardList.collapseButton");
                string2 = string2.replace("%collapse_bypass%", String.format("%s:achievements %d %d", SCRIPT_BYPASS_CLASS, n, n2));
                List<RewardData> list = achievementInfoLevel.getRewardDataList();
                string = string.replace("%reward_list%", this.a(player, list));
                string = string.replace("%reward_switch%", string2);
            } else {
                String string3 = StringHolder.getInstance().getNotNull(player, "achievements.rewardList.expandButton");
                string3 = string3.replace("%expand_bypass%", String.format("%s:achievements %d %d %d %d", SCRIPT_BYPASS_CLASS, n, n2, achievement.getAchInfo().getId(), achievementInfoLevel.getLevel()));
                string = string.replace("%reward_list%", StringHolder.getInstance().getNotNull(player, "achievements.rewardList.collapsedText"));
                string = string.replace("%reward_switch%", string3);
            }
        }
        if (achFaceLevelDisplayType.haveProgressBar) {
            string = string.replace("%achievement_level_progress_bar%", new HtmlProgressBarUI(HtmlProgressBarUI.ProgressBarStyle.flax_light).setBarWidth(64).setFull(achievementInfoLevel.getValue()).setValue(achievement.getCounter().getVal()).toHtml());
        }
        if (achFaceLevelDisplayType.haveRewardBypass) {
            string = n3 >= 0 && n4 >= 0 ? string.replace("%achievement_level_reward_bypass%", String.format("%s:achieveReward %d %d %d %d %d %d", SCRIPT_BYPASS_CLASS, achievement.getAchInfo().getId(), achievementInfoLevel.getLevel(), n, n2, n3, n4)) : string.replace("%achievement_level_reward_bypass%", String.format("%s:achieveReward %d %d %d %d", SCRIPT_BYPASS_CLASS, achievement.getAchInfo().getId(), achievementInfoLevel.getLevel(), n, n2));
        }
        return string.trim();
    }

    private String a(Player player, List<RewardData> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (RewardData rewardData : list) {
            String string = HtmCache.getInstance().getNotNull("scripts/achievements/reward.htm", player);
            ItemTemplate itemTemplate = rewardData.getItem();
            string = string.replace("%item_icon%", itemTemplate.getIcon());
            string = string.replace("%item_id%", String.valueOf(itemTemplate.getItemId()));
            string = string.replace("%item_name%", itemTemplate.getName());
            string = string.replace("%min_amount%", String.valueOf(rewardData.getMinDrop()));
            string = string.replace("%max_amount%", String.valueOf(rewardData.getMaxDrop()));
            string = string.replace("%chance%", au.format(rewardData.getChance() / 1000000.0));
            stringBuilder.append(string);
        }
        return stringBuilder.toString().trim();
    }

    public void achieveReward(String ... stringArray) {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        NpcInstance npcInstance = this.getNpc();
        if (!Achievements.getInstance().isEnabled()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (stringArray.length < 4) {
            return;
        }
        int n = Integer.parseInt(stringArray[0]);
        int n2 = Integer.parseInt(stringArray[1]);
        AchievementInfo achievementInfo = Achievements.getInstance().getAchievementInfoById(n);
        if (achievementInfo == null) {
            return;
        }
        AchievementInfo.AchievementInfoLevel achievementInfoLevel = achievementInfo.getLevel(n2);
        if (achievementInfoLevel == null) {
            return;
        }
        Achievement achievement = new Achievement(achievementInfo, player);
        if (achievement.isRewardableLevel(achievementInfoLevel)) {
            List<RewardData> list = achievementInfoLevel.getRewardDataList();
            long l = 0L;
            long l2 = 0L;
            for (RewardData rewardData : list) {
                l += (long)rewardData.getItem().getWeight() * rewardData.getMaxDrop();
                l2 += rewardData.getItem().isStackable() ? 1L : rewardData.getMaxDrop();
            }
            if (!player.getInventory().validateWeight(l)) {
                player.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            if (!player.getInventory().validateCapacity(l2)) {
                player.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                return;
            }
            achievement.setLevelRewarded(achievementInfoLevel, true);
            for (RewardData rewardData : list) {
                long l3 = Util.rollDrop(rewardData.getMinDrop(), rewardData.getMaxDrop(), rewardData.getChance(), false);
                if (l3 <= 0L) continue;
                ItemFunctions.addItem((Playable)player, rewardData.getItemId(), l3, true);
                Log.LogItem(player, Log.ItemLog.AchievementReceived, rewardData.getItemId(), l3);
            }
            player.sendMessage(new CustomMessage("achievements.rewardedS1LevelS2", player, achievementInfo.getName(player), achievementInfoLevel.getLevel()));
        }
        this.achievements(stringArray[2], stringArray[3]);
    }

    public void adminReload() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!player.isGM()) {
            return;
        }
        Achievements.getInstance().parse();
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

    public static abstract class Paginator<ItemType> {
        private final int aG;
        private List<Pair<ItemType, Integer>> aH = new LinkedList<Pair<ItemType, Integer>>();
        private int aI;

        public Paginator(int n, int n2) {
            this.aG = n;
            this.aI = n2;
        }

        public Paginator(int n) {
            this(n, 0);
        }

        public int getPageIdx() {
            return this.aI;
        }

        public Paginator<ItemType> setPageIdx(int n) {
            this.aI = n;
            return this;
        }

        public Paginator<ItemType> addItem(ItemType ItemType, int n) {
            this.aH.add(Pair.of(ItemType, n));
            return this;
        }

        private int c() {
            int n = 0;
            for (Pair<ItemType, Integer> pair : this.aH) {
                n += pair.getRight().intValue();
            }
            return n;
        }

        public List<Pair<ItemType, Pair<Integer, Integer>>> getItems(int n) {
            int n2 = n * this.aG;
            int n3 = n2 + this.aG;
            int n4 = 0;
            LinkedList<Pair<ItemType, Pair<Integer, Integer>>> linkedList = new LinkedList<Pair<ItemType, Pair<Integer, Integer>>>();
            for (Pair<ItemType, Integer> pair : this.aH) {
                int n5 = pair.getRight();
                if (n4 < n3 && n4 + n5 > n2) {
                    linkedList.add(Pair.of(pair.getLeft(), Pair.of(Math.max(n2 - n4, 0), Math.min(n5, n3 - n4))));
                }
                n4 += n5;
            }
            return linkedList;
        }

        public List<Pair<ItemType, Pair<Integer, Integer>>> getItems() {
            return this.getItems(this.aI);
        }

        protected abstract String getBypassForPageOrdinal(int var1, int var2);

        public String toHtml() {
            int n = this.c();
            int n2 = (n + this.aG - 1) / this.aG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<table border=0 cellspacing=0 cellpadding=0><tr>");
            for (int i = 0; i < n2; ++i) {
                int n3 = i + 1;
                stringBuilder.append("<td>");
                if (i == this.aI) {
                    stringBuilder.append(n3);
                } else {
                    stringBuilder.append("<a action=\"bypass -h ").append(this.getBypassForPageOrdinal(i, n3)).append("\">").append(n3).append("</a>");
                }
                stringBuilder.append("</td>");
            }
            stringBuilder.append("</tr></table>");
            return stringBuilder.toString();
        }
    }

    public static class HtmlProgressBarUI {
        private final ProgressBarStyle aw;
        private int ax;
        private int ay = -1;
        private int az = -1;
        private int value = -1;

        public HtmlProgressBarUI(ProgressBarStyle progressBarStyle) {
            this.aw = progressBarStyle;
            this.ax = progressBarStyle.maxWidth >= 0 ? progressBarStyle.maxWidth : 100;
        }

        public int getBarWidth() {
            return this.ax;
        }

        public HtmlProgressBarUI setBarWidth(int n) {
            this.ax = n;
            return this;
        }

        public int getValue() {
            return this.value;
        }

        public HtmlProgressBarUI setValue(int n) {
            this.value = n;
            if (n > this.az) {
                this.az = n;
            }
            return this;
        }

        public int getFull() {
            return this.az;
        }

        public HtmlProgressBarUI setFull(int n) {
            this.az = n;
            return this;
        }

        public int getPercent() {
            return this.ay;
        }

        public void setPercent(int n) {
            this.ay = n;
        }

        public String toHtml() {
            StringBuilder stringBuilder = new StringBuilder();
            int n = this.aw.maxWidth >= 0 ? Math.min(this.ax, this.aw.maxWidth) : this.ax;
            stringBuilder.append("<table width=").append(n).append(" border=0 cellspacing=0 cellpadding=0><tr>");
            if (this.value >= 0 && this.az >= 0) {
                if (this.value < this.az) {
                    int n2 = (int)((float)n / (float)this.az * (float)this.value);
                    stringBuilder.append("<td><img src=\"").append(this.aw.valueTexture).append("\" width=").append(n2).append(" height=").append(this.aw.height).append("></td>");
                    stringBuilder.append("<td><img src=\"").append(this.aw.backTexture).append("\" width=").append(n - n2).append(" height=").append(this.aw.height).append("></td>");
                } else {
                    stringBuilder.append("<td><img src=\"").append(this.az == 0 ? this.aw.backTexture : this.aw.valueTexture).append("\" width=").append(n).append(" height=").append(this.aw.height).append("></td>");
                }
            } else if (this.ay >= 0) {
                int n3 = (int)((float)this.ay / 100.0f * (float)this.value);
                if (this.ay < 100) {
                    stringBuilder.append("<td><img src=\"").append(this.aw.valueTexture).append("\" width=").append(n3).append(" height=").append(this.aw.height).append("></td>");
                    stringBuilder.append("<td><img src=\"").append(this.aw.backTexture).append("\" width=").append(n - n3).append(" height=").append(this.aw.height).append("></td>");
                } else {
                    stringBuilder.append("<td><img src=\"").append(this.ay == 0 ? this.aw.backTexture : this.aw.valueTexture).append("\" width=").append(n).append(" height=").append(this.aw.height).append("></td>");
                }
            }
            stringBuilder.append("</tr></table>");
            return stringBuilder.toString();
        }

        public static enum ProgressBarStyle {
            classic_red(3, 96, "sek.cbui62", "sek.cbui64"),
            classic_blue(3, 96, "sek.cbui63", "sek.cbui64"),
            yellow(12, -1, "L2UI_CH3.br_bar1_cp", "L2UI_CH3.br_bar1back_cp"),
            flax(12, -1, "L2UI_CH3.br_bar1_cp1", "L2UI_CH3.br_bar1back_cp"),
            flax_light(8, -1, "L2UI_CH3.br_bar1_cp1", "L2UI_CH3.br_bar1back_cp");

            final int height;
            final int maxWidth;
            final String valueTexture;
            final String backTexture;

            private ProgressBarStyle(int n2, int n3, String string2, String string3) {
                this.height = n2;
                this.maxWidth = n3;
                this.valueTexture = string2;
                this.backTexture = string3;
            }
        }
    }

    public static class HtmlTabUI {
        private final TabStyle aB;
        private TabRecord aC;
        private List<TabRecord> aD = new ArrayList<TabRecord>();
        private int aE = -1;

        public HtmlTabUI(TabStyle tabStyle) {
            this(tabStyle, 296 / tabStyle.width);
        }

        public HtmlTabUI(TabStyle tabStyle, int n) {
            this.aB = tabStyle;
            this.aE = n;
        }

        public TabRecord addTab(String string, String string2, boolean bl) {
            TabRecord tabRecord = new TabRecord(string, string2);
            if (bl) {
                this.setActive(tabRecord);
            }
            this.aD.add(tabRecord);
            return tabRecord;
        }

        public TabRecord addTab(String string, String string2) {
            return this.addTab(string, string2, false);
        }

        public void setActive(TabRecord tabRecord) {
            this.aC = tabRecord;
        }

        public void setTabsPerRow(int n) {
            this.aE = n;
        }

        public String toHtml() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<table width=").append(this.aE * this.aB.width).append(" border=0 cellspacing=0 cellpadding=0>");
            int n = 0;
            while (n * this.aE < this.aD.size()) {
                stringBuilder.append("<tr>");
                for (int i = 0; i < this.aE; ++i) {
                    stringBuilder.append("<td width=").append(this.aB.width).append(">");
                    int n2 = i + n * this.aE;
                    if (n2 < this.aD.size()) {
                        TabRecord tabRecord = this.aD.get(n2);
                        stringBuilder.append("<button").append(" width=").append(this.aB.width).append(" height=").append(this.aB.height);
                        if (tabRecord.title != null) {
                            stringBuilder.append(" value=\"").append(tabRecord.title).append("\"");
                        }
                        if (tabRecord.bypass != null) {
                            stringBuilder.append(" action=\"bypass -h ").append(tabRecord.bypass).append("\"");
                        }
                        if (tabRecord == this.aC) {
                            stringBuilder.append(" fore=").append(this.aB.active).append(" back=").append(this.aB.active);
                        } else {
                            stringBuilder.append(" fore=").append(this.aB.fore).append(" back=").append(this.aB.back);
                        }
                        stringBuilder.append(">");
                    } else {
                        stringBuilder.append("&nbsp;");
                    }
                    stringBuilder.append("</td>");
                }
                stringBuilder.append("</tr>");
                ++n;
            }
            stringBuilder.append("</table>");
            return stringBuilder.toString();
        }

        public class TabRecord {
            final String title;
            final String bypass;

            private TabRecord(String string, String string2) {
                this.title = string;
                this.bypass = string2;
            }
        }

        public static enum TabStyle {
            board(74, 22, "L2UI_CH3.board_tab1", "L2UI_CH3.board_tab2", "L2UI_CH3.board_tab2"),
            chatting(true, 64, 22, "L2UI_CH3.chatting_tab1", "L2UI_CH3.chatting_tab2", "L2UI_CH3.chatting_tab2"),
            inventory(94, 22, "L2UI_CH3.inventory_tab1", "L2UI_CH3.inventory_tab2", "L2UI_CH3.inventory_tab2"),
            msn(114, 22, "L2UI_CH3.msn_tab1", "L2UI_CH3.msn_tab2", "L2UI_CH3.msn_tab2"),
            normal(74, 22, "L2UI_CH3.normal_tab", "L2UI_CH3.normal_tab_on", "L2UI_CH3.normal_tab_on");

            final boolean isButtom;
            final int width;
            final int height;
            final String active;
            final String fore;
            final String back;

            private TabStyle(boolean bl, int n2, int n3, String string2, String string3, String string4) {
                this.isButtom = bl;
                this.width = n2;
                this.height = n3;
                this.active = string2;
                this.fore = string3;
                this.back = string4;
            }

            private TabStyle(int n2, int n3, String string2, String string3, String string4) {
                this(false, n2, n3, string2, string3, string4);
            }
        }
    }

    private static enum AchFaceLevelDisplayType {
        DISPLAY_DEFAULT("level.htm", false, false, true),
        DISPLAY_PROGRESSING("level.progressing.htm", true, false, true),
        DISPLAY_COMPLETED("level.completed.htm", false, true, true),
        DISPLAY_REWARDED("level.rewarded.htm", false, false, false);

        final String templateFileName;
        final boolean haveProgressBar;
        final boolean haveRewardBypass;
        final boolean haveRewardList;

        private AchFaceLevelDisplayType(String string2, boolean bl, boolean bl2, boolean bl3) {
            this.templateFileName = string2;
            this.haveProgressBar = bl;
            this.haveRewardBypass = bl2;
            this.haveRewardList = bl3;
        }
    }
}

