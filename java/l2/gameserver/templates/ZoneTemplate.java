/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import java.util.List;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Territory;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.Race;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Location;

public class ZoneTemplate {
    private final String _name;
    private final Zone.ZoneType Me;
    private final Territory Au;
    private final boolean aN;
    private final boolean _isDefault;
    private final List<Location> atL;
    private final List<Location> atM;
    private final long atN;
    private final int atO;
    private final int atP;
    private final Race atQ;
    private final Zone.ZoneTarget atR;
    private final Skill _skill;
    private final int atS;
    private final int atT;
    private final int atU;
    private final int atV;
    private final int atW;
    private final int atX;
    private final int atY;
    private final double atZ;
    private final double aua;
    private final double aub;
    private final int agd;
    private final String[] auc;
    private final int _index;
    private final int aud;
    private final StatsSet aue;

    public ZoneTemplate(StatsSet statsSet) {
        this._name = statsSet.getString("name");
        this.Me = Zone.ZoneType.valueOf(statsSet.getString("type"));
        this.Au = (Territory)statsSet.get("territory");
        this.atO = statsSet.getInteger("entering_message_no", 0);
        this.atP = statsSet.getInteger("leaving_message_no", 0);
        this.atR = Zone.ZoneTarget.valueOf(statsSet.getString("target", "pc"));
        this.atQ = statsSet.getString("affect_race", "all").equals("all") ? null : Race.valueOf(statsSet.getString("affect_race"));
        String string = statsSet.getString("skill_name", null);
        Skill skill = null;
        if (string != null) {
            String[] stringArray = string.split("[\\s,;]+");
            skill = SkillTable.getInstance().getInfo(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]));
        }
        this._skill = skill;
        this.atS = statsSet.getInteger("skill_prob", 100);
        this.atT = statsSet.getInteger("initial_delay", 1);
        this.atU = statsSet.getInteger("unit_tick", 1);
        this.atV = statsSet.getInteger("random_time", 0);
        this.atZ = statsSet.getDouble("move_bonus", 0.0);
        this.aua = statsSet.getDouble("hp_regen_bonus", 0.0);
        this.aub = statsSet.getDouble("mp_regen_bonus", 0.0);
        this.atX = statsSet.getInteger("damage_on_hp", 0);
        this.atY = statsSet.getInteger("damage_on_mp", 0);
        this.atW = statsSet.getInteger("message_no", 0);
        this.agd = statsSet.getInteger("eventId", 0);
        this.aN = statsSet.getBool("enabled", true);
        this._isDefault = statsSet.getBool("default", true);
        this.atL = (List)statsSet.get("restart_points");
        this.atM = (List)statsSet.get("PKrestart_points");
        this.atN = statsSet.getLong("restart_time", 0L);
        string = (String)statsSet.get("blocked_actions");
        this.auc = string != null ? string.split("[\\s,;]+") : null;
        this._index = statsSet.getInteger("index", 0);
        this.aud = statsSet.getInteger("taxById", 0);
        this.aue = statsSet;
    }

    public boolean isEnabled() {
        return this.aN;
    }

    public boolean isDefault() {
        return this._isDefault;
    }

    public String getName() {
        return this._name;
    }

    public Zone.ZoneType getType() {
        return this.Me;
    }

    public Territory getTerritory() {
        return this.Au;
    }

    public int getEnteringMessageId() {
        return this.atO;
    }

    public int getLeavingMessageId() {
        return this.atP;
    }

    public Skill getZoneSkill() {
        return this._skill;
    }

    public int getSkillProb() {
        return this.atS;
    }

    public int getInitialDelay() {
        return this.atT;
    }

    public int getUnitTick() {
        return this.atU;
    }

    public int getRandomTick() {
        return this.atV;
    }

    public Zone.ZoneTarget getZoneTarget() {
        return this.atR;
    }

    public Race getAffectRace() {
        return this.atQ;
    }

    public String[] getBlockedActions() {
        return this.auc;
    }

    public int getDamageMessageId() {
        return this.atW;
    }

    public int getDamageOnHP() {
        return this.atX;
    }

    public int getDamageOnMP() {
        return this.atY;
    }

    public double getMoveBonus() {
        return this.atZ;
    }

    public double getRegenBonusHP() {
        return this.aua;
    }

    public double getRegenBonusMP() {
        return this.aub;
    }

    public long getRestartTime() {
        return this.atN;
    }

    public List<Location> getRestartPoints() {
        return this.atL;
    }

    public List<Location> getPKRestartPoints() {
        return this.atM;
    }

    public int getIndex() {
        return this._index;
    }

    public int getTaxById() {
        return this.aud;
    }

    public int getEventId() {
        return this.agd;
    }

    public MultiValueSet<String> getParams() {
        return this.aue.clone();
    }
}

