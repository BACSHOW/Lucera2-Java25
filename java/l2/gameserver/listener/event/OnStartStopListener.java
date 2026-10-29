/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.event;

import l2.gameserver.listener.EventListener;
import l2.gameserver.model.entity.events.GlobalEvent;

public interface OnStartStopListener
extends EventListener {
    public void onStart(GlobalEvent var1);

    public void onStop(GlobalEvent var1);
}

