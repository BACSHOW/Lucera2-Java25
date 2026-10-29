/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.parser;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import l2.commons.data.xml.AbstractDirParser;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.stats.StatTemplate;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.conditions.Condition;
import l2.gameserver.stats.conditions.ConditionClanPlayerMinPledgeRank;
import l2.gameserver.stats.conditions.ConditionIsPremium;
import l2.gameserver.stats.conditions.ConditionLogicAnd;
import l2.gameserver.stats.conditions.ConditionLogicNot;
import l2.gameserver.stats.conditions.ConditionLogicOr;
import l2.gameserver.stats.conditions.ConditionPlayerCastleId;
import l2.gameserver.stats.conditions.ConditionPlayerClassId;
import l2.gameserver.stats.conditions.ConditionPlayerClassIsMage;
import l2.gameserver.stats.conditions.ConditionPlayerForbiddenClassId;
import l2.gameserver.stats.conditions.ConditionPlayerGender;
import l2.gameserver.stats.conditions.ConditionPlayerInCombat;
import l2.gameserver.stats.conditions.ConditionPlayerInTeam;
import l2.gameserver.stats.conditions.ConditionPlayerInstanceZone;
import l2.gameserver.stats.conditions.ConditionPlayerIsClanLeader;
import l2.gameserver.stats.conditions.ConditionPlayerIsHero;
import l2.gameserver.stats.conditions.ConditionPlayerIsInAcademy;
import l2.gameserver.stats.conditions.ConditionPlayerMaxLevel;
import l2.gameserver.stats.conditions.ConditionPlayerMaxPK;
import l2.gameserver.stats.conditions.ConditionPlayerMinLevel;
import l2.gameserver.stats.conditions.ConditionPlayerMinMaxDamage;
import l2.gameserver.stats.conditions.ConditionPlayerOlympiad;
import l2.gameserver.stats.conditions.ConditionPlayerOnPvPEvent;
import l2.gameserver.stats.conditions.ConditionPlayerPrivateStore;
import l2.gameserver.stats.conditions.ConditionPlayerRace;
import l2.gameserver.stats.conditions.ConditionPlayerResidence;
import l2.gameserver.stats.conditions.ConditionSlotItemId;
import l2.gameserver.stats.conditions.ConditionTargetPlayable;
import l2.gameserver.stats.conditions.ConditionUsingItemType;
import l2.gameserver.stats.conditions.ConditionUsingSkill;
import l2.gameserver.stats.conditions.ConditionZoneType;
import l2.gameserver.stats.funcs.EFunction;
import l2.gameserver.stats.funcs.FuncTemplate;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.stats.triggers.TriggerType;
import l2.gameserver.templates.item.ArmorTemplate;
import l2.gameserver.templates.item.WeaponTemplate;
import org.dom4j.Attribute;
import org.dom4j.Element;

