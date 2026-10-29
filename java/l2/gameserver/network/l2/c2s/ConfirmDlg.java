/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import org.apache.commons.lang3.tuple.Pair;

public class ConfirmDlg
extends L2GameClientPacket {
    private int _answer;
    private int aaJ;

    @Override
    protected void readImpl() {
        this.readD();
        this._answer = this.readD();
        this.aaJ = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Pair<Integer, OnAnswerListener> pair = player.getAskListener(true);
        if (pair == null || pair.getKey() != this.aaJ) {
            return;
        }
        OnAnswerListener onAnswerListener = pair.getValue();
        if (this._answer == 1) {
            onAnswerListener.sayYes();
        } else {
            onAnswerListener.sayNo();
        }
    }
}

