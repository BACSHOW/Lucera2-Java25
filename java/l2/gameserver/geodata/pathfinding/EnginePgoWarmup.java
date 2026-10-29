package l2.gameserver.geodata.pathfinding;

import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.network.l2.GameCrypt;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Motor de Saturação Real e Compilação Tier-4 JIT C2 / PGO (Profile-Guided Optimization).
 * <p>
 * Objetivo Mecânico:
 * Quase satura os caminhos críticos mais sensíveis à latência da engine antes da abertura
 * pública dos sockets de rede, forçando o compilador HotSpot C2 a atingir o limiar de compilação
 * (CompileThreshold) e aplicar inlining agressivo, desenrolamento de loops (loop unrolling),
 * vetorização SIMD (AVX/NEON) e eliminação de checagens de borda em:
 * 1. Geodata 3D Raycasting, Height-lookup e NSWE.
 * 2. Motor de Busca de Rota Bidirecional A* (NBA*) com Poda JPS e String Pulling.
 * 3. Serialização de Pacotes Críticos de Movimento e Combate (S2C e C2S).
 * 4. Criptografia de Pacotes Blowfish.
 * 5. Matemática Trigonométrica de Heading, Ângulo e Distância Euclidiana.
 */
public final class EnginePgoWarmup {
    private static final Logger _log = LoggerFactory.getLogger(EnginePgoWarmup.class);

    public static final int GEODATA_WARMUP_ITERATIONS = 50_000;
    public static final int PATHFINDER_WARMUP_ITERATIONS = 5_000;
    public static final int NETWORK_WARMUP_ITERATIONS = 50_000;
    public static final int CRYPT_WARMUP_ITERATIONS = 20_000;
    public static final int MATH_WARMUP_ITERATIONS = 50_000;
    public static final int SKILLS_WARMUP_ITERATIONS = 50_000;

    private EnginePgoWarmup() {
        // Utilitário
    }

    public static void runSaturatingWarmup() {
        long totalStart = System.currentTimeMillis();
        _log.info("================================================================================");
        _log.info("         L2JLucera2 Tier-4 JIT C2 Satiation & PGO Warmup Engine                 ");
        _log.info("================================================================================");

        warmupGeodataEngine();
        warmupPathfinderEngine();
        warmupMathEngine();
        warmupNetworkPacketsEngine();
        warmupCryptEngine();
        warmupSkillsEngine();
        warmupCombatFormulas();
        warmupItemsAndInventory();
        warmupNewSubsystemsEngine();

        long totalDuration = System.currentTimeMillis() - totalStart;
        _log.info("[PGO Warmup] Saturação completa de subsistemas finalizada em {} ms.", totalDuration);
        _log.info("[PGO Warmup] Perfil quente gerado: HotSpot C2 com prioridade máxima de otimização nativa.");
        _log.info("================================================================================");
    }

    /**
     * 1. Saturação de Geodata (50.000 iterações): exercita raycasts, lookup de altura e NSWE
     * em regiões de alta densidade (Giran, Dion, Gludio, Aden).
     */
    private static void warmupGeodataEngine() {
        long start = System.currentTimeMillis();
        // Coordenadas mundiais de referência no Lineage II
        int[] testX = { 83400, -14279, -84852, 11193, 147450 };
        int[] testY = { 147943, 124446, 243174, 15963, 26000 };
        int[] testZ = { -3404, -3120, -3728, -4584, -2200 };

        long sink = 0;
        for (int i = 0; i < GEODATA_WARMUP_ITERATIONS; i++) {
            int idx = i % testX.length;
            int x1 = testX[idx] + (i % 64);
            int y1 = testY[idx] + (i % 64);
            int z1 = testZ[idx];

            int x2 = x1 + ((i % 128) - 64);
            int y2 = y1 + ((i % 128) - 64);
            int z2 = z1;

            // Exercita busca de altura
            int h = GeoEngine.getHeight(x1, y1, z1, 0);
            sink += h;

            // Exercita verificação de linha de visão e colisão de movimento
            boolean canMove = GeoEngine.canMoveWithCollision(x1, y1, z1, x2, y2, z2, 0);
            if (canMove) sink++;

            // Exercita leitura de flags NSWE
            int gx = (x1 - l2.gameserver.model.World.MAP_MIN_X) >> 4;
            int gy = (y1 - l2.gameserver.model.World.MAP_MIN_Y) >> 4;
            sink += GeoEngine.getNSWE(gx, gy, h, 0);
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Geodata 3D Raycasting : {} iterações concluídas em {} ms (sink={})",
            GEODATA_WARMUP_ITERATIONS, elapsed, sink);
    }

