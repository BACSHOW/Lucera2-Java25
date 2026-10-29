/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2;

import l2.commons.util.Rnd;

public class BlowFishKeygen {
    private static final int ZC = 20;
    private static final byte[][] ZD = new byte[20][16];

    private BlowFishKeygen() {
    }

    public static byte[] getRandomKey() {
        return ZD[Rnd.get(20)];
    }

    static {
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < ZD[i].length; ++j) {
                BlowFishKeygen.ZD[i][j] = (byte)Rnd.get(255);
            }
            BlowFishKeygen.ZD[i][8] = -56;
            BlowFishKeygen.ZD[i][9] = 39;
            BlowFishKeygen.ZD[i][10] = -109;
            BlowFishKeygen.ZD[i][11] = 1;
            BlowFishKeygen.ZD[i][12] = -95;
            BlowFishKeygen.ZD[i][13] = 108;
            BlowFishKeygen.ZD[i][14] = 49;
            BlowFishKeygen.ZD[i][15] = -105;
        }
    }
}

