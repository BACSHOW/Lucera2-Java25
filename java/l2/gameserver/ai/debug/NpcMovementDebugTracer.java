package l2.gameserver.ai.debug;

import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * Telemetria de Alta Precisão para Depuração de Movimentação e Ciclo de Vida de IA de Monstros.
 * <p>
 * Grava métricas precisas a cada tick (posições, intenções, distâncias, tempos de geoengine e chamadas de returnHome)
 * sem travar as threads de jogo (escrita assíncrona desacoplada em arquivo dedicado).
 */
public final class NpcMovementDebugTracer {
    private static final Logger LOG = LoggerFactory.getLogger(NpcMovementDebugTracer.class);
    private static final NpcMovementDebugTracer INSTANCE = new NpcMovementDebugTracer();

    public static volatile boolean GLOBAL_TRACE = true;
    private static final Set<Integer> TRACKED_OBJS = ConcurrentHashMap.newKeySet();
    private final BlockingQueue<String> logQueue = new LinkedTransferQueue<>();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private volatile boolean running = true;

    public static NpcMovementDebugTracer getInstance() {
        return INSTANCE;
    }

    private NpcMovementDebugTracer() {
        Thread flusherThread = new Thread(this::runFlusher, "NpcMovementDebugFlusher");
        flusherThread.setDaemon(true);
        flusherThread.setPriority(Thread.MIN_PRIORITY);
        flusherThread.start();
    }

    public static void setTrackNpc(int objectId, boolean enable) {
        if (enable) {
            TRACKED_OBJS.add(objectId);
        } else {
            TRACKED_OBJS.remove(objectId);
        }
    }

    public static boolean isTracking(NpcInstance npc) {
        if (npc == null) return false;
        if (TRACKED_OBJS.contains(npc.getObjectId())) return true;
        if (!GLOBAL_TRACE) return false;
        
        // Rastreia qualquer monstro cujo alvo seja "agaze" ou que esteja perto de um player
        Creature target = npc.getAI() != null ? npc.getAI().getAttackTarget() : null;
        if (target != null && target.isPlayer() && target.getName().equalsIgnoreCase("agaze")) {
            return true;
        }
        if (npc.isMonster() && npc.isInCombat()) {
            return true;
        }
        return false;
    }

    /**
     * Registra um tick detalhado da IA do monstro.
     */
    public void traceTick(NpcInstance npc, String phase, Creature target, String details) {
        if (!isTracking(npc)) {
            return;
        }

        long now = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(256);
        sb.append('[').append(timeFormat.format(new Date(now))).append("] ");
        sb.append("[NPC:").append(npc.getNpcId()).append(":").append(npc.getObjectId()).append(":").append(npc.getName()).append("] ");
        sb.append("[PHASE:").append(phase).append("] ");
        sb.append("[POS:").append(npc.getX()).append(',').append(npc.getY()).append(',').append(npc.getZ()).append("] ");
        sb.append("[INTENTION:").append(npc.hasAI() ? npc.getAI().getIntention() : "NONE").append("] ");
        sb.append("[MOVING:").append(npc.isMoving()).append(":f=").append((npc.getFollowTarget() != null)).append("] ");

        if (target != null) {
            double dist2d = npc.getDistance(target);
            sb.append("[TARGET:").append(target.getName()).append(":dist=").append(String.format("%.1f", dist2d)).append("] ");
        } else {
            sb.append("[TARGET:NULL] ");
        }

        if (details != null && !details.isEmpty()) {
            sb.append("[INFO:").append(details).append(']');
        }

        this.logQueue.offer(sb.toString());
    }

    /**
     * Registra evento cirúrgico (ex: returnHome, perda de alvo, recalculo de rota).
     */
    public void traceEvent(NpcInstance npc, String eventName, String details) {
        if (!isTracking(npc)) {
            return;
        }

        long now = System.currentTimeMillis();
        String line = String.format("[%s] [NPC:%d:%d:%s] >>> EVENT: %s | %s (pos: %d, %d, %d)",
            timeFormat.format(new Date(now)),
            npc.getNpcId(), npc.getObjectId(), npc.getName(),
            eventName, details,
            npc.getX(), npc.getY(), npc.getZ());

        this.logQueue.offer(line);
    }

    private void runFlusher() {
        File logDir = new File("log");
        if (!logDir.exists() && new File("game/log").exists()) {
            logDir = new File("game/log");
        } else if (!logDir.exists()) {
            logDir.mkdirs();
        }
        File logFile = new File(logDir, "npc_movement_debug.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            while (this.running || !this.logQueue.isEmpty()) {
                try {
                    String line = this.logQueue.poll(500, TimeUnit.MILLISECONDS);
                    if (line != null) {
                        writer.write(line);
                        writer.newLine();

                        // Drena em lote caso haja mais linhas enfileiradas
                        int drained = 0;
                        while (drained < 200) {
                            String next = this.logQueue.poll();
                            if (next == null) break;
                            writer.write(next);
                            writer.newLine();
                            drained++;
                        }
                        writer.flush();
                    }
                } catch (InterruptedException ignored) {
                    if (!this.running && this.logQueue.isEmpty()) break;
                } catch (IOException ioEx) {
                    LOG.error("NpcMovementDebugTracer: Erro ao escrever no arquivo de log", ioEx);
                }
            }
        } catch (IOException e) {
            LOG.error("NpcMovementDebugTracer: Nao foi possivel abrir o arquivo de log: " + logFile.getAbsolutePath(), e);
        }
    }

    /**
     * Registra com precisao cirurgica as fases de combate e animacao (Ataque, Rotacao, HitDelay).
     */
    public void traceCombat(NpcInstance npc, String action, Creature target, String details) {
        if (!isTracking(npc)) {
            return;
        }

        long now = System.currentTimeMillis();
        String line = String.format("[%s] [NPC:%d:%d:%s] === COMBAT: %s | target=%s | %s | isAttackingNow=%b (endIn=%dms) | isMoving=%b",
            timeFormat.format(new Date(now)),
            npc.getNpcId(), npc.getObjectId(), npc.getName(),
            action,
            target != null ? target.getName() : "NULL",
            details,
            npc.isAttackingNow(),
            Math.max(0, npc.getAttackEndTime() - now),
            npc.isMoving());

        this.logQueue.offer(line);
    }

    public void shutdown() {
        this.running = false;
    }
}
