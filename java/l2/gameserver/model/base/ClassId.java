/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.base;

import gnu.trove.TIntObjectHashMap;
import l2.gameserver.model.base.ClassType2;
import l2.gameserver.model.base.Race;

public enum ClassId {
    fighter(0, false, Race.human, null, null, 1, null),
    warrior(1, false, Race.human, fighter, null, 2, null),
    gladiator(2, false, Race.human, warrior, null, 3, ClassType2.Warrior),
    warlord(3, false, Race.human, warrior, null, 3, ClassType2.Warrior),
    knight(4, false, Race.human, fighter, null, 2, null),
    paladin(5, false, Race.human, knight, null, 3, ClassType2.Knight),
    dark_avenger(6, false, Race.human, knight, null, 3, ClassType2.Knight),
    rogue(7, false, Race.human, fighter, null, 2, null),
    treasure_hunter(8, false, Race.human, rogue, null, 3, ClassType2.Rogue),
    hawkeye(9, false, Race.human, rogue, null, 3, ClassType2.Rogue),
    mage(10, true, Race.human, null, null, 1, null),
    wizard(11, true, Race.human, mage, null, 2, null),
    sorcerer(12, true, Race.human, wizard, null, 3, ClassType2.Wizard),
    necromancer(13, true, Race.human, wizard, null, 3, ClassType2.Wizard),
    warlock(14, true, Race.human, wizard, null, 3, ClassType2.Summoner),
    cleric(15, true, Race.human, mage, null, 2, null),
    bishop(16, true, Race.human, cleric, null, 3, ClassType2.Healer),
    prophet(17, true, Race.human, cleric, null, 3, ClassType2.Enchanter),
    elven_fighter(18, false, Race.elf, null, null, 1, null),
    elven_knight(19, false, Race.elf, elven_fighter, null, 2, null),
    temple_knight(20, false, Race.elf, elven_knight, null, 3, ClassType2.Knight),
    swordsinger(21, false, Race.elf, elven_knight, null, 3, ClassType2.Enchanter),
    elven_scout(22, false, Race.elf, elven_fighter, null, 2, null),
    plain_walker(23, false, Race.elf, elven_scout, null, 3, ClassType2.Rogue),
    silver_ranger(24, false, Race.elf, elven_scout, null, 3, ClassType2.Rogue),
    elven_mage(25, true, Race.elf, null, null, 1, null),
    elven_wizard(26, true, Race.elf, elven_mage, null, 2, null),
    spellsinger(27, true, Race.elf, elven_wizard, null, 3, ClassType2.Wizard),
    elemental_summoner(28, true, Race.elf, elven_wizard, null, 3, ClassType2.Summoner),
    oracle(29, true, Race.elf, elven_mage, null, 2, null),
    elder(30, true, Race.elf, oracle, null, 3, ClassType2.Healer),
    dark_fighter(31, false, Race.darkelf, null, null, 1, null),
    palus_knight(32, false, Race.darkelf, dark_fighter, null, 2, null),
    shillien_knight(33, false, Race.darkelf, palus_knight, null, 3, ClassType2.Knight),
    bladedancer(34, false, Race.darkelf, palus_knight, null, 3, ClassType2.Enchanter),
    assasin(35, false, Race.darkelf, dark_fighter, null, 2, null),
    abyss_walker(36, false, Race.darkelf, assasin, null, 3, ClassType2.Rogue),
    phantom_ranger(37, false, Race.darkelf, assasin, null, 3, ClassType2.Rogue),
    dark_mage(38, true, Race.darkelf, null, null, 1, null),
    dark_wizard(39, true, Race.darkelf, dark_mage, null, 2, null),
    spellhowler(40, true, Race.darkelf, dark_wizard, null, 3, ClassType2.Wizard),
    phantom_summoner(41, true, Race.darkelf, dark_wizard, null, 3, ClassType2.Summoner),
    shillien_oracle(42, true, Race.darkelf, dark_mage, null, 2, null),
    shillien_elder(43, true, Race.darkelf, shillien_oracle, null, 3, ClassType2.Healer),
    orc_fighter(44, false, Race.orc, null, null, 1, null),
    orc_raider(45, false, Race.orc, orc_fighter, null, 2, null),
    destroyer(46, false, Race.orc, orc_raider, null, 3, ClassType2.Warrior),
    orc_monk(47, false, Race.orc, orc_fighter, null, 2, null),
    tyrant(48, false, Race.orc, orc_monk, null, 3, ClassType2.Warrior),
    orc_mage(49, true, Race.orc, null, null, 1, null),
    orc_shaman(50, true, Race.orc, orc_mage, null, 2, null),
    overlord(51, true, Race.orc, orc_shaman, null, 3, ClassType2.Enchanter),
    warcryer(52, true, Race.orc, orc_shaman, null, 3, ClassType2.Enchanter),
    dwarven_fighter(53, false, Race.dwarf, null, null, 1, null),
    scavenger(54, false, Race.dwarf, dwarven_fighter, null, 2, null),
    bounty_hunter(55, false, Race.dwarf, scavenger, null, 3, ClassType2.Warrior),
    artisan(56, false, Race.dwarf, dwarven_fighter, null, 2, null),
    warsmith(57, false, Race.dwarf, artisan, null, 3, ClassType2.Warrior),
    dummyEntry1(58, false, null, null, null, 0, null),
    dummyEntry2(59, false, null, null, null, 0, null),
    dummyEntry3(60, false, null, null, null, 0, null),
    dummyEntry4(61, false, null, null, null, 0, null),
    dummyEntry5(62, false, null, null, null, 0, null),
    dummyEntry6(63, false, null, null, null, 0, null),
    dummyEntry7(64, false, null, null, null, 0, null),
    dummyEntry8(65, false, null, null, null, 0, null),
    dummyEntry9(66, false, null, null, null, 0, null),
    dummyEntry10(67, false, null, null, null, 0, null),
    dummyEntry11(68, false, null, null, null, 0, null),
    dummyEntry12(69, false, null, null, null, 0, null),
    dummyEntry13(70, false, null, null, null, 0, null),
    dummyEntry14(71, false, null, null, null, 0, null),
    dummyEntry15(72, false, null, null, null, 0, null),
    dummyEntry16(73, false, null, null, null, 0, null),
    dummyEntry17(74, false, null, null, null, 0, null),
    dummyEntry18(75, false, null, null, null, 0, null),
    dummyEntry19(76, false, null, null, null, 0, null),
    dummyEntry20(77, false, null, null, null, 0, null),
    dummyEntry21(78, false, null, null, null, 0, null),
    dummyEntry22(79, false, null, null, null, 0, null),
    dummyEntry23(80, false, null, null, null, 0, null),
    dummyEntry24(81, false, null, null, null, 0, null),
    dummyEntry25(82, false, null, null, null, 0, null),
    dummyEntry26(83, false, null, null, null, 0, null),
    dummyEntry27(84, false, null, null, null, 0, null),
    dummyEntry28(85, false, null, null, null, 0, null),
    dummyEntry29(86, false, null, null, null, 0, null),
    dummyEntry30(87, false, null, null, null, 0, null),
    duelist(88, false, Race.human, gladiator, null, 4, ClassType2.Warrior),
    dreadnought(89, false, Race.human, warlord, null, 4, ClassType2.Warrior),
    phoenix_knight(90, false, Race.human, paladin, null, 4, ClassType2.Knight),
    hell_knight(91, false, Race.human, dark_avenger, null, 4, ClassType2.Knight),
    sagittarius(92, false, Race.human, hawkeye, null, 4, ClassType2.Rogue),
    adventurer(93, false, Race.human, treasure_hunter, null, 4, ClassType2.Rogue),
    archmage(94, true, Race.human, sorcerer, null, 4, ClassType2.Wizard),
    soultaker(95, true, Race.human, necromancer, null, 4, ClassType2.Wizard),
    arcana_lord(96, true, Race.human, warlock, null, 4, ClassType2.Summoner),
    cardinal(97, true, Race.human, bishop, null, 4, ClassType2.Healer),
    hierophant(98, true, Race.human, prophet, null, 4, ClassType2.Enchanter),
    evas_templar(99, false, Race.elf, temple_knight, null, 4, ClassType2.Knight),
    sword_muse(100, false, Race.elf, swordsinger, null, 4, ClassType2.Enchanter),
    wind_rider(101, false, Race.elf, plain_walker, null, 4, ClassType2.Rogue),
    moonlight_sentinel(102, false, Race.elf, silver_ranger, null, 4, ClassType2.Rogue),
    mystic_muse(103, true, Race.elf, spellsinger, null, 4, ClassType2.Wizard),
    elemental_master(104, true, Race.elf, elemental_summoner, null, 4, ClassType2.Summoner),
    evas_saint(105, true, Race.elf, elder, null, 4, ClassType2.Healer),
    shillien_templar(106, false, Race.darkelf, shillien_knight, null, 4, ClassType2.Knight),
    spectral_dancer(107, false, Race.darkelf, bladedancer, null, 4, ClassType2.Enchanter),
    ghost_hunter(108, false, Race.darkelf, abyss_walker, null, 4, ClassType2.Rogue),
    ghost_sentinel(109, false, Race.darkelf, phantom_ranger, null, 4, ClassType2.Rogue),
    storm_screamer(110, true, Race.darkelf, spellhowler, null, 4, ClassType2.Wizard),
    spectral_master(111, true, Race.darkelf, phantom_summoner, null, 4, ClassType2.Summoner),
    shillien_saint(112, true, Race.darkelf, shillien_elder, null, 4, ClassType2.Healer),
    titan(113, false, Race.orc, destroyer, null, 4, ClassType2.Warrior),
    grand_khavatari(114, false, Race.orc, tyrant, null, 4, ClassType2.Warrior),
    dominator(115, true, Race.orc, overlord, null, 4, ClassType2.Enchanter),
    doomcryer(116, true, Race.orc, warcryer, null, 4, ClassType2.Enchanter),
    fortune_seeker(117, false, Race.dwarf, bounty_hunter, null, 4, ClassType2.Warrior),
    maestro(118, false, Race.dwarf, warsmith, null, 4, ClassType2.Warrior),
    dummyEntry31(119, false, null, null, null, 0, null),
    dummyEntry32(120, false, null, null, null, 0, null),
    dummyEntry33(121, false, null, null, null, 0, null),
    dummyEntry34(122, false, null, null, null, 0, null);

