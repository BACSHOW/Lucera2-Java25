/*
 * Decompiled with CFR 0.152.
 */
package l2.authserver.network.l2;

import l2.commons.util.Rnd;

public class SessionKey {
    public final int playOkID1;
    public final int playOkID2;
    public final int loginOkID1;
    public final int loginOkID2;
    private final int pr;

    public SessionKey(int n, int n2, int n3, int n4) {
        this.playOkID1 = n3;
        this.playOkID2 = n4;
        this.loginOkID1 = n;
        this.loginOkID2 = n2;
        int n5 = n3;
        n5 *= 17;
        n5 += n4;
        n5 *= 37;
        n5 += n;
        n5 *= 51;
        this.pr = n5 += n2;
    }

    public boolean checkLoginPair(int n, int n2) {
        return this.loginOkID1 == n && this.loginOkID2 == n2;
    }

    public static final SessionKey create() {
        return new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() == this.getClass()) {
            SessionKey sessionKey = (SessionKey)object;
            return this.playOkID1 == sessionKey.playOkID1 && this.playOkID2 == sessionKey.playOkID2 && sessionKey.checkLoginPair(this.loginOkID1, this.loginOkID2);
        }
        return false;
    }

    public int hashCode() {
        return this.pr;
    }

    public String toString() {
        return "[playOkID1: " + this.playOkID1 + " playOkID2: " + this.playOkID2 + " loginOkID1: " + this.loginOkID1 + " loginOkID2: " + this.loginOkID2 + "]";
    }
}

