/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import java.text.NumberFormat;
import java.util.Locale;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Strings;
import org.apache.commons.lang3.text.StrBuilder;

public class WhoAmI
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"whoami", "whoiam"};

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        Creature creature = null;
        if (Config.SERVICES_WHOIAM_COMMAND_ENABLE || player.isGM()) {
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
            double d13 = Config.RATE_XP * player.getRateExp();
            double d14 = Config.RATE_SP * player.getRateSp();
            double d15 = Config.RATE_RAIDBOSS_XP * player.getRaidRateExp();
            double d16 = Config.RATE_RAIDBOSS_XP * player.getRaidRateSp();
            double d17 = Config.RATE_DROP_ITEMS * player.getRateItems();
            double d18 = Config.RATE_DROP_ADENA * player.getRateAdena();
            double d19 = Config.RATE_DROP_SPOIL * player.getRateSpoil();
            double d20 = player.calcStat(Element.FIRE.getDefence(), 0.0, creature, null);
            double d21 = player.calcStat(Element.WIND.getDefence(), 0.0, creature, null);
            double d22 = player.calcStat(Element.WATER.getDefence(), 0.0, creature, null);
            double d23 = player.calcStat(Element.EARTH.getDefence(), 0.0, creature, null);
            double d24 = player.calcStat(Element.HOLY.getDefence(), 0.0, creature, null);
            double d25 = player.calcStat(Element.UNHOLY.getDefence(), 0.0, creature, null);
            double d26 = player.calcStat(Stats.BLEED_POWER, creature, null);
            double d27 = player.calcStat(Stats.BLEED_RESIST, creature, null);
            double d28 = player.calcStat(Stats.POISON_POWER, creature, null);
            double d29 = player.calcStat(Stats.POISON_RESIST, creature, null);
            double d30 = player.calcStat(Stats.STUN_POWER, creature, null);
            double d31 = player.calcStat(Stats.STUN_RESIST, creature, null);
            double d32 = player.calcStat(Stats.ROOT_POWER, creature, null);
            double d33 = player.calcStat(Stats.ROOT_RESIST, creature, null);
            double d34 = player.calcStat(Stats.SLEEP_POWER, creature, null);
            double d35 = player.calcStat(Stats.SLEEP_RESIST, creature, null);
            double d36 = player.calcStat(Stats.PARALYZE_POWER, creature, null);
            double d37 = player.calcStat(Stats.PARALYZE_RESIST, creature, null);
            double d38 = player.calcStat(Stats.MENTAL_POWER, creature, null);
            double d39 = player.calcStat(Stats.MENTAL_RESIST, creature, null);
            double d40 = player.calcStat(Stats.DEBUFF_POWER, creature, null);
            double d41 = player.calcStat(Stats.DEBUFF_RESIST, creature, null);
            double d42 = player.calcStat(Stats.CANCEL_POWER, creature, null);
            double d43 = player.calcStat(Stats.CANCEL_RESIST, creature, null);
            double d44 = 100.0 - player.calcStat(Stats.SWORD_WPN_VULNERABILITY, creature, null);
            double d45 = 100.0 - player.calcStat(Stats.DUAL_WPN_VULNERABILITY, creature, null);
            double d46 = 100.0 - player.calcStat(Stats.BLUNT_WPN_VULNERABILITY, creature, null);
            double d47 = 100.0 - player.calcStat(Stats.DAGGER_WPN_VULNERABILITY, creature, null);
            double d48 = 100.0 - player.calcStat(Stats.BOW_WPN_VULNERABILITY, creature, null);
            double d49 = 100.0 - player.calcStat(Stats.POLE_WPN_VULNERABILITY, creature, null);
            double d50 = 100.0 - player.calcStat(Stats.FIST_WPN_VULNERABILITY, creature, null);
            double d51 = 100.0 - player.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, creature, null);
            double d52 = player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, creature, null);
            double d53 = 100.0 - 100.0 * (player.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1.0, creature, null) - d52);
            double d54 = player.calcStat(Stats.SKILL_POWER, 1.0, creature, null);
            double d55 = player.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1.0, creature, null);
            double d56 = player.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1.0, creature, null);
            double d57 = player.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0, creature, null);
            double d58 = player.calcStat(Stats.PSKILL_EVASION, null, null);
            double d59 = player.calcStat(Stats.REFLECT_DAMAGE_PERCENT, creature, null);
            double d60 = player.calcStat(Stats.REFLECT_MAGIC_SKILL, creature, null);
            double d61 = player.calcStat(Stats.REFLECT_PHYSIC_SKILL, creature, null);
            double d62 = player.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, creature, null);
            double d63 = player.calcStat(Stats.PHYSIC_REUSE_RATE, creature, null);
            double d64 = player.calcStat(Stats.MAGIC_REUSE_RATE, creature, null);
            String string3 = HtmCache.getInstance().getNotNull("command/whoami.htm", player);
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
            numberFormat.setMaximumFractionDigits(1);
            numberFormat.setMinimumFractionDigits(1);
            StrBuilder strBuilder = new StrBuilder(string3);
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
            strBuilder.replaceFirst("%xpRate%", numberFormat.format(d13));
            strBuilder.replaceFirst("%spRate%", numberFormat.format(d14));
            strBuilder.replaceFirst("%xpRaidRate%", numberFormat.format(d15));
            strBuilder.replaceFirst("%spRaidRate%", numberFormat.format(d16));
            strBuilder.replaceFirst("%dropRate%", numberFormat.format(d17));
            strBuilder.replaceFirst("%adenaRate%", numberFormat.format(d18));
            strBuilder.replaceFirst("%spoilRate%", numberFormat.format(d19));
            strBuilder.replaceFirst("%fireResist%", numberFormat.format(d20));
            strBuilder.replaceFirst("%windResist%", numberFormat.format(d21));
            strBuilder.replaceFirst("%waterResist%", numberFormat.format(d22));
            strBuilder.replaceFirst("%earthResist%", numberFormat.format(d23));
            strBuilder.replaceFirst("%holyResist%", numberFormat.format(d24));
            strBuilder.replaceFirst("%darkResist%", numberFormat.format(d25));
            strBuilder.replaceFirst("%bleedPower%", numberFormat.format(d26));
            strBuilder.replaceFirst("%bleedResist%", numberFormat.format(d27));
            strBuilder.replaceFirst("%poisonPower%", numberFormat.format(d28));
            strBuilder.replaceFirst("%poisonResist%", numberFormat.format(d29));
            strBuilder.replaceFirst("%stunPower%", numberFormat.format(d30));
            strBuilder.replaceFirst("%stunResist%", numberFormat.format(d31));
            strBuilder.replaceFirst("%rootPower%", numberFormat.format(d32));
            strBuilder.replaceFirst("%rootResist%", numberFormat.format(d33));
            strBuilder.replaceFirst("%sleepPower%", numberFormat.format(d34));
            strBuilder.replaceFirst("%sleepResist%", numberFormat.format(d35));
            strBuilder.replaceFirst("%paralyzePower%", numberFormat.format(d36));
            strBuilder.replaceFirst("%paralyzeResist%", numberFormat.format(d37));
            strBuilder.replaceFirst("%mentalPower%", numberFormat.format(d38));
            strBuilder.replaceFirst("%mentalResist%", numberFormat.format(d39));
            strBuilder.replaceFirst("%debuffPower%", numberFormat.format(d40));
            strBuilder.replaceFirst("%debuffResist%", numberFormat.format(d41));
            strBuilder.replaceFirst("%cancelPower%", numberFormat.format(d42));
            strBuilder.replaceFirst("%cancelResist%", numberFormat.format(d43));
            strBuilder.replaceFirst("%swordResist%", numberFormat.format(d44));
            strBuilder.replaceFirst("%dualResist%", numberFormat.format(d45));
            strBuilder.replaceFirst("%bluntResist%", numberFormat.format(d46));
            strBuilder.replaceFirst("%daggerResist%", numberFormat.format(d47));
            strBuilder.replaceFirst("%bowResist%", numberFormat.format(d48));
            strBuilder.replaceFirst("%fistResist%", numberFormat.format(d50));
            strBuilder.replaceFirst("%poleResist%", numberFormat.format(d49));
            strBuilder.replaceFirst("%critChanceResist%", numberFormat.format(d51));
            strBuilder.replaceFirst("%critDamResist%", numberFormat.format(d53));
            strBuilder.replaceFirst("%SkillPower%", numberFormat.format(d54));
            strBuilder.replaceFirst("%PvPPhysDmg%", numberFormat.format(d55));
            strBuilder.replaceFirst("%PvPSkillDmg%", numberFormat.format(d56));
            strBuilder.replaceFirst("%MagicPvPSkillDmg%", numberFormat.format(d57));
            strBuilder.replaceFirst("%pSkillEvas%", numberFormat.format(d58));
            strBuilder.replaceFirst("%reflectDam%", numberFormat.format(d59));
            strBuilder.replaceFirst("%reflectSMagic%", numberFormat.format(d60));
            strBuilder.replaceFirst("%reflectSPhys%", numberFormat.format(d61));
            strBuilder.replaceFirst("%meleePhysRes%", numberFormat.format(d62));
            strBuilder.replaceFirst("%pReuse%", numberFormat.format(d63));
            strBuilder.replaceFirst("%mReuse%", numberFormat.format(d64));
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
            npcHtmlMessage.setHtml(Strings.bbParse(strBuilder.toString()));
            player.sendPacket((IStaticPacket)npcHtmlMessage);
            return false;
        }
        return true;
    }
}

