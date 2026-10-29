/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectTasks;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.taskmanager.EffectTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;

public class SymbolInstance
extends NpcInstance {
    private final Creature Wd;
    private final Skill _skill;
    private ScheduledFuture<?> We;
    private ScheduledFuture<?> Wf;

    public SymbolInstance(int n, NpcTemplate npcTemplate, Creature creature, Skill skill) {
        super(n, npcTemplate);
        this.Wd = creature;
        this._skill = skill;
        this.setReflection(creature.getReflection());
        this.setLevel(creature.getLevel());
        this.setTitle(creature.getName());
    }

    public Creature getOwner() {
        return this.Wd;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        this.Wf = ThreadPoolManager.getInstance().schedule(new GameObjectTasks.DeleteTask(this), 120000L);
        this.We = EffectTaskManager.getInstance().scheduleAtFixedRate(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                for (Creature creature : SymbolInstance.this.getAroundCharacters(200, 200)) {
                    if (SymbolInstance.this._skill.checkTarget(SymbolInstance.this.Wd, creature, null, false, false) != null) continue;
                    ArrayList<Creature> arrayList = new ArrayList<Creature>();
                    if (!SymbolInstance.this._skill.isAoE()) {
                        arrayList.add(creature);
                    } else {
                        for (Creature creature2 : SymbolInstance.this.getAroundCharacters(SymbolInstance.this._skill.getSkillRadius(), 128)) {
                            if (SymbolInstance.this._skill.checkTarget(SymbolInstance.this.Wd, creature2, null, false, false) != null) continue;
                            arrayList.add(creature);
                        }
                    }
                    SymbolInstance.this._skill.useSkill(SymbolInstance.this, arrayList);
                }
            }
        }, 1000L, Rnd.get(4000L, 7000L));
    }

    @Override
    protected void onDelete() {
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
        player.sendActionFailed();
    }

    @Override
    public Clan getClan() {
        return null;
    }
}

