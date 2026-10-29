/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2;

import java.nio.ByteBuffer;
import l2.gameserver.network.l2.BlowFishKeygen;

public abstract class GameCrypt<CGCtx, CGKey> {
    private final byte[] ZZ = new byte[16];
    private final byte[] aaa = new byte[16];
    private boolean aN = false;

    public CGKey initKey(CGCtx CGCtx) {
        return (CGKey)BlowFishKeygen.getRandomKey();
    }

    public CGKey setKey(CGKey CGKey) {
        System.arraycopy(CGKey, 0, this.ZZ, 0, 16);
        System.arraycopy(CGKey, 0, this.aaa, 0, 16);
        return CGKey;
    }

    public boolean decrypt(ByteBuffer[] byteBufferArray) {
        throw new UnsupportedOperationException();
    }

    public boolean decrypt(byte[] byArray, int n, int n2) {
        if (!this.aN) {
            return true;
        }
        int previous = 0;
        int i = 0;
        for (; i + 16 <= n2; i += 16) {
            int base = n + i;
            int c0 = byArray[base] & 0xFF, c1 = byArray[base + 1] & 0xFF;
            int c2 = byArray[base + 2] & 0xFF, c3 = byArray[base + 3] & 0xFF;
            int c4 = byArray[base + 4] & 0xFF, c5 = byArray[base + 5] & 0xFF;
            int c6 = byArray[base + 6] & 0xFF, c7 = byArray[base + 7] & 0xFF;
            int c8 = byArray[base + 8] & 0xFF, c9 = byArray[base + 9] & 0xFF;
            int c10 = byArray[base + 10] & 0xFF, c11 = byArray[base + 11] & 0xFF;
            int c12 = byArray[base + 12] & 0xFF, c13 = byArray[base + 13] & 0xFF;
            int c14 = byArray[base + 14] & 0xFF, c15 = byArray[base + 15] & 0xFF;
            byArray[base] = (byte) (c0 ^ this.ZZ[0] ^ previous);
            byArray[base + 1] = (byte) (c1 ^ this.ZZ[1] ^ c0);
            byArray[base + 2] = (byte) (c2 ^ this.ZZ[2] ^ c1);
            byArray[base + 3] = (byte) (c3 ^ this.ZZ[3] ^ c2);
            byArray[base + 4] = (byte) (c4 ^ this.ZZ[4] ^ c3);
            byArray[base + 5] = (byte) (c5 ^ this.ZZ[5] ^ c4);
            byArray[base + 6] = (byte) (c6 ^ this.ZZ[6] ^ c5);
            byArray[base + 7] = (byte) (c7 ^ this.ZZ[7] ^ c6);
            byArray[base + 8] = (byte) (c8 ^ this.ZZ[8] ^ c7);
            byArray[base + 9] = (byte) (c9 ^ this.ZZ[9] ^ c8);
            byArray[base + 10] = (byte) (c10 ^ this.ZZ[10] ^ c9);
            byArray[base + 11] = (byte) (c11 ^ this.ZZ[11] ^ c10);
            byArray[base + 12] = (byte) (c12 ^ this.ZZ[12] ^ c11);
            byArray[base + 13] = (byte) (c13 ^ this.ZZ[13] ^ c12);
            byArray[base + 14] = (byte) (c14 ^ this.ZZ[14] ^ c13);
            byArray[base + 15] = (byte) (c15 ^ this.ZZ[15] ^ c14);
            previous = c15;
        }
        for (; i < n2; i++) {
            int value = byArray[n + i] & 0xFF;
            byArray[n + i] = (byte) (value ^ this.ZZ[i & 15] ^ previous);
            previous = value;
        }
        int n3 = this.ZZ[8] & 0xFF;
        n3 |= this.ZZ[9] << 8 & 0xFF00;
        n3 |= this.ZZ[10] << 16 & 0xFF0000;
        n3 |= this.ZZ[11] << 24 & 0xFF000000;
        this.ZZ[8] = (byte)((n3 += n2) & 0xFF);
        this.ZZ[9] = (byte)(n3 >> 8 & 0xFF);
        this.ZZ[10] = (byte)(n3 >> 16 & 0xFF);
        this.ZZ[11] = (byte)(n3 >> 24 & 0xFF);
        return true;
    }

    public boolean encrypt(ByteBuffer[] byteBufferArray) {
        throw new UnsupportedOperationException();
    }

    public void encrypt(byte[] byArray, int n, int n2) {
        int n3;
        if (!this.aN) {
            this.aN = true;
            return;
        }
        int n4 = 0;
        for (n3 = 0; n3 < n2; ++n3) {
            int n5 = byArray[n + n3] & 0xFF;
            n4 = n5 ^ this.aaa[n3 & 0xF] ^ n4;
            byArray[n + n3] = (byte)n4;
        }
        n3 = this.aaa[8] & 0xFF;
        n3 |= this.aaa[9] << 8 & 0xFF00;
        n3 |= this.aaa[10] << 16 & 0xFF0000;
        n3 |= this.aaa[11] << 24 & 0xFF000000;
        this.aaa[8] = (byte)((n3 += n2) & 0xFF);
        this.aaa[9] = (byte)(n3 >> 8 & 0xFF);
        this.aaa[10] = (byte)(n3 >> 16 & 0xFF);
        this.aaa[11] = (byte)(n3 >> 24 & 0xFF);
    }
}
