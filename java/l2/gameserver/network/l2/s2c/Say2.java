/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SysString;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcStringContainer;

public class Say2
extends NpcStringContainer {
    private ChatType ade;
    private SysString QA;
    private SystemMsg QB;
    private int CS;
    private String agb;

    public Say2(int n, ChatType chatType, SysString sysString, SystemMsg systemMsg) {
        super(NpcString.NONE, new String[0]);
        this.CS = n;
        this.ade = chatType;
        this.QA = sysString;
        this.QB = systemMsg;
    }

    public Say2(int n, ChatType chatType, String string, String string2) {
        this(n, chatType, string, NpcString.NONE, string2);
    }

    public Say2(int n, ChatType chatType, String string, NpcString npcString, String ... stringArray) {
        super(npcString, stringArray);
        this.CS = n;
        this.ade = chatType;
        this.agb = string;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(74);
        this.writeD(this.CS);
        this.writeD(this.ade.ordinal());
        switch (this.ade) {
            case SYSTEM_MESSAGE: {
                this.writeD(this.QA.getId());
                this.writeD(this.QB.getId());
                break;
            }
            default: {
                this.writeS(this.agb);
                this.writeElements();
            }
        }
    }
}

