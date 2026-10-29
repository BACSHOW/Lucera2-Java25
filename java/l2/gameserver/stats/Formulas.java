/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.data.xml.holder.HitCondBonusHolder;
import l2.gameserver.data.xml.holder.KarmaIncreaseDataHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.HitCondBonusType;
import l2.gameserver.model.base.SkillTrait;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.PositionUtils;

public class Formulas {
    public static double calcHpRegen(Creature creature) {
        double d = creature.isPlayer() ? (creature.getLevel() <= 10 ? 1.5 + (double)creature.getLevel() / 20.0 : 1.4 + (double)creature.getLevel() / 10.0) * creature.getLevelMod() : creature.getTemplate().baseHpReg;
        if (creature.isPlayable()) {
            d *= BaseStats.CON.calcBonus(creature);
            if (creature.isSummon()) {
                d *= 2.0;
            }
        }
        return creature.calcStat(Stats.REGENERATE_HP_RATE, d, null, null);
    }

    public static double calcMpRegen(Creature creature) {
        double d = creature.isPlayer() ? (0.87 + (double)creature.getLevel() * 0.03) * creature.getLevelMod() : creature.getTemplate().baseMpReg;
        if (creature.isPlayable()) {
            d *= BaseStats.MEN.calcBonus(creature);
            if (creature.isSummon()) {
                d *= 2.0;
            }
        }
        return creature.calcStat(Stats.REGENERATE_MP_RATE, d, null, null);
    }

    public static double calcCpRegen(Creature creature) {
        double d = (1.5 + (double)(creature.getLevel() / 10)) * creature.getLevelMod() * BaseStats.CON.calcBonus(creature);
        return creature.calcStat(Stats.REGENERATE_CP_RATE, d, null, null);
    }

