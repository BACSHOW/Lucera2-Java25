/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.CharSelected;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.RestartResponse;
import l2.gameserver.scripts.Functions;

public class Relog
extends Functions
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"relog", "restart"};

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALT_ALLOW_RELOG_COMMAND) {
            return false;
        }
        if (string.equals("relog") || string.equals("restart")) {
            if (player == null) {
                return false;
            }
            if (player.isInObserverMode()) {
                player.sendPacket(Msg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponse.FAIL, ActionFail.STATIC);
                return false;
            }
            if (player.isInCombat()) {
                player.sendPacket(Msg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponse.FAIL, ActionFail.STATIC);
                return false;
            }
            if (player.isFishing()) {
                player.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING, RestartResponse.FAIL, ActionFail.STATIC);
                return false;
            }
            if (player.isOlyParticipant()) {
                player.sendPacket(Msg.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT, RestartResponse.FAIL, ActionFail.STATIC);
                return false;
            }
            if (player.isInZone(Zone.ZoneType.epic)) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.epiczone", player, new Object[0]));
                return false;
            }
            if (player.isBlocked() && !player.isFlying()) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.OutOfControl", player, new Object[0]));
                player.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
                return false;
            }
            if (player.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized()) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.Festival", player, new Object[0]));
                player.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
                return false;
            }
            final GameClient gameClient = player.getNetConnection();
            if (gameClient == null || !gameClient.isConnected()) {
                return false;
            }
            gameClient.setState(GameClient.GameClientState.AUTHED);
            Player player2 = player;
            synchronized (player2) {
                final int n = player.getObjectId();
                RunnableImpl runnableImpl = new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        if (gameClient == null || !gameClient.isConnected() || !gameClient.isAuthed()) {
                            return;
                        }
                        if (Config.USE_SECOND_PASSWORD_AUTH && !gameClient.isSecondPasswordAuthed()) {
                            return;
                        }
                        int n2 = gameClient.getSlotForObjectId(n);
                        if (n2 < 0) {
                            return;
                        }
                        Player player = gameClient.loadCharFromDisk(n2);
                        gameClient.setState(GameClient.GameClientState.IN_GAME);
                        gameClient.sendPacket((L2GameServerPacket)new CharSelected(player, gameClient.getSessionKey().playOkID1));
                    }
                };
                player.restart();
                CharacterSelectionInfo characterSelectionInfo = new CharacterSelectionInfo(gameClient.getLogin(), gameClient.getSessionKey().playOkID1);
                gameClient.sendPacket(RestartResponse.OK, characterSelectionInfo);
                gameClient.setCharSelection(characterSelectionInfo.getCharInfo());
                ThreadPoolManager.getInstance().schedule(runnableImpl, 333L);
            }
            return true;
        }
        return false;
    }
}

