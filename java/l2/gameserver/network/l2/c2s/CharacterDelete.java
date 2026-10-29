/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.mysql;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.CharacterDeleteFail;
import l2.gameserver.network.l2.s2c.CharacterDeleteSuccess;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterDelete
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(CharacterDelete.class);
    private int aaI;

    @Override
    protected void readImpl() {
        this.aaI = this.readD();
    }

    @Override
    protected void runImpl() {
        int n = this.ds();
        int n2 = this.dt();
        if (n > 0 || n2 > 0) {
            if (n == 2) {
                this.sendPacket((L2GameServerPacket)new CharacterDeleteFail(CharacterDeleteFail.REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED));
            } else if (n == 1) {
                this.sendPacket((L2GameServerPacket)new CharacterDeleteFail(CharacterDeleteFail.REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER));
            } else if (n2 > 0) {
                this.sendPacket((L2GameServerPacket)new CharacterDeleteFail(CharacterDeleteFail.REASON_DELETION_FAILED));
            }
            return;
        }
        final GameClient gameClient = (GameClient)this.getClient();
        RunnableImpl runnableImpl = new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                try {
                    if (Config.DELETE_DAYS == 0) {
                        gameClient.deleteCharacterInSlot(CharacterDelete.this.aaI);
                    } else {
                        gameClient.markToDeleteChar(CharacterDelete.this.aaI);
                    }
                    gameClient.sendPacket((L2GameServerPacket)new CharacterDeleteSuccess());
                }
                finally {
                    CharacterSelectionInfo characterSelectionInfo = new CharacterSelectionInfo(gameClient.getLogin(), gameClient.getSessionKey().playOkID1);
                    gameClient.sendPacket((L2GameServerPacket)characterSelectionInfo);
                    gameClient.setCharSelection(characterSelectionInfo.getCharInfo());
                }
            }
        };
        if (Config.USE_SECOND_PASSWORD_AUTH && !gameClient.isSecondPasswordAuthed()) {
            if (gameClient.getSecondPasswordAuth().isSecondPasswordSet()) {
                if (gameClient.getSecondPasswordAuth().getUI() == null) {
                    gameClient.getSecondPasswordAuth().setUI(new SecondPasswordAuth.SecondPasswordAuthUI(SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType.VERIFY));
                }
            } else if (gameClient.getSecondPasswordAuth().getUI() == null) {
                gameClient.getSecondPasswordAuth().setUI(new SecondPasswordAuth.SecondPasswordAuthUI(SecondPasswordAuth.SecondPasswordAuthUI.SecondPasswordAuthUIType.CREATE));
            }
            gameClient.getSecondPasswordAuth().getUI().verify(gameClient, runnableImpl);
        } else {
            ThreadPoolManager.getInstance().execute(runnableImpl);
        }
    }

    private int ds() {
        int n = ((GameClient)this.getClient()).getObjectIdForSlot(this.aaI);
        if (n == -1) {
            return 0;
        }
        if (mysql.simple_get_int("clanid", "characters", "obj_Id=" + n) > 0) {
            if (mysql.simple_get_int("leader_id", "clan_subpledges", "leader_id=" + n + " AND type = " + 0) > 0) {
                return 2;
            }
            return 1;
        }
        return 0;
    }

    private int dt() {
        int n = ((GameClient)this.getClient()).getObjectIdForSlot(this.aaI);
        if (n == -1) {
            return 0;
        }
        if (mysql.simple_get_int("online", "characters", "obj_Id=" + n) > 0) {
            return 1;
        }
        return 0;
    }
}

