/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.door;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.instances.DoorInstance;

public interface OnOpenCloseListener
extends CharListener {
    public void onOpen(DoorInstance var1);

    public void onClose(DoorInstance var1);
}

