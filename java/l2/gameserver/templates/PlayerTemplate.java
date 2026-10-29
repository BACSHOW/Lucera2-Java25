/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Race;
import l2.gameserver.templates.CharTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Triple;

public class PlayerTemplate
extends CharTemplate {
    public final ClassId classId;
    public final Race race;
    public final String className;
    public final Location spawnLoc = new Location();
    public final boolean isMale;
    private List<ItemTemplate> _items = new ArrayList<ItemTemplate>();
    private List<Triple<ItemTemplate, Long, Integer>> atC = new ArrayList<Triple<ItemTemplate, Long, Integer>>();
    private List<Triple<ItemTemplate, Long, Integer>> atD = new ArrayList<Triple<ItemTemplate, Long, Integer>>();

    public PlayerTemplate(StatsSet statsSet) {
        super(statsSet);
        this.classId = ClassId.VALUES[statsSet.getInteger("classId")];
        this.race = Race.values()[statsSet.getInteger("raceId")];
        this.className = statsSet.getString("className");
        this.spawnLoc.set(new Location(statsSet.getInteger("spawnX"), statsSet.getInteger("spawnY"), statsSet.getInteger("spawnZ")));
        this.isMale = statsSet.getBool("isMale", true);
    }

    public PlayerTemplate(ClassId classId, StatsSet statsSet, String string, Location location, boolean bl) {
        super(statsSet);
        this.classId = classId;
        this.race = classId.getRace();
        this.className = string;
        this.spawnLoc.set(location);
        this.isMale = bl;
    }

    public void addItem(int n) {
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
        if (itemTemplate != null) {
            this._items.add(itemTemplate);
        }
    }

    public ItemTemplate[] getItems() {
        return this._items.toArray(new ItemTemplate[this._items.size()]);
    }

    public List<Triple<ItemTemplate, Long, Integer>> getInitialItems() {
        return this.atC;
    }

    public List<Triple<ItemTemplate, Long, Integer>> getInitialEquipItems() {
        return this.atD;
    }
}

