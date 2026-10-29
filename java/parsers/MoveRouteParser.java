/*
 * Decompiled with CFR 0.152.
 */
package parsers;

import java.io.File;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.moveroute.MoveNode;
import l2.gameserver.templates.moveroute.MoveRoute;
import l2.gameserver.templates.moveroute.MoveRouteType;
import org.dom4j.Element;
import parsers.MoveRouteHolder;

public class MoveRouteParser
extends AbstractDirParser<MoveRouteHolder>
implements ScriptFile {
    private static MoveRouteParser azm;

    public static MoveRouteParser getInstance() {
        if (azm == null) {
            azm = new MoveRouteParser();
            azm.load();
        }
        return azm;
    }

    public MoveRouteParser() {
        super(MoveRouteHolder.getInstance());
    }

    @Override
    public File getXMLDir() {
        return new File(Config.DATAPACK_ROOT, "./data/superpointinfo");
    }

    @Override
    public boolean isIgnored(File file) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "superpointinfo.dtd";
    }

    @Override
    protected void readData(Element element) throws Exception {
        for (Element element2 : element.elements()) {
            String string = element2.attributeValue("name");
            MoveRouteType moveRouteType = MoveRouteType.valueOf(element2.attributeValue("type"));
            String string2 = element2.attributeValue("is_running");
            MoveRoute moveRoute = new MoveRoute(string, moveRouteType, string2 != null ? Boolean.parseBoolean(string2) : false);
            for (Element element3 : element2.elements()) {
                int n = Integer.parseInt(element3.attributeValue("x"));
                int n2 = Integer.parseInt(element3.attributeValue("y"));
                int n3 = Integer.parseInt(element3.attributeValue("z"));
                int n4 = Integer.parseInt(element3.attributeValue("social", "0"));
                long l = Long.parseLong(element3.attributeValue("delay", "0"));
                String string3 = element3.attributeValue("msg_addr");
                ChatType chatType = null;
                if (string3 != null) {
                    chatType = ChatType.valueOf(element3.attributeValue("chat_type"));
                }
                MoveNode moveNode = new MoveNode(n, n2, n3, string3, n4, l, chatType);
                moveRoute.getNodes().add(moveNode);
            }
            ((MoveRouteHolder)this.getHolder()).addRoute(moveRoute);
        }
    }

    @Override
    public void onLoad() {
        MoveRouteParser.getInstance();
    }

    @Override
    public void onReload() {
        MoveRouteParser.getInstance().reload();
    }

    @Override
    public void onShutdown() {
    }
}

