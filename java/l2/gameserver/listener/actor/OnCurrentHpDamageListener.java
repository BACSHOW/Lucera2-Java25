/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;

public interface OnCurrentHpDamageListener
extends CharListener {
    public void onCurrentHpDamage(Creature var1, double var2, Creature var4, Skill var5);
}

