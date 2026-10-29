/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.function.LongSupplier;
import l2.commons.collections.MultiValueSet;
import l2.commons.threading.RunnableImpl;
import l2.commons.time.cron.SchedulingPattern;
import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.Zone;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronZoneSwitcher
extends Functions
implements ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(CronZoneSwitcher.class);
    private static final String bZN = "cronZoneEnablePattern";
    private static final String bZO = "cronZoneEnableBroadcastSpawnEvent";
    private static final String bZP = "cronZoneEnableBroadcastDespawnEvent";
    private static final String bZQ = "cronZoneEnableAnnounceCustomMessage";
    private static final String bZR = "cronZoneDisablePattern";
    private static final String bZS = "cronZoneDisableBroadcastSpawnEvent";
    private static final String bZT = "cronZoneDisableBroadcastDespawnEvent";
    private static final String bZU = "cronZoneDisableAnnounceCustomMessage";
    private static final List<Mutable<ScheduledFuture<?>>> bZV = new CopyOnWriteArrayList();

    private static Mutable<ScheduledFuture<?>> a(final Runnable runnable, String string) {
        final MutableObject mutableObject = new MutableObject();
        final LongSupplier longSupplier = () -> {
            long l = System.currentTimeMillis();
            long l2 = new SchedulingPattern(string).next(l) - l;
            return l2;
        };
        mutableObject.setValue(ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                try {
                    runnable.run();
                }
                catch (Throwable throwable) {
                    sQ.error(throwable.getMessage(), throwable);
                }
                finally {
                    mutableObject.setValue(ThreadPoolManager.getInstance().schedule(this, longSupplier.getAsLong()));
                }
            }
        }, longSupplier.getAsLong()));
        return mutableObject;
    }

    @Override
    public void onLoad() {
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        int n = 0;
        int n2 = 0;
        for (Zone zone : collection) {
            MultiValueSet<String> multiValueSet = zone.getParams();
            if (multiValueSet.isSet(bZN)) {
                bZV.add(CronZoneSwitcher.a(((CronZoneEnableTask)((CronZoneEnableTask)new CronZoneEnableTask(zone).setBroadcastSpawnEvent(multiValueSet.getString(bZO, ""))).setBroadcastDespawnEvent(multiValueSet.getString(bZP, ""))).setBroadcastAnnounceCustomMessage(multiValueSet.getString(bZQ, "")), multiValueSet.getString(bZN)));
                ++n;
            }
            if (!multiValueSet.isSet(bZR)) continue;
            bZV.add(CronZoneSwitcher.a(((CronZoneDisableTask)((CronZoneDisableTask)new CronZoneDisableTask(zone).setBroadcastSpawnEvent(multiValueSet.getString(bZS, ""))).setBroadcastDespawnEvent(multiValueSet.getString(bZT, ""))).setBroadcastAnnounceCustomMessage(multiValueSet.getString(bZU, "")), multiValueSet.getString(bZR)));
            ++n2;
        }
        sQ.info("CronZoneSwitcher: Loaded " + n + " cron zone enable pattern(s) and " + n2 + " cron zone disable pattern(s).");
    }

    @Override
    public void onReload() {
        this.onShutdown();
    }

    @Override
    public void onShutdown() {
        for (Mutable<ScheduledFuture<?>> mutable : bZV) {
            ScheduledFuture<?> scheduledFuture;
            if (mutable == null || (scheduledFuture = mutable.getValue()) == null) continue;
            scheduledFuture.cancel(true);
        }
    }

    private static abstract class BaseCronZoneTask<T extends BaseCronZoneTask>
    extends RunnableImpl {
        private final Zone Cb;
        private String bZW;
        private String bZX;
        private String bZY;

        private BaseCronZoneTask(Zone zone) {
            this.Cb = zone;
        }

        protected Zone getZone() {
            return this.Cb;
        }

        public String getBroadcastSpawnEvent() {
            return this.bZW;
        }

        public T setBroadcastSpawnEvent(String string) {
            this.bZW = string;
            return (T)this;
        }

        public String getBroadcastDespawnEvent() {
            return this.bZX;
        }

        public T setBroadcastDespawnEvent(String string) {
            this.bZX = string;
            return (T)this;
        }

        public String getBroadcastAnnounceCustomMessage() {
            return this.bZY;
        }

        public T setBroadcastAnnounceCustomMessage(String string) {
            this.bZY = string;
            return (T)this;
        }

        protected abstract boolean doIt();

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() throws Exception {
            Zone zone = this.Cb;
            synchronized (zone) {
                if (!this.doIt()) {
                    return;
                }
                if (StringUtils.isNotBlank(this.getBroadcastSpawnEvent())) {
                    SpawnManager.getInstance().spawn(this.getBroadcastSpawnEvent());
                }
                if (StringUtils.isNotBlank(this.getBroadcastDespawnEvent())) {
                    SpawnManager.getInstance().despawn(this.getBroadcastDespawnEvent());
                }
                if (StringUtils.isNotBlank(this.getBroadcastAnnounceCustomMessage())) {
                    Announcements.getInstance().announceByCustomMessage(this.getBroadcastAnnounceCustomMessage(), ArrayUtils.EMPTY_STRING_ARRAY);
                }
            }
        }
    }

    private static final class CronZoneDisableTask
    extends BaseCronZoneTask<CronZoneDisableTask> {
        private CronZoneDisableTask(Zone zone) {
            super(zone);
        }

        @Override
        protected boolean doIt() {
            if (!this.getZone().isActive()) {
                return false;
            }
            this.getZone().setActive(false);
            return true;
        }
    }

    private static final class CronZoneEnableTask
    extends BaseCronZoneTask<CronZoneEnableTask> {
        private CronZoneEnableTask(Zone zone) {
            super(zone);
        }

        @Override
        protected boolean doIt() {
            if (this.getZone().isActive()) {
                return false;
            }
            this.getZone().setActive(true);
            return true;
        }
    }
}

