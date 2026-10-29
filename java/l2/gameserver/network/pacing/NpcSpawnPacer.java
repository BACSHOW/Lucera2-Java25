package l2.gameserver.network.pacing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.geodata.SimdGeoMath;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.*;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cadenciador de despacho de NpcInfo (NpcSpawnPacer 2.0 com SIMD Sorting e Priorização de Tiers).
 *
 * By Dhousefe:
 * - Evita congelamentos (freezes/FPS drops) no cliente L2 Unreal Engine 2.5 ao instanciar
 *   dezenas de atores 3D num único tick.
 * - Prioridade imediata absoluta (Tier 0) para Bosses, Raids, NPCs de serviços essenciais,
 *   e qualquer monstro hostil que esteja em combate ou mirando o jogador/pet.
 * - Ordenação espacial Euclidiana via SimdGeoMath: avalia até 64 monstros em lote com loops unrolled
 *   e despacha os mais próximos primeiro para eliminar o efeito pop-in visual.
 * - Coalescing: cancelamento mútuo se o mob sumir antes da transmissão.
 */
public final class NpcSpawnPacer {
    private final Player _player;
    private final ReentrantLock _lock = new ReentrantLock();

    private final IntArrayList _pendingQueue = new IntArrayList(32);
    private final IntOpenHashSet _pendingSet = new IntOpenHashSet(32);

    private ScheduledFuture<?> _dispatchTask = null;
    private int _recentBurstCount = 0;
    private long _lastBurstResetTime = 0L;

    public NpcSpawnPacer(Player player) {
        this._player = player;
    }

    public static boolean isEssentialNpc(NpcInstance npc) {
        if (npc == null) {
            return false;
        }
        return npc instanceof MerchantInstance
                || npc instanceof WarehouseInstance
                || npc instanceof TrainerInstance
                || npc instanceof VillageMasterInstance
                || npc instanceof OlympiadManagerInstance
                || npc instanceof FishermanInstance
                || npc instanceof OlympiadBufferInstance
                || npc instanceof GuardInstance
                || npc.isMerchantNpc();
    }

    public static boolean isBossNpc(NpcInstance npc) {
        if (npc == null) {
            return false;
        }
        return npc.isBoss() || npc.isRaid() || npc instanceof BossInstance || npc instanceof RaidBossInstance || npc instanceof ReflectionBossInstance;
    }

