/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.door.impl;

import l2.gameserver.listener.actor.door.OnOpenCloseListener;
import l2.gameserver.model.instances.DoorInstance;

public class MasterOnOpenCloseListenerImpl
implements OnOpenCloseListener {
    private DoorInstance Cy;

    public MasterOnOpenCloseListenerImpl(DoorInstance doorInstance) {
        this.Cy = doorInstance;
    }

    @Override
    public void onOpen(DoorInstance doorInstance) {
        this.Cy.openMe();
    }

    @Override
    public void onClose(DoorInstance doorInstance) {
        this.Cy.closeMe();
    }
}