    /**
     * 2. Saturação do Motor de Busca NBA* (5.000 iterações): expande milhares de nós no heap Fastutil,
     * exercita o critério de parada mu, poda JPS e o algoritmo de String Pulling.
     */
    private static void warmupPathfinderEngine() {
        long start = System.currentTimeMillis();
        NativeBidirectionalPathFinder finder = NativeBidirectionalPathFinder.getInstance();

        int[] originsX = { 83400, 83450, -14279, -14200, 82500 };
        int[] originsY = { 147943, 148000, 124446, 124500, 148200 };
        int[] originsZ = { -3404, -3404, -3120, -3120, -3460 };

        int foundPaths = 0;
        for (int i = 0; i < PATHFINDER_WARMUP_ITERATIONS; i++) {
            int idx = i % originsX.length;
            int ox = originsX[idx];
            int oy = originsY[idx];
            int oz = originsZ[idx];

            int tx = ox + ((i % 16) * 16) + 32;
            int ty = oy + (((i + 1) % 16) * 16) + 32;
            int tz = oz;

            var path = finder.findPath(ox, oy, oz, tx, ty, tz, 0);
            if (!path.isEmpty()) {
                foundPaths++;
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Pathfinder NBA* & JPS  : {} buscas executadas em {} ms (rotas encontradas={})",
            PATHFINDER_WARMUP_ITERATIONS, elapsed, foundPaths);
    }

    /**
     * 3. Saturação Matemática (50.000 iterações): trigonometria de heading, ângulos e distâncias euclidianas.
     */
    private static void warmupMathEngine() {
        long start = System.currentTimeMillis();
        long sink = 0;

        for (int i = 0; i < MATH_WARMUP_ITERATIONS; i++) {
            int x1 = 80000 + i;
            int y1 = 140000 - i;
            int x2 = x1 + (i % 500) - 250;
            int y2 = y1 + ((i * 2) % 500) - 250;

            int heading = PositionUtils.calculateHeadingFrom(x1, y1, x2, y2);
            double angle = PositionUtils.calculateAngleFrom(x1, y1, x2, y2);
            boolean inRange = PositionUtils.checkIfInRange(150, x1, y1, x2, y2);

            sink += heading + (int) angle + (inRange ? 1 : 0);
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Matemática e Vetores    : {} cálculos executados em {} ms (sink={})",
            MATH_WARMUP_ITERATIONS, elapsed, sink);
    }

    /**
     * 4. Saturação de Network (50.000 iterações): serialização intensiva Little-Endian
     * de pacotes críticos de alta frequência (movimento, combate, teleporte).
     */
    private static void warmupNetworkPacketsEngine() {
        long start = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.LITTLE_ENDIAN);

        int[] wayX = { 83420, 83460, 83500 };
        int[] wayY = { 147960, 147980, 148000 };
        int[] wayZ = { -3404, -3402, -3400 };

        for (int i = 0; i < NETWORK_WARMUP_ITERATIONS; i++) {
            buffer.clear();

            // Simulação 1: CharMoveToLocation (opcode 0x01: 29B)
            buffer.put((byte) 0x01);
            buffer.putInt(10001);
            buffer.putInt(83400 + i);
            buffer.putInt(147943);
            buffer.putInt(-3404);
            buffer.putInt(83500);
            buffer.putInt(148000);
            buffer.putInt(-3404);

            // Simulação 2: Attack (opcode 0x05)
            buffer.clear();
            buffer.put((byte) 0x05);
            buffer.putInt(10001);
            buffer.putInt(20002);
            buffer.putInt(150);
            buffer.put((byte) 1);
            buffer.putInt(83400);
            buffer.putInt(147943);
            buffer.putInt(-3404);

            // Simulação 3: Trajetória binária Cap'n Proto
            buffer.clear();
            PathTrajectoryBinary.serialize(buffer, 83400, 147943, -3404, 83500, 148000, -3400, 150.0f, wayX, wayY, wayZ, 3);
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Serialização de Rede    : {} frames S2C codificados em {} ms",
            NETWORK_WARMUP_ITERATIONS, elapsed);
    }