public abstract class StatParser<H extends AbstractHolder>
extends AbstractDirParser<H> {
    protected StatParser(H h) {
        super(h);
    }

    protected Condition parseFirstCond(Element element) {
        List<Element> list = element.elements();
        if (list.isEmpty()) {
            return null;
        }
        Element element2 = list.get(0);
        return this.parseCond(element2);
    }

    protected Condition parseCond(Element element) {
        String string = element.getName();
        if (string.equalsIgnoreCase("and")) {
            return this.parseLogicAnd(element);
        }
        if (string.equalsIgnoreCase("or")) {
            return this.parseLogicOr(element);
        }
        if (string.equalsIgnoreCase("not")) {
            return this.parseLogicNot(element);
        }
        if (string.equalsIgnoreCase("target")) {
            return this.parseTargetCondition(element);
        }
        if (string.equalsIgnoreCase("player")) {
            return this.parsePlayerCondition(element);
        }
        if (string.equalsIgnoreCase("using")) {
            return this.parseUsingCondition(element);
        }
        if (string.equalsIgnoreCase("zone")) {
            return this.parseZoneCondition(element);
        }
        return null;
    }

    protected Condition parseLogicAnd(Element element) {
        ConditionLogicAnd conditionLogicAnd = new ConditionLogicAnd();
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            conditionLogicAnd.add(this.parseCond(element2));
        }
        if (conditionLogicAnd._conditions == null || conditionLogicAnd._conditions.length == 0) {
            this.error("Empty <and> condition in " + this.getCurrentFileName());
        }
        return conditionLogicAnd;
    }

    protected Condition parseLogicOr(Element element) {
        ConditionLogicOr conditionLogicOr = new ConditionLogicOr();
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            conditionLogicOr.add(this.parseCond(element2));
        }
        if (conditionLogicOr._conditions == null || conditionLogicOr._conditions.length == 0) {
            this.error("Empty <or> condition in " + this.getCurrentFileName());
        }
        return conditionLogicOr;
    }

    protected Condition parseLogicNot(Element element) {
        Iterator<Element> iterator = element.elements().iterator();
        if (iterator.hasNext()) {
            Element element2 = iterator.next();
            return new ConditionLogicNot(this.parseCond(element2));
        }
        this.error("Empty <not> condition in " + this.getCurrentFileName());
        return null;
    }

    protected Condition parseTargetCondition(Element element) {
        Condition condition = null;
        Iterator<Attribute> iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            String string = attribute.getName();
            String string2 = attribute.getValue();
            if (!string.equalsIgnoreCase("pvp")) continue;
            condition = this.joinAnd(condition, new ConditionTargetPlayable(Boolean.valueOf(string2)));
        }
        return condition;
    }

    protected Condition parseZoneCondition(Element element) {
        Condition condition = null;
        Iterator<Attribute> iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            String string = attribute.getName();
            String string2 = attribute.getValue();
            if (!string.equalsIgnoreCase("type")) continue;
            condition = this.joinAnd(condition, new ConditionZoneType(string2));
        }
        return condition;
    }

    protected Condition parsePlayerCondition(Element element) {
        Condition condition = null;
        Iterator<Attribute> iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            String[] stringArray;
            Attribute attribute = iterator.next();
            String string = attribute.getName();
            String string2 = attribute.getValue();
            if (string.equalsIgnoreCase("residence")) {
                stringArray = string2.split(";");
                condition = this.joinAnd(condition, new ConditionPlayerResidence(Integer.parseInt(stringArray[1]), ResidenceType.valueOf(stringArray[0])));
                continue;
            }
            if (string.equalsIgnoreCase("classId")) {
                condition = this.joinAnd(condition, new ConditionPlayerClassId(string2.split(",")));
                continue;
            }
            if (string.equalsIgnoreCase("forbiddenClassIds")) {
                condition = this.joinAnd(condition, new ConditionPlayerForbiddenClassId(string2.split(";")));
                continue;
            }
            if (string.equalsIgnoreCase("privateStoreType")) {
                condition = this.joinAnd(condition, new ConditionPlayerPrivateStore(string2));
                continue;
            }
            if (string.equalsIgnoreCase("olympiad")) {
                condition = this.joinAnd(condition, new ConditionPlayerOlympiad(Boolean.valueOf(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("min_pledge_rank")) {
                condition = this.joinAnd(condition, new ConditionClanPlayerMinPledgeRank(string2));
                continue;
            }
            if (string.equalsIgnoreCase("is_hero")) {
                condition = this.joinAnd(condition, new ConditionPlayerIsHero(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("is_clan_leader")) {
                condition = this.joinAnd(condition, new ConditionPlayerIsClanLeader(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("is_in_academy")) {
                condition = this.joinAnd(condition, new ConditionPlayerIsInAcademy(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("is_premium")) {
                condition = this.joinAnd(condition, new ConditionIsPremium(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("castleId")) {
                condition = this.joinAnd(condition, new ConditionPlayerCastleId(Integer.parseInt(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("is_in_combat")) {
                condition = this.joinAnd(condition, new ConditionPlayerInCombat(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("is_in_team")) {
                condition = this.joinAnd(condition, new ConditionPlayerInTeam(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("on_pvp_event")) {
                condition = this.joinAnd(condition, new ConditionPlayerOnPvPEvent(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("class_is_mage")) {
                condition = this.joinAnd(condition, new ConditionPlayerClassIsMage(Boolean.parseBoolean(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("instance_zone")) {
                condition = this.joinAnd(condition, new ConditionPlayerInstanceZone(Integer.parseInt(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("minLevel")) {
                condition = this.joinAnd(condition, new ConditionPlayerMinLevel(Integer.parseInt(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("maxLevel")) {
                condition = this.joinAnd(condition, new ConditionPlayerMaxLevel(Integer.parseInt(string2)));
                continue;
            }
            if (string.equalsIgnoreCase("race")) {
                condition = this.joinAnd(condition, new ConditionPlayerRace(string2));
                continue;
            }
            if (string.equalsIgnoreCase("gender")) {
                condition = this.joinAnd(condition, new ConditionPlayerGender(string2));
                continue;
            }
            if (string.equalsIgnoreCase("maxPK")) {
                condition = this.joinAnd(condition, new ConditionPlayerMaxPK(Integer.parseInt(string2)));
                continue;
            }
            if (!string.equalsIgnoreCase("damage")) continue;
            stringArray = string2.split(";");
            condition = this.joinAnd(condition, new ConditionPlayerMinMaxDamage(Double.parseDouble(stringArray[0]), Double.parseDouble(stringArray[1])));
        }
        return condition;
    }

    protected Condition parseUsingCondition(Element element) {
        Condition condition = null;
        Iterator<Attribute> iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            String string = attribute.getName();
            String string2 = attribute.getValue();
            if (string.equalsIgnoreCase("slotitem")) {
                StringTokenizer stringTokenizer = new StringTokenizer(string2, ";");
                int n = Integer.parseInt(stringTokenizer.nextToken().trim());
                int n2 = Integer.parseInt(stringTokenizer.nextToken().trim());
                int n3 = 0;
                if (stringTokenizer.hasMoreTokens()) {
                    n3 = Integer.parseInt(stringTokenizer.nextToken().trim());
                }
                condition = this.joinAnd(condition, new ConditionSlotItemId(n2, n, n3));
                continue;
            }
            if (string.equalsIgnoreCase("kind") || string.equalsIgnoreCase("weapon")) {
                long l = 0L;
                StringTokenizer stringTokenizer = new StringTokenizer(string2, ",");
                block1: while (stringTokenizer.hasMoreTokens()) {
                    String string3 = stringTokenizer.nextToken().trim();
                    for (WeaponTemplate.WeaponType weaponType : WeaponTemplate.WeaponType.VALUES) {
                        if (!weaponType.toString().equalsIgnoreCase(string3)) continue;
                        l |= weaponType.mask();
                        continue block1;
                    }
                    for (Enum enum_ : ArmorTemplate.ArmorType.VALUES) {
                        if (!((ArmorTemplate.ArmorType)enum_).toString().equalsIgnoreCase(string3)) continue;
                        l |= ((ArmorTemplate.ArmorType)enum_).mask();
                        continue block1;
                    }
                    this.error("Invalid item kind: \"" + string3 + "\" in " + this.getCurrentFileName());
                }
                if (l == 0L) continue;
                condition = this.joinAnd(condition, new ConditionUsingItemType(l));
                continue;
            }
            if (!string.equalsIgnoreCase("skill")) continue;
            condition = this.joinAnd(condition, new ConditionUsingSkill(Integer.parseInt(string2)));
        }
        return condition;
    }

    protected Condition joinAnd(Condition condition, Condition condition2) {
        if (condition == null) {
            return condition2;
        }
        if (condition instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd)condition).add(condition2);
            return condition;
        }
        ConditionLogicAnd conditionLogicAnd = new ConditionLogicAnd();
        conditionLogicAnd.add(condition);
        conditionLogicAnd.add(condition2);
        return conditionLogicAnd;
    }

    protected void parseFor(Element element, StatTemplate statTemplate) {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            String string = element2.getName();
            EFunction eFunction = EFunction.VALUES_BY_LOWER_NAME.get(string.toLowerCase());
            if (null == eFunction) {
                throw new RuntimeException("Unknown function specified '" + string + "'");
            }
            this.attachFunc(element2, statTemplate, eFunction);
        }
    }

    protected void parseTriggers(Element element, StatTemplate statTemplate) {
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            int n = this.parseNumber(element2.attributeValue("id")).intValue();
            int n2 = this.parseNumber(element2.attributeValue("level")).intValue();
            TriggerType triggerType = TriggerType.valueOf(element2.attributeValue("type"));
            double d = this.parseNumber(element2.attributeValue("chance")).doubleValue();
            TriggerInfo triggerInfo = new TriggerInfo(n, n2, triggerType, d);
            statTemplate.addTrigger(triggerInfo);
            Iterator<Element> iterator2 = element2.elementIterator();
            while (iterator2.hasNext()) {
                Element element3 = iterator2.next();
                Condition condition = this.parseFirstCond(element3);
                if (condition == null) continue;
                triggerInfo.addCondition(condition);
            }
        }
    }

    protected void attachFunc(Element element, StatTemplate statTemplate, String string) {
        Stats stats = Stats.valueOfXml(element.attributeValue("stat"));
        String string2 = element.attributeValue("order");
        int n = this.parseNumber(string2).intValue();
        Condition condition = this.parseFirstCond(element);
        double d = 0.0;
        if (element.attributeValue("value") != null) {
            d = this.parseNumber(element.attributeValue("value")).doubleValue();
        }
        statTemplate.attachFunc(new FuncTemplate(condition, string, stats, n, d));
    }

    protected void attachFunc(Element element, StatTemplate statTemplate, EFunction eFunction) {
        Stats stats = Stats.valueOfXml(element.attributeValue("stat"));
        String string = element.attributeValue("order");
        int n = this.parseNumber(string).intValue();
        Condition condition = this.parseFirstCond(element);
        double d = 0.0;
        if (element.attributeValue("value") != null) {
            d = this.parseNumber(element.attributeValue("value")).doubleValue();
        }
        statTemplate.attachFunc(new FuncTemplate(condition, eFunction, stats, n, d));
    }

    protected Number parseNumber(String string) {
        if (string.charAt(0) == '#') {
            string = this.getTableValue(string).toString();
        }
        try {
            if (string.indexOf(46) == -1) {
                int n = 10;
                if (string.length() > 2 && string.substring(0, 2).equalsIgnoreCase("0x")) {
                    string = string.substring(2);
                    n = 16;
                }
                return Integer.valueOf(string, n);
            }
            return Double.valueOf(string);
        }
        catch (NumberFormatException numberFormatException) {
            return null;
        }
    }

    protected abstract Object getTableValue(String var1);
}

