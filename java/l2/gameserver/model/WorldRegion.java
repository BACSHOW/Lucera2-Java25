/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import l2.commons.lang.ArrayUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldRegion
implements Iterable<GameObject> {
    public static final WorldRegion[] EMPTY_L2WORLDREGION_ARRAY = new WorldRegion[0];
    private static final Logger _log = LoggerFactory.getLogger(WorldRegion.class);
    private final int LU;
    private final int LV;
    private final int LW;
    private volatile GameObject[] LX = GameObject.EMPTY_L2OBJECT_ARRAY;
    private int LY = 0;
    private volatile Zone[] LZ = Zone.EMPTY_L2ZONE_ARRAY;
    private int Ma = 0;
    private final AtomicBoolean Mb = new AtomicBoolean();
    private Future<?> Mc;
    private final Lock lock = new ReentrantLock();

    WorldRegion(int n, int n2, int n3) {
        this.LU = n;
        this.LV = n2;
        this.LW = n3;
    }

    int getX() {
        return this.LU;
    }

    int getY() {
        return this.LV;
    }

    int getZ() {
        return this.LW;
    }

    void setActive(boolean bl) {
        if (!this.Mb.compareAndSet(!bl, bl)) {
            return;
        }
        for (GameObject gameObject : this) {
            NpcInstance npcInstance;
            if (!gameObject.isNpc() || (npcInstance = (NpcInstance)gameObject).getAI().isActive() == this.isActive()) continue;
            if (this.isActive()) {
                npcInstance.getAI().startAITask();
                npcInstance.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                npcInstance.startRandomAnimation();
                continue;
            }
            if (npcInstance.getAI().isGlobalAI()) continue;
            npcInstance.getAI().stopAITask();
            npcInstance.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            npcInstance.stopRandomAnimation();
        }
    }

    void addToPlayers(GameObject gameObject, Creature creature) {
        if (gameObject == null) {
            return;
        }
        Player player = null;
        if (gameObject.isPlayer()) {
            player = (Player)gameObject;
        }
        int n = gameObject.getObjectId();
        int n2 = gameObject.getReflectionId();
        Map<Player, List<L2GameServerPacket>> packetsByPlayer = new HashMap<>();
        for (GameObject gameObject2 : this) {
            if (gameObject2.getObjectId() == n || gameObject2.getReflectionId() != n2) continue;
            if (player != null && isWithinVisibleRadius(player, gameObject2)) {
                if (Config.ENABLE_NPC_INFO_PACING && gameObject2.isNpc()) {
                    player.getNpcSpawnPacer().queueNpc((NpcInstance) gameObject2);
                } else {
                    appendPackets(packetsByPlayer, player, player.addVisibleObject(gameObject2, null));
                }
            }
            if (gameObject2.isPlayer()) {
                Player player2 = (Player)gameObject2;
                if (isWithinVisibleRadius(player2, gameObject)) {
                    if (Config.ENABLE_NPC_INFO_PACING && gameObject.isNpc()) {
                        player2.getNpcSpawnPacer().queueNpc((NpcInstance) gameObject);
                    } else {
                        appendPackets(packetsByPlayer, player2, player2.addVisibleObject(gameObject, creature));
                    }
                }
            }
        }
        for (Map.Entry<Player, List<L2GameServerPacket>> entry : packetsByPlayer.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                entry.getKey().sendPacket(entry.getValue());
            }
        }
    }

    private static void appendPackets(Map<Player, List<L2GameServerPacket>> packetsByPlayer,
                                      Player player, List<L2GameServerPacket> packets) {
        if (packets == null || packets.isEmpty()) {
            return;
        }
        packetsByPlayer.computeIfAbsent(player, ignored -> new ArrayList<>(packets.size())).addAll(packets);
    }

    static boolean isWithinVisibleRadius(GameObject observer, GameObject target) {
        if (!Config.ENABLE_VISIBLE_OBJECT_RADIUS_FILTER || observer == null || target == null) {
            return true;
        }
        return isWithinVisibleRadius(observer.getX(), observer.getY(), target.getX(), target.getY(),
                Config.VISIBLE_OBJECT_RADIUS);
    }

    static boolean isWithinVisibleRadius(int observerX, int observerY, int targetX, int targetY, int radius) {
        if (radius < 0) {
            return true;
        }
        long dx = (long)targetX - observerX;
        long dy = (long)targetY - observerY;
        if (Math.abs(dx) > radius || Math.abs(dy) > radius) {
            return false;
        }
        long radiusSquared = (long)radius * radius;
        return dx * dx + dy * dy <= radiusSquared;
    }

    void removeFromPlayers(GameObject gameObject) {
        if (gameObject == null) {
            return;
        }
        Player player = null;
        if (gameObject.isPlayer()) {
            player = (Player)gameObject;
        }
        int n = gameObject.getObjectId();
        Reflection reflection = gameObject.getReflection();
        List<L2GameServerPacket> list = null;
        for (GameObject gameObject2 : this) {
            if (gameObject2.getObjectId() == n || gameObject2.getReflection() != reflection) continue;
            if (player != null) {
                if (Config.ENABLE_DELETE_OBJECT_COALESCING && gameObject2.isNpc() && player.getNpcSpawnPacer().cancelPending(gameObject2.getObjectId())) {
                    // Coalesced: NPC ainda não tinha sido transmitido ao cliente
                } else {
                    player.sendPacket(player.removeVisibleObject(gameObject2, null));
                }
            }
            if (!gameObject2.isPlayer()) continue;
            Player player2 = (Player)gameObject2;
            if (Config.ENABLE_DELETE_OBJECT_COALESCING && gameObject.isNpc() && player2.getNpcSpawnPacer().cancelPending(gameObject.getObjectId())) {
                // Coalesced: NPC ainda não tinha sido transmitido ao cliente
            } else {
                player2.sendPacket(player2.removeVisibleObject(gameObject, list == null ? gameObject.deletePacketList() : list));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addObject(GameObject gameObject) {
        if (gameObject == null) {
            return;
        }
        this.lock.lock();
        try {
            GameObject[] gameObjectArray = this.LX;
            GameObject[] gameObjectArray2 = new GameObject[this.LY + 1];
            System.arraycopy(gameObjectArray, 0, gameObjectArray2, 0, this.LY);
            gameObjectArray = gameObjectArray2;
            gameObjectArray[this.LY++] = gameObject;
            this.LX = gameObjectArray2;
            if (gameObject.isPlayer() && this.Ma++ == 0) {
                if (this.Mc != null) {
                    this.Mc.cancel(false);
                }
                this.Mc = ThreadPoolManager.getInstance().schedule(new ActivateTask(true), 1000L);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeObject(GameObject gameObject) {
        if (gameObject == null) {
            return;
        }
        this.lock.lock();
        try {
            GameObject[] gameObjectArray = this.LX;
            int n = -1;
            for (int i = 0; i < this.LY; ++i) {
                if (gameObjectArray[i] != gameObject) continue;
                n = i;
                break;
            }
            if (n == -1) {
                return;
            }
            --this.LY;
            GameObject[] gameObjectArray2 = new GameObject[this.LY];
            gameObjectArray[n] = gameObjectArray[this.LY];
            System.arraycopy(gameObjectArray, 0, gameObjectArray2, 0, this.LY);
            this.LX = gameObjectArray2;
            if (gameObject.isPlayer() && --this.Ma == 0) {
                if (this.Mc != null) {
                    this.Mc.cancel(false);
                }
                this.Mc = ThreadPoolManager.getInstance().schedule(new ActivateTask(false), 60000L);
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public int getObjectsSize() {
        return this.LY;
    }

    public int getPlayersCount() {
        return this.Ma;
    }

    public boolean isEmpty() {
        return this.Ma == 0;
    }

    public boolean isActive() {
        return this.Mb.get();
    }

    void addZone(Zone zone) {
        this.lock.lock();
        try {
            this.LZ = ArrayUtils.add(this.LZ, zone);
        }
        finally {
            this.lock.unlock();
        }
    }

    void removeZone(Zone zone) {
        this.lock.lock();
        try {
            this.LZ = ArrayUtils.remove(this.LZ, zone);
        }
        finally {
            this.lock.unlock();
        }
    }

    public Zone[] getZones() {
        return this.LZ;
    }

    /**
     * Dhousefe-style zero-allocation predicate check for matching objects in this region.
     * Short-circuits immediately upon the first matching object without allocating any list or iterator.
     */
    @SuppressWarnings("unchecked")
    public <T extends GameObject> boolean anyType(Class<T> type, GameObject observer, java.util.function.Predicate<T> filter) {
        GameObject[] objects = this.LX;
        int size = this.LY;
        for (int i = 0; i < size && i < objects.length; i++) {
            GameObject obj = objects[i];
            if (obj == null || !type.isInstance(obj)) {
                continue;
            }
            if (observer != null && observer.getReflectionId() != obj.getReflectionId()) {
                continue;
            }
            T actor = (T) obj;
            if (filter == null || filter.test(actor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dhousefe-style zero-allocation traversal for matching objects in this region.
     */
    @SuppressWarnings("unchecked")
    public <T extends GameObject> void forEachType(Class<T> type, GameObject observer, java.util.function.Predicate<T> filter, java.util.function.Consumer<T> action) {
        GameObject[] objects = this.LX;
        int size = this.LY;
        for (int i = 0; i < size && i < objects.length; i++) {
            GameObject obj = objects[i];
            if (obj == null || !type.isInstance(obj)) {
                continue;
            }
            if (observer != null && observer.getReflectionId() != obj.getReflectionId()) {
                continue;
            }
            T actor = (T) obj;
            if (filter == null || filter.test(actor)) {
                action.accept(actor);
            }
        }
    }

    public String toString() {
        return "[" + this.LU + ", " + this.LV + ", " + this.LW + "]";
    }

    @Override
    public Iterator<GameObject> iterator() {
        return new InternalIterator(this.LX);
    }

    private class InternalIterator
    implements Iterator<GameObject> {
        final GameObject[] objects;
        int cursor = 0;

        public InternalIterator(GameObject[] gameObjectArray) {
            this.objects = gameObjectArray;
        }

        @Override
        public boolean hasNext() {
            if (this.cursor < this.objects.length) {
                return this.objects[this.cursor] != null;
            }
            return false;
        }

        @Override
        public GameObject next() {
            return this.objects[this.cursor++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public class ActivateTask
    extends RunnableImpl {
        private boolean Md;

        public ActivateTask(boolean bl) {
            this.Md = bl;
        }

        @Override
        public void runImpl() throws Exception {
            if (this.Md) {
                World.activate(WorldRegion.this);
            } else {
                World.deactivate(WorldRegion.this);
            }
        }
    }
}
