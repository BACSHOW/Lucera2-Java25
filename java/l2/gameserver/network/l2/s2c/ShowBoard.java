/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.nio.charset.Charset;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowBoard
extends L2GameServerPacket {
    private static final Logger _log = LoggerFactory.getLogger(ShowBoard.class);
    private static final Charset amu = Charset.forName("UTF-16LE");
    private String amv;
    private String amw;
    private List<String> amx;
    private String amy = "";

    public static void separateAndSend(String string, Player player) {
        string = player.getNetConnection().encodeBypasses(string, true);
        byte[] byArray = string.getBytes(amu);
        if (byArray.length < 8180) {
            player.sendPacket((IStaticPacket)new ShowBoard(string, "101", player, false));
            player.sendPacket((IStaticPacket)new ShowBoard(null, "102", player, false));
            player.sendPacket((IStaticPacket)new ShowBoard(null, "103", player, false));
        } else if (byArray.length < 16360) {
            player.sendPacket((IStaticPacket)new ShowBoard(new String(byArray, 0, 8180, amu), "101", player, false));
            player.sendPacket((IStaticPacket)new ShowBoard(new String(byArray, 8180, byArray.length - 8180, amu), "102", player, false));
            player.sendPacket((IStaticPacket)new ShowBoard(null, "103", player, false));
        } else {
            player.sendPacket((IStaticPacket)new ShowBoard(new String(byArray, 0, 8180, amu), "101", player, false));
            player.sendPacket((IStaticPacket)new ShowBoard(new String(byArray, 8180, byArray.length - 8180, amu), "102", player, false));
            player.sendPacket((IStaticPacket)new ShowBoard(new String(byArray, 16360, byArray.length - 16360, amu), "103", player, false));
        }
    }

    private ShowBoard(String string, String string2, Player player, boolean bl) {
        if (string != null && string.length() > 8192) {
            _log.warn("Html '" + string + "' is too long! this will crash the client!");
            this.amv = "<html><body>Html was too long</body></html>";
            return;
        }
        this.amw = string2;
        if (player.getSessionVar("add_fav") != null) {
            this.amy = "bypass _bbsaddfav_List";
        }
        this.amv = string != null ? (bl ? player.getNetConnection().encodeBypasses(string, true) : string) : null;
    }

    public ShowBoard(String string, String string2, Player player) {
        this(string, string2, player, true);
    }

    public ShowBoard(List<String> list) {
        this.amw = "1002";
        this.amv = null;
        this.amx = list;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(110);
        this.writeC(1);
        this.writeS("bypass _bbshome");
        this.writeS("bypass _bbsgetfav");
        this.writeS("bypass _bbsloc");
        this.writeS("bypass _bbsclan");
        this.writeS("bypass _bbsmemo");
        this.writeS("bypass _maillist_0_1_0_");
        this.writeS("bypass _friendlist_0_");
        this.writeS(this.amy);
        String string = this.amw + "\b";
        if (!this.amw.equals("1002")) {
            if (this.amv != null) {
                string = string + this.amv;
            }
        } else {
            for (String string2 : this.amx) {
                string = string + string2 + " \b";
            }
        }
        this.writeS(string);
    }
}

