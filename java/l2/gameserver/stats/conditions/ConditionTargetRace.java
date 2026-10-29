/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.stats.conditions;

import l2.gameserver.model.Creature;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.templates.npc.NpcTemplate;

public class ConditionTargetRace
extends Condition {
    private final int CV;

    public ConditionTargetRace(String string) {
        if (string.equalsIgnoreCase("Undead")) {
            this.CV = 1;
        } else if (string.equalsIgnoreCase("MagicCreatures")) {
            this.CV = 2;
        } else if (string.equalsIgnoreCase("Beasts")) {
            this.CV = 3;
        } else if (string.equalsIgnoreCase("Animals")) {
            this.CV = 4;
        } else if (string.equalsIgnoreCase("Plants")) {
            this.CV = 5;
        } else if (string.equalsIgnoreCase("Humanoids")) {
            this.CV = 6;
        } else if (string.equalsIgnoreCase("Spirits")) {
            this.CV = 7;
        } else if (string.equalsIgnoreCase("Angels")) {
            this.CV = 8;
        } else if (string.equalsIgnoreCase("Demons")) {
            this.CV = 9;
        } else if (string.equalsIgnoreCase("Dragons")) {
            this.CV = 10;
        } else if (string.equalsIgnoreCase("Giants")) {
            this.CV = 11;
        } else if (string.equalsIgnoreCase("Bugs")) {
            this.CV = 12;
        } else if (string.equalsIgnoreCase("Fairies")) {
            this.CV = 13;
        } else if (string.equalsIgnoreCase("Humans")) {
            this.CV = 14;
        } else if (string.equalsIgnoreCase("Elves")) {
            this.CV = 15;
        } else if (string.equalsIgnoreCase("DarkElves")) {
            this.CV = 16;
        } else if (string.equalsIgnoreCase("Orcs")) {
            this.CV = 17;
        } else if (string.equalsIgnoreCase("Dwarves")) {
            this.CV = 18;
        } else if (string.equalsIgnoreCase("Others")) {
            this.CV = 19;
        } else if (string.equalsIgnoreCase("NonLivingBeings")) {
            this.CV = 20;
        } else if (string.equalsIgnoreCase("SiegeWeapons")) {
            this.CV = 21;
        } else if (string.equalsIgnoreCase("DefendingArmy")) {
            this.CV = 22;
        } else if (string.equalsIgnoreCase("Mercenaries")) {
            this.CV = 23;
        } else if (string.equalsIgnoreCase("UnknownCreature")) {
            this.CV = 24;
        } else {
            throw new IllegalArgumentException("ConditionTargetRace: Invalid race name: " + string);
        }
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature creature = env.target;
        return creature != null && creature.getTemplate() != null && (creature.isSummon() || creature.isNpc()) && this.CV == ((NpcTemplate)creature.getTemplate()).getRace();
    }
}

