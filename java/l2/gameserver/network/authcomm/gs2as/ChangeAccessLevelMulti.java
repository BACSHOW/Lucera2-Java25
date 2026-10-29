/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.authcomm.gs2as;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import l2.gameserver.network.authcomm.SendablePacket;

public class ChangeAccessLevelMulti
extends SendablePacket {
    private List<String> oX;
    private int level;
    private int nw;

    public ChangeAccessLevelMulti(String[] stringArray, int n, int n2) {
        this.oX = Arrays.asList(stringArray);
        this.level = n;
        this.nw = n2;
    }

    public ChangeAccessLevelMulti(Collection<String> collection, int n, int n2) {
        this.oX = new ArrayList<String>(collection);
        this.level = n;
        this.nw = n2;
    }

    @Override
    protected void writeImpl() {
        this.writeC(18);
        this.writeD(Math.min(this.oX.size(), 127));
        for (int i = 0; i < Math.min(127, this.oX.size()); ++i) {
            this.writeS(this.oX.get(i));
        }
        this.writeD(this.level);
        this.writeD(this.nw);
    }
}

