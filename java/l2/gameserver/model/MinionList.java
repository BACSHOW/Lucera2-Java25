/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.MinionData;

public class MinionList {
    private final Set<MinionData> Gl;
    private final Set<MinionInstance> Gm;
    private final Lock lock;
    private final MonsterInstance Gn;

    public MinionList(MonsterInstance monsterInstance) {
        this.Gn = monsterInstance;
        this.Gm = new HashSet<MinionInstance>();
        this.Gl = new HashSet<MinionData>();
        this.Gl.addAll(this.Gn.getTemplate().getMinionData());
        this.lock = new ReentrantLock();
    }

    public boolean addMinion(MinionData minionData) {
        this.lock.lock();
        try {
            boolean bl = this.Gl.add(minionData);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    public boolean addMinion(MinionInstance minionInstance) {
        this.lock.lock();
        try {
            boolean bl = this.Gm.add(minionInstance);
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasAliveMinions() {
        this.lock.lock();
        try {
            for (MinionInstance minionInstance : this.Gm) {
                if (!minionInstance.isVisible() || minionInstance.isDead()) continue;
                boolean bl = true;
                return bl;
            }
        }
        finally {
            this.lock.unlock();
        }
        return false;
    }

    public boolean hasMinions() {
        return this.Gl.size() > 0 || this.Gm.size() > 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<MinionInstance> getAliveMinions() {
        ArrayList<MinionInstance> arrayList = new ArrayList<MinionInstance>(this.Gm.size());
        this.lock.lock();
        try {
            for (MinionInstance minionInstance : this.Gm) {
                if (!minionInstance.isVisible() || minionInstance.isDead()) continue;
                arrayList.add(minionInstance);
            }
        }
        finally {
            this.lock.unlock();
        }
        return arrayList;
    }

    public void spawnMinions() {
        for (MinionData minionData : this.Gl) {
            this.spawnMinion(minionData.getMinionId(), minionData.getAmount(), true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void spawnMinion(int n, int n2, boolean bl) {
        if (this.Gn.isMinion() && this.Gn.getNpcId() == n) {
            return;
        }
        this.lock.lock();
        try {
            int n3 = n2;
            for (MinionInstance minionInstance : this.Gm) {
                if (minionInstance.getNpcId() != n || !bl && !minionInstance.isDead()) continue;
                --n3;
                minionInstance.stopDecay();
                minionInstance.decayMe();
                minionInstance.refreshID();
                this.Gn.spawnMinion(minionInstance);
            }
            if (n3 > 0) {
                for (int i = 0; i < n3; ++i) {
                    MinionInstance minionInstance;
                    minionInstance = new MinionInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(n));
                    minionInstance.setLeader(this.Gn);
                    this.Gn.spawnMinion(minionInstance);
                    this.Gm.add(minionInstance);
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void unspawnMinions() {
        this.lock.lock();
        try {
            for (MinionInstance minionInstance : this.Gm) {
                minionInstance.decayMe();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void deleteMinions() {
        this.lock.lock();
        try {
            for (MinionInstance minionInstance : this.Gm) {
                minionInstance.deleteMe();
            }
            this.Gm.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
}

