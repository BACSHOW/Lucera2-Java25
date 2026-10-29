/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import gnu.trove.TIntObjectIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.SummonAI;
import l2.gameserver.dao.EffectsDAO;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Party;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.recorder.SummonStatsChangeRecorder;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PetInventory;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.AutoAttackStart;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.network.l2.s2c.PartySpelled;
import l2.gameserver.network.l2.s2c.PetDelete;
import l2.gameserver.network.l2.s2c.PetInfo;
import l2.gameserver.network.l2.s2c.PetItemList;
import l2.gameserver.network.l2.s2c.PetStatusShow;
import l2.gameserver.network.l2.s2c.PetStatusUpdate;
import l2.gameserver.network.l2.s2c.RelationChanged;
import l2.gameserver.network.l2.s2c.StatusUpdate;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Events;
import l2.gameserver.stats.Stats;
import l2.gameserver.taskmanager.DecayTaskManager;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public abstract class Summon
extends Playable {
    private static final int LE = 2500;
    private final Player LF;
    private int _spawnAnimation = 2;
    protected long _exp = 0L;
    protected int _sp = 0;
    private int GN;
    private int LG;
    private boolean LH = true;
    private boolean LI = false;
    private boolean LJ = false;
    private Future<?> LK;
    private Future<?> JC;
    private ScheduledFuture<?> JE;
    private Future<?> LL;

    public Summon(int n, NpcTemplate npcTemplate, Player player) {
        super(n, npcTemplate);
        this.LF = player;
        if (npcTemplate.getSkills().size() > 0) {
            TIntObjectIterator<Skill> tIntObjectIterator = npcTemplate.getSkills().iterator();
            while (tIntObjectIterator.hasNext()) {
                tIntObjectIterator.advance();
                this.addSkill(tIntObjectIterator.value());
            }
        }
        this.setLoc(Location.findPointToStay(player, (int)player.getColRadius(), 100));
    }

    public HardReference<? extends Summon> getRef() {
        return (HardReference<? extends Summon>)(HardReference<?>)super.getRef();
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        this._spawnAnimation = 0;
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SummonAI getAI() {
        if (this._ai == null) {
            Summon summon = this;
            synchronized (summon) {
                if (this._ai == null) {
                    this._ai = new SummonAI(this);
                }
            }
        }
        return (SummonAI)this._ai;
    }

    @Override
    public NpcTemplate getTemplate() {
        return (NpcTemplate)this._template;
    }

    @Override
    public boolean isUndead() {
        return this.getTemplate().isUndead();
    }

    public abstract int getSummonType();

    public abstract int getEffectIdentifier();

    public boolean isMountable() {
        return false;
    }

    @Override
    public void onAction(Player player, boolean bl) {
        if (this.isFrozen()) {
            player.sendPacket((IStaticPacket)ActionFail.STATIC);
            return;
        }
        if (Events.onAction(player, this, bl)) {
            player.sendPacket((IStaticPacket)ActionFail.STATIC);
            return;
        }
        Player player2 = this.getPlayer();
        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                player.sendPacket(new MyTargetSelected(this.getObjectId(), 0), this.makeStatusUpdate(9, 10, 11, 12));
            } else {
                player.sendPacket((IStaticPacket)ActionFail.STATIC);
            }
        } else if (player == player2) {
            player.sendPacket((IStaticPacket)new PetInfo(this).update());
            if (!player.isActionsDisabled()) {
                player.sendPacket((IStaticPacket)new PetStatusShow(this));
            }
            player.sendPacket((IStaticPacket)ActionFail.STATIC);
        } else if (this.isAutoAttackable(player)) {
            player.getAI().Attack(this, false, bl);
        } else if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW) {
            if (!bl) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this);
            } else {
                player.sendActionFailed();
            }
        } else {
            player.sendActionFailed();
        }
    }

    public long getExpForThisLevel() {
        return Experience.getExpForLevel(this.getLevel());
    }

    public long getExpForNextLevel() {
        return Experience.getExpForLevel(this.getLevel() + 1);
    }

    @Override
    public int getNpcId() {
        return this.getTemplate().npcId;
    }

    public final long getExp() {
        return this._exp;
    }

    public final void setExp(long l) {
        this._exp = l;
    }

    public final int getSp() {
        return this._sp;
    }

    public void setSp(int n) {
        this._sp = n;
    }

    @Override
    public int getMaxLoad() {
        return this.GN;
    }

    public void setMaxLoad(int n) {
        this.GN = n;
    }

    @Override
    public int getBuffLimit() {
        Player player = this.getPlayer();
        return (int)this.calcStat(Stats.BUFF_LIMIT, player.getBuffLimit(), null, null);
    }

    public abstract int getCurrentFed();

    public abstract int getMaxFed();

    @Override
    protected void onDeath(Creature creature) {
        super.onDeath(creature);
        this.startDecay(8500L);
        Player player = this.getPlayer();
        if (creature == null || creature == player || creature == this || this.isInZoneBattle() || creature.isInZoneBattle()) {
            return;
        }
        if (creature instanceof Summon) {
            creature = creature.getPlayer();
        }
        if (creature == null) {
            return;
        }
        if (creature.isPlayer()) {
            Player player2 = (Player)creature;
            if (this.isInZone(Zone.ZoneType.SIEGE)) {
                return;
            }
            if (this.isInZone(Zone.ZoneType.fun)) {
                return;
            }
            DuelEvent duelEvent = this.getEvent(DuelEvent.class);
            if (player.getPvpFlag() > 0 || player.atMutualWarWith(player2)) {
                player2.setPvpKills(player2.getPvpKills() + 1);
            } else if ((duelEvent == null || duelEvent != player2.getEvent(DuelEvent.class)) && this.getKarma() <= 0) {
                this.doPurePk(player2);
            }
            player2.sendChanges();
        }
    }

    protected void startDecay(long l) {
        this.stopDecay();
        this.LK = DecayTaskManager.getInstance().addDecayTask(this, l);
    }

    protected void stopDecay() {
        if (this.LK != null) {
            this.LK.cancel(false);
            this.LK = null;
        }
    }

    @Override
    protected void onDecay() {
        this.deleteMe();
    }

    public void endDecayTask() {
        this.stopDecay();
        this.doDecay();
    }

    @Override
    public void broadcastStatusUpdate() {
        if (!this.needStatusUpdate()) {
            return;
        }
        Player player = this.getPlayer();
        this.sendStatusUpdate();
        StatusUpdate statusUpdate = this.makeStatusUpdate(10, 9);
        this.broadcastToStatusListeners(statusUpdate);
    }

    public void sendStatusUpdate() {
        Player player = this.getPlayer();
        player.sendPacket((IStaticPacket)new PetStatusUpdate(this));
    }

    @Override
    protected void onDelete() {
        Player player = this.getPlayer();
        player.sendPacket((IStaticPacket)new PetDelete(this.getObjectId(), this.getSummonType()));
        player.setPet(null);
        this.stopDecay();
        super.onDelete();
    }

    public void unSummon() {
        this.deleteMe();
    }

    public void saveEffects() {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        if (player.isOlyParticipant()) {
            this.getEffectList().stopAllEffects();
        }
        EffectsDAO.getInstance().insert(this);
    }

    public void setFollowMode(boolean bl) {
        Player player = this.getPlayer();
        this.LH = bl;
        if (this.LH) {
            if (this.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
            }
        } else if (this.getAI().getIntention() == CtrlIntention.AI_INTENTION_FOLLOW) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    public boolean isFollowMode() {
        return this.LH;
    }

    @Override
    public void updateEffectIcons() {
        if (Config.USER_INFO_INTERVAL == 0L) {
            if (this.JC != null) {
                this.JC.cancel(false);
                this.JC = null;
            }
            this.updateEffectIconsImpl();
            return;
        }
        if (this.JC != null) {
            return;
        }
        this.JC = ThreadPoolManager.getInstance().schedule(new UpdateEffectIcons(), Config.USER_INFO_INTERVAL);
    }

    public void updateEffectIconsImpl() {
        Player player = this.getPlayer();
        PartySpelled partySpelled = new PartySpelled(this, true);
        player.sendPacket((IStaticPacket)partySpelled);
    }

    public int getControlItemObjId() {
        return 0;
    }

    @Override
    public PetInventory getInventory() {
        return null;
    }

    @Override
    public void doPickupItem(GameObject gameObject) {
    }

    @Override
    public void doRevive() {
        super.doRevive();
        this.setRunning();
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        this.setFollowMode(true);
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public abstract void displayGiveDamageMessage(Creature var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6);

    @Override
    public abstract void displayReceiveDamageMessage(Creature var1, int var2);

    @Override
    public boolean unChargeShots(boolean bl) {
        Player player = this.getPlayer();
        if (bl) {
            if (this.LG != 0) {
                this.LG = 0;
                player.autoShot();
                return true;
            }
        } else if (this.LJ) {
            this.LJ = false;
            player.autoShot();
            return true;
        }
        return false;
    }

    @Override
    public boolean getChargedSoulShot() {
        return this.LJ;
    }

    @Override
    public int getChargedSpiritShot() {
        return this.LG;
    }

    public void chargeSoulShot() {
        this.LJ = true;
    }

    public void chargeSpiritShot(int n) {
        this.LG = n;
    }

    public int getSoulshotConsumeCount() {
        return this.getLevel() / 27 + 1;
    }

    public int getSpiritshotConsumeCount() {
        return this.getLevel() / 58 + 1;
    }

    public boolean isDepressed() {
        return this.LI;
    }

    public void setDepressed(boolean bl) {
        this.LI = bl;
    }

    public boolean isInRange() {
        Player player = this.getPlayer();
        return this.getDistance(player) < 2500.0;
    }

    public void teleportToOwner() {
        Player player = this.getPlayer();
        this.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
        if (player.isOlyParticipant()) {
            this.teleToLocation(player.getLoc(), player.getReflection());
        } else {
            this.teleToLocation(Location.findPointToStay(player, 50, 150), player.getReflection());
        }
        if (!this.isDead() && this.LH) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
        }
    }

    @Override
    public void broadcastCharInfo() {
        if (this.JE != null) {
            return;
        }
        this.JE = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
    }

    public void broadcastCharInfoImpl() {
        Player player = this.getPlayer();
        for (Player player2 : World.getAroundPlayers(this)) {
            if (player2 == player) {
                player2.sendPacket((IStaticPacket)new PetInfo(this).update());
                continue;
            }
            player2.sendPacket((IStaticPacket)new NpcInfo(this, (Creature)player2).update());
        }
    }

    private void cA() {
        Player player = this.getPlayer();
        player.sendPacket((IStaticPacket)new PetInfo(this).update());
    }

    public void sendPetInfo() {
        if (Config.USER_INFO_INTERVAL == 0L) {
            if (this.LL != null) {
                this.LL.cancel(false);
                this.LL = null;
            }
            this.cA();
            return;
        }
        if (this.LL != null) {
            return;
        }
        this.LL = ThreadPoolManager.getInstance().schedule(new PetInfoTask(), Config.USER_INFO_INTERVAL);
    }

    public int getSpawnAnimation() {
        return this._spawnAnimation;
    }

    @Override
    public void startPvPFlag(Creature creature) {
        Player player = this.getPlayer();
        player.startPvPFlag(creature);
    }

    @Override
    public int getPvpFlag() {
        Player player = this.getPlayer();
        return player.getPvpFlag();
    }

    @Override
    public int getKarma() {
        Player player = this.getPlayer();
        return player.getKarma();
    }

    @Override
    public TeamType getTeam() {
        Player player = this.getPlayer();
        return player.getTeam();
    }

    @Override
    public Player getPlayer() {
        return this.LF;
    }

    public abstract double getExpPenalty();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SummonStatsChangeRecorder getStatsRecorder() {
        if (this._statsRecorder == null) {
            Summon summon = this;
            synchronized (summon) {
                if (this._statsRecorder == null) {
                    this._statsRecorder = new SummonStatsChangeRecorder(this);
                }
            }
        }
        return (SummonStatsChangeRecorder)this._statsRecorder;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player player, Creature creature) {
        ArrayList<L2GameServerPacket> arrayList = new ArrayList<L2GameServerPacket>();
        Player player2 = this.getPlayer();
        if (player2 == player) {
            arrayList.add(new PetInfo(this));
            arrayList.add(new PartySpelled(this, true));
            if (this.isPet()) {
                arrayList.add(new PetItemList((PetInstance)this));
            }
        } else {
            Party party = player.getParty();
            if (this.getReflection() == ReflectionManager.GIRAN_HARBOR && (player2 == null || party == null || party != player2.getParty())) {
                return arrayList;
            }
            arrayList.add(new NpcInfo(this, (Creature)player));
            arrayList.add(RelationChanged.create(player, this, player));
        }
        if (this.isInCombat()) {
            arrayList.add(new AutoAttackStart(this.getObjectId()));
        }
        if (this.isMoving() || this.isFollowing()) {
            arrayList.add(this.movePacket());
        }
        return arrayList;
    }

    @Override
    public void startAttackStanceTask() {
        this.startAttackStanceTask0();
        Player player = this.getPlayer();
        if (player != null) {
            player.startAttackStanceTask0();
        }
    }

    @Override
    public <E extends GlobalEvent> E getEvent(Class<E> clazz) {
        Player player = this.getPlayer();
        if (player != null) {
            return player.getEvent(clazz);
        }
        return super.getEvent(clazz);
    }

    @Override
    public Set<GlobalEvent> getEvents() {
        Player player = this.getPlayer();
        if (player != null) {
            return player.getEvents();
        }
        return super.getEvents();
    }

    @Override
    public void sendReuseMessage(Skill skill) {
        Player player = this.getPlayer();
        if (player != null && this.isSkillDisabled(skill)) {
            player.sendPacket((IStaticPacket)new SystemMessage(48).addSkillName(skill.getDisplayId(), skill.getDisplayLevel()));
        }
    }

    private class PetInfoTask
    extends RunnableImpl {
        private PetInfoTask() {
        }

        @Override
        public void runImpl() throws Exception {
            Summon.this.cA();
            Summon.this.LL = null;
        }
    }

    public class BroadcastCharInfoTask
    extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            Summon.this.broadcastCharInfoImpl();
            Summon.this.JE = null;
        }
    }

    private class UpdateEffectIcons
    extends RunnableImpl {
        private UpdateEffectIcons() {
        }

        @Override
        public void runImpl() throws Exception {
            Summon.this.updateEffectIconsImpl();
            Summon.this.JC = null;
        }
    }
}

