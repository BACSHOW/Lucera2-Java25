/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class Attack
extends L2GameServerPacket {
    public final int _attackerId;
    public final boolean _soulshot;
    private final int Tl;
    private final int _x;
    private final int _y;
    private final int Di;
    private final int adM;
    private final int adN;
    private final int adO;
    private Hit[] adP;

    public Attack(Creature creature, Creature creature2, boolean bl, int n) {
        this._attackerId = creature.getObjectId();
        this._soulshot = bl;
        this.Tl = n;
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
        this.adP = new Hit[0];
    }

    public void addHit(GameObject gameObject, int n, boolean bl, boolean bl2, boolean bl3) {
        int n2 = this.adP.length;
        Hit[] hitArray = new Hit[n2 + 1];
        System.arraycopy(this.adP, 0, hitArray, 0, this.adP.length);
        hitArray[n2] = new Hit(gameObject, n, bl, bl2, bl3);
        this.adP = hitArray;
    }

    public boolean hasHits() {
        return this.adP.length > 0;
    }

    @Override
    protected final void writeImpl() {
        if (this.getClient() != null && this.getClient().getActiveChar() != null && this.getClient().getActiveChar().getObjectId() == this._attackerId && this.adP.length > 0) {
            l2.gameserver.utils.MovementTelemetryLogger.logS2C(this.getClient().getActiveChar(), "Attack", String.format("TargetId=%d damage=%d AttackerLoc=(%d,%d,%d)", this.adP[0]._targetId, this.adP[0]._damage, this._x, this._y, this.Di));
        }
        this.writeC(5);
        this.writeD(this._attackerId);
        this.writeD(this.adP[0]._targetId);
        this.writeD(this.adP[0]._damage);
        this.writeC(this.adP[0]._flags);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
        this.writeH(this.adP.length - 1);
        for (int i = 1; i < this.adP.length; ++i) {
            this.writeD(this.adP[i]._targetId);
            this.writeD(this.adP[i]._damage);
            this.writeC(this.adP[i]._flags);
        }
    }

    private class Hit {
        int _targetId;
        int _damage;
        int _flags;

        Hit(GameObject gameObject, int n, boolean bl, boolean bl2, boolean bl3) {
            this._targetId = gameObject.getObjectId();
            this._damage = n;
            if (Attack.this._soulshot) {
                this._flags |= 0x10 | Attack.this.Tl;
            }
            if (bl2) {
                this._flags |= 0x20;
            }
            if (bl3) {
                this._flags |= 0x40;
            }
            if (bl) {
                this._flags |= 0x80;
            }
        }
    }
}

