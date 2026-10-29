/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class DoorTools
extends Functions
implements ScriptFile {
    private static long H(String string) {
        long l = System.currentTimeMillis();
        long l2 = new SchedulingPattern(string).next(l) - l;
        return l2;
    }

    private static Mutable<ScheduledFuture<?>> a(final Runnable runnable, final String string) {
        final MutableObject mutableObject = new MutableObject();
        mutableObject.setValue(ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                try {
                    runnable.run();
                }
                finally {
                    mutableObject.setValue(ThreadPoolManager.getInstance().schedule(this, DoorTools.H(string)));
                }
            }
        }, DoorTools.H(string)));
        return mutableObject;
    }

    @Override
    public void onLoad() {
        if (Config.ENABLE_ON_TIME_DOOR_OPEN) {
            DoorTools.a(new RunnableImpl(){

                @Override
                public void runImpl() throws Exception {
                    DoorInstance doorInstance = ReflectionManager.DEFAULT.getDoor(Config.ON_TIME_OPEN_DOOR_ID);
                    if (doorInstance != null) {
                        doorInstance.openMe(null, true);
                    }
                }
            }, Config.ON_TIME_OPEN_PATTERN);
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

