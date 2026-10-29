/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import l2.commons.lang.reference.HardReference;
import l2.commons.util.Rnd;
import l2.commons.util.concurrent.atomic.AtomicState;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExServerPrimitive;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.EffectType;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.item.EtcItemTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Location;

public abstract class Playable
extends Creature {
    private AtomicState GS = new AtomicState();
    private boolean GT;
    protected final ReadWriteLock questLock = new ReentrantReadWriteLock();
    protected final Lock questRead = this.questLock.readLock();
    protected final Lock questWrite = this.questLock.writeLock();
    private long GU = 0L;

    public Playable(int n, CharTemplate charTemplate) {
        super(n, charTemplate);
    }

    public HardReference<? extends Playable> getRef() {
        return (HardReference<? extends Playable>)(HardReference<?>)super.getRef();
    }

    public abstract Inventory getInventory();

    public abstract long getWearedMask();

    @Override
    public boolean checkPvP(Creature creature, Skill skill) {
        DuelEvent duelEvent;
        Player player = this.getPlayer();
        if (this.isDead() || creature == null || player == null || creature == this || creature == player || creature == player.getPet() || player.getKarma() > 0) {
            return false;
        }
        if (skill != null) {
            if (skill.altUse()) {
                return false;
            }
            if (skill.getTargetType() == Skill.SkillTargetType.TARGET_FEEDABLE_BEAST) {
                return false;
            }
            if (skill.getTargetType() == Skill.SkillTargetType.TARGET_UNLOCKABLE) {
                return false;
            }
            if (skill.getTargetType() == Skill.SkillTargetType.TARGET_CHEST) {
                return false;
            }
        }
        if ((duelEvent = this.getEvent(DuelEvent.class)) != null && duelEvent == creature.getEvent(DuelEvent.class)) {
            return false;
        }
        if (this.isInZonePeace() && creature.isInZonePeace()) {
            return false;
        }
        if (this.isInZoneBattle() && creature.isInZoneBattle()) {
            return false;
        }
        if (this.isInZone(Zone.ZoneType.SIEGE) && creature.isInZone(Zone.ZoneType.SIEGE)) {
            return false;
        }
        if (this.isInZone(Zone.ZoneType.fun) && creature.isInZone(Zone.ZoneType.fun)) {
            return false;
        }
        if (skill == null || skill.isOffensive()) {
            if (creature.getKarma() > 0) {
                return false;
            }
            if (creature.isPlayable()) {
                return true;
            }
        } else if (creature.getPvpFlag() > 0 || creature.getKarma() > 0 || creature.isMonster()) {
            return true;
        }
        return false;
    }

    public boolean checkTarget(Creature creature) {
        Player player = this.getPlayer();
        if (player == null) {
            return false;
        }
        if (creature == null || creature.isDead()) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        if (!this.isInRange(creature, 2000L)) {
            player.sendPacket((IStaticPacket)Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }
        if (creature.isDoor() && !creature.isAttackable(this)) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        if (creature.paralizeOnAttack(this)) {
            if (Config.PARALIZE_ON_RAID_DIFF) {
                this.paralizeMe(creature);
            }
            return false;
        }
        if (creature.isInvisible() || this.getReflection() != creature.getReflection() || !GeoEngine.canSeeTarget(this, creature, false)) {
            player.sendPacket((IStaticPacket)SystemMsg.CANNOT_SEE_TARGET);
            return false;
        }
        if (!Config.CAN_ATTACK_FROM_ANOTHER_ZONE_TO_EPIC && player.isInZone(Zone.ZoneType.epic) != creature.isInZone(Zone.ZoneType.epic)) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        if (!Config.ALT_CAN_ATTACK_NPC_AT_PEACE_ZONE && creature.isInZone(Zone.ZoneType.peace_zone) && !creature.isMonster() && creature.isNpc()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
            return false;
        }
        if (creature.isPlayable()) {
            if (this.isInZoneBattle() != creature.isInZoneBattle()) {
                player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                return false;
            }
            if (this.isInZonePeace() || creature.isInZonePeace()) {
                player.sendPacket((IStaticPacket)Msg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
                return false;
            }
            if (player.isOlyParticipant() && !player.isOlyCompetitionStarted()) {
                return false;
            }
            if (creature.isPlayer()) {
                Player player2 = creature.getPlayer();
                if (player.isOlyParticipant()) {
                    if (player2.isOlyParticipant() && player.getOlyParticipant().getCompetition() != player2.getOlyParticipant().getCompetition()) {
                        return false;
                    }
                    if (player.isOlyCompetitionStarted() && player.getOlyParticipant() == player2.getOlyParticipant() && (!this.isSummon() || player2.getPet() != this)) {
                        return false;
                    }
                    if (player.isLooseOlyCompetition()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void setXYZ(int n, int n2, int n3, boolean bl) {
        Player player;
        int n4;
        super.setXYZ(n, n2, n3, bl);
        if (bl && this.isPlayable() && (n4 = (player = this.getPlayer()).getVarInt("debugMove", 0)) > 0) {
            Location location = this.getLoc();
            ExServerPrimitive exServerPrimitive = new ExServerPrimitive(location.toXYZString(), location.getX(), location.getY(), (int)((double)location.getZ() + this.getColHeight() + 16.0));
            if (this.moveAction != null) {
                Color[] colorArray = new Color[]{Color.CYAN, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.RED, Color.YELLOW, Color.RED};
                Color color = colorArray[System.identityHashCode(this.moveAction) % colorArray.length];
                exServerPrimitive.addPoint(String.format("%s|%08x", location.toXYZString(), this.moveAction.hashCode()), color, true, location.getX(), location.getY(), location.getZ());
            } else {
                exServerPrimitive.addPoint(location.toXYZString(), 0xFFFFFF, true, location.getX(), location.getY(), location.getZ());
            }
            player.sendPacket((IStaticPacket)exServerPrimitive);
            if (n4 > 1) {
                player.broadcastPacketToOthers(exServerPrimitive);
            }
        }
    }

    @Override
    public void doAttack(Creature creature) {
        WeaponTemplate weaponTemplate;
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        if (this.isAMuted() || this.isAttackingNow()) {
            player.sendActionFailed();
            return;
        }
        if (player.isInObserverMode()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.model.L2Playable.OutOfControl.ObserverNoAttack", player, new Object[0]));
            return;
        }
        if (!this.checkTarget(creature)) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
            player.sendActionFailed();
            return;
        }
        DuelEvent duelEvent = this.getEvent(DuelEvent.class);
        if (duelEvent != null && creature.getEvent(DuelEvent.class) != duelEvent) {
            duelEvent.abortDuel(this.getPlayer());
        }
        if ((weaponTemplate = this.getActiveWeaponItem()) != null && weaponTemplate.getItemType() == WeaponTemplate.WeaponType.BOW) {
            double d = weaponTemplate.getMpConsume();
            if (d > 0.0) {
                double d2 = this.calcStat(Stats.MP_USE_BOW_CHANCE, 0.0, creature, null);
                if (d2 > 0.0 && Rnd.chance(d2)) {
                    d = this.calcStat(Stats.MP_USE_BOW, d, creature, null);
                }
                if (this._currentMp < d) {
                    this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
                    player.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
                    player.sendActionFailed();
                    return;
                }
                this.reduceCurrentMp(d, null);
            }
            if (!player.checkAndEquipArrows()) {
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
                player.sendPacket((IStaticPacket)Msg.YOU_HAVE_RUN_OUT_OF_ARROWS);
                player.sendActionFailed();
                return;
            }
        }
        super.doAttack(creature);
    }

    public void doPurePk(Player player) {
        int n = Config.KARMA_STATIC_INCREASES;
        if (n != -1) {
            player.increaseKarma(n);
        } else if (player.getPkKills() > 0) {
            player.increaseKarma((long)Config.KARMA_MIN_INCREASE + (long)Config.KARMA_MIN_INCREASE * (long)player.getPkKills() / 2L);
        } else {
            player.increaseKarma(Config.KARMA_MIN_INCREASE);
        }
    }

    @Override
    public void doCast(Skill skill, Creature creature, boolean bl) {
        if (skill == null) {
            return;
        }
        DuelEvent duelEvent = this.getEvent(DuelEvent.class);
        if (duelEvent != null && creature.getEvent(DuelEvent.class) != duelEvent) {
            duelEvent.abortDuel(this.getPlayer());
        }
        if (this.isInPeaceZone() && (skill.getTargetType() == Skill.SkillTargetType.TARGET_AREA || skill.getTargetType() == Skill.SkillTargetType.TARGET_AURA || skill.getTargetType() == Skill.SkillTargetType.TARGET_MULTIFACE || skill.getTargetType() == Skill.SkillTargetType.TARGET_MULTIFACE_AURA)) {
            this.getPlayer().sendPacket((IStaticPacket)Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return;
        }
        if (skill.getSkillType() == Skill.SkillType.DEBUFF && skill.isMagic() && creature.isNpc() && creature.isInvul() && !creature.isMonster()) {
            this.getPlayer().sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        super.doCast(skill, creature, bl);
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
        if (creature == null || this.isDead() || creature.isDead() && !bl6) {
            return;
        }
        if (this.isDamageBlocked() && bl5) {
            return;
        }
        if (this.isDamageBlocked() && creature != this) {
            if (bl7) {
                creature.sendPacket((IStaticPacket)Msg.THE_ATTACK_HAS_BEEN_BLOCKED);
            }
            return;
        }
        if (creature != this && creature.isPlayable()) {
            Player player = this.getPlayer();
            Player player2 = creature.getPlayer();
            if (player2 != player && player.isOlyParticipant() && !player.isOlyCompetitionStarted()) {
                if (bl7) {
                    player2.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                }
                return;
            }
            if (this.isInZoneBattle() != creature.isInZoneBattle()) {
                if (bl7) {
                    creature.getPlayer().sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                }
                return;
            }
            DuelEvent duelEvent = this.getEvent(DuelEvent.class);
            if (duelEvent != null && creature.getEvent(DuelEvent.class) != duelEvent) {
                duelEvent.abortDuel(player);
            }
        }
        super.reduceCurrentHp(d, creature, skill, bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }

    @Override
    public int getPAtkSpd() {
        return Math.max((int)this.calcStat(Stats.POWER_ATTACK_SPEED, this.calcStat(Stats.ATK_BASE, this._template.basePAtkSpd, null, null), null, null), 1);
    }

    @Override
    public int getPAtk(Creature creature) {
        double d = this.getActiveWeaponInstance() == null ? (double)this._template.basePAtk : 0.0;
        return (int)this.calcStat(Stats.POWER_ATTACK, d, creature, null);
    }

    @Override
    public int getMAtk(Creature creature, Skill skill) {
        if (skill != null && skill.getMatak() > 0) {
            return skill.getMatak();
        }
        double d = this.getActiveWeaponInstance() == null ? (double)this._template.baseMAtk : 0.0;
        return (int)this.calcStat(Stats.MAGIC_ATTACK, d, creature, skill);
    }

    @Override
    public boolean isAttackable(Creature creature) {
        return this.isCtrlAttackable(creature, true, false);
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return this.isCtrlAttackable(creature, false, false);
    }

    public boolean isCtrlAttackable(Creature creature, boolean bl, boolean bl2) {
        Player player = this.getPlayer();
        if (creature == null || player == null || creature == this || creature == player && !bl || this.isAlikeDead() || creature.isAlikeDead()) {
            return false;
        }
        if (this.isInvisible() || this.getReflection() != creature.getReflection()) {
            return false;
        }
        if (this.isInBoat()) {
            return false;
        }
        if (creature == this.getPet()) {
            return false;
        }
        for (GlobalEvent globalEvent : this.getEvents()) {
            if (globalEvent.checkForAttack(creature, this, null, bl) == null) continue;
            return false;
        }
        for (GlobalEvent globalEvent : player.getEvents()) {
            if (!globalEvent.canAttack(this, creature, null, bl)) continue;
            return true;
        }
        Player player2 = creature.getPlayer();
        if (player2 != null && player2 != player) {
            if (player2.isInBoat()) {
                return false;
            }
            if (player2.isCursedWeaponEquipped() && player.getLevel() < 21 || player.isCursedWeaponEquipped() && player2.getLevel() < 21) {
                return false;
            }
            if (!Config.CAN_ATTACK_FROM_ANOTHER_ZONE_TO_EPIC && player.isInZone(Zone.ZoneType.epic) != player2.isInZone(Zone.ZoneType.epic)) {
                return false;
            }
            if (player.isOlyParticipant()) {
                if (player2.isOlyParticipant() && player.getOlyParticipant().getCompetition() != player2.getOlyParticipant().getCompetition()) {
                    return false;
                }
                if (player.isOlyCompetitionStarted() && player.getOlyParticipant() == player2.getOlyParticipant()) {
                    return false;
                }
                if (player.isLooseOlyCompetition()) {
                    return false;
                }
                if (player.getClan() != null && player.getClan() == player2.getClan()) {
                    return true;
                }
            }
            if (player.getTeam() != TeamType.NONE && player.getTeam() == player2.getTeam()) {
                return false;
            }
            if (this.isInZonePeace()) {
                return false;
            }
            if (!bl && player.getParty() != null && player.getParty() == player2.getParty()) {
                return false;
            }
            if (this.isInZoneBattle()) {
                return true;
            }
            if (!bl) {
                if (player.getClan() != null && player.getClan() == player2.getClan()) {
                    return false;
                }
                if (Config.ALLY_ALLOW_BUFF_DEBUFFS && player.getAlliance() != null && player.getAlliance() == player2.getAlliance()) {
                    return false;
                }
            }
            if (this.isInZone(Zone.ZoneType.SIEGE)) {
                return true;
            }
            if (this.isInZone(Zone.ZoneType.fun)) {
                return true;
            }
            if (player2.atMutualWarWith(player)) {
                return true;
            }
            if (player.getKarma() > 0 || player.getPvpFlag() != 0) {
                return true;
            }
            if (bl2 && player.getPvpFlag() > 0) {
                return true;
            }
            if (player2.isCursedWeaponEquipped()) {
                return true;
            }
            return bl;
        }
        return true;
    }

    @Override
    public int getKarma() {
        Player player = this.getPlayer();
        return player == null ? 0 : player.getKarma();
    }

    @Override
    public void callSkill(Skill skill, List<Creature> list, boolean bl) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        if (bl && !skill.altUse() && !skill.getSkillType().equals((Object)Skill.SkillType.BEAST_FEED)) {
            for (Creature creature : list) {
                int n;
                if (creature.isNpc()) {
                    if (skill.isOffensive()) {
                        if (creature.paralizeOnAttack(player)) {
                            if (Config.PARALIZE_ON_RAID_DIFF) {
                                this.paralizeMe(creature);
                            }
                            return;
                        }
                        if (!skill.isAI()) {
                            n = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : 1;
                            creature.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, this, n);
                        }
                    }
                    creature.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this);
                } else if (creature.isPlayable() && creature != this.getPet() && (!this.isSummon() && !this.isPet() || creature != player)) {
                    n = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : Math.max(1, (int)skill.getPower());
                    List<NpcInstance> list2 = World.getAroundNpc(creature);
                    for (NpcInstance npcInstance : list2) {
                        if (npcInstance.isDead() || !npcInstance.isInRangeZ(this, 2000L)) continue;
                        npcInstance.getAI().notifyEvent(CtrlEvent.EVT_SEE_SPELL, skill, this);
                        AggroList.AggroInfo aggroInfo = npcInstance.getAggroList().get(creature);
                        if (aggroInfo == null) continue;
                        if (!skill.isHandler() && npcInstance.paralizeOnAttack(player)) {
                            if (Config.PARALIZE_ON_RAID_DIFF) {
                                this.paralizeMe(npcInstance);
                            }
                            return;
                        }
                        if (aggroInfo.hate < 100 || !GeoEngine.canSeeTarget(npcInstance, creature, false)) continue;
                        npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this, aggroInfo.damage == 0 ? n / 2 : n);
                    }
                }
                if (!this.checkPvP(creature, skill)) continue;
                this.startPvPFlag(creature);
            }
        }
        super.callSkill(skill, list, bl);
    }

    public void broadcastPickUpMsg(ItemInstance itemInstance) {
        Player player = this.getPlayer();
        if (itemInstance == null || player == null || player.isInvisible()) {
            return;
        }
        if (itemInstance.isEquipable() && !(itemInstance.getTemplate() instanceof EtcItemTemplate)) {
            SystemMessage systemMessage = null;
            String string = player.getName();
            if (itemInstance.getEnchantLevel() > 0) {
                int n = this.isPlayer() ? 1534 : 1536;
                systemMessage = new SystemMessage(n).addString(string).addNumber(itemInstance.getEnchantLevel()).addItemName(itemInstance.getItemId());
            } else {
                int n = this.isPlayer() ? 1533 : 1536;
                systemMessage = new SystemMessage(n).addString(string).addItemName(itemInstance.getItemId());
            }
            player.broadcastPacket(systemMessage);
        }
    }

    public void paralizeMe(Creature creature) {
        Skill skill = SkillTable.getInstance().getInfo(4515, 1);
        skill.getEffects(creature, this, false, false);
    }

    public final void setPendingRevive(boolean bl) {
        this.GT = bl;
    }

    public boolean isPendingRevive() {
        return this.GT;
    }

    public void doRevive() {
        if (!this.isTeleporting()) {
            this.setPendingRevive(false);
            this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
            if (this.isSalvation()) {
                for (Effect effect : this.getEffectList().getAllEffects()) {
                    if (effect.getEffectType() != EffectType.Salvation) continue;
                    effect.exit();
                    break;
                }
                this.setCurrentHp(this.getMaxHp(), true);
                this.setCurrentMp(this.getMaxMp());
                this.setCurrentCp(this.getMaxCp());
            } else {
                if (Config.RESPAWN_RESTORE_HP >= 0.0) {
                    this.setCurrentHp((double)this.getMaxHp() * Config.RESPAWN_RESTORE_HP, true);
                }
                if (Config.RESPAWN_RESTORE_MP >= 0.0) {
                    this.setCurrentMp((double)this.getMaxMp() * Config.RESPAWN_RESTORE_MP);
                }
                if (this.isPlayer() && Config.RESPAWN_RESTORE_CP >= 0.0) {
                    this.setCurrentCp((double)this.getMaxCp() * Config.RESPAWN_RESTORE_CP, true);
                }
            }
            this.broadcastPacket(new Revive(this));
        } else {
            this.setPendingRevive(true);
        }
    }

    public abstract void doPickupItem(GameObject var1);

    public void sitDown(StaticObjectInstance staticObjectInstance) {
    }

    public void standUp() {
    }

    public long getNonAggroTime() {
        return this.GU;
    }

    public void setNonAggroTime(long l) {
        this.GU = l;
    }

    public boolean startSilentMoving() {
        return this.GS.getAndSet(true);
    }

    public boolean stopSilentMoving() {
        return this.GS.setAndGet(false);
    }

    public boolean isSilentMoving() {
        return this.GS.get();
    }

    public boolean isInCombatZone() {
        return this.isInZoneBattle();
    }

    public boolean isInPeaceZone() {
        return this.isInZonePeace();
    }

    @Override
    public boolean isInZoneBattle() {
        return super.isInZoneBattle();
    }

    public boolean isOnSiegeField() {
        return this.isInZone(Zone.ZoneType.SIEGE);
    }

    public boolean isInSSQZone() {
        return this.isInZone(Zone.ZoneType.ssq_zone);
    }

    public boolean isInDangerArea() {
        return this.isInZone(Zone.ZoneType.damage) || this.isInZone(Zone.ZoneType.swamp) || this.isInZone(Zone.ZoneType.poison) || this.isInZone(Zone.ZoneType.instant_skill);
    }

    public int getMaxLoad() {
        return 0;
    }

    public int getInventoryLimit() {
        return 0;
    }

    @Override
    public boolean isPlayable() {
        return true;
    }
}

