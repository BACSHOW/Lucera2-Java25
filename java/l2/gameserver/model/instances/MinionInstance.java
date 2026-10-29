/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class MinionInstance
extends MonsterInstance {
    private MonsterInstance Gn;

    public MinionInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    public void setLeader(MonsterInstance monsterInstance) {
        this.Gn = monsterInstance;
    }

    public MonsterInstance getLeader() {
        return this.Gn;
    }

    public boolean isRaidFighter() {
        return this.getLeader() != null && this.getLeader().isRaid();
    }

    @Override
    protected void onDeath(Creature creature) {
        if (this.getLeader() != null) {
            this.getLeader().notifyMinionDied(this);
        }
        super.onDeath(creature);
    }

    @Override
    protected void onDecay() {
        this.decayMe();
        this._spawnAnimation = 2;
    }

    @Override
    public boolean isFearImmune() {
        return this.isRaidFighter();
    }

    @Override
    public Location getSpawnedLoc() {
        return this.getLeader() != null ? this.getLeader().getLoc() : this.getLoc();
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    @Override
    public boolean isMinion() {
        return true;
    }
}

