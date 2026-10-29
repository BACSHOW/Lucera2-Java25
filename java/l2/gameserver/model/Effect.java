/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.listener.actor.OnAttackListener;
import l2.gameserver.listener.actor.OnMagicUseListener;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.AbnormalStatusUpdate;
import l2.gameserver.network.l2.s2c.ExOlympiadSpelledInfo;
import l2.gameserver.network.l2.s2c.PartySpelled;
import l2.gameserver.network.l2.s2c.ShortBuffStatusUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncOwner;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.taskmanager.EffectTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Effect
extends RunnableImpl
implements Comparable<Effect>,
FuncOwner {
    protected static final Logger _log = LoggerFactory.getLogger(Effect.class);
    public static final Effect[] EMPTY_L2EFFECT_ARRAY = new Effect[0];
    public static int SUSPENDED = -1;
    public static int STARTING = 0;
    public static int STARTED = 1;
    public static int ACTING = 2;
    public static int FINISHING = 3;
    public static int FINISHED = 4;
    protected final Creature _effector;
    protected final Creature _effected;
    protected final Skill _skill;
    protected final int _displayId;
    protected final int _displayLevel;
    private final double _value;
    private final AtomicInteger qX;
    private int _count;
    private long _period;
    private long EV;
    private long EW;
    private boolean EX = false;
    private Effect EY = null;
    private boolean _active = false;
    protected final EffectTemplate _template;
    private Future<?> EZ;
    private final EEffectSlot Fa;
    private ActionDispelListener Fb;
    private ActionDispelOnItemUnequipListener Fc;

    protected Effect(Env env, EffectTemplate effectTemplate) {
        this._skill = env.skill;
        this._effector = env.character;
        this._effected = env.target;
        this._template = effectTemplate;
        this._value = effectTemplate._value;
        this._count = effectTemplate.getCount();
        this._period = effectTemplate.getPeriod();
        this.EW = this._period * (long)this._count;
        this._displayId = effectTemplate._displayId != 0 ? effectTemplate._displayId : this._skill.getDisplayId();
        this._displayLevel = effectTemplate._displayLevel != 0 ? effectTemplate._displayLevel : this._skill.getDisplayLevel();
        this.qX = new AtomicInteger(STARTING);
        this.Fa = this._skill.isOffensive() ? EEffectSlot.EFFECT_SLOT_DEBUFF : EEffectSlot.EFFECT_SLOT_NORMAL;
    }

    public long getPeriod() {
        return this._period;
    }

    public void setPeriod(long l) {
        this._period = l;
        this.EW = this._period * (long)this._count;
    }

    public int getCount() {
        return this._count;
    }

    public void setCount(int n) {
        this._count = n;
        this.EW = this._period * (long)this._count;
    }

    public boolean isOneTime() {
        return this._period == 0L;
    }

    public long getStartTime() {
        if (this.EV == 0L) {
            return System.currentTimeMillis();
        }
        return this.EV;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.getStartTime();
    }

    public long getDuration() {
        return this.EW;
    }

    public int getTimeLeft() {
        return (int)((this.getDuration() - this.getTime()) / 1000L);
    }

    public boolean isTimeLeft() {
        return this.getDuration() - this.getTime() > 0L;
    }

    public boolean isInUse() {
        return this.EX;
    }

    public void setInUse(boolean bl) {
        this.EX = bl;
    }

    public boolean isActive() {
        return this._active;
    }

    public void setActive(boolean bl) {
        this._active = bl;
    }

    public EffectTemplate getTemplate() {
        return this._template;
    }

    public EEffectSlot getEffectSlot() {
        return this.Fa;
    }

    public String getStackType() {
        return this.getTemplate()._stackType;
    }

    public String getStackType2() {
        return this.getTemplate()._stackType2;
    }

    public boolean isStackTypeMatch(String ... stringArray) {
        String string = this.getStackType();
        String string2 = this.getStackType2();
        for (int i = 0; i < stringArray.length; ++i) {
            if (stringArray[i].equalsIgnoreCase(string)) {
                return true;
            }
            if (!stringArray[i].equalsIgnoreCase(string2)) continue;
            return true;
        }
        return false;
    }

    public boolean isStackTypeMatch(Effect effect) {
        return this.isStackTypeMatch(effect.getStackType()) || this.isStackTypeMatch(effect.getStackType2());
    }

    public int getStackOrder() {
        return this.getTemplate()._stackOrder;
    }

    public Skill getSkill() {
        return this._skill;
    }

    public Creature getEffector() {
        return this._effector;
    }

    public Creature getEffected() {
        return this._effected;
    }

    public double calc() {
        return this._value;
    }

    public boolean isEnded() {
        return this.isFinished() || this.isFinishing();
    }

    public boolean isFinishing() {
        return this.getState() == FINISHING;
    }

    public boolean isFinished() {
        return this.getState() == FINISHED;
    }

    private int getState() {
        return this.qX.get();
    }

    private boolean d(int n, int n2) {
        return this.qX.compareAndSet(n, n2);
    }

    public boolean checkCondition() {
        return true;
    }

    protected void onStart() {
        this.getEffected().addStatFuncs(this.getStatFuncs());
        this.getEffected().addTriggers(this.getTemplate());
        if (this.getTemplate()._abnormalEffect != AbnormalEffect.NULL) {
            this.getEffected().startAbnormalEffect(this.getTemplate()._abnormalEffect);
        } else if (this.getEffectType().getAbnormal() != null) {
            this.getEffected().startAbnormalEffect(this.getEffectType().getAbnormal());
        }
        if (this.getTemplate()._abnormalEffect2 != AbnormalEffect.NULL) {
            this.getEffected().startAbnormalEffect(this.getTemplate()._abnormalEffect2);
        }
        if (this.getTemplate()._abnormalEffect3 != AbnormalEffect.NULL) {
            this.getEffected().startAbnormalEffect(this.getTemplate()._abnormalEffect3);
        }
        if (this._template._cancelOnAction) {
            this.Fb = new ActionDispelListener();
            this.getEffected().addListener(this.Fb);
        }
        if (this.getEffected().isPlayer()) {
            Player player = this.getEffected().getPlayer();
            if (this._template._cancelOnItem > 0) {
                this.Fc = new ActionDispelOnItemUnequipListener();
                player.getInventory().addListener(this.Fc);
            }
            if (!this.getSkill().canUseTeleport()) {
                player.getPlayerAccess().UseTeleport = false;
            }
        }
    }

    protected abstract boolean onActionTime();

    protected void onExit() {
        this.getEffected().removeStatsOwner(this);
        this.getEffected().removeTriggers(this.getTemplate());
        if (this.getTemplate()._abnormalEffect != AbnormalEffect.NULL) {
            this.getEffected().stopAbnormalEffect(this.getTemplate()._abnormalEffect);
        } else if (this.getEffectType().getAbnormal() != null) {
            this.getEffected().stopAbnormalEffect(this.getEffectType().getAbnormal());
        }
        if (this.getTemplate()._abnormalEffect2 != AbnormalEffect.NULL) {
            this.getEffected().stopAbnormalEffect(this.getTemplate()._abnormalEffect2);
        }
        if (this.getTemplate()._abnormalEffect3 != AbnormalEffect.NULL) {
            this.getEffected().stopAbnormalEffect(this.getTemplate()._abnormalEffect3);
        }
        if (this._template._cancelOnAction) {
            this.getEffected().removeListener(this.Fb);
        }
        if (this.getEffected().isPlayer()) {
            Player player = this.getEffected().getPlayer();
            if (this.Fc != null) {
                player.getInventory().removeListener(this.Fc);
            }
            if (this.isStackTypeMatch("HpRecoverCast")) {
                player.sendPacket((IStaticPacket)new ShortBuffStatusUpdate());
            }
            if (!this.getSkill().canUseTeleport() && !player.getPlayerAccess().UseTeleport) {
                player.getPlayerAccess().UseTeleport = true;
            }
        }
    }

    private void bZ() {
        if (this.EZ != null) {
            this.EZ.cancel(false);
        }
    }

    private void ca() {
        if (this.EZ == null) {
            this.EV = System.currentTimeMillis();
            this.EZ = EffectTaskManager.getInstance().scheduleAtFixedRate(this, this._period, this._period);
        }
    }

    public final void schedule() {
        Creature creature = this.getEffected();
        if (creature == null) {
            return;
        }
        if (!this.checkCondition()) {
            return;
        }
        this.getEffected().getEffectList().addEffect(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void suspend() {
        if (this.d(STARTING, SUSPENDED)) {
            this.ca();
        } else if (this.d(STARTED, SUSPENDED) || this.d(ACTING, SUSPENDED)) {
            Effect effect = this;
            synchronized (effect) {
                if (this.isInUse()) {
                    this.setInUse(false);
                    this.setActive(false);
                    this.onExit();
                }
            }
            this.getEffected().getEffectList().removeEffect(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void start() {
        if (this.d(STARTING, STARTED)) {
            Effect effect = this;
            synchronized (effect) {
                if (this.isInUse()) {
                    this.setActive(true);
                    this.onStart();
                    this.ca();
                }
            }
        }
        this.run();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void runImpl() throws Exception {
        if (this.d(STARTED, ACTING)) {
            if (!this.getSkill().isHideStartMessage() && !this.getSkill().isToggle() && this.getEffected().getEffectList().getEffectsCountForSkill(this.getSkill().getId()) == 1) {
                this.getEffected().sendPacket((IStaticPacket)new SystemMessage(110).addSkillName(this._displayId, this._displayLevel));
            }
            if (this.getSkill().getSecondSkill() > 0) {
                SkillTable.getInstance().getInfo(this.getSkill().getSecondSkill(), 1).getEffects(this._effector, this._effected, false, false);
            }
            return;
        }
        if (this.getState() == SUSPENDED) {
            if (this.isTimeLeft()) {
                --this._count;
                if (this.isTimeLeft()) {
                    return;
                }
            }
            this.exit();
            return;
        }
        if (this.getState() == ACTING && this.isTimeLeft()) {
            --this._count;
            if ((!this.isActive() || this.onActionTime()) && this.isTimeLeft()) {
                return;
            }
        }
        if (this.d(ACTING, FINISHING)) {
            this.setInUse(false);
        }
        if (this.d(FINISHING, FINISHED)) {
            Effect effect = this;
            synchronized (effect) {
                this.setActive(false);
                this.bZ();
                this.onExit();
            }
            effect = this.getNext();
            if (effect != null && effect.d(SUSPENDED, STARTING)) {
                effect.schedule();
            }
            if (this.getSkill().getDelayedEffect() > 0) {
                SkillTable.getInstance().getInfo(this.getSkill().getDelayedEffect(), 1).getEffects(this._effector, this._effected, false, false);
            }
            boolean bl = !this.isHidden() && this.getEffected().getEffectList().getEffectsCountForSkill(this.getSkill().getId()) == 1;
            this.getEffected().getEffectList().removeEffect(this);
            if (bl) {
                this.getEffected().sendPacket((IStaticPacket)new SystemMessage(92).addSkillName(this._displayId, this._displayLevel));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void exit() {
        Effect effect = this.getNext();
        if (effect != null) {
            effect.exit();
        }
        this.cb();
        if (this.d(STARTING, FINISHED)) {
            this.getEffected().getEffectList().removeEffect(this);
        } else if (this.d(SUSPENDED, FINISHED)) {
            this.bZ();
        } else if (this.d(STARTED, FINISHED) || this.d(ACTING, FINISHED)) {
            Effect effect2 = this;
            synchronized (effect2) {
                if (this.isInUse()) {
                    this.setInUse(false);
                    this.setActive(false);
                    this.bZ();
                    this.onExit();
                }
            }
            this.getEffected().getEffectList().removeEffect(this);
        }
    }

    private boolean a(Effect effect) {
        if (effect == null || effect.isEnded()) {
            return false;
        }
        Effect effect2 = this.getNext();
        if (effect2 != null && !effect2.maybeScheduleNext(effect)) {
            return false;
        }
        this.EY = effect;
        return true;
    }

    public Effect getNext() {
        return this.EY;
    }

    private void cb() {
        this.EY = null;
    }

    public boolean maybeScheduleNext(Effect effect) {
        if (effect.getStackOrder() < this.getStackOrder()) {
            if (effect.getTimeLeft() > this.getTimeLeft()) {
                effect.suspend();
                this.a(effect);
            }
            return false;
        }
        if (effect.getTimeLeft() >= this.getTimeLeft()) {
            if (this.getNext() != null && this.getNext().getTimeLeft() > effect.getTimeLeft()) {
                effect.a(this.getNext());
                this.cb();
            }
            this.exit();
        } else {
            this.suspend();
            effect.a(this);
        }
        return true;
    }

    public Func[] getStatFuncs() {
        return this.getTemplate().getStatFuncs(this);
    }

    public void addIcon(AbnormalStatusUpdate abnormalStatusUpdate) {
        if (!this.isActive() || this.isHidden()) {
            return;
        }
        int n = this._skill.isToggle() ? -1 : this.getTimeLeft();
        abnormalStatusUpdate.addEffect(this._displayId, this._displayLevel, n);
    }

    public void addPartySpelledIcon(PartySpelled partySpelled) {
        if (!this.isActive() || this.isHidden()) {
            return;
        }
        int n = this._skill.isToggle() ? -1 : this.getTimeLeft();
        partySpelled.addPartySpelledEffect(this._displayId, this._displayLevel, n);
    }

    public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfo exOlympiadSpelledInfo) {
        if (!this.isActive() || this.isHidden()) {
            return;
        }
        int n = this._skill.isToggle() ? -1 : this.getTimeLeft();
        exOlympiadSpelledInfo.addSpellRecivedPlayer(player);
        exOlympiadSpelledInfo.addEffect(this._displayId, this._displayLevel, n);
    }

    protected int getLevel() {
        return this._skill.getLevel();
    }

    public EffectType getEffectType() {
        return this.getTemplate()._effectType;
    }

    public boolean isHidden() {
        return this._displayId < 0;
    }

    @Override
    public int compareTo(Effect effect) {
        if (effect.equals(this)) {
            return 0;
        }
        return 1;
    }

    public boolean isSaveable() {
        return this._template.isSaveable(this.getSkill().isSaveable()) && this.getTimeLeft() >= Config.ALT_SAVE_EFFECTS_REMAINING_TIME;
    }

    public int getDisplayId() {
        return this._displayId;
    }

    public int getDisplayLevel() {
        return this._displayLevel;
    }

    public boolean isCancelable() {
        return this._template.isCancelable(this.getSkill().isCancelable());
    }

    public String toString() {
        return "Skill: " + this._skill + ", state: " + this.getState() + ", inUse: " + this.EX + ", active : " + this._active;
    }

    @Override
    public boolean isFuncEnabled() {
        return this.isInUse();
    }

    @Override
    public boolean overrideLimits() {
        return false;
    }

    public boolean isOffensive() {
        return this._template.isOffensive(this.getSkill().isOffensive());
    }

    private class ActionDispelOnItemUnequipListener
    implements OnEquipListener {
        private ActionDispelOnItemUnequipListener() {
        }

        @Override
        public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        }

        @Override
        public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
            if (itemInstance.isWeapon() && (Effect.this.getTemplate()._cancelOnItem & 1) != 0 || itemInstance.isArmor() && (Effect.this.getTemplate()._cancelOnItem & 2) != 0) {
                Effect.this.exit();
            }
        }
    }

    private class ActionDispelListener
    implements OnAttackListener,
    OnMagicUseListener {
        private ActionDispelListener() {
        }

        @Override
        public void onMagicUse(Creature creature, Skill skill, Creature creature2, boolean bl) {
            Effect.this.exit();
        }

        @Override
        public void onAttack(Creature creature, Creature creature2) {
            Effect.this.exit();
        }
    }

    public static enum EEffectSlot {
        EFFECT_SLOT_NORMAL,
        EFFECT_SLOT_DEBUFF;

        public static final EEffectSlot[] VALUES;

        static {
            VALUES = EEffectSlot.values();
        }
    }
}

