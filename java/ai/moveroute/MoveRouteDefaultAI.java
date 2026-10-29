/*
 * Decompiled with CFR 0.152.
 */
package ai.moveroute;

import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.MinionList;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.moveroute.MoveNode;
import l2.gameserver.templates.moveroute.MoveRoute;
import l2.gameserver.utils.Location;
import parsers.MoveRouteHolder;

public class MoveRouteDefaultAI
extends DefaultAI {
    private MoveRoute eh;
    private MoveNode ei;
    private boolean ej;
    private boolean ek = false;

    public MoveRouteDefaultAI(NpcInstance npcInstance) {
        super(npcInstance);
        String string = npcInstance.getParameter("moveroute", null);
        this.eh = string == null ? null : MoveRouteHolder.getInstance().getRoute(string);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        if (this.eh == null) {
            return;
        }
        this.ej = false;
        if (this.eh.isRunning()) {
            this.getActor().setRunning();
        } else {
            this.getActor().setWalking();
        }
    }

    @Override
    protected Location getPursueBaseLoc() {
        return this.ei != null ? this.ei : super.getPursueBaseLoc();
    }

    @Override
    protected boolean thinkActive() {
        MonsterInstance monsterInstance;
        if (this.eh == null) {
            return super.thinkActive();
        }
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isActionsDisabled()) {
            return true;
        }
        if (this._randomAnimationEnd > System.currentTimeMillis()) {
            return true;
        }
        if (this._def_think) {
            if (this.doTask()) {
                this.clearTasks();
            }
            return true;
        }
        long l = System.currentTimeMillis();
        if (l - this._checkAggroTimestamp > (long)Config.AGGRO_CHECK_INTERVAL) {
            this._checkAggroTimestamp = l;
            boolean bl = Rnd.chance(npcInstance.getParameter("SelfAggressive", npcInstance.isAggressive() ? 100 : 0));
            if (!npcInstance.getAggroList().isEmpty() || bl) {
                List<Creature> list = World.getAroundCharacters(npcInstance);
                CollectionUtils.eqSort(list, this._nearestTargetComparator);
                for (Creature creature : list) {
                    if (!bl && npcInstance.getAggroList().get(creature) == null || !this.checkAggression(creature)) continue;
                    npcInstance.getAggroList().addDamageHate(creature, 0, 2);
                    if (creature.isSummon()) {
                        npcInstance.getAggroList().addDamageHate(creature.getPlayer(), 0, 1);
                    }
                    this.startRunningTask(this.AI_TASK_ATTACK_DELAY);
                    this.setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
                    return true;
                }
            }
        }
        if (npcInstance.isMinion() && (monsterInstance = ((MinionInstance)npcInstance).getLeader()) != null) {
            double d = npcInstance.getDistance(monsterInstance.getX(), monsterInstance.getY());
            if (d > 1000.0) {
                npcInstance.teleToLocation(monsterInstance.getMinionPosition());
            } else if (d > 200.0) {
                this.addTaskMove(monsterInstance.getMinionPosition(), false);
            }
            return true;
        }
        if (this.randomAnimation()) {
            return true;
        }
        if (this.randomWalk()) {
            return true;
        }
        if (!npcInstance.isMoving() && !npcInstance.isFollowing()) {
            Location location = npcInstance.getLoc();
            int n = this.s();
            MoveNode moveNode = this.eh.getNodes().get(n);
            double d = moveNode.distance3D(location);
            if (d > Math.max(64.0, 4.0 * npcInstance.getCollisionRadius())) {
                this.ei = moveNode;
                this.returnHome(true, !GeoEngine.canMoveToCoord(location.getX(), location.getY(), location.getZ(), moveNode.getX(), moveNode.getY(), moveNode.getZ(), npcInstance.getGeoIndex()));
            } else {
                MoveNode moveNode2;
                Object object = moveNode;
                int n2 = n;
                if (((MoveNode)object).getSocialId() > 0) {
                    npcInstance.broadcastPacketToOthers(new SocialAction(npcInstance.getObjectId(), ((MoveNode)object).getSocialId()));
                }
                if (!this.ek) {
                    if (((MoveNode)object).getNpcMsgAddress() != null) {
                        Functions.npcSayCustomMessage(npcInstance, ((MoveNode)object).getChatType(), ((MoveNode)object).getNpcMsgAddress(), new Object[0]);
                    }
                    if (((MoveNode)object).getDelay() > 0L) {
                        this.ek = true;
                        this.setIsInRandomAnimation(((MoveNode)object).getDelay());
                        return true;
                    }
                }
                this.ek = false;
                if (this.eh.isRunning()) {
                    npcInstance.setRunning();
                } else {
                    npcInstance.setWalking();
                }
                int n3 = this.d(n2);
                if (n3 < 0) {
                    npcInstance.decayOrDelete();
                    return false;
                }
                this.ei = moveNode2 = this.eh.getNodes().get(n3);
                this.addTaskMove(moveNode2, true);
                if (npcInstance.hasMinions()) {
                    this.r();
                }
                return this.doTask();
            }
        }
        return false;
    }

    private int d(int n) {
        switch (this.eh.getType()) {
            case LOOP: {
                return n + 1 < this.eh.getNodes().size() ? n + 1 : 0;
            }
            case CIRCLE: {
                if (!this.ej) {
                    if (n + 1 < this.eh.getNodes().size()) {
                        return n + 1;
                    }
                    this.ej = true;
                    return n - 1;
                }
                if (n - 1 > 0) {
                    return n - 1;
                }
                this.ej = false;
                return n + 1;
            }
            case ONCE: {
                return n + 1 < this.eh.getNodes().size() ? n + 1 : -1;
            }
        }
        return -1;
    }

    private void r() {
        NpcInstance npcInstance = this.getActor();
        MinionList minionList = npcInstance.getMinionList();
        if (minionList.hasAliveMinions()) {
            for (NpcInstance npcInstance2 : minionList.getAliveMinions()) {
                if (npcInstance2.isInCombat() || npcInstance2.isAfraid()) continue;
                if (this.eh.isRunning()) {
                    npcInstance2.setRunning();
                } else {
                    npcInstance2.setWalking();
                }
                npcInstance2.moveToRelative(this.getActor(), 400, 500, true);
            }
        }
    }

    private int s() {
        if (this.eh == null) {
            return -1;
        }
        NpcInstance npcInstance = this.getActor();
        List<MoveNode> list = this.eh.getNodes();
        int n = -1;
        long l = Long.MAX_VALUE;
        for (int i = 0; i < list.size(); ++i) {
            MoveNode moveNode = list.get(i);
            long l2 = npcInstance.getXYZDeltaSq(moveNode);
            if (l2 >= l) continue;
            l = l2;
            n = i;
        }
        return n;
    }

    @Override
    public boolean isGlobalAI() {
        return false;
    }
}

