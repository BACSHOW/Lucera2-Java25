/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import l2.commons.text.StrTable;
import l2.gameserver.Config;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectArray;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameObjectsStorage {
    private static final Logger _log = LoggerFactory.getLogger(GameObjectsStorage.class);
    private static final int FF = 0;
    private static final int FG = 1;
    private static final int FH = 2;
    private static final int FI = 30;
    private static final int FJ = 31;
    private static final GameObjectArray[] FK = new GameObjectArray[31];
    private static long FL;
    private static int FM;

    private static GameObjectArray<Player> cg() {
        return FK[0];
    }

    private static GameObjectArray<Playable> ch() {
        return FK[1];
    }

    private static GameObjectArray<NpcInstance> ci() {
        return FK[2];
    }

    private static int b(GameObject gameObject) {
        if (gameObject.isNpc()) {
            return 2;
        }
        if (gameObject.isPlayable()) {
            return gameObject.isPlayer() ? 0 : 1;
        }
        return 30;
    }

    public static GameObject get(long l) {
        int n;
        if (l == 0L || (n = GameObjectsStorage.j(l)) == 31) {
            return null;
        }
        Object e = FK[n].get(GameObjectsStorage.k(l));
        return e != null && ((GameObject)e).getObjectId() == GameObjectsStorage.getStoredObjectId(l) ? (GameObject)e : null;
    }

    public static GameObject get(Long l) {
        int n;
        if (l == null || l == 0L || (n = GameObjectsStorage.j(l)) == 31) {
            return null;
        }
        Object e = FK[n].get(GameObjectsStorage.k(l));
        return e != null && ((GameObject)e).getObjectId() == GameObjectsStorage.getStoredObjectId(l) ? (GameObject)e : null;
    }

    public static boolean isStored(long l) {
        int n;
        if (l == 0L || (n = GameObjectsStorage.j(l)) == 31) {
            return false;
        }
        Object e = FK[n].get(GameObjectsStorage.k(l));
        return e != null && ((GameObject)e).getObjectId() == GameObjectsStorage.getStoredObjectId(l);
    }

    public static NpcInstance getAsNpc(long l) {
        return (NpcInstance)GameObjectsStorage.get(l);
    }

    public static NpcInstance getAsNpc(Long l) {
        return (NpcInstance)GameObjectsStorage.get(l);
    }

    public static Player getAsPlayer(long l) {
        return (Player)GameObjectsStorage.get(l);
    }

    public static Playable getAsPlayable(long l) {
        return (Playable)GameObjectsStorage.get(l);
    }

    public static Creature getAsCharacter(long l) {
        return (Creature)GameObjectsStorage.get(l);
    }

    public static MonsterInstance getAsMonster(long l) {
        return (MonsterInstance)GameObjectsStorage.get(l);
    }

    public static PetInstance getAsPet(long l) {
        return (PetInstance)GameObjectsStorage.get(l);
    }

    public static ItemInstance getAsItem(long l) {
        return (ItemInstance)GameObjectsStorage.get(l);
    }

    public static boolean contains(long l) {
        return GameObjectsStorage.get(l) != null;
    }

    public static Player getPlayer(String string) {
        return GameObjectsStorage.cg().findByName(string);
    }

    public static Player getPlayer(int n) {
        return GameObjectsStorage.cg().findByObjectId(n);
    }

    public static List<Player> getAllPlayers() {
        return GameObjectsStorage.cg().getAll();
    }

    public static Iterable<Player> getAllPlayersForIterate() {
        return GameObjectsStorage.cg();
    }

    public static int getAllPlayersCount() {
        return GameObjectsStorage.cg().getRealSize();
    }

    public static int getAllObjectsCount() {
        int n = 0;
        for (GameObjectArray gameObjectArray : FK) {
            if (gameObjectArray == null) continue;
            n += gameObjectArray.getRealSize();
        }
        return n;
    }

    public static List<GameObject> getAllObjects() {
        ArrayList<GameObject> arrayList = new ArrayList<GameObject>(GameObjectsStorage.getAllObjectsCount());
        for (GameObjectArray gameObjectArray : FK) {
            if (gameObjectArray == null) continue;
            gameObjectArray.getAll(arrayList);
        }
        return arrayList;
    }

    public static GameObject findObject(int n) {
        GameObject gameObject = null;
        for (GameObjectArray gameObjectArray : FK) {
            if (gameObjectArray == null) continue;
            Object e = gameObjectArray.findByObjectId(n);
            gameObject = (GameObject)e;
            if (e == null) continue;
            return gameObject;
        }
        return null;
    }

    public static int getAllOfflineCount() {
        if (!Config.SERVICES_OFFLINE_TRADE_ALLOW) {
            return 0;
        }
        long l = System.currentTimeMillis();
        if (l > FL) {
            FL = l + 10000L;
            FM = 0;
            for (Player player : GameObjectsStorage.cg()) {
                if (!player.isInOfflineMode()) continue;
                ++FM;
            }
        }
        return FM;
    }

    public static List<NpcInstance> getAllNpcs() {
        return GameObjectsStorage.ci().getAll();
    }

    public static Iterable<NpcInstance> getAllNpcsForIterate() {
        return GameObjectsStorage.ci();
    }

    public static NpcInstance getByNpcId(int n) {
        NpcInstance npcInstance = null;
        for (NpcInstance npcInstance2 : GameObjectsStorage.ci()) {
            if (n != npcInstance2.getNpcId()) continue;
            if (!npcInstance2.isDead()) {
                return npcInstance2;
            }
            npcInstance = npcInstance2;
        }
        return npcInstance;
    }

    public static List<NpcInstance> getAllByNpcId(int n, boolean bl) {
        ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
        for (NpcInstance npcInstance : GameObjectsStorage.ci()) {
            if (npcInstance.getTemplate() == null || n != npcInstance.getTemplate().getNpcId() || bl && npcInstance.isDead()) continue;
            arrayList.add(npcInstance);
        }
        return arrayList;
    }

    public static List<NpcInstance> getAllByNpcId(int[] nArray, boolean bl) {
        ArrayList<NpcInstance> arrayList = new ArrayList<NpcInstance>();
        for (NpcInstance npcInstance : GameObjectsStorage.ci()) {
            if (bl && npcInstance.isDead()) continue;
            for (int n : nArray) {
                if (n != npcInstance.getNpcId()) continue;
                arrayList.add(npcInstance);
            }
        }
        return arrayList;
    }

    public static NpcInstance getNpc(String string) {
        List<NpcInstance> list = GameObjectsStorage.ci().findAllByName(string);
        if (list.size() == 0) {
            return null;
        }
        for (NpcInstance npcInstance : list) {
            if (npcInstance.isDead()) continue;
            return npcInstance;
        }
        if (list.size() > 0) {
            return list.remove(list.size() - 1);
        }
        return null;
    }

    public static NpcInstance getNpc(int n) {
        return GameObjectsStorage.ci().findByObjectId(n);
    }

    public static long put(GameObject gameObject) {
        int n = GameObjectsStorage.b(gameObject);
        return (long)gameObject.getObjectId() & 0xFFFFFFFFL | ((long)n & 0x1FL) << 32 | ((long)FK[n].add(gameObject) & 0xFFFFFFFFL) << 37;
    }

    public static long putDummy(GameObject gameObject) {
        return GameObjectsStorage.objIdNoStore(gameObject.getObjectId());
    }

    public static long objIdNoStore(int n) {
        return (long)n & 0xFFFFFFFFL | 0x1F00000000L;
    }

    public static long refreshId(Creature creature) {
        return (long)creature.getObjectId() & 0xFFFFFFFFL | creature.getStoredId() >> 32 << 32;
    }

    public static GameObject remove(long l) {
        int n = GameObjectsStorage.j(l);
        return n == 31 ? null : (GameObject)FK[n].remove(GameObjectsStorage.k(l), GameObjectsStorage.getStoredObjectId(l));
    }

    private static int j(long l) {
        return (int)(l >> 32) & 0x1F;
    }

    private static int k(long l) {
        return (int)(l >> 37);
    }

    public static int getStoredObjectId(long l) {
        return (int)l;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static StrTable getStats() {
        StrTable strTable = new StrTable("L2 Objects Storage Stats");
        for (int i = 0; i < FK.length; ++i) {
            GameObjectArray gameObjectArray = FK[i];
            if (gameObjectArray == null) continue;
            GameObjectArray gameObjectArray2 = gameObjectArray;
            synchronized (gameObjectArray2) {
                strTable.set(i, "Name", gameObjectArray.name);
                strTable.set(i, "Size / Real", gameObjectArray.size() + " / " + gameObjectArray.getRealSize());
                strTable.set(i, "Capacity / init", gameObjectArray.capacity() + " / " + gameObjectArray.initCapacity);
                continue;
            }
        }
        return strTable;
    }

    static {
        GameObjectsStorage.FK[0] = new GameObjectArray("PLAYERS", Config.MAXIMUM_ONLINE_USERS, 1);
        GameObjectsStorage.FK[1] = new GameObjectArray("SUMMONS", Config.MAXIMUM_ONLINE_USERS, 1);
        GameObjectsStorage.FK[2] = new GameObjectArray("NPCS", 60000 * Config.RATE_MOB_SPAWN, 5000);
        GameObjectsStorage.FK[30] = new GameObjectArray("OTHER", 2000, 1000);
        FL = 0L;
        FM = 0;
    }
}

