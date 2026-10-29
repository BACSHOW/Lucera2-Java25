package l2.gameserver.geodata.pathfinding.serialization;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

enum EdgeType {
    WALK, JUMP, CLIMB, FALL
}

enum SourceType {
    L2J, CONV_DAT, MIXED
}

class Edge {
    private final int targetId;
    private final double cost;
    private final EdgeType type;

    public Edge(int targetId, double cost, EdgeType type) {
        this.targetId = targetId;
        this.cost = cost;
        this.type = type;
    }

    public int getTargetId() { return targetId; }
    public double getCost() { return cost; }
    public EdgeType getType() { return type; }
}

class Connection {
    private final int from;
    private final int to;
    private final double cost;
    private final EdgeType type;

    public Connection(int from, int to, double cost, EdgeType type) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.type = type;
    }

    public int getFrom() { return from; }
    public int getTo() { return to; }
    public double getCost() { return cost; }
    public EdgeType getType() { return type; }
}

class NodeConnectivity {
    private final List<Connection> edges;

    public NodeConnectivity(List<Connection> edges) {
        this.edges = edges != null ? edges : Collections.emptyList();
    }

    public List<Connection> getEdges() { return edges; }
}

class Node {
    private final int x;
    private final int y;
    private final int z;
    private final int originalId;
    private final boolean walkable;
    private final boolean cellEdge;
    private final List<Edge> edges;

    public Node(int x, int y, int z, int originalId, boolean walkable, boolean cellEdge, List<Edge> edges) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.originalId = originalId;
        this.walkable = walkable;
        this.cellEdge = cellEdge;
        this.edges = edges != null ? edges : Collections.emptyList();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getOriginalId() { return originalId; }
    public boolean isWalkable() { return walkable; }
    public boolean isCellEdge() { return cellEdge; }
    public List<Edge> getEdges() { return edges; }
}

class SpatialGrid {
    private final int cellSize;
    private final int width;
    private final int height;
    private final IntArrayList[][] cells;

    public SpatialGrid(int cellSize, int width, int height) {
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;
        this.cells = new IntArrayList[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.cells[x][y] = new IntArrayList();
            }
        }
    }

    public int getCellSize() { return cellSize; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void addNode(int cellX, int cellY, int nodeId) {
        if (cellX >= 0 && cellX < width && cellY >= 0 && cellY < height) {
            cells[cellX][cellY].add(nodeId);
        }
    }

    public IntArrayList getCell(int cellX, int cellY) {
        if (cellX >= 0 && cellX < width && cellY >= 0 && cellY < height) {
            return cells[cellX][cellY];
        }
        return new IntArrayList();
    }
}

class IndexedRegion {
    private final int regionX;
    private final int regionY;
    private final Map<Integer, Node> nodes;
    private final int walkableCount;
    private final SpatialGrid spatialGrid;
    private final NodeConnectivity connectivity;
    private final SourceType sourceType;
    private final long generationTimeMs;

    public IndexedRegion(int regionX, int regionY, Map<Integer, Node> nodes, int walkableCount,
                         SpatialGrid spatialGrid, NodeConnectivity connectivity, SourceType sourceType, long generationTimeMs) {
        this.regionX = regionX;
        this.regionY = regionY;
        this.nodes = nodes;
        this.walkableCount = walkableCount;
        this.spatialGrid = spatialGrid;
        this.connectivity = connectivity;
        this.sourceType = sourceType;
        this.generationTimeMs = generationTimeMs;
    }

    public int getRegionX() { return regionX; }
    public int getRegionY() { return regionY; }
    public Map<Integer, Node> getNodes() { return nodes; }
    public int getWalkableCount() { return walkableCount; }
    public SpatialGrid getSpatialGrid() { return spatialGrid; }
    public NodeConnectivity getConnectivity() { return connectivity; }
    public SourceType getSourceType() { return sourceType; }
    public long getGenerationTimeMs() { return generationTimeMs; }
}
