/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.SubClass;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.ClassType2;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;

public final class SkillAcquireHolder
extends AbstractHolder {
    private static final SkillAcquireHolder wN = new SkillAcquireHolder();
    private TIntObjectHashMap<List<SkillLearn>> wO = new TIntObjectHashMap();
    private TIntObjectHashMap<List<SkillLearn>> wP = new TIntObjectHashMap();
    private List<SkillLearn> wQ = new ArrayList<SkillLearn>();
    private List<SkillLearn> wR = new ArrayList<SkillLearn>();
    private List<SkillLearn> wS = new ArrayList<SkillLearn>();
    private List<SkillLearn> wT = new ArrayList<SkillLearn>();

    public static SkillAcquireHolder getInstance() {
        return wN;
    }

    public int getMinLevelForNewSkill(ClassId classId, int n, AcquireType acquireType) {
        List<SkillLearn> list;
        switch (acquireType) {
            case NORMAL: {
                list = this.wO.get(classId.getId());
                if (list != null) break;
                this.info("skill tree for class " + classId.getId() + " is not defined !");
                return 0;
            }
            default: {
                return 0;
            }
        }
        int n2 = 0;
        for (SkillLearn skillLearn : list) {
            if (skillLearn.getMinLevel() <= n || n2 != 0 && skillLearn.getMinLevel() >= n2) continue;
            n2 = skillLearn.getMinLevel();
        }
        return n2;
    }

    public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType acquireType) {
        return this.getAvailableSkills(player, player.getClassId(), acquireType, null);
    }

    public Collection<SkillLearn> getAvailableSkills(Player player, ClassId classId, AcquireType acquireType, SubUnit subUnit) {
        switch (acquireType) {
            case NORMAL: {
                Collection collection = this.wO.get(classId.getId());
                if (collection == null) {
                    this.info("skill tree for class " + (Object)((Object)classId) + " is not defined !");
                    return Collections.emptyList();
                }
                return this.a(collection, player.getAllSkillsArray(), player.getLevel());
            }
            case FISHING: {
                Collection collection = this.wP.get(player.getRace().ordinal());
                if (collection == null) {
                    this.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
                    return Collections.emptyList();
                }
                return this.a(collection, player.getAllSkillsArray(), player.getLevel());
            }
            case CLAN: {
                List<SkillLearn> list = this.wQ;
                Collection<Skill> collection = player.getClan().getSkills();
                return this.a(list, collection.toArray(new Skill[collection.size()]), player.getClan().getLevel());
            }
            case NOBLES: {
                List<SkillLearn> list = this.wR;
                if (list == null) {
                    this.info("Nobles skill tree is not defined !");
                    return Collections.emptyList();
                }
                return this.a(list, player.getAllSkillsArray(), player.getLevel(), player);
            }
            case HERO: {
                List<SkillLearn> list = this.wS;
                if (list == null) {
                    this.info("Hero skill tree is not defined !");
                    return Collections.emptyList();
                }
                return this.a(list, player.getAllSkillsArray(), player.getLevel(), player);
            }
            case CLAN_LEADER: {
                List<SkillLearn> list = this.wT;
                if (list == null) {
                    this.info("Hero skill tree is not defined !");
                    return Collections.emptyList();
                }
                return this.a(list, player.getAllSkillsArray(), player.getLevel(), player);
            }
        }
        return Collections.emptyList();
    }

    private Collection<SkillLearn> a(Collection<SkillLearn> collection, Skill[] skillArray, int n) {
        return this.a(collection, skillArray, n, null);
    }

    private Collection<SkillLearn> a(Collection<SkillLearn> collection, Skill[] skillArray, int n, Player player) {
        TreeMap<Integer, SkillLearn> treeMap = new TreeMap<Integer, SkillLearn>();
        for (SkillLearn skillLearn : collection) {
            boolean bl;
            if (skillLearn.getMinLevel() > n) continue;
            if (player != null && skillLearn.getClassType2() != ClassType2.None) {
                bl = false;
                for (Map.Entry<Integer, SubClass> entry : player.getSubClasses().entrySet()) {
                    if (entry.getValue().isBase()) continue;
                    for (ClassId classId : ClassId.values()) {
                        if (classId.getId() != entry.getKey().intValue() || classId.getType2() != skillLearn.getClassType2()) continue;
                        bl = true;
                    }
                }
                if (!bl) continue;
            }
            bl = false;
            for (int i = 0; i < skillArray.length && !bl; ++i) {
                if (skillArray[i].getId() != skillLearn.getId()) continue;
                bl = true;
                if (skillArray[i].getLevel() != skillLearn.getLevel() - 1) continue;
                treeMap.put(skillLearn.getId(), skillLearn);
            }
            if (bl || skillLearn.getLevel() != 1) continue;
            treeMap.put(skillLearn.getId(), skillLearn);
        }
        return treeMap.values();
    }

    public List<SkillLearn> getAllSkillLearn(Player player, ClassId classId, AcquireType acquireType) {
        switch (acquireType) {
            case NORMAL: {
                return this.wO.get(classId.getId());
            }
            case FISHING: {
                return this.wP.get(player.getRace().ordinal());
            }
            case CLAN: {
                return this.wQ;
            }
            case NOBLES: {
                return this.wR;
            }
            case HERO: {
                return this.wS;
            }
            case CLAN_LEADER: {
                return this.wT;
            }
        }
        return Collections.emptyList();
    }

    public SkillLearn getSkillLearn(Player player, ClassId classId, int n, int n2, AcquireType acquireType) {
        List<SkillLearn> list = this.getAllSkillLearn(player, classId, acquireType);
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (SkillLearn skillLearn : list) {
            if (skillLearn.getLevel() != n2 || skillLearn.getId() != n) continue;
            return skillLearn;
        }
        return null;
    }

    public boolean isSkillPossible(Player player, Skill skill, AcquireType acquireType) {
        List<SkillLearn> list;
        Clan clan = null;
        switch (acquireType) {
            case NORMAL: {
                list = this.wO.get(player.getActiveClassId());
                break;
            }
            case FISHING: {
                list = this.wP.get(player.getRace().ordinal());
                break;
            }
            case CLAN: {
                clan = player.getClan();
                if (clan == null) {
                    return false;
                }
                list = this.wQ;
                break;
            }
            case NOBLES: {
                if (!player.isNoble()) {
                    return false;
                }
                list = this.wR;
                break;
            }
            case HERO: {
                if (!player.isHero()) {
                    return false;
                }
                list = this.wS;
                break;
            }
            case CLAN_LEADER: {
                if (!player.isClanLeader()) {
                    return false;
                }
                list = this.wT;
                break;
            }
            default: {
                return false;
            }
        }
        return this.a(list, skill);
    }

    public boolean isSkillPossible(Player player, ClassId classId, Skill skill, AcquireType acquireType) {
        List<SkillLearn> list;
        Clan clan = null;
        switch (acquireType) {
            case NORMAL: {
                list = this.wO.get(classId.getId());
                break;
            }
            case FISHING: {
                list = this.wP.get(player.getRace().ordinal());
                break;
            }
            case CLAN: {
                clan = player.getClan();
                if (clan == null) {
                    return false;
                }
                list = this.wQ;
                break;
            }
            default: {
                return false;
            }
        }
        return this.a(list, skill);
    }

    private boolean a(Collection<SkillLearn> collection, Skill skill) {
        for (SkillLearn skillLearn : collection) {
            if (skillLearn.getId() != skill.getId() || skillLearn.getLevel() > skill.getLevel()) continue;
            return true;
        }
        return false;
    }

    public boolean isSkillPossible(Player player, Skill skill) {
        for (AcquireType acquireType : AcquireType.VALUES) {
            if (!this.isSkillPossible(player, skill, acquireType)) continue;
            return true;
        }
        return false;
    }

    public boolean isSkillPossible(Player player, ClassId classId, Skill skill) {
        for (AcquireType acquireType : AcquireType.VALUES) {
            if (!this.isSkillPossible(player, classId, skill, acquireType)) continue;
            return true;
        }
        return false;
    }

    public List<SkillLearn> getSkillLearnListByItemId(Player player, int n) {
        List<SkillLearn> list = this.wO.get(player.getActiveClassId());
        if (list == null) {
            return Collections.emptyList();
        }
        ArrayList<SkillLearn> arrayList = new ArrayList<SkillLearn>(1);
        for (SkillLearn skillLearn : list) {
            if (skillLearn.getItemId() != n) continue;
            arrayList.add(skillLearn);
        }
        return arrayList;
    }

    public List<SkillLearn> getAllNormalSkillTreeWithForgottenScrolls() {
        ArrayList<SkillLearn> arrayList = new ArrayList<SkillLearn>();
        TIntObjectIterator<List<SkillLearn>> tIntObjectIterator = this.wO.iterator();
        while (tIntObjectIterator.hasNext()) {
            tIntObjectIterator.advance();
            for (SkillLearn skillLearn : tIntObjectIterator.value()) {
                if (skillLearn.getItemId() <= 0 || !skillLearn.isClicked()) continue;
                arrayList.add(skillLearn);
            }
        }
        return arrayList;
    }

    public void addAllNormalSkillLearns(TIntObjectHashMap<List<SkillLearn>> tIntObjectHashMap) {
        for (ClassId classId : ClassId.VALUES) {
            if (classId.getRace() == null) continue;
            int n = classId.getId();
            List<SkillLearn> list = tIntObjectHashMap.get(n);
            if (list == null) {
                this.info("Not found NORMAL skill learn for class " + n);
                continue;
            }
            this.wO.put(classId.getId(), list);
            ClassId classId2 = classId.getParent(1);
            if (classId2 == classId.getParent(0)) {
                classId2 = null;
            }
            classId = classId.getParent(0);
            while (classId != null) {
                List<SkillLearn> list2 = this.wO.get(classId.getId());
                list.addAll(list2);
                if ((classId = classId.getParent(0)) != null || classId2 == null) continue;
                classId = classId2;
                classId2 = classId2.getParent(1);
            }
        }
    }

    public void addAllFishingLearns(int n, List<SkillLearn> list) {
        this.wP.put(n, list);
    }

    public void addAllPledgeLearns(List<SkillLearn> list) {
        this.wQ.addAll(list);
    }

    public void addAllNoblesseLearns(List<SkillLearn> list) {
        this.wR.addAll(list);
    }

    public void addAllHeroLearns(List<SkillLearn> list) {
        this.wS.addAll(list);
    }

    public void addAllClanLeaderLearns(List<SkillLearn> list) {
        this.wT.addAll(list);
    }

    @Override
    public void log() {
        this.info("load " + this.a(this.wO) + " normal learns for " + this.wO.size() + " classes.");
        this.info("load " + this.a(this.wP) + " fishing learns for " + this.wP.size() + " races.");
        this.info("load " + this.wQ.size() + " pledge learns.");
        this.info("load " + this.wR.size() + " nobles skills.");
        this.info("load " + this.wS.size() + " hero skills.");
        this.info("load " + this.wT.size() + " clan leader skills.");
    }

    @Override
    @Deprecated
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        this.wO.clear();
        this.wP.clear();
        this.wQ.clear();
        this.wR.clear();
        this.wS.clear();
        this.wT.clear();
    }

    private int a(TIntObjectHashMap<List<SkillLearn>> tIntObjectHashMap) {
        int n = 0;
        TIntObjectIterator<List<SkillLearn>> tIntObjectIterator = tIntObjectHashMap.iterator();
        while (tIntObjectIterator.hasNext()) {
            tIntObjectIterator.advance();
            n += tIntObjectIterator.value().size();
        }
        return n;
    }
}