    /**
     * 5. Saturação de Criptografia Blowfish (20.000 blocos): cifra e decifra pacotes reais
     * para aquecer os vetores de chaves de criptografia e S-Boxes.
     */
    private static void warmupCryptEngine() {
        long start = System.currentTimeMillis();
        GameCrypt<Object, byte[]> crypt = new GameCrypt<Object, byte[]>() {};
        byte[] dummyKey = new byte[] {
            (byte) 0x94, (byte) 0x35, (byte) 0x00, (byte) 0x00,
            (byte) 0xa1, (byte) 0x6c, (byte) 0x54, (byte) 0x87,
            (byte) 0x55, (byte) 0xaa, (byte) 0x12, (byte) 0x34,
            (byte) 0x78, (byte) 0x90, (byte) 0xab, (byte) 0xcd
        };
        crypt.setKey(dummyKey);

        byte[] packetBytes = new byte[64];
        for (int i = 0; i < packetBytes.length; i++) {
            packetBytes[i] = (byte) (i ^ 0x55);
        }

        for (int i = 0; i < CRYPT_WARMUP_ITERATIONS; i++) {
            crypt.encrypt(packetBytes, 0, packetBytes.length);
            crypt.decrypt(packetBytes, 0, packetBytes.length);
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Criptografia Blowfish   : {} blocos processados em {} ms",
            CRYPT_WARMUP_ITERATIONS, elapsed);
    }

