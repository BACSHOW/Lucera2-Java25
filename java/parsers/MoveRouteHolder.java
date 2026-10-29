/*
 * Decompiled with CFR 0.152.
 */
package parsers;

import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.moveroute.MoveRoute;
import parsers.MoveRouteParser;

public class MoveRouteHolder
extends AbstractHolder {
    private static MoveRouteHolder azk;
    private Map<String, MoveRoute> azl = new HashMap<String, MoveRoute>();

    public static MoveRouteHolder getInstance() {
        if (azk == null) {
            azk = new MoveRouteHolder();
            MoveRouteParser.getInstance();
        }
        return azk;
    }

    private MoveRouteHolder() {
    }

    public void addRoute(MoveRoute moveRoute) {
        if (moveRoute.getNodes().isEmpty()) {
            this._log.warn("Route \"" + moveRoute.getName() + "\" is empty.");
        }
        this.azl.put(moveRoute.getName(), moveRoute);
    }

    public MoveRoute getRoute(String string) {
        return this.azl.get(string);
    }

    @Override
    public int size() {
        return this.azl.size();
    }

    @Override
    public void clear() {
        this.azl.clear();
    }
}

