/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.zone;

import l2.commons.listener.Listener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Zone;

public interface OnZoneEnterLeaveListener
extends Listener<Zone> {
    public void onZoneEnter(Zone var1, Creature var2);

    public void onZoneLeave(Zone var1, Creature var2);
}

