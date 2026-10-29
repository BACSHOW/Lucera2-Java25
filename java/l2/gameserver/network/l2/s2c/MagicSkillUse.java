/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class MagicSkillUse
extends L2GameServerPacket {
    private int _targetId;
    private int _skillId;
    private int _skillLevel;
    private int _hitTime;
    private int ajH;
    private int aed;
    private int _x;
    private int _y;
    private int Di;
    private int adM;
    private int adN;
    private int adO;

    public MagicSkillUse(Creature creature, Creature creature2, int n, int n2, int n3, long l) {
        this.aed = creature.getObjectId();
        this._targetId = creature2.getObjectId();
        this._skillId = n;
        this._skillLevel = n2;
        this._hitTime = n3;
        this.ajH = (int)l;
        this._x = creature.getX();
        this._y = creature.getY();
        this.Di = creature.getZ();
        this.adM = creature2.getX();
        this.adN = creature2.getY();
        this.adO = creature2.getZ();
    }

    public MagicSkillUse(Creature creature, Creature creature2, Skill skill, int n, long l) {
        this.aed = creature.getObjectId();
        this._targetId = creature2.getObjectId();
        this._skillId = skill.getDisplayId();
        this._skillLevel = skill.getDisplayLevel();
        this._hitTime = n;
        this.ajH = (int)l;
        int posX = creature.getX();
        int posY = creature.getY();
        int posZ = creature.getZ();
        if (creature.isPlayer() && creature.getPlayer().getLastClientPosition() != null) {
            l2.gameserver.utils.Location cp = creature.getPlayer().getLastClientPosition();
            if (cp.distance(posX, posY) < 100.0) {
                posX = cp.x;
                posY = cp.y;
                posZ = cp.z;
            }
        }
        this._x = posX;
        this._y = posY;
        this.Di = posZ;
        this.adM = creature2.getX();
        this.adN = creature2.getY();
        this.adO = creature2.getZ();
    }

    public MagicSkillUse(Creature creature, int n, int n2, int n3, long l) {
        this.aed = creature.getObjectId();
        this._targetId = creature.getTargetId();
        this._skillId = n;
        this._skillLevel = n2;
        this._hitTime = n3;
        this.ajH = (int)l;
        this._x = creature.getX();
        this._y = creature.getY();
        this.Di = creature.getZ();
        this.adM = creature.getX();
        this.adN = creature.getY();
        this.adO = creature.getZ();
    }

    public MagicSkillUse(Creature creature, Skill skill, int n, long l) {
        this.aed = creature.getObjectId();
        this._targetId = creature.getTargetId();
        this._skillId = skill.getDisplayId();
        this._skillLevel = skill.getDisplayLevel();
        this._hitTime = n;
        this.ajH = (int)l;
        this._x = creature.getX();
        this._y = creature.getY();
        this.Di = creature.getZ();
        this.adM = creature.getX();
        this.adN = creature.getY();
        this.adO = creature.getZ();
    }

    @Override
    protected final void writeImpl() {
        if (this.getClient() != null && this.getClient().getActiveChar() != null && this.getClient().getActiveChar().getObjectId() == this.aed) {
            l2.gameserver.utils.MovementTelemetryLogger.logS2C(this.getClient().getActiveChar(), "MagicSkillUse", String.format("SkillId=%d lvl=%d hitTime=%d targetId=%d CasterLoc=(%d,%d,%d) TargetLoc=(%d,%d,%d)", this._skillId, this._skillLevel, this._hitTime, this._targetId, this._x, this._y, this.Di, this.adM, this.adN, this.adO));
        }
        this.writeC(72);
        this.writeD(this.aed);
        this.writeD(this._targetId);
        this.writeD(this._skillId);
        this.writeD(this._skillLevel);
        this.writeD(this._hitTime);
        this.writeD(this.ajH);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
        this.writeD(0);
        this.writeD(this.adM);
        this.writeD(this.adN);
        this.writeD(this.adO);
    }

    @Override
    public L2GameServerPacket packet(Player player) {
        if (player != null && !player.isInObserverMode()) {
            if (player.getBuffAnimRange() < 0) {
                return null;
            }
            if (player.getBuffAnimRange() == 0) {
                return this.aed == player.getObjectId() ? super.packet(player) : null;
            }
            int n = player.getBuffAnimRange();
            return player.getXYDeltaSq(this._x, this._y) < (long)(n * n) ? super.packet(player) : null;
        }
        return super.packet(player);
    }
}

