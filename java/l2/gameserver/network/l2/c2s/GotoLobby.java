/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class GotoLobby
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        CharacterSelectionInfo characterSelectionInfo = new CharacterSelectionInfo(((GameClient)this.getClient()).getLogin(), ((GameClient)this.getClient()).getSessionKey().playOkID1);
        this.sendPacket((L2GameServerPacket)characterSelectionInfo);
    }
}

