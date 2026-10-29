/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.geometry.Shape;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.DoorAI;
import l2.gameserver.geodata.GeoCollision;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.listener.actor.door.OnOpenCloseListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.DoorInfo;
import l2.gameserver.network.l2.s2c.DoorStatusUpdate;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.ValidateLocation;
import l2.gameserver.scripts.Events;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;

public final class DoorInstance
extends Creature
implements GeoCollision {
    private boolean Qw = true;
    private boolean TC = true;
    private Lock TD = new ReentrantLock();
    private int TE;
    private byte[][] TF;
    protected ScheduledFuture<?> _autoActionTask;

    public DoorInstance(int n, DoorTemplate doorTemplate) {
        super(n, doorTemplate);
    }

    public boolean isUnlockable() {
        return this.getTemplate().isUnlockable();
    }

    @Override
    public String getName() {
        return this.getTemplate().getName();
    }

    @Override
    public int getLevel() {
        return 1;
    }

    public int getDoorId() {
        return this.getTemplate().getNpcId();
    }

    public boolean isOpen() {
        return this.Qw;
    }

    protected boolean setOpen(boolean bl) {
        if (this.Qw == bl) {
            return false;
        }
        this.Qw = bl;
        return true;
    }

    public void scheduleAutoAction(boolean bl, long l) {
        if (this._autoActionTask != null) {
            this._autoActionTask.cancel(false);
            this._autoActionTask = null;
        }
        this._autoActionTask = ThreadPoolManager.getInstance().schedule(new AutoOpenClose(bl), l);
    }

    public int getDamage() {
        int n = 6 - (int)Math.ceil(this.getCurrentHpRatio() * 6.0);
        return Math.max(0, Math.min(6, n));
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return this.isAttackable(creature);
    }

    @Override
    public boolean isAttackable(Creature creature) {
        if (creature == null || this.isOpen()) {
            return false;
        }
        SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
        switch (this.getDoorType()) {
            case WALL: {
                if (creature.isSummon() && siegeEvent != null && siegeEvent.containsSiegeSummon((SummonInstance)creature)) break;
                return false;
            }
            case DOOR: {
                Player player = creature.getPlayer();
                if (player == null) {
                    return false;
                }
                if (siegeEvent == null || !siegeEvent.isInProgress() || siegeEvent.getSiegeClan("attackers", player.getClan()) != null) break;
                return false;
            }
        }
        return !this.isInvul();
    }

    @Override
    public void sendChanges() {
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

    public Location getCenterPoint() {
        Shape shape = this.getShape();
        return new Location(shape.getXmin() + (shape.getXmax() - shape.getXmin() >> 1), shape.getYmin() + (shape.getYmax() - shape.getYmin() >> 1), shape.getZmin() + (shape.getZmax() - shape.getZmin() >> 1));
    }

    @Override
    public void onAction(Player player, boolean bl) {
        if (Events.onAction(player, this, bl)) {
            return;
        }
        if (this != player.getTarget()) {
            player.setTarget(this);
            player.sendPacket((IStaticPacket)new MyTargetSelected(this.getObjectId(), player.getLevel()));
            if (this.isAutoAttackable(player)) {
                player.sendPacket((IStaticPacket)new DoorInfo(this, player));
            }
            player.sendPacket((IStaticPacket)new ValidateLocation(this));
        } else {
            player.sendPacket((IStaticPacket)new MyTargetSelected(this.getObjectId(), 0));
            if (this.isAutoAttackable(player)) {
                player.getAI().Attack(this, false, bl);
                return;
            }
            if (!this.isInActingRange(player)) {
                if (!player.getAI().isIntendingInteract(this)) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
                }
                return;
            }
            this.getAI().onEvtTwiceClick(player);
        }
    }

    @Override
    public int getActingRange() {
        return 150;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DoorAI getAI() {
        if (this._ai == null) {
            DoorInstance doorInstance = this;
            synchronized (doorInstance) {
                if (this._ai == null) {
                    this._ai = this.getTemplate().getNewAI(this);
                }
            }
        }
        return (DoorAI)this._ai;
    }

    @Override
    public void broadcastStatusUpdate() {
        for (Player player : World.getAroundPlayers(this)) {
            if (player == null) continue;
            player.sendPacket((IStaticPacket)new DoorStatusUpdate(this, player));
        }
    }

    public boolean openMe() {
        return this.openMe(null, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean openMe(Player player, boolean bl) {
        this.TD.lock();
        try {
            if (!this.setOpen(true)) {
                boolean bl2 = false;
                return bl2;
            }
            this.setGeoOpen(true);
        }
        finally {
            this.TD.unlock();
        }
        this.broadcastStatusUpdate();
        if (bl && this.getTemplate().getCloseTime() > 0) {
            this.scheduleAutoAction(false, (long)this.getTemplate().getCloseTime() * 1000L);
        }
        this.getAI().onEvtOpen(player);
        this.getListeners().forEachListener(OnOpenCloseListener.class, onOpenCloseListener -> onOpenCloseListener.onOpen(this));
        return true;
    }

    public boolean closeMe() {
        return this.closeMe(null, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean closeMe(Player player, boolean bl) {
        if (this.isDead()) {
            return false;
        }
        this.TD.lock();
        try {
            if (!this.setOpen(false)) {
                boolean bl2 = false;
                return bl2;
            }
            this.setGeoOpen(false);
        }
        finally {
            this.TD.unlock();
        }
        this.broadcastStatusUpdate();
        if (bl && this.getTemplate().getOpenTime() > 0) {
            long l = (long)this.getTemplate().getOpenTime() * 1000L;
            if (this.getTemplate().getRandomTime() > 0) {
                l += (long)Rnd.get(0, this.getTemplate().getRandomTime()) * 1000L;
            }
            this.scheduleAutoAction(true, l);
        }
        this.getAI().onEvtClose(player);
        this.getListeners().forEachListener(OnOpenCloseListener.class, onOpenCloseListener -> onOpenCloseListener.onClose(this));
        return true;
    }

    @Override
    public String toString() {
        return "[Door " + this.getDoorId() + "]";
    }

    @Override
    protected void onDeath(Creature creature) {
        this.TD.lock();
        try {
            this.setGeoOpen(true);
        }
        finally {
            this.TD.unlock();
        }
        SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
        if (siegeEvent != null && siegeEvent.isInProgress()) {
            Log.add(this.toString(), (Object)((Object)this.getDoorType()) + " destroyed by " + creature + ", " + siegeEvent);
        }
        super.onDeath(creature);
    }

    @Override
    protected void onRevive() {
        super.onRevive();
        this.TD.lock();
        try {
            if (!this.isOpen()) {
                this.setGeoOpen(false);
            }
        }
        finally {
            this.TD.unlock();
        }
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp(), true);
        this.closeMe(null, true);
    }

    @Override
    protected void onDespawn() {
        if (this._autoActionTask != null) {
            this._autoActionTask.cancel(false);
            this._autoActionTask = null;
        }
        super.onDespawn();
    }

    public boolean isHPVisible() {
        return this.getTemplate().isHPVisible();
    }

    @Override
    public int getMaxHp() {
        return super.getMaxHp() + this.TE;
    }

    public void setUpgradeHp(int n) {
        this.TE = n;
    }

    public int getUpgradeHp() {
        return this.TE;
    }

    @Override
    public int getPDef(Creature creature) {
        switch (SevenSigns.getInstance().getSealOwner(3)) {
            case 2: {
                return (int)((double)super.getPDef(creature) * 1.2);
            }
            case 1: {
                return (int)((double)super.getPDef(creature) * 0.3);
            }
        }
        return super.getPDef(creature);
    }

    @Override
    public int getMDef(Creature creature, Skill skill) {
        switch (SevenSigns.getInstance().getSealOwner(3)) {
            case 2: {
                return (int)((double)super.getMDef(creature, skill) * 1.2);
            }
            case 1: {
                return (int)((double)super.getMDef(creature, skill) * 0.3);
            }
        }
        return super.getMDef(creature, skill);
    }

    @Override
    public boolean isInvul() {
        if (!this.getTemplate().isHPVisible()) {
            return true;
        }
        SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
        if (siegeEvent != null && siegeEvent.isInProgress()) {
            return false;
        }
        return super.isInvul();
    }

    protected boolean setGeoOpen(boolean bl) {
        if (this.TC == bl) {
            return false;
        }
        this.TC = bl;
        if (Config.ALLOW_GEODATA) {
            if (bl) {
                GeoEngine.removeGeoCollision(this, this.getGeoIndex());
            } else {
                GeoEngine.applyGeoCollision(this, this.getGeoIndex());
            }
        }
        return true;
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean isActionsDisabled() {
        return true;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean isConcrete() {
        return true;
    }

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player player, Creature creature) {
        return Arrays.asList(new DoorInfo(this, player), new DoorStatusUpdate(this, player));
    }

    @Override
    public boolean isDoor() {
        return true;
    }

    @Override
    public Shape getShape() {
        return this.getTemplate().getPolygon();
    }

    @Override
    public byte[][] getGeoAround() {
        return this.TF;
    }

    @Override
    public void setGeoAround(byte[][] byArray) {
        this.TF = byArray;
    }

    @Override
    public DoorTemplate getTemplate() {
        return (DoorTemplate)super.getTemplate();
    }

    public DoorTemplate.DoorType getDoorType() {
        return this.getTemplate().getDoorType();
    }

    public int getKey() {
        return this.getTemplate().getKey();
    }

    private class AutoOpenClose
    extends RunnableImpl {
        private boolean Qw;

        public AutoOpenClose(boolean bl) {
            this.Qw = bl;
        }

        @Override
        public void runImpl() throws Exception {
            if (this.Qw) {
                DoorInstance.this.openMe(null, true);
            } else {
                DoorInstance.this.closeMe(null, true);
            }
        }
    }
}

