/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Effect;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestDispel
extends L2GameClientPacket {
    private int CS;
    private int _id;
    private int _level;

    @Override
    protected void readImpl() throws Exception {
        this.CS = this.readD();
        this._id = this.readD();
        this._level = this.readD();
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || player.getObjectId() != this.CS && player.getPet() == null) {
            return;
        }
        Playable playable = player;
        if (player.getObjectId() != this.CS) {
            playable = player.getPet();
        }
        for (Effect effect : playable.getEffectList().getAllEffects()) {
            if (effect.getDisplayId() != this._id || effect.getDisplayLevel() != this._level) continue;
            if (!effect.isOffensive() && !effect.getSkill().isMusic() && effect.getSkill().isSelfDispellable() && effect.getSkill().getSkillType() != Skill.SkillType.TRANSFORMATION) {
                effect.exit();
                continue;
            }
            return;
        }
        player.sendPacket((IStaticPacket)new SystemMessage(92).addSkillName(this._id, this._level));
    }
}

