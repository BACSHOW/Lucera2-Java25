/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet;

import java.util.Set;
import l2.gameserver.network.telnet.TelnetCommand;

public interface TelnetCommandHolder {
    public Set<TelnetCommand> getCommands();
}

