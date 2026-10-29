/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.recorder;

import l2.gameserver.model.actor.recorder.CharStatsChangeRecorder;
import l2.gameserver.model.instances.NpcInstance;

public class NpcStatsChangeRecorder
extends CharStatsChangeRecorder<NpcInstance> {
    public NpcStatsChangeRecorder(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onSendChanges() {
        super.onSendChanges();
        if ((this._changes & 1) == 1) {
            ((NpcInstance)this._activeChar).broadcastCharInfo();
        }
    }
}

