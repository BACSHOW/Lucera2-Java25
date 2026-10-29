/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import l2.commons.listener.ListenerList;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.listener.GameListener;
import l2.gameserver.listener.game.OnDayNightChangeListener;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ClientSetTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameTimeController {
    private static final Logger _log = LoggerFactory.getLogger(GameTimeController.class);
    private static final long tz = TimeUnit.HOURS.toMinutes(18L);
    private static final GameTimeController tA = new GameTimeController();
    private GameTimeListenerList tB = new GameTimeListenerList();

    private GameTimeController() {
        GameServer.getInstance().addListener(new OnStartListener(){

            @Override
            public void onStart() {
                ThreadPoolManager.getInstance().execute(new CheckSunState());
            }
        });
    }

    public static final GameTimeController getInstance() {
        return tA;
    }

    public void init() {
        _log.info("GameTimeController: initialized. Now {} in game. {} time.", (Object)String.format("%02d:%02d", this.getGameHour(), this.getGameMin()), (Object)StringUtils.capitalize(DayPart.now().toString().toLowerCase()));
    }

    public boolean isNowNight() {
        return DayPart.isNow(DayPart.NIGHT);
    }

    public int getGameTime() {
        return DayPart.bf();
    }

    public int getGameHour() {
        return (int)(TimeUnit.MINUTES.toHours(this.getGameTime()) % TimeUnit.DAYS.toHours(1L));
    }

    public int getGameMin() {
        return (int)((long)this.getGameTime() % TimeUnit.HOURS.toMinutes(1L));
    }

    public GameTimeListenerList getListenerEngine() {
        return this.tB;
    }

    public <T extends GameListener> boolean addListener(T t) {
        return this.tB.add(t);
    }

    public <T extends GameListener> boolean removeListener(T t) {
        return this.tB.remove(t);
    }

    protected class GameTimeListenerList
    extends ListenerList<GameServer> {
        protected GameTimeListenerList() {
        }

        public void onDay() {
            this.forEachListener(OnDayNightChangeListener.class, onDayNightChangeListener -> {
                try {
                    onDayNightChangeListener.onDay();
                }
                catch (Exception exception) {
                    _log.warn("Exception during day change", exception);
                }
            });
        }

        public void onNight() {
            this.forEachListener(OnDayNightChangeListener.class, onDayNightChangeListener -> {
                try {
                    onDayNightChangeListener.onNight();
                }
                catch (Exception exception) {
                    _log.warn("Exception during night change", exception);
                }
            });
        }
    }

    private static class CheckSunState
    extends RunnableImpl {
        private DayPart tC;

        private CheckSunState() {
        }

        private void be() {
            Calendar calendar = Calendar.getInstance();
            long l = (long)calendar.get(14) + TimeUnit.SECONDS.toMillis(calendar.get(13)) + TimeUnit.MINUTES.toMillis(calendar.get(12));
            long l2 = TimeUnit.MINUTES.toMillis(10L) - l % TimeUnit.MINUTES.toMillis(10L);
            ThreadPoolManager.getInstance().schedule(this, l2);
        }

        @Override
        public void runImpl() throws Exception {
            DayPart dayPart = DayPart.now();
            if (this.tC != dayPart) {
                this.tC = dayPart;
                switch (this.tC) {
                    case DAY: {
                        ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                            @Override
                            public void runImpl() throws Exception {
                                GameTimeController.getInstance().getListenerEngine().onDay();
                            }
                        });
                        break;
                    }
                    case NIGHT: {
                        ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                            @Override
                            public void runImpl() throws Exception {
                                GameTimeController.getInstance().getListenerEngine().onNight();
                            }
                        });
                    }
                }
                ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                            try {
                                player.checkDayNightMessages();
                                player.sendPacket((IStaticPacket)ClientSetTime.STATIC);
                            }
                            catch (Exception exception) {}
                        }
                    }
                });
            }
            this.be();
        }
    }

    static enum DayPart {
        DAY,
        NIGHT;


        private static int bf() {
            Calendar calendar = Calendar.getInstance();
            return (int)((tz + (TimeUnit.HOURS.toMinutes(calendar.get(11)) + (long)calendar.get(12)) * 6L) % TimeUnit.DAYS.toMinutes(1L));
        }

        public static DayPart now() {
            if (TimeUnit.MINUTES.toHours(DayPart.bf()) % TimeUnit.DAYS.toHours(1L) < 6L) {
                return NIGHT;
            }
            return DAY;
        }

        public static boolean isNow(DayPart dayPart) {
            return DayPart.now() == dayPart;
        }

        public boolean isItNow() {
            return DayPart.isNow(this);
        }
    }
}

