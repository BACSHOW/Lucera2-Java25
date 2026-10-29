/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class CharacterRestore
extends L2GameClientPacket {
    private int aaI;

    @Override
    protected void readImpl() {
        this.aaI = this.readD();
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        try {
            gameClient.markRestoredChar(this.aaI);
        }
        catch (Exception exception) {
        }
        finally {
            CharacterSelectionInfo characterSelectionInfo = new CharacterSelectionInfo(gameClient.getLogin(), gameClient.getSessionKey().playOkID1);
            this.sendPacket((L2GameServerPacket)characterSelectionInfo);
            gameClient.setCharSelection(characterSelectionInfo.getCharInfo());
        }
    }
}

