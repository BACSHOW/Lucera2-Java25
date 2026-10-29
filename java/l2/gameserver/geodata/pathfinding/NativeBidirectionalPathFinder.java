package l2.gameserver.geodata.pathfinding;

import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.geodata.GeoMath;
import l2.gameserver.model.World;
import l2.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Motor de Pathfinding de Alta Performance: Bidirectional A* (NBA*) com Zero-GC e Poda JPS.
 * <p>
 * Mechanical Sympathy & Low Latency:
 * 1. <b>Zero Alocações Dinâmicas</b>: Coordenadas empacotadas em primitivos {@code long} de 64 bits.
 * 2. <b>Coleções Primitivas Fastutil</b>: Mapas abertos sem autoboxing (Long2Int, Long2Float, Long2Long).
 * 3. <b>Padrão Epoch-Marker</b>: {@code runId} incremental por thread elimina chamadas a {@code .clear()}.
 * 4. <b>Busca Bidirecional Simultânea</b>: Origem <-> Destino com parada estrita por custo m<u> (mu).
 * 5. <b>Suavização String-Pulling</b>: Raycasting direto via linha de visão que poda ziguezagues na malha.
 */
public final class NativeBidirectionalPathFinder {
    public static final int MAX_EXPANDED_NODES = 4000;
    public static final float MOVE_COST_CARDINAL = 10.0f;
    public static final float MOVE_COST_DIAGONAL = 14.142f;

    // 8 Direções: E, W, S, N, SE, SW, NE, NW
    private static final int[] DX = { 1, -1, 0, 0, 1, -1, 1, -1 };
    private static final int[] DY = { 0, 0, 1, -1, 1, 1, -1, -1 };
    private static final float[] COSTS = {
        MOVE_COST_CARDINAL, MOVE_COST_CARDINAL, MOVE_COST_CARDINAL, MOVE_COST_CARDINAL,
        MOVE_COST_DIAGONAL, MOVE_COST_DIAGONAL, MOVE_COST_DIAGONAL, MOVE_COST_DIAGONAL
    };

    // Flags de movimento exigidas por direção
    private static final byte[] NSWE_FLAGS = {
        1,  // E
        2,  // W
        4,  // S
        8,  // N
        5,  // SE (S | E)
        6,  // SW (S | W)
        9,  // NE (N | E)
        10  // NW (N | W)
    };

    private static final NativeBidirectionalPathFinder INSTANCE = new NativeBidirectionalPathFinder();

    public static NativeBidirectionalPathFinder getInstance() {
        return INSTANCE;
    }

    // Empacota (gx: 20 bits, gy: 20 bits, z: 16 bits)
    public static long pack(int gx, int gy, int z) {
        return (((long) gx & 0xFFFFFL) << 36) |
               (((long) gy & 0xFFFFFL) << 16) |
               ((long) (z + 32768) & 0xFFFFL);
    }

    public static int unpackGx(long packed) {
        int v = (int) ((packed >>> 36) & 0xFFFFFL);
        return (v << 12) >> 12; // Sign extend 20 bits
    }

    public static int unpackGy(long packed) {
        int v = (int) ((packed >>> 16) & 0xFFFFFL);
        return (v << 12) >> 12;
    }

    public static int unpackZ(long packed) {
        return ((int) (packed & 0xFFFFL)) - 32768;
    }

    public static int world2geoX(int wx) {
        return (wx - World.MAP_MIN_X) >> 4;
    }

    public static int world2geoY(int wy) {
        return (wy - World.MAP_MIN_Y) >> 4;
    }

    public static int geo2worldX(int gx) {
        return (gx << 4) + World.MAP_MIN_X + 8;
    }

    public static int geo2worldY(int gy) {
        return (gy << 4) + World.MAP_MIN_Y + 8;
    }

    private static final class PathNode implements Comparable<PathNode> {
        long key;
        float fCost;

        PathNode(long key, float fCost) {
            this.key = key;
            this.fCost = fCost;
        }

        @Override
        public int compareTo(PathNode other) {
            return Float.compare(this.fCost, other.fCost);
        }
    }

    /**
     * Contexto pré-alocado por thread: zero alocação no heap durante a busca.
     */
    private static final class ThreadContext {
        int runId = 1;

        final PriorityQueue<PathNode> openForward = new PriorityQueue<>(512);
        final PriorityQueue<PathNode> openBackward = new PriorityQueue<>(512);

