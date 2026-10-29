/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.util.ArrayList;
import java.util.Iterator;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Util;

public class SpawnTaskManager {
    private SpawnTask[] arr = new SpawnTask[500];
    private int ars = 0;
    private final Object art = new Object();
    private static SpawnTaskManager aru;

    public SpawnTaskManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnScheduler(), 2000L, 2000L);
    }

    public static SpawnTaskManager getInstance() {
        if (aru == null) {
            aru = new SpawnTaskManager();
        }
        return aru;
    }

    public void addSpawnTask(NpcInstance npcInstance, long l) {
        this.removeObject(npcInstance);
        this.a(new SpawnTask(npcInstance, System.currentTimeMillis() + l));
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("============= SpawnTask Manager Report ============\n\r");
        stringBuilder.append("Tasks count: ").append(this.ars).append("\n\r");
        stringBuilder.append("Tasks dump:\n\r");
        long l = System.currentTimeMillis();
        for (SpawnTask spawnTask : this.arr) {
            if (spawnTask == null) continue;
            stringBuilder.append("Class/Name: ").append(spawnTask.getClass().getSimpleName()).append('/').append(spawnTask.getActor());
            stringBuilder.append(" spawn timer: ").append(Util.formatTime((int)((spawnTask.endtime - l) / 1000L))).append("\n\r");
        }
        return stringBuilder.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void a(SpawnTask spawnTask) {
        Object object = this.art;
        synchronized (object) {
            if (this.ars >= this.arr.length) {
                SpawnTask[] spawnTaskArray = new SpawnTask[this.arr.length * 2];
                for (int i = 0; i < this.ars; ++i) {
                    spawnTaskArray[i] = this.arr[i];
                }
                this.arr = spawnTaskArray;
            }
            this.arr[this.ars] = spawnTask;
            ++this.ars;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeObject(NpcInstance npcInstance) {
        Object object = this.art;
        synchronized (object) {
            if (this.ars > 1) {
                int n = -1;
                for (int i = 0; i < this.ars; ++i) {
                    if (this.arr[i].getActor() != npcInstance) continue;
                    n = i;
                }
                if (n > -1) {
                    this.arr[n] = this.arr[this.ars - 1];
                    this.arr[this.ars - 1] = null;
                    --this.ars;
                }
            } else if (this.ars == 1 && this.arr[0].getActor() == npcInstance) {
                this.arr[0] = null;
                this.ars = 0;
            }
        }
    }

    private class SpawnTask {
        private final HardReference<NpcInstance> arv;
        public long endtime;

        SpawnTask(NpcInstance npcInstance, long l) {
            this.arv = npcInstance.getRef();
            this.endtime = l;
        }

        public NpcInstance getActor() {
            return this.arv.get();
        }
    }

    public class SpawnScheduler
    extends RunnableImpl {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() throws Exception {
            if (SpawnTaskManager.this.ars > 0) {
                try {
                    ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
                    Object lock = SpawnTaskManager.this.art;
                    synchronized (lock) {
                        long l = System.currentTimeMillis();
                        int n = SpawnTaskManager.this.ars;
                        for (int i = n - 1; i >= 0; --i) {
                            try {
                                SpawnTask spawnTask = SpawnTaskManager.this.arr[i];
                                if (spawnTask != null && spawnTask.endtime > 0L && l > spawnTask.endtime) {
                                    NpcInstance npcInstance = spawnTask.getActor();
                                    if (npcInstance != null && npcInstance.getSpawn() != null) {
                                        arrayList.add(npcInstance);
                                    }
                                    spawnTask.endtime = -1L;
                                }
                                if (spawnTask != null && spawnTask.getActor() != null && spawnTask.endtime >= 0L) continue;
                                if (i == SpawnTaskManager.this.ars - 1) {
                                    ((SpawnTaskManager)SpawnTaskManager.this).arr[i] = null;
                                } else {
                                    ((SpawnTaskManager)SpawnTaskManager.this).arr[i] = SpawnTaskManager.this.arr[SpawnTaskManager.this.ars - 1];
                                    ((SpawnTaskManager)SpawnTaskManager.this).arr[((SpawnTaskManager)SpawnTaskManager.this).ars - 1] = null;
                                }
                                if (SpawnTaskManager.this.ars <= 0) continue;
                                SpawnTaskManager.this.ars--;
                                continue;
                            }
                            catch (Exception exception) {
                                _log.error("", exception);
                            }
                        }
                    }
                    for (NpcInstance npcInstance : arrayList) {
                        Spawner spawner = npcInstance.getSpawn();
                        if (spawner == null) continue;
                        spawner.decreaseScheduledCount();
                        if (!spawner.isDoRespawn()) continue;
                        spawner.respawnNpc(npcInstance);
                    }
                }
                catch (Exception exception) {
                    _log.error("", exception);
                }
            }
        }
    }
}

