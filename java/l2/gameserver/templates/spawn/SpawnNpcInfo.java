/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.spawn;

import l2.commons.collections.MultiValueSet;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class SpawnNpcInfo {
    private final int _npcId;
    private final NpcTemplate awi;
    private Location awj;
    private final int avx;
    private final MultiValueSet<String> UH;

    public SpawnNpcInfo(int n, int n2, MultiValueSet<String> multiValueSet) {
        this._npcId = n;
        this.awi = NpcHolder.getInstance().getTemplate(n);
        this.avx = n2;
        this.UH = multiValueSet;
    }

    public int getNpcId() {
        return this._npcId;
    }

    public Location getSpawnLoc() {
        return this.awj;
    }

    public NpcTemplate getTemplate() {
        return this.awi;
    }

    public int getMax() {
        return this.avx;
    }

    public MultiValueSet<String> getParameters() {
        return this.UH;
    }
}

