/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances.residences;

import java.util.Set;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.base.SpecialEffectState;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.templates.npc.NpcTemplate;

public abstract class SiegeToggleNpcInstance
extends NpcInstance {
    private NpcInstance Wv;
    private int _maxHp;

    public SiegeToggleNpcInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setHasChatWindow(false);
        this.setUndying(SpecialEffectState.FALSE);
    }

    public void setMaxHp(int n) {
        this._maxHp = n;
    }

    public void setZoneList(Set<String> set) {
    }

    public void register(Spawner spawner) {
    }

    public void initFake(int n) {
        this.Wv = NpcHolder.getInstance().getTemplate(n).getNewInstance();
        this.Wv.setCurrentHpMp(1.0, this.Wv.getMaxMp());
        this.Wv.setHasChatWindow(false);
    }

    public abstract void onDeathImpl(Creature var1);

    @Override
    protected void onReduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3) {
        this.setCurrentHp(Math.max(this.getCurrentHp() - d, 0.0), false);
        if (this.getCurrentHp() < 0.5) {
            this.doDie(creature);
            this.onDeathImpl(creature);
            this.decayMe();
            this.Wv.spawnMe(this.getLoc());
        }
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        if (creature == null) {
            return false;
        }
        Player player = creature.getPlayer();
        if (player == null) {
            return false;
        }
        SiegeEvent siegeEvent = this.getEvent(SiegeEvent.class);
        return siegeEvent != null && siegeEvent.isInProgress();
    }

    @Override
    public boolean isAttackable(Creature creature) {
        return this.isAutoAttackable(creature);
    }

    @Override
    public boolean isInvul() {
        return false;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
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

    public void decayFake() {
        this.Wv.decayMe();
    }

    @Override
    public int getMaxHp() {
        return this._maxHp;
    }

    @Override
    protected void onDecay() {
        this.decayMe();
        this._spawnAnimation = 2;
    }

    @Override
    public Clan getClan() {
        return null;
    }
}

