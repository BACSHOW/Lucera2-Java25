/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordCheck;
import l2.gameserver.network.l2.s2c.Ex2ndPasswordVerify;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RequestEx2ndPasswordVerify
extends L2GameClientPacket {
    private String abJ;

    @Override
    protected void readImpl() {
        this.abJ = this.readS(8);
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        SecondPasswordAuth secondPasswordAuth = gameClient.getSecondPasswordAuth();
        if (secondPasswordAuth == null) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordVerify(Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult.ERROR));
            return;
        }
        if (!secondPasswordAuth.isSecondPasswordSet()) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordCheck(Ex2ndPasswordCheck.Ex2ndPasswordCheckResult.CREATE));
            return;
        }
        if (secondPasswordAuth.isValidSecondPassword(this.abJ)) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordVerify(Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult.SUCCESS));
            gameClient.setSecondPasswordAuthed(true);
        } else if (secondPasswordAuth.isBlocked()) {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordVerify(Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult.BLOCK_HOMEPAGE));
        } else {
            gameClient.sendPacket((L2GameServerPacket)new Ex2ndPasswordVerify(Ex2ndPasswordVerify.Ex2ndPasswordVerifyResult.FAILED, secondPasswordAuth.getTrysCount()));
        }
    }
}

