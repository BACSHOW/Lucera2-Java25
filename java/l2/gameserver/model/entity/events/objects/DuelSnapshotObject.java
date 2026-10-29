/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.stats.Env;
import l2.gameserver.utils.Location;

public class DuelSnapshotObject
implements Serializable {
    private final TeamType _team;
    private final HardReference<Player> e;
    private final int Rh;
    private final List<Effect> Fe;
    private final Location PF;
    private final double _currentHp;
    private final double _currentMp;
    private final double _currentCp;
    private boolean Ri;

    public DuelSnapshotObject(Player player, TeamType teamType) {
        this.e = player.getRef();
        this._team = teamType;
        this.PF = player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc();
        this._currentCp = player.getCurrentCp();
        this._currentHp = player.getCurrentHp();
        this._currentMp = player.getCurrentMp();
        this.Rh = player.getActiveClassId();
        List<Effect> list = player.getEffectList().getAllEffects();
        this.Fe = new ArrayList<Effect>(list.size());
        for (Effect effect : list) {
            Effect effect2 = effect.getTemplate().getEffect(new Env(effect.getEffector(), effect.getEffected(), effect.getSkill()));
            if (!effect2.isSaveable()) continue;
            effect2.setCount(effect.getCount());
            effect2.setPeriod(effect.getCount() == 1 ? effect.getPeriod() - effect.getTime() : effect.getPeriod());
            this.Fe.add(effect2);
        }
    }

    public void restore(boolean bl) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        if (!bl) {
            player.getEffectList().stopAllEffects();
            if (this.Rh == player.getActiveClassId()) {
                for (Effect effect : this.Fe) {
                    if (player.getEffectList().getEffectsBySkill(effect.getSkill()) != null) continue;
                    player.getEffectList().addEffect(effect);
                }
            }
            player.setCurrentCp(this._currentCp);
            player.setCurrentHpMp(this._currentHp, this._currentMp);
        }
    }

    public void teleport() {
        Player player = this.getPlayer();
        player._stablePoint = null;
        if (player.isFrozen()) {
            player.stopFrozen();
        }
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                Player player = DuelSnapshotObject.this.getPlayer();
                if (player == null) {
                    return;
                }
                player.teleToLocation(DuelSnapshotObject.this.PF, ReflectionManager.DEFAULT);
            }
        }, 5000L);
    }

    public Player getPlayer() {
        return this.e.get();
    }

    public boolean isDead() {
        return this.Ri;
    }

    public void setDead() {
        this.Ri = true;
    }

    public Location getLoc() {
        return this.PF;
    }

    public TeamType getTeam() {
        return this._team;
    }
}

