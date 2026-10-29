/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.GameObjectTasks;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class NpcUtils {
    public static NpcInstance spawnSingle(int n, int n2, int n3, int n4) {
        return NpcUtils.spawnSingle(n, new Location(n2, n3, n4, -1), ReflectionManager.DEFAULT, 0L, null);
    }

    public static NpcInstance spawnSingle(int n, int n2, int n3, int n4, long l) {
        return NpcUtils.spawnSingle(n, new Location(n2, n3, n4, -1), ReflectionManager.DEFAULT, l, null);
    }

    public static NpcInstance spawnSingle(int n, int n2, int n3, int n4, int n5, long l) {
        return NpcUtils.spawnSingle(n, new Location(n2, n3, n4, n5), ReflectionManager.DEFAULT, l, null);
    }

    public static NpcInstance spawnSingle(int n, Location location) {
        return NpcUtils.spawnSingle(n, location, ReflectionManager.DEFAULT, 0L, null);
    }

    public static NpcInstance spawnSingle(int n, Location location, long l) {
        return NpcUtils.spawnSingle(n, location, ReflectionManager.DEFAULT, l, null);
    }

    public static NpcInstance spawnSingle(int n, Location location, Reflection reflection) {
        return NpcUtils.spawnSingle(n, location, reflection, 0L, null);
    }

    public static NpcInstance spawnSingle(int n, Location location, Reflection reflection, long l) {
        return NpcUtils.spawnSingle(n, location, reflection, l, null);
    }

    public static NpcInstance spawnSingle(int n, Location location, Reflection reflection, long l, String string) {
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
        if (npcTemplate == null) {
            throw new NullPointerException("Npc template id : " + n + " not found!");
        }
        NpcInstance npcInstance = npcTemplate.getNewInstance();
        npcInstance.setHeading(location.h < 0 ? Rnd.get(65535) : location.h);
        npcInstance.setSpawnedLoc(location);
        npcInstance.setReflection(reflection);
        npcInstance.setCurrentHpMp(npcInstance.getMaxHp(), npcInstance.getMaxMp(), true);
        if (string != null) {
            npcInstance.setTitle(string);
        }
        npcInstance.spawnMe(npcInstance.getSpawnedLoc());
        if (l > 0L) {
            ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(npcInstance), l);
        }
        return npcInstance;
    }
}

