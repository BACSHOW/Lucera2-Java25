/*
 * Decompiled with CFR 0.152.
 */
package actions;

import actions.RewardListInfo;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.admincommands.impl.AdminEditChar;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.HardSpawner;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import l2.gameserver.scripts.Functions;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.StringUtils;

public class OnActionShift
extends Functions {
    public boolean OnActionShift_NpcInstance(Player player, GameObject gameObject) {
        if (player == null || gameObject == null) {
            return false;
        }
        if (!Config.ALLOW_NPC_SHIFTCLICK && !player.isGM()) {
            if (Config.ALT_GAME_SHOW_DROPLIST && gameObject.isNpc()) {
                NpcInstance npcInstance = (NpcInstance)gameObject;
                if (npcInstance.isDead()) {
                    return false;
                }
                this.droplist(player, npcInstance);
            }
            return false;
        }
        if (gameObject.isNpc()) {
            String string;
            NpcInstance npcInstance = (NpcInstance)gameObject;
            if (npcInstance.isDead()) {
                return false;
            }
            if (Config.ALT_FULL_NPC_STATS_PAGE) {
                string = HtmCache.getInstance().getNotNull("scripts/actions/player.L2NpcInstance.onActionShift.full.htm", player);
                if (npcInstance.getSpawn() != null && npcInstance.getSpawn() instanceof HardSpawner) {
                    HardSpawner hardSpawner = (HardSpawner)npcInstance.getSpawn();
                    SpawnTemplate spawnTemplate = hardSpawner.getTemplate();
                    string = string.replaceFirst("%maker%", spawnTemplate != null && spawnTemplate.getMakerName() != null ? spawnTemplate.getMakerName() : "");
                    StringBuilder stringBuilder = new StringBuilder(hardSpawner.getTemplate().getEventName() + "|");
                    for (GlobalEvent globalEvent : npcInstance.getEvents()) {
                        stringBuilder.append(globalEvent.toString()).append(";");
                    }
                    string = string.replaceFirst("%event%", stringBuilder.toString());
                } else {
                    string = string.replaceFirst("%maker%", "");
                    StringBuilder stringBuilder = new StringBuilder("");
                    for (GlobalEvent globalEvent : npcInstance.getEvents()) {
                        stringBuilder.append(globalEvent.toString()).append(";");
                    }
                    string = string.replaceFirst("%event%", stringBuilder.toString());
                }
                string = string.replaceFirst("%class%", String.valueOf(npcInstance.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
                string = string.replaceFirst("%id%", String.valueOf(npcInstance.getNpcId()));
                string = string.replaceFirst("%respawn%", String.valueOf(npcInstance.getSpawn() != null ? Util.formatTime((int)(npcInstance.getSpawn().getRespawnCron() != null ? (npcInstance.getSpawn().getRespawnCron().next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L : npcInstance.getSpawn().getRespawnDelay())) : "0"));
                string = string.replaceFirst("%walkSpeed%", String.valueOf(npcInstance.getWalkSpeed()));
                string = string.replaceFirst("%evs%", String.valueOf(npcInstance.getEvasionRate(null)));
                string = string.replaceFirst("%acc%", String.valueOf(npcInstance.getAccuracy()));
                string = string.replaceFirst("%crt%", String.valueOf(npcInstance.getCriticalHit(null, null)));
                string = string.replaceFirst("%aspd%", String.valueOf(npcInstance.getPAtkSpd()));
                string = string.replaceFirst("%cspd%", String.valueOf(npcInstance.getMAtkSpd()));
                string = string.replaceFirst("%currentMP%", String.valueOf(npcInstance.getCurrentMp()));
                string = string.replaceFirst("%currentHP%", String.valueOf(npcInstance.getCurrentHp()));
                string = string.replaceFirst("%loc%", "");
                string = string.replaceFirst("%dist%", String.valueOf((int)npcInstance.getDistance3D(player)));
                string = string.replaceFirst("%killed%", String.valueOf(0));
                string = string.replaceFirst("%spReward%", String.valueOf(npcInstance.getSpReward()));
                string = string.replaceFirst("%xyz%", npcInstance.getLoc().x + " " + npcInstance.getLoc().y + " " + npcInstance.getLoc().z);
                string = string.replaceFirst("%ai_type%", npcInstance.getAI().getClass().getSimpleName());
                string = string.replaceFirst("%direction%", PositionUtils.getDirectionTo(npcInstance, player).toString().toLowerCase());
                string = string.replaceFirst("%spawner%", "");
            } else {
                string = HtmCache.getInstance().getNotNull("scripts/actions/player.L2NpcInstance.onActionShift.htm", player);
            }
            string = string.replaceFirst("%name%", this.a(npcInstance));
            string = string.replaceFirst("%id%", String.valueOf(npcInstance.getNpcId()));
            string = string.replaceFirst("%level%", String.valueOf(npcInstance.getLevel()));
            string = string.replaceFirst("%respawn%", String.valueOf(npcInstance.getSpawn() != null ? Util.formatTime((int)(npcInstance.getSpawn().getRespawnCron() != null ? (npcInstance.getSpawn().getRespawnCron().next(System.currentTimeMillis()) - System.currentTimeMillis()) / 1000L : npcInstance.getSpawn().getRespawnDelay())) : "0"));
            string = string.replaceFirst("%factionId%", String.valueOf(npcInstance.getFaction()));
            string = string.replaceFirst("%aggro%", String.valueOf(npcInstance.getAggroRange()));
            string = string.replaceFirst("%maxHp%", String.valueOf(npcInstance.getMaxHp()));
            string = string.replaceFirst("%maxMp%", String.valueOf(npcInstance.getMaxMp()));
            string = string.replaceFirst("%pDef%", String.valueOf(npcInstance.getPDef(null)));
            string = string.replaceFirst("%mDef%", String.valueOf(npcInstance.getMDef(null, null)));
            string = string.replaceFirst("%pAtk%", String.valueOf(npcInstance.getPAtk(null)));
            string = string.replaceFirst("%mAtk%", String.valueOf(npcInstance.getMAtk(null, null)));
            string = string.replaceFirst("%expReward%", String.valueOf(npcInstance.getExpReward()));
            string = string.replaceFirst("%spReward%", String.valueOf(npcInstance.getSpReward()));
            string = string.replaceFirst("%runSpeed%", String.valueOf(npcInstance.getRunSpeed()));
            string = player.isGM() ? string.replaceFirst("%AI%", String.valueOf(npcInstance.getAI()) + ",<br1>active: " + npcInstance.getAI().isActive() + ",<br1>intention: " + (Object)((Object)npcInstance.getAI().getIntention())) : string.replaceFirst("%AI%", "");
            OnActionShift.show(string, player, npcInstance, new Object[0]);
        }
        return true;
    }

    public String getNpcRaceById(int n) {
        switch (n) {
            case 1: {
                return "Undead";
            }
            case 2: {
                return "Magic Creatures";
            }
            case 3: {
                return "Beasts";
            }
            case 4: {
                return "Animals";
            }
            case 5: {
                return "Plants";
            }
            case 6: {
                return "Humanoids";
            }
            case 7: {
                return "Spirits";
            }
            case 8: {
                return "Angels";
            }
            case 9: {
                return "Demons";
            }
            case 10: {
                return "Dragons";
            }
            case 11: {
                return "Giants";
            }
            case 12: {
                return "Bugs";
            }
            case 13: {
                return "Fairies";
            }
            case 14: {
                return "Humans";
            }
            case 15: {
                return "Elves";
            }
            case 16: {
                return "Dark Elves";
            }
            case 17: {
                return "Orcs";
            }
            case 18: {
                return "Dwarves";
            }
            case 19: {
                return "Others";
            }
            case 20: {
                return "Non-living Beings";
            }
            case 21: {
                return "Siege Weapons";
            }
            case 22: {
                return "Defending Army";
            }
            case 23: {
                return "Mercenaries";
            }
            case 24: {
                return "Unknown Creature";
            }
            case 25: {
                return "Kamael";
            }
        }
        return "Not defined";
    }

    public void droplist() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        this.droplist(player, npcInstance);
    }

    public void droplist(Player player, NpcInstance npcInstance) {
        if (player == null || npcInstance == null) {
            return;
        }
        if (Config.ALT_GAME_SHOW_DROPLIST) {
            RewardListInfo.showRewardHtml(player, npcInstance);
        }
    }

    public void quests() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
        stringBuilder.append(this.a(npcInstance)).append("<br></font></center><br>");
        Map<QuestEventType, Quest[]> map = npcInstance.getTemplate().getQuestEvents();
        for (Map.Entry<QuestEventType, Quest[]> entry : map.entrySet()) {
            for (Quest quest : entry.getValue()) {
                stringBuilder.append((Object)entry.getKey()).append(" ").append(quest.getClass().getSimpleName()).append("<br1>");
            }
        }
        stringBuilder.append("</body></html>");
        OnActionShift.show(stringBuilder.toString(), player, npcInstance, new Object[0]);
    }

    public void skills() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
        stringBuilder.append(this.a(npcInstance)).append("<br></font></center>");
        Collection<Skill> collection = npcInstance.getAllSkills();
        if (collection != null && !collection.isEmpty()) {
            stringBuilder.append("<br>Active:<br>");
            for (Skill skill : collection) {
                if (!skill.isActive()) continue;
                stringBuilder.append(skill.getName()).append("<br1>");
            }
            stringBuilder.append("<br>Passive:<br>");
            for (Skill skill : collection) {
                if (skill.isActive()) continue;
                stringBuilder.append(skill.getName()).append("<br1>");
            }
        }
        stringBuilder.append("</body></html>");
        OnActionShift.show(stringBuilder.toString(), player, npcInstance, new Object[0]);
    }

    public void effects() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
        stringBuilder.append(this.a(npcInstance)).append("<br></font></center><br>");
        List<Effect> list = npcInstance.getEffectList().getAllEffects();
        if (list != null && !list.isEmpty()) {
            for (Effect effect : list) {
                stringBuilder.append(effect.getSkill().getName()).append("<br1>");
            }
        }
        stringBuilder.append("<br><center><button value=\"");
        stringBuilder.append(player.isLangRus() ? "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c" : "Refresh");
        stringBuilder.append("\" action=\"bypass -h scripts_actions.OnActionShift:effects\" width=100 height=15 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" /></center></body></html>");
        OnActionShift.show(stringBuilder.toString(), player, npcInstance, new Object[0]);
    }

    public void stats() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        String string = HtmCache.getInstance().getNotNull("scripts/actions/player.L2NpcInstance.stats.htm", player);
        string = string.replaceFirst("%name%", this.a(npcInstance));
        string = string.replaceFirst("%level%", String.valueOf(npcInstance.getLevel()));
        string = string.replaceFirst("%factionId%", String.valueOf(npcInstance.getFaction()));
        string = string.replaceFirst("%aggro%", String.valueOf(npcInstance.getAggroRange()));
        string = string.replaceFirst("%race%", this.getNpcRaceById(npcInstance.getTemplate().getRace()));
        string = string.replaceFirst("%maxHp%", String.valueOf(npcInstance.getMaxHp()));
        string = string.replaceFirst("%maxMp%", String.valueOf(npcInstance.getMaxMp()));
        string = string.replaceFirst("%pDef%", String.valueOf(npcInstance.getPDef(null)));
        string = string.replaceFirst("%mDef%", String.valueOf(npcInstance.getMDef(null, null)));
        string = string.replaceFirst("%pAtk%", String.valueOf(npcInstance.getPAtk(null)));
        string = string.replaceFirst("%mAtk%", String.valueOf(npcInstance.getMAtk(null, null)));
        string = string.replaceFirst("%accuracy%", String.valueOf(npcInstance.getAccuracy()));
        string = string.replaceFirst("%evasionRate%", String.valueOf(npcInstance.getEvasionRate(null)));
        string = string.replaceFirst("%criticalHit%", String.valueOf(npcInstance.getCriticalHit(null, null)));
        string = string.replaceFirst("%runSpeed%", String.valueOf(npcInstance.getRunSpeed()));
        string = string.replaceFirst("%walkSpeed%", String.valueOf(npcInstance.getWalkSpeed()));
        string = string.replaceFirst("%pAtkSpd%", String.valueOf(npcInstance.getPAtkSpd()));
        string = string.replaceFirst("%mAtkSpd%", String.valueOf(npcInstance.getMAtkSpd()));
        OnActionShift.show(string, player, npcInstance, new Object[0]);
    }

    public void resists() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("<html><body><center><font color=\"LEVEL\">");
        stringBuilder.append(this.a(npcInstance)).append("<br></font></center><table width=\"80%\">");
        boolean bl = this.a(stringBuilder, "Fire", npcInstance.calcStat(Stats.DEFENCE_FIRE, 0.0, null, null));
        bl |= this.a(stringBuilder, "Wind", npcInstance.calcStat(Stats.DEFENCE_WIND, 0.0, null, null));
        bl |= this.a(stringBuilder, "Water", npcInstance.calcStat(Stats.DEFENCE_WATER, 0.0, null, null));
        bl |= this.a(stringBuilder, "Earth", npcInstance.calcStat(Stats.DEFENCE_EARTH, 0.0, null, null));
        bl |= this.a(stringBuilder, "Light", npcInstance.calcStat(Stats.DEFENCE_HOLY, 0.0, null, null));
        bl |= this.a(stringBuilder, "Darkness", npcInstance.calcStat(Stats.DEFENCE_UNHOLY, 0.0, null, null));
        bl |= this.a(stringBuilder, "Bleed", npcInstance.calcStat(Stats.BLEED_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Poison", npcInstance.calcStat(Stats.POISON_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Stun", npcInstance.calcStat(Stats.STUN_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Root", npcInstance.calcStat(Stats.ROOT_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Sleep", npcInstance.calcStat(Stats.SLEEP_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Paralyze", npcInstance.calcStat(Stats.PARALYZE_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Mental", npcInstance.calcStat(Stats.MENTAL_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Debuff", npcInstance.calcStat(Stats.DEBUFF_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Cancel", npcInstance.calcStat(Stats.CANCEL_RESIST, 0.0, null, null));
        bl |= this.a(stringBuilder, "Sword", 100.0 - npcInstance.calcStat(Stats.SWORD_WPN_VULNERABILITY, null, null));
        bl |= this.a(stringBuilder, "Dual Sword", 100.0 - npcInstance.calcStat(Stats.DUAL_WPN_VULNERABILITY, null, null));
        bl |= this.a(stringBuilder, "Blunt", 100.0 - npcInstance.calcStat(Stats.BLUNT_WPN_VULNERABILITY, null, null));
        bl |= this.a(stringBuilder, "Dagger", 100.0 - npcInstance.calcStat(Stats.DAGGER_WPN_VULNERABILITY, null, null));
        bl |= this.a(stringBuilder, "Bow", 100.0 - npcInstance.calcStat(Stats.BOW_WPN_VULNERABILITY, null, null));
        bl |= this.a(stringBuilder, "Polearm", 100.0 - npcInstance.calcStat(Stats.POLE_WPN_VULNERABILITY, null, null));
        if (!(bl |= this.a(stringBuilder, "Fist", 100.0 - npcInstance.calcStat(Stats.FIST_WPN_VULNERABILITY, null, null)))) {
            stringBuilder.append("</table>No resists</body></html>");
        } else {
            stringBuilder.append("</table></body></html>");
        }
        OnActionShift.show(stringBuilder.toString(), player, npcInstance, new Object[0]);
    }

    private boolean a(StringBuilder stringBuilder, String string, double d) {
        if (d == 0.0) {
            return false;
        }
        stringBuilder.append("<tr><td>").append(string).append("</td><td>");
        if (d == Double.POSITIVE_INFINITY) {
            stringBuilder.append("MAX");
        } else if (d == Double.NEGATIVE_INFINITY) {
            stringBuilder.append("MIN");
        } else {
            stringBuilder.append(String.valueOf((int)d));
            stringBuilder.append("</td></tr>");
            return true;
        }
        stringBuilder.append("</td></tr>");
        return true;
    }

    public void aggro() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("<html><body><table width=\"80%\"><tr><td>Attacker</td><td>Damage</td><td>Hate</td></tr>");
        TreeSet treeSet = new TreeSet(AggroList.HateComparator.getInstance());
        treeSet.addAll(npcInstance.getAggroList().getCharMap().values());
        for (Object rawHateInfo : treeSet) {
            AggroList.HateInfo hateInfo = (AggroList.HateInfo)rawHateInfo;
            stringBuilder.append("<tr><td>" + hateInfo.attacker.getName() + "</td><td>" + hateInfo.damage + "</td><td>" + hateInfo.hate + "</td></tr>");
        }
        stringBuilder.append("</table><br><center><button value=\"");
        stringBuilder.append(player.isLangRus() ? "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c" : "Refresh");
        stringBuilder.append("\" action=\"bypass -h scripts_actions.OnActionShift:aggro\" width=100 height=15 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" /></center></body></html>");
        OnActionShift.show(stringBuilder.toString(), player, npcInstance, new Object[0]);
    }

    public boolean OnActionShift_DoorInstance(Player player, GameObject gameObject) {
        if (player == null || gameObject == null || !player.getPlayerAccess().Door || !gameObject.isDoor()) {
            return false;
        }
        DoorInstance doorInstance = (DoorInstance)gameObject;
        String string = HtmCache.getInstance().getNotNull("scripts/actions/admin.L2DoorInstance.onActionShift.htm", player);
        string = string.replaceFirst("%CurrentHp%", String.valueOf((int)doorInstance.getCurrentHp()));
        string = string.replaceFirst("%MaxHp%", String.valueOf(doorInstance.getMaxHp()));
        string = string.replaceAll("%ObjectId%", String.valueOf(doorInstance.getObjectId()));
        string = string.replaceFirst("%doorId%", String.valueOf(doorInstance.getDoorId()));
        string = string.replaceFirst("%pdef%", String.valueOf(doorInstance.getPDef(null)));
        string = string.replaceFirst("%mdef%", String.valueOf(doorInstance.getMDef(null, null)));
        string = string.replaceFirst("%type%", doorInstance.getDoorType().name());
        string = string.replaceFirst("%upgradeHP%", String.valueOf(doorInstance.getUpgradeHp()));
        StringBuilder stringBuilder = new StringBuilder("");
        for (GlobalEvent globalEvent : doorInstance.getEvents()) {
            stringBuilder.append(globalEvent.toString()).append(";");
        }
        string = string.replaceFirst("%event%", stringBuilder.toString());
        this.show(string, player);
        player.sendActionFailed();
        return true;
    }

    public boolean OnActionShift_Player(Player player, GameObject gameObject) {
        if (player == null || gameObject == null || !player.getPlayerAccess().CanViewChar) {
            return false;
        }
        if (gameObject.isPlayer()) {
            AdminEditChar.showCharacterList(player, (Player)gameObject);
        }
        return true;
    }

    public boolean OnActionShift_PetInstance(Player player, GameObject gameObject) {
        if (player == null || gameObject == null || !player.getPlayerAccess().CanViewChar) {
            return false;
        }
        if (gameObject.isPet()) {
            PetInstance petInstance = (PetInstance)gameObject;
            String string = HtmCache.getInstance().getNotNull("scripts/actions/admin.L2PetInstance.onActionShift.htm", player);
            string = string.replaceFirst("%name%", HtmlUtils.htmlNpcName(petInstance.getNpcId()));
            string = string.replaceFirst("%title%", String.valueOf(StringUtils.isEmpty(petInstance.getTitle()) ? "Empty" : petInstance.getTitle()));
            string = string.replaceFirst("%level%", String.valueOf(petInstance.getLevel()));
            string = string.replaceFirst("%class%", String.valueOf(petInstance.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
            string = string.replaceFirst("%xyz%", petInstance.getLoc().x + " " + petInstance.getLoc().y + " " + petInstance.getLoc().z);
            string = string.replaceFirst("%heading%", String.valueOf(petInstance.getLoc().h));
            string = string.replaceFirst("%owner%", String.valueOf(petInstance.getPlayer().getName()));
            string = string.replaceFirst("%ownerId%", String.valueOf(petInstance.getPlayer().getObjectId()));
            string = string.replaceFirst("%npcId%", String.valueOf(petInstance.getNpcId()));
            string = string.replaceFirst("%controlItemId%", String.valueOf(petInstance.getControlItem().getItemId()));
            string = string.replaceFirst("%exp%", String.valueOf(petInstance.getExp()));
            string = string.replaceFirst("%sp%", String.valueOf(petInstance.getSp()));
            string = string.replaceFirst("%maxHp%", String.valueOf(petInstance.getMaxHp()));
            string = string.replaceFirst("%maxMp%", String.valueOf(petInstance.getMaxMp()));
            string = string.replaceFirst("%currHp%", String.valueOf((int)petInstance.getCurrentHp()));
            string = string.replaceFirst("%currMp%", String.valueOf((int)petInstance.getCurrentMp()));
            string = string.replaceFirst("%pDef%", String.valueOf(petInstance.getPDef(null)));
            string = string.replaceFirst("%mDef%", String.valueOf(petInstance.getMDef(null, null)));
            string = string.replaceFirst("%pAtk%", String.valueOf(petInstance.getPAtk(null)));
            string = string.replaceFirst("%mAtk%", String.valueOf(petInstance.getMAtk(null, null)));
            string = string.replaceFirst("%accuracy%", String.valueOf(petInstance.getAccuracy()));
            string = string.replaceFirst("%evasionRate%", String.valueOf(petInstance.getEvasionRate(null)));
            string = string.replaceFirst("%crt%", String.valueOf(petInstance.getCriticalHit(null, null)));
            string = string.replaceFirst("%runSpeed%", String.valueOf(petInstance.getRunSpeed()));
            string = string.replaceFirst("%walkSpeed%", String.valueOf(petInstance.getWalkSpeed()));
            string = string.replaceFirst("%pAtkSpd%", String.valueOf(petInstance.getPAtkSpd()));
            string = string.replaceFirst("%mAtkSpd%", String.valueOf(petInstance.getMAtkSpd()));
            string = string.replaceFirst("%dist%", String.valueOf((int)petInstance.getRealDistance(player)));
            string = string.replaceFirst("%STR%", String.valueOf(petInstance.getSTR()));
            string = string.replaceFirst("%DEX%", String.valueOf(petInstance.getDEX()));
            string = string.replaceFirst("%CON%", String.valueOf(petInstance.getCON()));
            string = string.replaceFirst("%INT%", String.valueOf(petInstance.getINT()));
            string = string.replaceFirst("%WIT%", String.valueOf(petInstance.getWIT()));
            string = string.replaceFirst("%MEN%", String.valueOf(petInstance.getMEN()));
            this.show(string, player);
        }
        return true;
    }

    public boolean OnActionShift_SummonInstance(Player player, GameObject gameObject) {
        if (player == null || gameObject == null || !player.getPlayerAccess().CanViewChar) {
            return false;
        }
        if (gameObject.isSummon()) {
            SummonInstance summonInstance = (SummonInstance)gameObject;
            System.out.println(summonInstance.getAI().toString());
            System.out.println((Object)summonInstance.getAI().getIntention());
            System.out.println(summonInstance.getAI().getAttackTarget());
            System.out.println((Object)summonInstance.getAI().getNextAction());
            System.out.println();
            String string = HtmCache.getInstance().getNotNull("scripts/actions/admin.L2SummonInstance.onActionShift.htm", player);
            string = string.replaceFirst("%name%", String.valueOf(summonInstance.getName()));
            string = string.replaceFirst("%level%", String.valueOf(summonInstance.getLevel()));
            string = string.replaceFirst("%class%", String.valueOf(this.getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", "")));
            string = string.replaceFirst("%xyz%", summonInstance.getLoc().x + " " + summonInstance.getLoc().y + " " + summonInstance.getLoc().z);
            string = string.replaceFirst("%heading%", String.valueOf(summonInstance.getLoc().h));
            string = string.replaceFirst("%owner%", String.valueOf(summonInstance.getPlayer().getName()));
            string = string.replaceFirst("%ownerId%", String.valueOf(summonInstance.getPlayer().getObjectId()));
            string = string.replaceFirst("%npcId%", String.valueOf(summonInstance.getNpcId()));
            string = string.replaceFirst("%expPenalty%", String.valueOf(summonInstance.getExpPenalty()));
            string = string.replaceFirst("%maxHp%", String.valueOf(summonInstance.getMaxHp()));
            string = string.replaceFirst("%maxMp%", String.valueOf(summonInstance.getMaxMp()));
            string = string.replaceFirst("%currHp%", String.valueOf((int)summonInstance.getCurrentHp()));
            string = string.replaceFirst("%currMp%", String.valueOf((int)summonInstance.getCurrentMp()));
            string = string.replaceFirst("%pDef%", String.valueOf(summonInstance.getPDef(null)));
            string = string.replaceFirst("%mDef%", String.valueOf(summonInstance.getMDef(null, null)));
            string = string.replaceFirst("%pAtk%", String.valueOf(summonInstance.getPAtk(null)));
            string = string.replaceFirst("%mAtk%", String.valueOf(summonInstance.getMAtk(null, null)));
            string = string.replaceFirst("%accuracy%", String.valueOf(summonInstance.getAccuracy()));
            string = string.replaceFirst("%evasionRate%", String.valueOf(summonInstance.getEvasionRate(null)));
            string = string.replaceFirst("%crt%", String.valueOf(summonInstance.getCriticalHit(null, null)));
            string = string.replaceFirst("%runSpeed%", String.valueOf(summonInstance.getRunSpeed()));
            string = string.replaceFirst("%walkSpeed%", String.valueOf(summonInstance.getWalkSpeed()));
            string = string.replaceFirst("%pAtkSpd%", String.valueOf(summonInstance.getPAtkSpd()));
            string = string.replaceFirst("%mAtkSpd%", String.valueOf(summonInstance.getMAtkSpd()));
            string = string.replaceFirst("%dist%", String.valueOf((int)summonInstance.getRealDistance(player)));
            string = string.replaceFirst("%STR%", String.valueOf(summonInstance.getSTR()));
            string = string.replaceFirst("%DEX%", String.valueOf(summonInstance.getDEX()));
            string = string.replaceFirst("%CON%", String.valueOf(summonInstance.getCON()));
            string = string.replaceFirst("%INT%", String.valueOf(summonInstance.getINT()));
            string = string.replaceFirst("%WIT%", String.valueOf(summonInstance.getWIT()));
            string = string.replaceFirst("%MEN%", String.valueOf(summonInstance.getMEN()));
            this.show(string, player);
        }
        return true;
    }

    public boolean OnActionShift_ItemInstance(Player player, GameObject gameObject) {
        if (player == null || gameObject == null || !player.getPlayerAccess().CanViewChar) {
            return false;
        }
        if (gameObject.isItem()) {
            ItemInstance itemInstance = (ItemInstance)gameObject;
            String string = HtmCache.getInstance().getNotNull("scripts/actions/admin.L2ItemInstance.onActionShift.htm", player);
            string = string.replaceFirst("%name%", String.valueOf(itemInstance.getTemplate().getName()));
            string = string.replaceFirst("%objId%", String.valueOf(itemInstance.getObjectId()));
            string = string.replaceFirst("%itemId%", String.valueOf(itemInstance.getItemId()));
            string = string.replaceFirst("%grade%", String.valueOf((Object)itemInstance.getCrystalType()));
            string = string.replaceFirst("%count%", String.valueOf(itemInstance.getCount()));
            GameObject gameObject2 = GameObjectsStorage.findObject(itemInstance.getOwnerId());
            string = string.replaceFirst("%owner%", String.valueOf(gameObject2 == null ? "none" : gameObject2.getName()));
            string = string.replaceFirst("%ownerId%", String.valueOf(itemInstance.getOwnerId()));
            for (Element element : Element.VALUES) {
                string = string.replaceFirst("%" + element.name().toLowerCase() + "Val%", String.valueOf(itemInstance.getAttributeElementValue(element, true)));
            }
            string = string.replaceFirst("%attrElement%", String.valueOf((Object)itemInstance.getAttributeElement()));
            string = string.replaceFirst("%attrValue%", String.valueOf(itemInstance.getAttributeElementValue()));
            string = string.replaceFirst("%enchLevel%", String.valueOf(itemInstance.getEnchantLevel()));
            string = string.replaceFirst("%type%", String.valueOf(itemInstance.getItemType()));
            string = string.replaceFirst("%dropTime%", String.valueOf(itemInstance.getDropTimeOwner()));
            this.show(string, player);
            player.sendActionFailed();
        }
        return true;
    }

    private String a(NpcInstance npcInstance) {
        return HtmlUtils.htmlNpcName(npcInstance.getNpcId());
    }
}