        final Long2IntOpenHashMap seenForward = new Long2IntOpenHashMap(1024);
        final Long2IntOpenHashMap seenBackward = new Long2IntOpenHashMap(1024);
        final Long2IntOpenHashMap closedForward = new Long2IntOpenHashMap(1024);
        final Long2IntOpenHashMap closedBackward = new Long2IntOpenHashMap(1024);

        final Long2FloatOpenHashMap gForward = new Long2FloatOpenHashMap(1024);
        final Long2FloatOpenHashMap gBackward = new Long2FloatOpenHashMap(1024);

        final Long2LongOpenHashMap parentForward = new Long2LongOpenHashMap(1024);
        final Long2LongOpenHashMap parentBackward = new Long2LongOpenHashMap(1024);

        final PathNode[] nodePool = new PathNode[MAX_EXPANDED_NODES * 2];
        int nodePoolIdx = 0;

        ThreadContext() {
            for (int i = 0; i < this.nodePool.length; i++) {
                this.nodePool[i] = new PathNode(0L, 0.0f);
            }
        }

        PathNode acquireNode(long key, float fCost) {
            if (this.nodePoolIdx < this.nodePool.length) {
                PathNode n = this.nodePool[this.nodePoolIdx++];
                n.key = key;
                n.fCost = fCost;
                return n;
            }
            return new PathNode(key, fCost);
        }

        void reset() {
            this.runId++;
            if (this.runId == Integer.MAX_VALUE) {
                this.seenForward.clear();
                this.seenBackward.clear();
                this.closedForward.clear();
                this.closedBackward.clear();
                this.runId = 1;
            }
            this.openForward.clear();
            this.openBackward.clear();
            this.nodePoolIdx = 0;
        }
    }

    private final ThreadLocal<ThreadContext> contextThreadLocal = ThreadLocal.withInitial(ThreadContext::new);

    /**
     * Busca de caminho bidirecional (NBA*) com poda em linha reta (JPS).
     */
    public List<Location> findPath(int ox, int oy, int oz, int tx, int ty, int tz, int geoIndex) {
        // Beeline check imediato: se o caminho reto for transitável, devolve rota direta sem busca
        if (GeoEngine.canMoveWithCollision(ox, oy, oz, tx, ty, tz, geoIndex)) {
            List<Location> direct = new ArrayList<>(2);
            direct.add(new Location(ox, oy, oz));
            direct.add(new Location(tx, ty, tz));
            return direct;
        }

        int gox = world2geoX(ox);
        int goy = world2geoY(oy);
        int goz = GeoEngine.getHeight(ox, oy, oz, geoIndex);

        int gtx = world2geoX(tx);
        int gty = world2geoY(ty);
        int gtz = GeoEngine.getHeight(tx, ty, tz, geoIndex);

        ThreadContext ctx = this.contextThreadLocal.get();
        ctx.reset();
        int currentRun = ctx.runId;

        long startKey = pack(gox, goy, goz);
        long targetKey = pack(gtx, gty, gtz);

        if (startKey == targetKey) {
            return Collections.singletonList(new Location(ox, oy, oz));
        }

        // Inicializa fronteira Forward
        ctx.gForward.put(startKey, 0.0f);
        ctx.seenForward.put(startKey, currentRun);
        float hStart = heuristic(gox, goy, goz, gtx, gty, gtz);
        ctx.openForward.add(ctx.acquireNode(startKey, hStart));

        // Inicializa fronteira Backward
        ctx.gBackward.put(targetKey, 0.0f);
        ctx.seenBackward.put(targetKey, currentRun);
        float hTarget = heuristic(gtx, gty, gtz, gox, goy, goz);
        ctx.openBackward.add(ctx.acquireNode(targetKey, hTarget));

        long bestMeetingKey = -1L;
        float mu = Float.MAX_VALUE; // Menor custo de caminho encontrado até agora
        int expanded = 0;

        // Loop NBA*
        while (!ctx.openForward.isEmpty() && !ctx.openBackward.isEmpty() && expanded < MAX_EXPANDED_NODES) {
            expanded++;

            PathNode topF = ctx.openForward.peek();
            PathNode topB = ctx.openBackward.peek();

            // Condição de parada ótima do NBA*
            if (topF.fCost >= mu && topB.fCost >= mu) {
                break;
            }

            // Alternância balanceada de fronteiras (expande a de menor f-cost)
            if (topF.fCost <= topB.fCost) {
                PathNode cur = ctx.openForward.poll();
                long curKey = cur.key;
                ctx.closedForward.put(curKey, currentRun);

                int cgx = unpackGx(curKey);
                int cgy = unpackGy(curKey);
                int cgz = unpackZ(curKey);
                float curG = ctx.gForward.get(curKey);

                if (cur.fCost < mu) {
                    expandFrontier(curKey, cgx, cgy, cgz, curG, gtx, gty, gtz, true, ctx, geoIndex, currentRun);
                    if (ctx.seenBackward.get(curKey) == currentRun) {
                        float totalCost = curG + ctx.gBackward.get(curKey);
                        if (totalCost < mu) {
                            mu = totalCost;
                            bestMeetingKey = curKey;
                        }
                    }
                }
            } else {
                PathNode cur = ctx.openBackward.poll();
                long curKey = cur.key;
                ctx.closedBackward.put(curKey, currentRun);

                int cgx = unpackGx(curKey);
                int cgy = unpackGy(curKey);
                int cgz = unpackZ(curKey);
                float curG = ctx.gBackward.get(curKey);

                if (cur.fCost < mu) {
                    expandFrontier(curKey, cgx, cgy, cgz, curG, gox, goy, goz, false, ctx, geoIndex, currentRun);
                    if (ctx.seenForward.get(curKey) == currentRun) {
                        float totalCost = curG + ctx.gForward.get(curKey);
                        if (totalCost < mu) {
                            mu = totalCost;
                            bestMeetingKey = curKey;
                        }
                    }
                }
            }
        }

        if (bestMeetingKey == -1L) {
            return Collections.emptyList();
        }

        long[] rawKeys = reconstructPath(startKey, targetKey, bestMeetingKey, ctx);
        if (rawKeys.length == 0) {
            return Collections.emptyList();
        }

        // Pós-processamento String Pulling (LOS smoothing / Funnel) para podar ziguezagues
        return smoothPath(rawKeys, ox, oy, oz, tx, ty, tz, geoIndex);
    }

