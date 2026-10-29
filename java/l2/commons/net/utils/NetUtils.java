/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.utils;

import l2.commons.net.utils.Net;
import l2.commons.net.utils.NetList;

public class NetUtils {
    private static final NetList rt = new NetList();

    public static final boolean isInternalIP(String string) {
        return rt.isInRange(string);
    }

    static {
        rt.add(Net.valueOf("127.0.0.0/8"));
        rt.add(Net.valueOf("10.0.0.0/8"));
        rt.add(Net.valueOf("172.16.0.0/12"));
        rt.add(Net.valueOf("192.168.0.0/16"));
        rt.add(Net.valueOf("169.254.0.0/16"));
    }
}

