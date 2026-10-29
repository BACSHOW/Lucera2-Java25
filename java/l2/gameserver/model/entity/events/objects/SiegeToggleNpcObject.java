/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.util.Set;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2.gameserver.utils.Location;

public class SiegeToggleNpcObject
implements SpawnableObject {
    private SiegeToggleNpcInstance Rl;
    private Location CD;

    public SiegeToggleNpcObject(int n, int n2, Location location, int n3, Set<String> set) {
        this.CD = location;
        this.Rl = (SiegeToggleNpcInstance)NpcHolder.getInstance().getTemplate(n).getNewInstance();
        this.Rl.initFake(n2);
        this.Rl.setMaxHp(n3);
        this.Rl.setZoneList(set);
    }

    @Override
    public void spawnObject(GlobalEvent globalEvent) {
        this.Rl.decayFake();
        if (globalEvent.isInProgress()) {
            this.Rl.addEvent(globalEvent);
        } else {
            this.Rl.removeEvent(globalEvent);
        }
        this.Rl.setCurrentHp(this.Rl.getMaxHp(), true);
        this.Rl.spawnMe(this.CD);
    }

    @Override
    public void despawnObject(GlobalEvent globalEvent) {
        this.Rl.removeEvent(globalEvent);
        this.Rl.decayFake();
        this.Rl.decayMe();
    }

    @Override
    public void refreshObject(GlobalEvent globalEvent) {
    }

    public SiegeToggleNpcInstance getToggleNpc() {
        return this.Rl;
    }

    public boolean isAlive() {
        return this.Rl.isVisible();
    }
}

