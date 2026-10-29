/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class CTBTeamObject
implements SpawnableObject {
    private CTBSiegeClanObject Ra;
    private final NpcTemplate Rb;
    private final NpcTemplate Rc;
    private final Location Rd;
    private NpcInstance Re;
    private CTBBossInstance Rf;

    public CTBTeamObject(int n, int n2, Location location) {
        this.Rb = NpcHolder.getInstance().getTemplate(n);
        this.Rc = NpcHolder.getInstance().getTemplate(n2);
        this.Rd = location;
    }

    @Override
    public void spawnObject(GlobalEvent globalEvent) {
        if (this.Re == null) {
            this.Re = new NpcInstance(IdFactory.getInstance().getNextId(), this.Rc);
            this.Re.setCurrentHpMp(this.Re.getMaxHp(), this.Re.getMaxMp());
            this.Re.setHasChatWindow(false);
            this.Re.spawnMe(this.Rd);
        } else if (this.Rf == null) {
            NpcTemplate npcTemplate = this.Ra == null || this.Ra.getParam() == 0L ? this.Rb : NpcHolder.getInstance().getTemplate((int)this.Ra.getParam());
            this.Rf = (CTBBossInstance)npcTemplate.getNewInstance();
            this.Rf.setCurrentHpMp(this.Rf.getMaxHp(), this.Rf.getMaxMp());
            this.Rf.setMatchTeamObject(this);
            this.Rf.addEvent(globalEvent);
            int n = (int)((double)this.Rd.x + 300.0 * Math.cos(this.Rf.headingToRadians(this.Re.getHeading() - 32768)));
            int n2 = (int)((double)this.Rd.y + 300.0 * Math.sin(this.Rf.headingToRadians(this.Re.getHeading() - 32768)));
            Location location = new Location(n, n2, this.Re.getZ(), this.Re.getHeading());
            this.Rf.setSpawnedLoc(location);
            this.Rf.spawnMe(location);
        } else {
            throw new IllegalArgumentException("Cant spawn twice");
        }
    }

    @Override
    public void despawnObject(GlobalEvent globalEvent) {
        if (this.Rf != null) {
            this.Rf.deleteMe();
            this.Rf = null;
        }
        if (this.Re != null) {
            this.Re.deleteMe();
            this.Re = null;
        }
        this.Ra = null;
    }

    @Override
    public void refreshObject(GlobalEvent globalEvent) {
    }

    public CTBSiegeClanObject getSiegeClan() {
        return this.Ra;
    }

    public void setSiegeClan(CTBSiegeClanObject cTBSiegeClanObject) {
        this.Ra = cTBSiegeClanObject;
    }

    public boolean isParticle() {
        return this.Re != null && this.Rf != null;
    }

    public NpcInstance getFlag() {
        return this.Re;
    }
}

