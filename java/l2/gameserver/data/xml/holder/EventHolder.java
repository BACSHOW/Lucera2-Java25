/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.GlobalEvent;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

public final class EventHolder
extends AbstractHolder {
    private static final EventHolder vW = new EventHolder();
    private final IntObjectMap<GlobalEvent> vX = new TreeIntObjectMap<GlobalEvent>();

    public static EventHolder getInstance() {
        return vW;
    }

    public void addEvent(EventType eventType, GlobalEvent globalEvent) {
        this.vX.put(eventType.step() + globalEvent.getId(), globalEvent);
    }

    public <E extends GlobalEvent> E getEvent(EventType eventType, int n) {
        return (E)this.vX.get(eventType.step() + n);
    }

    public void findEvent(Player player) {
        for (GlobalEvent globalEvent : this.vX.values()) {
            if (!globalEvent.isParticle(player)) continue;
            player.addEvent(globalEvent);
        }
    }

    public void callInit() {
        for (GlobalEvent globalEvent : this.vX.values()) {
            globalEvent.initEvent();
        }
    }

    @Override
    public int size() {
        return this.vX.size();
    }

    @Override
    public void clear() {
        this.vX.clear();
    }
}