    public static final ClassId[] VALUES;
    private static TIntObjectHashMap<ClassId> Oa;
    private final int _id;
    private final boolean Ob;
    private final Race Oc;
    private final ClassId Od;
    private final ClassId Oe;
    private final ClassType2 Of;
    private final int _level;

    public static ClassId getClassById(int n) {
        return Oa.get(n);
    }

    private ClassId(int n2, boolean bl, Race race, ClassId classId, ClassId classId2, int n3, ClassType2 classType2) {
        this._id = n2;
        this.Ob = bl;
        this.Oc = race;
        this.Od = classId;
        this.Oe = classId2;
        this._level = n3;
        this.Of = classType2;
    }

    public final int getId() {
        return this._id;
    }

    public final boolean isMage() {
        return this.Ob;
    }

    public final Race getRace() {
        return this.Oc;
    }

    public final boolean childOf(ClassId classId) {
        if (this.Od == null) {
            return false;
        }
        if (this.Od == classId || this.Oe == classId) {
            return true;
        }
        return this.Od.childOf(classId);
    }

    public final boolean equalsOrChildOf(ClassId classId) {
        return this == classId || this.childOf(classId);
    }

    public final int level() {
        if (this.Od == null) {
            return 0;
        }
        return 1 + this.Od.level();
    }

    public final ClassId getParent(int n) {
        return n == 0 || this.Oe == null ? this.Od : this.Oe;
    }

    public final int getLevel() {
        return this._level;
    }

    public ClassType2 getType2() {
        return this.Of;
    }

    static {
        VALUES = ClassId.values();
        Oa = new TIntObjectHashMap();
        for (ClassId classId : ClassId.values()) {
            Oa.put(classId.getId(), classId);
        }
    }
}

