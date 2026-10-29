package l2.gameserver.network.l2.fermata;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.fermata.s2c.AtlasCatalog;
import l2.gameserver.network.l2.fermata.s2c.AtlasCatalog.CatalogRow;
import l2.gameserver.network.l2.fermata.s2c.ItemSources;
import l2.gameserver.network.l2.fermata.s2c.ItemSources.DropSource;
import l2.gameserver.network.l2.fermata.s2c.ItemSources.ShopSource;
import l2.gameserver.network.l2.fermata.s2c.NpcAtlasEntry;
import l2.gameserver.network.l2.fermata.s2c.NpcAtlasEntry.Location;
import l2.gameserver.network.l2.fermata.s2c.NpcDrops;
import l2.gameserver.network.l2.fermata.s2c.NpcDrops.DropGroup;
import l2.gameserver.network.l2.fermata.s2c.NpcDrops.DropItem;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AtlasDataCodecTest {

    private EmbeddedChannel channel;
    private GameClient client;

    @BeforeEach
    void setUp() {
        channel = new EmbeddedChannel();
        client = new GameClient(new NettyGameConnection(channel));
    }

    @AfterEach
    void tearDown() {
        channel.finishAndReleaseAll();
    }

    @Test
    @DisplayName("NpcDrops: layout wire com envelope 0xFD e subopcode 0x0007")
    void testNpcDropsLayout() {
        List<DropItem> items = List.of(new DropItem(57, 100, 200, 700000));
        List<DropGroup> groups = List.of(new DropGroup(1, 1000000, 1, items));

        NpcDrops packet = new NpcDrops(20001, groups);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(NpcDrops.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(20001);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // groupCount
        assertThat(buf.get() & 0xFF).isEqualTo(1);       // kind
        assertThat(buf.getInt()).isEqualTo(1000000);     // categoryChance
        assertThat(buf.get() & 0xFF).isEqualTo(1);       // rolls
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // itemCount
        assertThat(buf.getInt()).isEqualTo(57);
        assertThat(buf.getInt()).isEqualTo(100);
        assertThat(buf.getInt()).isEqualTo(200);
        assertThat(buf.getInt()).isEqualTo(700000);
    }

    @Test
    @DisplayName("AtlasCatalog: layout wire paginado com rows de 24 bytes")
    void testAtlasCatalogLayout() {
        List<CatalogRow> rows = List.of(
                new CatalogRow(20001, 70, 0, 5, 10000, 20000, -3000, 120)
        );
        AtlasCatalog packet = new AtlasCatalog(1, 500, 0, 50, rows);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(AtlasCatalog.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(500);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(0);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(50);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(20001);
        assertThat(buf.get() & 0xFF).isEqualTo(70);
        assertThat(buf.get() & 0xFF).isEqualTo(0);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(5);
        assertThat(buf.getInt()).isEqualTo(10000);
        assertThat(buf.getInt()).isEqualTo(20000);
        assertThat(buf.getInt()).isEqualTo(-3000);
        assertThat(buf.getInt()).isEqualTo(120);
    }

    @Test
    @DisplayName("ItemSources: layout com drops, shops e recipes")
    void testItemSourcesLayout() {
        List<DropSource> drops = List.of(new DropSource(20001, 1, 500000, 1, 2));
        List<ShopSource> shops = List.of(new ShopSource(30001, 10000));
        List<Integer> recipes = List.of(4001);

        ItemSources packet = new ItemSources(1, 135, drops, shops, recipes);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(ItemSources.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(135);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // dropCount
        assertThat(buf.getInt()).isEqualTo(20001);
        assertThat(buf.get() & 0xFF).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(500000);
        assertThat(buf.getInt()).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(2);

        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // shopCount
        assertThat(buf.getInt()).isEqualTo(30001);
        assertThat(buf.getInt()).isEqualTo(10000);

        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // recipeCount
        assertThat(buf.getInt()).isEqualTo(4001);
    }

    @Test
    @DisplayName("NpcAtlasEntry: layout de 59 bytes fixos + locations")
    void testNpcAtlasEntryLayout() {
        List<Location> locs = List.of(new Location(111, 222, 333, 4));
        NpcAtlasEntry packet = new NpcAtlasEntry(1, 20001, 75, 1, 50000, 10000, 800, 600, 500, 400, 100000, 5000, 300, locs);
        ByteBuffer buf = ByteBuffer.wrap(serialize(packet)).order(ByteOrder.LITTLE_ENDIAN);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(NpcAtlasEntry.SUB_OPCODE);
        assertThat(buf.getInt()).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(20001);
        assertThat(buf.get() & 0xFF).isEqualTo(75);
        assertThat(buf.get() & 0xFF).isEqualTo(1);
        assertThat(buf.getInt()).isEqualTo(50000); // hp
        assertThat(buf.getInt()).isEqualTo(10000); // mp
        assertThat(buf.getInt()).isEqualTo(800);   // pAtk
        assertThat(buf.getInt()).isEqualTo(600);   // pDef
        assertThat(buf.getInt()).isEqualTo(500);   // mAtk
        assertThat(buf.getInt()).isEqualTo(400);   // mDef
        assertThat(buf.getInt()).isEqualTo(100000); // exp
        assertThat(buf.getInt()).isEqualTo(5000);   // sp
        assertThat(buf.getInt()).isEqualTo(300);    // respawn
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(1); // location count
        assertThat(buf.getInt()).isEqualTo(111);
        assertThat(buf.getInt()).isEqualTo(222);
        assertThat(buf.getInt()).isEqualTo(333);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(4);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(0);
    }

    private byte[] serialize(L2GameServerPacket packet) {
        ByteBuffer buf = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);
        boolean ok = packet.writeTo(buf, client);
        assertThat(ok).isTrue();
        buf.flip();
        byte[] out = new byte[buf.remaining()];
        buf.get(out);
        return out;
    }
}
