/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.crypt;

import java.io.IOException;
import l2.authserver.crypt.NewCrypt;
import l2.commons.util.Rnd;

public class LoginCrypt {
    private static final byte[] nV = new byte[]{107, 96, -53, 91, -126, -50, -112, -79, -52, 43, 108, 85, 108, 108, 108, 108};
    private NewCrypt nW;
    private NewCrypt nX;
    private boolean nY = true;

    public void setKey(byte[] byArray) {
        this.nW = new NewCrypt(nV);
        this.nX = new NewCrypt(byArray);
    }

    public boolean decrypt(byte[] byArray, int n, int n2) throws IOException {
        this.nX.decrypt(byArray, n, n2);
        return NewCrypt.verifyChecksum(byArray, n, n2);
    }

    public int encrypt(byte[] byArray, int n, int n2) throws IOException {
        n2 += 4;
        if (this.nY) {
            n2 += 4;
            n2 += 8 - n2 % 8;
            NewCrypt.encXORPass(byArray, n, n2, Rnd.nextInt());
            this.nW.crypt(byArray, n, n2);
            this.nY = false;
        } else {
            n2 += 8 - n2 % 8;
            NewCrypt.appendChecksum(byArray, n, n2);
            this.nX.crypt(byArray, n, n2);
        }
        return n2;
    }
}

