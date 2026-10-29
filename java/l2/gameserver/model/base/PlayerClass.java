/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import l2.gameserver.Config;
import l2.gameserver.model.base.ClassLevel;
import l2.gameserver.model.base.ClassType;
import l2.gameserver.model.base.Race;

public enum PlayerClass {
    HumanFighter(Race.human, ClassType.Fighter, ClassLevel.First),
    Warrior(Race.human, ClassType.Fighter, ClassLevel.Second),
    Gladiator(Race.human, ClassType.Fighter, ClassLevel.Third),
    Warlord(Race.human, ClassType.Fighter, ClassLevel.Third),
    HumanKnight(Race.human, ClassType.Fighter, ClassLevel.Second),
    Paladin(Race.human, ClassType.Fighter, ClassLevel.Third),
    DarkAvenger(Race.human, ClassType.Fighter, ClassLevel.Third),
    Rogue(Race.human, ClassType.Fighter, ClassLevel.Second),
    TreasureHunter(Race.human, ClassType.Fighter, ClassLevel.Third),
    Hawkeye(Race.human, ClassType.Fighter, ClassLevel.Third),
    HumanMystic(Race.human, ClassType.Mystic, ClassLevel.First),
    HumanWizard(Race.human, ClassType.Mystic, ClassLevel.Second),
    Sorceror(Race.human, ClassType.Mystic, ClassLevel.Third),
    Necromancer(Race.human, ClassType.Mystic, ClassLevel.Third),
    Warlock(Race.human, ClassType.Mystic, ClassLevel.Third),
    Cleric(Race.human, ClassType.Priest, ClassLevel.Second),
    Bishop(Race.human, ClassType.Priest, ClassLevel.Third),
    Prophet(Race.human, ClassType.Priest, ClassLevel.Third),
    ElvenFighter(Race.elf, ClassType.Fighter, ClassLevel.First),
    ElvenKnight(Race.elf, ClassType.Fighter, ClassLevel.Second),
    TempleKnight(Race.elf, ClassType.Fighter, ClassLevel.Third),
    Swordsinger(Race.elf, ClassType.Fighter, ClassLevel.Third),
    ElvenScout(Race.elf, ClassType.Fighter, ClassLevel.Second),
    Plainswalker(Race.elf, ClassType.Fighter, ClassLevel.Third),
    SilverRanger(Race.elf, ClassType.Fighter, ClassLevel.Third),
    ElvenMystic(Race.elf, ClassType.Mystic, ClassLevel.First),
    ElvenWizard(Race.elf, ClassType.Mystic, ClassLevel.Second),
    Spellsinger(Race.elf, ClassType.Mystic, ClassLevel.Third),
    ElementalSummoner(Race.elf, ClassType.Mystic, ClassLevel.Third),
    ElvenOracle(Race.elf, ClassType.Priest, ClassLevel.Second),
    ElvenElder(Race.elf, ClassType.Priest, ClassLevel.Third),
    DarkElvenFighter(Race.darkelf, ClassType.Fighter, ClassLevel.First),
    PalusKnight(Race.darkelf, ClassType.Fighter, ClassLevel.Second),
    ShillienKnight(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
    Bladedancer(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
    Assassin(Race.darkelf, ClassType.Fighter, ClassLevel.Second),
    AbyssWalker(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
    PhantomRanger(Race.darkelf, ClassType.Fighter, ClassLevel.Third),
    DarkElvenMystic(Race.darkelf, ClassType.Mystic, ClassLevel.First),
    DarkElvenWizard(Race.darkelf, ClassType.Mystic, ClassLevel.Second),
    Spellhowler(Race.darkelf, ClassType.Mystic, ClassLevel.Third),
    PhantomSummoner(Race.darkelf, ClassType.Mystic, ClassLevel.Third),
    ShillienOracle(Race.darkelf, ClassType.Priest, ClassLevel.Second),
    ShillienElder(Race.darkelf, ClassType.Priest, ClassLevel.Third),
    OrcFighter(Race.orc, ClassType.Fighter, ClassLevel.First),
    orcRaider(Race.orc, ClassType.Fighter, ClassLevel.Second),
    Destroyer(Race.orc, ClassType.Fighter, ClassLevel.Third),
    orcMonk(Race.orc, ClassType.Fighter, ClassLevel.Second),
    Tyrant(Race.orc, ClassType.Fighter, ClassLevel.Third),
    orcMystic(Race.orc, ClassType.Mystic, ClassLevel.First),
    orcShaman(Race.orc, ClassType.Mystic, ClassLevel.Second),
    Overlord(Race.orc, ClassType.Mystic, ClassLevel.Third),
    Warcryer(Race.orc, ClassType.Mystic, ClassLevel.Third),
    DwarvenFighter(Race.dwarf, ClassType.Fighter, ClassLevel.First),
    DwarvenScavenger(Race.dwarf, ClassType.Fighter, ClassLevel.Second),
    BountyHunter(Race.dwarf, ClassType.Fighter, ClassLevel.Third),
    DwarvenArtisan(Race.dwarf, ClassType.Fighter, ClassLevel.Second),
    Warsmith(Race.dwarf, ClassType.Fighter, ClassLevel.Third),
    DummyEntry1(null, null, null),
    DummyEntry2(null, null, null),
    DummyEntry3(null, null, null),
    DummyEntry4(null, null, null),
    DummyEntry5(null, null, null),
    DummyEntry6(null, null, null),
    DummyEntry7(null, null, null),
    DummyEntry8(null, null, null),
    DummyEntry9(null, null, null),
    DummyEntry10(null, null, null),
    DummyEntry11(null, null, null),
    DummyEntry12(null, null, null),
    DummyEntry13(null, null, null),
    DummyEntry14(null, null, null),
    DummyEntry15(null, null, null),
    DummyEntry16(null, null, null),
    DummyEntry17(null, null, null),
    DummyEntry18(null, null, null),
    DummyEntry19(null, null, null),
    DummyEntry20(null, null, null),
    DummyEntry21(null, null, null),
    DummyEntry22(null, null, null),
    DummyEntry23(null, null, null),
    DummyEntry24(null, null, null),
    DummyEntry25(null, null, null),
    DummyEntry26(null, null, null),
    DummyEntry27(null, null, null),
    DummyEntry28(null, null, null),
    DummyEntry29(null, null, null),
    DummyEntry30(null, null, null),
    Duelist(Race.human, ClassType.Fighter, ClassLevel.Fourth),
    Dreadnought(Race.human, ClassType.Fighter, ClassLevel.Fourth),
    PhoenixKnight(Race.human, ClassType.Fighter, ClassLevel.Fourth),
    HellKnight(Race.human, ClassType.Fighter, ClassLevel.Fourth),
    Sagittarius(Race.human, ClassType.Fighter, ClassLevel.Fourth),
    Adventurer(Race.human, ClassType.Fighter, ClassLevel.Fourth),
    Archmage(Race.human, ClassType.Mystic, ClassLevel.Fourth),
    Soultaker(Race.human, ClassType.Mystic, ClassLevel.Fourth),
    ArcanaLord(Race.human, ClassType.Mystic, ClassLevel.Fourth),
    Cardinal(Race.human, ClassType.Priest, ClassLevel.Fourth),
    Hierophant(Race.human, ClassType.Priest, ClassLevel.Fourth),
    EvaTemplar(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
    SwordMuse(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
    WindRider(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
    MoonlightSentinel(Race.elf, ClassType.Fighter, ClassLevel.Fourth),
    MysticMuse(Race.elf, ClassType.Mystic, ClassLevel.Fourth),
    ElementalMaster(Race.elf, ClassType.Mystic, ClassLevel.Fourth),
    EvaSaint(Race.elf, ClassType.Priest, ClassLevel.Fourth),
    ShillienTemplar(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
    SpectralDancer(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
    GhostHunter(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
    GhostSentinel(Race.darkelf, ClassType.Fighter, ClassLevel.Fourth),
    StormScreamer(Race.darkelf, ClassType.Mystic, ClassLevel.Fourth),
    SpectralMaster(Race.darkelf, ClassType.Mystic, ClassLevel.Fourth),
    ShillienSaint(Race.darkelf, ClassType.Priest, ClassLevel.Fourth),
    Titan(Race.orc, ClassType.Fighter, ClassLevel.Fourth),
    GrandKhauatari(Race.orc, ClassType.Fighter, ClassLevel.Fourth),
    Dominator(Race.orc, ClassType.Mystic, ClassLevel.Fourth),
    Doomcryer(Race.orc, ClassType.Mystic, ClassLevel.Fourth),
    FortuneSeeker(Race.dwarf, ClassType.Fighter, ClassLevel.Fourth),
    Maestro(Race.dwarf, ClassType.Fighter, ClassLevel.Fourth),
    DummyEntry31(null, null, null),
    DummyEntry32(null, null, null),
    DummyEntry33(null, null, null),
    DummyEntry34(null, null, null);

    public static final PlayerClass[] VALUES;
    private Race Oc;
    private ClassLevel OI;
    private ClassType OJ;
    private static final Set<PlayerClass> OL;
    private static final Set<PlayerClass> OM;
    private static final Set<PlayerClass> ON;
    private static final Set<PlayerClass> OO;
    private static final Set<PlayerClass> OP;
    private static final Set<PlayerClass> OQ;
    private static final Set<PlayerClass> OR;
    private static final EnumMap<PlayerClass, Set<PlayerClass>> OS;

    private PlayerClass(Race race, ClassType classType, ClassLevel classLevel) {
        this.Oc = race;
        this.OI = classLevel;
        this.OJ = classType;
    }

    public final Set<PlayerClass> getAvailableSubclasses() {
        EnumSet<PlayerClass> enumSet = null;
        if (this.OI == ClassLevel.Third || this.OI == ClassLevel.Fourth) {
            Set<PlayerClass> set;
            enumSet = EnumSet.copyOf(OL);
            if (!Config.ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL) {
                enumSet.removeAll(OM);
            }
            enumSet.remove((Object)this);
            if (!Config.ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF) {
                switch (this.Oc) {
                    case elf: {
                        enumSet.removeAll(PlayerClass.getSet(Race.darkelf, ClassLevel.Third));
                        break;
                    }
                    case darkelf: {
                        enumSet.removeAll(PlayerClass.getSet(Race.elf, ClassLevel.Third));
                    }
                }
            }
            if ((set = OS.get((Object)this)) != null && !Config.ALTSUBCLASS_LIST_ALL) {
                enumSet.removeAll(set);
            }
        }
        return enumSet;
    }

    public static EnumSet<PlayerClass> getSet(Race race, ClassLevel classLevel) {
        EnumSet<PlayerClass> enumSet = EnumSet.noneOf(PlayerClass.class);
        for (PlayerClass playerClass : EnumSet.allOf(PlayerClass.class)) {
            if (race != null && !playerClass.isOfRace(race) || classLevel != null && !playerClass.isOfLevel(classLevel)) continue;
            enumSet.add(playerClass);
        }
        return enumSet;
    }

    public final boolean isOfRace(Race race) {
        return this.Oc == race;
    }

    public final boolean isOfType(ClassType classType) {
        return this.OJ == classType;
    }

    public final boolean isOfLevel(ClassLevel classLevel) {
        return this.OI == classLevel;
    }

    public static boolean areClassesComportable(PlayerClass playerClass, PlayerClass playerClass2) {
        if (!Config.ALTSUBCLASS_ALLOW_FOR_ELF_TO_DARK_ELF && (playerClass.isOfRace(Race.elf) && playerClass2.isOfRace(Race.darkelf) || playerClass.isOfRace(Race.darkelf) && playerClass2.isOfRace(Race.elf))) {
            return false;
        }
        if (!(Config.ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL || playerClass != Overlord && playerClass != Warsmith && playerClass2 != Overlord && playerClass2 != Warsmith)) {
            return false;
        }
        return OS.get((Object)playerClass) != OS.get((Object)playerClass2);
    }

    static {
        VALUES = PlayerClass.values();
        OM = EnumSet.of(Overlord, Warsmith);
        ON = EnumSet.of(DarkAvenger, Paladin, TempleKnight, ShillienKnight);
        OO = EnumSet.of(TreasureHunter, AbyssWalker, Plainswalker);
        OP = EnumSet.of(Hawkeye, SilverRanger, PhantomRanger);
        OQ = EnumSet.of(Warlock, ElementalSummoner, PhantomSummoner);
        OR = EnumSet.of(Sorceror, Spellsinger, Spellhowler);
        OS = new EnumMap(PlayerClass.class);
        EnumSet<PlayerClass> enumSet = PlayerClass.getSet(null, ClassLevel.Third);
        if (!Config.ALTSUBCLASS_ALLOW_OVER_AND_WARSMITH_TO_ALL) {
            enumSet.removeAll(OM);
        }
        OL = enumSet;
        OS.put(DarkAvenger, ON);
        OS.put(HellKnight, ON);
        OS.put(Paladin, ON);
        OS.put(PhoenixKnight, ON);
        OS.put(TempleKnight, ON);
        OS.put(EvaTemplar, ON);
        OS.put(ShillienKnight, ON);
        OS.put(ShillienTemplar, ON);
        OS.put(TreasureHunter, OO);
        OS.put(Adventurer, OO);
        OS.put(AbyssWalker, OO);
        OS.put(GhostHunter, OO);
        OS.put(Plainswalker, OO);
        OS.put(WindRider, OO);
        OS.put(Hawkeye, OP);
        OS.put(Sagittarius, OP);
        OS.put(SilverRanger, OP);
        OS.put(MoonlightSentinel, OP);
        OS.put(PhantomRanger, OP);
        OS.put(GhostSentinel, OP);
        OS.put(Warlock, OQ);
        OS.put(ArcanaLord, OQ);
        OS.put(ElementalSummoner, OQ);
        OS.put(ElementalMaster, OQ);
        OS.put(PhantomSummoner, OQ);
        OS.put(SpectralMaster, OQ);
        OS.put(Sorceror, OR);
        OS.put(Archmage, OR);
        OS.put(Spellsinger, OR);
        OS.put(MysticMuse, OR);
        OS.put(Spellhowler, OR);
        OS.put(StormScreamer, OR);
        OS.put(Duelist, EnumSet.of(Gladiator));
        OS.put(Dreadnought, EnumSet.of(Warlord));
        OS.put(Soultaker, EnumSet.of(Necromancer));
        OS.put(Cardinal, EnumSet.of(Bishop));
        OS.put(Hierophant, EnumSet.of(Prophet));
        OS.put(SwordMuse, EnumSet.of(Swordsinger));
        OS.put(EvaSaint, EnumSet.of(ElvenElder));
        OS.put(SpectralDancer, EnumSet.of(Bladedancer));
        OS.put(Titan, EnumSet.of(Destroyer));
        OS.put(GrandKhauatari, EnumSet.of(Tyrant));
        OS.put(Dominator, EnumSet.of(Overlord));
        OS.put(Doomcryer, EnumSet.of(Warcryer));
    }
}

