package l2.gameserver.geodata.pathfinding.serialization;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.*;

/**
 * L2BRSerializer — Compactador e serializador de grafos de Geodata / Pathfinding usando Zstandard (Zstd Level 3).
 * Baseado na arquitetura de Mechanical Sympathy do Dhousefe.
 */
public final class L2BRSerializer {
    public static final int HEADER_SIZE = 64;
    public static final int INDEX_ENTRY_SIZE = 32;
    public static final int ZSTD_LEVEL = 3;
    public static final long MAGIC = 0x4C32425276350000L; // 'L2BRv5'
    public static final short VERSION = 5;

    public void serialize(IndexedRegion region, Path outputPath) throws IOException {
        File outFile = outputPath.toFile();
        File parent = outFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        int nodeCount = region.getNodes().size();
        TreeMap<Integer, Node> sortedNodes = new TreeMap<>(region.getNodes());

        if (!sortedNodes.isEmpty() && (sortedNodes.firstKey() != 0 || sortedNodes.lastKey() != nodeCount - 1)) {
            throw new IOException("IDs dos nós não são sequenciais de 0 a " + (nodeCount - 1));
        }

        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        NodeIndexEntry[] indexEntries = new NodeIndexEntry[nodeCount];

        try (DataOutputStream dataStream = new DataOutputStream(new BufferedOutputStream(new ZstdOutputStream(dataBuffer, ZSTD_LEVEL)))) {
            dataStream.writeInt(region.getRegionX());
            dataStream.writeInt(region.getRegionY());
            dataStream.writeInt(region.getSourceType().ordinal());
            dataStream.writeLong(region.getGenerationTimeMs());
            dataStream.writeInt(nodeCount);

            for (Map.Entry<Integer, Node> entry : sortedNodes.entrySet()) {
                int index = entry.getKey();
                Node node = entry.getValue();

                int offset = dataStream.size();
                writeNode(dataStream, node);

                indexEntries[index] = new NodeIndexEntry(
                        offset,
                        (short) node.getX(),
                        (short) node.getY(),
                        (short) node.getZ(),
                        node.getOriginalId(),
                        buildFlags(node),
                        (short) node.getEdges().size()
                );
            }

            writeSpatialGrid(dataStream, region.getSpatialGrid());
            writeConnectivity(dataStream, region.getConnectivity());
        }

        byte[] compressedData = dataBuffer.toByteArray();
        int indexSize = nodeCount * INDEX_ENTRY_SIZE;
        long dataOffset = (long) HEADER_SIZE + indexSize;
        long expectedFileSize = dataOffset + compressedData.length;

        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            ByteBuffer headerBuffer = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN);
            headerBuffer.putLong(MAGIC);
            headerBuffer.putShort(VERSION);
            headerBuffer.putShort((short) region.getRegionX());
            headerBuffer.putShort((short) region.getRegionY());
            headerBuffer.putShort((short) 0); // padding
            headerBuffer.putInt(nodeCount);
            headerBuffer.putInt(region.getWalkableCount());
            headerBuffer.putLong(HEADER_SIZE);
            headerBuffer.putLong(dataOffset);
            headerBuffer.putLong(calculateChecksum(compressedData));
            headerBuffer.putInt(compressedData.length);
            headerBuffer.putInt(INDEX_ENTRY_SIZE);

            fos.write(headerBuffer.array());