    public static boolean isThreateningPlayer(Player player, NpcInstance npc) {
        if (npc == null || player == null) {
            return false;
        }
        GameObject target = npc.getTarget();
        if (target == player || (player.getPet() != null && target == player.getPet())) {
            return true;
        }
        if (npc.isAttackingNow() || npc.isCastingNow()) {
            if (target == player || (player.getPet() != null && target == player.getPet())) {
                return true;
            }
        }
        if (npc.isAggressive()) {
            int aggroRange = (npc.getTemplate() != null) ? npc.getTemplate().aggroRange : 0;
            double dangerRadius = Math.max(650.0, aggroRange + 150.0);
            long dx = (long) player.getX() - npc.getX();
            long dy = (long) player.getY() - npc.getY();
            long dz = (long) player.getZ() - npc.getZ();
            if (dx * dx + dy * dy + dz * dz <= dangerRadius * dangerRadius) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWithinImmediateDistance(int px, int py, int pz, int nx, int ny, int nz, int maxRadius) {
        long dx = (long) px - nx;
        long dy = (long) py - ny;
        long dz = (long) pz - nz;
        long maxRadiusSq = (long) maxRadius * maxRadius;
        return (dx * dx + dy * dy + dz * dz) <= maxRadiusSq;
    }

    public static boolean shouldBypassPacing(Player player, NpcInstance npc) {
        if (npc == null || player == null) {
            return false;
        }
        if (isBossNpc(npc)) {
            return true;
        }
        if (isEssentialNpc(npc)) {
            return true;
        }
        if (isThreateningPlayer(player, npc)) {
            return true;
        }
        if (isWithinImmediateDistance(player.getX(), player.getY(), player.getZ(), npc.getX(), npc.getY(), npc.getZ(), 450)) {
            return true;
        }
        return false;
    }

    /**
     * Enfileira ou despacha imediatamente o NPC para o jogador.
     */
    public void queueNpc(NpcInstance npc) {
        if (npc == null || _player == null || !_player.isOnline() || _player.isLogoutStarted()) {
            return;
        }

        if (!Config.ENABLE_NPC_INFO_PACING) {
            sendImmediate(npc);
            return;
        }

        if (shouldBypassPacing(_player, npc)) {
            sendImmediate(npc);
            return;
        }

        final long now = System.currentTimeMillis();
        final int burstLimit = Config.NPC_INFO_IMMEDIATE_BURST_LIMIT;

        _lock.lock();
        try {
            if (now - _lastBurstResetTime > Config.NPC_INFO_BURST_RESET_MS) {
                _recentBurstCount = 0;
                _lastBurstResetTime = now;
            }

            if (_pendingQueue.isEmpty() && _recentBurstCount < burstLimit) {
                _recentBurstCount++;
                sendImmediate(npc);
                return;
            }

            final int objId = npc.getObjectId();
            if (_pendingSet.add(objId)) {
                _pendingQueue.add(objId);
                if (_dispatchTask == null || _dispatchTask.isDone()) {
                    final long interval = (long) Config.NPC_INFO_PACING_INTERVAL_MS;
                    _dispatchTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(this::dispatchPacedBatch, interval, interval);
                }
            }
        } finally {
            _lock.unlock();
        }
    }

    public boolean cancelPending(int objectId) {
        if (!Config.ENABLE_DELETE_OBJECT_COALESCING) {
            return false;
        }
        _lock.lock();
        try {
            if (_pendingSet.remove(objectId)) {
                _pendingQueue.rem(objectId);
                if (_pendingQueue.isEmpty() && _dispatchTask != null) {
                    _dispatchTask.cancel(false);
                    _dispatchTask = null;
                }
                return true;
            }
            return false;
        } finally {
            _lock.unlock();
        }
    }

    public boolean isPending(int objectId) {
        _lock.lock();
        try {
            return _pendingSet.contains(objectId);
        } finally {
            _lock.unlock();
        }
    }

    public boolean flushImmediate(int objectId) {
        _lock.lock();
        try {
            if (_pendingSet.remove(objectId)) {
                _pendingQueue.rem(objectId);
                if (_pendingQueue.isEmpty() && _dispatchTask != null) {
                    _dispatchTask.cancel(false);
                    _dispatchTask = null;
                }
                NpcInstance npc = resolveNpc(objectId);
                if (npc != null && npc.isVisible()) {
                    sendImmediate(npc);
                    return true;
                }
            }
            return false;
        } finally {
            _lock.unlock();
        }
    }

    private void dispatchPacedBatch() {
        if (_player == null || !_player.isOnline() || _player.isLogoutStarted()) {
            clear();
            return;
        }

        final int batchSize = Config.NPC_INFO_PACED_BATCH_SIZE;
        final int[] toDispatch = new int[batchSize];
        int count = 0;
        final int playerX = _player.getX();
        final int playerY = _player.getY();

        _lock.lock();
        try {
            final int totalPending = _pendingQueue.size();
            if (totalPending == 0) {
                if (_dispatchTask != null) {
                    _dispatchTask.cancel(false);
                    _dispatchTask = null;
                }
                return;
            }

            if (totalPending <= batchSize) {
                while (!_pendingQueue.isEmpty()) {
                    final int objId = _pendingQueue.removeInt(0);
                    _pendingSet.remove(objId);
                    toDispatch[count++] = objId;
                }
            } else {
                final int candidatesCount = Math.min(totalPending, 64);
                final int[] candidateIds = new int[candidatesCount];
                final int[] targetX = new int[candidatesCount];
                final int[] targetY = new int[candidatesCount];
                final long[] distSq = new long[candidatesCount];
                final byte[] tiers = new byte[candidatesCount];
                int validCandidates = 0;

                for (int i = 0; i < totalPending && validCandidates < candidatesCount; i++) {
                    final int objId = _pendingQueue.getInt(i);
                    final NpcInstance npc = resolveNpc(objId);
                    if (npc != null && npc.isVisible()) {
                        candidateIds[validCandidates] = objId;
                        targetX[validCandidates] = npc.getX();
                        targetY[validCandidates] = npc.getY();

                        if (isBossNpc(npc) || isThreateningPlayer(_player, npc)) {
                            tiers[validCandidates] = 0;
                        } else if (npc.isAggressive()) {
                            tiers[validCandidates] = 1;
                        } else {
                            tiers[validCandidates] = 2;
                        }
                        validCandidates++;
                    }
                }

                if (validCandidates > 0) {
                    SimdGeoMath.batchDistanceSquared2D(playerX, playerY, targetX, targetY, distSq, validCandidates);

                    Integer[] indices = new Integer[validCandidates];
                    for (int i = 0; i < validCandidates; i++) {
                        indices[i] = i;
                    }

                    Arrays.sort(indices, (a, b) -> {
                        int tierDiff = Byte.compare(tiers[a], tiers[b]);
                        if (tierDiff != 0) return tierDiff;
                        return Long.compare(distSq[a], distSq[b]);
                    });

                    int toSelect = Math.min(batchSize, validCandidates);
                    for (int i = 0; i < toSelect; i++) {
                        int bestIndex = indices[i];
                        int bestObjId = candidateIds[bestIndex];
                        if (_pendingSet.remove(bestObjId)) {
                            _pendingQueue.rem(bestObjId);
                            toDispatch[count++] = bestObjId;
                        }
                    }
                } else {
                    while (!_pendingQueue.isEmpty() && count < batchSize) {
                        final int objId = _pendingQueue.removeInt(0);
                        _pendingSet.remove(objId);
                        toDispatch[count++] = objId;
                    }
                }
            }

            if (_pendingQueue.isEmpty() && _dispatchTask != null) {
                _dispatchTask.cancel(false);
                _dispatchTask = null;
            }
        } finally {
            _lock.unlock();
        }

        if (count == 0) {
            return;
        }

        try (GameClient.PacketBatchScope scope = _player.openPacketBatch()) {
            for (int i = 0; i < count; i++) {
                NpcInstance npc = resolveNpc(toDispatch[i]);
                if (npc != null && npc.isVisible() && _player.isInRangeZ(npc, (long) Config.VISIBLE_OBJECT_RADIUS)) {
                    sendImmediate(npc);
                }
            }
        }
    }

    private void sendImmediate(NpcInstance npc) {
        List<L2GameServerPacket> packets = _player.addVisibleObject(npc, null);
        if (packets != null && !packets.isEmpty()) {
            _player.sendPacket(packets);
        }
    }

    private NpcInstance resolveNpc(int objectId) {
        GameObject obj = GameObjectsStorage.getNpc(objectId);
        if (obj instanceof NpcInstance) {
            return (NpcInstance) obj;
        }
        return null;
    }

    public void clear() {
        _lock.lock();
        try {
            _pendingQueue.clear();
            _pendingSet.clear();
            if (_dispatchTask != null) {
                _dispatchTask.cancel(false);
                _dispatchTask = null;
            }
            _recentBurstCount = 0;
        } finally {
            _lock.unlock();
        }
    }
}
