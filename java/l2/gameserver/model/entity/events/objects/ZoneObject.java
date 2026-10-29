/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.InitableObject;

public class ZoneObject
implements InitableObject {
    private String _name;
    private Zone _zone;

    public ZoneObject(String string) {
        this._name = string;
    }

    @Override
    public void initObject(GlobalEvent globalEvent) {
        Reflection reflection = globalEvent.getReflection();
        this._zone = reflection.getZone(this._name);
    }

    public void setActive(boolean bl) {
        this._zone.setActive(bl);
    }

    public void setActive(boolean bl, GlobalEvent globalEvent) {
        this.setActive(bl);
    }

    public Zone getZone() {
        return this._zone;
    }

    public List<Player> getInsidePlayers() {
        return this._zone.getInsidePlayers();
    }

    public boolean checkIfInZone(Creature creature) {
        return this._zone.checkIfInZone(creature);
    }
}

