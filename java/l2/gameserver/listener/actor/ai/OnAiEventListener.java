/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.ai;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.listener.AiListener;
import l2.gameserver.model.Creature;

public interface OnAiEventListener
extends AiListener {
    public void onAiEvent(Creature var1, CtrlEvent var2, Object[] var3);
}

