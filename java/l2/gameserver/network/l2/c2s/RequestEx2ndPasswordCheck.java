/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordCheck;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RequestEx2ndPasswordCheck
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        if (!Config.USE_SECOND_PASSWORD_AUTH) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult.SUCCESS));
            return;
        }
        SecondPasswordAuth secondPasswordAuth = gameClient.getSecondPasswordAuth();
        if (secondPasswordAuth == null) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult.ERROR));
            return;
        }
        if (!secondPasswordAuth.isSecondPasswordSet()) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult.CREATE));
            return;
        }
        if (secondPasswordAuth.isBlocked()) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult.BLOCK_TIME, secondPasswordAuth.getBlockTimeLeft()));
            return;
        }
        if (gameClient.isSecondPasswordAuthed()) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult.SUCCESS));
            return;
        }
        gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult.CHECK));
    }
}

