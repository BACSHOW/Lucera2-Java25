/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectTasks;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.MyTargetSelected;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.taskmanager.EffectTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public final class TrapInstance
extends NpcInstance {
    private final HardReference<? extends Creature> Wq;
    private final Skill _skill;
    private ScheduledFuture<?> We;
    private ScheduledFuture<?> Wf;
    private boolean Wr;

    public TrapInstance(int n, NpcTemplate npcTemplate, Creature creature, Skill skill) {
        this(n, npcTemplate, creature, skill, creature.getLoc());
    }

    public TrapInstance(int n, NpcTemplate npcTemplate, Creature creature, Skill skill, Location location) {
        super(n, npcTemplate);
        this.Wq = creature.getRef();
        this._skill = skill;
        this.setReflection(creature.getReflection());
        this.setLevel(creature.getLevel());
        this.setTitle(creature.getName());
        this.setLoc(location);
    }

    @Override
    public boolean isTrap() {
        return true;
    }

    public Creature getOwner() {
        return this.Wq.get();
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        this.Wf = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(this), 120000L);
        this.We = EffectTaskManager.getInstance().scheduleAtFixedRate(new CastTask(this), 250L, 250L);
    }

    @Override
    public void broadcastCharInfo() {
        if (!this.isDetected()) {
            return;
        }
        super.broadcastCharInfo();
    }

    @Override
    protected void onDelete() {
        Creature creature = this.getOwner();
        if (creature != null && creature.isPlayer()) {
            ((Player)creature).removeTrap(this);
        }
        if (this.Wf != null) {
            this.Wf.cancel(false);
        }
        this.Wf = null;
        if (this.We != null) {
            this.We.cancel(false);
        }
        this.We = null;
        super.onDelete();
    }

    public boolean isDetected() {
        return this.Wr;
    }

    public void setDetected(boolean bl) {
        this.Wr = bl;
    }

    @Override
    public int getPAtk(Creature creature) {
        Creature creature2 = this.getOwner();
        return creature2 == null ? 0 : creature2.getPAtk(creature);
    }

    @Override
    public int getMAtk(Creature creature, Skill skill) {
        Creature creature2 = this.getOwner();
        return creature2 == null ? 0 : creature2.getMAtk(creature, skill);
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return false;
    }

    @Override
    public boolean isAttackable(Creature creature) {
        return false;
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
    }

    @Override
    public void showChatWindow(Player player, String string, Object ... objectArray) {
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
    }

    @Override
    public void onAction(Player player, boolean bl) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this) {
                player.sendPacket((IStaticPacket)new MyTargetSelected(this.getObjectId(), player.getLevel()));
            }
        }
        player.sendActionFailed();
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player player, Creature creature) {
        if (!this.isDetected() && this.getOwner() != player) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new NpcInfo(this, (Creature)player));
    }

    private static class CastTask
    extends RunnableImpl {
        private HardReference<NpcInstance> Ws;

        public CastTask(TrapInstance trapInstance) {
            this.Ws = trapInstance.getRef();
        }

        @Override
        public void runImpl() throws Exception {
            TrapInstance trapInstance = (TrapInstance)this.Ws.get();
            if (trapInstance == null) {
                return;
            }
            Creature creature = trapInstance.getOwner();
            if (creature == null) {
                return;
            }
            for (Creature creature2 : trapInstance.getAroundCharacters(200, 200)) {
                if (creature2 == creature || trapInstance._skill.checkTarget(creature, creature2, null, false, false) != null) continue;
                ArrayList<Creature> arrayList = new ArrayList<Creature>();
                if (trapInstance._skill.getTargetType() != Skill.SkillTargetType.TARGET_AREA) {
                    arrayList.add(creature2);
                } else {
                    for (Creature creature3 : trapInstance.getAroundCharacters(trapInstance._skill.getSkillRadius(), 128)) {
                        if (trapInstance._skill.checkTarget(creature, creature3, null, false, false) != null) continue;
                        arrayList.add(creature2);
                    }
                }
                trapInstance._skill.useSkill(trapInstance, arrayList);
                if (creature2.isPlayer()) {
                    creature2.sendMessage(new CustomMessage("common.Trap", creature2.getPlayer(), new Object[0]));
                }
                trapInstance.deleteMe();
                break;
            }
        }
    }
}

