/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExOlympiadSpelledInfo
extends L2GameServerPacket {
    private int char_obj_id = 0;
    private ArrayList<Effect> ahb = new ArrayList();

    public void addEffect(int n, int n2, int n3) {
        this.ahb.add(new Effect(n, n2, n3));
    }

    public void addSpellRecivedPlayer(Player player) {
        if (player != null) {
            this.char_obj_id = player.getObjectId();
        }
    }

    @Override
    protected final void writeImpl() {
        if (this.char_obj_id == 0) {
            return;
        }
        this.writeEx(42);
        this.writeD(this.char_obj_id);
        this.writeD(this.ahb.size());
        for (Effect effect : this.ahb) {
            this.writeD(effect.skillId);
            this.writeH(effect.level);
            this.writeD(effect.duration);
        }
    }

    class Effect {
        int skillId;
        int level;
        int duration;

        public Effect(int n, int n2, int n3) {
            this.skillId = n;
            this.level = n2;
            this.duration = n3;
        }
    }
}

