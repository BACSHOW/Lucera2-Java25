/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.NpcStringContainer;

public class NpcSay
extends NpcStringContainer {
    private int aeo;
    private int _type;
    private int _id;

    public NpcSay(NpcInstance npcInstance, ChatType chatType, String string) {
        this(npcInstance, chatType, NpcString.NONE, string);
    }

    public NpcSay(NpcInstance npcInstance, ChatType chatType, NpcString npcString, String ... stringArray) {
        super(npcString, stringArray);
        this.aeo = npcInstance.getObjectId();
        this._id = npcInstance.getNpcId();
        this._type = chatType.ordinal();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(2);
        this.writeD(this.aeo);
        this.writeD(this._type);
        this.writeD(1000000 + this._id);
        this.writeElements();
    }
}

