/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.quest;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.QuestState;

public class QuestTimer
extends RunnableImpl {
    private String _name;
    private NpcInstance _npc;
    private long Pv;
    private QuestState Zj;
    private ScheduledFuture<?> Zk;

    public QuestTimer(String string, long l, NpcInstance npcInstance) {
        this._name = string;
        this.Pv = l;
        this._npc = npcInstance;
    }

    void setQuestState(QuestState questState) {
        this.Zj = questState;
    }

    QuestState getQuestState() {
        return this.Zj;
    }

    void start() {
        this.Zk = ThreadPoolManager.getInstance().schedule(this, this.Pv);
    }

    @Override
    public void runImpl() throws Exception {
        QuestState questState = this.getQuestState();
        if (questState != null) {
            questState.removeQuestTimer(this.getName());
            questState.getQuest().notifyEvent(this.getName(), questState, this.getNpc());
        }
    }

    void pause() {
        if (this.Zk != null) {
            this.Pv = this.Zk.getDelay(TimeUnit.SECONDS);
            this.Zk.cancel(false);
        }
    }

    void stop() {
        if (this.Zk != null) {
            this.Zk.cancel(false);
        }
    }

    public boolean isActive() {
        return this.Zk != null && !this.Zk.isDone();
    }

    public String getName() {
        return this._name;
    }

    public long getTime() {
        return this.Pv;
    }

    public NpcInstance getNpc() {
        return this._npc;
    }

    public final String toString() {
        return this._name;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        return ((QuestTimer)object).getName().equals(this.getName());
    }
}

