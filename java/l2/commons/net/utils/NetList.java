/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.utils;

import java.util.ArrayList;
import java.util.Iterator;
import l2.commons.net.utils.Net;

public final class NetList
extends ArrayList<Net> {
    private static final long serialVersionUID = 4266033257195615387L;

    public boolean isInRange(String string) {
        for (Net net : this) {
            if (!net.isInRange(string)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            if (!iterator.hasNext()) continue;
            stringBuilder.append(',');
        }
        return stringBuilder.toString();
    }
}

