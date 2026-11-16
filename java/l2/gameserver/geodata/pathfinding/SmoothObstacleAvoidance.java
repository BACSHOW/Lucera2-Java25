package l2.gameserver.geodata.pathfinding;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.utils.Location;

/**
 * Suavizacao de quinas para NPCs e monstros.
 *
 * O caminho calculado encosta na quina do obstaculo e vira de uma vez: o corpo do monstro raspa na
 * parede (trava no canto) e a sequencia de viradas secas aparece como ziguezague. Aqui cada quina
 * (ponto P entre A e B) vira tres pontos:
 *
 *   E = entrada, ainda no trecho A-P, a "cut" unidades de P
 *   O = P afastado da parede, para o lado de fora da curva
 *   X = saida, ja no trecho P-B, a "cut" unidades de P
 *
 * Cada quina so e trocada se E-O e O-X forem percorriveis no geodata (com colisao e desnivel).
 * Se nao forem, tenta meio afastamento; se ainda nao, a quina fica exatamente como estava.
 */
public final class SmoothObstacleAvoidance {
    /** Abaixo deste angulo de virada a quina nao e tocada (cos 20 graus). */
    private static final double STRAIGHT_COS = 0.9397;
    /** Trechos menores que isto (2 celulas) nao comportam pontos de entrada/saida. */
    private static final int MIN_LEG = 32;

    private SmoothObstacleAvoidance() {
    }

    /**
     * @param path pontos do caminho em coordenadas de mundo (inicio, quinas, destino)
     * @return a mesma lista se nada mudou, ou uma lista nova com as quinas suavizadas
     */
    public static List<Location> smooth(List<Location> path, int geoIndex) {
        int size = path == null ? 0 : path.size();
        if (size < 3) {
            return path;
        }
        int offset = Config.SMOOTH_OBSTACLE_OFFSET;
        int cut = Config.SMOOTH_OBSTACLE_CUT;
        if (offset <= 0 || cut <= 0) {
            return path;
        }

        List<Location> out = new ArrayList<>(size + 2 * (size - 2));
        out.add(path.get(0));
        boolean changed = false;

        for (int i = 1; i < size - 1; i++) {
            Location a = path.get(i - 1);
            Location p = path.get(i);
            Location b = path.get(i + 1);

            double inX = p.x - a.x, inY = p.y - a.y;
            double outX = b.x - p.x, outY = b.y - p.y;
            double inLen = Math.sqrt(inX * inX + inY * inY);
            double outLen = Math.sqrt(outX * outX + outY * outY);
            if (inLen < MIN_LEG || outLen < MIN_LEG) {
                out.add(p);
                continue;
            }
            inX /= inLen; inY /= inLen;
            outX /= outLen; outY /= outLen;
            if (inX * outX + inY * outY > STRAIGHT_COS) {
                out.add(p);
                continue;
            }

            // Lado de fora da curva: o obstaculo contornado fica do lado de dentro.
            double awayX = inX - outX, awayY = inY - outY;
            double awayLen = Math.sqrt(awayX * awayX + awayY * awayY);
            if (awayLen < 1.0E-6) {
                out.add(p);
                continue;
            }
            awayX /= awayLen; awayY /= awayLen;

            // No maximo metade de cada trecho, para a saida desta quina nao passar da entrada da proxima.
            double c = Math.min(cut, Math.min(inLen, outLen) / 2.0);
            Location e = at(p.x - inX * c, p.y - inY * c, p.z, geoIndex);
            Location x = at(p.x + outX * c, p.y + outY * c, p.z, geoIndex);

            Location o = null;
            for (int off = offset; off >= 8 && o == null; off /= 2) {
                Location cand = at(p.x + awayX * off, p.y + awayY * off, p.z, geoIndex);
                if (walkable(e, cand, geoIndex) && walkable(cand, x, geoIndex)) {
                    o = cand;
                }
            }
            if (o == null) {
                out.add(p);
                continue;
            }
            out.add(e);
            out.add(o);
            out.add(x);
            changed = true;
        }

        out.add(path.get(size - 1));
        return changed ? out : path;
    }

    private static Location at(double x, double y, int z, int geoIndex) {
        int ix = (int) Math.round(x);
        int iy = (int) Math.round(y);
        return new Location(ix, iy, GeoEngine.getHeight(ix, iy, z, geoIndex));
    }

    private static boolean walkable(Location from, Location to, int geoIndex) {
        if (Math.abs(from.z - to.z) > Config.PATHFIND_MAX_Z_DIFF * 2) {
            return false;
        }
        return GeoEngine.canMoveWithCollision(from.x, from.y, from.z, to.x, to.y, to.z, geoIndex);
    }
}
