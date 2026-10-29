/*
 * Decompiled with CFR 0.152.
 */
package services.community.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.gs2as.IGPwdCng;
import l2.gameserver.network.l2.s2c.ShowBoard;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.item.WeaponTemplate;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CbPersonalCabinet
implements ICommunityBoardHandler,
ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(CbPersonalCabinet.class);
    private static final Pattern zK = Pattern.compile("^([\\w\\d_-]{4,18})$");
    private static final long zL = 3600000L;
    private static final String[] bYI = new String[]{"_bbscpassword", "_bbscrepair", "_bbscstats"};

    @Override
    public String[] getBypassCommands() {
        return bYI;
    }

    private void r(Player player, String string) {
        long l;
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        if (!stringTokenizer.hasMoreTokens()) {
            return;
        }
        String string2 = stringTokenizer.nextToken();
        if (!stringTokenizer.hasMoreTokens()) {
            player.sendMessage("New password required.");
            return;
        }
        String string3 = stringTokenizer.nextToken();
        if (!stringTokenizer.hasMoreTokens()) {
            player.sendMessage("Confirm new password required.");
            return;
        }
        String string4 = stringTokenizer.nextToken();
        if (!string3.equals(string4)) {
            player.sendMessage("New password and confirm must match.");
            return;
        }
        if (!zK.matcher(string3).matches()) {
            player.sendMessage("Password requirement's is not met!");
            return;
        }
        String string5 = player.getVar("LastPwdChng");
        if (string5 != null && !string5.isEmpty() && (l = Long.parseLong(string5) * 1000L) + 3600000L > System.currentTimeMillis()) {
            player.sendMessage("Password can't be change so frequently.");
            return;
        }
        AuthServerCommunication.getInstance().sendPacket(new IGPwdCng(player, string2, string3));
    }

    private int M(String string) {
        int n;
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        if (!stringTokenizer.hasMoreTokens()) {
            return -1;
        }
        String string2 = stringTokenizer.nextToken();
        try {
            n = Integer.parseInt(string2);
        }
        catch (NumberFormatException numberFormatException) {
            return -1;
        }
        return n;
    }

    private String s(Player player, String string) {
        Creature creature = null;
        double d = Formulas.calcHpRegen(player);
        double d2 = Formulas.calcCpRegen(player);
        double d3 = Formulas.calcMpRegen(player);
        double d4 = player.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0.0, creature, null);
        double d5 = player.calcStat(Stats.HEAL_EFFECTIVNESS, 100.0, creature, null);
        double d6 = player.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100.0, creature, null);
        double d7 = 2.0 * player.calcStat(Stats.CRITICAL_DAMAGE, creature, null);
        double d8 = player.calcStat(Stats.CRITICAL_DAMAGE_STATIC, creature, null);
        double d9 = player.calcStat(Stats.MCRITICAL_RATE, creature, null);
        double d10 = player.calcStat(Stats.FATALBLOW_RATE, creature, null);
        ItemInstance itemInstance = player.getSecondaryWeaponInstance();
        boolean bl = itemInstance != null && itemInstance.getItemType() == WeaponTemplate.WeaponType.NONE;
        double d11 = bl ? player.calcStat(Stats.SHIELD_DEFENCE, player.getTemplate().baseShldDef, creature, null) : 0.0;
        double d12 = bl ? player.calcStat(Stats.SHIELD_RATE, creature, null) : 0.0;
        double d13 = player.calcStat(Stats.SKILL_POWER, 1.0, creature, null);
        double d14 = player.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1.0, creature, null);
        double d15 = player.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1.0, creature, null);
        double d16 = player.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0, creature, null);
        double d17 = player.calcStat(Stats.PSKILL_EVASION, null, null);
        double d18 = player.calcStat(Stats.REFLECT_DAMAGE_PERCENT, creature, null);
        double d19 = player.calcStat(Stats.REFLECT_MAGIC_SKILL, creature, null);
        double d20 = player.calcStat(Stats.REFLECT_PHYSIC_SKILL, creature, null);
        double d21 = player.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, creature, null);
        double d22 = player.calcStat(Stats.PHYSIC_REUSE_RATE, creature, null);
        double d23 = player.calcStat(Stats.MAGIC_REUSE_RATE, creature, null);
        double d24 = player.calcStat(Stats.BLEED_POWER, creature, null);
        double d25 = player.calcStat(Stats.BLEED_RESIST, creature, null);
        double d26 = player.calcStat(Stats.POISON_POWER, creature, null);
        double d27 = player.calcStat(Stats.POISON_RESIST, creature, null);
        double d28 = player.calcStat(Stats.STUN_POWER, creature, null);
        double d29 = player.calcStat(Stats.STUN_RESIST, creature, null);
        double d30 = player.calcStat(Stats.ROOT_POWER, creature, null);
        double d31 = player.calcStat(Stats.ROOT_RESIST, creature, null);
        double d32 = player.calcStat(Stats.SLEEP_POWER, creature, null);
        double d33 = player.calcStat(Stats.SLEEP_RESIST, creature, null);
        double d34 = player.calcStat(Stats.PARALYZE_POWER, creature, null);
        double d35 = player.calcStat(Stats.PARALYZE_RESIST, creature, null);
        double d36 = player.calcStat(Stats.MENTAL_POWER, creature, null);
        double d37 = player.calcStat(Stats.MENTAL_RESIST, creature, null);
        double d38 = player.calcStat(Stats.DEBUFF_POWER, creature, null);
        double d39 = player.calcStat(Stats.DEBUFF_RESIST, creature, null);
        double d40 = player.calcStat(Stats.CANCEL_POWER, creature, null);
        double d41 = player.calcStat(Stats.CANCEL_RESIST, creature, null);
        double d42 = 100.0 - player.calcStat(Stats.SWORD_WPN_VULNERABILITY, creature, null);
        double d43 = 100.0 - player.calcStat(Stats.DUAL_WPN_VULNERABILITY, creature, null);
        double d44 = 100.0 - player.calcStat(Stats.BLUNT_WPN_VULNERABILITY, creature, null);
        double d45 = 100.0 - player.calcStat(Stats.DAGGER_WPN_VULNERABILITY, creature, null);
        double d46 = 100.0 - player.calcStat(Stats.BOW_WPN_VULNERABILITY, creature, null);
        double d47 = 100.0 - player.calcStat(Stats.POLE_WPN_VULNERABILITY, creature, null);
        double d48 = 100.0 - player.calcStat(Stats.FIST_WPN_VULNERABILITY, creature, null);
        double d49 = 100.0 - player.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, creature, null);
        double d50 = player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, creature, null);
        double d51 = 100.0 - 100.0 * (player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1.0, creature, null) - d50);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMaximumFractionDigits(1);
        numberFormat.setMinimumFractionDigits(1);
        NumberFormat numberFormat2 = NumberFormat.getInstance(Locale.ENGLISH);
        numberFormat2.setMaximumFractionDigits(1);
        numberFormat2.setMinimumFractionDigits(2);
        StrBuilder strBuilder = new StrBuilder(string);
        strBuilder.replaceFirst("%hpRegen%", numberFormat.format(d));
        strBuilder.replaceFirst("%cpRegen%", numberFormat.format(d2));
        strBuilder.replaceFirst("%mpRegen%", numberFormat.format(d3));
        strBuilder.replaceFirst("%hpDrain%", numberFormat.format(d4));
        strBuilder.replaceFirst("%hpGain%", numberFormat.format(d5));
        strBuilder.replaceFirst("%mpGain%", numberFormat.format(d6));
        strBuilder.replaceFirst("%critPerc%", numberFormat.format(d7));
        strBuilder.replaceFirst("%critStatic%", numberFormat.format(d8));
        strBuilder.replaceFirst("%mCritRate%", numberFormat.format(d9));
        strBuilder.replaceFirst("%blowRate%", numberFormat.format(d10));
        strBuilder.replaceFirst("%shieldDef%", numberFormat.format(d11));
        strBuilder.replaceFirst("%shieldRate%", numberFormat.format(d12));
        strBuilder.replaceFirst("%bleedPower%", numberFormat.format(d24));
        strBuilder.replaceFirst("%bleedResist%", numberFormat.format(d25));
        strBuilder.replaceFirst("%poisonPower%", numberFormat.format(d26));
        strBuilder.replaceFirst("%poisonResist%", numberFormat.format(d27));
        strBuilder.replaceFirst("%stunPower%", numberFormat.format(d28));
        strBuilder.replaceFirst("%stunResist%", numberFormat.format(d29));
        strBuilder.replaceFirst("%rootPower%", numberFormat.format(d30));
        strBuilder.replaceFirst("%SkillPower%", numberFormat2.format(d13));
        strBuilder.replaceFirst("%PvPPhysDmg%", numberFormat2.format(d14));
        strBuilder.replaceFirst("%PvPSkillDmg%", numberFormat2.format(d15));
        strBuilder.replaceFirst("%MagicPvPSkillDmg%", numberFormat2.format(d16));
        strBuilder.replaceFirst("%pSkillEvas%", numberFormat.format(d17));
        strBuilder.replaceFirst("%reflectDam%", numberFormat.format(d18));
        strBuilder.replaceFirst("%reflectSMagic%", numberFormat.format(d19));
        strBuilder.replaceFirst("%reflectSPhys%", numberFormat.format(d20));
        strBuilder.replaceFirst("%meleePhysRes%", numberFormat.format(d21));
        strBuilder.replaceFirst("%pReuse%", numberFormat.format(d22));
        strBuilder.replaceFirst("%mReuse%", numberFormat.format(d23));
        strBuilder.replaceFirst("%rootResist%", numberFormat.format(d31));
        strBuilder.replaceFirst("%sleepPower%", numberFormat.format(d32));
        strBuilder.replaceFirst("%sleepResist%", numberFormat.format(d33));
        strBuilder.replaceFirst("%paralyzePower%", numberFormat.format(d34));
        strBuilder.replaceFirst("%paralyzeResist%", numberFormat.format(d35));
        strBuilder.replaceFirst("%mentalPower%", numberFormat.format(d36));
        strBuilder.replaceFirst("%mentalResist%", numberFormat.format(d37));
        strBuilder.replaceFirst("%debuffPower%", numberFormat.format(d38));
        strBuilder.replaceFirst("%debuffResist%", numberFormat.format(d39));
        strBuilder.replaceFirst("%cancelPower%", numberFormat.format(d40));
        strBuilder.replaceFirst("%cancelResist%", numberFormat.format(d41));
        strBuilder.replaceFirst("%swordResist%", numberFormat.format(d42));
        strBuilder.replaceFirst("%dualResist%", numberFormat.format(d43));
        strBuilder.replaceFirst("%bluntResist%", numberFormat.format(d44));
        strBuilder.replaceFirst("%daggerResist%", numberFormat.format(d45));
        strBuilder.replaceFirst("%bowResist%", numberFormat.format(d46));
        strBuilder.replaceFirst("%fistResist%", numberFormat.format(d48));
        strBuilder.replaceFirst("%poleResist%", numberFormat.format(d47));
        strBuilder.replaceFirst("%critChanceResist%", numberFormat.format(d49));
        strBuilder.replaceFirst("%critDamResist%", numberFormat.format(d51));
        return strBuilder.toString();
    }

    @Override
    public void onBypassCommand(Player player, String string) {
        if (string.startsWith(bYI[0])) {
            String string2 = HtmCache.getInstance().getNotNull("scripts/services/community/percab/pass.htm", player);
            ShowBoard.separateAndSend(string2, player);
            this.r(player, string.substring(bYI[0].length()));
        } else if (string.startsWith(bYI[1])) {
            String string3 = HtmCache.getInstance().getNotNull("scripts/services/community/percab/repair.htm", player);
            String string4 = player.getAccountName();
            StringBuilder stringBuilder = new StringBuilder();
            int n = this.M(string.substring(bYI[1].length()));
            int n2 = -1;
            String string5 = null;
            List<Pair<Integer, String>> list = CbPersonalCabinet.N(string4);
            int n3 = 1;
            for (Pair<Integer, String> pair : list) {
                if (pair.getLeft().intValue() == player.getObjectId() || World.getPlayer(pair.getLeft()) != null) continue;
                if (n3 % 2 != 0) {
                    stringBuilder.append("<tr><td ALIGN=\"left\"><table height=20 WIDTH=216><tr><td height=20 WIDTH=20 ALIGN=\"center\">");
                } else {
                    stringBuilder.append("<tr><td ALIGN=\"left\"><table height=20 WIDTH=216><tr><td height=20 WIDTH=20 ALIGN=\"center\">");
                }
                stringBuilder.append("<IMG HEIGHT=32 WIDTH=32 SRC=\"L2UI_CH3.calculate1_").append(n3);
                stringBuilder.append("\" WIDTH=15></td><td height=20 WIDTH=200 ALIGN=\"left\"><a action=\"bypass -h _bbscrepair ");
                stringBuilder.append(pair.getLeft()).append("\" msg=\"Are you really want to repair ");
                stringBuilder.append(pair.getRight()).append("?\">").append(pair.getRight()).append("</a>");
                stringBuilder.append("</td></tr></table></td></tr>\n");
                ++n3;
                if (pair.getLeft() != n) continue;
                n2 = pair.getLeft();
                string5 = pair.getRight();
            }
            string3 = string3.replace("<%repair_char_list_tbl%>", stringBuilder.toString());
            ShowBoard.separateAndSend(string3, player);
            if (n2 > 0) {
                if (World.getPlayer(n2) == null) {
                    CbPersonalCabinet.ai(n2);
                    player.sendMessage("Character " + string5 + " repaired.");
                } else {
                    player.sendMessage("Character online.");
                }
            }
        } else if (string.startsWith(bYI[2])) {
            String string6 = HtmCache.getInstance().getNotNull("scripts/services/community/percab/stats.htm", player);
            string6 = this.s(player, string6);
            ShowBoard.separateAndSend(string6, player);
        }
    }

    @Override
    public void onWriteCommand(Player player, String string, String string2, String string3, String string4, String string5, String string6) {
    }

    @Override
    public void onLoad() {
        CommunityBoardManager.getInstance().registerHandler(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static List<Pair<Integer, String>> N(String string) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        LinkedList<Pair<Integer, String>> linkedList = new LinkedList<Pair<Integer, String>>();
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `obj_Id`, `char_name` FROM `characters` WHERE `account_name` = ? AND `online` = 0");
            preparedStatement.setString(1, string);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int n = resultSet.getInt("obj_Id");
                String string2 = resultSet.getString("char_name");
                linkedList.add(new ImmutablePair<Integer, String>(n, string2));
            }
        }
        catch (Exception exception) {
            try {
                LOG.error("Error while getting characters of " + string, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return linkedList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void ai(int n) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE `characters` SET `x` = 17867, `y` = 170259, `z` = -3503 WHERE `obj_Id` = ?");
            preparedStatement.setInt(1, n);
            preparedStatement.executeUpdate();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("DELETE FROM character_effects_save WHERE object_id=?");
            preparedStatement.setInt(1, n);
            preparedStatement.executeUpdate();
            DbUtils.close(preparedStatement);
            preparedStatement = connection.prepareStatement("UPDATE `items` SET `location` = \"WAREHOUSE\" WHERE `location` IN (\"PAPERDOLL\", \"INVENTORY\") AND `owner_id` = ? AND `item_type` NOT IN (13530, 13531, 13532, 13533, 13534, 13535, 13536, 13537, 13538, 10281, 10283, 10282, 10286, 10284, 10285, 10287, 10289, 10290, 10288, 10294, 10292, 10291, 10293, 10280, 10612)");
            preparedStatement.setInt(1, n);
            preparedStatement.executeUpdate();
            DbUtils.close(preparedStatement);
        }
        catch (Exception exception) {
            try {
                LOG.error("Error while repairing character " + n, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }
}

