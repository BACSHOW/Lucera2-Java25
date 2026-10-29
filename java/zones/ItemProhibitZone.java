/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.Collection;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.LockType;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemProhibitZone
implements OnZoneEnterLeaveListener,
ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(ItemProhibitZone.class);
    private static final String cac = "zoneProhibitedItemIds";

    @Override
    public void onZoneEnter(Zone zone, Creature creature) {
        if (creature == null || !creature.isPlayer()) {
            return;
        }
        int[] nArray = zone.getParams().getIntegerArray(cac);
        Player player = creature.getPlayer();
        if (nArray == null || nArray.length == 0) {
            return;
        }
        for (ItemInstance itemInstance : player.getInventory().getPaperdollItems()) {
            if (itemInstance == null || !ArrayUtils.contains(nArray, itemInstance.getItemId())) continue;
            player.getInventory().unEquipItem(itemInstance);
        }
        player.getInventory().lockItems(LockType.INCLUDE, nArray);
    }

    @Override
    public void onZoneLeave(Zone zone, Creature creature) {
        Player player = creature.getPlayer();
        int[] nArray = zone.getParams().getIntegerArray(cac);
        if (nArray == null || nArray.length == 0 || player == null) {
            return;
        }
        player.getInventory().unlock();
    }

    @Override
    public void onLoad() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            int[] nArray = zone.getParams().getIntegerArray(cac, ArrayUtils.EMPTY_INT_ARRAY);
            if (nArray == null || nArray.length == 0) continue;
            zone.addListener(this);
            ++n;
        }
        if (n > 0) {
            sQ.info("ItemProhibitZone: added {} item(s) prohibited zone(s).", (Object)n);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        for (Zone zone : ReflectionManager.DEFAULT.getZones()) {
            zone.removeListener(this);
        }
    }
}

