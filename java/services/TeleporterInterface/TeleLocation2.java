package services.TeleporterInterface;

import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Location;

/** Um destino do teleporte da interface (data/teleportLocationsinterface.xml). */
public class TeleLocation2 extends Location {
    private final String _id;
    private final String _name;
    private final String _type;
    private final int _category;
    private final int _price;
    private final boolean _isNoble;
    private final int _skillEffectId;

    public TeleLocation2(StatsSet set) {
        super(set.getInteger("x"), set.getInteger("y"), set.getInteger("z"));
        this._id = set.getString("id");
        this._name = set.getString("name", "Teleport " + this._id);
        this._type = set.getString("type", "main");
        this._category = set.getInteger("cat", 6);
        this._price = set.getInteger("price", 0);
        this._isNoble = set.getBool("isNoble", false);
        this._skillEffectId = set.getInteger("SkillEffectId", 2036);
    }

    public String getId() {
        return this._id;
    }

    public String getName() {
        return this._name;
    }

    /** "main" (aba Principais) ou "hunt" (aba Zonas de caca). */
    public String getType() {
        return this._type;
    }

    public int getCategory() {
        return this._category;
    }

    public int getPrice() {
        return this._price;
    }

    public boolean isNoble() {
        return this._isNoble;
    }

    public int getSkillEffectId() {
        return this._skillEffectId;
    }
}
