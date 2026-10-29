/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.recorder;

import l2.gameserver.model.Summon;
import l2.gameserver.model.actor.recorder.CharStatsChangeRecorder;

public class SummonStatsChangeRecorder
extends CharStatsChangeRecorder<Summon> {
    public SummonStatsChangeRecorder(Summon summon) {
        super(summon);
    }

    @Override
    protected void onSendChanges() {
        super.onSendChanges();
        if ((this._changes & 2) == 2) {
            ((Summon)this._activeChar).sendPetInfo();
        } else if ((this._changes & 1) == 1) {
            ((Summon)this._activeChar).broadcastCharInfo();
        }
    }
}

