/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Collections;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class MagicSkillLaunched
extends L2GameServerPacket {
    private final int ajD;
    private final int _skillId;
    private final int _skillLevel;
    private final int ajE;
    private final int ajF;
    private final Collection<Creature> ajG;

    public MagicSkillLaunched(Creature creature, int n, int n2, Creature creature2) {
        this.ajD = creature.getObjectId();
        this.ajE = creature.getX();
        this.ajF = creature.getY();
        this._skillId = n;
        this._skillLevel = n2;
        this.ajG = Collections.singletonList(creature2);
    }

    public MagicSkillLaunched(Creature creature, Skill skill, Creature creature2) {
        this.ajD = creature.getObjectId();
        this.ajE = creature.getX();
        this.ajF = creature.getY();
        this._skillId = skill.getDisplayId();
        this._skillLevel = skill.getDisplayLevel();
        this.ajG = Collections.singletonList(creature2);
    }

    public MagicSkillLaunched(Creature creature, int n, int n2, Collection<Creature> collection) {
        this.ajD = creature.getObjectId();
        this.ajE = creature.getX();
        this.ajF = creature.getY();
        this._skillId = n;
        this._skillLevel = n2;
        this.ajG = collection;
    }

    public MagicSkillLaunched(Creature creature, Skill skill, Collection<Creature> collection) {
        this.ajD = creature.getObjectId();
        this.ajE = creature.getX();
        this.ajF = creature.getY();
        this._skillId = skill.getDisplayId();
        this._skillLevel = skill.getDisplayLevel();
        this.ajG = collection;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(118);
        this.writeD(this.ajD);
        this.writeD(this._skillId);
        this.writeD(this._skillLevel);
        this.writeD(this.ajG.size());
        for (Creature creature : this.ajG) {
            if (creature == null) continue;
            this.writeD(creature.getObjectId());
        }
    }

    @Override
    public L2GameServerPacket packet(Player player) {
        if (player != null && !player.isInObserverMode()) {
            if (player.getBuffAnimRange() < 0) {
                return null;
            }
            if (player.getBuffAnimRange() == 0) {
                return this.ajD == player.getObjectId() ? super.packet(player) : null;
            }
            int n = player.getBuffAnimRange();
            return player.getXYDeltaSq(this.ajE, this.ajF) < (long)(n * n) ? super.packet(player) : null;
        }
        return super.packet(player);
    }
}

