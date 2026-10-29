/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.crypt;

import java.io.IOException;
import l2.authserver.crypt.BlowfishEngine;

public class NewCrypt {
    private final BlowfishEngine nZ = new BlowfishEngine();
    private final BlowfishEngine oa;

    public NewCrypt(byte[] byArray) {
        this.nZ.init(true, byArray);
        this.oa = new BlowfishEngine();
        this.oa.init(false, byArray);
    }

    public NewCrypt(String string) {
        this(string.getBytes());
    }

    public static boolean verifyChecksum(byte[] byArray) {
        return NewCrypt.verifyChecksum(byArray, 0, byArray.length);
    }

    public static boolean verifyChecksum(byte[] byArray, int n, int n2) {
        int n3;
        if ((n2 & 3) != 0 || n2 <= 4) {
            return false;
        }
        long l = 0L;
        int n4 = n2 - 4;
        long l2 = -1L;
        for (n3 = n; n3 < n4; n3 += 4) {
            l2 = byArray[n3] & 0xFF;
            l2 |= (long)(byArray[n3 + 1] << 8 & 0xFF00);
            l2 |= (long)(byArray[n3 + 2] << 16 & 0xFF0000);
            l ^= (l2 |= (long)(byArray[n3 + 3] << 24 & 0xFF000000));
        }
        l2 = byArray[n3] & 0xFF;
        l2 |= (long)(byArray[n3 + 1] << 8 & 0xFF00);
        l2 |= (long)(byArray[n3 + 2] << 16 & 0xFF0000);
        return (l2 |= (long)(byArray[n3 + 3] << 24 & 0xFF000000)) == l;
    }

    public static void appendChecksum(byte[] byArray) {
        NewCrypt.appendChecksum(byArray, 0, byArray.length);
    }

    public static void appendChecksum(byte[] byArray, int n, int n2) {
        long l;
        int n3;
        long l2 = 0L;
        int n4 = n2 - 4;
        for (n3 = n; n3 < n4; n3 += 4) {
            l = byArray[n3] & 0xFF;
            l |= (long)(byArray[n3 + 1] << 8 & 0xFF00);
            l |= (long)(byArray[n3 + 2] << 16 & 0xFF0000);
            l2 ^= (l |= (long)(byArray[n3 + 3] << 24 & 0xFF000000));
        }
        l = byArray[n3] & 0xFF;
        l |= (long)(byArray[n3 + 1] << 8 & 0xFF00);
        l |= (long)(byArray[n3 + 2] << 16 & 0xFF0000);
        l |= (long)(byArray[n3 + 3] << 24 & 0xFF000000);
        byArray[n3] = (byte)(l2 & 0xFFL);
        byArray[n3 + 1] = (byte)(l2 >> 8 & 0xFFL);
        byArray[n3 + 2] = (byte)(l2 >> 16 & 0xFFL);
        byArray[n3 + 3] = (byte)(l2 >> 24 & 0xFFL);
    }

    public static void encXORPass(byte[] byArray, int n) {
        NewCrypt.encXORPass(byArray, 0, byArray.length, n);
    }

    public static void encXORPass(byte[] byArray, int n, int n2, int n3) {
        int n4 = n2 - 8;
        int n5 = 4 + n;
        int n6 = n3;
        while (n5 < n4) {
            int n7 = byArray[n5] & 0xFF;
            n7 |= (byArray[n5 + 1] & 0xFF) << 8;
            n7 |= (byArray[n5 + 2] & 0xFF) << 16;
            n6 += (n7 |= (byArray[n5 + 3] & 0xFF) << 24);
            byArray[n5++] = (byte)((n7 ^= n6) & 0xFF);
            byArray[n5++] = (byte)(n7 >> 8 & 0xFF);
            byArray[n5++] = (byte)(n7 >> 16 & 0xFF);
            byArray[n5++] = (byte)(n7 >> 24 & 0xFF);
        }
        byArray[n5++] = (byte)(n6 & 0xFF);
        byArray[n5++] = (byte)(n6 >> 8 & 0xFF);
        byArray[n5++] = (byte)(n6 >> 16 & 0xFF);
        byArray[n5] = (byte)(n6 >> 24 & 0xFF);
    }

    public byte[] decrypt(byte[] byArray) throws IOException {
        byte[] byArray2 = new byte[byArray.length];
        int n = byArray.length / 8;
        for (int i = 0; i < n; ++i) {
            this.oa.processBlock(byArray, i * 8, byArray2, i * 8);
        }
        return byArray2;
    }

    public void decrypt(byte[] byArray, int n, int n2) throws IOException {
        byte[] byArray2 = new byte[n2];
        int n3 = n2 / 8;
        for (int i = 0; i < n3; ++i) {
            this.oa.processBlock(byArray, n + i * 8, byArray2, i * 8);
        }
        System.arraycopy(byArray2, 0, byArray, n, n2);
    }

    public byte[] crypt(byte[] byArray) throws IOException {
        int n = byArray.length / 8;
        byte[] byArray2 = new byte[byArray.length];
        for (int i = 0; i < n; ++i) {
            this.nZ.processBlock(byArray, i * 8, byArray2, i * 8);
        }
        return byArray2;
    }

    public void crypt(byte[] byArray, int n, int n2) throws IOException {
        int n3 = n2 / 8;
        byte[] byArray2 = new byte[n2];
        for (int i = 0; i < n3; ++i) {
            this.nZ.processBlock(byArray, n + i * 8, byArray2, i * 8);
        }
        System.arraycopy(byArray2, 0, byArray, n, n2);
    }
}

