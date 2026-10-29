/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity;

import java.lang.reflect.Constructor;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonsterRace {
    private static final Logger _log = LoggerFactory.getLogger(MonsterRace.class);
    private NpcInstance[] Px = new NpcInstance[8];
    private static MonsterRace Py;
    private Constructor<?> _constructor;
    private int[][] Pz = new int[8][20];
    private int[] PA = new int[2];
    private int[] PB = new int[2];

    private MonsterRace() {
    }

    public static MonsterRace getInstance() {
        if (Py == null) {
            Py = new MonsterRace();
        }
        return Py;
    }

    public void newRace() {
        int n = 0;
        for (int i = 0; i < 8; ++i) {
            int n2 = 31003;
            n = Rnd.get(24);
            for (int j = i - 1; j >= 0; --j) {
                if (this.Px[j].getTemplate().npcId != n2 + n) continue;
                n = Rnd.get(24);
            }
            try {
                NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n2 + n);
                this._constructor = npcTemplate.getInstanceConstructor();
                int n3 = IdFactory.getInstance().getNextId();
                this.Px[i] = (NpcInstance)this._constructor.newInstance(n3, npcTemplate);
                continue;
            }
            catch (Exception exception) {
                _log.error("", exception);
            }
        }
        this.newSpeeds();
    }

    public void newSpeeds() {
        this.Pz = new int[8][20];
        int n = 0;
        this.PA[1] = 0;
        this.PB[1] = 0;
        for (int i = 0; i < 8; ++i) {
            n = 0;
            for (int j = 0; j < 20; ++j) {
                this.Pz[i][j] = j == 19 ? 100 : Rnd.get(65, 124);
                n += this.Pz[i][j];
            }
            if (n >= this.PA[1]) {
                this.PB[0] = this.PA[0];
                this.PB[1] = this.PA[1];
                this.PA[0] = 8 - i;
                this.PA[1] = n;
                continue;
            }
            if (n < this.PB[1]) continue;
            this.PB[0] = 8 - i;
            this.PB[1] = n;
        }
    }

    public NpcInstance[] getMonsters() {
        return this.Px;
    }

    public int[][] getSpeeds() {
        return this.Pz;
    }

    public int getFirstPlace() {
        return 8 - this.PA[0];
    }

    public int getSecondPlace() {
        return 8 - this.PB[0];
    }
}

