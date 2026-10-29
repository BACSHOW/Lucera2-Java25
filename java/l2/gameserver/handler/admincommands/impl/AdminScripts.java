/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Scripts;

public class AdminScripts
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.isGM()) {
            return false;
        }
        switch (commands) {
            case admin_scripts_reload: 
            case admin_sreload: {
                if (stringArray.length < 2) {
                    return false;
                }
                String string2 = stringArray[1];
                if (string2.equalsIgnoreCase("all")) {
                    player.sendMessage("Scripts reload starting...");
                    if (!Scripts.getInstance().reload()) {
                        player.sendMessage("Scripts reloaded with errors. Loaded " + Scripts.getInstance().getClasses().size() + " classes.");
                        break;
                    }
                    player.sendMessage("Scripts successfully reloaded. Loaded " + Scripts.getInstance().getClasses().size() + " classes.");
                    break;
                }
                if (!Scripts.getInstance().reload(string2)) {
                    player.sendMessage("Script(s) reloaded with errors.");
                    break;
                }
                player.sendMessage("Script(s) successfully reloaded.");
                break;
            }
            case admin_sqreload: {
                if (stringArray.length < 2) {
                    return false;
                }
                String string3 = stringArray[1];
                if (!Scripts.getInstance().reload("quests/" + string3)) {
                    player.sendMessage("Quest \"" + string3 + "\" reloaded with errors.");
                } else {
                    player.sendMessage("Quest \"" + string3 + "\" successfully reloaded.");
                }
                this.y(player);
            }
        }
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
        admin_scripts_reload,
        admin_sreload,
        admin_sqreload;

    }
}

