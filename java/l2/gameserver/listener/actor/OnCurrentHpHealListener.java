/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;

/**
 * Disparado quando um personagem recupera HP (cura), informando o actor
 * receptor, o curandeiro (caster), o delta positivo e a skill responsavel
 * (pode ser null em regen passiva / potion).
 *
 * O listener e disparado em {@code Creature.setCurrentHp} sempre que
 * {@code _currentHp} aumenta de fato. S2C packets Fermata QoL (ex.
 * {@code Healing}) se inscrevem via {@code CharListenerList.addGlobal} para
 * alimentar HUDs de cura.
 */
public interface OnCurrentHpHealListener
extends CharListener {
    public void onCurrentHpHeal(Creature var1, double var2, Creature var4, Skill var5);
}
