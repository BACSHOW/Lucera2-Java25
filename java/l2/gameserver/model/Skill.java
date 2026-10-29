/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import l2.commons.collections.LazyArrayList;
import l2.commons.geometry.AbstractShape;
import l2.commons.geometry.Polygon;
import l2.commons.lang.ArrayUtils;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.EffectList;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.base.SkillTrait;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.oly.CompetitionType;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.FeedableBeastInstance;
import l2.gameserver.model.items.ItemContainer;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.FlyToLocation;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectBuffImmunity;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.skills.skillclasses.AIeffects;
import l2.gameserver.skills.skillclasses.Aggression;
import l2.gameserver.skills.skillclasses.Balance;
import l2.gameserver.skills.skillclasses.BeastFeed;
import l2.gameserver.skills.skillclasses.BuffCharger;
import l2.gameserver.skills.skillclasses.CPDam;
import l2.gameserver.skills.skillclasses.Call;
import l2.gameserver.skills.skillclasses.ClanGate;
import l2.gameserver.skills.skillclasses.CombatPointHeal;
import l2.gameserver.skills.skillclasses.Continuous;
import l2.gameserver.skills.skillclasses.Craft;
import l2.gameserver.skills.skillclasses.DeathPenalty;
import l2.gameserver.skills.skillclasses.Default;
import l2.gameserver.skills.skillclasses.DefuseTrap;
import l2.gameserver.skills.skillclasses.DeleteHate;
import l2.gameserver.skills.skillclasses.DeleteHateOfMe;
import l2.gameserver.skills.skillclasses.DestroySummon;
import l2.gameserver.skills.skillclasses.DetectTrap;
import l2.gameserver.skills.skillclasses.Disablers;
import l2.gameserver.skills.skillclasses.Drain;
import l2.gameserver.skills.skillclasses.DrainSoul;
import l2.gameserver.skills.skillclasses.EXPHeal;
import l2.gameserver.skills.skillclasses.EffectsFromSkills;
import l2.gameserver.skills.skillclasses.FishingSkill;
import l2.gameserver.skills.skillclasses.Harvesting;
import l2.gameserver.skills.skillclasses.Heal;
import l2.gameserver.skills.skillclasses.HealPercent;
import l2.gameserver.skills.skillclasses.LethalShot;
import l2.gameserver.skills.skillclasses.MDam;
import l2.gameserver.skills.skillclasses.ManaDam;
import l2.gameserver.skills.skillclasses.ManaHeal;
import l2.gameserver.skills.skillclasses.ManaHealPercent;
import l2.gameserver.skills.skillclasses.NegateEffects;
import l2.gameserver.skills.skillclasses.NegateStats;
import l2.gameserver.skills.skillclasses.PDam;
import l2.gameserver.skills.skillclasses.PcBangPointsAdd;
import l2.gameserver.skills.skillclasses.PetSummon;
import l2.gameserver.skills.skillclasses.Recall;
import l2.gameserver.skills.skillclasses.ReelingPumping;
import l2.gameserver.skills.skillclasses.Resurrect;
import l2.gameserver.skills.skillclasses.Ride;
import l2.gameserver.skills.skillclasses.SPHeal;
import l2.gameserver.skills.skillclasses.ShiftAggression;
import l2.gameserver.skills.skillclasses.SkillSeed;
import l2.gameserver.skills.skillclasses.Sowing;
import l2.gameserver.skills.skillclasses.Spoil;
import l2.gameserver.skills.skillclasses.StealBuff;
import l2.gameserver.skills.skillclasses.SummonItem;
import l2.gameserver.skills.skillclasses.SummonSiegeFlag;
import l2.gameserver.skills.skillclasses.Sweep;
import l2.gameserver.skills.skillclasses.TakeCastle;
import l2.gameserver.skills.skillclasses.TameControl;
import l2.gameserver.skills.skillclasses.TeleportNpc;
import l2.gameserver.skills.skillclasses.Toggle;
import l2.gameserver.skills.skillclasses.Transformation;
import l2.gameserver.skills.skillclasses.Unlock;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Formulas;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.PositionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Skill
extends StatTemplate
implements Cloneable {
    public static final Skill[] EMPTY_ARRAY = new Skill[0];
    public static final int SKILL_CRAFTING = 172;
    public static final int SKILL_POLEARM_MASTERY = 216;
    public static final int SKILL_CRYSTALLIZE = 248;
    public static final int SKILL_WEAPON_MAGIC_MASTERY1 = 249;
    public static final int SKILL_WEAPON_MAGIC_MASTERY2 = 250;
    public static final int SKILL_BLINDING_BLOW = 321;
    public static final int SKILL_STRIDER_ASSAULT = 325;
    public static final int SKILL_WYVERN_AEGIS = 327;
    public static final int SKILL_BLUFF = 358;
    public static final int SKILL_HEROIC_MIRACLE = 395;
    public static final int SKILL_HEROIC_BERSERKER = 396;
    public static final int SKILL_SOUL_MASTERY = 467;
    public static final int SKILL_TRANSFORM_DISPEL = 619;
    public static final int SKILL_FINAL_FLYING_FORM = 840;
    public static final int SKILL_AURA_BIRD_FALCON = 841;
    public static final int SKILL_AURA_BIRD_OWL = 842;
    public static final int SKILL_RECHARGE = 1013;
    public static final int SKILL_TRANSFER_PAIN = 1262;
    public static final int SKILL_FISHING_MASTERY = 1315;
    public static final int SKILL_NOBLESSE_BLESSING = 1323;
    public static final int SKILL_SUMMON_CP_POTION = 1324;
    public static final int SKILL_FORTUNE_OF_NOBLESSE = 1325;
    public static final int SKILL_HARMONY_OF_NOBLESSE = 1326;
    public static final int SKILL_SYMPHONY_OF_NOBLESSE = 1327;
    public static final int SKILL_HEROIC_VALOR = 1374;
    public static final int SKILL_HEROIC_GRANDEUR = 1375;
    public static final int SKILL_HEROIC_DREAD = 1376;
    public static final int SKILL_MYSTIC_IMMUNITY = 1411;
    public static final int SKILL_RAID_BLESSING = 2168;
    public static final int SKILL_HINDER_STRIDER = 4258;
    public static final int SKILL_WYVERN_BREATH = 4289;
    public static final int SKILL_RAID_CURSE = 4515;
    public static final int SKILL_CHARM_OF_COURAGE = 5041;
    private static final Logger _log = LoggerFactory.getLogger(Skill.class);
    protected final int[] _itemConsume;
    protected final int[] _itemConsumeId;
    protected final int _referenceItemId;
    protected final int _referenceItemMpConsume;
    private final int pr;
    public boolean _isStandart = false;
    protected EffectTemplate[] _effectTemplates = EffectTemplate.EMPTY_ARRAY;
    private boolean Ll = false;
    protected List<Integer> _teachers;
    protected List<ClassId> _canLearn;
    protected AddedSkill[] _addedSkills = AddedSkill.EMPTY_ARRAY;
    protected boolean _isAltUse;
    protected boolean _isBehind;
    protected boolean _isCancelable;
    protected boolean _isCorpse;
    protected boolean _isCommon;
    protected boolean _isItemHandler;
    protected boolean _isOffensive;
    protected boolean _isPvpSkill;
    protected boolean _isNotUsedByAI;
    protected boolean _isFishingSkill;
    protected boolean _isPvm;
    protected boolean _isForceUse;
    protected boolean _isNewbie;
    protected boolean _isPreservedOnDeath;
    protected boolean _isHeroic;
    protected boolean _isSaveable;
    protected boolean _isSkillTimePermanent;
    protected boolean _isReuseDelayPermanent;
    protected boolean _isReflectable;
    protected boolean _isSuicideAttack;
    protected boolean _isShieldignore;
    protected boolean _isUndeadOnly;
    protected Ternary _isUseSS;
    protected boolean _isOverhit;
    protected boolean _isSoulBoost;
    protected boolean _isChargeBoost;
    protected boolean _isUsingWhileCasting;
    protected boolean _isIgnoreResists;
    protected boolean _isIgnoreInvul;
    protected boolean _isTrigger;
    protected boolean _isNotAffectedByMute;
    protected boolean _basedOnTargetDebuff;
    protected boolean _deathlink;
    protected boolean _hideStartMessage;
    protected boolean _hideUseMessage;
    protected boolean _skillInterrupt;
    protected boolean _flyingTransformUsage;
    protected boolean _canUseTeleport;
    protected boolean _isProvoke;
    protected boolean _isCubicSkill = false;
    protected boolean _isSelfDispellable;
    protected boolean _isSlotNone;
    protected boolean _isSharedClassReuse;
    protected SkillType _skillType;
    protected SkillOpType _operateType;
    protected SkillTargetType _targetType;
    protected SkillMagicType _magicType;
    protected SkillTrait _traitType;
    protected BaseStats _saveVs;
    protected SkillNextAction _skillNextAction;
    protected Element _element;
    protected FlyToLocation.FlyType _flyType;
    protected boolean _flyToBack;
    protected Condition[] _preCondition = Condition.EMPTY_ARRAY;
    protected int _id;
    protected int _level;
    protected int _baseLevel;
    protected int _displayId;
    protected int _displayLevel;
    protected int _activateRate;
    protected int _castRange;
    protected int _cancelTarget;
    protected int _coolTime;
    protected int _delayedEffect;
    protected int _effectPoint;
    protected int _energyConsume;
    protected int _elementPower;
    protected int _flyRadius;
    protected int _hitTime;
    protected int _hpConsume;
    protected int _levelModifier;
    protected int _magicLevel;
    protected int _matak;
    protected int _minPledgeClass;
    protected int _minRank;
    protected int _negatePower;
    protected int _negateSkill;
    protected int _npcId;
    protected int _numCharges;
    protected int _skillInterruptTime;
    protected int _skillRadius;
    protected int _effectiveRange;
    protected int _soulsConsume;
    protected int _symbolId;
    protected int _weaponsAllowed;
    protected int _enchantLevelCount;
    protected int _criticalRate;
    protected int _secondSkill;
    protected long _reuseDelay;
    protected double _power;
    protected double _powerPvP;
    protected double _powerPvE;
    protected double _mpConsume1;
    protected double _mpConsume2;
    protected double _lethal1;
    protected double _lethal2;
    protected double _absorbPart;
    protected double _baseBlowRate;
    protected String _name;
    protected String _baseValues;
    protected String _icon;

    protected Skill(StatsSet statsSet) {
        String string;
        int n;
        Object object;
        this._id = statsSet.getInteger("skill_id");
        this._level = statsSet.getInteger("level");
        this._displayId = statsSet.getInteger("displayId", this._id);
        this._displayLevel = statsSet.getInteger("displayLevel", this._level);
        this._baseLevel = statsSet.getInteger("base_level");
        this._name = statsSet.getString("name");
        this._operateType = statsSet.getEnum("operateType", SkillOpType.class);
        this._isNewbie = statsSet.getBool("isNewbie", false);
        this._isSelfDispellable = statsSet.getBool("isSelfDispellable", true);
        this._isPreservedOnDeath = statsSet.getBool("isPreservedOnDeath", false);
        this._isHeroic = statsSet.getBool("isHeroic", false);
        this._isAltUse = statsSet.getBool("altUse", false);
        this._mpConsume1 = statsSet.getInteger("mpConsume1", 0);
        this._mpConsume2 = statsSet.getInteger("mpConsume2", 0);
        this._energyConsume = statsSet.getInteger("energyConsume", 0);
        this._hpConsume = statsSet.getInteger("hpConsume", 0);
        this._soulsConsume = statsSet.getInteger("soulsConsume", 0);
        this._isSoulBoost = statsSet.getBool("soulBoost", false);
        this._isChargeBoost = statsSet.getBool("chargeBoost", false);
        this._isProvoke = statsSet.getBool("provoke", false);
        this._isUsingWhileCasting = statsSet.getBool("isUsingWhileCasting", false);
        this._matak = statsSet.getInteger("mAtk", 0);
        this._isUseSS = Ternary.valueOf(statsSet.getString("useSS", Ternary.DEFAULT.toString()).toUpperCase());
        this._magicLevel = statsSet.getInteger("magicLevel", 0);
        this._castRange = statsSet.getInteger("castRange", 40);
        this._effectiveRange = statsSet.getInteger("effectiveRange", this._castRange + (this._castRange < 200 ? 400 : 500));
        this._baseValues = statsSet.getString("baseValues", null);
        String string2 = statsSet.getString("itemConsumeCount", "");
        String string3 = statsSet.getString("itemConsumeId", "");
        if (string2.length() == 0) {
            this._itemConsume = new int[]{0};
        } else {
            object = string2.split(" ");
            this._itemConsume = new int[((String[])object).length];
            for (n = 0; n < ((String[])object).length; ++n) {
                this._itemConsume[n] = Integer.parseInt(((String[])object)[n]);
            }
        }
        if (string3.length() == 0) {
            this._itemConsumeId = new int[]{0};
        } else {
            object = string3.split(" ");
            this._itemConsumeId = new int[((String[])object).length];
            for (n = 0; n < ((String[])object).length; ++n) {
                this._itemConsumeId[n] = Integer.parseInt(((String[])object)[n]);
            }
        }
        this._referenceItemId = statsSet.getInteger("referenceItemId", 0);
        this._referenceItemMpConsume = statsSet.getInteger("referenceItemMpConsume", 0);
        this._isItemHandler = statsSet.getBool("isHandler", false);
        this._isCommon = statsSet.getBool("isCommon", false);
        this._isSaveable = statsSet.getBool("isSaveable", true);
        this._coolTime = statsSet.getInteger("coolTime", 0);
        this._skillInterruptTime = statsSet.getInteger("hitCancelTime", 0);
        this._reuseDelay = statsSet.getLong("reuseDelay", 0L);
        this._hitTime = statsSet.getInteger("hitTime", 0);
        this._skillRadius = statsSet.getInteger("skillRadius", 80);
        this._targetType = statsSet.getEnum("target", SkillTargetType.class);
        this._magicType = statsSet.getEnum("magicType", SkillMagicType.class, SkillMagicType.PHYSIC);
        this._traitType = statsSet.getEnum("trait", SkillTrait.class, null);
        this._saveVs = statsSet.getEnum("saveVs", BaseStats.class, null);
        this._hideStartMessage = statsSet.getBool("isHideStartMessage", false);
        this._hideUseMessage = statsSet.getBool("isHideUseMessage", false);
        this._isUndeadOnly = statsSet.getBool("undeadOnly", false);
        this._isCorpse = statsSet.getBool("corpse", false);
        this._power = statsSet.getDouble("power", 0.0);
        this._baseBlowRate = statsSet.getDouble("baseBlowRate", 0.0);
        this._powerPvP = statsSet.getDouble("powerPvP", 0.0);
        this._powerPvE = statsSet.getDouble("powerPvE", 0.0);
        this._effectPoint = statsSet.getInteger("effectPoint", 0);
        this._skillNextAction = SkillNextAction.valueOf(statsSet.getString("nextAction", "DEFAULT").toUpperCase());
        this._skillType = statsSet.getEnum("skillType", SkillType.class);
        this._isSuicideAttack = statsSet.getBool("isSuicideAttack", false);
        this._isSkillTimePermanent = statsSet.getBool("isSkillTimePermanent", false);
        this._isReuseDelayPermanent = statsSet.getBool("isReuseDelayPermanent", false);
        this._deathlink = statsSet.getBool("deathlink", false);
        this._basedOnTargetDebuff = statsSet.getBool("basedOnTargetDebuff", false);
        this._isNotUsedByAI = statsSet.getBool("isNotUsedByAI", false);
        this._isIgnoreResists = statsSet.getBool("isIgnoreResists", false);
        this._isIgnoreInvul = statsSet.getBool("isIgnoreInvul", false);
        this._isSharedClassReuse = statsSet.getBool("isSharedClassReuse", false);
        this._isTrigger = statsSet.getBool("isTrigger", false);
        this._isNotAffectedByMute = statsSet.getBool("isNotAffectedByMute", false);
        this._flyingTransformUsage = statsSet.getBool("flyingTransformUsage", false);
        this._canUseTeleport = statsSet.getBool("canUseTeleport", true);
        this._element = NumberUtils.isNumber(statsSet.getString("element", "NONE")) ? Element.getElementById(statsSet.getInteger("element", -1)) : Element.getElementByName(statsSet.getString("element", "none").toUpperCase());
        this._elementPower = statsSet.getInteger("elementPower", 0);
        if (this._element != Element.NONE && this._elementPower == 0) {
            this._elementPower = 20;
        }
        this._activateRate = statsSet.getInteger("activateRate", -1);
        this._levelModifier = statsSet.getInteger("levelModifier", 1);
        this._isCancelable = statsSet.getBool("cancelable", true);
        this._isReflectable = statsSet.getBool("reflectable", true);
        this._isShieldignore = statsSet.getBool("shieldignore", false);
        this._criticalRate = statsSet.getInteger("criticalRate", 0);
        this._isOverhit = statsSet.getBool("overHit", false);
        this._weaponsAllowed = statsSet.getInteger("weaponsAllowed", 0);
        this._minPledgeClass = statsSet.getInteger("minPledgeClass", 0);
        this._minRank = statsSet.getInteger("minRank", 0);
        this._isOffensive = statsSet.getBool("isOffensive", this._skillType.isOffensive());
        this._isPvpSkill = statsSet.getBool("isPvpSkill", this._skillType.isPvpSkill());
        this._isFishingSkill = statsSet.getBool("isFishingSkill", false);
        this._isPvm = statsSet.getBool("isPvm", this._skillType.isPvM());
        this._isForceUse = statsSet.getBool("isForceUse", false);
        this._isBehind = statsSet.getBool("behind", false);
        this._symbolId = statsSet.getInteger("symbolId", 0);
        this._npcId = statsSet.getInteger("npcId", 0);
        this._flyType = FlyToLocation.FlyType.valueOf(statsSet.getString("flyType", "NONE").toUpperCase());
        this._flyToBack = statsSet.getBool("flyToBack", false);
        this._flyRadius = statsSet.getInteger("flyRadius", 200);
        this._negateSkill = statsSet.getInteger("negateSkill", 0);
        this._negatePower = statsSet.getInteger("negatePower", Integer.MAX_VALUE);
        this._numCharges = statsSet.getInteger("num_charges", 0);
        this._delayedEffect = statsSet.getInteger("delayedEffect", 0);
        this._cancelTarget = statsSet.getInteger("cancelTarget", 0);
        this._skillInterrupt = statsSet.getBool("skillInterrupt", false);
        this._lethal1 = statsSet.getDouble("lethal1", 0.0);
        this._lethal2 = statsSet.getDouble("lethal2", 0.0);
        this._absorbPart = statsSet.getDouble("absorbPart", 0.0);
        this._icon = statsSet.getString("icon", "");
        this._secondSkill = statsSet.getInteger("secondSkill", 0);
        this._isSlotNone = statsSet.getBool("isIgnorBuffLimit", false);
        object = new StringTokenizer(statsSet.getString("addSkills", ""), ";");
        while (((StringTokenizer)object).hasMoreTokens()) {
            n = Integer.parseInt(((StringTokenizer)object).nextToken());
            int n2 = Integer.parseInt(((StringTokenizer)object).nextToken());
            if (n2 == -1) {
                n2 = this._level;
            }
            this._addedSkills = ArrayUtils.add(this._addedSkills, new AddedSkill(n, n2));
        }
        if (this._skillNextAction == SkillNextAction.DEFAULT) {
            switch (this._skillType) {
                case PDAM: 
                case CPDAM: 
                case LETHAL_SHOT: 
                case SPOIL: 
                case SOWING: 
                case STUN: {
                    this._skillNextAction = SkillNextAction.ATTACK;
                    break;
                }
                default: {
                    this._skillNextAction = SkillNextAction.NONE;
                }
            }
        }
        if ((string = statsSet.getString("canLearn", null)) == null) {
            this._canLearn = null;
        } else {
            this._canLearn = new ArrayList<ClassId>();
            object = new StringTokenizer(string, " \r\n\t,;");
            while (((StringTokenizer)object).hasMoreTokens()) {
                String string4 = ((StringTokenizer)object).nextToken();
                this._canLearn.add(ClassId.valueOf(string4));
            }
        }
        String string5 = statsSet.getString("teachers", null);
        if (string5 == null) {
            this._teachers = null;
        } else {
            this._teachers = new ArrayList<Integer>();
            object = new StringTokenizer(string5, " \r\n\t,;");
            while (((StringTokenizer)object).hasMoreTokens()) {
                String string6 = ((StringTokenizer)object).nextToken();
                this._teachers.add(Integer.parseInt(string6));
            }
        }
        this.pr = this._id * 1023 + this._level;
    }

    public final boolean getWeaponDependancy(Creature creature) {
        if (this._weaponsAllowed == 0) {
            return true;
        }
        if (creature.getActiveWeaponInstance() != null && creature.getActiveWeaponItem() != null && (creature.getActiveWeaponItem().getItemType().mask() & (long)this._weaponsAllowed) != 0L) {
            return true;
        }
        if (creature.getSecondaryWeaponInstance() != null && creature.getSecondaryWeaponItem() != null && (creature.getSecondaryWeaponItem().getItemType().mask() & (long)this._weaponsAllowed) != 0L) {
            return true;
        }
        creature.sendPacket((IStaticPacket)new SystemMessage(113).addSkillName(this._displayId, this._displayLevel));
        return false;
    }

    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        return this.checkCondition(creature, creature2, true, bl, bl2, bl3);
    }

    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        Object object;
        Player player = creature.getPlayer();
        if (creature.isDead()) {
            return false;
        }
        if (creature2 != null && creature.getReflection() != creature2.getReflection()) {
            if (bl) {
                creature.sendPacket((IStaticPacket)SystemMsg.CANNOT_SEE_TARGET);
            }
            return false;
        }
        if (!this.getWeaponDependancy(creature)) {
            return false;
        }
        if (creature.isUnActiveSkill(this._id)) {
            return false;
        }
        if (bl4 && creature.isSkillDisabled(this)) {
            if (bl) {
                creature.sendReuseMessage(this);
            }
            return false;
        }
        if (bl4) {
            double d = this._mpConsume2;
            if (this.isMusic()) {
                d += (double)creature.getEffectList().getActiveMusicCount(this.getId()) * d / 2.0;
                d = creature.calcStat(Stats.MP_DANCE_SKILL_CONSUME, d, creature2, this);
            } else {
                d = this.isMagic() ? creature.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, d, creature2, this) : creature.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, d, creature2, this);
            }
            if (creature.getCurrentMp() < this._mpConsume1 + d) {
                if (bl) {
                    creature.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
                }
                return false;
            }
        }
        if (creature.getCurrentHp() < (double)(this._hpConsume + 1)) {
            if (bl) {
                creature.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_HP);
            }
            return false;
        }
        if (!this._isItemHandler && !this._isAltUse && creature.isMuted(this)) {
            return false;
        }
        if (this._soulsConsume > creature.getConsumedSouls()) {
            if (bl) {
                creature.sendPacket((IStaticPacket)Msg.THERE_IS_NOT_ENOUGHT_SOUL);
            }
            return false;
        }
        if (player != null) {
            if (player.isInFlyingTransform() && this._isItemHandler && !this.flyingTransformUsage()) {
                if (bl) {
                    player.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(this.getItemConsumeId()[0]));
                }
                return false;
            }
            if (player.isInBoat() && player.getBoat().isVehicle() && !(this instanceof FishingSkill) && !(this instanceof ReelingPumping)) {
                return false;
            }
            if (player.isInObserverMode()) {
                if (bl) {
                    creature.sendPacket((IStaticPacket)Msg.OBSERVERS_CANNOT_PARTICIPATE);
                }
                return false;
            }
            if (bl4 && this._itemConsume[0] > 0) {
                for (int i = 0; i < this._itemConsume.length; ++i) {
                    object = creature instanceof Summon ? player.getInventory() : ((Playable)creature).getInventory();
                    ItemInstance consumedItem = ((ItemContainer)object).getItemByItemId(this._itemConsumeId[i]);
                    if (consumedItem != null && consumedItem.getCount() >= (long)this._itemConsume[i]) continue;
                    if (bl && creature == player) {
                        player.sendPacket(this.isHandler() ? SystemMsg.INCORRECT_ITEM_COUNT : new SystemMessage(113).addSkillName(this.getDisplayId(), this.getDisplayLevel()));
                    }
                    return false;
                }
            }
            if (!(!player.isFishing() || this.isFishingSkill() || this.altUse() || creature.isSummon() || creature.isPet())) {
                if (bl && creature == player) {
                    player.sendPacket((IStaticPacket)Msg.ONLY_FISHING_SKILLS_ARE_AVAILABLE);
                }
                return false;
            }
            if (player.isOlyParticipant() && this.isOffensive() && !player.isOlyCompetitionStarted() && this.getId() != 347) {
                return false;
            }
        }
        if (this.getFlyType() != FlyToLocation.FlyType.NONE && this.getId() != 628 && this.getId() != 821 && (creature.isImmobilized() || creature.isRooted())) {
            if (bl) {
                creature.getPlayer().sendPacket((IStaticPacket)Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
            }
            return false;
        }
        if (bl4 && creature2 != null && this.getFlyType() == FlyToLocation.FlyType.CHARGE && creature.isInRange(creature2.getLoc(), (long)Math.min(150, this.getFlyRadius()))) {
            if (bl) {
                creature.getPlayer().sendPacket((IStaticPacket)Msg.THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED);
            }
            return false;
        }
        SystemMsg systemMsg = this.checkTarget(creature, creature2, creature2, bl2, bl4);
        if (systemMsg != null && creature.getPlayer() != null) {
            if (bl) {
                creature.getPlayer().sendPacket((IStaticPacket)systemMsg);
            }
            return false;
        }
        if (this._preCondition.length == 0) {
            return true;
        }
        object = new Env();
        ((Env)object).character = creature;
        ((Env)object).skill = this;
        ((Env)object).target = creature2;
        if (bl4) {
            for (Condition condition : this._preCondition) {
                if (condition.test((Env)object)) continue;
                SystemMsg systemMsg2 = condition.getSystemMsg();
                if (bl && systemMsg2 != null) {
                    if (systemMsg2.size() > 0) {
                        creature.sendPacket((IStaticPacket)new SystemMessage2(systemMsg2).addSkillName(this));
                    } else {
                        creature.sendPacket((IStaticPacket)systemMsg2);
                    }
                }
                return false;
            }
        }
        return true;
    }

    public int getSecondSkill() {
        return this._secondSkill;
    }

    public SystemMsg checkTarget(Creature creature, Creature creature2, Creature creature3, boolean bl, boolean bl2) {
        Player player;
        if (creature2 == creature && this.isNotTargetAoE() || creature2 == creature.getPet() && this._targetType == SkillTargetType.TARGET_PET_AURA) {
            return null;
        }
        if (creature2 == null || this.isOffensive() && creature2 == creature) {
            return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
        }
        if (creature.getReflection() != creature2.getReflection()) {
            return SystemMsg.CANNOT_SEE_TARGET;
        }
        if (creature2 != creature && creature2 == creature3 && this.getCastRange() > 0 && this.getCastRange() < Short.MAX_VALUE) {
            if (!GeoEngine.canSeeTarget(creature, creature2, creature.isFlying())) {
                return SystemMsg.CANNOT_SEE_TARGET;
            }
            if (!bl2) {
                int n = (int)((double)Math.max(0, this.getEffectiveRange()) + creature.getMinDistance(creature2) + 16.0);
                if (!creature.isInRange(creature2.getLoc(), (long)n)) {
                    return SystemMsg.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED;
                }
            }
        }
        if (this._skillType == SkillType.TAKECASTLE) {
            return null;
        }
        if (!bl2 && creature2 != creature && (this._targetType == SkillTargetType.TARGET_MULTIFACE || this._targetType == SkillTargetType.TARGET_MULTIFACE_AURA || this._targetType == SkillTargetType.TARGET_TUNNEL) && (this._isBehind ? PositionUtils.isFacing(creature, creature2, 120) : !PositionUtils.isFacing(creature, creature2, 60))) {
            return SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE;
        }
        if ((creature2.isDead() != this._isCorpse && this._targetType != SkillTargetType.TARGET_AREA_AIM_CORPSE || this._isUndeadOnly && !creature2.isUndead()) && (this.isNotTargetAoE() || !Config.FIRST_CONDITION_CHECK_ON_DEATH_TARGET || bl2)) {
            return SystemMsg.INVALID_TARGET;
        }
        if (this._isAltUse || this._targetType == SkillTargetType.TARGET_FEEDABLE_BEAST || this._targetType == SkillTargetType.TARGET_UNLOCKABLE || this._targetType == SkillTargetType.TARGET_CHEST) {
            return null;
        }
        Player player2 = creature.getPlayer();
        if (player2 != null && (player = creature2.getPlayer()) != null) {
            if (this.isPvM()) {
                return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
            }
            if (!Config.CAN_ATTACK_FROM_ANOTHER_ZONE_TO_EPIC && player2.isInZone(Zone.ZoneType.epic) != player.isInZone(Zone.ZoneType.epic)) {
                return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
            }
            if (!player2.isOlyParticipant() && player.isOlyParticipant() || player2.isOlyParticipant() && !player.isOlyParticipant() || player2.isOlyParticipant() && player.isOlyParticipant() && player2.getOlyParticipant().getCompetition() != player.getOlyParticipant().getCompetition()) {
                return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
            }
            if (this.isOffensive()) {
                if (player2.isOlyParticipant() && player.isOlyParticipant() && player2.getOlyParticipant().getCompetition() != player.getOlyParticipant().getCompetition()) {
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }
                if (player2.isOlyParticipant() && !player2.isOlyCompetitionStarted()) {
                    return SystemMsg.INVALID_TARGET;
                }
                if (player2.isOlyParticipant() && player2.getOlyParticipant() == player.getOlyParticipant()) {
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }
                if (player.isOlyParticipant() && player.isLooseOlyCompetition()) {
                    return SystemMsg.INVALID_TARGET;
                }
                if (player2.getTeam() != TeamType.NONE && player2.getTeam() == player.getTeam()) {
                    return SystemMsg.THAT_IS_AN_INCORRECT_TARGET;
                }
                if (this.isAoE() && this.getCastRange() < Short.MAX_VALUE && !GeoEngine.canSeeTarget(creature, creature2, creature.isFlying())) {
                    return SystemMsg.CANNOT_SEE_TARGET;
                }
                if (creature.isInZoneBattle() != creature2.isInZoneBattle() && !player2.getPlayerAccess().PeaceAttack) {
                    return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                }
                if ((creature.isInZonePeace() || creature2.isInZonePeace()) && !player2.getPlayerAccess().PeaceAttack) {
                    return SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
                }
                if (this.isAoE() && player2.getParty() != null && player2.getParty() == player.getParty()) {
                    return SystemMsg.INVALID_TARGET;
                }
                if (creature.isInZoneBattle()) {
                    if (!bl && !this.isForceUse() && player2.getParty() != null && player2.getParty() == player.getParty()) {
                        return SystemMsg.INVALID_TARGET;
                    }
                    return null;
                }
                SystemMsg systemMsg = null;
                for (GlobalEvent globalEvent : player2.getEvents()) {
                    systemMsg = globalEvent.checkForAttack(creature2, creature, this, bl);
                    if (systemMsg == null) continue;
                    return systemMsg;
                }
                for (GlobalEvent globalEvent : player2.getEvents()) {
                    if (!globalEvent.canAttack(creature2, creature, this, bl)) continue;
                    return null;
                }
                if (this.isProvoke()) {
                    if (!bl && player2.getParty() != null && player2.getParty() == player.getParty()) {
                        return SystemMsg.INVALID_TARGET;
                    }
                    return null;
                }
                if (this.isPvpSkill() || !bl || this.isAoE()) {
                    if (player2 == player) {
                        return SystemMsg.INVALID_TARGET;
                    }
                    if (player2.getParty() != null && player2.getParty() == player.getParty()) {
                        return SystemMsg.INVALID_TARGET;
                    }
                    if (player2.getClan() != null && player2.getClan() == player.getClan()) {
                        return SystemMsg.INVALID_TARGET;
                    }
                    if (Config.ALLY_ALLOW_BUFF_DEBUFFS && player2.getAlliance() != null && player2.getAlliance() == player.getAlliance()) {
                        return SystemMsg.INVALID_TARGET;
                    }
                }
                if (creature.isInZone(Zone.ZoneType.SIEGE) && creature2.isInZone(Zone.ZoneType.SIEGE)) {
                    return null;
                }
                if (creature.isInZone(Zone.ZoneType.fun) && creature2.isInZone(Zone.ZoneType.fun)) {
                    return null;
                }
                if (player2.atMutualWarWith(player)) {
                    return null;
                }
                if (this.isForceUse()) {
                    return null;
                }
                if (player.getPvpFlag() != 0) {
                    return null;
                }
                if (player.getKarma() > 0) {
                    return null;
                }
                if (!(!bl || this.isPvpSkill() || this.isAoE() && creature3 != creature2)) {
                    return null;
                }
                if (player2.isCursedWeaponEquipped()) {
                    return null;
                }
                return SystemMsg.INVALID_TARGET;
            }
            if (player == player2) {
                return null;
            }
            if (player2.isOlyParticipant() && player2.getOlyParticipant().getCompetition() == player.getOlyParticipant().getCompetition() && player2.getOlyParticipant() != player.getOlyParticipant()) {
                if (player2.getOlyParticipant().getCompetition().getType() == CompetitionType.TEAM_CLASS_FREE) {
                    return SystemMsg.INVALID_TARGET;
                }
                if (!bl) {
                    return SystemMsg.INVALID_TARGET;
                }
            }
            if (!creature.isInZoneBattle() && creature2.isInZoneBattle()) {
                return SystemMsg.INVALID_TARGET;
            }
            if (bl || this.isForceUse()) {
                return null;
            }
            if (player2.getParty() != null && player2.getParty() == player.getParty()) {
                return null;
            }
            if (player2.getClan() != null && player2.getClan() == player.getClan()) {
                return null;
            }
            if (Config.ALLY_ALLOW_BUFF_DEBUFFS && player2.getAlliance() != null && player2.getAlliance() == player.getAlliance()) {
                return null;
            }
            if (player2.atMutualWarWith(player)) {
                return SystemMsg.INVALID_TARGET;
            }
            if (player.getPvpFlag() != 0) {
                return SystemMsg.INVALID_TARGET;
            }
            if (player.getKarma() > 0) {
                return SystemMsg.INVALID_TARGET;
            }
            return null;
        }
        if (this.isAoE() && this.isOffensive() && this.getCastRange() < Short.MAX_VALUE && !GeoEngine.canSeeTarget(creature, creature2, creature.isFlying())) {
            return SystemMsg.CANNOT_SEE_TARGET;
        }
        if (!bl && !this.isForceUse() && !this.isOffensive() && creature2.isAutoAttackable(creature)) {
            return SystemMsg.INVALID_TARGET;
        }
        if (!bl && !this.isForceUse() && this.isOffensive() && !creature2.isAutoAttackable(creature)) {
            return SystemMsg.INVALID_TARGET;
        }
        if (!creature2.isAttackable(creature)) {
            return SystemMsg.INVALID_TARGET;
        }
        return null;
    }

    public final Creature getAimingTarget(Creature creature, GameObject gameObject) {
        Creature creature2 = gameObject == null || !gameObject.isCreature() ? null : (Creature)gameObject;
        switch (this._targetType) {
            case TARGET_ALLY: 
            case TARGET_ALLY_AND_PARTY: 
            case TARGET_CLAN: 
            case TARGET_PARTY: 
            case TARGET_CLAN_ONLY: 
            case TARGET_SELF: {
                return creature;
            }
            case TARGET_AURA: 
            case TARGET_COMMCHANNEL: 
            case TARGET_MULTIFACE_AURA: {
                return creature;
            }
            case TARGET_HOLY: {
                return creature2 != null && creature.isPlayer() && creature2.isArtefact() ? creature2 : null;
            }
            case TARGET_FLAGPOLE: {
                return creature;
            }
            case TARGET_UNLOCKABLE: {
                return creature2 != null && creature2.isDoor() || creature2 instanceof ChestInstance ? creature2 : null;
            }
            case TARGET_CHEST: {
                return creature2 instanceof ChestInstance ? creature2 : null;
            }
            case TARGET_FEEDABLE_BEAST: {
                return creature2 instanceof FeedableBeastInstance ? creature2 : null;
            }
            case TARGET_PET: 
            case TARGET_PET_AURA: {
                creature2 = creature.getPet();
                return creature2 != null && creature2.isDead() == this._isCorpse ? creature2 : null;
            }
            case TARGET_OWNER: {
                if (!creature.isSummon() && !creature.isPet()) {
                    return null;
                }
                creature2 = creature.getPlayer();
                return creature2 != null && creature2.isDead() == this._isCorpse ? creature2 : null;
            }
            case TARGET_ENEMY_PET: {
                if (creature2 == null || creature2 == creature.getPet() || !creature2.isPet()) {
                    return null;
                }
                return creature2;
            }
            case TARGET_ENEMY_SUMMON: {
                if (creature2 == null || creature2 == creature.getPet() || !creature2.isSummon()) {
                    return null;
                }
                return creature2;
            }
            case TARGET_ENEMY_SERVITOR: {
                if (creature2 == null || creature2 == creature.getPet() || !(creature2 instanceof Summon)) {
                    return null;
                }
                return creature2;
            }
            case TARGET_ONE: {
                return !(creature2 == null || creature2.isDead() != this._isCorpse || creature2 == creature && this.isOffensive() || this._isUndeadOnly && !creature2.isUndead()) ? creature2 : null;
            }
            case TARGET_OTHER: {
                return creature2 != null && creature2 != creature && creature2.isDead() == this._isCorpse && (!this._isUndeadOnly || creature2.isUndead()) ? creature2 : null;
            }
            case TARGET_AREA: 
            case TARGET_AREA_CLAN: {
                return !(creature2 == null || creature2.isDead() != this._isCorpse || creature2 == creature && this.isOffensive() || this._isUndeadOnly && !creature2.isUndead()) ? creature2 : null;
            }
            case TARGET_MULTIFACE: 
            case TARGET_TUNNEL: {
                return creature2 != null && creature2.isDead() == this._isCorpse && (creature2 != creature || !this.isOffensive()) && (!this._isUndeadOnly || creature2.isUndead() && this._isBehind == PositionUtils.isBehind(creature2, creature)) ? creature2 : null;
            }
            case TARGET_AREA_AIM_CORPSE: {
                return creature2 != null && creature2.isDead() ? creature2 : null;
            }
            case TARGET_CORPSE: {
                if (creature2 == null || !creature2.isDead()) {
                    return null;
                }
                if (creature2.isSummon() && creature2 != creature.getPet()) {
                    return creature2;
                }
                return creature2.isNpc() ? creature2 : null;
            }
            case TARGET_CORPSE_PLAYER: {
                return creature2 != null && creature2.isPlayable() && creature2.isDead() ? creature2 : null;
            }
            case TARGET_SIEGE: {
                return creature2 != null && !creature2.isDead() && creature2.isDoor() ? creature2 : null;
            }
        }
        creature.sendMessage("Target type of skill is not currently handled");
        return null;
    }

    public List<Creature> getTargets(Creature creature, Creature creature2, boolean bl) {
        if (this.oneTarget()) {
            LazyArrayList<Creature> lazyArrayList = new LazyArrayList<Creature>(1);
            lazyArrayList.add(creature2);
            return lazyArrayList;
        }
        LazyArrayList<Creature> lazyArrayList = new LazyArrayList<Creature>();
        switch (this._targetType) {
            case TARGET_AREA: 
            case TARGET_MULTIFACE: 
            case TARGET_TUNNEL: 
            case TARGET_AREA_AIM_CORPSE: {
                if (creature2.isDead() == this._isCorpse && (!this._isUndeadOnly || creature2.isUndead())) {
                    lazyArrayList.add(creature2);
                }
                this.a(lazyArrayList, creature2, creature, bl);
                break;
            }
            case TARGET_AREA_CLAN: {
                if (creature.isMonster() || creature.isSiegeGuard()) {
                    lazyArrayList.add(creature);
                    for (Creature creature3 : World.getAroundCharacters(creature, this._skillRadius, 600)) {
                        if (creature3.isDead() || !creature3.isMonster() && !creature3.isSiegeGuard()) continue;
                        lazyArrayList.add(creature3);
                    }
                } else {
                    Player player;
                    Player player2 = creature.getPlayer();
                    if (player2 == null || (player = creature2.getPlayer()) == null) break;
                    int n = player.getClan() != null ? player.getClanId() : -1;
                    this.a(lazyArrayList, player2, player);
                    for (Player player3 : World.getAroundPlayers(creature2, this._skillRadius, 600)) {
                        if (player3 == null || creature == player3 || player2 == player3.getPlayer() || player3.getClanId() != n || this.checkTarget(creature, player3, creature2, true, false) == null) continue;
                        this.a(lazyArrayList, player2, player3);
                    }
                }
                break;
            }
            case TARGET_AURA: 
            case TARGET_MULTIFACE_AURA: {
                this.a(lazyArrayList, creature, creature, bl);
                break;
            }
            case TARGET_COMMCHANNEL: {
                if (creature.getPlayer() == null) break;
                if (creature.getPlayer().isInParty()) {
                    if (creature.getPlayer().getParty().isInCommandChannel()) {
                        for (Player player : creature.getPlayer().getParty().getCommandChannel()) {
                            if (player.isDead() || !player.isInRange(creature, this._skillRadius == 0 ? 600L : (long)this._skillRadius)) continue;
                            lazyArrayList.add(player);
                        }
                        this.a(lazyArrayList, creature.getPlayer(), creature.getPlayer());
                        break;
                    }
                    for (Player player : creature.getPlayer().getParty().getPartyMembers()) {
                        if (player.isDead() || !player.isInRange(creature, this._skillRadius == 0 ? 600L : (long)this._skillRadius)) continue;
                        lazyArrayList.add(player);
                    }
                    this.a(lazyArrayList, creature.getPlayer(), creature.getPlayer());
                    break;
                }
                lazyArrayList.add(creature);
                this.a(lazyArrayList, creature.getPlayer(), creature.getPlayer());
                break;
            }
            case TARGET_PET_AURA: {
                if (creature.getPet() == null) break;
                this.a(lazyArrayList, creature.getPet(), creature, bl);
                break;
            }
            case TARGET_ALLY: 
            case TARGET_ALLY_AND_PARTY: 
            case TARGET_CLAN: 
            case TARGET_PARTY: 
            case TARGET_CLAN_ONLY: {
                if (creature.isMonster() || creature.isSiegeGuard()) {
                    lazyArrayList.add(creature);
                    for (Creature creature4 : World.getAroundCharacters(creature, this._skillRadius, 600)) {
                        if (creature4.isDead() || !creature4.isMonster() && !creature4.isSiegeGuard()) continue;
                        lazyArrayList.add(creature4);
                    }
                    break;
                }
                Player player = creature.getPlayer();
                if (player == null) break;
                for (Player player4 : World.getAroundPlayers(player, this._skillRadius, 600)) {
                    boolean bl2 = false;
                    switch (this._targetType) {
                        case TARGET_PARTY: {
                            bl2 = player.getParty() != null && player.getParty() == player4.getParty();
                            break;
                        }
                        case TARGET_CLAN: {
                            bl2 = player.getClanId() != 0 && player4.getClanId() == player.getClanId() || player.getParty() != null && player4.getParty() == player.getParty();
                            break;
                        }
                        case TARGET_CLAN_ONLY: {
                            bl2 = player.getClanId() != 0 && player4.getClanId() == player.getClanId();
                            break;
                        }
                        case TARGET_ALLY: {
                            bl2 = player.getClanId() != 0 && player4.getClanId() == player.getClanId() || player.getAllyId() != 0 && player4.getAllyId() == player.getAllyId();
                            break;
                        }
                        case TARGET_ALLY_AND_PARTY: {
                            boolean bl3 = bl2 = player.getClanId() != 0 && player4.getClanId() == player.getClanId() || player.getAllyId() != 0 && player4.getAllyId() == player.getAllyId() || player.getParty() != null && player4.getParty() == player.getParty();
                        }
                    }
                    if (!bl2 || player.isOlyParticipant() && player4.isOlyParticipant() && player.getOlyParticipant() != player4.getOlyParticipant() || this.checkTarget(player, player4, creature2, bl, false) != null) continue;
                    this.a(lazyArrayList, player, player4);
                }
                this.a(lazyArrayList, player, player);
                break;
            }
        }
        return lazyArrayList;
    }

    private void a(List<Creature> list, Player player, Player player2) {
        Summon summon;
        if ((player == player2 || player.isInRange(player2, (long)this._skillRadius)) && player2.isDead() == this._isCorpse) {
            list.add(player2);
        }
        if ((summon = player2.getPet()) != null && player.isInRange(summon, (long)this._skillRadius) && summon.isDead() == this._isCorpse) {
            list.add(summon);
        }
    }

    private void a(List<Creature> list, Creature creature, Creature creature2, boolean bl) {
        int n = 0;
        AbstractShape abstractShape = null;
        if (this._targetType == SkillTargetType.TARGET_TUNNEL) {
            int n2 = 100;
            int n3 = creature2.getZ() - 200;
            int n4 = creature2.getZ() + 200;
            int n5 = creature.getZ() - 200;
            int n6 = creature.getZ() + 200;
            double d = PositionUtils.convertHeadingToDegree(creature2.getHeading());
            double d2 = Math.toRadians(d - 90.0);
            double d3 = Math.toRadians(d + 90.0);
            abstractShape = new Polygon();
            ((Polygon)abstractShape).add(creature2.getX() + (int)(Math.cos(d2) * (double)n2), creature2.getY() + (int)(Math.sin(d2) * (double)n2));
            ((Polygon)abstractShape).add(creature2.getX() + (int)(Math.cos(d3) * (double)n2), creature2.getY() + (int)(Math.sin(d3) * (double)n2));
            ((Polygon)abstractShape).add(creature.getX() + (int)(Math.cos(d3) * (double)n2), creature.getY() + (int)(Math.sin(d3) * (double)n2));
            ((Polygon)abstractShape).add(creature.getX() + (int)(Math.cos(d2) * (double)n2), creature.getY() + (int)(Math.sin(d2) * (double)n2));
            ((Polygon)abstractShape).setZmin(Math.min(n3, n5)).setZmax(Math.max(n4, n6));
        }
        for (Creature creature3 : creature.getAroundCharacters(this._skillRadius, 300)) {
            if (abstractShape != null && !abstractShape.isInside(creature3.getX(), creature3.getY(), creature3.getZ()) || creature3 == null || creature2 == creature3 || creature2.getPlayer() != null && creature2.getPlayer() == creature3.getPlayer() || this.checkTarget(creature2, creature3, creature, bl, false) != null || creature2.isNpc() && creature3.isNpc()) continue;
            list.add(creature3);
            if (!this.isOffensive() || ++n < 20 || creature2.isRaid()) continue;
            break;
        }
    }

    public final void getEffects(Creature creature, Creature creature2, boolean bl, boolean bl2) {
        this.getEffects(creature, creature2, bl, bl2, false);
    }

    public final void getEffects(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        double d = 1.0;
        if (this.isMusic()) {
            d = Config.SONGDANCETIME_MODIFIER;
        } else if (this.getId() >= 4342 && this.getId() <= 4360) {
            d = Config.CLANHALL_BUFFTIME_MODIFIER;
        }
        this.getEffects(creature, creature2, bl, bl2, 0L, d, bl3);
    }

    public final void getEffects(final Creature creature, final Creature creature2, final boolean bl, final boolean bl2, final long l, final double d, final boolean bl3) {
        if (this.isPassive() || !this.hasEffects() || creature == null || creature2 == null) {
            return;
        }
        boolean bl4 = false;
        if (this.getId() == 345 || this.getId() == 346 || this.getId() == 321 || this.getId() == 369 || this.getId() == 1231) {
            boolean bl5 = bl4 = creature2 == creature;
        }
        if (!bl4 && (creature2.isEffectImmune() || creature2.isInvul() && this.isOffensive())) {
            if (creature.isPlayer()) {
                creature.sendPacket((IStaticPacket)new SystemMessage(139).addName(creature2).addSkillName(this._displayId, this._displayLevel));
            }
            return;
        }
        if (creature2.isDoor() || creature2.isAlikeDead() && !this.isPreservedOnDeath()) {
            return;
        }
        ThreadPoolManager.getInstance().execute(new RunnableImpl(){

            @Override
            public void runImpl() {
                boolean bl4 = false;
                boolean bl22 = false;
                int n = creature.getChargedSpiritShot();
                if (creature.getSkillMastery(Skill.this.getId()) == 2) {
                    bl22 = true;
                    creature.removeSkillMastery(Skill.this.getId());
                }
                for (EffectTemplate effectTemplate : Skill.this.getEffectTemplates()) {
                    Object object;
                    if (bl2 != effectTemplate._applyOnCaster || effectTemplate._count == 0) continue;
                    Creature creature3 = effectTemplate._applyOnCaster || effectTemplate._isReflectable && bl3 ? creature : creature2;
                    LazyArrayList<Object> lazyArrayList = new LazyArrayList<Object>(1);
                    lazyArrayList.add(creature3);
                    if (effectTemplate._applyOnSummon && creature3.isPlayer() && (object = creature3.getPlayer().getPet()) != null && ((GameObject)object).isSummon() && !Skill.this.isOffensive() && !Skill.this.isToggle() && !Skill.this.isCubicSkill()) {
                        lazyArrayList.add(object);
                    }
                    Iterator<Creature> effectTargets = (Iterator<Creature>)(Iterator<?>)lazyArrayList.iterator();
                    block1: while (effectTargets.hasNext()) {
                        Effect effect2;
                        Creature creature22 = effectTargets.next();
                        if (creature22.isAlikeDead() && !Skill.this.isPreservedOnDeath() || creature22.isRaid() && effectTemplate.getEffectType().isRaidImmune() || effectTemplate.getPeriod() > 0L && (creature2.isBuffImmune() && !Skill.this.isOffensive() && (creature != creature2 || Optional.ofNullable(creature2.getEffectList().getEffectByType(EffectType.BuffImmunity)).map(effect -> (EffectBuffImmunity)effect).map(effectBuffImmunity -> effectBuffImmunity.isIgnoreSelfBuff()).orElse(false).booleanValue()) && !Config.BLOCK_BUFF_EXCLUDE.contains(Skill.this.getId()) || creature2.isDebuffImmune() && Skill.this.isOffensive()) || Skill.this.isBlockedByChar(creature22, effectTemplate)) continue;
                        if (effectTemplate._stackOrder == -1) {
                            if (!effectTemplate._stackType.equals("none")) {
                                for (Effect effect3 : creature22.getEffectList().getAllEffects()) {
                                    if (!effect3.getStackType().equalsIgnoreCase(effectTemplate._stackType)) continue;
                                    continue block1;
                                }
                            } else if (creature22.getEffectList().getEffectsBySkillId(Skill.this.getId()) != null) continue;
                        }
                        Env env = new Env(creature, creature22, Skill.this);
                        int n2 = effectTemplate.chance(Skill.this.getActivateRate());
                        if ((bl || n2 >= 0) && !effectTemplate._applyOnCaster) {
                            env.value = n2;
                            if (!Formulas.calcSkillSuccess(env, effectTemplate, n)) continue;
                        }
                        if (Skill.this._isReflectable && effectTemplate._isReflectable && Skill.this.isOffensive() && !creature.isInvul() && !creature.isDebuffImmune() && creature22 != creature && !creature.isTrap() && Rnd.chance(creature22.calcStat(Skill.this.isMagic() ? Stats.REFLECT_MAGIC_DEBUFF : Stats.REFLECT_PHYSIC_DEBUFF, 0.0, creature, Skill.this))) {
                            creature22.sendPacket((IStaticPacket)new SystemMessage(1998).addName(creature));
                            creature.sendPacket((IStaticPacket)new SystemMessage(1999).addName(creature22));
                            env.target = creature22 = creature;
                        }
                        if (bl4) {
                            env.value = 2.147483647E9;
                        }
                        if ((effect2 = effectTemplate.getEffect(env)) == null) continue;
                        if (n2 > 0) {
                            bl4 = true;
                        }
                        if (effect2.isOneTime()) {
                            if (!effect2.checkCondition()) continue;
                            effect2.onStart();
                            effect2.onActionTime();
                            effect2.onExit();
                            continue;
                        }
                        int n3 = effectTemplate.getCount();
                        long l2 = effectTemplate.getPeriod();
                        if (bl22) {
                            if (n3 > 1) {
                                n3 *= 2;
                            } else {
                                l2 *= 2L;
                            }
                        }
                        if (Config.CALC_EFFECT_TIME_YIELD_AND_RESIST && !effectTemplate._applyOnCaster && Skill.this.isOffensive() && !Skill.this.isIgnoreResists() && !creature.isRaid()) {
                            double d3 = 0.0;
                            Pair<Stats, Stats> pair = effectTemplate.getEffectType().getResistAndPowerType();
                            if (pair != null) {
                                Stats stats = pair.getLeft();
                                Stats stats2 = pair.getRight();
                                if (stats != null) {
                                    d3 += creature2.calcStat(stats, creature, Skill.this);
                                }
                                if (stats2 != null) {
                                    d3 -= creature.calcStat(stats2, creature2, Skill.this);
                                }
                            }
                            if ((d3 += creature2.calcStat(Stats.DEBUFF_RESIST, creature, Skill.this)) != 0.0) {
                                double d2 = 1.0 + Math.abs(0.01 * d3);
                                if (d3 > 0.0) {
                                    d2 = 1.0 / d2;
                                }
                                if (n3 > 1) {
                                    n3 = (int)Math.floor(Math.max((double)n3 * d2, 1.0));
                                } else {
                                    l2 = (long)Math.floor(Math.max((double)l2 * d2, 1.0));
                                }
                            }
                        }
                        if (l > 0L) {
                            l2 = n3 > 1 ? l / (long)n3 : l;
                        } else if (d > 1.0) {
                            if (n3 > 1) {
                                n3 = (int)((double)n3 * d);
                            } else {
                                l2 = (long)((double)l2 * d);
                            }
                        }
                        Skill skill = effect2.getSkill();
                        if (skill != null && Config.SKILL_DURATION_MOD.containsKey(skill.getId()) && (Config.SKILL_DURATION_MOD_AT_OLY || !creature.isOlyParticipant())) {
                            int n4 = Config.SKILL_DURATION_MOD.get(skill.getId());
                            if (skill.getLevel() >= 100 && skill.getLevel() < 140) {
                                if (n3 > 1) {
                                    n3 = n4;
                                } else {
                                    l2 = n4;
                                }
                            } else if (n3 > 1) {
                                n3 = n4;
                            } else {
                                l2 = n4;
                            }
                        }
                        effect2.setCount(n3);
                        effect2.setPeriod(l2);
                        effect2.schedule();
                    }
                }
                if (bl) {
                    if (bl4) {
                        creature.sendPacket((IStaticPacket)new SystemMessage(1595).addSkillName(Skill.this._displayId, Skill.this._displayLevel));
                    } else {
                        creature.sendPacket((IStaticPacket)new SystemMessage(1597).addSkillName(Skill.this._displayId, Skill.this._displayLevel));
                    }
                }
            }
        });
    }

    public final void attach(EffectTemplate effectTemplate) {
        this._effectTemplates = ArrayUtils.add(this._effectTemplates, effectTemplate);
        if (!effectTemplate._applyOnCaster) {
            this.Ll = true;
        }
    }

    public EffectTemplate[] getEffectTemplates() {
        return this._effectTemplates;
    }

    public boolean hasEffects() {
        return this._effectTemplates.length > 0;
    }

    public boolean hasNotSelfEffects() {
        return this.Ll;
    }

    public final Func[] getStatFuncs() {
        return this.getStatFuncs(this);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return this.hashCode() == ((Skill)object).hashCode();
    }

    public int hashCode() {
        return this.pr;
    }

    public final void attach(Condition condition) {
        this._preCondition = ArrayUtils.add(this._preCondition, condition);
    }

    public final boolean altUse() {
        return this._isAltUse;
    }

    public final boolean canTeachBy(int n) {
        return this._teachers == null || this._teachers.contains(n);
    }

    public final int getActivateRate() {
        return this._activateRate;
    }

    public AddedSkill[] getAddedSkills() {
        return this._addedSkills;
    }

    public final boolean getCanLearn(ClassId classId) {
        return this._canLearn == null || this._canLearn.contains((Object)classId);
    }

    public final int getCastRange() {
        return this._castRange;
    }

    public void setCastRange(int n) {
        this._castRange = n;
    }

    public int getEffectiveRange() {
        return this._effectiveRange;
    }

    public final int getAOECastRange() {
        return Math.max(this._castRange, this._skillRadius);
    }

    public final int getCoolTime() {
        return this._coolTime;
    }

    public boolean getCorpse() {
        return this._isCorpse;
    }

    public int getDelayedEffect() {
        return this._delayedEffect;
    }

    public final int getDisplayId() {
        return this._displayId;
    }

    public int getDisplayLevel() {
        return this._displayLevel;
    }

    public void setDisplayLevel(int n) {
        this._displayLevel = n;
    }

    public int getEffectPoint() {
        return this._effectPoint;
    }

    public Effect getSameByStackType(List<Effect> list) {
        for (EffectTemplate effectTemplate : this.getEffectTemplates()) {
            Effect effect;
            if (effectTemplate == null || (effect = effectTemplate.getSameByStackType(list)) == null) continue;
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

    public final Element getElement() {
        return this._element;
    }

    public final int getElementPower() {
        return this._elementPower;
    }

    public Skill getFirstAddedSkill() {
        if (this._addedSkills.length == 0) {
            return null;
        }
        return this._addedSkills[0].getSkill();
    }

    public int getFlyRadius() {
        return this._flyRadius;
    }

    public FlyToLocation.FlyType getFlyType() {
        return this._flyType;
    }

    public boolean isFlyToBack() {
        return this._flyToBack;
    }

    public final int getHitTime() {
        return this._hitTime;
    }

    public void setHitTime(int n) {
        this._hitTime = n;
    }

    public final int getHpConsume() {
        return this._hpConsume;
    }

    public void setHpConsume(int n) {
        this._hpConsume = n;
    }

    public int getId() {
        return this._id;
    }

    public void setId(int n) {
        this._id = n;
    }

    public final int[] getItemConsume() {
        return this._itemConsume;
    }

    public final int[] getItemConsumeId() {
        return this._itemConsumeId;
    }

    public final int getReferenceItemId() {
        return this._referenceItemId;
    }

    public final int getReferenceItemMpConsume() {
        return this._referenceItemMpConsume;
    }

    public final int getLevel() {
        return this._level;
    }

    public final int getBaseLevel() {
        return this._baseLevel;
    }

    public final void setBaseLevel(int n) {
        this._baseLevel = n;
    }

    public final int getLevelModifier() {
        return this._levelModifier;
    }

    public final int getMagicLevel() {
        return this._magicLevel;
    }

    public final void setMagicLevel(int n) {
        this._magicLevel = n;
    }

    public int getMatak() {
        return this._matak;
    }

    public int getMinPledgeClass() {
        return this._minPledgeClass;
    }

    public int getMinRank() {
        return this._minRank;
    }

    public final double getMpConsume() {
        return this._mpConsume1 + this._mpConsume2;
    }

    public final double getMpConsume1() {
        return this._mpConsume1;
    }

    public void setMpConsume1(double d) {
        this._mpConsume1 = d;
    }

    public final double getMpConsume2() {
        return this._mpConsume2;
    }

    public void setMpConsume2(double d) {
        this._mpConsume2 = d;
    }

    public final String getName() {
        return this._name;
    }

    public void setName(String string) {
        this._name = string;
    }

    public int getNegatePower() {
        return this._negatePower;
    }

    public int getNegateSkill() {
        return this._negateSkill;
    }

    public SkillNextAction getSkillNextAction() {
        return this._skillNextAction;
    }

    public int getNpcId() {
        return this._npcId;
    }

    public int getNumCharges() {
        return this._numCharges;
    }

    public final double getPower(Creature creature) {
        if (creature != null) {
            if (creature.isPlayable()) {
                return this.getPowerPvP();
            }
            if (creature.isMonster()) {
                return this.getPowerPvE();
            }
        }
        return this.getPower();
    }

    public final double getPower() {
        return this._power;
    }

    public final void setPower(double d) {
        this._power = d;
    }

    public final double getBaseBlowRate() {
        return this._baseBlowRate;
    }

    public final double getPowerPvP() {
        return this._powerPvP != 0.0 ? this._powerPvP : this._power;
    }

    public final double getPowerPvE() {
        return this._powerPvE != 0.0 ? this._powerPvE : this._power;
    }

    public final long getReuseDelay() {
        return this._reuseDelay;
    }

    public final void setReuseDelay(long l) {
        this._reuseDelay = l;
    }

    public final boolean getShieldIgnore() {
        return this._isShieldignore;
    }

    public final boolean isReflectable() {
        return this._isReflectable;
    }

    public final int getSkillInterruptTime() {
        return this._skillInterruptTime;
    }

    public void setSkillInterruptTime(int n) {
        this._skillInterruptTime = n;
    }

    public final int getSkillRadius() {
        return this._skillRadius;
    }

    public final SkillType getSkillType() {
        return this._skillType;
    }

    public int getSoulsConsume() {
        return this._soulsConsume;
    }

    public int getSymbolId() {
        return this._symbolId;
    }

    public final SkillTargetType getTargetType() {
        return this._targetType;
    }

    public final SkillTrait getTraitType() {
        return this._traitType;
    }

    public final BaseStats getSaveVs() {
        return this._saveVs;
    }

    public final int getWeaponsAllowed() {
        return this._weaponsAllowed;
    }

    public double getLethal1() {
        return this._lethal1;
    }

    public double getLethal2() {
        return this._lethal2;
    }

    public String getBaseValues() {
        return this._baseValues;
    }

    public boolean isBlockedByChar(Creature creature, EffectTemplate effectTemplate) {
        if (effectTemplate.getAttachedFuncs() == null) {
            return false;
        }
        for (FuncTemplate funcTemplate : effectTemplate.getAttachedFuncs()) {
            if (funcTemplate == null || !creature.checkBlockedStat(funcTemplate._stat)) continue;
            return true;
        }
        return false;
    }

    public final boolean isCancelable() {
        return this._isCancelable && this.getSkillType() != SkillType.TRANSFORMATION && !this.isToggle();
    }

    public final boolean isCommon() {
        return this._isCommon;
    }

    public final int getCriticalRate() {
        return this._criticalRate;
    }

    public final boolean isHandler() {
        return this._isItemHandler;
    }

    public final boolean isMagic() {
        return this._magicType == SkillMagicType.MAGIC;
    }

    public final SkillMagicType getMagicType() {
        return this._magicType;
    }

    public void setMagicType(SkillMagicType skillMagicType) {
        this._magicType = skillMagicType;
    }

    public final boolean isNewbie() {
        return this._isNewbie;
    }

    public final boolean isPreservedOnDeath() {
        return this._isPreservedOnDeath;
    }

    public final boolean isHeroic() {
        return this._isHeroic;
    }

    public final boolean isSelfDispellable() {
        return this._isSelfDispellable;
    }

    public void setOperateType(SkillOpType skillOpType) {
        this._operateType = skillOpType;
    }

    public final boolean isOverhit() {
        return this._isOverhit;
    }

    public void setOverhit(boolean bl) {
        this._isOverhit = bl;
    }

    public final boolean isActive() {
        return this._operateType == SkillOpType.OP_ACTIVE;
    }

    public final boolean isPassive() {
        return this._operateType == SkillOpType.OP_PASSIVE;
    }

    public boolean isSaveable() {
        if (!Config.ALT_SAVE_UNSAVEABLE && (this.isMusic() || this._name.startsWith("Herb of"))) {
            return false;
        }
        return this._isSaveable;
    }

    public final boolean isSkillTimePermanent() {
        return this._isSkillTimePermanent || this._isItemHandler || this._name.contains("Talisman");
    }

    public final boolean isReuseDelayPermanent() {
        return this._isReuseDelayPermanent || this._isItemHandler;
    }

    public boolean isDeathlink() {
        return this._deathlink;
    }

    public boolean isBasedOnTargetDebuff() {
        return this._basedOnTargetDebuff;
    }

    public boolean isSoulBoost() {
        return this._isSoulBoost;
    }

    public boolean isChargeBoost() {
        return this._isChargeBoost;
    }

    public boolean isUsingWhileCasting() {
        return this._isUsingWhileCasting;
    }

    public boolean isBehind() {
        return this._isBehind;
    }

    public boolean isHideStartMessage() {
        return this._hideStartMessage;
    }

    public boolean isHideUseMessage() {
        return this._hideUseMessage;
    }

    public boolean isSSPossible() {
        return this._isUseSS == Ternary.TRUE || this._isUseSS == Ternary.DEFAULT && !this._isItemHandler && !this.isMusic() && this.isActive() && (this.getTargetType() != SkillTargetType.TARGET_SELF || this.isMagic());
    }

    public final boolean isSuicideAttack() {
        return this._isSuicideAttack;
    }

    public final boolean isToggle() {
        return this._operateType == SkillOpType.OP_TOGGLE;
    }

    public boolean isItemSkill() {
        return this._name.contains("Item Skill") || this._name.contains("Talisman");
    }

    public String toString() {
        return this._name + "[id=" + this._id + ",lvl=" + this._level + "]";
    }

    public abstract void useSkill(Creature var1, List<Creature> var2);

    public boolean isAoE() {
        switch (this._targetType) {
            case TARGET_AURA: 
            case TARGET_MULTIFACE_AURA: 
            case TARGET_PET_AURA: 
            case TARGET_AREA: 
            case TARGET_AREA_CLAN: 
            case TARGET_MULTIFACE: 
            case TARGET_TUNNEL: 
            case TARGET_AREA_AIM_CORPSE: {
                return true;
            }
        }
        return false;
    }

    public boolean isNotTargetAoE() {
        switch (this._targetType) {
            case TARGET_ALLY: 
            case TARGET_ALLY_AND_PARTY: 
            case TARGET_CLAN: 
            case TARGET_PARTY: 
            case TARGET_CLAN_ONLY: 
            case TARGET_AURA: 
            case TARGET_MULTIFACE_AURA: {
                return true;
            }
        }
        return false;
    }

    public boolean isOffensive() {
        return this._isOffensive;
    }

    public final boolean isForceUse() {
        return this._isForceUse;
    }

    public boolean isAI() {
        return this._skillType.isAI();
    }

    public boolean isPvM() {
        return this._isPvm;
    }

    public final boolean isPvpSkill() {
        return this._isPvpSkill;
    }

    public final boolean isFishingSkill() {
        return this._isFishingSkill;
    }

    public boolean isMusic() {
        return this._magicType == SkillMagicType.MUSIC;
    }

    public boolean isTrigger() {
        return this._isTrigger;
    }

    public boolean isSlotNone() {
        return this._isSlotNone;
    }

    public boolean oneTarget() {
        switch (this._targetType) {
            case TARGET_SELF: 
            case TARGET_HOLY: 
            case TARGET_FLAGPOLE: 
            case TARGET_UNLOCKABLE: 
            case TARGET_CHEST: 
            case TARGET_FEEDABLE_BEAST: 
            case TARGET_PET: 
            case TARGET_OWNER: 
            case TARGET_ENEMY_PET: 
            case TARGET_ENEMY_SUMMON: 
            case TARGET_ENEMY_SERVITOR: 
            case TARGET_ONE: 
            case TARGET_OTHER: 
            case TARGET_CORPSE: 
            case TARGET_CORPSE_PLAYER: 
            case TARGET_SIEGE: 
            case TARGET_ITEM: 
            case TARGET_NONE: {
                return true;
            }
        }
        return false;
    }

    public int getCancelTarget() {
        return this._cancelTarget;
    }

    public boolean isSkillInterrupt() {
        return this._skillInterrupt;
    }

    public boolean isNotUsedByAI() {
        return this._isNotUsedByAI;
    }

    public boolean isIgnoreResists() {
        return this._isIgnoreResists;
    }

    public boolean isIgnoreInvul() {
        return this._isIgnoreInvul;
    }

    public boolean isSharedClassReuse() {
        return this._isSharedClassReuse;
    }

    public boolean isNotAffectedByMute() {
        return this._isNotAffectedByMute;
    }

    public boolean flyingTransformUsage() {
        return this._flyingTransformUsage;
    }

    public boolean canUseTeleport() {
        return this._canUseTeleport;
    }

    public int getEnchantLevelCount() {
        return this._enchantLevelCount;
    }

    public void setEnchantLevelCount(int n) {
        this._enchantLevelCount = n;
    }

    public boolean isClanSkill() {
        return this._id >= 370 && this._id <= 391 || this._id >= 611 && this._id <= 616;
    }

    public boolean isBaseTransformation() {
        return this._id >= 810 && this._id <= 813 || this._id >= 1520 && this._id <= 1522 || this._id == 538;
    }

    public boolean isSummonerTransformation() {
        return this._id >= 929 && this._id <= 931;
    }

    public void onAbortCast(Creature creature, Creature creature2) {
        if (this.isUsingWhileCasting() && creature2 != null) {
            creature2.getEffectList().stopEffect(this.getId());
        }
    }

    public double getSimpleDamage(Creature creature, Creature creature2) {
        if (this.isMagic()) {
            double d = creature.getMAtk(creature2, this);
            double d2 = creature2.getMDef(null, this);
            double d3 = this.getPower();
            int n = creature.getChargedSpiritShot() > 0 && this.isSSPossible() ? creature.getChargedSpiritShot() * 2 : 1;
            return 91.0 * d3 * Math.sqrt((double)n * d) / d2;
        }
        double d = creature.getPAtk(creature2);
        double d4 = creature2.getPDef(creature);
        double d5 = this.getPower();
        int n = creature.getChargedSoulShot() && this.isSSPossible() ? 2 : 1;
        return (double)n * (d + d5) * 70.0 / d4;
    }

    public long getReuseForMonsters() {
        long l = 1000L;
        switch (this._skillType) {
            case PARALYZE: 
            case DEBUFF: 
            case NEGATE_EFFECTS: 
            case NEGATE_STATS: 
            case STEAL_BUFF: {
                l = 10000L;
                break;
            }
            case STUN: 
            case MUTE: 
            case ROOT: 
            case SLEEP: {
                l = 5000L;
            }
        }
        return Math.max(Math.max((long)(this._hitTime + this._coolTime), this._reuseDelay), l);
    }

    public double getAbsorbPart() {
        return this._absorbPart;
    }

    public boolean isProvoke() {
        return this._isProvoke;
    }

    public String getIcon() {
        return this._icon;
    }

    public int getEnergyConsume() {
        return this._energyConsume;
    }

    public boolean isCubicSkill() {
        return this._isCubicSkill;
    }

    public void setCubicSkill(boolean bl) {
        this._isCubicSkill = bl;
    }

    public boolean isBlowSkill() {
        return false;
    }

    public boolean isSpoilSkill() {
        switch (this.getId()) {
            case 254: 
            case 302: 
            case 348: 
            case 537: 
            case 947: {
                return true;
            }
        }
        return false;
    }

    public boolean isSweepSkill() {
        switch (this.getId()) {
            case 42: 
            case 444: {
                return true;
            }
        }
        return false;
    }

    public static class AddedSkill {
        public static final AddedSkill[] EMPTY_ARRAY = new AddedSkill[0];
        public int id;
        public int level;
        private Skill _skill;

        public AddedSkill(int n, int n2) {
            this.id = n;
            this.level = n2;
        }

        public Skill getSkill() {
            if (this._skill == null) {
                this._skill = SkillTable.getInstance().getInfo(this.id, this.level);
            }
            return this._skill;
        }
    }

    public static enum SkillType {
        AGGRESSION(Aggression.class),
        AIEFFECTS(AIeffects.class),
        BALANCE(Balance.class),
        BEAST_FEED(BeastFeed.class),
        BLEED(Continuous.class),
        BUFF(Continuous.class),
        BUFF_CHARGER(BuffCharger.class),
        CALL(Call.class),
        CLAN_GATE(ClanGate.class),
        COMBATPOINTHEAL(CombatPointHeal.class),
        CONT(Toggle.class),
        CPDAM(CPDam.class),
        CPHOT(Continuous.class),
        CRAFT(Craft.class),
        DEATH_PENALTY(DeathPenalty.class),
        DEBUFF(Continuous.class),
        DELETE_HATE(DeleteHate.class),
        DELETE_HATE_OF_ME(DeleteHateOfMe.class),
        DESTROY_SUMMON(DestroySummon.class),
        DEFUSE_TRAP(DefuseTrap.class),
        DETECT_TRAP(DetectTrap.class),
        DISCORD(Continuous.class),
        DOT(Continuous.class),
        DRAIN(Drain.class),
        DRAIN_SOUL(DrainSoul.class),
        EFFECT(l2.gameserver.skills.skillclasses.Effect.class),
        EFFECTS_FROM_SKILLS(EffectsFromSkills.class),
        ENCHANT_ARMOR,
        ENCHANT_WEAPON,
        FEED_PET,
        FISHING(FishingSkill.class),
        HARDCODED(l2.gameserver.skills.skillclasses.Effect.class),
        HARVESTING(Harvesting.class),
        HEAL(Heal.class),
        HEAL_PERCENT(HealPercent.class),
        HOT(Continuous.class),
        LETHAL_SHOT(LethalShot.class),
        LUCK,
        MANADAM(ManaDam.class),
        MANAHEAL(ManaHeal.class),
        MANAHEAL_PERCENT(ManaHealPercent.class),
        MDAM(MDam.class),
        MDOT(Continuous.class),
        MPHOT(Continuous.class),
        MUTE(Disablers.class),
        NEGATE_EFFECTS(NegateEffects.class),
        NEGATE_STATS(NegateStats.class),
        ADD_PC_BANG(PcBangPointsAdd.class),
        NOTDONE,
        NOTUSED,
        PARALYZE(Disablers.class),
        PASSIVE,
        PDAM(PDam.class),
        PET_SUMMON(PetSummon.class),
        POISON(Continuous.class),
        PUMPING(ReelingPumping.class),
        RECALL(Recall.class),
        REELING(ReelingPumping.class),
        RESURRECT(Resurrect.class),
        RIDE(Ride.class),
        ROOT(Disablers.class),
        SHIFT_AGGRESSION(ShiftAggression.class),
        SSEED(SkillSeed.class),
        SLEEP(Disablers.class),
        SOULSHOT,
        SOWING(Sowing.class),
        SPHEAL(SPHeal.class),
        EXPHEAL(EXPHeal.class),
        SPIRITSHOT,
        SPOIL(Spoil.class),
        STEAL_BUFF(StealBuff.class),
        STUN(Disablers.class),
        SUMMON(l2.gameserver.skills.skillclasses.Summon.class),
        SUMMON_FLAG(SummonSiegeFlag.class),
        SUMMON_ITEM(SummonItem.class),
        SWEEP(Sweep.class),
        TAKECASTLE(TakeCastle.class),
        TAMECONTROL(TameControl.class),
        TELEPORT_NPC(TeleportNpc.class),
        TRANSFORMATION(Transformation.class),
        UNLOCK(Unlock.class),
        WATCHER_GAZE(Continuous.class);

        private final Class<? extends Skill> clazz;

        private SkillType() {
            this.clazz = Default.class;
        }

        private SkillType(Class<? extends Skill> clazz) {
            this.clazz = clazz;
        }

        public Skill makeSkill(StatsSet statsSet) {
            try {
                Constructor<? extends Skill> constructor = this.clazz.getConstructor(StatsSet.class);
                return constructor.newInstance(statsSet);
            }
            catch (Exception exception) {
                _log.error("", exception);
                throw new RuntimeException(exception);
            }
        }

        public final boolean isPvM() {
            switch (this) {
                case DISCORD: {
                    return true;
                }
            }
            return false;
        }

        public boolean isAI() {
            switch (this) {
                case SOWING: 
                case AGGRESSION: 
                case AIEFFECTS: 
                case DELETE_HATE: 
                case DELETE_HATE_OF_ME: {
                    return true;
                }
            }
            return false;
        }

        public final boolean isPvpSkill() {
            switch (this) {
                case PARALYZE: 
                case DEBUFF: 
                case NEGATE_EFFECTS: 
                case NEGATE_STATS: 
                case STEAL_BUFF: 
                case MUTE: 
                case ROOT: 
                case SLEEP: 
                case AGGRESSION: 
                case DELETE_HATE: 
                case DELETE_HATE_OF_ME: 
                case BLEED: 
                case DOT: 
                case MDOT: 
                case POISON: 
                case MANADAM: 
                case DESTROY_SUMMON: {
                    return true;
                }
            }
            return false;
        }

        public boolean isOffensive() {
            switch (this) {
                case PDAM: 
                case CPDAM: 
                case LETHAL_SHOT: 
                case SPOIL: 
                case SOWING: 
                case STUN: 
                case PARALYZE: 
                case DEBUFF: 
                case STEAL_BUFF: 
                case MUTE: 
                case ROOT: 
                case SLEEP: 
                case DISCORD: 
                case AGGRESSION: 
                case AIEFFECTS: 
                case DELETE_HATE: 
                case DELETE_HATE_OF_ME: 
                case BLEED: 
                case DOT: 
                case MDOT: 
                case POISON: 
                case MANADAM: 
                case DESTROY_SUMMON: 
                case DRAIN: 
                case DRAIN_SOUL: 
                case MDAM: 
                case SOULSHOT: 
                case SPIRITSHOT: 
                case SWEEP: 
                case HARVESTING: 
                case TELEPORT_NPC: {
                    return true;
                }
            }
            return false;
        }
    }

    public static enum SkillTargetType {
        TARGET_ALLY,
        TARGET_AREA,
        TARGET_AREA_AIM_CORPSE,
        TARGET_AREA_CLAN,
        TARGET_AURA,
        TARGET_PET_AURA,
        TARGET_CHEST,
        TARGET_FEEDABLE_BEAST,
        TARGET_CLAN,
        TARGET_CLAN_ONLY,
        TARGET_CORPSE,
        TARGET_CORPSE_PLAYER,
        TARGET_ENEMY_PET,
        TARGET_ENEMY_SUMMON,
        TARGET_ENEMY_SERVITOR,
        TARGET_FLAGPOLE,
        TARGET_COMMCHANNEL,
        TARGET_HOLY,
        TARGET_ITEM,
        TARGET_MULTIFACE,
        TARGET_MULTIFACE_AURA,
        TARGET_TUNNEL,
        TARGET_NONE,
        TARGET_ONE,
        TARGET_OTHER,
        TARGET_OWNER,
        TARGET_PARTY,
        TARGET_PET,
        TARGET_SELF,
        TARGET_SIEGE,
        TARGET_UNLOCKABLE,
        TARGET_ALLY_AND_PARTY;

    }

    public static enum SkillMagicType {
        PHYSIC,
        MAGIC,
        SPECIAL,
        MUSIC;

    }

    public static enum Ternary {
        TRUE,
        FALSE,
        DEFAULT;

    }

    public static enum SkillOpType {
        OP_ACTIVE,
        OP_PASSIVE,
        OP_TOGGLE;

    }

    public static enum SkillNextAction {
        ATTACK,
        CAST,
        DEFAULT,
        MOVE,
        NONE;

    }
}

