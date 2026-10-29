/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;

public final class Env {
    public Creature character;
    public Creature target;
    public ItemInstance item;
    public Skill skill;
    public double value;

    public Env() {
    }

    public Env(Creature creature, Creature creature2, Skill skill) {
        this.character = creature;
        this.target = creature2;
        this.skill = skill;
    }
}