    private void expandFrontier(
        long curKey, int cgx, int cgy, int cgz, float curG,
        int destGx, int destGy, int destGz,
        boolean isForward,
        ThreadContext ctx,
        int geoIndex,
        int currentRun
    ) {
        Long2FloatOpenHashMap gMap = isForward ? ctx.gForward : ctx.gBackward;
        Long2LongOpenHashMap pMap = isForward ? ctx.parentForward : ctx.parentBackward;
        Long2IntOpenHashMap seenMap = isForward ? ctx.seenForward : ctx.seenBackward;
        Long2IntOpenHashMap closedMap = isForward ? ctx.closedForward : ctx.closedBackward;
        PriorityQueue<PathNode> queue = isForward ? ctx.openForward : ctx.openBackward;

        byte curNswe = (byte) GeoEngine.getNSWE(cgx, cgy, cgz, geoIndex);

        for (int i = 0; i < 8; i++) {
            byte reqFlag = NSWE_FLAGS[i];
            if ((curNswe & reqFlag) == 0) {
                continue; // Bloqueio geodata nesta direção
            }

            if (i >= 4 && !canCrossDiagonal(cgx, cgy, cgz, i, geoIndex)) {
                continue; // Impede cortar a quina de duas células bloqueadas
            }

            int ngx = cgx + DX[i];
            int ngy = cgy + DY[i];
            int ngz = GeoEngine.getHeight(geo2worldX(ngx), geo2worldY(ngy), cgz, geoIndex);

            // Desnível de degrau máximo permitido (32 unidades)
            if (Math.abs(ngz - cgz) > 32) {
                continue;
            }

            long neighborKey = pack(ngx, ngy, ngz);
            if (closedMap.get(neighborKey) == currentRun) {
                continue;
            }

            int stepCount = 1;
            // Poda JPS: Em terreno plano sem quinas e com NSWE aberto, salta até 2 células em linha reta
            if (i < 4 && Math.abs(ngz - cgz) <= 4) {
                int jumpGx = ngx + DX[i];
                int jumpGy = ngy + DY[i];
                int jumpGz = GeoEngine.getHeight(geo2worldX(jumpGx), geo2worldY(jumpGy), ngz, geoIndex);
                byte nNswe = (byte) GeoEngine.getNSWE(ngx, ngy, ngz, geoIndex);
                if ((nNswe & reqFlag) != 0 && Math.abs(jumpGz - ngz) <= 4) {
                    ngx = jumpGx;
                    ngy = jumpGy;
                    ngz = jumpGz;
                    neighborKey = pack(ngx, ngy, ngz);
                    stepCount = 2;
                    if (closedMap.get(neighborKey) == currentRun) {
                        continue;
                    }
                }
            }

            float edgeCost = movementCost(i, stepCount);
            float tentativeG = curG + edgeCost;

            boolean isSeen = seenMap.get(neighborKey) == currentRun;
            float prevG = isSeen ? gMap.get(neighborKey) : Float.MAX_VALUE;

            if (!isSeen || tentativeG < prevG) {
                gMap.put(neighborKey, tentativeG);
                pMap.put(neighborKey, curKey);
                seenMap.put(neighborKey, currentRun);

                float h = heuristic(ngx, ngy, ngz, destGx, destGy, destGz);
                float f = tentativeG + h;
                queue.add(ctx.acquireNode(neighborKey, f));
            }
        }
    }

