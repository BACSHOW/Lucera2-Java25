/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.InitableObject;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.instances.DoorInstance;

public class DoorObject
implements InitableObject,
SpawnableObject {
    private int _id;
    private DoorInstance Cy;
    private boolean Rg;

    public DoorObject(int n) {
        this._id = n;
    }

    @Override
    public void initObject(GlobalEvent globalEvent) {
        this.Cy = globalEvent.getReflection().getDoor(this._id);
    }

    @Override
    public void spawnObject(GlobalEvent globalEvent) {
        this.refreshObject(globalEvent);
    }

    @Override
    public void despawnObject(GlobalEvent globalEvent) {
        Reflection reflection = globalEvent.getReflection();
        if (reflection == ReflectionManager.DEFAULT) {
            this.refreshObject(globalEvent);
        }
    }

    @Override
    public void refreshObject(GlobalEvent globalEvent) {
        if (!globalEvent.isInProgress()) {
            this.Cy.removeEvent(globalEvent);
        } else {
            this.Cy.addEvent(globalEvent);
        }
        if (this.Cy.getCurrentHp() <= 0.0) {
            this.Cy.decayMe();
            this.Cy.spawnMe();
        }
        this.Cy.setCurrentHp((double)this.Cy.getMaxHp() * (this.isWeak() ? 0.5 : 1.0), true);
        this.close(globalEvent);
    }

    public int getUId() {
        return this.Cy.getDoorId();
    }

    public int getUpgradeValue() {
        return this.Cy.getUpgradeHp();
    }

    public void setUpgradeValue(GlobalEvent globalEvent, int n) {
        this.Cy.setUpgradeHp(n);
        this.refreshObject(globalEvent);
    }

    public void open(GlobalEvent globalEvent) {
        this.Cy.openMe(null, !globalEvent.isInProgress());
    }

    public void close(GlobalEvent globalEvent) {
        this.Cy.closeMe(null, !globalEvent.isInProgress());
    }

    public DoorInstance getDoor() {
        return this.Cy;
    }

    public boolean isWeak() {
        return this.Rg;
    }

    public void setWeak(boolean bl) {
        this.Rg = bl;
    }
}

