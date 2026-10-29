/*
 * Decompiled with CFR 0.152.
 */
package services.community.custom;

import java.util.StringTokenizer;
import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ACbConfigManager
implements ICommunityBoardHandler,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(ACbConfigManager.class);
    public static final String PVPCB_FILE = "config/pvpcommunityboard.properties";
    public static int FIRST_CLASS_ID;
    public static int FIRST_CLASS_PRICE;
    public static int SECOND_CLASS_ID;
    public static int SECOND_CLASS_PRICE;
    public static int THRID_CLASS_ID;
    public static int THRID_CLASS_PRICE;
    public static boolean ALLOW_PVPCB_ABNORMAL;
    public static boolean ALLOW_PVPCB_SHOP;
    public static boolean ALLOW_PVPCB_ECHANT;
    public static boolean ALLOW_PVPCB_CLASSMASTER;
    public static boolean ALLOW_PVPCB_TELEPORT;
    public static int[] COMMUNITY_CLASS_MASTERS_REWARD_ITEM;
    public static int[] COMMUNITY_MASTERS_REWARD_AMOUNT;
    public static StringTokenizer st;
    public static int ALT_CB_TELE_POINT_PRICE;
    public static int ALT_CB_TELE_POINT_MAX_COUNT;
    public static boolean ALT_CB_TELE_PREMIUM_ONLY;
    public static int ALT_CB_DELVLV_ITEM_ID;
    public static long ALT_CB_DELVL_ITEM_COUNT;
    public static int ALT_CB_NOBLES_ITEM_ID;
    public static long ALT_CB_NOBLES_ITEM_COUNT;
    public static int ALT_CB_CHANGESEX_ITEM_ID;
    public static long ALT_CB_CHANGESEX_ITEM_COUNT;
    public static int ALT_CB_CHANGENAME_ITEM_ID;
    public static long ALT_CB_CHANGENAME_ITEM_COUNT;
    public static int ALT_CB_CLANUP_ITEM_ID;
    public static long ALT_CB_CLANUP_ITEM_COUNT;
    public static int ALT_CB_CLAN_PENALTY_ITEM_ID;
    public static long ALT_CB_CLAN_PENALTY_ITEM_COUNT;
    public static boolean ALT_CB_ALLOW_AUGMENT_BBS;
    public static boolean ALT_CB_ALLOW_AUGMENT_CANCEL_BBS;
    public static double ALT_CB_ONLINE_MULTIPLIER;

    @Override
    public String[] getBypassCommands() {
        return null;
    }

    @Override
    public void onBypassCommand(Player player, String string) {
    }

    @Override
    public void onWriteCommand(Player player, String string, String string2, String string3, String string4, String string5, String string6) {
    }

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Custom Community Config loaded.");
            ACbConfigManager.loadPvPCBSettings();
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public static void loadPvPCBSettings() {
        ExProperties exProperties = Config.load(PVPCB_FILE);
        ALLOW_PVPCB_ABNORMAL = exProperties.getProperty("AllowBBSAbnormal", false);
        ALLOW_PVPCB_SHOP = exProperties.getProperty("AllowBBSShop", true);
        ALLOW_PVPCB_ECHANT = exProperties.getProperty("AllowBBSEnchant", true);
        ALLOW_PVPCB_CLASSMASTER = exProperties.getProperty("AllowBBSClassMaster", true);
        ALLOW_PVPCB_TELEPORT = exProperties.getProperty("AllowBBSTeleport", true);
        FIRST_CLASS_ID = exProperties.getProperty("FirstProffesionId", 57);
        FIRST_CLASS_PRICE = exProperties.getProperty("FirstProffesionCount", 10000000);
        SECOND_CLASS_ID = exProperties.getProperty("SecondProffesionId", 57);
        SECOND_CLASS_PRICE = exProperties.getProperty("SecondProffesionCount", 20000000);
        THRID_CLASS_ID = exProperties.getProperty("ThridProffesionId", 57);
        THRID_CLASS_PRICE = exProperties.getProperty("ThridProffesionCount", 30000000);
        COMMUNITY_CLASS_MASTERS_REWARD_ITEM = exProperties.getProperty("CommunityProffReward", new int[]{0, 0, 0});
        COMMUNITY_MASTERS_REWARD_AMOUNT = exProperties.getProperty("CommunityProffAmount", new int[]{0, 0, 0});
        ALT_CB_TELE_POINT_PRICE = exProperties.getProperty("CommunityTeleporterPointPrice", 100);
        ALT_CB_TELE_POINT_MAX_COUNT = exProperties.getProperty("CommunityTeleporterPointCount", 10);
        ALT_CB_TELE_PREMIUM_ONLY = exProperties.getProperty("CommunityTeleporterPremiumOnly", false);
        ALT_CB_DELVLV_ITEM_ID = exProperties.getProperty("CommunityDeLevelItemId", 57);
        ALT_CB_DELVL_ITEM_COUNT = exProperties.getProperty("CommunityDeLevelItemCount", 100L);
        ALT_CB_NOBLES_ITEM_ID = exProperties.getProperty("CommunityNobleItemId", 57);
        ALT_CB_NOBLES_ITEM_COUNT = exProperties.getProperty("CommunityNobleItemCount", 100L);
        ALT_CB_CHANGESEX_ITEM_ID = exProperties.getProperty("CommunityChangeSexItemId", 57);
        ALT_CB_CHANGESEX_ITEM_COUNT = exProperties.getProperty("CommunityChangeSexItemCount", 100L);
        ALT_CB_CHANGENAME_ITEM_ID = exProperties.getProperty("CommunityChangeNameItemId", 57);
        ALT_CB_CHANGENAME_ITEM_COUNT = exProperties.getProperty("CommunityChangeNameItemCount", 100L);
        ALT_CB_CLANUP_ITEM_ID = exProperties.getProperty("CommunityClanupNameItemId", 57);
        ALT_CB_CLANUP_ITEM_COUNT = exProperties.getProperty("CommunityClanupItemCount", 100000000L);
        ALT_CB_CLAN_PENALTY_ITEM_ID = exProperties.getProperty("ClanPenaltyClearItem", 4037);
        ALT_CB_CLAN_PENALTY_ITEM_COUNT = exProperties.getProperty("ClanPenaltyClearCount", 1L);
        ALT_CB_ALLOW_AUGMENT_BBS = exProperties.getProperty("AllowAugmentItemsFromCB", false);
        ALT_CB_ALLOW_AUGMENT_CANCEL_BBS = exProperties.getProperty("AllowAugmentCancelFromCB", false);
        ALT_CB_ONLINE_MULTIPLIER = exProperties.getProperty("BBSOnlineMultiplier", 1.0);
    }
}

