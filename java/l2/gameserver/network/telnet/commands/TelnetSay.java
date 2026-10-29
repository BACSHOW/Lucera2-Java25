/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet.commands;

import java.util.LinkedHashSet;
import java.util.Set;
import l2.gameserver.Announcements;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.telnet.TelnetCommand;
import l2.gameserver.network.telnet.TelnetCommandHolder;

public class TelnetSay
implements TelnetCommandHolder {
    private Set<TelnetCommand> aoa = new LinkedHashSet<TelnetCommand>();

    public TelnetSay() {
        this.aoa.add(new TelnetCommand("announce", new String[]{"ann"}){

            @Override
            public String getUsage() {
                return "announce <text>";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length == 0) {
                    return null;
                }
                Announcements.getInstance().announceToAll(stringArray[0]);
                return "Announcement sent.\n";
            }
        });
        this.aoa.add(new TelnetCommand("message", new String[]{"msg"}){

            @Override
            public String getUsage() {
                return "message <player> <text>";
            }

            @Override
            public String handle(String[] stringArray) {
                if (stringArray.length < 2) {
                    return null;
                }
                Player player = World.getPlayer(stringArray[0]);
                if (player == null) {
                    return "Player not found.\n";
                }
                Say2 say2 = new Say2(0, ChatType.TELL, "[Admin]", stringArray[1]);
                player.sendPacket((IStaticPacket)say2);
                return "Message sent.\n";
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return this.aoa;
    }
}

