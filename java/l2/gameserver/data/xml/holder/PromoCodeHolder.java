/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TObjectIntHashMap;
import java.util.HashMap;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.dao.PromoCodeDAO;
import l2.gameserver.dao.PromoCodeLimitDAO;
import l2.gameserver.dao.PromoCodeUserDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.PromoCode;
import l2.gameserver.model.promoCode.PromoCodeReward;

public class PromoCodeHolder
extends AbstractHolder {
    private Map<String, PromoCode> wD = new HashMap<String, PromoCode>();
    private TObjectIntHashMap<String> wE = PromoCodeLimitDAO.INSTANCE.select();
    private static final PromoCodeHolder wF = new PromoCodeHolder();

    public static PromoCodeHolder getInstance() {
        return wF;
    }

    public void addPromoCode(String string, PromoCode promoCode) {
        this.wD.put(string, promoCode);
    }

    public PromoCode getPromoCode(String string) {
        return this.wD.get(string);
    }

    public ActivateResult tryActivate(Player player, String string) {
        return this.tryActivate(player, this.getPromoCode(string));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ActivateResult tryActivate(Player player, PromoCode promoCode) {
        if (promoCode == null) {
            return ActivateResult.INVALID_TRY_LATER;
        }
        String string = player.getAccountName();
        String string2 = player.getNetConnection().getHwid();
        String string3 = player.getNetConnection().getIpAddr();
        int n = player.getLevel();
        String string4 = promoCode.getName();
        if (PromoCodeDAO.INSTANCE.isActivated(string, string4)) {
            return ActivateResult.ALREADY;
        }
        if (promoCode.isLimitByHWID() && PromoCodeDAO.INSTANCE.isHwidActivated(string2, string4)) {
            return ActivateResult.HWID_LIMITED;
        }
        if (promoCode.isLimitByIP() && PromoCodeDAO.INSTANCE.isIpActivated(string3, string4)) {
            return ActivateResult.IP_LIMITED;
        }
        if (promoCode.getMinLevel() > 0 && n < promoCode.getMinLevel()) {
            return ActivateResult.MIN_LEVEL_LIMITED;
        }
        if (promoCode.getLimit() > 0) {
            TObjectIntHashMap<String> tObjectIntHashMap = this.wE;
            synchronized (tObjectIntHashMap) {
                int n2 = this.wE.get(string4);
                if (n2 >= promoCode.getLimit()) {
                    return ActivateResult.OUT_OF_LIMIT;
                }
                this.wE.put(string4, ++n2);
                PromoCodeLimitDAO.INSTANCE.insert(string4, n2);
            }
        }
        long l = System.currentTimeMillis();
        if (promoCode.getFromDate() > 0L && l < promoCode.getFromDate()) {
            return ActivateResult.OUT_OF_DATE;
        }
        if (promoCode.getToDate() > 0L && l > promoCode.getToDate()) {
            return ActivateResult.OUT_OF_DATE;
        }
        if (promoCode.isLimitByUser() && !PromoCodeUserDAO.INSTANCE.isContains(string, string4)) {
            return ActivateResult.NOT_FOR_YOU;
        }
        for (PromoCodeReward promoCodeReward : promoCode.getRewards()) {
            if (promoCodeReward.validate()) continue;
            return ActivateResult.INVALID_TRY_LATER;
        }
        for (PromoCodeReward promoCodeReward : promoCode.getRewards()) {
            promoCodeReward.giveReward(player);
        }
        PromoCodeDAO.INSTANCE.insert(string, string4);
        if (promoCode.isLimitByIP()) {
            PromoCodeDAO.INSTANCE.insertIpLimit(string3, string4);
        }
        if (promoCode.isLimitByHWID()) {
            PromoCodeDAO.INSTANCE.insertHwidLimit(string2, string4);
        }
        return ActivateResult.OK;
    }

    @Override
    public int size() {
        return this.wD.size();
    }

    @Override
    public void clear() {
        this.wD.clear();
    }

    public static enum ActivateResult {
        ALREADY,
        OUT_OF_DATE,
        OUT_OF_LIMIT,
        NOT_FOR_YOU,
        HWID_LIMITED,
        IP_LIMITED,
        MIN_LEVEL_LIMITED,
        INVALID_TRY_LATER,
        OK;

    }
}

