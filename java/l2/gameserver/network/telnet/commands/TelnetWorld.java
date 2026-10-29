/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet.commands;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;
import l2.gameserver.tables.GmListTable;

public class TelnetWorld
implements TelnetCommandHolder {
    private Set<TelnetCommand> aoa = new LinkedHashSet<TelnetCommand>();

    public TelnetWorld() {
        this.aoa.add(new TelnetCommand("find"){

            @Override
            public String getUsage() {
                return "find <name>";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length == 0) {
                    return null;
                }
                Iterable<Player> iterable = GameObjectsStorage.getAllPlayersForIterate();
                Iterator<Player> iterator = iterable.iterator();
                StringBuilder stringBuilder = new StringBuilder();
                int n = 0;
                Pattern pattern = Pattern.compile(stringArray[0] + "\\S+", 2);
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    if (!pattern.matcher(player.getName()).matches()) continue;
                    ++n;
                    stringBuilder.append(player).append("\n");
                }
                if (n == 0) {
                    stringBuilder.append("Player not found.").append("\n");
                } else {
                    stringBuilder.append("=================================================\n");
                    stringBuilder.append("Found: ").append(n).append(" players.").append("\n");
                }
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("whois", new String[]{"who"}){

            @Override
            public String getUsage() {
                return "whois <name>";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length == 0) {
                    return null;
                }
                Player player = GameObjectsStorage.getPlayer(stringArray[0]);
                if (player == null) {
                    return "Player not found.\n";
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Name: .................... ").append(player.getName()).append("\n");
                stringBuilder.append("ID: ...................... ").append(player.getObjectId()).append("\n");
                stringBuilder.append("Account Name: ............ ").append(player.getAccountName()).append("\n");
                stringBuilder.append("IP: ...................... ").append(player.getIP()).append("\n");
                stringBuilder.append("Level: ................... ").append(player.getLevel()).append("\n");
                stringBuilder.append("Location: ................ ").append(player.getLoc()).append("\n");
                if (player.getClan() != null) {
                    stringBuilder.append("Clan: .................... ").append(player.getClan().getName()).append("\n");
                    if (player.getAlliance() != null) {
                        stringBuilder.append("Ally: .................... ").append(player.getAlliance().getAllyName()).append("\n");
                    }
                }
                stringBuilder.append("Offline: ................. ").append(player.isInOfflineMode()).append("\n");
                stringBuilder.append(player.toString()).append("\n");
                return stringBuilder.toString();
            }
        });
        this.aoa.add(new TelnetCommand("gmlist", new String[]{"gms"}){

            @Override
            public String getUsage() {
                return "gmlist";
            }

            @Override
            public String handle(String[] stringArray) {
                List<Player> list = GmListTable.getAllGMs();
                int n = list.size();
                if (n == 0) {
                    return "GMs not found.\n";
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < n; ++i) {
                    stringBuilder.append(list.get(i)).append("\n");
                }
                stringBuilder.append("Found: ").append(n).append(" GMs.").append("\n");
                return stringBuilder.toString();
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return this.aoa;
    }
}

