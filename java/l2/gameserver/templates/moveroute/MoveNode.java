/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.moveroute;

import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.utils.Location;

public class MoveNode
extends Location {
    private static final long serialVersionUID = 8291528118019681063L;
    private final String avH;
    private final ChatType Qu;
    private final long avI;
    private final int avJ;

    public MoveNode(int n, int n2, int n3, String string, int n4, long l, ChatType chatType) {
        super(n, n2, n3);
        this.avH = string;
        this.avJ = n4;
        this.avI = l;
        this.Qu = chatType;
    }

    public String getNpcMsgAddress() {
        return this.avH;
    }

    public long getDelay() {
        return this.avI;
    }

    public int getSocialId() {
        return this.avJ;
    }

    public ChatType getChatType() {
        return this.Qu;
    }
}

