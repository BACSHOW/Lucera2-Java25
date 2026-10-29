/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import l2.gameserver.templates.StatsSet;

public class CharTemplate {
    public static final int[] EMPTY_ATTRIBUTES = new int[6];
    public final int baseSTR;
    public final int baseCON;
    public final int baseDEX;
    public final int baseINT;
    public final int baseWIT;
    public final int baseMEN;
    public final double baseHpMax;
    public final double baseCpMax;
    public final double baseMpMax;
    public final double baseHpReg;
    public final double baseMpReg;
    public final double baseCpReg;
    public final int basePAtk;
    public final int baseMAtk;
    public final int basePDef;
    public final int baseMDef;
    public final int basePAtkSpd;
    public final int baseMAtkSpd;
    public final int baseShldDef;
    public final int baseAtkRange;
    public final int baseShldRate;
    public final int baseCritRate;
    public final int baseRunSpd;
    public final int baseWalkSpd;
    public final int[] baseAttributeAttack;
    public final int[] baseAttributeDefence;
    public final double collisionRadius;
    public final double collisionHeight;

    public CharTemplate(StatsSet statsSet) {
        this.baseSTR = statsSet.getInteger("baseSTR");
        this.baseCON = statsSet.getInteger("baseCON");
        this.baseDEX = statsSet.getInteger("baseDEX");
        this.baseINT = statsSet.getInteger("baseINT");
        this.baseWIT = statsSet.getInteger("baseWIT");
        this.baseMEN = statsSet.getInteger("baseMEN");
        this.baseHpMax = statsSet.getDouble("baseHpMax");
        this.baseCpMax = statsSet.getDouble("baseCpMax");
        this.baseMpMax = statsSet.getDouble("baseMpMax");
        this.baseHpReg = statsSet.getDouble("baseHpReg");
        this.baseCpReg = statsSet.getDouble("baseCpReg");
        this.baseMpReg = statsSet.getDouble("baseMpReg");
        this.basePAtk = statsSet.getInteger("basePAtk");
        this.baseMAtk = statsSet.getInteger("baseMAtk");
        this.basePDef = statsSet.getInteger("basePDef");
        this.baseMDef = statsSet.getInteger("baseMDef");
        this.basePAtkSpd = statsSet.getInteger("basePAtkSpd");
        this.baseMAtkSpd = statsSet.getInteger("baseMAtkSpd");
        this.baseShldDef = statsSet.getInteger("baseShldDef");
        this.baseAtkRange = statsSet.getInteger("baseAtkRange");
        this.baseShldRate = statsSet.getInteger("baseShldRate");
        this.baseCritRate = statsSet.getInteger("baseCritRate");
        this.baseRunSpd = statsSet.getInteger("baseRunSpd");
        this.baseWalkSpd = statsSet.getInteger("baseWalkSpd");
        this.baseAttributeAttack = statsSet.getIntegerArray("baseAttributeAttack", EMPTY_ATTRIBUTES);
        this.baseAttributeDefence = statsSet.getIntegerArray("baseAttributeDefence", EMPTY_ATTRIBUTES);
        this.collisionRadius = statsSet.getDouble("collision_radius", 5.0);
        this.collisionHeight = statsSet.getDouble("collision_height", 5.0);
    }

    public int getNpcId() {
        return 0;
    }

    public static StatsSet getEmptyStatsSet() {
        StatsSet statsSet = new StatsSet();
        statsSet.set("baseSTR", 0);
        statsSet.set("baseCON", 0);
        statsSet.set("baseDEX", 0);
        statsSet.set("baseINT", 0);
        statsSet.set("baseWIT", 0);
        statsSet.set("baseMEN", 0);
        statsSet.set("baseHpMax", 0);
        statsSet.set("baseCpMax", 0);
        statsSet.set("baseMpMax", 0);
        statsSet.set("baseHpReg", 0.003f);
        statsSet.set("baseCpReg", 0);
        statsSet.set("baseMpReg", 0.003f);
        statsSet.set("basePAtk", 0);
        statsSet.set("baseMAtk", 0);
        statsSet.set("basePDef", 100);
        statsSet.set("baseMDef", 100);
        statsSet.set("basePAtkSpd", 0);
        statsSet.set("baseMAtkSpd", 0);
        statsSet.set("baseShldDef", 0);
        statsSet.set("baseAtkRange", 0);
        statsSet.set("baseShldRate", 0);
        statsSet.set("baseCritRate", 0);
        statsSet.set("baseRunSpd", 0);
        statsSet.set("baseWalkSpd", 0);
        return statsSet;
    }
}

