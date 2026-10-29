/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;

public interface OnMagicUseListener
extends CharListener {
    public void onMagicUse(Creature var1, Skill var2, Creature var3, boolean var4);
}

