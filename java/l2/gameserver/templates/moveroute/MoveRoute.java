/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.moveroute;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.templates.moveroute.MoveNode;
import l2.gameserver.templates.moveroute.MoveRouteType;

public class MoveRoute {
    private final List<MoveNode> avK = new ArrayList<MoveNode>();
    private final String _name;
    private final MoveRouteType avL;
    private final boolean akj;

    public MoveRoute(String string, MoveRouteType moveRouteType, boolean bl) {
        this._name = string;
        this.avL = moveRouteType;
        this.akj = bl;
    }

    public List<MoveNode> getNodes() {
        return this.avK;
    }

    public String getName() {
        return this._name;
    }

    public MoveRouteType getType() {
        return this.avL;
    }

    public boolean isRunning() {
        return this.akj;
    }
}

