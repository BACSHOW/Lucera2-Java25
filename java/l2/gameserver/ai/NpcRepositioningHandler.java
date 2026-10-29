package l2.gameserver.ai;

import java.util.concurrent.ThreadLocalRandom;
import l2.gameserver.ai.util.RepositioningMath;
import l2.gameserver.ai.util.RepositioningMath.OrthogonalVectors;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

/**
 * Coordena o reposicionamento tático de NPCs após ataque/cast bem-sucedido.
 *
 * Monstros de longo alcance (Rangers, Mystics, arqueiros e magos) flanqueiam e recuam
 * de forma dinâmica em vez de ficarem paralisados como estátuas em um ponto fixo.
 */
public final class NpcRepositioningHandler {

    /** Recuo frontal reto padrão quando o NPC está colado em melee (< 200u). */
    private static final double FRONTAL_RETREAT_DIST = 380.0;

    /** Strafe lateral de flanqueamento quando em alcance de combate (140u). */
    private static final double LATERAL_STRAFE_DIST = 140.0;

    /** Distância de fallback perpendicular lateral (180u). */
    private static final double LATERAL_FALLBACK_DIST = 180.0;

    /** Limiar de proximidade excessiva para arqueiros/magos. */
    private static final double TOO_CLOSE_THRESHOLD = 200.0;

    /** Margem sobre o alcance máximo. */
    private static final int ATTACK_RANGE_SLACK = 20;

    private NpcRepositioningHandler() {}

    /**
     * Tenta reposicionar o NPC taticamente em relação ao seu alvo.
     *
     * @param npc         NPC atacante
     * @param target      Alvo atual
     * @param attackRange Alcance do golpe/skill
     * @return true se o movimento de reposicionamento foi despachado com sucesso
     */
    public static boolean tryReposition(NpcInstance npc, Creature target, int attackRange) {
        if (npc == null || target == null) {
            return false;
        }
        if (npc.isMovementDisabled() || npc.isParalyzed() || npc.isAlikeDead() || target.isAlikeDead()) {
            return false;
        }
        if (npc.isMoving() || npc.isAttackingNow() || npc.isCastingNow()) {
            return false;
        }

        // Apenas monstros de combate à distância reposicionam (arqueiros/magos)
        if (npc.getPhysicalAttackRange() <= 200) {
            return false;
        }

        int dist = (int) npc.getDistance(target);
        int collisionBuffer = (int) (npc.getColRadius() + target.getColRadius());

        OrthogonalVectors v = RepositioningMath.computeOrthogonalVectors(
                npc.getX(), npc.getY(), target.getX(), target.getY(), npc.getHeading()
        );
        Location currentLoc = npc.getLoc();
        int currentZ = currentLoc.z;
        int geoIndex = npc.getGeoIndex();

        // 1. Caso 'Too Close': Se o alvo grudou no arqueiro/mago (< 200u), tenta recuo frontal com fallbacks laterais
        if (dist < TOO_CLOSE_THRESHOLD) {
            // Fallback 1: Recuo frontal reto (380u)
            int rx = (int) (target.getX() + FRONTAL_RETREAT_DIST * v.ux);
            int ry = (int) (target.getY() + FRONTAL_RETREAT_DIST * v.uy);
            int rz = GeoEngine.getHeight(rx, ry, currentZ, geoIndex);
            if (GeoEngine.canMoveToCoord(currentLoc.x, currentLoc.y, currentZ, rx, ry, rz, geoIndex)) {
                npc.moveToLocation(rx, ry, rz, 0, false);
                return true;
            }

            // Fallback 2: Perpendicular lateral esquerdo (180u)
            int lx = (int) (currentLoc.x + LATERAL_FALLBACK_DIST * v.leftX);
            int ly = (int) (currentLoc.y + LATERAL_FALLBACK_DIST * v.leftY);
            int lz = GeoEngine.getHeight(lx, ly, currentZ, geoIndex);
            if (GeoEngine.canMoveToCoord(currentLoc.x, currentLoc.y, currentZ, lx, ly, lz, geoIndex)) {
                npc.moveToLocation(lx, ly, lz, 0, false);
                return true;
            }

            // Fallback 3: Perpendicular lateral direito (180u)
            int r2x = (int) (currentLoc.x + LATERAL_FALLBACK_DIST * v.rightX);
            int r2y = (int) (currentLoc.y + LATERAL_FALLBACK_DIST * v.rightY);
            int r2z = GeoEngine.getHeight(r2x, r2y, currentZ, geoIndex);
            if (GeoEngine.canMoveToCoord(currentLoc.x, currentLoc.y, currentZ, r2x, r2y, r2z, geoIndex)) {
                npc.moveToLocation(r2x, r2y, r2z, 0, false);
                return true;
            }
            return false;
        }

        // 2. Se o alvo estiver longe demais além do alcance de ataque, deixa a IA perseguidora atuar
        if (dist > attackRange + collisionBuffer + ATTACK_RANGE_SLACK) {
            return false;
        }

        // 3. Caso em alcance ótimo: Flanqueamento lateral evasivo alternado (140u)
        boolean useLeft = ThreadLocalRandom.current().nextBoolean();
        double sideX = useLeft ? v.leftX : v.rightX;
        double sideY = useLeft ? v.leftY : v.rightY;

        int sx = (int) (currentLoc.x + LATERAL_STRAFE_DIST * sideX);
        int sy = (int) (currentLoc.y + LATERAL_STRAFE_DIST * sideY);
        int sz = GeoEngine.getHeight(sx, sy, currentZ, geoIndex);
        if (GeoEngine.canMoveToCoord(currentLoc.x, currentLoc.y, currentZ, sx, sy, sz, geoIndex)) {
            npc.moveToLocation(sx, sy, sz, 0, false);
            return true;
        }

        // Fallback para o lado oposto se o primeiro lado estiver bloqueado
        double altX = useLeft ? v.rightX : v.leftX;
        double altY = useLeft ? v.rightY : v.leftY;
        int ax = (int) (currentLoc.x + LATERAL_STRAFE_DIST * altX);
        int ay = (int) (currentLoc.y + LATERAL_STRAFE_DIST * altY);
        int az = GeoEngine.getHeight(ax, ay, currentZ, geoIndex);
        if (GeoEngine.canMoveToCoord(currentLoc.x, currentLoc.y, currentZ, ax, ay, az, geoIndex)) {
            npc.moveToLocation(ax, ay, az, 0, false);
            return true;
        }

        return false;
    }
}