            ByteBuffer indexBuffer = ByteBuffer.allocate(indexSize).order(ByteOrder.LITTLE_ENDIAN);
            for (NodeIndexEntry entry : indexEntries) {
                if (entry == null) {
                    throw new IOException("Entry nulo no índice");
                }
                indexBuffer.putLong(entry.offset);
                indexBuffer.putInt(entry.originalId);
                indexBuffer.putShort(entry.x);
                indexBuffer.putShort(entry.y);
                indexBuffer.putShort(entry.z);
                indexBuffer.put(entry.flags);
                indexBuffer.putShort(entry.edgeCount);
                // 11 bytes de padding para completar 32 bytes por entry
                indexBuffer.put(new byte[11]);
            }
            fos.write(indexBuffer.array());
            fos.write(compressedData);
            fos.flush();
        }

        if (outFile.length() != expectedFileSize) {
            throw new IOException("Tamanho do arquivo incorreto: esperado " + expectedFileSize + ", real " + outFile.length());
        }
    }

    public IndexedRegion deserialize(Path inputPath) throws IOException {
        File file = inputPath.toFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] headerBytes = new byte[HEADER_SIZE];
            if (fis.read(headerBytes) != HEADER_SIZE) {
                throw new IOException("Header incompleto");
            }

            ByteBuffer header = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);
            long magic = header.getLong();
            if (magic != MAGIC) {
                throw new IOException("Formato inválido: 0x" + Long.toHexString(magic));
            }
            short version = header.getShort();
            if (version != VERSION) {
                throw new IOException("Versão não suportada: " + version);
            }

            int regionX = header.getShort();
            int regionY = header.getShort();
            header.getShort(); // skip padding
            int nodeCount = header.getInt();
            int walkableCount = header.getInt();
            long indexOffset = header.getLong();
            long dataOffset = header.getLong();
            long checksum = header.getLong();
            int compressedSize = header.getInt();
            int indexEntrySize = header.getInt();

            long skipBytes = dataOffset - HEADER_SIZE;
            long skipped = fis.skip(skipBytes);
            if (skipped != skipBytes) {
                throw new IOException("Falha ao pular índice");
            }

            byte[] compressedData = new byte[compressedSize];
            int readBytes = fis.read(compressedData);
            if (readBytes != compressedSize) {
                throw new IOException("Dados comprimidos incompletos");
            }

            if (calculateChecksum(compressedData) != checksum) {
                throw new IOException("Checksum Zstd inválido");
            }

            try (DataInputStream dataStream = new DataInputStream(new BufferedInputStream(new ZstdInputStream(new ByteArrayInputStream(compressedData))))) {
                int readRegionX = dataStream.readInt();
                int readRegionY = dataStream.readInt();
                int srcOrdinal = dataStream.readInt();
                SourceType sourceType = SourceType.values()[Math.clamp(srcOrdinal, 0, SourceType.values().length - 1)];
                long genTime = dataStream.readLong();

                Map<Integer, Node> nodes = readNodes(dataStream, nodeCount);
                SpatialGrid spatialGrid = readSpatialGrid(dataStream);
                NodeConnectivity connectivity = readConnectivity(dataStream);

                return new IndexedRegion(
                        readRegionX,
                        readRegionY,
                        nodes,
                        walkableCount,
                        spatialGrid,
                        connectivity,
                        sourceType,
                        genTime
                );
            }
        }
    }

    private static byte buildFlags(Node node) {
        byte flags = 0;
        if (node.isWalkable()) flags |= 0x01;
        if (node.isCellEdge()) flags |= 0x02;
        return flags;
    }

    private static void writeNode(DataOutputStream stream, Node node) throws IOException {
        stream.writeShort(node.getX());
        stream.writeShort(node.getY());
        stream.writeShort(node.getZ());
        stream.writeInt(node.getOriginalId());
        stream.writeByte(buildFlags(node));
        stream.writeShort(node.getEdges().size());
        for (Edge edge : node.getEdges()) {
            stream.writeInt(edge.getTargetId());
            stream.writeShort((int) (edge.getCost() * 10.0));
            stream.writeByte(edge.getType().ordinal());
        }
    }

    private static Map<Integer, Node> readNodes(DataInputStream stream, int expectedCount) throws IOException {
        int count = stream.readInt();
        if (count != expectedCount) {
            throw new IOException("Contagem inconsistente de nós: " + count + " vs " + expectedCount);
        }

        Int2ObjectOpenHashMap<Node> nodes = new Int2ObjectOpenHashMap<>(count);
        for (int i = 0; i < count; i++) {
            int x = stream.readShort();
            int y = stream.readShort();
            int z = stream.readShort();
            int originalId = stream.readInt();
            byte flags = stream.readByte();
            boolean isWalkable = (flags & 0x01) != 0;
            boolean isCellEdge = (flags & 0x02) != 0;

            int edgeCount = stream.readShort();
            List<Edge> edges = new ArrayList<>(edgeCount);
            for (int e = 0; e < edgeCount; e++) {
                int targetId = stream.readInt();
                double cost = stream.readShort() / 10.0;
                int typeOrdinal = stream.readByte();
                EdgeType type = EdgeType.values()[Math.clamp(typeOrdinal, 0, EdgeType.values().length - 1)];
                edges.add(new Edge(targetId, cost, type));
            }
            nodes.put(i, new Node(x, y, z, originalId, isWalkable, isCellEdge, edges));
        }
        return nodes;
    }

    private static void writeSpatialGrid(DataOutputStream stream, SpatialGrid grid) throws IOException {
        stream.writeInt(grid.getCellSize());
        stream.writeInt(grid.getWidth());
        stream.writeInt(grid.getHeight());

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                var cellNodes = grid.getCell(x, y);
                stream.writeShort(cellNodes.size());
                for (int i = 0; i < cellNodes.size(); i++) {
                    stream.writeInt(cellNodes.getInt(i));
                }
            }
        }
    }

    private static SpatialGrid readSpatialGrid(DataInputStream stream) throws IOException {
        int cellSize = stream.readInt();
        int width = stream.readInt();
        int height = stream.readInt();
        SpatialGrid grid = new SpatialGrid(cellSize, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int count = stream.readShort();
                for (int i = 0; i < count; i++) {
                    grid.addNode(x, y, stream.readInt());
                }
            }
        }
        return grid;
    }

    private static void writeConnectivity(DataOutputStream stream, NodeConnectivity connectivity) throws IOException {
        stream.writeInt(connectivity.getEdges().size());
        for (Connection conn : connectivity.getEdges()) {
            stream.writeInt(conn.getFrom());
            stream.writeInt(conn.getTo());
            stream.writeShort((int) (conn.getCost() * 10.0));
            stream.writeByte(conn.getType().ordinal());
        }
    }

    private static NodeConnectivity readConnectivity(DataInputStream stream) throws IOException {
        int count = stream.readInt();
        List<Connection> edges = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int from = stream.readInt();
            int to = stream.readInt();
            double cost = stream.readShort() / 10.0;
            int typeOrdinal = stream.readByte();
            EdgeType type = EdgeType.values()[Math.clamp(typeOrdinal, 0, EdgeType.values().length - 1)];
            edges.add(new Connection(from, to, cost, type));
        }
        return new NodeConnectivity(edges);
    }

    private static long calculateChecksum(byte[] data) {
        long a = 1;
        long b = 0;
        for (byte by : data) {
            a = (a + (by & 0xFF)) % 65521;
            b = (b + a) % 65521;
        }
        return (b << 16) | a;
    }

    private static class NodeIndexEntry {
        final long offset;
        final short x;
        final short y;
        final short z;
        final int originalId;
        final byte flags;
        final short edgeCount;

        NodeIndexEntry(long offset, short x, short y, short z, int originalId, byte flags, short edgeCount) {
            this.offset = offset;
            this.x = x;
            this.y = y;
            this.z = z;
            this.originalId = originalId;
            this.flags = flags;
            this.edgeCount = edgeCount;
        }
    }
}
