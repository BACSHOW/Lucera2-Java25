/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml;

import l2.commons.threading.ParallelBoot;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.BuyListHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.data.xml.parser.ArmorSetsParser;
import l2.gameserver.data.xml.parser.CapsuleItemParser;
import l2.gameserver.data.xml.parser.CharacterTemplateParser;
import l2.gameserver.data.xml.parser.ClassLevelGainParser;
import l2.gameserver.data.xml.parser.CubicParser;
import l2.gameserver.data.xml.parser.DomainParser;
import l2.gameserver.data.xml.parser.DoorParser;
import l2.gameserver.data.xml.parser.EnchantItemParser;
import l2.gameserver.data.xml.parser.EnchantSkillParser;
import l2.gameserver.data.xml.parser.EventParser;
import l2.gameserver.data.xml.parser.FishDataParser;
import l2.gameserver.data.xml.parser.HennaParser;
import l2.gameserver.data.xml.parser.HitCondBonusParser;
import l2.gameserver.data.xml.parser.InstantZoneParser;
import l2.gameserver.data.xml.parser.ItemParser;
import l2.gameserver.data.xml.parser.KarmaIncreaseDataParser;
import l2.gameserver.data.xml.parser.NpcParser;
import l2.gameserver.data.xml.parser.OptionDataParser;
import l2.gameserver.data.xml.parser.PromoCodeParser;
import l2.gameserver.data.xml.parser.RecipeParser;
import l2.gameserver.data.xml.parser.ResidenceParser;
import l2.gameserver.data.xml.parser.RestartPointParser;
import l2.gameserver.data.xml.parser.SkillAcquireParser;
import l2.gameserver.data.xml.parser.SoulCrystalParser;
import l2.gameserver.data.xml.parser.SpawnParser;
import l2.gameserver.data.xml.parser.StaticObjectParser;
import l2.gameserver.data.xml.parser.VariationChanceParser;
import l2.gameserver.data.xml.parser.VariationGroupParser;
import l2.gameserver.data.xml.parser.ZoneParser;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.tables.SkillTable;

public abstract class Parsers {
    public static void parseAll() {
        // 1. Dados básicos primários carregados de forma sequencial para garantir integridade estrutural
        StringHolder.getInstance().load();
        EnchantSkillParser.getInstance().load();
        SkillTable.getInstance().load();
        ItemParser.getInstance().load();
        CharacterTemplateParser.getInstance().load();
        NpcParser.getInstance().load();
        DomainParser.getInstance().load();
        RestartPointParser.getInstance().load();
        StaticObjectParser.getInstance().load();
        DoorParser.getInstance().load();
        ZoneParser.getInstance().load();
        SpawnParser.getInstance().load();
        InstantZoneParser.getInstance().load();
        ResidenceParser.getInstance().load();
        ReflectionManager.getInstance();

        // 2. Parsers secundários independentes carregados em paralelo via Coroutines
        ParallelBoot.runBatch("Parsers-Secondary",
            ParallelBoot.step("HtmCache", () -> HtmCache.getInstance().reload()),
            ParallelBoot.step("OptionDataParser", () -> OptionDataParser.getInstance().load()),
            ParallelBoot.step("VariationGroupParser", () -> VariationGroupParser.getInstance().load()),
            ParallelBoot.step("VariationChanceParser", () -> VariationChanceParser.getInstance().load()),
            ParallelBoot.step("SkillAcquireParser", () -> SkillAcquireParser.getInstance().load()),
            ParallelBoot.step("HitCondBonusParser", () -> HitCondBonusParser.getInstance().load()),
            ParallelBoot.step("KarmaIncreaseDataParser", () -> KarmaIncreaseDataParser.getInstance().load()),
            ParallelBoot.step("ClassLevelGainParser", () -> ClassLevelGainParser.getInstance().load()),
            ParallelBoot.step("EventParser", () -> EventParser.getInstance().load()),
            ParallelBoot.step("CubicParser", () -> CubicParser.getInstance().load()),
            ParallelBoot.step("RecipeParser", () -> RecipeParser.getInstance().load()),
            ParallelBoot.step("BuyListHolder", () -> BuyListHolder.getInstance()),
            ParallelBoot.step("MultiSellHolder", () -> MultiSellHolder.getInstance()),
            ParallelBoot.step("HennaParser", () -> HennaParser.getInstance().load()),
            ParallelBoot.step("EnchantItemParser", () -> EnchantItemParser.getInstance().load()),
            ParallelBoot.step("SoulCrystalParser", () -> SoulCrystalParser.getInstance().load()),
            ParallelBoot.step("ArmorSetsParser", () -> ArmorSetsParser.getInstance().load()),
            ParallelBoot.step("FishDataParser", () -> FishDataParser.getInstance().load()),
            ParallelBoot.step("CapsuleItemParser", () -> CapsuleItemParser.getInstance().load())
        );

        if (Config.SERVICE_PROMOCODE_ENABLED) {
            PromoCodeParser.getInstance().load();
        }
    }
}
