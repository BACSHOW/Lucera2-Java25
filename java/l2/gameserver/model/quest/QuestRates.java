/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.quest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestRates {
    private static final Logger LOG = LoggerFactory.getLogger(QuestRates.class);
    private final int _questId;
    private double YZ;
    private double Za;
    private double Zb;
    private double Zc;
    private double Zd;

    public QuestRates(int n) {
        this._questId = n;
        this.YZ = 1.0;
        this.Za = 1.0;
        this.Zb = 1.0;
        this.Zc = 1.0;
        this.Zd = 1.0;
    }

    public void updateParam(String string, String string2) {
        if (string.equalsIgnoreCase("Drop") || string.equalsIgnoreCase("DropRate")) {
            this.setDropRate(Double.parseDouble(string2));
        } else if (string.equalsIgnoreCase("Reward") || string.equalsIgnoreCase("RewardRate")) {
            this.setRewardRate(Double.parseDouble(string2));
        } else if (string.equalsIgnoreCase("RewardAdena") || string.equalsIgnoreCase("RewardAdenaRate")) {
            this.setRewardAdenaRate(Double.parseDouble(string2));
        } else if (string.equalsIgnoreCase("Exp") || string.equalsIgnoreCase("ExpRate")) {
            this.setExpRate(Double.parseDouble(string2));
        } else if (string.equalsIgnoreCase("Sp") || string.equalsIgnoreCase("SpRate")) {
            this.setExpRate(Double.parseDouble(string2));
        } else {
            throw new IllegalArgumentException("Unknown param \"" + string + "\"");
        }
    }

    public double getDropRate() {
        return this.YZ;
    }

    public void setDropRate(double d) {
        this.YZ = d;
    }

    public void setRewardAdenaRate(double d) {
        this.Zb = d;
    }

    public double getRewardAdenaRate() {
        return this.Zb;
    }

    public double getRewardRate() {
        return this.Za;
    }

    public void setRewardRate(double d) {
        this.Za = d;
    }

    public double getExpRate() {
        return this.Zc;
    }

    public void setExpRate(double d) {
        this.Zc = d;
    }

    public double getSpRate() {
        return this.Zd;
    }

    public void setSpRate(double d) {
        this.Zd = d;
    }
}

