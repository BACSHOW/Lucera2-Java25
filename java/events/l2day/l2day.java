/*
 * Decompiled with CFR 0.152.
 */
package events.l2day;

import events.l2day.LettersCollection;
import java.util.HashMap;
import java.util.Map;
import l2.gameserver.model.reward.RewardData;

public class l2day
extends LettersCollection {
    private static int la = 3958;
    private static int lb = 3959;
    private static int lc = 3926;
    private static int ld = 3927;
    private static int le = 3928;
    private static int lf = 3929;
    private static int lg = 3930;
    private static int lh = 3931;
    private static int li = 3932;
    private static int lj = 3933;
    private static int lk = 3934;
    private static int ll = 3935;
    private static int lm = 955;
    private static int ln = 951;
    private static int lo = 947;
    private static int lp = 729;
    private static int lq = 8947;
    private static int lr = 8950;
    private static int ls = 8949;
    private static int lt = 8951;
    private static int lu = 8948;
    private static int lv = 6660;
    private static int lw = 6661;
    private static int lx = 6662;
    private static int ly = 8191;

    static {
        _name = "l2day";
        _msgStarted = "scripts.events.l2day.AnnounceEventStarted";
        _msgEnded = "scripts.events.l2day.AnnounceEventStoped";
        _words.put("LineageII", new Integer[][]{{L, 1}, {I, 1}, {N, 1}, {E, 2}, {A, 1}, {G, 1}, {II, 1}});
        _rewards.put("LineageII", new RewardData[]{new RewardData(lc, 3L, 3L, 85000.0), new RewardData(ld, 3L, 3L, 85000.0), new RewardData(le, 3L, 3L, 85000.0), new RewardData(lf, 3L, 3L, 85000.0), new RewardData(lg, 3L, 3L, 85000.0), new RewardData(lh, 3L, 3L, 85000.0), new RewardData(li, 3L, 3L, 85000.0), new RewardData(lj, 3L, 3L, 85000.0), new RewardData(lk, 3L, 3L, 85000.0), new RewardData(ll, 3L, 3L, 85000.0), new RewardData(la, 1L, 1L, 50000.0), new RewardData(lb, 1L, 1L, 50000.0), new RewardData(ln, 3L, 3L, 14000.0), new RewardData(lo, 2L, 2L, 7000.0), new RewardData(lp, 1L, 1L, 7000.0), new RewardData(lq, 1L, 1L, 5000.0), new RewardData(lr, 1L, 1L, 5000.0), new RewardData(ls, 1L, 1L, 5000.0), new RewardData(lv, 1L, 1L, 100.0), new RewardData(lx, 1L, 1L, 100.0)});
        _words.put("Throne", new Integer[][]{{T, 1}, {H, 1}, {R, 1}, {O, 1}, {N, 1}, {E, 1}});
        _rewards.put("Throne", new RewardData[]{new RewardData(lc, 3L, 3L, 85000.0), new RewardData(ld, 3L, 3L, 85000.0), new RewardData(le, 3L, 3L, 85000.0), new RewardData(lf, 3L, 3L, 85000.0), new RewardData(lg, 3L, 3L, 85000.0), new RewardData(lh, 3L, 3L, 85000.0), new RewardData(li, 3L, 3L, 85000.0), new RewardData(lj, 3L, 3L, 85000.0), new RewardData(lk, 3L, 3L, 85000.0), new RewardData(ll, 3L, 3L, 85000.0), new RewardData(la, 1L, 1L, 50000.0), new RewardData(lb, 1L, 1L, 50000.0), new RewardData(lm, 4L, 4L, 16000.0), new RewardData(ln, 3L, 3L, 11000.0), new RewardData(lo, 2L, 2L, 6000.0), new RewardData(lt, 1L, 1L, 6000.0), new RewardData(lu, 1L, 1L, 5000.0), new RewardData(lv, 1L, 1L, 100.0), new RewardData(lx, 1L, 1L, 100.0)});
        _words.put("NCSoft", new Integer[][]{{N, 1}, {C, 1}, {S, 1}, {O, 1}, {F, 1}, {T, 1}});
        _rewards.put("NCSoft", new RewardData[]{new RewardData(lc, 3L, 3L, 85000.0), new RewardData(ld, 3L, 3L, 85000.0), new RewardData(le, 3L, 3L, 85000.0), new RewardData(lf, 3L, 3L, 85000.0), new RewardData(lg, 3L, 3L, 85000.0), new RewardData(lh, 3L, 3L, 85000.0), new RewardData(li, 3L, 3L, 85000.0), new RewardData(lj, 3L, 3L, 85000.0), new RewardData(lk, 3L, 3L, 85000.0), new RewardData(ll, 3L, 3L, 85000.0), new RewardData(la, 1L, 1L, 50000.0), new RewardData(lb, 1L, 1L, 50000.0), new RewardData(lm, 4L, 4L, 16000.0), new RewardData(ln, 3L, 3L, 11000.0), new RewardData(lo, 2L, 2L, 6000.0), new RewardData(lt, 1L, 1L, 6000.0), new RewardData(lu, 1L, 1L, 5000.0), new RewardData(lv, 1L, 1L, 100.0), new RewardData(lx, 1L, 1L, 100.0)});
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (Integer[][] word : _words.values()) {
            for (Integer[] integerArray : word) {
                Integer n = (Integer)hashMap.get(integerArray[0]);
                if (n == null) {
                    hashMap.put(integerArray[0], integerArray[1]);
                    continue;
                }
                hashMap.put(integerArray[0], n + integerArray[1]);
            }
        }
        letters = new int[hashMap.size()][2];
        int n = 0;
        for (Map.Entry entry : hashMap.entrySet()) {
            l2day.letters[n++] = new int[]{(Integer)entry.getKey(), (Integer)entry.getValue() * 3};
        }
    }
}
