/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet.commands;

import java.util.LinkedHashSet;
import java.util.Set;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;
import l2.gameserver.utils.ItemFunctions;

public class TelnetItems
implements TelnetCommandHolder {
    private Set<TelnetCommand> aoa = new LinkedHashSet<TelnetCommand>();

    public TelnetItems() {
        this.aoa.add(new TelnetCommand("give_item"){

            @Override
            public String getUsage() {
                return "give_item <name> <id> <count>";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length != 3 || stringArray[0].isEmpty() || stringArray[1].isEmpty() || stringArray[2].isEmpty()) {
                    return null;
                }
                try {
                    int n = Integer.parseInt(stringArray[1]);
                    long l = Integer.parseInt(stringArray[2]);
                    String string = stringArray[0];
                    boolean bl = false;
                    for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                        if (!player.getName().equals(string)) continue;
                        ItemFunctions.addItem((Playable)player, n, l, true);
                        bl = true;
                    }
                    return bl ? "Issued item.\n" : "Player not found.\n";
                }
                catch (Exception exception) {
                    return exception.getMessage();
                }
            }
        });
        this.aoa.add(new TelnetCommand("remove_item"){

            @Override
            public String getUsage() {
                return "remove_item <name> <id> <count>";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length != 3 || stringArray[0].isEmpty() || stringArray[1].isEmpty() || stringArray[2].isEmpty()) {
                    return null;
                }
                try {
                    int n = Integer.parseInt(stringArray[1]);
                    long l = Integer.parseInt(stringArray[2]);
                    String string = stringArray[0];
                    boolean bl = false;
                    for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                        if (!player.getName().equals(string)) continue;
                        ItemFunctions.removeItem(player, n, l, true);
                        bl = true;
                    }
                    return bl ? "Item removed.\n" : "Player not found.\n";
                }
                catch (Exception exception) {
                    return exception.getMessage();
                }
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return this.aoa;
    }
}

