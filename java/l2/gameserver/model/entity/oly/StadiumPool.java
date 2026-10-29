/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

import java.util.ArrayDeque;
import l2.gameserver.model.entity.oly.Stadium;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StadiumPool {
    private static final Logger _log = LoggerFactory.getLogger(StadiumPool.class);
    private static StadiumPool SE;
    public static final int REFLECTION_COUNT = 22;
    private static final int[] SF;
    private ArrayDeque<Stadium> SG;
    private Stadium[] SH = new Stadium[22];

    public static final StadiumPool getInstance() {
        if (SE == null) {
            SE = new StadiumPool();
        }
        return SE;
    }

    private StadiumPool() {
        this.SG = new ArrayDeque();
    }

    public Stadium[] getAllStadiums() {
        return this.SH;
    }

    public void AllocateStadiums() {
        int n = 0;
        for (int i = 0; i < 22 / SF.length; ++i) {
            for (int j = 0; j < SF.length; ++j) {
                Stadium stadium;
                this.SH[n] = stadium = new Stadium(n, SF[j]);
                this.SG.addLast(stadium);
                ++n;
            }
        }
        _log.info("OlyStadiumPool: allocated " + n + " stadiums.");
    }

    public void FreeStadiums() {
        for (int i = 0; i < this.SH.length; ++i) {
            if (this.SH[i] == null) continue;
            this.SH[i].collapse();
            this.SH[i] = null;
        }
        this.SG.clear();
        _log.info("OlyStadiumPool: stadiums cleared.");
    }

    public boolean isStadiumAvailable() {
        return this.SG.size() > 0;
    }

    public synchronized Stadium pollStadium() {
        Stadium stadium = this.SG.pollFirst();
        if (!stadium.isFree()) {
            _log.warn("Poll used stadium");
            Thread.dumpStack();
            stadium = this.SG.pollFirst();
        }
        stadium.setFree(false);
        return stadium;
    }

    public synchronized void putStadium(Stadium stadium) {
        if (stadium.isFree()) {
            _log.warn("Put free stadium");
            Thread.dumpStack();
        }
        stadium.clear();
        stadium.setFree(true);
        this.SG.addFirst(stadium);
    }

    public Stadium getStadium(int n) {
        return this.SH[n];
    }

    static {
        SF = new int[]{147};
    }
}

