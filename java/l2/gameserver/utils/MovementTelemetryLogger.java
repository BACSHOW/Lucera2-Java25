package l2.gameserver.utils;

import l2.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Telemetrista de Alta Precisão para Movimento, Ações e Combate.
 * Grava todas as trocas de pacotes e eventos em game/log/movement_debug.txt de forma assíncrona.
 */
public final class MovementTelemetryLogger {
    private static final Logger _log = LoggerFactory.getLogger(MovementTelemetryLogger.class);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final ConcurrentLinkedQueue<String> QUEUE = new ConcurrentLinkedQueue<>();
    private static volatile boolean RUNNING = true;
    private static PrintWriter WRITER;

    static {
        try {
            File logDir = new File("game/log");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            File logFile = new File(logDir, "movement_debug.txt");
            // Recria ou anexa de forma limpa a cada inicialização
            WRITER = new PrintWriter(new BufferedWriter(new FileWriter(logFile, false), 32 * 1024));

            Thread flushThread = new Thread(() -> {
                while (RUNNING || !QUEUE.isEmpty()) {
                    String line = QUEUE.poll();
                    if (line != null) {
                        WRITER.println(line);
                        WRITER.flush();
                    } else {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
                if (WRITER != null) {
                    WRITER.flush();
                    WRITER.close();
                }
            }, "MovementTelemetryWriter");
            flushThread.setDaemon(true);
            flushThread.setPriority(Thread.MIN_PRIORITY);
            flushThread.start();

            log("=== INICIALIZANDO TELEMETRIA DE MOVIMENTO E PACOTES L2JLUCERA2 ===");
        } catch (Exception e) {
            _log.error("Falha ao inicializar MovementTelemetryLogger", e);
        }
    }

    private static void log(String line) {
        String ts = LocalTime.now().format(TIME_FORMAT);
        QUEUE.add("[" + ts + "] " + line);
    }

    public static void logC2S(Player player, String packetName, String details) {
        if (player == null) return;
        log(String.format("C2S [%s #%d] %-22s | SrvLoc=(%d,%d,%d, h=%d, isMoving=%b) | %s",
                player.getName(), player.getObjectId(), packetName,
                player.getX(), player.getY(), player.getZ(), player.getHeading(), player.isMoving(),
                details));
    }

    public static void logS2C(Player player, String packetName, String details) {
        if (player == null) return;
        log(String.format("S2C [%s #%d] %-22s | SrvLoc=(%d,%d,%d, h=%d, isMoving=%b) | %s",
                player.getName(), player.getObjectId(), packetName,
                player.getX(), player.getY(), player.getZ(), player.getHeading(), player.isMoving(),
                details));
    }

    public static void logEvent(Player player, String eventName, String details) {
        if (player == null) return;
        log(String.format("EVT [%s #%d] %-22s | SrvLoc=(%d,%d,%d, h=%d, isMoving=%b) | %s",
                player.getName(), player.getObjectId(), eventName,
                player.getX(), player.getY(), player.getZ(), player.getHeading(), player.isMoving(),
                details));
    }
}
