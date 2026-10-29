/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet.commands;

import java.util.LinkedHashSet;
import java.util.Set;
import l2.gameserver.Config;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;

public class TelnetConfig
implements TelnetCommandHolder {
    private Set<TelnetCommand> aoa = new LinkedHashSet<TelnetCommand>();

    public TelnetConfig() {
        this.aoa.add(new TelnetCommand("config", new String[]{"cfg"}){

            @Override
            public String getUsage() {
                return "config parameter[=value]";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length == 0 || stringArray[0].isEmpty()) {
                    return null;
                }
                String[] stringArray2 = stringArray[0].split("=");
                if (stringArray2.length == 1) {
                    String string = Config.getField(stringArray[0]);
                    return string == null ? "Not found.\n" : string + "\n";
                }
                if (Config.setField(stringArray2[0], stringArray2[1])) {
                    return "Done.\n";
                }
                return "Error!\n";
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return this.aoa;
    }
}

