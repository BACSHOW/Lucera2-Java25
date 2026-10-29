/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.crypt;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

public class ScrambledKeyPair {
    private KeyPair ob;
    private byte[] oc;

    public ScrambledKeyPair(KeyPair keyPair) {
        this.ob = keyPair;
        this.oc = ScrambledKeyPair.a(((RSAPublicKey)this.ob.getPublic()).getModulus());
    }

    public KeyPair getKeyPair() {
        return this.ob;
    }

    public byte[] getScrambledModulus() {
        return this.oc;
    }

    private static final byte[] a(BigInteger bigInteger) {
        int n;
        byte[] byArray = bigInteger.toByteArray();
        if (byArray.length == 129 && byArray[0] == 0) {
            byte[] byArray2 = new byte[128];
            System.arraycopy(byArray, 1, byArray2, 0, 128);
            byArray = byArray2;
        }
        for (n = 0; n < 4; ++n) {
            byte by = byArray[n];
            byArray[n] = byArray[77 + n];
            byArray[77 + n] = by;
        }
        for (n = 0; n < 64; ++n) {
            byArray[n] = (byte)(byArray[n] ^ byArray[64 + n]);
        }
        for (n = 0; n < 4; ++n) {
            byArray[13 + n] = (byte)(byArray[13 + n] ^ byArray[52 + n]);
        }
        for (n = 0; n < 64; ++n) {
            byArray[64 + n] = (byte)(byArray[64 + n] ^ byArray[n]);
        }
        return byArray;
    }
}

