package services;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import l2.commons.listener.Listener;
import l2.gameserver.Config;
import l2.gameserver.ConfigReloadHooks;
import l2.gameserver.GameServer;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.listener.actor.OnAttackListener;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Playable;
import l2.gameserver.model.World;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.player.AutoArcherFarmTask;
import l2.gameserver.model.actor.player.AutoHealFarmTask;
import l2.gameserver.model.actor.player.AutoMagicFarmTask;
import l2.gameserver.model.actor.player.AutoPhysicalFarmTask;
import l2.gameserver.model.actor.player.AutoSummonFarmTask;
import l2.gameserver.model.actor.player.BaseFarmTask;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.utils.ServiceLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface SBlack.
 *
 * Counter Attack + Respect Hunt live entirely in this extension.
 * server.jar is not patched or replaced.
 *
 * Config:
 *   config/custom/autofarm_plus.properties (recarrega com //reload_config)
 */
/**
 * AutoFarm Plus: Counter Attack, Respect Hunt, tipos de farm e Keep Location.
 * Adaptado do InterfaceSBlack (GiranForge) para rodar dentro do servidor: o AutoFarmContext
 * cria estas tarefas diretamente (sem reflexao, sem extensao .jar e sem checagem de licenca).
 */
public class AutoFarmPlus
{
    public static final String COUNTER_VAR = "farmCounterAttack";
    public static final String RESPECT_VAR = "farmRespectHunt";

    private static final Logger _log = LoggerFactory.getLogger(AutoFarmPlus.class);

    private static final String[] CONFIG_PATHS = {
            "config/custom/autofarm_plus.properties"
    };

    // false = nao envia pacotes TutorialShowHtml (AUTOFARM_OPTIONS, FARM_TYPE_UI, AUTOFARM_TOAST,
    // SBLACK_FEATURES). Interfaces que nao tratam esses pacotes dao erro critico no cliente.

    private static boolean INTERFACE_SHOW_AUTOFARM = true;
    private static boolean INTERFACE_SHOW_TELEPORT = true;
    private static boolean INTERFACE_SHOW_SIDEBAR = true;
    private static boolean INTERFACE_SHOW_COUNTER_ATTACK = true;
    private static boolean INTERFACE_SHOW_RESPECT_HUNT = true;
    private static boolean INTERFACE_SHOW_AUTO_SKILL = true;

    private static boolean AUTO_FARM_COUNTER_ATTACK_DEFAULT = false;
    private static boolean AUTO_FARM_RESPECT_HUNT_DEFAULT = true;
    private static int AUTO_FARM_COUNTER_ATTACK_RESUME_MS = 5000;
    private static int AUTO_FARM_COUNTER_ATTACK_MAX_DISTANCE = 1500;
    private static int AUTO_FARM_COUNTER_ATTACK_CHASE_DISTANCE = 700;
    private static int AUTO_FARM_RESPECT_CLAIM_TIMEOUT_MS = 5000;
    private static int INTERFACE_FEATURE_SYNC_DELAY_MS = 1000;
    private static int AUTOFARM_WRAPPER_CHECK_MS = 250;

    private static final EnterListener ENTER_LISTENER = new EnterListener();
    private static final CombatListener COMBAT_LISTENER = new CombatListener();

    private static final Map<Integer, Threat> THREATS = new ConcurrentHashMap<>();
    private static final Map<Integer, Claim> CLAIMS = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> COUNTER_ENGAGED = new ConcurrentHashMap<>();
    private static final Map<Integer, Long> COUNTER_SKILL_DELAY_UNTIL = new ConcurrentHashMap<>();
    // V15: remembers that AutoFarm was ON when PvP Counter started. Lucera's
    // Player.startPvPFlag() can call AutoFarmContext.stopFarmTask() when
    // AUTO_FARM_STOP_ON_PVP_FLAG=true. Counter must survive that core stop and
    // restore the native farm automatically, without touching server.jar.
    private static final Map<Integer, Boolean> COUNTER_AUTO_RESUME = new ConcurrentHashMap<>();
    private static final Map<Integer, Long> COUNTER_RESTART_AFTER = new ConcurrentHashMap<>();
    // Counter combat is driven by the EXT watcher, not by the AutoFarm future.
    // Throttle actions to the native farm cadence instead of firing every 250 ms.
    private static final Map<Integer, Long> COUNTER_NEXT_ACTION_AT = new ConcurrentHashMap<>();
    // Short guard after Counter release. A stale/repeated OnAttack callback from
    // the old PvP AI cannot instantly lock the farmer again. Real HP damage
    // always bypasses this guard and can re-arm Counter immediately.
    private static final Map<Integer, Long> COUNTER_ONATTACK_BLOCK_UNTIL = new ConcurrentHashMap<>();
    // Debounce for the AutoFarm status overlay. Keeping it separate avoids flooding
    // the client screen-message queue when buttons are clicked in quick succession.
    private static final Map<Integer, ScheduledFuture<?>> STATUS_SCREEN_FUTURES = new ConcurrentHashMap<>();
    private static volatile ScheduledFuture<?> WRAPPER_WATCH_FUTURE;
    private static volatile boolean STARTED = false;

    @SuppressWarnings({"rawtypes", "unchecked"})
    /** Bloco "=== AUTOFARM ===" do console (boot e //reload_config). */
    public static void logSection()
    {
        ServiceLog.section("AUTOFARM")
                .add("Janela AutoFarm: " + ServiceLog.onOff(INTERFACE_SHOW_AUTOFARM) + " | Teleport: " + ServiceLog.onOff(INTERFACE_SHOW_TELEPORT)
                        + " | barra lateral: " + ServiceLog.onOff(INTERFACE_SHOW_SIDEBAR))
                .add("Counter Attack: " + ServiceLog.onOff(INTERFACE_SHOW_COUNTER_ATTACK) + " | Respect Hunt: "
                        + ServiceLog.onOff(INTERFACE_SHOW_RESPECT_HUNT) + " | Auto Skill: " + ServiceLog.onOff(INTERFACE_SHOW_AUTO_SKILL))
                .add("Offline Hunting: " + ServiceLog.onOff(OfflineHuntingService.isEnabled()) + " | botão: "
                        + ServiceLog.onOff(OfflineHuntingService.isButtonVisible()))
                .print();
    }

    public static synchronized void start()
    {
        if (STARTED)
            return;

        loadConfig();
        ConfigReloadHooks.register("AutoFarmPlus", () -> { loadConfig(); logSection(); });
        OfflineHuntingService.start(loadRawProps());

        CharListenerList.addGlobal((Listener<Creature>) (Listener<?>) ENTER_LISTENER);
        CharListenerList.addGlobal((Listener<Creature>) (Listener<?>) COMBAT_LISTENER);

        WRAPPER_WATCH_FUTURE = ThreadPoolManager.getInstance().scheduleAtFixedRate(
                AutoFarmPlus::watchAutoFarmTasks,
                100L,
                AUTOFARM_WRAPPER_CHECK_MS);

        STARTED = true;

        logSection();

        ThreadPoolManager.getInstance().schedule(() -> {
            for (Player player : GameObjectsStorage.getAllPlayers())
            {
                if (player != null && player.isOnline() && !player.isInOfflineMode())
                {
                    sendInterfaceFeatures(player);
                    sendAutoFarmOptions(player);

                }
            }
        }, INTERFACE_FEATURE_SYNC_DELAY_MS);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static synchronized void stop()
    {
        if (!STARTED)
            return;

        OfflineHuntingService.stop();

        try
        {
            CharListenerList.removeGlobal((Listener<Creature>) (Listener<?>) ENTER_LISTENER);
        }
        catch (Exception ignored)
        {
        }

        try
        {
            CharListenerList.removeGlobal((Listener<Creature>) (Listener<?>) COMBAT_LISTENER);
        }
        catch (Exception ignored)
        {
        }

        final ScheduledFuture<?> watcher = WRAPPER_WATCH_FUTURE;
        WRAPPER_WATCH_FUTURE = null;
        if (watcher != null)
            watcher.cancel(false);

        THREATS.clear();
        CLAIMS.clear();
        COUNTER_ENGAGED.clear();
        COUNTER_SKILL_DELAY_UNTIL.clear();
        COUNTER_AUTO_RESUME.clear();
        COUNTER_RESTART_AFTER.clear();
        COUNTER_NEXT_ACTION_AT.clear();
        COUNTER_ONATTACK_BLOCK_UNTIL.clear();
        for (ScheduledFuture<?> future : STATUS_SCREEN_FUTURES.values())
        {
            if (future != null)
                future.cancel(false);
        }
        STATUS_SCREEN_FUTURES.clear();

        STARTED = false;
    }

    private static Properties loadRawProps()
    {
        final Properties p = new Properties();
        for (String path : CONFIG_PATHS)
        {
            final File f = new File(path);
            if (f.exists())
            {
                try (FileInputStream fis = new FileInputStream(f))
                {
                    p.load(fis);
                }
                catch (Exception e)
                {
                    _log.warn("AutoFarmPlus: failed to read offline config.", e);
                }
                break;
            }
        }
        return p;
    }

    private static void loadConfig()
    {
        final Properties p = new Properties();
        File selected = null;

        for (String path : CONFIG_PATHS)
        {
            final File f = new File(path);
            if (f.exists())
            {
                selected = f;
                break;
            }
        }

        if (selected != null)
        {
            try (FileInputStream fis = new FileInputStream(selected))
            {
                p.load(fis);
            }
            catch (Exception e)
            {
                _log.warn("AutoFarmPlus: failed to load config, using defaults.", e);
            }
        }
        else
        {
            _log.warn("AutoFarmPlus: config not found, using defaults.");
        }

        INTERFACE_SHOW_AUTOFARM =
                Boolean.parseBoolean(p.getProperty("InterfaceShowAutoFarm", "true"));

        INTERFACE_SHOW_TELEPORT =
                Boolean.parseBoolean(p.getProperty("InterfaceShowTeleport", "true"));

        INTERFACE_SHOW_SIDEBAR =
                Boolean.parseBoolean(p.getProperty("InterfaceShowSideBar", "true"));

        INTERFACE_SHOW_COUNTER_ATTACK =
                Boolean.parseBoolean(p.getProperty("InterfaceShowCounterAttack", "true"));

        INTERFACE_SHOW_RESPECT_HUNT =
                Boolean.parseBoolean(p.getProperty("InterfaceShowRespectHunt", "true"));

        INTERFACE_SHOW_AUTO_SKILL =
                Boolean.parseBoolean(p.getProperty("InterfaceShowAutoSkill", "true"));

        AUTO_FARM_COUNTER_ATTACK_DEFAULT =
                Boolean.parseBoolean(p.getProperty("AutoFarmCounterAttackDefault", "false"));

        AUTO_FARM_RESPECT_HUNT_DEFAULT =
                Boolean.parseBoolean(p.getProperty("AutoFarmRespectHuntDefault", "true"));

        // V11: PvP Counter is temporary. Five seconds after the last PvP
        // action, AutoFarm is released back to normal monster hunting.
        // This uses a new key so an old V10 TimeoutSeconds=10 does not keep
        // the player stuck in PvP longer than requested.
        AUTO_FARM_COUNTER_ATTACK_RESUME_MS = 1000 * Math.max(1,
                parseInt(p.getProperty("AutoFarmCounterAttackResumeSeconds", "5"), 5));

        AUTO_FARM_COUNTER_ATTACK_MAX_DISTANCE = Math.max(200,
                parseInt(p.getProperty("AutoFarmCounterAttackMaxDistance", "1500"), 1500));

        // V14: Counter is retaliation, not a player-hunting mode.  The player
        // may move a little to answer the hit, but it is never allowed to drag
        // AutoFarm far away from the point where the aggression started.
        AUTO_FARM_COUNTER_ATTACK_CHASE_DISTANCE = Math.max(100, Math.min(
                AUTO_FARM_COUNTER_ATTACK_MAX_DISTANCE,
                parseInt(p.getProperty("AutoFarmCounterAttackChaseDistance", "700"), 700)));

        AUTO_FARM_RESPECT_CLAIM_TIMEOUT_MS = 1000 * Math.max(1,
                parseInt(p.getProperty("AutoFarmRespectClaimTimeoutSeconds", "5"), 5));

        INTERFACE_FEATURE_SYNC_DELAY_MS =
                Math.max(250, parseInt(p.getProperty("InterfaceFeatureSyncDelayMs", "1000"), 1000));

        AUTOFARM_WRAPPER_CHECK_MS =
                Math.max(100, parseInt(p.getProperty("AutoFarmWrapperCheckMs", "250"), 250));
    }

    private static final class EnterListener implements OnPlayerEnterListener
    {
        @Override
        public void onPlayerEnter(final Player player)
        {
            if (player == null)
                return;

            OfflineHuntingService.onPlayerEnter(player);

            ThreadPoolManager.getInstance().schedule(() -> {
                if (player == null || !player.isOnline() || player.isInOfflineMode())
                    return;

                sendInterfaceFeatures(player);
                sendAutoFarmOptions(player);

            }, INTERFACE_FEATURE_SYNC_DELAY_MS);
        }
    }

    /**
     * One listener handles both parts:
     *
     * 1) A real Player/Pet/Summon attack against an NPC creates/refreshes the
     *    Respect Hunt claim. Proximity alone NEVER creates a claim.
     *
     * 2) Real damage against an autofarming Player creates the Counter Attack
     *    threat. Only player-owned attackers are considered.
     */
    private static final class CombatListener implements OnAttackListener, OnCurrentHpDamageListener
    {
        @Override
        public void onAttack(Creature actor, Creature target)
        {
            if (actor == null || target == null)
                return;

            final Player actorOwner = actor.getPlayer();
            if (actorOwner == null)
                return;

            // Respect Hunt: only a real attack against this exact NPC claims it.
            if (target instanceof NpcInstance)
                claimNpc((NpcInstance) target, actorOwner);

            // Counter Attack: an actual PvP attack starts the temporary counter
            // even before HP damage is reported (miss/evade/blocked attacks too).
            final Player targetOwner = target.getPlayer();
            if (targetOwner != null && targetOwner != actorOwner)
            {
                registerCounterThreat(targetOwner, actor, false);
            }
        }

        @Override
        public void onCurrentHpDamage(Creature victim, double damage, Creature attacker, l2.gameserver.model.Skill skill)
        {
            if (victim == null || attacker == null || damage <= 0)
                return;

            // Magic/ranged/other damage path for Respect Hunt.
            if (victim instanceof NpcInstance)
            {
                final Player owner = attacker.getPlayer();
                if (owner != null)
                    claimNpc((NpcInstance) victim, owner);
            }

            final Player victimOwner = victim.getPlayer();
            final Player attackerOwner = attacker.getPlayer();
            if (victimOwner == null || attackerOwner == null || victimOwner == attackerOwner)
                return;

            // Only INCOMING hostility refreshes Counter Attack. The counter
            // player's own retaliation must never extend the PvP lock.
            registerCounterThreat(victimOwner, attacker, true);
        }
    }

    /**
     * Registers incoming hostility for Counter Attack.
     *
     * refreshExisting=false is used by OnAttackListener.  It starts Counter
     * immediately (including miss/evade), but repeated AI attack attempts do
     * NOT keep the victim locked forever.
     *
     * refreshExisting=true is used only by real HP damage.  A real new hit is
     * allowed to refresh the return timer.
     */
    private static void registerCounterThreat(Player victim, Creature attacker, boolean refreshExisting)
    {
        if (victim == null || attacker == null)
            return;

        final Player attackerPlayer = attacker.getPlayer();
        final AutoFarmContext context = victim.getFarmSystem();
        final int victimId = victim.getObjectId();
        final boolean farmWasRunning = context != null && context.isAutofarming();
        final boolean counterAlreadyOwnsResume = COUNTER_AUTO_RESUME.containsKey(victimId);

        // First aggression is accepted only while native AutoFarm is really ON.
        // After the first retaliation Lucera may stop that native future because
        // the farmer gained PvP flag; incoming hits must still refresh the same
        // Counter session so the EXT can release/restart it correctly.
        if (context == null
                || (!farmWasRunning && !counterAlreadyOwnsResume)
                || !isCounterAttackEnabled(victim)
                || attackerPlayer == null
                || attackerPlayer == victim
                || isFriendly(victim, attackerPlayer))
            return;

        if (farmWasRunning)
            COUNTER_AUTO_RESUME.put(victimId, Boolean.TRUE);

        final long now = System.currentTimeMillis();
        final long expiresAt = now + AUTO_FARM_COUNTER_ATTACK_RESUME_MS;
        final int attackerId = attacker.getObjectId();

        // A noisy OnAttack callback from the just-released target must not
        // immediately capture AutoFarm again. Real incoming damage bypasses
        // this small guard and re-arms Counter at once.
        if (!refreshExisting)
        {
            final Long blockedUntil = COUNTER_ONATTACK_BLOCK_UNTIL.get(victim.getObjectId());
            if (blockedUntil != null && blockedUntil > now)
                return;
        }

        THREATS.compute(victim.getObjectId(), (id, old) -> {
            // New/expired/different aggressor: start a fresh retaliation leash
            // from the victim's CURRENT farm position.
            if (old == null
                    || old.attacker == null
                    || old.expiresAt < now
                    || old.attacker.getObjectId() != attackerId)
            {
                return new Threat(attacker, expiresAt, victim.getX(), victim.getY(), victim.getZ());
            }

            // Repeated OnAttack events from the same AI do not extend Counter.
            // Only real incoming HP damage may refresh it.
            if (!refreshExisting)
                return old;

            return new Threat(attacker, expiresAt, old.originX, old.originY, old.originZ);
        });
    }

    private static void claimNpc(NpcInstance npc, Player attacker)
    {
        if (npc == null
                || attacker == null
                || npc.isDead()
                || !npc.isVisible()
                || !npc.isMonster()
                || npc.isRaid())
            return;

        final long now = System.currentTimeMillis();
        final long expires = now + AUTO_FARM_RESPECT_CLAIM_TIMEOUT_MS;
        final int attackerId = attacker.getObjectId();

        CLAIMS.compute(npc.getObjectId(), (id, old) -> {
            // The first active attacker owns the claim. Another player hitting
            // the same mob does not steal it. The owner refreshes it per hit.
            if (old == null || old.expiresAt < now || old.playerId == attackerId)
                return new Claim(attackerId, expires);
            return old;
        });
    }

    private static void watchAutoFarmTasks()
    {
        final long now = System.currentTimeMillis();

        // Cheap stale cleanup.
        for (Map.Entry<Integer, Claim> entry : CLAIMS.entrySet())
        {
            final Claim claim = entry.getValue();
            if (claim == null || claim.expiresAt < now)
                CLAIMS.remove(entry.getKey(), claim);
        }

        for (Map.Entry<Integer, Threat> entry : THREATS.entrySet())
        {
            final Threat threat = entry.getValue();
            if (threat == null || threat.expiresAt < now)
                THREATS.remove(entry.getKey(), threat);
        }

        for (Map.Entry<Integer, Long> entry : COUNTER_ONATTACK_BLOCK_UNTIL.entrySet())
        {
            final Long until = entry.getValue();
            if (until == null || until < now)
                COUNTER_ONATTACK_BLOCK_UNTIL.remove(entry.getKey(), until);
        }

        for (Player player : GameObjectsStorage.getAllPlayers())
        {
            if (player == null || !player.isOnline() || player.isInOfflineMode())
                continue;

            // V15: Counter has its own pulse. This remains alive even when the
            // core cancels AutoFarm because retaliation raised PvP flag.
            processCounterPulse(player);

            // If Counter/core restarted the native task, re-attach only our
            // Respect wrapper. The wrapper itself no longer owns PvP combat.

        }
    }

    /**
     * V15 Counter pulse, intentionally independent from BaseFarmTask's future.
     *
     * Lucera can cancel AutoFarm inside Player.startPvPFlag() when
     * AUTO_FARM_STOP_ON_PVP_FLAG=true. Previously that also killed the only
     * code capable of releasing the PvP target, so the character kept chasing
     * and AutoFarm stayed off. This pulse keeps running from the EXT watcher,
     * restores native AutoFarm, and owns the short retaliation only.
     */
    private static void processCounterPulse(Player player)
    {
        if (player == null)
            return;

        final int playerId = player.getObjectId();
        final AutoFarmContext context = player.getFarmSystem();
        final boolean hasCounterState = THREATS.containsKey(playerId)
                || COUNTER_ENGAGED.containsKey(playerId)
                || COUNTER_AUTO_RESUME.containsKey(playerId);

        if (!hasCounterState)
            return;

        if (context == null || !isCounterAttackEnabled(player))
        {
            THREATS.remove(playerId);
            finishCounterEngagement(player, null, null);
            return;
        }

        // If the core stopped AutoFarm because our retaliation raised PvP flag,
        // bring the native future back immediately. While a threat is alive the
        // enhanced farm task simply yields, so it cannot attack mobs in parallel.
        keepNativeAutoFarmAlive(player, context, false);

        final long now = System.currentTimeMillis();
        final Threat threat = THREATS.get(playerId);

        // Expired/missing threat: release PvP AI and resume monster farming.
        // V16 cast grace: if the inactivity timeout expires while a Counter spell
        // is ALREADY casting, let that cast finish instead of aborting it. No new
        // Counter action is started; the next pulse after casting ends releases PvP.
        if (threat == null || threat.expiresAt < now)
        {
            if (threat != null && player.isCastingNow())
                return;

            if (threat != null)
                THREATS.remove(playerId, threat);
            finishCounterEngagement(player, null, threat == null ? null : threat.attacker);
            keepNativeAutoFarmAlive(player, context, true);
            return;
        }

        final Long nextAction = COUNTER_NEXT_ACTION_AT.get(playerId);
        if (nextAction != null && nextAction > now)
            return;

        final int farmType = Math.max(0, Math.min(4,
                player.getVarInt("farmType", Config.FARM_TYPE)));
        final long cadence = Math.max(250L, getFarmInterval(player, farmType));
        COUNTER_NEXT_ACTION_AT.put(playerId, now + cadence);

        final boolean handled = handleCounterAttackSafe(context, null);
        if (!handled)
        {
            keepNativeAutoFarmAlive(player, context, true);
        }
        else
        {
            // The offensive action may call Player.startPvPFlag() a little later
            // and cancel the native future. The next watcher pulse restores it.
            keepNativeAutoFarmAlive(player, context, false);
        }
    }

    /**
     * Restores native AutoFarm if Counter was entered while it was ON.
     * This is the EXT-only answer to AUTO_FARM_STOP_ON_PVP_FLAG.
     */
    private static void keepNativeAutoFarmAlive(Player player, AutoFarmContext context, boolean finishingCounter)
    {
        if (player == null || context == null)
            return;

        final int playerId = player.getObjectId();
        if (!COUNTER_AUTO_RESUME.containsKey(playerId))
            return;

        if (!player.isOnline() || player.isInOfflineMode() || player.isDead() || player.isAlikeDead()
                || (!player.isConnected() && !OfflineHuntingService.isOffline(player)))
        {
            COUNTER_AUTO_RESUME.remove(playerId);
            COUNTER_RESTART_AFTER.remove(playerId);
            COUNTER_NEXT_ACTION_AT.remove(playerId);
            return;
        }

        if (!context.isAutofarming())
        {
            final long now = System.currentTimeMillis();
            final Long retryAt = COUNTER_RESTART_AFTER.get(playerId);
            if (retryAt != null && retryAt > now)
                return;

            COUNTER_RESTART_AFTER.put(playerId, now + 1000L);
            try
            {
                KeepLocationSupport.preserveOrigin(context, context::startFarmTask);
            }
            catch (Throwable e)
            {
                _log.warn("AutoFarmPlus: unable to restore AutoFarm after Counter for " + player.getName(), e);
            }
        }

        if (context.isAutofarming())
        {
            COUNTER_RESTART_AFTER.remove(playerId);
            if (finishingCounter)
                COUNTER_AUTO_RESUME.remove(playerId);
        }
    }

    private static boolean hasLiveCounterSession(AutoFarmContext context)
    {
        if (context == null)
            return false;

        final Player player = context.getPlayer();
        if (player == null)
            return false;

        final int playerId = player.getObjectId();
        return THREATS.containsKey(playerId) || COUNTER_ENGAGED.containsKey(playerId);
    }


    /** Chamado pelo AutoFarmContext quando o farm nativo inicia (lembra o ponto de Keep Location). */
    public static void onFarmStart(Player player)
    {
        if (player != null)
        {
            KeepLocationSupport.rememberStart(player);
            sendAutoFarmOptions(player);
        }
    }

    /** Chamado pelo AutoFarmContext quando o farm nativo para. */
    public static void onFarmStop(Player player)
    {
        if (player != null)
            sendAutoFarmOptions(player);
    }

    /**
     * Tarefa de farm usada pelo AutoFarmContext no lugar das tarefas nativas.
     * Com Counter Attack e Respect Hunt desligados na config devolve a tarefa original.
     */
    public static BaseFarmTask createTask(AutoFarmContext context, int farmType)
    {
        if (!STARTED || (!INTERFACE_SHOW_COUNTER_ATTACK && !INTERFACE_SHOW_RESPECT_HUNT))
            return null;
        return createEnhancedTask(context, farmType);
    }

    static BaseFarmTask createEnhancedTask(AutoFarmContext context, int farmType)
    {
        switch (farmType)
        {
            case 0:
                return new EnhancedPhysicalFarmTask(context);
            case 1:
                return new EnhancedArcherFarmTask(context);
            case 2:
                return new EnhancedMagicFarmTask(context);
            case 3:
                return new EnhancedHealFarmTask(context);
            case 4:
                return new EnhancedSummonFarmTask(context);
            default:
                return null;
        }
    }

    static long getFarmInterval(Player player, int farmType)
    {
        int interval = Config.FARM_INTERVAL_TASK;
        if (interval > 0)
            return interval;

        switch (farmType)
        {
            case 0:
            case 1:
                return player.getPAtkSpd() > 1000 ? 500L : 1000L;
            case 2:
                return player.getMAtkSpd() > 1000 ? 500L : 1000L;
            case 3:
            {
                final int speed = player.getPAtkSpd() > player.getMAtkSpd()
                        ? player.getPAtkSpd()
                        : player.getMAtkSpd();
                return speed > 1000 ? 500L : 1000L;
            }
            case 4:
            {
                final int speed = player.getPAtkSpd() > player.getMAtkSpd()
                        ? player.getPAtkSpd()
                        : player.getMAtkSpd();
                return speed > 1000 ? 700L : 1000L;
            }
            default:
                return 1000L;
        }
    }

    /**
     * Counter Attack V14.
     *
     * The PvP target temporarily replaces monster hunting, but the combat style
     * comes from the SAME AutoFarm configuration already selected by the player.
     * Attack skills configured in AutoFarm are preferred, including Mage mode.
     * A first real attack starts Counter immediately, but repeated OnAttack AI
     * callbacks do not refresh it. Only real incoming HP damage refreshes the
     * return timer. Counter is also leashed to the position where aggression
     * started, so it cannot turn AutoFarm into a player chase. When Counter is
     * released, monster farming resumes on the same AutoFarm tick.
     */
    private static boolean handleCounterAttack(AutoFarmContext context, Runnable resetFarmTarget)
    {
        if (context == null)
            return false;

        final Player player = context.getPlayer();
        if (player == null || !isCounterAttackEnabled(player))
        {
            if (player != null)
            {
                THREATS.remove(player.getObjectId());
                finishCounterEngagement(player, resetFarmTarget, null);
            }
            return false;
        }

        final int playerId = player.getObjectId();
        // During Counter the core may set the native farm future to null when
        // PvP flag starts. The EXT watcher still owns this session and must be
        // able to finish it; do not treat that core stop as Counter cancellation.
        if (!context.isAutofarming() && !COUNTER_AUTO_RESUME.containsKey(playerId))
            return false;
        final Threat threat = THREATS.get(playerId);
        if (threat == null)
        {
            finishCounterEngagement(player, resetFarmTarget, null);
            return false;
        }

        final long now = System.currentTimeMillis();
        final Creature attacker = threat.attacker;
        final Player attackerPlayer = attacker == null ? null : attacker.getPlayer();

        // V16: split Counter release into hard-invalid and soft-release cases.
        // Hard-invalid targets (dead/offline/teleported/safe/friendly) are released
        // immediately. Soft limits (timeout/distance/leash) must NEVER abort a cast
        // that already started; let that cast finish, then release on the next pulse.
        final boolean hardInvalidTarget = attacker == null
                || attackerPlayer == null
                || attackerPlayer == player
                || attacker.isDead()
                || attacker.isAlikeDead()
                || attacker.getCurrentHp() <= 0
                || attackerPlayer.isDead()
                || attackerPlayer.isAlikeDead()
                || attackerPlayer.getCurrentHp() <= 0
                || !attacker.isVisible()
                || !attackerPlayer.isOnline()
                || isFriendly(player, attackerPlayer)
                || !attacker.isAutoAttackable(player);

        if (hardInvalidTarget)
        {
            THREATS.remove(playerId, threat);
            finishCounterEngagement(player, resetFarmTarget, attacker);
            return false;
        }

        final boolean softRelease = now > threat.expiresAt
                || player.getDistance(attacker) > AUTO_FARM_COUNTER_ATTACK_MAX_DISTANCE
                || distanceFromCounterOrigin(player, threat) > AUTO_FARM_COUNTER_ATTACK_CHASE_DISTANCE;

        if (softRelease)
        {
            // Important: do not abort a spell already in progress just because the
            // aggressor moved a little farther away. No NEW Counter action starts
            // beyond the soft limit. As soon as the current cast ends, the next
            // pulse releases the player target and monster farming resumes.
            if (player.isCastingNow())
                return true;

            THREATS.remove(playerId, threat);
            finishCounterEngagement(player, resetFarmTarget, attacker);
            return false;
        }

        // First Counter tick (or a different aggressor): release any old NPC
        // commitment so the task can select a fresh monster when PvP is over.
        final Integer previousTargetId = COUNTER_ENGAGED.put(playerId, attacker.getObjectId());
        if (previousTargetId == null || previousTargetId.intValue() != attacker.getObjectId())
        {
            if (resetFarmTarget != null)
                resetFarmTarget.run();
        }

        if (player.getTarget() != attacker)
            player.setTarget(attacker);

        // Never interrupt an already running cast. Counter keeps ownership of the
        // tick until the cast finishes.
        if (player.isCastingNow())
            return true;

        final int farmType = Math.max(0, Math.min(4,
                player.getVarInt("farmType", Config.FARM_TYPE)));

        // Use the player's configured AutoFarm attack-skill list first.
        // Keep the V11 skill behavior: Mage Counter no longer degenerates
        // into melee while attack skills are configured.
        final boolean hasConfiguredAttackSkills = context.getAttackSpells() != null
                && !context.getAttackSpells().isEmpty();
        if (tryCounterConfiguredAttackSkill(context, player, attacker, now))
            return true;

        // Native Mage AutoFarm is spell-driven. If Mage has attack skills configured
        // but none is currently usable (chance/MP/reuse), wait for the next tick
        // rather than punching the opponent with the staff.
        if (farmType == AutoFarmContext.FARM_TYPE_Mage && hasConfiguredAttackSkills)
            return true;

        // Fighter / Archer / Support / Summon keep the native physical fallback.
        player.getAI().Attack(attacker, false, false);
        return true;
    }

    /**
     * Counter must never be able to stop the native farm scheduler. If an
     * unexpected PvP target/state throws inside the extension, release the
     * Counter state and let super.runImpl() execute in the same tick.
     */
    private static boolean handleCounterAttackSafe(AutoFarmContext context, Runnable resetFarmTarget)
    {
        try
        {
            return handleCounterAttack(context, resetFarmTarget);
        }
        catch (Throwable e)
        {
            final Player player = context == null ? null : context.getPlayer();
            if (player != null)
            {
                THREATS.remove(player.getObjectId());
                finishCounterEngagement(player, resetFarmTarget, null);
                _log.warn("AutoFarmPlus: Counter Attack recovered and returned to AutoFarm for " + player.getName(), e);
            }
            return false;
        }
    }

    private static void finishCounterEngagement(Player player, Runnable resetFarmTarget, Creature counterTarget)
    {
        if (player == null)
            return;

        final int playerId = player.getObjectId();
        final Integer engagedTargetId = COUNTER_ENGAGED.remove(playerId);
        COUNTER_SKILL_DELAY_UNTIL.remove(playerId);
        COUNTER_NEXT_ACTION_AT.remove(playerId);

        // Prevent stale OnAttack notifications from the old AI intention from
        // instantly taking control again. Real damage ignores this guard.
        if (engagedTargetId != null || counterTarget != null)
            COUNTER_ONATTACK_BLOCK_UNTIL.put(playerId, System.currentTimeMillis() + 1500L);

        // If there was no bookkeeping, a lingering Playable target can still be
        // the remains of a previous Counter after logout/teleport.  Treat that
        // as Counter-owned while AutoFarm is active so it can never stall farm.
        final GameObject currentTarget = player.getTarget();
        final boolean currentPlayable = currentTarget instanceof Playable && currentTarget != player;
        final boolean hadCounterState = engagedTargetId != null || counterTarget != null || currentPlayable;
        if (!hadCounterState)
        {
            // Even if PvP AI never reached the engaged bookkeeping, the core may
            // already have stopped AutoFarm when PvP flag was raised. Resume the
            // farm session remembered at the first incoming aggression.
            final AutoFarmContext context = player.getFarmSystem();
            if (context != null)
                keepNativeAutoFarmAlive(player, context, true);
            return;
        }

        // HARD RETURN: Counter never owns movement/attack/cast after release.
        // Always stop the old PvP intention, even when the target vanished and
        // Player.getTarget() has already become null. This is important for
        // logout/teleport, where the AI may still be moving toward the old object.
        try
        {
            player.abortAttack(true, false);
        }
        catch (Throwable ignored)
        {
        }

        try
        {
            if (player.isCastingNow())
                player.abortCast(true, false);
        }
        catch (Throwable ignored)
        {
        }

        try
        {
            player.stopMove();
        }
        catch (Throwable ignored)
        {
        }

        try
        {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
        catch (Throwable ignored)
        {
        }

        // AutoFarm is a monster mode. Once Counter ends there must be no Player,
        // Pet or Summon left as the active target. Clearing unconditionally here
        // makes the next native AutoFarm pass acquire a monster immediately.
        try
        {
            player.setTarget(null);
        }
        catch (Throwable ignored)
        {
        }

        if (resetFarmTarget != null)
            resetFarmTarget.run();

        // V15: Player.startPvPFlag() may have cancelled AutoFarm itself. Restore
        // it here instead of assuming that a native scheduled tick still exists.
        final AutoFarmContext context = player.getFarmSystem();
        if (context != null)
            keepNativeAutoFarmAlive(player, context, true);
    }

    private static double distanceFromCounterOrigin(Player player, Threat threat)
    {
        if (player == null || threat == null)
            return 0.0;

        final long dx = (long) player.getX() - threat.originX;
        final long dy = (long) player.getY() - threat.originY;
        return Math.sqrt((double) (dx * dx + dy * dy));
    }

    /**
     * V14 safety net: while native AutoFarm is active, a Playable target is
     * valid only when it is the CURRENT live Counter aggressor. Any other player,
     * pet or summon target is stale for monster farming and is released.
     */
    private static void clearStalePlayableAutoFarmTarget(AutoFarmContext context, Runnable resetFarmTarget)
    {
        if (context == null)
            return;

        final Player player = context.getPlayer();
        if (player == null || !context.isAutofarming())
            return;

        final GameObject currentTarget = player.getTarget();
        if (!(currentTarget instanceof Playable) || currentTarget == player)
            return;

        final long now = System.currentTimeMillis();
        final Threat threat = THREATS.get(player.getObjectId());
        final Creature attacker = threat == null ? null : threat.attacker;
        final boolean liveCounterTarget = threat != null
                && threat.expiresAt >= now
                && attacker != null
                && attacker.getObjectId() == currentTarget.getObjectId();

        if (liveCounterTarget)
            return;

        THREATS.remove(player.getObjectId());
        finishCounterEngagement(player, resetFarmTarget, attacker);
    }

    private static boolean tryCounterConfiguredAttackSkill(
            AutoFarmContext context, Player player, Creature target, long now)
    {
        if (context == null || player == null || target == null)
            return false;

        final List<Integer> configured = context.getAttackSpells();
        if (configured == null || configured.isEmpty())
            return false;

        if (player.isOutOfControl() || player.isCursedWeaponEquipped())
            return false;

        final Long delayUntil = COUNTER_SKILL_DELAY_UNTIL.get(player.getObjectId());
        if (context.isExtraDelaySkill() && delayUntil != null && delayUntil > now)
            return false;

        if (player.getCurrentMpPercents() < context.getAttackPercent())
            return false;

        final int chance = Math.max(0, Math.min(100, context.getAttackChance()));
        if (chance <= 0 || ThreadLocalRandom.current().nextInt(100) >= chance)
            return false;

        final List<Skill> usable = new ArrayList<>();
        for (Integer skillId : configured)
        {
            if (skillId == null)
                continue;

            final Skill skill = player.getKnownSkill(skillId);
            if (skill == null
                    || !skill.isActive()
                    || !skill.isOffensive()
                    || player.isSkillDisabled(skill))
                continue;

            if (!skill.checkCondition(player, target, false, false, false, true))
                continue;

            usable.add(skill);
            if (!context.isRndAttackSkills())
                break;
        }

        if (usable.isEmpty())
            return false;

        final Skill skill = context.isRndAttackSkills()
                ? usable.get(ThreadLocalRandom.current().nextInt(usable.size()))
                : usable.get(0);

        player.setTarget(target);
        player.setGroundSkillLoc(null);
        Creature aimingTarget = skill.getAimingTarget(player, target);
        if (aimingTarget == null)
            aimingTarget = target;

        player.getAI().Cast(skill, aimingTarget, false, false);

        if (context.isExtraDelaySkill())
            COUNTER_SKILL_DELAY_UNTIL.put(
                    player.getObjectId(),
                    now + Math.max(0L, Config.SKILLS_EXTRA_DELAY));

        return true;
    }

    /**
     * Respect Hunt target selection.
     *
     * The previous version temporarily injected another player into the NPC AI
     * target list and then called the native selector. That was not strong
     * enough when the NPC AI already contained this autofarming player too.
     *
     * This version keeps server.jar untouched and filters the candidate list
     * inside our enhanced task before a NEW target is committed. A mob is
     * rejected only when another player (or that player's summon/pet) is
     * actually fighting that exact NPC, or when a recent real damage/attack
     * event created a live claim. Mere proximity never blocks a mob.
     */
    private static boolean selectWithRespect(
            AutoFarmContext context,
            boolean alreadyHasCommittedTarget,
            BooleanSupplier nativeSelector,
            Consumer<NpcInstance> commitTarget)
    {
        final Player player = context == null ? null : context.getPlayer();

        if (player == null
                || alreadyHasCommittedTarget
                || !isRespectHuntEnabled(player))
            return nativeSelector.getAsBoolean();

        // Preserve the core's special party/assist target-selection paths.
        // Respect Hunt only replaces the normal random/nearest mob search.
        if (context.isLeaderAssist() || context.isAssistMonsterAttack())
            return nativeSelector.getAsBoolean();

        if (player.isCastingNow())
            return false;

        final List<NpcInstance> candidates = context.getAroundNpc(
                player,
                npc -> GeoEngine.canSeeTarget(player, npc, false) && !npc.isDead());

        if (candidates == null || candidates.isEmpty())
            return false;

        if (context.isKeepLocation() && context.getKeepLocation() != null)
        {
            final double radius = context.getFarmRadius();
            candidates.removeIf(npc -> npc == null
                    || npc.getDistance(context.getKeepLocation()) > radius);
        }

        // This is the actual Respect Hunt rule. We filter only while searching
        // for a new target, so a mob already committed by this player is never
        // dropped simply because somebody else arrives later.
        candidates.removeIf(npc -> isNpcActivelyClaimedByOther(npc, player));

        if (candidates.isEmpty())
            return false;

        // Mesmo calculo em lote (SimdGeoMath) da selecao nativa do AutoFarm.
        final NpcInstance selected = l2.gameserver.model.actor.player.BaseFarmTask.nearestNpc(player, candidates);

        if (selected == null)
            return false;

        commitTarget.accept(selected);
        player.setTarget(selected);
        player.sendPacket(new MyTargetSelected(
                selected.getObjectId(),
                player.getLevel() - selected.getLevel()));
        player.sendPacket(selected.makeStatusUpdate(new int[] {9, 10}));
        return true;
    }

    /**
     * True only when another hunting character is really contesting this exact
     * NPC. Being close to the NPC is intentionally NOT enough.
     */
    private static boolean isNpcActivelyClaimedByOther(NpcInstance npc, Player observer)
    {
        if (npc == null || observer == null || npc.isDead() || !npc.isVisible())
            return false;

        // Strong signal #1: a real attack/damage event seen by the global
        // listener during the configured claim window.
        if (getOtherClaimant(npc, observer) != null)
            return true;

        // Strong signal #2: currently attacking/casting/in-combat playable with
        // this exact NPC selected. This also covers pets and summons and makes
        // Respect Hunt reliable even on cores where NPC damage listeners are
        // not dispatched for every attack type.
        for (Playable playable : World.getAroundPlayables(npc, 2000, 600))
        {
            if (playable == null || playable == observer || playable.isDead())
                continue;

            final Player owner = playable.getPlayer();
            if (owner == null || owner == observer || !owner.isOnline())
                continue;

            if (observer.getParty() != null && observer.getParty() == owner.getParty())
                continue;

            if (playable.getTarget() != npc)
                continue;

            if (playable.isAttackingNow() || playable.isCastingNow() || playable.isInCombat())
                return true;
        }

        return false;
    }

    private static Player getOtherClaimant(NpcInstance npc, Player observer)
    {
        if (npc == null || observer == null)
            return null;

        final Claim claim = CLAIMS.get(npc.getObjectId());
        if (claim == null)
            return null;

        final long now = System.currentTimeMillis();
        if (claim.expiresAt < now)
        {
            CLAIMS.remove(npc.getObjectId(), claim);
            return null;
        }

        if (claim.playerId == observer.getObjectId())
            return null;

        final Player claimant = GameObjectsStorage.getPlayer(claim.playerId);
        if (claimant == null || !claimant.isOnline())
        {
            CLAIMS.remove(npc.getObjectId(), claim);
            return null;
        }

        // Party members are treated as one hunting group.
        if (observer.getParty() != null && observer.getParty() == claimant.getParty())
            return null;

        return claimant;
    }

    private static boolean isFriendly(Player player, Player other)
    {
        if (player == null || other == null || player == other)
            return true;

        if (player.getParty() != null && player.getParty() == other.getParty())
            return true;

        return player.getClanId() > 0 && player.getClanId() == other.getClanId();
    }

    public static boolean isCounterAttackEnabled(Player player)
    {
        return player != null
                && isCounterAttackFeatureEnabled()
                && player.getVarB(COUNTER_VAR, AUTO_FARM_COUNTER_ATTACK_DEFAULT);
    }

    public static boolean isRespectHuntEnabled(Player player)
    {
        return player != null
                && isRespectHuntFeatureEnabled()
                && player.getVarB(RESPECT_VAR, AUTO_FARM_RESPECT_HUNT_DEFAULT);
    }

    public static void toggleCounterAttack(Player player)
    {
        if (player == null)
            return;

        if (!isCounterAttackFeatureEnabled())
        {
            player.sendMessage("Counter Attack is disabled by server configuration.");
            sendAutoFarmOptions(player);
            return;
        }

        final boolean enabled = !isCounterAttackEnabled(player);
        player.setVar(COUNTER_VAR, enabled ? 1 : 0, -1L);

        if (!enabled)
        {
            THREATS.remove(player.getObjectId());
            finishCounterEngagement(player, null, null);
            COUNTER_ENGAGED.remove(player.getObjectId());
            COUNTER_SKILL_DELAY_UNTIL.remove(player.getObjectId());
            COUNTER_NEXT_ACTION_AT.remove(player.getObjectId());
            COUNTER_RESTART_AFTER.remove(player.getObjectId());
            COUNTER_ONATTACK_BLOCK_UNTIL.remove(player.getObjectId());
        }

        player.sendMessage("Counter Attack: " + (enabled ? "ON" : "OFF"));
        sendAutoFarmModeToast(player, "Counter Attack: " + (enabled ? "ON" : "OFF"));
        sendAutoFarmOptions(player);

    }

    public static void toggleRespectHunt(Player player)
    {
        if (player == null)
            return;

        if (!isRespectHuntFeatureEnabled())
        {
            player.sendMessage("Respect Hunt is disabled by server configuration.");
            sendAutoFarmOptions(player);
            return;
        }

        final boolean enabled = !isRespectHuntEnabled(player);
        player.setVar(RESPECT_VAR, enabled ? 1 : 0, -1L);
        player.sendMessage("Respect Hunt: " + (enabled ? "ON" : "OFF"));
        sendAutoFarmModeToast(player, "Respect Hunt: " + (enabled ? "ON" : "OFF"));
        sendAutoFarmOptions(player);

    }

    public static void toggleKeepLocation(Player player)
    {
        if (player == null)
            return;
        if (!isAutoFarmEnabled() || !Config.ALLOW_AUTO_FARM || player.getFarmSystem() == null)
        {
            sendAutoFarmOptions(player);
            return;
        }

        final boolean enabled = KeepLocationSupport.toggle(player);
        player.sendMessage("Keep Location: " + (enabled ? "ON" : "OFF"));
        sendAutoFarmModeToast(player, "Keep Location: " + (enabled ? "ON" : "OFF"));
        sendAutoFarmOptions(player);
    }

    /**
     * AutoFarm status screen intentionally disabled.
     *
     * This Interlude client shares/queues ExShowScreenMessage with Attendance, loot
     * and other notices. Any extra AutoFarm screen packet can reposition or flicker
     * those existing messages. The round buttons already show the feature state,
     * while Counter/Respect keep their normal chat confirmation.
     */
    public static void showAutoFarmStatusScreen(final Player player)
    {
        // Intentionally no-op. Do not send ExShowScreenMessage here.
    }

    private static String getFarmTypeName(int mode)
    {
        switch (mode)
        {
            case 0: return "Fighter";
            case 1: return "Archer";
            case 2: return "Magic";
            case 3: return "Heal";
            case 4: return "Summon";
            default: return "Unknown";
        }
    }

    /**
     * Synchronizes the round Farm Type button without using a voiced command.
     * The value is transported directly to Interface.u through TutorialShowHtml.
     */
    public static void syncFarmType(Player player)
    {
        if (player == null || !isAutoFarmEnabled())
            return;

        final AutoFarmContext farmSystem = player.getFarmSystem();
        if (farmSystem == null)
            return;

        final int current = Math.max(0, Math.min(4, player.getVarInt("farmType", Config.FARM_TYPE)));
        sendFarmTypeUi(player, current);
    }

    /**
     * Advances the native AutoFarm type directly from an Interface bypass.
     * No chat command / voiced-command handler is involved, so opening or
     * clicking AutoHunting cannot trigger the server chat-command flood guard.
     */
    public static void nextFarmType(Player player)
    {
        if (player == null || !isAutoFarmEnabled())
            return;

        final AutoFarmContext farmSystem = player.getFarmSystem();
        if (farmSystem == null)
            return;

        final int requested;
        synchronized (farmSystem)
        {
            final int current = Math.max(0, Math.min(4, player.getVarInt("farmType", Config.FARM_TYPE)));
            requested = (current + 1) % 5;

            // Keep the exact native AutoFarm state/variable used by the core.
            player.setVar("farmType", requested, -1L);
            KeepLocationSupport.preserveOrigin(farmSystem, () -> farmSystem.setFarmTypeValue(requested));
        }

        // The native setter can replace the running task when farm is active.
        // Re-apply our EXT-only wrapper immediately so Counter/Respect remain live.

        sendFarmTypeUi(player, requested);
        sendAutoFarmModeToast(player, "Farm Type: " + getFarmTypeName(requested));
    }

    private static void sendFarmTypeUi(Player player, int mode)
    {
        if (player == null)
            return;

        player.sendPacket(new TutorialShowHtml("FARM_TYPE_UI:" + Math.max(0, Math.min(4, mode))));
    }

    /**
     * Sends a client-side AutoFarm notification request.
     * TutorialShowHtml is used only as a private transport to Interface.u;
     * the client renders the message through OnScreenMessageWnd window type 2.
     * This avoids ExShowScreenMessage and does not touch the Attendance window (type 9).
     */
    private static void sendAutoFarmModeToast(Player player, String text)
    {
        if (player == null || text == null || text.isEmpty())
            return;

        // Keep the transport delimiter safe even if a future label contains '|'.
        final String safeText = text.replace('|', '/');
        player.sendPacket(new TutorialShowHtml("AUTOFARM_TOAST|TEXT=" + safeText + "|"));
    }

    public static void sendAutoFarmOptions(Player player)
    {
        if (player == null)
            return;

        final String payload =
                "AUTOFARM_OPTIONS|" +
                        "COUNTER=" + (isCounterAttackEnabled(player) ? "1" : "0") + "|" +
                        "RESPECT=" + (isRespectHuntEnabled(player) ? "1" : "0") + "|" +
                        "KEEP_LOCATION=" + (player.getFarmSystem() != null && player.getFarmSystem().isKeepLocation() ? "1" : "0") + "|" +
                        "COUNTER_VISIBLE=" + (isCounterAttackFeatureEnabled() ? "1" : "0") + "|" +
                        "RESPECT_VISIBLE=" + (isRespectHuntFeatureEnabled() ? "1" : "0") + "|" +
                        "FARM_ACTIVE=" + (player.getFarmSystem() != null && player.getFarmSystem().isAutofarming() ? "1" : "0") + "|" +
                        "OFFLINE_VISIBLE=" + (OfflineHuntingService.isButtonVisible() ? "1" : "0") + "|" +
                        "OFFLINE_ACTIVE=" + (OfflineHuntingService.isOffline(player) ? "1" : "0") + "|";

        player.sendPacket(new TutorialShowHtml(payload));
    }

    private static void sendInterfaceFeatures(Player player)
    {
        if (player == null)
            return;

        final String payload =
                "SBLACK_FEATURES|" +
                        "AUTOFARM=" + (INTERFACE_SHOW_AUTOFARM ? "1" : "0") + "|" +
                        "TELEPORT=" + (INTERFACE_SHOW_TELEPORT ? "1" : "0") + "|" +
                        "SIDEBAR=" + (INTERFACE_SHOW_SIDEBAR ? "1" : "0") + "|" +
                        "COUNTER_BUTTON=" + (INTERFACE_SHOW_COUNTER_ATTACK ? "1" : "0") + "|" +
                        "RESPECT_BUTTON=" + (INTERFACE_SHOW_RESPECT_HUNT ? "1" : "0") + "|" +
                        "AUTOSKILL_BUTTON=" + (INTERFACE_SHOW_AUTO_SKILL ? "1" : "0") + "|";

        player.sendPacket(new TutorialShowHtml(payload));
    }


    public static boolean isAutoFarmEnabled()
    {
        return INTERFACE_SHOW_AUTOFARM;
    }

    /** Ends Counter bookkeeping together with an offline session. */
    static void clearOfflineCounterState(Player player)
    {
        int id = player.getObjectId();
        THREATS.remove(id);
        COUNTER_ENGAGED.remove(id);
        COUNTER_AUTO_RESUME.remove(id);
        COUNTER_RESTART_AFTER.remove(id);
        COUNTER_NEXT_ACTION_AT.remove(id);
        COUNTER_SKILL_DELAY_UNTIL.remove(id);
        COUNTER_ONATTACK_BLOCK_UNTIL.remove(id);
    }

    static boolean ownsCounterResume(Player player)
    {
        return isCounterAttackEnabled(player) && COUNTER_AUTO_RESUME.containsKey(player.getObjectId());
    }

    public static boolean isTeleportEnabled()
    {
        return INTERFACE_SHOW_TELEPORT;
    }

    public static boolean isAutoSkillEnabled()
    {
        return INTERFACE_SHOW_AUTOFARM && INTERFACE_SHOW_AUTO_SKILL;
    }

    public static boolean isSideBarEnabled()
    {
        return INTERFACE_SHOW_SIDEBAR;
    }

    public static boolean isCounterAttackFeatureEnabled()
    {
        return INTERFACE_SHOW_AUTOFARM && INTERFACE_SHOW_COUNTER_ATTACK;
    }

    public static boolean isRespectHuntFeatureEnabled()
    {
        return INTERFACE_SHOW_AUTOFARM && INTERFACE_SHOW_RESPECT_HUNT;
    }

    private static int parseInt(String value, int def)
    {
        try
        {
            return Integer.parseInt(value.trim());
        }
        catch (Exception ignored)
        {
            return def;
        }
    }

    private static final class Threat
    {
        private final Creature attacker;
        private final long expiresAt;
        private final int originX;
        private final int originY;
        private final int originZ;

        private Threat(Creature attacker, long expiresAt, int originX, int originY, int originZ)
        {
            this.attacker = attacker;
            this.expiresAt = expiresAt;
            this.originX = originX;
            this.originY = originY;
            this.originZ = originZ;
        }
    }

    private static final class Claim
    {
        private final int playerId;
        private final long expiresAt;

        private Claim(int playerId, long expiresAt)
        {
            this.playerId = playerId;
            this.expiresAt = expiresAt;
        }
    }

    // ---------------------------------------------------------------------
    // Native AutoFarm subclasses.
    //
    // The server core remains untouched. BaseFarmTask.run() still performs
    // all original safety checks. V15 keeps Counter combat in the independent
    // EXT watcher; these tasks only yield during Counter and customize
    // selectRandomTarget() for Respect Hunt.
    // ---------------------------------------------------------------------

    private static final class EnhancedPhysicalFarmTask extends AutoPhysicalFarmTask
    {
        private final AutoFarmContext context;

        private EnhancedPhysicalFarmTask(AutoFarmContext context)
        {
            super(context);
            this.context = context;
        }

        @Override
        public void runImpl() throws Exception
        {
            // V15: PvP retaliation is owned by the independent EXT watcher.
            // The native AutoFarm future stays alive but yields while Counter
            // is active, so PvP flag cannot strand the only cleanup code.
            if (hasLiveCounterSession(context))
                return;

            clearStalePlayableAutoFarmTarget(context, this::resetCounterTarget);
            super.runImpl();
        }

        @Override
        protected boolean selectRandomTarget()
        {
            return selectWithRespect(context, getCommittedTarget() != null, this::nativeSelect, this::commitRespectTarget);
        }

        private void resetCounterTarget()
        {
            setCommittedTarget(null);
        }

        private boolean nativeSelect()
        {
            return super.selectRandomTarget();
        }

        private void commitRespectTarget(NpcInstance npc)
        {
            setCommittedTarget(npc);
            if (getCommittedSummon() == null && context.getPlayer() != null)
                setCommittedSummon(context.getPlayer().getPet());
        }
    }

    private static final class EnhancedArcherFarmTask extends AutoArcherFarmTask
    {
        private final AutoFarmContext context;

        private EnhancedArcherFarmTask(AutoFarmContext context)
        {
            super(context);
            this.context = context;
        }

        @Override
        public void runImpl() throws Exception
        {
            // V15: PvP retaliation is owned by the independent EXT watcher.
            // The native AutoFarm future stays alive but yields while Counter
            // is active, so PvP flag cannot strand the only cleanup code.
            if (hasLiveCounterSession(context))
                return;

            clearStalePlayableAutoFarmTarget(context, this::resetCounterTarget);
            super.runImpl();
        }

        @Override
        protected boolean selectRandomTarget()
        {
            return selectWithRespect(context, getCommittedTarget() != null, this::nativeSelect, this::commitRespectTarget);
        }

        private void resetCounterTarget()
        {
            setCommittedTarget(null);
        }

        private boolean nativeSelect()
        {
            return super.selectRandomTarget();
        }

        private void commitRespectTarget(NpcInstance npc)
        {
            setCommittedTarget(npc);
            if (getCommittedSummon() == null && context.getPlayer() != null)
                setCommittedSummon(context.getPlayer().getPet());
        }
    }

    private static final class EnhancedMagicFarmTask extends AutoMagicFarmTask
    {
        private final AutoFarmContext context;

        private EnhancedMagicFarmTask(AutoFarmContext context)
        {
            super(context);
            this.context = context;
        }

        @Override
        public void runImpl() throws Exception
        {
            // V15: PvP retaliation is owned by the independent EXT watcher.
            // The native AutoFarm future stays alive but yields while Counter
            // is active, so PvP flag cannot strand the only cleanup code.
            if (hasLiveCounterSession(context))
                return;

            clearStalePlayableAutoFarmTarget(context, this::resetCounterTarget);
            super.runImpl();
        }

        @Override
        protected boolean selectRandomTarget()
        {
            return selectWithRespect(context, getCommittedTarget() != null, this::nativeSelect, this::commitRespectTarget);
        }

        private void resetCounterTarget()
        {
            setCommittedTarget(null);
        }

        private boolean nativeSelect()
        {
            return super.selectRandomTarget();
        }

        private void commitRespectTarget(NpcInstance npc)
        {
            setCommittedTarget(npc);
            if (getCommittedSummon() == null && context.getPlayer() != null)
                setCommittedSummon(context.getPlayer().getPet());
        }
    }

    private static final class EnhancedHealFarmTask extends AutoHealFarmTask
    {
        private final AutoFarmContext context;

        private EnhancedHealFarmTask(AutoFarmContext context)
        {
            super(context);
            this.context = context;
        }

        @Override
        public void runImpl() throws Exception
        {
            // V15: PvP retaliation is owned by the independent EXT watcher.
            // The native AutoFarm future stays alive but yields while Counter
            // is active, so PvP flag cannot strand the only cleanup code.
            if (hasLiveCounterSession(context))
                return;

            clearStalePlayableAutoFarmTarget(context, this::resetCounterTarget);
            super.runImpl();
        }

        @Override
        protected boolean selectRandomTarget()
        {
            return selectWithRespect(context, getCommittedTarget() != null, this::nativeSelect, this::commitRespectTarget);
        }

        private void resetCounterTarget()
        {
            setCommittedTarget(null);
        }

        private boolean nativeSelect()
        {
            return super.selectRandomTarget();
        }

        private void commitRespectTarget(NpcInstance npc)
        {
            setCommittedTarget(npc);
            if (getCommittedSummon() == null && context.getPlayer() != null)
                setCommittedSummon(context.getPlayer().getPet());
        }
    }

    private static final class EnhancedSummonFarmTask extends AutoSummonFarmTask
    {
        private final AutoFarmContext context;

        private EnhancedSummonFarmTask(AutoFarmContext context)
        {
            super(context);
            this.context = context;
        }

        @Override
        public void runImpl() throws Exception
        {
            // V15: PvP retaliation is owned by the independent EXT watcher.
            // The native AutoFarm future stays alive but yields while Counter
            // is active, so PvP flag cannot strand the only cleanup code.
            if (hasLiveCounterSession(context))
                return;

            clearStalePlayableAutoFarmTarget(context, this::resetCounterTarget);
            super.runImpl();
        }

        @Override
        protected boolean selectRandomTarget()
        {
            return selectWithRespect(context, getCommittedTarget() != null, this::nativeSelect, this::commitRespectTarget);
        }

        private void resetCounterTarget()
        {
            setCommittedTarget(null);
        }

        private boolean nativeSelect()
        {
            return super.selectRandomTarget();
        }

        private void commitRespectTarget(NpcInstance npc)
        {
            setCommittedTarget(npc);
            if (getCommittedSummon() == null && context.getPlayer() != null)
                setCommittedSummon(context.getPlayer().getPet());
        }
    }
}