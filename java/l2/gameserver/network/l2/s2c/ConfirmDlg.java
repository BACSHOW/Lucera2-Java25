/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SysMsgContainer;

public class ConfirmDlg
extends SysMsgContainer<ConfirmDlg> {
    private int _time;
    private int aaJ;

    public ConfirmDlg(SystemMsg systemMsg, int n) {
        super(systemMsg);
        this._time = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(237);
        this.writeElements();
        this.writeD(this._time);
        this.writeD(this.aaJ);
    }

    public void setRequestId(int n) {
        this.aaJ = n;
    }
}

