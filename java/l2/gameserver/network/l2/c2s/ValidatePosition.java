/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.data.BoatHolder;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import l2.gameserver.network.l2.s2c.CharMoveToLocation;
import l2.gameserver.utils.Location;

public class ValidatePosition
extends L2GameClientPacket {
    private final Location cH = new Location();
    private int ach;
    private Location JW;
    private Location JX;

    @Override
    protected void readImpl() {
        this.cH.x = this.readD();
        this.cH.y = this.readD();
        this.cH.z = this.readD();
        this.cH.h = this.readD();
        this.ach = this.readD();
    }

    @Override
    protected void runImpl() {
        boolean bl;
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isTeleporting() || player.isInObserverMode() || player.isOlyObserver()) {
            return;
        }
        this.JW = player.getLastClientPosition();
        this.JX = player.getLastServerPosition();
        if (this.JW == null) {
            this.JW = player.getLoc();
        }
        if (this.JX == null) {
            this.JX = player.getLoc();
        }
        if (player.getX() == 0 && player.getY() == 0 && player.getZ() == 0) {
            this.ad(player);
            return;
        }
        if (player.isInFlyingTransform()) {
            if (this.cH.x > -166168) {
                player.setTransformation(0);
                return;
            }
            if (this.cH.z <= 0 || this.cH.z >= 6000) {
                player.teleToLocation(player.getLoc().setZ(Math.min(5950, Math.max(50, this.cH.z))));
                return;
            }
        }
        double d = player.getDistance(this.cH.x, this.cH.y);
        double d2 = player.getDistance(this.cH.x, this.cH.y, this.cH.z);
        int n = Math.abs(this.cH.z - player.getZ());
        int n2 = this.JX.z - player.getZ();
        if (l2.gameserver.utils.MovementTelemetryLogger.ENABLED) l2.gameserver.utils.MovementTelemetryLogger.logC2S(player, "ValidatePosition", String.format("ClientLoc=(%d,%d,%d, h=%d) | delta2D=%.1f delta3D=%.1f deltaZ=%d | srvSpeed=%d cliSpeed=%.0f", this.cH.x, this.cH.y, this.cH.z, this.cH.h, d, d2, n, player.getMoveSpeed(), ValidatePosition.observedSpeed(this.JW, this.cH, System.currentTimeMillis() - player.getLastClientReportTime())));
        if (this.ach > 0) {
            Boat boat = BoatHolder.getInstance().getBoat(this.ach);
            if (boat != null && player.getBoat() == boat) {
                player.setHeading(this.cH.h);
                boat.validateLocationPacket(player);
            }
            player.setLastClientPosition(this.cH.setH(player.getHeading()));
            player.setLastServerPosition(player.getLoc());
            return;
        }
        if (player.isFalling()) {
            d = 0.0;
            n = 0;
            n2 = 0;
        }
        int n3 = player.maxZDiff() + 192;
        boolean bl2 = bl = !player.isInWater() && !player.isFlying();
        if (bl && n2 >= 512) {
            player.falling(n2);
        }
        if (bl && n >= n3) {
            player.validateLocation(0);
        } else if (this.cH.z < -30000 || this.cH.z > 30000) {
            if (player.getIncorrectValidateCount() >= 3) {
                player.teleToClosestTown();
            } else {
                this.ad(player);
                player.setIncorrectValidateCount(player.getIncorrectValidateCount() + 1);
            }
        } else if (d2 > RED_ZONE && clientLagging(player)) {
            // Conexao atrasada: a correcao anterior ainda esta na fila de envio, entao o cliente esta reportando
            // uma posicao de antes dela. A posicao do servidor ja e a valida; nao manda outra correcao (so encheria
            // mais a fila) e nao conta como tentativa invalida (senao a conexao lenta levava o jogador a cidade).
            this.logZone(player, "RED_LAG", d);
        } else if (d2 > RED_ZONE) {
            // Red Zone: snap de seguranca; tentativas invalidas consecutivas levam a cidade.
            int fails = player.getIncorrectValidateCount();
            if (Config.MOVE_TO_TOWN_ON_FAIL_VALIDATE && fails >= Config.MOVE_TO_TOWN_ON_FAIL_TRY_COUNT) {
                player.setIncorrectValidateCount(0);
                this.logZone(player, "RED_TOWN", d);
                player.teleToClosestTown();
            } else {
                player.setIncorrectValidateCount(fails + 1);
                this.logZone(player, "RED_SNAP", d);
                player.validateLocation(1);
            }
        } else if (d > GREEN_ZONE) {
            // Yellow Zone: reconciliacao elastica, sem teleporte.
            player.setIncorrectValidateCount(0);
            int result = this.reconcileElastic(player);
            if (result == 1) {
                this.logZone(player, "YELLOW_ELASTIC", d);
            } else if (result == 2) {
                this.logZone(player, "YELLOW_SNAP", d);
            }
        } else {
            // Green Zone: posicao do cliente aceita normalmente.
            player.setIncorrectValidateCount(0);
            player.resetElasticAttempts();
            if (this.acceptClientPosition(player, d)) {
                this.logZone(player, "GREEN_ACCEPT", d);
            }
        }
        player.setLastClientPosition(this.cH.setH(player.getHeading()));
        player.setLastServerPosition(player.getLoc());
        player.setLastClientReportTime(System.currentTimeMillis());
    }

    /** Limite da Green Zone (README: delta <= 180u). */
    public static final double GREEN_ZONE = 180.0;
    /** Limite da Yellow Zone (README: 180u < delta <= 500u); acima disso e Red Zone. */
    public static final double RED_ZONE = 500.0;
    /** Acima disso na fila de envio, o cliente ainda nao recebeu a ultima correcao de posicao. */
    private static final long LAG_PENDING_BYTES = 32L * 1024L;
    /** Abaixo disto a diferenca e ruido de latencia/quantizacao e nada e feito. */
    public static final double ACCEPT_MIN_DELTA = 32.0;
    /** O cliente e considerado parado quando dois relatorios seguidos diferem menos que isto. */
    public static final double CLIENT_STABLE_DELTA = 8.0;
    /** Dois relatorios iguais so contam como "parado" se vierem com pelo menos este intervalo (o cliente manda duplicatas). */
    public static final long CLIENT_STABLE_MIN_MS = 150L;
    /** Reconciliacoes elasticas ignoradas pelo cliente antes da correcao direta. */
    public static final int ELASTIC_MAX_ATTEMPTS = 2;
    /** Intervalo minimo entre duas reconciliacoes elasticas para o mesmo jogador. */
    public static final long ELASTIC_INTERVAL_MS = 1000L;

    /** Velocidade que o cliente demonstrou entre dois relatorios de posicao (so para o log). */
    public static double observedSpeed(Location previous, Location current, long elapsedMs) {
        if (previous == null || current == null || elapsedMs < 200L || elapsedMs > 5000L) {
            return 0.0;
        }
        return previous.distance(current) * 1000.0 / (double)elapsedMs;
    }

    /**
     * Distancia maxima que o servidor aceita de uma vez na Green Zone. Limitada pela velocidade do
     * personagem desde a ultima aceitacao, para que a aceitacao nao vire um atalho de speedhack.
     */
    public static double acceptBudget(int moveSpeed, long elapsedMs) {
        if (elapsedMs <= 0L) {
            return 0.0;
        }
        return Math.min(GREEN_ZONE, (double)moveSpeed * (double)elapsedMs / 1000.0);
    }

    /**
     * Green Zone. Com o jogador parado no servidor e o cliente parado em outro ponto alcancavel
     * pela geodata, o servidor passa a usar a posicao do cliente. Em movimento nada e feito:
     * a diferenca e so latencia.
     */
    private boolean acceptClientPosition(Player player, double delta2D) {
        if (delta2D < ACCEPT_MIN_DELTA) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (this.JW == null || this.JW.distance(this.cH) > CLIENT_STABLE_DELTA || now - player.getLastClientReportTime() < CLIENT_STABLE_MIN_MS) {
            return false;
        }
        return player.tryAcceptClientPosition(this.cH, false);
    }

    /**
     * Yellow Zone. Com o servidor em movimento nada e feito: o cliente ja corre para o mesmo destino
     * e reenviar o pacote so o faria recomecar a corrida. Com o servidor parado, manda o cliente
     * correr de onde esta ate o ponto do servidor, sem teleporte. Se o cliente ignorar isso
     * {@link #ELASTIC_MAX_ATTEMPTS} vezes seguidas no mesmo lugar, corrige de uma vez.
     *
     * @return 0 = nada, 1 = reconciliacao elastica, 2 = correcao direta
     */
    private int reconcileElastic(Player player) {
        if (player.isMoving()) {
            return 0;
        }
        long now = System.currentTimeMillis();
        if (now - player.getLastElasticReconcileTime() < ELASTIC_INTERVAL_MS) {
            return 0;
        }
        player.setLastElasticReconcileTime(now);
        if (player.countElasticAttempt(this.cH) > ELASTIC_MAX_ATTEMPTS) {
            player.resetElasticAttempts();
            player.validateLocation(0);
            return 2;
        }
        player.setLastClientPosition(this.cH);
        player.sendPacket(new CharMoveToLocation(player, this.cH.clone(), player.getLoc()));
        return 1;
    }

    /** Fila de envio desse cliente acima de 32 KB, ou a conexao marcada como cheia. */
    private static boolean clientLagging(Player player) {
        GameClient client = player.getNetConnection();
        NettyGameConnection conn = client == null ? null : client.getNettyConnection();
        return conn != null && (!conn.isWritable() || conn.pendingWriteBytes() > LAG_PENDING_BYTES);
    }

    private void logZone(Player player, String zone, double delta2D) {
        l2.gameserver.metrics.ServerMetrics.count(zone.equals("RED_LAG") ? l2.gameserver.metrics.ServerMetrics.Counter.VP_RED_LAG
                : zone.startsWith("RED") ? l2.gameserver.metrics.ServerMetrics.Counter.VP_RED
                : zone.startsWith("YELLOW") ? l2.gameserver.metrics.ServerMetrics.Counter.VP_YELLOW
                : l2.gameserver.metrics.ServerMetrics.Counter.VP_GREEN);
        if (l2.gameserver.utils.MovementTelemetryLogger.ENABLED) {
            l2.gameserver.utils.MovementTelemetryLogger.logEvent(player, "VALIDATE_ZONE", String.format("zone=%s delta2D=%.1f ClientLoc=(%d,%d,%d) moving=%b", zone, delta2D, this.cH.x, this.cH.y, this.cH.z, player.isMoving()));
        }
    }

    private void ad(Player player) {
        if (player.isGM()) {
            player.sendMessage("Server loc: " + player.getLoc());
            player.sendMessage("Correcting position...");
        }
        if (this.JX.x != 0 && this.JX.y != 0 && this.JX.z != 0) {
            if (GeoEngine.getNSWE(this.JX.x, this.JX.y, this.JX.z, player.getGeoIndex()) == 15) {
                player.teleToLocation(this.JX);
            } else {
                player.teleToClosestTown();
            }
        } else if (this.JW.x != 0 && this.JW.y != 0 && this.JW.z != 0) {
            if (GeoEngine.getNSWE(this.JW.x, this.JW.y, this.JW.z, player.getGeoIndex()) == 15) {
                player.teleToLocation(this.JW);
            } else {
                player.teleToClosestTown();
            }
        } else {
            player.teleToClosestTown();
        }
    }
}

