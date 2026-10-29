/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.recorder;

import l2.gameserver.model.Creature;
import l2.gameserver.model.base.TeamType;

public class CharStatsChangeRecorder<T extends Creature> {
    public static final int BROADCAST_CHAR_INFO = 1;
    public static final int SEND_CHAR_INFO = 2;
    public static final int SEND_STATUS_INFO = 4;
    protected final T _activeChar;
    protected int _level;
    protected int _accuracy;
    protected int _attackSpeed;
    protected int _castSpeed;
    protected int _criticalHit;
    protected int _evasion;
    protected int _magicAttack;
    protected int _magicDefence;
    protected int _maxHp;
    protected int _maxMp;
    protected int _physicAttack;
    protected int _physicDefence;
    protected int _runSpeed;
    protected int _abnormalEffects;
    protected int _abnormalEffects2;
    protected int _abnormalEffects3;
    protected TeamType _team;
    protected int _changes;

    public CharStatsChangeRecorder(T t) {
        this._activeChar = t;
    }

    protected int set(int n, int n2, int n3) {
        if (n2 != n3) {
            this._changes |= n;
        }
        return n3;
    }

    protected long set(int n, long l, long l2) {
        if (l != l2) {
            this._changes |= n;
        }
        return l2;
    }

    protected String set(int n, String string, String string2) {
        if (!string.equals(string2)) {
            this._changes |= n;
        }
        return string2;
    }

    protected <E extends Enum<E>> E set(int n, E e, E e2) {
        if (e != e2) {
            this._changes |= n;
        }
        return e2;
    }

    protected void refreshStats() {
        this._accuracy = this.set(2, this._accuracy, ((Creature)this._activeChar).getAccuracy());
        this._attackSpeed = this.set(1, this._attackSpeed, ((Creature)this._activeChar).getPAtkSpd());
        this._castSpeed = this.set(1, this._castSpeed, ((Creature)this._activeChar).getMAtkSpd());
        this._criticalHit = this.set(2, this._criticalHit, ((Creature)this._activeChar).getCriticalHit(null, null));
        this._evasion = this.set(2, this._evasion, ((Creature)this._activeChar).getEvasionRate(null));
        this._runSpeed = this.set(1, this._runSpeed, ((Creature)this._activeChar).getRunSpeed());
        this._physicAttack = this.set(2, this._physicAttack, ((Creature)this._activeChar).getPAtk(null));
        this._physicDefence = this.set(2, this._physicDefence, ((Creature)this._activeChar).getPDef(null));
        this._magicAttack = this.set(2, this._magicAttack, ((Creature)this._activeChar).getMAtk(null, null));
        this._magicDefence = this.set(2, this._magicDefence, ((Creature)this._activeChar).getMDef(null, null));
        this._maxHp = this.set(4, this._maxHp, ((Creature)this._activeChar).getMaxHp());
        this._maxMp = this.set(4, this._maxMp, ((Creature)this._activeChar).getMaxMp());
        this._level = this.set(2, this._level, ((Creature)this._activeChar).getLevel());
        this._abnormalEffects = this.set(1, this._abnormalEffects, ((Creature)this._activeChar).getAbnormalEffect());
        this._abnormalEffects2 = this.set(1, this._abnormalEffects2, ((Creature)this._activeChar).getAbnormalEffect2());
        this._abnormalEffects3 = this.set(1, this._abnormalEffects3, ((Creature)this._activeChar).getAbnormalEffect3());
        this._team = this.set(1, this._team, ((Creature)this._activeChar).getTeam());
    }

    public final void sendChanges() {
        this.refreshStats();
        this.onSendChanges();
        this._changes = 0;
    }

    protected void onSendChanges() {
        if ((this._changes & 4) == 4) {
            ((Creature)this._activeChar).broadcastStatusUpdate();
        }
    }
}

