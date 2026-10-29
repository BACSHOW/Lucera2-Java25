/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.dao.AccountBonusDAO;
import l2.gameserver.dao.CharacterVariablesDAO;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Bonus;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.SecondPasswordAuth;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.CharSelected;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.AutoBan;
import org.apache.commons.lang3.StringUtils;

public class CharacterSelected
extends L2GameClientPacket {
    private int aaI;

    @Override
    protected void readImpl() {
        this.aaI = this.readD();
    }

    @Override
    protected void runImpl() {
        String string;
        int n;
        final GameClient gameClient = (GameClient)this.getClient();
        if (gameClient.getActiveChar() != null) {
            return;
        }
        int n2 = gameClient.getObjectIdForSlot(this.aaI);
        if (AutoBan.isBanned(n2)) {
            this.sendPacket(ActionFail.STATIC);
            return;
        }
        String string2 = CharacterVariablesDAO.getInstance().getVar(n2, "hwidlock@");
        if (!(string2 == null || string2.isEmpty() || gameClient.getHwid() == null || gameClient.getHwid().isEmpty() || string2.equalsIgnoreCase(gameClient.getHwid()))) {
            this.sendPacket((L2GameServerPacket)new ExShowScreenMessage("HWID is locked.", 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
            this.sendPacket(ActionFail.STATIC);
            return;
        }
        Bonus bonus = null;
        if (Config.ALT_LIMIT_HWID_WINDOW != 0 && !StringUtils.isEmpty(gameClient.getHwid())) {
            int n3 = 0;
            for (Player object : GameObjectsStorage.getAllPlayersForIterate()) {
                GameClient gameClient2;
                if (object == null || object.isLogoutStarted() || (gameClient2 = object.getNetConnection()) == null || !gameClient2.isConnected() || !StringUtils.equals(gameClient.getHwid(), gameClient2.getHwid())) continue;
                ++n3;
            }
            bonus = AccountBonusDAO.getInstance().load(gameClient.getLogin());
            n = Config.ALT_LIMIT_HWID_WINDOW;
            if (bonus != null && bonus.getHwidsLimit() > n3) {
                n = bonus.getHwidsLimit();
            }
            if (n3 >= n) {
                this.sendPacket((L2GameServerPacket)new ExShowScreenMessage("Number of windows is limited by " + n + " HWID(s)", 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                this.sendPacket(ActionFail.STATIC);
                return;
            }
        }
        if (Config.ALT_LIMIT_PREMIUM_ACCOUNT) {
            if (bonus == null) {
                bonus = AccountBonusDAO.getInstance().load(gameClient.getLogin());
            }
            if (bonus.isExpired()) {
                this.sendPacket((L2GameServerPacket)new ExShowScreenMessage("The Game available with a Premium Account only", 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                this.sendPacket(ActionFail.STATIC);
                return;
            }
        }
        if (!((string = CharacterVariablesDAO.getInstance().getVar(n2, "iplock@")) == null || string.isEmpty() || gameClient.getIpAddr() == null || gameClient.getIpAddr().isEmpty() || string.equalsIgnoreCase(gameClient.getIpAddr()))) {
            this.sendPacket((L2GameServerPacket)new ExShowScreenMessage("IP address is locked.", 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
            this.sendPacket(ActionFail.STATIC);
            return;
        }
        if (Config.ALT_LIMIT_IP_WINDOW != 0 && !StringUtils.isEmpty(gameClient.getIpAddr())) {
            n = 0;
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                GameClient gameClient3;
                if (player == null || player.isLogoutStarted() || (gameClient3 = player.getNetConnection()) == null || !gameClient3.isConnected() || !StringUtils.equals(gameClient.getIpAddr(), gameClient3.getIpAddr())) continue;
                ++n;
            }
            if (n >= Config.ALT_LIMIT_IP_WINDOW) {
                this.sendPacket((L2GameServerPacket)new ExShowScreenMessage("The number of windows is limited by " + Config.ALT_LIMIT_IP_WINDOW + " IP", 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                this.sendPacket(ActionFail.STATIC);
                return;
            }
        }
        RunnableImpl runnableImpl = new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                Player player = gameClient.loadCharFromDisk(CharacterSelected.this.aaI);
                if (player == null) {
                    CharacterSelected.this.sendPacket(ActionFail.STATIC);
                    return;
                }
                if (player.getAccessLevel() < 0) {
                    player.setAccessLevel(0);
                }
                gameClient.setState(GameClient.GameClientState.IN_GAME);
                gameClient.sendPacket((L2GameServerPacket)new CharSelected(player, gameClient.getSessionKey().playOkID1));
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
}

