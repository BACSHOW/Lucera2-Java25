/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public abstract class NpcStringContainer
extends L2GameServerPacket {
    private final NpcString ako;
    private final String[] akp = new String[5];

    protected NpcStringContainer(NpcString npcString, String ... stringArray) {
        this.ako = npcString;
        System.arraycopy(stringArray, 0, this.akp, 0, stringArray.length);
    }

    protected void writeElements() {
        for (String string : this.akp) {
            this.writeS(string);
        }
    }
}