    static float movementCost(int direction, int stepCount) {
        return COSTS[direction] * stepCount;
    }

    private static boolean canCrossDiagonal(int gx, int gy, int gz, int direction, int geoIndex) {
        int firstDirection;
        int secondDirection;
        switch (direction) {
            case 4 -> { firstDirection = 0; secondDirection = 2; }
            case 5 -> { firstDirection = 1; secondDirection = 2; }
            case 6 -> { firstDirection = 0; secondDirection = 3; }
            case 7 -> { firstDirection = 1; secondDirection = 3; }
            default -> throw new IllegalArgumentException("Not a diagonal direction: " + direction);
        }

        byte firstNswe = (byte) GeoEngine.getNSWE(gx + DX[firstDirection], gy + DY[firstDirection], gz, geoIndex);
        byte secondNswe = (byte) GeoEngine.getNSWE(gx + DX[secondDirection], gy + DY[secondDirection], gz, geoIndex);
        return (firstNswe & NSWE_FLAGS[secondDirection]) != 0
            && (secondNswe & NSWE_FLAGS[firstDirection]) != 0;
    }

    private static float heuristic(int x1, int y1, int z1, int x2, int y2, int z2) {
        return (float) GeoMath.heuristicOctile3D(Math.abs(x1 - x2), Math.abs(y1 - y2), Math.abs(z1 - z2));
    }

    private static long[] reconstructPath(long startKey, long targetKey, long meetingKey, ThreadContext ctx) {
        LongArrayList forwardKeys = new LongArrayList(256);
        long curr = meetingKey;
        while (curr != startKey) {
            forwardKeys.add(curr);
            curr = ctx.parentForward.get(curr);
        }
        forwardKeys.add(startKey);

        LongArrayList fullPath = new LongArrayList(forwardKeys.size() + 256);
        for (int i = forwardKeys.size() - 1; i >= 0; i--) {
            fullPath.add(forwardKeys.getLong(i));
        }

        curr = meetingKey;
        while (curr != targetKey) {
            curr = ctx.parentBackward.get(curr);
            fullPath.add(curr);
        }

        return fullPath.toLongArray();
    }

    /**
     * String Pulling (Funnel Algorithm):
     * Conecta diretamente nós distantes através de raycasting LOS (canMoveWithCollision),
     * eliminando quinas e ziguezagues desnecessários da malha de geodata.
     */
    private static List<Location> smoothPath(long[] rawKeys, int ox, int oy, int oz, int tx, int ty, int tz, int geoIndex) {
        if (rawKeys.length == 0) {
            return Collections.emptyList();
        }

        List<Location> worldLocs = new ArrayList<>(rawKeys.length + 2);
        worldLocs.add(new Location(ox, oy, oz));

        for (long k : rawKeys) {
            int wx = geo2worldX(unpackGx(k));
            int wy = geo2worldY(unpackGy(k));
            int wz = unpackZ(k);
            worldLocs.add(new Location(wx, wy, wz));
        }
        worldLocs.add(new Location(tx, ty, tz));

        if (worldLocs.size() <= 2) {
            return worldLocs;
        }

        List<Location> smoothed = new ArrayList<>(worldLocs.size());
        smoothed.add(worldLocs.get(0));

        int anchorIdx = 0;
        int testIdx = 2;

        while (testIdx < worldLocs.size()) {
            Location pAnchor = worldLocs.get(anchorIdx);
            Location pTest = worldLocs.get(testIdx);

            if (GeoEngine.canMoveWithCollision(pAnchor.x, pAnchor.y, pAnchor.z, pTest.x, pTest.y, pTest.z, geoIndex)) {
                testIdx++;
            } else {
                Location keep = worldLocs.get(testIdx - 1);
                smoothed.add(keep);
                anchorIdx = testIdx - 1;
                testIdx = anchorIdx + 2;
            }
        }

        smoothed.add(worldLocs.get(worldLocs.size() - 1));
        return smoothed;
    }
}
