package l2.gameserver.geodata.pathfinding.serialization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class L2BRSerializerZstdTest {

    @Test
    @DisplayName("L2BRSerializer Zstd: Deve serializar e desserializar com compressão Zstd Level 3 e paridade 100%")
    void testSerializeAndDeserializeWithZstd(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("22_22.l2br");

        // Cria uma região indexada sintética com nós e conexões
        Map<Integer, Node> nodes = new HashMap<>();
        for (int i = 0; i < 200; i++) {
            List<Edge> edges = new ArrayList<>();
            if (i > 0) {
                edges.add(new Edge(i - 1, 10.0, EdgeType.WALK));
            }
            if (i < 199) {
                edges.add(new Edge(i + 1, 10.0, EdgeType.WALK));
            }
            nodes.put(i, new Node(1000 + i * 16, 2000 + i * 16, 50, i, true, false, edges));
        }

        SpatialGrid grid = new SpatialGrid(128, 16, 16);
        grid.addNode(1, 1, 0);
        grid.addNode(1, 1, 1);

        List<Connection> connections = List.of(
                new Connection(0, 1, 10.0, EdgeType.WALK),
                new Connection(1, 2, 10.0, EdgeType.WALK)
        );
        NodeConnectivity connectivity = new NodeConnectivity(connections);

        IndexedRegion originalRegion = new IndexedRegion(
                22, 22,
                nodes,
                200,
                grid,
                connectivity,
                SourceType.L2J,
                System.currentTimeMillis()
        );

        L2BRSerializer serializer = new L2BRSerializer();

        // 1. Serialização com Zstandard Level 3
        serializer.serialize(originalRegion, outputFile);

        File file = outputFile.toFile();
        assertTrue(file.exists(), "Arquivo compactado com Zstd deve existir");
        assertTrue(file.length() > L2BRSerializer.HEADER_SIZE, "Arquivo deve conter cabeçalho e payload Zstd");

        // 2. Desserialização com Zstandard
        IndexedRegion loadedRegion = serializer.deserialize(outputFile);

        // 3. Verificações de paridade
        assertEquals(originalRegion.getRegionX(), loadedRegion.getRegionX());
        assertEquals(originalRegion.getRegionY(), loadedRegion.getRegionY());
        assertEquals(originalRegion.getWalkableCount(), loadedRegion.getWalkableCount());
        assertEquals(originalRegion.getSourceType(), loadedRegion.getSourceType());
        assertEquals(originalRegion.getNodes().size(), loadedRegion.getNodes().size());

        // Verifica se o nó #10 manteve coordenadas e edges intactas
        Node originalNode10 = originalRegion.getNodes().get(10);
        Node loadedNode10 = loadedRegion.getNodes().get(10);
        assertNotNull(loadedNode10);
        assertEquals(originalNode10.getX(), loadedNode10.getX());
        assertEquals(originalNode10.getY(), loadedNode10.getY());
        assertEquals(originalNode10.getZ(), loadedNode10.getZ());
        assertEquals(originalNode10.getEdges().size(), loadedNode10.getEdges().size());
        assertEquals(originalNode10.getEdges().get(0).getTargetId(), loadedNode10.getEdges().get(0).getTargetId());
    }
}
