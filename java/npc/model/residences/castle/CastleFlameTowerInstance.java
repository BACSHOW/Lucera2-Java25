/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import java.util.List;
import java.util.Set;
import l2.gameserver.model.Creature;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import l2.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class CastleFlameTowerInstance
extends SiegeToggleNpcInstance {
    private Set<String> ayn;

    public CastleFlameTowerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onDeathImpl(Creature creature) {
        CastleSiegeEvent castleSiegeEvent = this.getEvent(CastleSiegeEvent.class);
        if (castleSiegeEvent == null || !castleSiegeEvent.isInProgress()) {
            return;
        }
        for (String string : this.ayn) {
            List list = castleSiegeEvent.getObjects(string);
            for (Object __raw_castleDamageZoneObject_30 : (Iterable<?>)(Object)(list)) {
               CastleDamageZoneObject castleDamageZoneObject = (CastleDamageZoneObject)__raw_castleDamageZoneObject_30;
                castleDamageZoneObject.getZone().setActive(false);
            }
        }
    }

    @Override
    public void setZoneList(Set<String> set) {
        this.ayn = set;
    }
}

