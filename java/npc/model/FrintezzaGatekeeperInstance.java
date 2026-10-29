/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import bosses.FrintezzaManager;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public final class FrintezzaGatekeeperInstance
extends NpcInstance {
    public FrintezzaGatekeeperInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        block15: {
            if (!FrintezzaGatekeeperInstance.canBypassCheck(player, this)) {
                return;
            }
            if (string.equalsIgnoreCase("request_frintezza")) {
                try {
                    if (!FrintezzaManager.getInstance().isInUse()) {
                        if (FrintezzaManager.getInstance().canEnter()) {
                            if (player.getParty() == null || player.getParty().getCommandChannel() == null) {
                                player.sendMessage(new CustomMessage("scripts.FrintezzaGatekeeperInstance.OnlyLeader", player, new Object[0]));
                                return;
                            }
                            CommandChannel commandChannel = player.getParty().getCommandChannel();
                            List<Party> list = commandChannel.getParties();
                            if (list.size() < Config.FRINTEZZA_MIN_PARTY_IN_CC || list.size() > Config.FRINTEZZA_MAX_PARTY_IN_CC) {
                                player.sendMessage(new CustomMessage("scripts.FrintezzaGatekeeperInstance.TooSmallOrToLarge", player, new Object[0]));
                                return;
                            }
                            if (commandChannel.getChannelLeader() != player) {
                                player.sendMessage(new CustomMessage("scripts.FrintezzaGatekeeperInstance.OnlyLeader", player, new Object[0]));
                                return;
                            }
                            for (Party party : commandChannel.getParties()) {
                                for (Player player2 : party.getPartyMembers()) {
                                    if (player2.getLevel() < 74) {
                                        player.sendMessage(new CustomMessage("scripts.FrintezzaGatekeeperInstance.PlayerLevelToLow", player, new Object[0]).addString(player2.getName()));
                                        return;
                                    }
                                    if (!(this.getDistance(player2) > (double)Config.FRINTEZZA_MIN_DISTANCE_FOR_ENTRANCE)) continue;
                                    player.sendMessage(player2.getName() + " to far.");
                                    return;
                                }
                            }
                            if (ItemFunctions.removeItem(player, 8073, 1L, true) < 1L) {
                                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                                return;
                            }
                            FrintezzaManager.getInstance().tryEnter(list);
                        } else {
                            player.sendMessage(new CustomMessage("scripts.FrintezzaGatekeeperInstance.StillReborn", player, new Object[0]));
                        }
                        break block15;
                    }
                    player.sendMessage(new CustomMessage("scripts.FrintezzaGatekeeperInstance.UnderAttack", player, new Object[0]));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                super.onBypassFeedback(player, string);
            }
        }
    }
}