    /**
     * 6. Saturação do Motor de Skills e Fórmulas de Combate (50.000 iterações):
     * Exercita lookups O(1) em SkillTable, avaliação de templates de efeitos,
     * consumo de MP, alcance de cast, reuse delay e cálculos de skill.
     */
    private static void warmupSkillsEngine() {
        long start = System.currentTimeMillis();
        l2.gameserver.tables.SkillTable table = l2.gameserver.tables.SkillTable.getInstance();

        long sink = 0;
        int resolvedSkills = 0;

        // 6.1 Saturação exaustiva: itera por TODAS as skills cadastradas no servidor
        java.util.Map<Integer, java.util.Map<Integer, l2.gameserver.model.Skill>> allSkills = table.getSkills();
        int totalLoaded = 0;
        if (allSkills != null) {
            for (java.util.Map<Integer, l2.gameserver.model.Skill> levels : allSkills.values()) {
                if (levels != null) {
                    for (l2.gameserver.model.Skill sk : levels.values()) {
                        if (sk != null) {
                            totalLoaded++;
                            sink += sk.getId();
                            sink += sk.getLevel();
                            sink += sk.getMpConsume();
                            sink += sk.getCastRange();
                            sink += sk.getHitTime();
                            sink += sk.getReuseDelay();
                            sink += sk.getEffectTemplates().length;
                            if (sk.hasEffects()) sink++;
                            if (sk.isActive()) sink++;
                            if (sk.isToggle()) sink++;
                            if (sk.isPassive()) sink++;
                            if (sk.isOffensive()) sink++;
                        }
                    }
                }
            }
        }

        // 6.2 Saturação de alta frequência (50.000 iterações): lookups de combate PvP/PvE
        int[] sampleSkillIds = {
            1230, // Prominence
            1235, // Hydro Blast
            1239, // Hurricane
            1265, // Solar Flare
            1164, // Curse Death Link
            1068, // Might
            1040, // Shield
            1086, // Haste
            1062, // Berserker Spirit
            1085, // Acumen
            1077, // Focus
            1078, // Concentration
            1087, // Agility
            1204, // Wind Walk
            263,  // Deadly Blow
            19,   // Double Shot
            28,   // Aggression
            18,   // Power Strike
            92,   // Shield Stun
            1064, // Silence
            1160, // Slow
            1201  // Dryad Root
        };

        for (int i = 0; i < SKILLS_WARMUP_ITERATIONS; i++) {
            int skillId = sampleSkillIds[i % sampleSkillIds.length];
            int maxLevel = table.getMaxLevel(skillId);
            int level = maxLevel > 0 ? ((i % maxLevel) + 1) : 1;

            l2.gameserver.model.Skill skill = table.getInfo(skillId, level);
            if (skill != null) {
                resolvedSkills++;
                sink += skill.getId();
                sink += skill.getLevel();
                sink += skill.getMpConsume();
                sink += skill.getCastRange();
                sink += skill.getHitTime();
                sink += skill.getReuseDelay();
                sink += skill.getEffectTemplates().length;
                if (skill.hasEffects()) {
                    sink++;
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Motor de Skills e Efeitos : {} skills totais percorridas + {} lookups intensivos em {} ms (sink={})",
            totalLoaded, SKILLS_WARMUP_ITERATIONS, elapsed, sink);
    }

    /**
     * 7. Saturação de Fórmulas de Combate (50.000 iterações):
     * Exercita cálculos de velocidade de ataque físico/mágico, stun break e regras de combate.
     */
    private static void warmupCombatFormulas() {
        long start = System.currentTimeMillis();
        long sink = 0;

        for (int i = 0; i < 50_000; i++) {
            double pAtkSpdInput = 300.0 + (i % 1200);
            int patkSpd = l2.gameserver.stats.Formulas.calcPAtkSpd(pAtkSpdInput);
            sink += patkSpd;

            boolean stunBreak = l2.gameserver.stats.Formulas.calcStunBreak((i & 1) == 0);
            if (stunBreak) sink++;
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Fórmulas de Combate      : 50000 cálculos executados em {} ms (sink={})", elapsed, sink);
    }

    /**
     * 8. Saturação de Templates de Itens e Inventário (50.000 iterações):
     * Exercita lookups O(1) em ItemHolder, leitura de graus de cristal, peso e slots.
     */
    private static void warmupItemsAndInventory() {
        long start = System.currentTimeMillis();
        l2.gameserver.data.xml.holder.ItemHolder itemHolder = l2.gameserver.data.xml.holder.ItemHolder.getInstance();
        int[] sampleItemIds = { 57, 1, 2, 6373, 6378, 6611, 6617, 6618, 6619, 6620, 6621, 6580, 6590, 1419, 7575 };
        long sink = 0;
        int resolvedItems = 0;

        for (int i = 0; i < 50_000; i++) {
            int itemId = sampleItemIds[i % sampleItemIds.length];
            l2.gameserver.templates.item.ItemTemplate tpl = itemHolder.getTemplate(itemId);
            if (tpl != null) {
                resolvedItems++;
                sink += tpl.getItemId();
                sink += tpl.getWeight();
                sink += tpl.getReferencePrice();
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Templates de Itens        : 50000 lookups executados em {} ms (resolvidos={}, sink={})",
            elapsed, resolvedItems, sink);
    }

    /**
     * 9. Saturação dos Novos Subsistemas (Heurística Octile 3D, Vetores Ortogonais, Debounce e Dirty Flags):
     * Força inlining e vetorização SIMD (NEON/AVX) nos algoritmos recém-implementados.
     */
    private static void warmupNewSubsystemsEngine() {
        long start = System.currentTimeMillis();
        long sink = 0;

        // 9.1 Octile 3D Heuristic (100.000 iterações)
        for (int i = 0; i < 100_000; i++) {
            int dx = i % 500;
            int dy = (i * 2) % 500;
            int dz = (i % 64);
            sink += l2.gameserver.geodata.GeoMath.heuristicOctile3D(dx, dy, dz);
        }

        // 9.2 Vetores Ortogonais e Colinearidade (50.000 iterações)
        for (int i = 0; i < 50_000; i++) {
            int nx = 83400 + (i % 200);
            int ny = 147943 + (i % 200);
            int tx = 83400;
            int ty = 147943;
            var v = l2.gameserver.ai.util.RepositioningMath.computeOrthogonalVectors(nx, ny, tx, ty, i % 65536);
            sink += (long) (v.ux * 1000 + v.leftX * 1000);
        }

        // 9.3 SkillFloodTracker (50.000 iterações)
        l2.gameserver.model.SkillFloodTracker tracker = new l2.gameserver.model.SkillFloodTracker();
        for (int i = 0; i < 50_000; i++) {
            boolean seen = tracker.tryMarkCastPacketSeen(1015, 12345, 250L, i * 10L);
            if (seen) sink++;
            boolean suppress = tracker.shouldSuppressActionFailed(1015, 500L, i * 10L);
            if (suppress) sink++;
        }

        // 9.4 PlayerDirtyFlags Bitmask (50.000 iterações)
        for (int i = 0; i < 50_000; i++) {
            int flags = l2.gameserver.model.state.PlayerDirtyFlags.DIRTY_COORDINATES
                      | l2.gameserver.model.state.PlayerDirtyFlags.DIRTY_VITALS
                      | l2.gameserver.model.state.PlayerDirtyFlags.DIRTY_STATS;
            if ((flags & l2.gameserver.model.state.PlayerDirtyFlags.DIRTY_COORDINATES) != 0) sink++;
            if ((flags & l2.gameserver.model.state.PlayerDirtyFlags.DIRTY_ALL) != 0) sink++;
        }

        long elapsed = System.currentTimeMillis() - start;
        _log.info("  -> Novos Subsistemas (SIMD)  : 250000 operações executadas em {} ms (sink={})", elapsed, sink);
    }
}