/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.pfilter;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.LeaveWorld;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;

abstract class LimitAction {
    private static final LimitAction anI = new LimitAction(){

        @Override
        public void doIt(GameClient gameClient) {
        }
    };
    private static final LimitAction anJ = new LimitAction(){

        @Override
        public void doIt(GameClient gameClient) {
            gameClient.sendPacket(ActionFail.STATIC);
        }
    };
    private static final LimitAction anK = new LimitAction(){

        @Override
        public void doIt(GameClient gameClient) {
            Player player = gameClient.getActiveChar();
            if (player != null) {
                player.kick();
            } else {
                gameClient.close(LeaveWorld.STATIC);
            }
        }
    };

    LimitAction() {
    }

    public static LimitAction doDrop() {
        return anI;
    }

    public static LimitAction doActionFail() {
        return anJ;
    }

    public static LimitAction doKick() {
        return anK;
    }

    public static LimitAction doLog(String string) {
        return new DoLogLimitAction(string);
    }

    public static LimitAction doMsg(String string) {
        return new DoMsgLimitAction(string);
    }

    public abstract void doIt(GameClient var1);

    public boolean dropPacket() {
        return true;
    }

    private static final class DoMsgLimitAction
    extends LimitAction {
        private final String text;

        private DoMsgLimitAction(String string) {
            this.text = string;
        }

        @Override
        public void doIt(GameClient gameClient) {
            gameClient.sendPacket((L2GameServerPacket)new SystemMessage(this.text));
        }

        @Override
        public boolean dropPacket() {
            return false;
        }
    }

    private static final class DoLogLimitAction
    extends LimitAction {
        private final String anL;

        private DoLogLimitAction(String string) {
            this.anL = string;
        }

        @Override
        public void doIt(GameClient gameClient) {
            String string = this.anL;
            Player player = gameClient.getActiveChar();
            string = string.replace("%user_name%", player != null ? player.getName() : "");
            string = string.replace("%account_name%", gameClient.getLogin());
            string = string.replace("%ip%", gameClient.getIpAddr() != null ? gameClient.getIpAddr() : "?.?.?.?");
            string = string.replace("%hwid%", gameClient.getHwid() != null ? gameClient.getHwid() : "EMPTY HWID");
            Log.network("RateLimit: " + string);
        }

        @Override
        public boolean dropPacket() {
            return false;
        }
    }
}

