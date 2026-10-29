package l2.gameserver.network.l2.fermata;

import io.netty.channel.embedded.EmbeddedChannel;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.fermata.s2c.ArmorSetAura;
import l2.gameserver.network.l2.fermata.s2c.Healing;
import l2.gameserver.network.l2.fermata.s2c.MonsterDebuffSnapshot;
import l2.gameserver.network.l2.netty.NettyGameConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitarios que verificam a serializacao little-endian dos pacotes
 * S2C Fermata Phase 2 contra vetores de bytes pre-computados (handshake
 * Phase 1 ja entrega o opcode 0xFD como identificador do frame).
 */
class CombatQoLCodecTest {

    private ByteBuffer writeToBuffer(l2.gameserver.network.l2.s2c.L2GameServerPacket packet) {
        EmbeddedChannel embedded = new EmbeddedChannel();
        NettyGameConnection conn = new NettyGameConnection(embedded);
        GameClient client = new GameClient(conn);

        ByteBuffer buf = ByteBuffer.allocate(256).order(ByteOrder.LITTLE_ENDIAN);
        boolean ok = packet.writeTo(buf, client);
        assertThat(ok).isTrue();

        buf.flip();
        return buf;
    }

    @Test
    @DisplayName("Healing (0x0008) emite opcode 0xFD, subopcode 0x0008 e payload little-endian")
    void testHealingSerialization() {
        Healing packet = new Healing(0x0A0B0C0D, 0x11223344, 1218, 36, 999_512);

        ByteBuffer buf = writeToBuffer(packet);

        // 0xFD
        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        // subopcode 0x0008 little-endian = 0x08 0x00
        assertThat(buf.get() & 0xFF).isEqualTo(0x08);
        assertThat(buf.get() & 0xFF).isEqualTo(0x00);
        // sourceId 0x0A0B0C0D
        assertThat(buf.getInt()).isEqualTo(0x0A0B0C0D);
        // targetId 0x11223344
        assertThat(buf.getInt()).isEqualTo(0x11223344);
        // skillId 1218
        assertThat(buf.getInt()).isEqualTo(1218);
        // skillLevel 36 (H, little-endian = 0x24 0x00)
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(36);
        // amount 999512
        assertThat(buf.getInt()).isEqualTo(999_512);

        assertThat(buf.remaining()).isZero();
    }

    @Test
    @DisplayName("MonsterDebuffSnapshot (0x0016) serializa 0xFD, subopcode, targetId, count e triplets")
    void testMonsterDebuffSnapshotSerialization() {
        int[] ids = new int[] { 1006, 7_777 };
        int[] lvls = new int[] { 12, 9 };
        int[] secs = new int[] { 30, 5 };
        MonsterDebuffSnapshot packet = new MonsterDebuffSnapshot(0xCAFEBABE, ids, lvls, secs);

        ByteBuffer buf = writeToBuffer(packet);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(0x0016);
        assertThat(buf.getInt()).isEqualTo(0xCAFEBABE);
        assertThat(buf.getInt()).isEqualTo(2);

        // entry 0
        assertThat(buf.getInt()).isEqualTo(1006);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(12);
        assertThat(buf.getInt()).isEqualTo(30);

        // entry 1
        assertThat(buf.getInt()).isEqualTo(7_777);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(9);
        assertThat(buf.getInt()).isEqualTo(5);

        assertThat(buf.remaining()).isZero();
    }

    @Test
    @DisplayName("MonsterDebuffSnapshot com lista vazia emite count=0 sem triplet array")
    void testMonsterDebuffSnapshotEmpty() {
        MonsterDebuffSnapshot packet = new MonsterDebuffSnapshot(0x12345678, new int[0], new int[0], new int[0]);
        ByteBuffer buf = writeToBuffer(packet);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(0x0016);
        assertThat(buf.getInt()).isEqualTo(0x12345678);
        assertThat(buf.getInt()).isZero();
        assertThat(buf.remaining()).isZero();
    }

    @Test
    @DisplayName("ArmorSetAura (0x000C) emite 0xFD, subopcode, objectId, tier")
    void testArmorSetAuraSerialization() {
        ArmorSetAura packet = new ArmorSetAura(0xDEADBEEF, ArmorSetAura.TIER_MYTHIC);

        ByteBuffer buf = writeToBuffer(packet);

        assertThat(buf.get() & 0xFF).isEqualTo(0xFD);
        assertThat(buf.getShort() & 0xFFFF).isEqualTo(0x000C);
        assertThat(buf.getInt()).isEqualTo(0xDEADBEEF);
        assertThat(buf.getInt()).isEqualTo(3);
        assertThat(buf.remaining()).isZero();
    }

    @Test
    @DisplayName("ArmorSetAura.tierFromEnchant: 5 -> NONE; 6 -> 1; 10 -> 2; 16 -> 3; 100 -> 3")
    void testArmorSetAuraTierFromEnchant() {
        assertThat(ArmorSetAura.tierFromEnchant(5)).isEqualTo(ArmorSetAura.TIER_NONE);
        assertThat(ArmorSetAura.tierFromEnchant(6)).isEqualTo(ArmorSetAura.TIER_BASIC);
        assertThat(ArmorSetAura.tierFromEnchant(10)).isEqualTo(ArmorSetAura.TIER_ADVANCED);
        assertThat(ArmorSetAura.tierFromEnchant(16)).isEqualTo(ArmorSetAura.TIER_MYTHIC);
        assertThat(ArmorSetAura.tierFromEnchant(100)).isEqualTo(ArmorSetAura.TIER_MYTHIC);
    }

    @Test
    @DisplayName("FermataCapabilities.has verifica bits esperados")
    void testFermataCapabilitiesBits() {
        int caps = FermataCapabilities.BIT_HEALING_FEEDBACK | FermataCapabilities.BIT_ARMOR_SET_AURA;
        assertThat(FermataCapabilities.has(caps, FermataCapabilities.BIT_HEALING_FEEDBACK)).isTrue();
        assertThat(FermataCapabilities.has(caps, FermataCapabilities.BIT_ARMOR_SET_AURA)).isTrue();
        assertThat(FermataCapabilities.has(caps, FermataCapabilities.BIT_MONSTER_DEBUFF_HUD)).isFalse();
        assertThat(FermataCapabilities.has(caps, FermataCapabilities.BIT_BUFF_REMOVE_SELF)).isFalse();

        int all = FermataCapabilities.allKnown();
        assertThat(FermataCapabilities.has(all, FermataCapabilities.BIT_HEALING_FEEDBACK)).isTrue();
        assertThat(FermataCapabilities.has(all, FermataCapabilities.BIT_MONSTER_DEBUFF_HUD)).isTrue();
        assertThat(FermataCapabilities.has(all, FermataCapabilities.BIT_ARMOR_SET_AURA)).isTrue();
        assertThat(FermataCapabilities.has(all, FermataCapabilities.BIT_BUFF_REMOVE_SELF)).isTrue();
    }

    @Test
    @DisplayName("RequestFermataRemoveBuff.SUBOPCODE = 0x0001 e Mascara de remocao do self buff")
    void testRequestFermataRemoveBuffOpcode() {
        assertThat(l2.gameserver.network.l2.fermata.c2s.RequestFermataRemoveBuff.SUBOPCODE).isEqualTo(0x0001);
    }
}