package l2.gameserver.ai.util;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.utils.Location;

/**
 * Matemática pura para reposicionamento tático pós-ataque de NPCs e simplificação de caminhos de pathfinding.
 *
 * - Vetores ortogonais derivados analiticamente sem atan2/sin/cos (custo de CPU praticamente zero).
 * - Supressão de nós colineares via cross-product inteiro O(n).
 * - String-pulling para colapsar nós intermediários quando há linha direta de visão.
 */
public final class RepositioningMath {

    private RepositioningMath() {}

    public static final class OrthogonalVectors {
        public final double ux;     // vetor unitário na direção de recuo (longe do alvo)
        public final double uy;
        public final double leftX;  // perpendicular à esquerda (+90°)
        public final double leftY;
        public final double rightX; // perpendicular à direita (-90°)
        public final double rightY;
        public final double rawDistance;
        public final boolean degenerate; // true se o NPC estiver na mesma coordenada exata do alvo

        OrthogonalVectors(double ux, double uy, double lx, double ly, double rx, double ry,
                          double rawDist, boolean degenerate) {
            this.ux = ux;
            this.uy = uy;
            this.leftX = lx;
            this.leftY = ly;
            this.rightX = rx;
            this.rightY = ry;
            this.rawDistance = rawDist;
            this.degenerate = degenerate;
        }
    }

    /**
     * Calcula vetor unitário de recuo (apontando do alvo para o NPC) e seus dois
     * perpendiculares analiticamente sem Math.atan2.
     *
     * Fórmula analítica:
     *   u = (dx, dy) / hypot(dx, dy)              // vetor recuo
     *   perp_left  = (-uy,  ux)                    // rotação +90°
     *   perp_right = ( uy, -ux)                    // rotação -90°
     */
    public static OrthogonalVectors computeOrthogonalVectors(int npcX, int npcY, int targetX,
                                                            int targetY, int npcHeading) {
        double dx = (double) (npcX - targetX);
        double dy = (double) (npcY - targetY);
        double rawDist = Math.hypot(dx, dy);

        double ux, uy;
        boolean degenerate = false;
        if (rawDist > 1e-3) {
            ux = dx / rawDist;
            uy = dy / rawDist;
        } else {
            degenerate = true;
            double headingRad = Math.toRadians(((double) npcHeading) * (360.0 / 65535.0));
            ux = Math.cos(headingRad);
            uy = Math.sin(headingRad);
            rawDist = 0.0;
        }
        double leftX = -uy;
        double leftY = ux;
        double rightX = uy;
        double rightY = -ux;
        return new OrthogonalVectors(ux, uy, leftX, leftY, rightX, rightY, rawDist, degenerate);
    }

    /**
     * Dhousefe-style Boids Crowd Separation Force.
     * Prevents overlapping NPCs from stalling each other with premature _blocked=true.
     * Distributes overlapping entities across 8 radial octants deterministically based on objectId.
     */
    public static double[] computeBoidsSeparationForce(int actorId, int actorX, int actorY, double actorRadius,
                                                       int otherX, int otherY, double otherRadius,
                                                       double maxForce, double checkRadius) {
        double dx = (double) (actorX - otherX);
        double dy = (double) (actorY - otherY);
        double distSq = dx * dx + dy * dy;

        if (distSq >= checkRadius * checkRadius) {
            return new double[]{0.0, 0.0};
        }

        if (distSq <= 0.1) {
            // Exact overlap on spawn/teleport: scatter radially across 8 angles
            double angle = (actorId % 8) * (Math.PI / 4.0);
            return new double[]{Math.cos(angle) * maxForce, Math.sin(angle) * maxForce};
        }

        double dist = Math.sqrt(distSq);
        double combinedRadius = actorRadius + otherRadius;
        if (dist < combinedRadius + 10.0) {
            double overlap = (combinedRadius + 10.0) - dist;
            double force = (overlap / (combinedRadius + 10.0)) * maxForce;
            return new double[]{(dx / dist) * force, (dy / dist) * force};
        }

        return new double[]{0.0, 0.0};
    }

    /**
     * Verifica se três pontos são colineares via cross product em precisão inteira.
     */
    public static boolean isCollinear(Location p0, Location p1, Location p2) {
        if ((p0.x == p2.x && p2.x == p1.x) || (p0.y == p2.y && p2.y == p1.y)) {
            return true;
        }
        long dx1 = (long) (p0.x - p1.x);
        long dy1 = (long) (p0.y - p1.y);
        long dx2 = (long) (p1.x - p2.x);
        long dy2 = (long) (p1.y - p2.y);
        return dx1 * dy2 == dx2 * dy1;
    }

    /**
     * Suprime nós colineares de um caminho em O(n) com janela deslizante de 3 pontos.
     */
    public static List<Location> suppressCollinear(List<Location> path) {
        if (path == null || path.size() <= 2) {
            return path;
        }
        List<Location> out = new ArrayList<>(path);
        int size = out.size();
        int i = 2;
        while (i < size) {
            Location p0 = out.get(i - 2);
            Location p1 = out.get(i - 1);
            Location p2 = out.get(i);
            if (p0.equals(p1) || p1.equals(p2) || isCollinear(p0, p1, p2)) {
                out.remove(i - 1);
                size--;
                i = Math.max(2, i - 1);
            } else {
                i++;
            }
        }
        return out;
    }

    /**
     * String-pulling: colapsa waypoints intermediários quando há linha direta de visão transitável.
     */
    public static List<Location> stringPull(List<Location> path, LineOfSightFn lineOfSight) {
        if (path == null || path.size() <= 2 || lineOfSight == null) {
            return path;
        }
        List<Location> out = new ArrayList<>(path);
        int startIdx = 0;
        while (startIdx < out.size() - 2) {
            Location start = out.get(startIdx);
            int endIdx = out.size() - 1;
            while (endIdx > startIdx + 1) {
                Location end = out.get(endIdx);
                if (lineOfSight.test(start, end)) {
                    while (startIdx + 1 < endIdx) {
                        out.remove(startIdx + 1);
                        endIdx--;
                    }
                    break;
                }
                endIdx--;
            }
            startIdx++;
        }
        return out;
    }

    @FunctionalInterface
    public interface LineOfSightFn {
        boolean test(Location from, Location to);
    }
}