    public static AttackInfo calcPhysDam(Creature creature, Creature creature2, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int n;
        boolean bl5;
        AttackInfo attackInfo = new AttackInfo();
        attackInfo.damage = creature.getPAtk(creature2);
        attackInfo.defence = creature2.getPDef(creature);
        attackInfo.crit_static = creature.calcStat(Stats.CRITICAL_DAMAGE_STATIC, creature2, skill);
        attackInfo.death_rcpt = 0.01 * creature2.calcStat(Stats.DEATH_VULNERABILITY, creature, skill);
        attackInfo.lethal1 = skill == null ? 0.0 : skill.getLethal1() * attackInfo.death_rcpt;
        attackInfo.lethal2 = skill == null ? 0.0 : skill.getLethal2() * attackInfo.death_rcpt;
        attackInfo.crit = Rnd.chance(Formulas.calcCrit(creature, creature2, skill, bl2));
        attackInfo.shld = (skill == null || !skill.getShieldIgnore()) && Formulas.calcShldUse(creature, creature2);
        attackInfo.lethal = false;
        attackInfo.miss = false;
        boolean bl6 = bl5 = creature.isPlayable() && creature2.isPlayable();
        if (attackInfo.shld) {
            attackInfo.defence += (double)creature2.getShldDef();
        }
        attackInfo.defence = Math.max(attackInfo.defence, 1.0);
        if (skill != null) {
            if (!bl2 && !creature2.isLethalImmune()) {
                if (Rnd.chance(attackInfo.lethal1)) {
                    if (creature2.isPlayer()) {
                        attackInfo.lethal = true;
                        attackInfo.lethal_dmg = creature2.getCurrentCp();
                        creature2.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
                    } else {
                        attackInfo.lethal_dmg = creature2.getCurrentHp() / 2.0;
                    }
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                } else if (Rnd.chance(attackInfo.lethal2)) {
                    if (creature2.isPlayer()) {
                        attackInfo.lethal = true;
                        attackInfo.lethal_dmg = creature2.getCurrentHp() + creature2.getCurrentCp() - 1.1;
                        creature2.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
                    } else {
                        attackInfo.lethal_dmg = creature2.getCurrentHp() - 1.0;
                    }
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                }
            }
            if (skill.getPower(creature2) == 0.0) {
                attackInfo.damage = 0.0;
                return attackInfo;
            }
            if (bl2 && !skill.isBehind() && bl3) {
                attackInfo.damage *= 2.04;
            }
            attackInfo.damage = skill.isChargeBoost() ? creature.calcStat(Stats.SKILL_POWER, attackInfo.damage + skill.getPower(creature2), null, null) : (attackInfo.damage += creature.calcStat(Stats.SKILL_POWER, skill.getPower(creature2), null, null));
            if (bl2 && skill.isBehind() && bl3) {
                attackInfo.damage *= 1.5;
            }
            if (!skill.isChargeBoost()) {
                attackInfo.damage *= 1.0 + (Rnd.get() * (double)creature.getRandomDamage() * 2.0 - (double)creature.getRandomDamage()) / 100.0;
            }
            if (bl2) {
                attackInfo.damage *= 0.01 * creature.calcStat(Stats.CRITICAL_DAMAGE, creature2, skill);
                attackInfo.damage = creature2.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, attackInfo.damage, creature, skill);
                attackInfo.damage += 6.1 * attackInfo.crit_static;
            }
            if (skill.isChargeBoost()) {
                attackInfo.damage *= 0.8 + 0.2 * (double)(creature.getIncreasedForce() + Math.max(skill.getNumCharges(), 0));
            } else if (skill.isSoulBoost()) {
                attackInfo.damage *= 1.0 + 0.06 * (double)Math.min(creature.getConsumedSouls(), 5);
            }
            if (attackInfo.crit) {
                attackInfo.damage *= 2.0;
            }
        } else {
            attackInfo.damage *= 1.0 + (Rnd.get() * (double)creature.getRandomDamage() * 2.0 - (double)creature.getRandomDamage()) / 100.0;
            if (bl) {
                attackInfo.damage /= 2.0;
            }
            if (attackInfo.crit) {
                attackInfo.damage *= 0.01 * creature.calcStat(Stats.CRITICAL_DAMAGE, creature2, skill);
                attackInfo.damage = 2.0 * creature2.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, attackInfo.damage, creature, skill);
                attackInfo.damage += attackInfo.crit_static;
            }
        }
        if (attackInfo.crit && (n = creature.getSkillLevel(467)) > 0) {
            if (n >= 21) {
                n = 30;
            } else if (n >= 15) {
                n = 25;
            } else if (n >= 9) {
                n = 20;
            } else if (n >= 4) {
                n = 15;
            }
            if (Rnd.chance(n)) {
                creature.setConsumedSouls(creature.getConsumedSouls() + 1, null);
            }
        }
        if (skill == null || !skill.isChargeBoost()) {
            switch (PositionUtils.getDirectionTo(creature2, creature)) {
                case BEHIND: {
                    attackInfo.damage *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.back) / 100.0;
                    break;
                }
                case SIDE: {
                    attackInfo.damage *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.side) / 100.0;
                    break;
                }
                default: {
                    attackInfo.damage *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.ahead) / 100.0;
                }
            }
        }
        if (bl3) {
            attackInfo.damage = attackInfo.damage * (bl2 ? 1.0 : 2.0);
        }
        attackInfo.damage *= 70.0 / attackInfo.defence;
        attackInfo.damage = creature.calcStat(Stats.PHYSICAL_DAMAGE, attackInfo.damage, creature2, skill);
        if (attackInfo.shld && Rnd.chance(5)) {
            attackInfo.damage = 1.0;
        }
        if (bl5) {
            if (skill == null) {
                attackInfo.damage *= creature.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1.0, null, null);
                attackInfo.damage /= creature2.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1.0, null, null);
            } else {
                attackInfo.damage *= creature.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1.0, null, null);
                attackInfo.damage /= creature2.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1.0, null, null);
            }
        }
        if (skill != null) {
            if (attackInfo.shld) {
                if (attackInfo.damage == 1.0) {
                    creature2.sendPacket((IStaticPacket)SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                } else {
                    creature2.sendPacket((IStaticPacket)SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
                }
            }
            if (attackInfo.damage > 1.0 && !skill.hasNotSelfEffects() && Rnd.chance(creature2.calcStat(Stats.PSKILL_EVASION, 0.0, creature, skill))) {
                creature.sendPacket((IStaticPacket)new SystemMessage(43));
                creature2.sendPacket((IStaticPacket)new SystemMessage(42).addName(creature));
                attackInfo.damage = 0.0;
            }
            if (attackInfo.damage > 1.0 && skill.isDeathlink()) {
                attackInfo.damage *= 1.8 * (1.0 - creature.getCurrentHpRatio());
            }
            if (bl2) {
                if (!Formulas.calcBlow(creature, creature2, skill)) {
                    attackInfo.miss = true;
                    attackInfo.damage = 0.0;
                }
                if (Rnd.chance(attackInfo.lethal1)) {
                    if (creature2.isPlayer()) {
                        attackInfo.lethal = true;
                        attackInfo.lethal_dmg = creature2.getCurrentCp();
                        creature2.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
                    } else if (creature2.isLethalImmune()) {
                        attackInfo.damage *= 2.0;
                    } else {
                        attackInfo.lethal_dmg = creature2.getCurrentHp() / 2.0;
                    }
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                } else if (Rnd.chance(attackInfo.lethal2)) {
                    if (creature2.isPlayer()) {
                        attackInfo.lethal = true;
                        attackInfo.lethal_dmg = creature2.getCurrentHp() + creature2.getCurrentCp() - 1.1;
                        creature2.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
                    } else if (creature2.isLethalImmune()) {
                        attackInfo.damage *= 3.0;
                    } else {
                        attackInfo.lethal_dmg = creature2.getCurrentHp() - 1.0;
                    }
                    creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                }
            }
            if (attackInfo.damage > 0.0) {
                creature.displayGiveDamageMessage(creature2, (int)attackInfo.damage, attackInfo.crit || bl2, false, false, false);
            }
            if (creature2.isStunned() && Formulas.calcStunBreak(attackInfo.crit)) {
                creature2.getEffectList().stopEffects(EffectType.Stun);
            }
            if (Formulas.calcCastBreak(creature2, attackInfo.crit)) {
                creature2.abortCast(false, true);
            }
        }
        return attackInfo;
    }

    public static double calcMagicDam(Creature creature, Creature creature2, Skill skill, int n) {
        double d;
        boolean bl = creature.isPlayable() && creature2.isPlayable();
        boolean bl2 = skill.getShieldIgnore() && Formulas.calcShldUse(creature, creature2);
        double d2 = creature.getMAtk(creature2, skill);
        if (n == 2) {
            d2 *= 4.0;
        } else if (n == 1) {
            d2 *= 2.0;
        }
        double d3 = creature2.getMDef(null, skill);
        if (bl2) {
            d3 += (double)creature2.getShldDef();
        }
        if (d3 == 0.0) {
            d3 = 1.0;
        }
        double d4 = skill.getPower(creature2);
        double d5 = 0.0;
        if (Rnd.chance(skill.getLethal1())) {
            if (creature2.isPlayer()) {
                d5 = creature2.getCurrentCp();
                creature2.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
            } else if (!creature2.isLethalImmune()) {
                d5 = creature2.getCurrentHp() / 2.0;
            } else {
                d4 *= 2.0;
            }
            creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        } else if (Rnd.chance(skill.getLethal2())) {
            if (creature2.isPlayer()) {
                d5 = creature2.getCurrentHp() + creature2.getCurrentCp() - 1.1;
                creature2.sendPacket((IStaticPacket)SystemMsg.LETHAL_STRIKE);
            } else if (!creature2.isLethalImmune()) {
                d5 = creature2.getCurrentHp() - 1.0;
            } else {
                d4 *= 3.0;
            }
            creature.sendPacket((IStaticPacket)SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
        }
        if (d4 == 0.0) {
            if (d5 > 0.0) {
                creature.displayGiveDamageMessage(creature2, (int)d5, false, false, false, false);
            }
            return d5;
        }
        if (skill.isSoulBoost()) {
            d4 *= 1.0 + 0.06 * (double)Math.min(creature.getConsumedSouls(), 5);
        }
        double d6 = 91.0 * d4 * Math.sqrt(d2) / d3;
        boolean bl3 = Formulas.calcMCrit(creature, creature2, creature.getMagicCriticalRate(creature2, skill));
        if (bl3) {
            d6 *= creature.calcStat(Stats.MCRITICAL_DAMAGE, creature.isPlayable() && creature2.isPlayable() ? Config.MCRITICAL_CRIT_POWER : Config.MCRITICAL_CRIT_POWER, creature2, skill);
        }
        d6 = creature.calcStat(Stats.MAGIC_DAMAGE, d6, creature2, skill);
        if (bl2) {
            if (Rnd.chance(5)) {
                d6 = 0.0;
                creature2.sendPacket((IStaticPacket)SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                creature.sendPacket((IStaticPacket)new SystemMessage(159).addName(creature));
            } else {
                creature2.sendPacket((IStaticPacket)SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
                creature.sendPacket((IStaticPacket)new SystemMessage(2151));
            }
        }
        int n2 = skill.getMagicLevel() == 0 ? creature.getLevel() : skill.getMagicLevel();
        int n3 = creature2.getLevel() - n2;
        if (d6 > 1.0 && skill.isDeathlink()) {
            d6 *= 1.8 * (1.0 - creature.getCurrentHpRatio());
        }
        if (d6 > 1.0 && skill.isBasedOnTargetDebuff()) {
            d6 *= 1.0 + 0.05 * (double)Math.min(36, creature2.getEffectList().getAllEffects().size());
        }
        d6 += d5;
        if (skill.getSkillType() == Skill.SkillType.MANADAM) {
            d6 = Math.max(1.0, d6 / 4.0);
        }
        if (bl && d6 > 1.0) {
            d6 *= creature.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0, null, null);
            d6 /= creature2.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1.0, null, null);
        }
        boolean bl4 = creature.isPlayer() && ((Player)creature).getGradePenalty() > 0;
        double d7 = 4.0 * Math.max(1.0, creature2.getLevel() >= 80 ? (double)(n3 - 4) * 1.6 : (double)((n3 - Config.MAGIC_FAIL_LEVEL_MOD) * 2));
        double d8 = creature2.calcStat(Stats.MAGIC_RESIST, creature, skill) - creature.calcStat(Stats.MAGIC_POWER, creature2, skill);
        double d9 = bl4 ? 95.0 : Math.min(d7 * (1.0 + d8 / 100.0), 95.0);
        double d10 = d = bl4 ? 95.0 : (double)(5 * Math.max(n3 - 10, 1));
        if (creature.isPlayer() && ((Player)creature).isDebug()) {
            creature.sendMessage("Fail chance " + (int)d9 + "/" + (int)d);
        }
        if (Rnd.chance(d9) && !Config.ALT_DISABLE_MAGICFAILURES) {
            if (Rnd.chance(d)) {
                d6 = 0.0;
                SystemMessage systemMessage = new SystemMessage(158);
                creature.sendPacket((IStaticPacket)systemMessage);
                SystemMessage systemMessage2 = new SystemMessage(159).addName(creature);
                creature2.sendPacket((IStaticPacket)systemMessage2);
            } else {
                d6 /= 2.0;
                SystemMessage systemMessage = new SystemMessage(158);
                creature.sendPacket((IStaticPacket)systemMessage);
                SystemMessage systemMessage3 = new SystemMessage(159).addName(creature);
                creature2.sendPacket((IStaticPacket)systemMessage3);
            }
        }
        if (d6 > 0.0) {
            creature.displayGiveDamageMessage(creature2, (int)d6, bl3, false, false, true);
        }
        if (Formulas.calcCastBreak(creature2, bl3)) {
            creature2.abortCast(false, true);
        }
        return d6;
    }

    public static boolean calcStunBreak(boolean bl) {
        return Rnd.chance(bl ? 75 : 10);
    }

    public static boolean calcBlow(Creature creature, Creature creature2, Skill skill) {
        WeaponTemplate weaponTemplate = creature.getActiveWeaponItem();
        int n = creature.getZ() - creature2.getZ();
        if (n < -25) {
            n = -25;
        } else if (n > 25) {
            n = 25;
        }
        int n2 = 1 + (n * 4 / 5 + 10) / 100;
        double d = BaseStats.DEX.calcBonus(creature);
        double d2 = weaponTemplate == null ? 4.0 : (double)weaponTemplate.getCritical();
        double d3 = creature.calcStat(Stats.FATALBLOW_RATE, creature2, skill);
        double d4 = (skill.getBaseBlowRate() + 100.0) / 100.0;
        double d5 = d2 * d * (double)n2 * d4 * d3;
        switch (PositionUtils.getDirectionTo(creature2, creature)) {
            case BEHIND: {
                if (skill.isBehind()) {
                    d5 = 100.0;
                    break;
                }
                d5 *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.back) / 100.0;
                break;
            }
            case SIDE: {
                if (skill.isBehind()) {
                    d5 = 50.0;
                    break;
                }
                d5 *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.side) / 100.0;
                break;
            }
            case FRONT: {
                if (skill.isBehind()) {
                    d5 = 3.0;
                    break;
                }
                d5 *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.ahead) / 100.0;
            }
        }
        d5 = Math.min(skill.isBehind() ? 100.0 : Config.BLOW_SKILLS_MAX_CHANCE_LIMIT, d5);
        return Rnd.chance(d5);
    }

    public static double calcCrit(Creature creature, Creature creature2, Skill skill, boolean bl) {
        if (creature.isPlayer() && creature.getActiveWeaponItem() == null) {
            return 0.0;
        }
        if (skill != null) {
            return (double)skill.getCriticalRate() * (bl ? BaseStats.DEX.calcBonus(creature) : BaseStats.STR.calcBonus(creature)) * 0.01 * creature.calcStat(Stats.SKILL_CRIT_CHANCE_MOD, creature2, skill);
        }
        double d = (double)creature.getCriticalHit(creature2, null) * 0.01 * creature2.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, creature, skill);
        switch (PositionUtils.getDirectionTo(creature2, creature)) {
            case BEHIND: {
                d *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.back) / 100.0;
                break;
            }
            case SIDE: {
                d *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.side) / 100.0;
                break;
            }
            default: {
                d *= 1.0 + HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.ahead) / 100.0;
            }
        }
        return d / 10.0;
    }

    public static boolean calcMCrit(Creature creature, Creature creature2, double d) {
        if (creature != null && creature.isNpc()) {
            return Rnd.get() * 100.0 <= Math.min((double)Config.ALT_NPC_LIM_MCRIT, d);
        }
        return Rnd.get() * 100.0 <= Math.min((double)Config.LIM_MCRIT, d);
    }

    public static boolean calcCastBreak(Creature creature, boolean bl) {
        if (creature == null || creature.isInvul() || creature.isRaid() || !creature.isCastingNow()) {
            return false;
        }
        Skill skill = creature.getCastingSkill();
        if (!(skill == null || skill.isMagic() && skill.getSkillType() != Skill.SkillType.TAKECASTLE)) {
            return false;
        }
        return Rnd.chance(creature.calcStat(Stats.CAST_INTERRUPT, bl ? 75.0 : 10.0, null, skill));
    }

    public static int calcPAtkSpd(double d) {
        double d2 = 500.0 / d;
        int n = (int)(d2 * 1000.0 * 0.9777777791023254);
        if (d2 * 1000.0 > (double)n) {
            n += (int)((double)(-n) - d2 * -1000.0);
        }
        return n;
    }

    public static int calcMAtkSpd(Creature creature, Skill skill, double d) {
        if (skill.isMagic()) {
            return (int)(d * 333.0 / (double)Math.max(creature.getMAtkSpd(), 1));
        }
        return (int)(d * 333.0 / (double)Math.max(creature.getPAtkSpd(), 1));
    }

    public static long calcSkillReuseDelay(Creature creature, Skill skill) {
        long l = skill.getReuseDelay();
        if (creature.isMonster()) {
            l = skill.getReuseForMonsters();
        }
        if (skill.isReuseDelayPermanent() || skill.isHandler() || skill.isItemSkill()) {
            return l;
        }
        if (creature.getSkillMastery(skill.getId()) == 1) {
            creature.removeSkillMastery(skill.getId());
            return 0L;
        }
        if (skill.isMusic()) {
            return (long)creature.calcStat(Stats.MUSIC_REUSE_RATE, l, null, skill) * 333L / (long)Math.max(creature.getMAtkSpd(), 1);
        }
        if (skill.isMagic()) {
            return (long)creature.calcStat(Stats.MAGIC_REUSE_RATE, l, null, skill) * 333L / (long)Math.max(creature.getMAtkSpd(), 1);
        }
        return (long)creature.calcStat(Stats.PHYSIC_REUSE_RATE, l, null, skill) * 333L / (long)Math.max(creature.getPAtkSpd(), 1);
    }

    public static boolean calcHitMiss(Creature creature, Creature creature2) {
        int n = (88 + 2 * (creature.getAccuracy() - creature2.getEvasionRate(creature))) * 10;
        double d = 100.0;
        PositionUtils.TargetDirection targetDirection = PositionUtils.getDirectionTo(creature, creature2);
        if (creature.getZ() - creature2.getZ() > 50) {
            d += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.high);
        } else if (creature.getZ() - creature2.getZ() < -50) {
            d += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.low);
        }
        if (GameTimeController.getInstance().isNowNight()) {
            d += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.dark);
        }
        d = targetDirection == PositionUtils.TargetDirection.BEHIND ? (d += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.back)) : (targetDirection == PositionUtils.TargetDirection.FRONT ? (d += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.ahead)) : (d += HitCondBonusHolder.getInstance().getHitCondBonus(HitCondBonusType.side)));
        double d2 = Math.max(d / 100.0, 0.0);
        n = (int)((double)n + d2);
        n = Math.max(n, 280);
        n = Math.min(n, 980);
        return n < Rnd.get(1000);
    }

    public static boolean calcShldUse(Creature creature, Creature creature2) {
        WeaponTemplate weaponTemplate = creature2.getSecondaryWeaponItem();
        if (weaponTemplate == null || weaponTemplate.getItemType() != WeaponTemplate.WeaponType.NONE) {
            return false;
        }
        int n = (int)creature2.calcStat(Stats.SHIELD_ANGLE, creature, null);
        if (!PositionUtils.isFacing(creature2, creature, n)) {
            return false;
        }
        return Rnd.chance((int)creature2.calcStat(Stats.SHIELD_RATE, creature, null));
    }

    public static boolean calcSkillSuccess(Env env, EffectTemplate effectTemplate, int n) {
        double d;
        if (env.value == -1.0) {
            return true;
        }
        double d2 = env.value = Math.max(Math.min(env.value, 100.0), 1.0);
        Skill skill = env.skill;
        if (!skill.isOffensive()) {
            return Rnd.chance(env.value);
        }
        Creature creature = env.character;
        Creature creature2 = env.target;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        if (Config.ALT_DEBUG_ENABLED) {
            bl = creature.getPlayer() != null && creature.getPlayer().isDebug();
            bl2 = creature2.getPlayer() != null && creature2.getPlayer().isDebug();
            boolean bl4 = Config.ALT_DEBUG_PVP_ENABLED && bl && bl2 && (!Config.ALT_DEBUG_PVP_DUEL_ONLY || creature.getPlayer().isInDuel() && creature2.getPlayer().isInDuel());
            bl3 = bl4 || Config.ALT_DEBUG_PVE_ENABLED && (bl && creature2.isMonster() || bl2 && creature.isMonster());
        }
        double d3 = 1.0;
        if (skill.getSaveVs() != null) {
            d3 = skill.getSaveVs().calcChanceMod(creature2);
            env.value *= d3;
        }
        env.value = Math.max(env.value, 1.0);
        double d4 = 1.0;
        int n2 = 0;
        if (skill.isMagic()) {
            int n3 = Math.max(1, creature2.getMDef(creature2, skill));
            double d5 = creature.getMAtk(creature2, skill);
            if (skill.isSSPossible()) {
                switch (n) {
                    case 2: {
                        n2 = 4;
                        break;
                    }
                    case 1: {
                        n2 = 2;
                        break;
                    }
                    default: {
                        n2 = 1;
                    }
                }
                d5 *= (double)n2;
            }
            d4 = Config.SKILLS_CHANCE_MOD * Math.pow(d5, Config.SKILLS_CHANCE_POW) / (double)n3;
            env.value *= d4;
            env.value = Math.max(env.value, 1.0);
        }
        if ((d = (double)skill.getLevelModifier()) != 0.0) {
            int n4 = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : creature.getLevel();
            d = 1.0 + (double)(n4 - creature2.getLevel()) * 0.03 * d;
            if (d < 0.0) {
                d = 0.0;
            } else if (d > 2.0) {
                d = 2.0;
            }
            env.value *= d;
        }
        double d6 = 0.0;
        double d7 = 0.0;
        double d8 = 1.0;
        double d9 = 1.0;
        if (!skill.isIgnoreResists()) {
            SkillTrait skillTrait;
            d9 = 1.0 - creature2.calcStat(Stats.DEBUFF_RESIST, creature, skill) / 120.0;
            if (d9 != 1.0) {
                if (d9 == Double.NEGATIVE_INFINITY) {
                    if (bl3) {
                        if (bl) {
                            creature.getPlayer().sendMessage("Full debuff immunity");
                        }
                        if (bl2) {
                            creature2.getPlayer().sendMessage("Full debuff immunity");
                        }
                    }
                    return false;
                }
                if (d9 == Double.POSITIVE_INFINITY) {
                    if (bl3) {
                        if (bl) {
                            creature.getPlayer().sendMessage("Full debuff vulnerability");
                        }
                        if (bl2) {
                            creature2.getPlayer().sendMessage("Full debuff vulnerability");
                        }
                    }
                    return true;
                }
                d9 = Math.max(d9, 0.0);
                env.value *= d9;
            }
            if ((skillTrait = skill.getTraitType()) != null) {
                d6 = skillTrait.calcVuln(env);
                d7 = skillTrait.calcProf(env);
                double d10 = 90.0 + d7 * 0.85;
                d8 = (d10 - d6) / 60.0;
            }
            if (d8 != 1.0) {
                if (d8 == Double.NEGATIVE_INFINITY) {
                    if (bl3) {
                        if (bl) {
                            creature.getPlayer().sendMessage("Full immunity");
                        }
                        if (bl2) {
                            creature2.getPlayer().sendMessage("Full immunity");
                        }
                    }
                    return false;
                }
                if (d8 == Double.POSITIVE_INFINITY) {
                    if (bl3) {
                        if (bl) {
                            creature.getPlayer().sendMessage("Full vulnerability");
                        }
                        if (bl2) {
                            creature2.getPlayer().sendMessage("Full vulnerability");
                        }
                    }
                    return true;
                }
                d8 = Math.max(d8, 0.0);
                env.value *= d8;
            }
        }
        double d11 = 0.0;
        Element element = skill.getElement();
        if (element != Element.NONE) {
            d11 = skill.getElementPower();
            Element element2 = Formulas.getAttackElement(creature, creature2);
            if (element2 == element) {
                d11 += creature.calcStat(element.getAttack(), 0.0);
            }
            d11 -= creature2.calcStat(element.getDefence(), 0.0);
            d11 = Math.round(d11 / 10.0);
            env.value += d11;
        }
        env.value = Math.max(env.value, Math.min(d2, Config.SKILLS_CHANCE_MIN));
        env.value = Math.max(Math.min(env.value, Config.SKILLS_CHANCE_CAP), 1.0);
        boolean bl5 = Rnd.chance((int)env.value);
        if (bl3) {
            StringBuilder stringBuilder = new StringBuilder(100);
            if (effectTemplate == null) {
                stringBuilder.append(skill.getName());
            } else {
                stringBuilder.append(effectTemplate._effectType.name());
            }
            stringBuilder.append(" AR:");
            stringBuilder.append((int)d2);
            stringBuilder.append(" ");
            if (skill.getSaveVs() != null) {
                stringBuilder.append(skill.getSaveVs().name());
                stringBuilder.append(":");
                stringBuilder.append(String.format("%1.1f", d3));
            }
            if (skill.isMagic()) {
                stringBuilder.append(" ");
                stringBuilder.append(" mAtk:");
                stringBuilder.append(String.format("%1.1f", d4));
            }
            if (skill.getTraitType() != null) {
                stringBuilder.append(" ");
                stringBuilder.append(skill.getTraitType().name());
            }
            stringBuilder.append(" ");
            stringBuilder.append(String.format("%1.1f", d8));
            stringBuilder.append("(");
            stringBuilder.append(String.format("%1.1f", d7));
            stringBuilder.append("/");
            stringBuilder.append(String.format("%1.1f", d6));
            if (d9 != 0.0) {
                stringBuilder.append("+");
                stringBuilder.append(String.format("%1.1f", d9));
            }
            stringBuilder.append(") lvl:");
            stringBuilder.append(String.format("%1.1f", d));
            stringBuilder.append(" elem:");
            stringBuilder.append((int)d11);
            stringBuilder.append(" Chance:");
            stringBuilder.append(String.format("%1.1f", env.value));
            if (!bl5) {
                stringBuilder.append(" failed");
            }
            if (bl) {
                creature.getPlayer().sendMessage(stringBuilder.toString());
            }
            if (bl2) {
                creature2.getPlayer().sendMessage(stringBuilder.toString());
            }
        }
        return bl5;
    }

    public static boolean calcSkillSuccess(Creature creature, Creature creature2, Skill skill, int n) {
        Env env = new Env();
        env.character = creature;
        env.target = creature2;
        env.skill = skill;
        env.value = n;
        return Formulas.calcSkillSuccess(env, null, creature.getChargedSpiritShot());
    }

    public static void calcSkillMastery(Skill skill, Creature creature) {
        if (skill.isHandler()) {
            return;
        }
        if (creature.getSkillLevel(331) > 0 && creature.calcStat(Stats.SKILL_MASTERY, creature.getINT(), null, skill) >= (double)Rnd.get(5000) || creature.getSkillLevel(330) > 0 && creature.calcStat(Stats.SKILL_MASTERY, creature.getSTR(), null, skill) >= (double)Rnd.get(5000)) {
            Skill.SkillType skillType = skill.getSkillType();
            int n = skill.isMusic() || skillType == Skill.SkillType.BUFF || skillType == Skill.SkillType.HOT || skillType == Skill.SkillType.HEAL_PERCENT ? 2 : (skillType == Skill.SkillType.HEAL ? 3 : 1);
            if (n > 0) {
                creature.setSkillMastery(skill.getId(), n);
            }
        }
    }

    public static double calcDamageResists(Skill skill, Creature creature, Creature creature2, double d) {
        if (creature == creature2) {
            return d;
        }
        if (creature.isBoss()) {
            d *= Config.RATE_EPIC_ATTACK;
        } else if (creature.isRaid() && creature.getLevel() >= Config.RATE_MOD_MIN_LEVEL_LIMIT && creature.getLevel() <= Config.RATE_MOD_MAX_LEVEL_LIMIT) {
            d *= Config.RATE_RAID_ATTACK;
        }
        if (creature2.isBoss()) {
            d /= Config.RATE_EPIC_DEFENSE;
        } else if (creature2.isRaid() && creature2.getLevel() >= Config.RATE_MOD_MIN_LEVEL_LIMIT && creature2.getLevel() <= Config.RATE_MOD_MAX_LEVEL_LIMIT) {
            d /= Config.RATE_RAID_DEFENSE;
        }
        Player player = creature.getPlayer();
        Element element = Element.NONE;
        double d2 = 0.0;
        if (skill != null) {
            element = skill.getElement();
            d2 = skill.getElementPower();
        } else {
            element = Formulas.getAttackElement(creature, creature2);
        }
        if (element == Element.NONE) {
            return d;
        }
        if (player != null && player.isGM() && Config.DEBUG) {
            player.sendMessage("Element: " + element.name());
            player.sendMessage("Attack: " + creature.calcStat(element.getAttack(), d2));
            player.sendMessage("Defence: " + creature2.calcStat(element.getDefence(), 0.0));
            player.sendMessage("Modifier: " + Formulas.c(creature2.calcStat(element.getDefence(), 0.0), creature.calcStat(element.getAttack(), d2)));
        }
        return d * Formulas.c(creature2.calcStat(element.getDefence(), 0.0), creature.calcStat(element.getAttack(), d2));
    }

    private static double c(double d, double d2) {
        if (d < 0.0) {
            d2 += -d;
            d = 0.0;
        }
        double d3 = 1.0 + d2 / 100.0;
        double d4 = 1.0 + d / 100.0;
        return d3 / d4;
    }

    public static Element getAttackElement(Creature creature, Creature creature2) {
        double d = Double.MIN_VALUE;
        Element element = Element.NONE;
        for (Element element2 : Element.VALUES) {
            double d2 = creature.calcStat(element2.getAttack(), 0.0, null, null);
            if (d2 <= 0.0) continue;
            if (creature2 != null) {
                d2 -= creature2.calcStat(element2.getDefence(), 0.0, null, null);
            }
            if (!(d2 > d)) continue;
            element = element2;
            d = d2;
        }
        return element;
    }

    public static int calculateKarmaLost(Player player, long l) {
        int n;
        int n2 = player.getKarma();
        int n3 = Config.RATE_KARMA_LOST_STATIC;
        if (n3 != -1) {
            n = n3;
        } else {
            double d = KarmaIncreaseDataHolder.getInstance().getData(player.getLevel());
            int n4 = Config.KARMA_RATE_LOST;
            if (l > 0L) {
                l = (long)((double)l * (n4 == -1 ? Config.RATE_XP : (double)n4));
            }
            n = (int)((double)Math.abs(l) / d / 15.0);
        }
        int n5 = Math.max(n2 - n, 0);
        player.sendPacket((IStaticPacket)new SystemMessage(1282).addNumber(n5));
        return n;
    }

    /**
     * Dhousefe-style DEX Attack Recovery Scaling.
     * Reduces attack reuse delay proportionally for high DEX characters (archers/daggers),
     * enabling fluid stutter-step and responsive movement.
     */
    public static double calcDexAttackRecoveryFactor(Creature attacker) {
        if (!Config.ENABLE_DEX_ATTACK_RECOVERY_SCALING || attacker == null || !attacker.isPlayer()) {
            return 1.0;
        }
        return calculateDexFactor(attacker.getDEX());
    }

    public static double calculateDexFactor(int dex) {
        if (!Config.ENABLE_DEX_ATTACK_RECOVERY_SCALING) {
            return 1.0;
        }
        double scaled = 1.0 - (((double) (dex - Config.DEX_ATTACK_RECOVERY_BASE_DEX) / Config.DEX_ATTACK_RECOVERY_DEX_DIVISOR) * Config.DEX_ATTACK_RECOVERY_MAX_BONUS);
        return Math.clamp(scaled, Config.DEX_ATTACK_RECOVERY_MIN_FACTOR, Config.DEX_ATTACK_RECOVERY_MAX_FACTOR);
    }

    /**
     * L2OFF Dhousefe Compliance: Stat Caps para Combate balanceado.
     */
    public static int clampBlowRate(int blowRate, boolean isBackstab) {
        int maxRate = isBackstab ? Config.BLOW_RATE_CAP_BACKSTAB : Config.BLOW_RATE_CAP_GENERAL;
        return Math.min(blowRate, maxRate);
    }

    public static double clampPhysicalCritRate(double rate) {
        return Math.min(rate, (double) Config.PHYSICAL_CRITICAL_RATE_CAP);
    }

    public static double clampMagicCritRate(double rate) {
        return Math.min(rate, Config.MAGIC_CRITICAL_RATE_CAP);
    }

    public static class AttackInfo {
        public double damage = 0.0;
        public double defence = 0.0;
        public double crit_static = 0.0;
        public double death_rcpt = 0.0;
        public double lethal1 = 0.0;
        public double lethal2 = 0.0;
        public double lethal_dmg = 0.0;
        public boolean crit = false;
        public boolean shld = false;
        public boolean lethal = false;
        public boolean miss = false;
    }
}

