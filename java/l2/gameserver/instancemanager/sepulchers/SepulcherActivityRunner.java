/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers;

import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SepulcherActivityRunner {
    private static final SepulcherActivityRunner BI = new SepulcherActivityRunner();
    private static final Logger _log = LoggerFactory.getLogger(SepulcherActivityRunner.class);
    private ScheduledFuture BJ;

    public static SepulcherActivityRunner getInstance() {
        return BI;
    }

    public void start() {
        if (this.BJ != null && !this.BJ.isDone()) {
            throw new IllegalStateException();
        }
        this.b(State.REGISTRATION_OPEN);
    }

    private void a(State state) {
        switch (state) {
            case REGISTRATION_OPEN: {
                FourSepulchers.getInstance().openRegistration();
                this.b(State.ACTIVE);
                break;
            }
            case ACTIVE: {
                FourSepulchers.getInstance().activate();
                this.b(State.COLLAPSE);
                break;
            }
            case COLLAPSE: {
                FourSepulchers.getInstance().collapse();
                this.b(State.MOB_DESPAWN);
                break;
            }
            case MOB_DESPAWN: {
                FourSepulchers.getInstance().deactivate();
                this.b(State.REGISTRATION_OPEN);
            }
        }
    }

    private void b(final State state) {
        long l = System.currentTimeMillis();
        this.BJ = ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                SepulcherActivityRunner.this.a(state);
            }
        }, state.getCron().next(l) - l);
    }

    private static enum State {
        REGISTRATION_OPEN("55 * * * *"),
        ACTIVE("0 * * * *"),
        COLLAPSE("50 * * * *"),
        MOB_DESPAWN("54 * * * *");

        private final SchedulingPattern BK;

        private State(String string2) {
            this.BK = new SchedulingPattern(string2);
        }

        public SchedulingPattern getCron() {
            return this.BK;
        }
    }
}

