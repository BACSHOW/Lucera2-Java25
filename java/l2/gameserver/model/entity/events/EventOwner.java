/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import l2.gameserver.model.entity.events.GlobalEvent;

public abstract class EventOwner
implements Serializable {
    private Set<GlobalEvent> Qm = new HashSet<GlobalEvent>(2);

    public <E extends GlobalEvent> E getEvent(Class<E> clazz) {
        for (GlobalEvent globalEvent : this.Qm) {
            if (globalEvent.getClass() == clazz) {
                return (E)globalEvent;
            }
            if (!clazz.isAssignableFrom(globalEvent.getClass())) continue;
            return (E)globalEvent;
        }
        return null;
    }

    public void addEvent(GlobalEvent globalEvent) {
        this.Qm.add(globalEvent);
    }

    public void removeEvent(GlobalEvent globalEvent) {
        this.Qm.remove(globalEvent);
    }

    public void removeEventsByClass(Class<? extends GlobalEvent> clazz) {
        for (GlobalEvent globalEvent : this.Qm) {
            if (globalEvent.getClass() == clazz) {
                this.Qm.remove(globalEvent);
                continue;
            }
            if (!clazz.isAssignableFrom(globalEvent.getClass())) continue;
            this.Qm.remove(globalEvent);
        }
    }

    public Set<GlobalEvent> getEvents() {
        return this.Qm;
    }
}

