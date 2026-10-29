/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

import l2.commons.collections.MultiValueSet;
import l2.gameserver.templates.item.support.FishGrade;
import l2.gameserver.templates.item.support.FishGroup;

public class FishTemplate {
    private final FishGroup avg;
    private final FishGrade avh;
    private final double avi;
    private final double avj;
    private final double avk;
    private final double avl;
    private final double avm;
    private final double avn;
    private final int _itemId;
    private final int _hp;
    private final int _level;
    private final int avo;
    private final int avp;
    private final int avq;
    private final int avr;

    public FishTemplate(MultiValueSet<String> multiValueSet) {
        this.avg = multiValueSet.getEnum("group", FishGroup.class);
        this.avh = multiValueSet.getEnum("grade", FishGrade.class);
        this.avi = multiValueSet.getDouble("bite_rate");
        this.avj = multiValueSet.getDouble("guts");
        this.avk = multiValueSet.getDouble("length_rate");
        this.avl = multiValueSet.getDouble("hp_regen");
        this.avm = multiValueSet.getDouble("guts_check_probability");
        this.avn = multiValueSet.getDouble("cheating_prob");
        this._itemId = multiValueSet.getInteger("item_id");
        this._level = multiValueSet.getInteger("level");
        this._hp = multiValueSet.getInteger("hp");
        this.avo = multiValueSet.getInteger("max_length");
        this.avp = multiValueSet.getInteger("start_combat_time");
        this.avq = multiValueSet.getInteger("combat_duration");
        this.avr = multiValueSet.getInteger("guts_check_time");
    }

    public FishGroup getGroup() {
        return this.avg;
    }

    public FishGrade getGrade() {
        return this.avh;
    }

    public double getBiteRate() {
        return this.avi;
    }

    public double getGuts() {
        return this.avj;
    }

    public double getLengthRate() {
        return this.avk;
    }

    public double getHpRegen() {
        return this.avl;
    }

    public double getGutsCheckProbability() {
        return this.avm;
    }

    public double getCheatingProb() {
        return this.avn;
    }

    public int getItemId() {
        return this._itemId;
    }

    public int getHp() {
        return this._hp;
    }

    public int getLevel() {
        return this._level;
    }

    public int getMaxLength() {
        return this.avo;
    }

    public int getStartCombatTime() {
        return this.avp;
    }

    public int getCombatDuration() {
        return this.avq;
    }

    public int getGutsCheckTime() {
        return this.avr;
    }
}

