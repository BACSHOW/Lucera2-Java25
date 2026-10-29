/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.parser.ChatFilterParser;
import l2.gameserver.data.xml.parser.ItemParser;
import l2.gameserver.data.xml.parser.NpcParser;
import l2.gameserver.data.xml.parser.PromoCodeParser;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.AutoAnnounce;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.tables.FishTable;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Strings;

public class AdminReload
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanReload) {
            return false;
        }
        switch (commands) {
            case admin_reload: {
                break;
            }
            case admin_reload_config: {
                try {
                    Config.load();
                }
                catch (Exception exception) {
                    player.sendMessage("Error: " + exception.getMessage() + "!");
                    return false;
                }
                player.sendMessage("Config reloaded!");
                break;
            }
            case admin_reload_multisell: {
                try {
                    MultiSellHolder.getInstance().reload();
                }
                catch (Exception exception) {
                    return false;
                }
                player.sendMessage("Multisell list reloaded!");
                break;
            }
            case admin_reload_gmaccess: {
                try {
                    Config.loadGMAccess();
                    for (Player player2 : GameObjectsStorage.getAllPlayersForIterate()) {
                        if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS) {
                            player2.setPlayerAccess(Config.gmlist.get(player2.getObjectId()));
                            continue;
                        }
                        player2.setPlayerAccess(Config.gmlist.get(Integer.valueOf(0)));
                    }
                }
                catch (Exception exception) {
                    return false;
                }
                player.sendMessage("GMAccess reloaded!");
                break;
            }
            case admin_reload_htm: {
                HtmCache.getInstance().clear();
                player.sendMessage("HTML cache clearned.");
                break;
            }
            case admin_reload_qr: {
                Config.loadQuestRateSettings();
                player.sendMessage("Quest rates reloaded.");
                break;
            }
            case admin_reload_qs: {
                if (string.endsWith("all")) {
                    for (Player player3 : GameObjectsStorage.getAllPlayersForIterate()) {
                        this.y(player3);
                    }
                    break;
                }
                GameObject gameObject = player.getTarget();
                if (gameObject != null && gameObject.isPlayer()) {
                    Player player4 = (Player)gameObject;
                    this.y(player4);
                    break;
                }
                this.y(player);
                break;
            }
            case admin_reload_qs_help: {
                player.sendMessage("");
                player.sendMessage("Quest Help:");
                player.sendMessage("reload_qs_help - This Message.");
                player.sendMessage("reload_qs <selected target> - reload all quest states for target.");
                player.sendMessage("reload_qs <no target or target is not player> - reload quests for self.");
                player.sendMessage("reload_qs all - reload quests for all players in world.");
                player.sendMessage("");
                break;
            }
            case admin_reload_skills: {
                SkillTable.getInstance().reload();
                break;
            }
            case admin_reload_items: {
                ItemParser.getInstance().reload();
                break;
            }
            case admin_reload_npc: {
                NpcParser.getInstance().reload();
                break;
            }
            case admin_reload_spawn: {
                ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        SpawnManager.getInstance().reloadAll();
                    }
                });
                break;
            }
            case admin_reload_fish: {
                FishTable.getInstance().reload();
                break;
            }
            case admin_reload_translit: {
                Strings.reload();
                break;
            }
            case admin_reload_shops: {
                BuyListHolder.reload();
                break;
            }
            case admin_reload_static: {
                break;
            }
            case admin_reload_chatfilters: {
                ChatFilterParser.getInstance().reload();
                break;
            }
            case admin_reload_auto_announce: {
                AutoAnnounce.getInstance().reload();
                break;
            }
            case admin_reload_pets: {
                PetDataTable.getInstance().reload();
                break;
            }
            case admin_reload_locale: {
                StringHolder.getInstance().reload();
                break;
            }
            case admin_reload_nobles: {
                NoblesController.getInstance().LoadNobleses();
                break;
            }
            case admin_reload_promo: {
                PromoCodeParser.getInstance().reload();
            }
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/reload.htm"));
        return true;
    }

    private void y(Player player) {
        for (QuestState questState : player.getAllQuestsStates()) {
            player.removeQuestState(questState.getQuest().getName());
        }
        Quest.restoreQuestStates(player);
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_reload,
        admin_reload_config,
        admin_reload_multisell,
        admin_reload_gmaccess,
        admin_reload_htm,
        admin_reload_qr,
        admin_reload_qs,
        admin_reload_qs_help,
        admin_reload_skills,
        admin_reload_items,
        admin_reload_npc,
        admin_reload_spawn,
        admin_reload_fish,
        admin_reload_chatfilters,
        admin_reload_auto_announce,
        admin_reload_translit,
        admin_reload_shops,
        admin_reload_static,
        admin_reload_pets,
        admin_reload_locale,
        admin_reload_nobles,
        admin_reload_promo;

    }
}
