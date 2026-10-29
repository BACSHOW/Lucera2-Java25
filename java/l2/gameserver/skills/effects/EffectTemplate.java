/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.EffectList;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.templates.StatsSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EffectTemplate
extends StatTemplate {
    private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);
    public static final EffectTemplate[] EMPTY_ARRAY = new EffectTemplate[0];
    public static final String NO_STACK = "none";
    public static final String HP_RECOVER_CAST = "HpRecoverCast";
    public static final int CANCEL_ON_ITEM_UNEQUIP_WEAPON = 1;
    public static final int CANCEL_ON_ITEM_UNEQUIP_ARMOR = 2;
    public Condition _attachCond;
    public final double _value;
    public final int _count;
    public final long _period;
    public AbnormalEffect _abnormalEffect;
    public AbnormalEffect _abnormalEffect2;
    public AbnormalEffect _abnormalEffect3;
    public final EffectType _effectType;
    public final String _stackType;
    public final String _stackType2;
    public final int _stackOrder;
    public final int _displayId;
    public final int _displayLevel;
    public final boolean _applyOnCaster;
    public final boolean _applyOnSummon;
    public final boolean _cancelOnAction;
    public final int _cancelOnItem;
    public final boolean _isReflectable;
    private final Boolean aoR;
    private final Boolean aoS;
    private final Boolean aoT;
    private final StatsSet aoU;
    private final int cJ;

    public EffectTemplate(StatsSet statsSet) {
        this._value = statsSet.getDouble("value");
        this._count = statsSet.getInteger("count", 1) < 0 ? Integer.MAX_VALUE : statsSet.getInteger("count", 1);
        this._period = Math.min(Integer.MAX_VALUE, 1000 * (statsSet.getInteger("time", 1) < 0 ? Integer.MAX_VALUE : statsSet.getInteger("time", 1)));
        this._abnormalEffect = statsSet.getEnum("abnormal", AbnormalEffect.class);
        this._abnormalEffect2 = statsSet.getEnum("abnormal2", AbnormalEffect.class);
        this._abnormalEffect3 = statsSet.getEnum("abnormal3", AbnormalEffect.class);
        this._stackType = statsSet.getString("stackType", NO_STACK);
        this._stackType2 = statsSet.getString("stackType2", NO_STACK);
        this._stackOrder = statsSet.getInteger("stackOrder", this._stackType.equals(NO_STACK) && this._stackType2.equals(NO_STACK) ? 1 : 0);
        this._applyOnCaster = statsSet.getBool("applyOnCaster", false);
        this._applyOnSummon = statsSet.getBool("applyOnSummon", false);
        this._cancelOnAction = statsSet.getBool("cancelOnAction", false);
        String string = StringUtils.upperCase(statsSet.getString("cancelOnItemSwitch", ""));
        this._cancelOnItem = (string.contains("WEAPON") ? 1 : 0) | (string.contains("ARMOR") ? 2 : 0);
        this._isReflectable = statsSet.getBool("isReflectable", true);
        this.aoR = statsSet.isSet("isSaveable") ? Boolean.valueOf(statsSet.getBool("isSaveable")) : null;
        this.aoS = statsSet.isSet("isCancelable") ? Boolean.valueOf(statsSet.getBool("isCancelable")) : null;
        this.aoT = statsSet.isSet("isOffensive") ? Boolean.valueOf(statsSet.getBool("isOffensive")) : null;
        this._displayId = statsSet.getInteger("displayId", 0);
        this._displayLevel = statsSet.getInteger("displayLevel", 0);
        this._effectType = statsSet.getEnum("name", EffectType.class);
        this.cJ = statsSet.getInteger("chance", Integer.MAX_VALUE);
        this.aoU = statsSet;
    }

    public Effect getEffect(Env env) {
        if (this._attachCond != null && !this._attachCond.test(env)) {
            return null;
        }
        try {
            return this._effectType.makeEffect(env, this);
        }
        catch (Exception exception) {
            _log.error("", exception);
            return null;
        }
    }

    public void attachCond(Condition condition) {
        this._attachCond = condition;
    }

    public int getCount() {
        return this._count;
    }

    public long getPeriod() {
        return this._period;
    }

    public EffectType getEffectType() {
        return this._effectType;
    }

    public Effect getSameByStackType(List<Effect> list) {
        for (Effect effect : list) {
            if (effect == null || !EffectList.checkStackType(effect.getTemplate(), this)) continue;
            return effect;
        }
        return null;
    }

    public Effect getSameByStackType(EffectList effectList) {
        return this.getSameByStackType(effectList.getAllEffects());
    }

    public Effect getSameByStackType(Creature creature) {
        return this.getSameByStackType(creature.getEffectList().getAllEffects());
    }

    public StatsSet getParam() {
        return this.aoU;
    }

    public int chance(int n) {
        return this.cJ == Integer.MAX_VALUE ? n : this.cJ;
    }

    public boolean isSaveable(boolean bl) {
        return this.aoR != null ? this.aoR : bl;
    }

    public boolean isCancelable(boolean bl) {
        return this.aoS != null ? this.aoS : bl;
    }

    public boolean isOffensive(boolean bl) {
        return this.aoT != null ? this.aoT : bl;
    }
}

