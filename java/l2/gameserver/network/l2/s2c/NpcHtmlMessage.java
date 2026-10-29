/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NpcHtmlMessage
extends L2GameServerPacket {
    protected static final Logger _log = LoggerFactory.getLogger(NpcHtmlMessage.class);
    protected static final Pattern objectId = Pattern.compile("%objectId%");
    protected static final Pattern playername = Pattern.compile("%playername%");
    protected int _npcObjId;
    protected String _html;
    protected String _file = null;
    protected List<String> _replaces = new ArrayList<String>();
    protected boolean have_appends = false;

    public NpcHtmlMessage(Player player, int n, String string, int n2) {
        List<Scripts.ScriptClassAndMethod> list = Scripts.dialogAppends.get(n);
        if (list != null && list.size() > 0) {
            this.have_appends = true;
            if (string != null && string.equalsIgnoreCase("npcdefault.htm")) {
                this.setHtml("");
            } else {
                this.setFile(string);
            }
            String string2 = "";
            Object[] objectArray = new Object[]{Integer.valueOf(n2)};
            for (Scripts.ScriptClassAndMethod scriptClassAndMethod : list) {
                Object object = Scripts.getInstance().callScripts(player, scriptClassAndMethod.className, scriptClassAndMethod.methodName, objectArray);
                if (object == null) continue;
                string2 = string2 + object;
            }
            if (!string2.equals("")) {
                this.replace("</body>", "\n" + Strings.bbParse(string2) + "</body>");
            }
        } else {
            this.setFile(string);
        }
    }

    public NpcHtmlMessage(Player player, NpcInstance npcInstance, String string, int n) {
        this(player, npcInstance.getNpcId(), string, n);
        this._npcObjId = npcInstance.getObjectId();
        player.setLastNpc(npcInstance);
        this.replace("%npcId%", String.valueOf(npcInstance.getNpcId()));
        this.replace("%npcname%", npcInstance.getName());
        this.replace("%festivalMins%", SevenSignsFestival.getInstance().getTimeToNextFestivalStr());
    }

    public NpcHtmlMessage(Player player, NpcInstance npcInstance) {
        if (npcInstance == null) {
            this._npcObjId = 5;
            player.setLastNpc(null);
        } else {
            this._npcObjId = npcInstance.getObjectId();
            player.setLastNpc(npcInstance);
        }
    }

    public NpcHtmlMessage(int n) {
        this._npcObjId = n;
    }

    public final NpcHtmlMessage setHtml(String string) {
        if (!string.contains("<html>")) {
            string = "<html><body>" + string + "</body></html>";
        }
        this._html = string;
        return this;
    }

    public final NpcHtmlMessage setFile(String string) {
        this._file = string;
        if (this._file.startsWith("data/html/")) {
            _log.info("NpcHtmlMessage: need fix : " + string, new Exception());
            this._file = this._file.replace("data/html/", "");
        }
        return this;
    }

    public NpcHtmlMessage replace(String string, String string2) {
        if (string == null || string2 == null) {
            return this;
        }
        this._replaces.add(string);
        this._replaces.add(string2);
        return this;
    }

    public NpcHtmlMessage replaceNpcString(String string, NpcString npcString, Object ... objectArray) {
        if (string == null) {
            return this;
        }
        if (npcString.getSize() != objectArray.length) {
            throw new IllegalArgumentException("Not valid size of parameters: " + (Object)((Object)npcString));
        }
        this._replaces.add(string);
        this._replaces.add(HtmlUtils.htmlNpcString(npcString, objectArray));
        return this;
    }

    public void processHtml(GameClient gameClient) {
        Player player = gameClient.getActiveChar();
        if (this._file != null) {
            if (player != null && player.isGM()) {
                Functions.sendDebugMessage(player, "HTML: " + this._file);
            }
            String string = HtmCache.getInstance().getNotNull(this._file, player);
            String string2 = HtmCache.getInstance().getNullable(this._file, player);
            if (string2 == null) {
                this.setHtml(this.have_appends && this._file.endsWith(".htm") ? "" : string);
            } else {
                this.setHtml(string);
            }
        }
        for (int i = 0; i < this._replaces.size(); i += 2) {
            this._html = this._html.replace(this._replaces.get(i), this._replaces.get(i + 1));
        }
        if (this._html == null) {
            return;
        }
        Matcher matcher = objectId.matcher(this._html);
        if (matcher != null) {
            this._html = matcher.replaceAll(String.valueOf(this._npcObjId));
        }
        if (player != null) {
            this._html = playername.matcher(this._html).replaceAll(player.getName());
        }
        gameClient.cleanBypasses(false);
        this._html = gameClient.encodeBypasses(this._html, false);
    }

    @Override
    protected void writeImpl() {
        if (this._html != null) {
            this.writeC(15);
            this.writeD(this._npcObjId);
            this.writeS(this._html);
            this.writeD(0);
        }
    }
}
