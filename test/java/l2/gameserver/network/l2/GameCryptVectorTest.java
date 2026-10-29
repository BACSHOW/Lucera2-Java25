package l2.gameserver.network.l2;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class GameCryptVectorTest {
    private static final class Crypt extends GameCrypt<Object, byte[]> { }

    @Test
    void unrolledEncryptionMatchesScalarProtocol() {
        byte[] key = new byte[16];
        for (int i = 0; i < key.length; i++) key[i] = (byte) (i * 17 + 3);
        byte[] input = new byte[257];
        for (int i = 0; i < input.length; i++) input[i] = (byte) (i * 31 - 7);

        Crypt crypt = new Crypt();
        crypt.setKey(key);
        crypt.encrypt(new byte[0], 0, 0); // protocol handshake packet arms encryption
        byte[] actual = input.clone();
        crypt.encrypt(actual, 0, actual.length);

        byte[] expected = input.clone();
        int previous = 0;
        for (int i = 0; i < expected.length; i++) {
            int value = expected[i] & 0xFF;
            expected[i] = (byte) (value ^ key[i & 15] ^ previous);
            previous = expected[i] & 0xFF;
        }
        assertThat(actual).containsExactly(expected);
    }

    @Test
    void decryptRestoresEncryptedPayloadAcrossChunkSizes() {
        byte[] key = new byte[16];
        for (int i = 0; i < key.length; i++) key[i] = (byte) (0xA0 + i);
        byte[] plain = new byte[96];
        for (int i = 0; i < plain.length; i++) plain[i] = (byte) (i ^ 0x5A);

        Crypt encryptor = new Crypt();
        encryptor.setKey(key);
        encryptor.encrypt(new byte[0], 0, 0);
        byte[] wire = plain.clone();
        encryptor.encrypt(wire, 0, wire.length);

        Crypt decryptor = new Crypt();
        decryptor.setKey(key);
        decryptor.encrypt(new byte[0], 0, 0);
        assertThat(decryptor.decrypt(wire, 0, wire.length)).isTrue();
        assertThat(wire).containsExactly(plain);
        assertThat(Arrays.equals(wire, plain)).isTrue();
    }
}
