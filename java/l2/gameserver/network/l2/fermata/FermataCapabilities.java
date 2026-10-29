/*
 * L2JLucera2 — Fermata protocol capability bits (Revision 1).
 */
package l2.gameserver.network.l2.fermata;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class FermataCapabilities {

    public static final int SUPPORTED_REVISION = 1;
    public static final int MIN_REVISION = 1;
    public static final int MAX_REVISION = 1;

    // --- Bits disjuntos e wire-safe ---
    public static final int BIT_LINKED_ITEM_CHAT     = 1 << 0; // 1
    public static final int BIT_WIDE_ECONOMY          = 1 << 1; // 2
    public static final int BIT_OFFLINE_BUFF_UI        = 1 << 2; // 4
    public static final int BIT_PACKET_TIMING_REPORT   = 1 << 3; // 8
    public static final int BIT_AUGMENT_SCROLL_V2     = 1 << 4; // 16
    public static final int BIT_HEALING_FEEDBACK    = 1 << 5; // 32
    public static final int BIT_MONSTER_DEBUFF_HUD  = 1 << 6; // 64
    public static final int BIT_ARMOR_SET_AURA     = 1 << 7; // 128
    public static final int BIT_BUFF_REMOVE_SELF   = 1 << 8; // 256

    // Aliases semânticos para compatibilidade cruzada
    public static final int BIT_SERVER_WEATHER      = 1 << 9;
    public static final int BIT_MONSTER_DROP_TABLES = 1 << 10;
    public static final int BIT_COORDINATION        = 1 << 11;
    public static final int BIT_ECONOMY_64          = BIT_WIDE_ECONOMY;
    public static final int BIT_COMBAT_METER        = 1 << 12;
    public static final int BIT_REPORT_TRANSFER     = 1 << 13;
    public static final int BIT_FIGHT_LINK_CHAT     = 1 << 14;
    public static final int BIT_PARTY_PINGS         = 1 << 15;
    public static final int BIT_MONSTER_DEBUFFS     = BIT_MONSTER_DEBUFF_HUD;
    public static final int BIT_ARMOR_SET_AURAS     = BIT_ARMOR_SET_AURA;
    public static final int BIT_LINKED_CHAT         = BIT_LINKED_ITEM_CHAT;
    public static final int BIT_ATLAS               = BIT_MONSTER_DROP_TABLES;
    public static final int BIT_EQUIPMENT_PREVIEW   = 1 << 10;

    public static final int MASK_NONE                 = 0;
    public static final int EMPTY_MASK                = 0;
    public static final int MASK_RESERVED_HIGH        = ~((1 << 16) - 1);

    private FermataCapabilities() {
    }

    public static boolean has(int mask, int bit) {
        return (mask & bit) == bit;
    }

    public static int negotiate(int clientMask, int serverMask) {
        int client = clientMask & ~MASK_RESERVED_HIGH;
        int server = serverMask & ~MASK_RESERVED_HIGH;
        return client & server;
    }

    public static int allKnownBits() {
        return BIT_LINKED_ITEM_CHAT
                | BIT_WIDE_ECONOMY
                | BIT_OFFLINE_BUFF_UI
                | BIT_PACKET_TIMING_REPORT
                | BIT_AUGMENT_SCROLL_V2
                | BIT_HEALING_FEEDBACK
                | BIT_MONSTER_DEBUFF_HUD
                | BIT_ARMOR_SET_AURA
                | BIT_BUFF_REMOVE_SELF;
    }

    public static int allKnown() {
        return allKnownBits();
    }

    public static Set<String> describe(int mask) {
        Set<String> names = new LinkedHashSet<>(16);
        if (has(mask, BIT_LINKED_ITEM_CHAT)) names.add("LINKED_ITEM_CHAT");
        if (has(mask, BIT_WIDE_ECONOMY)) names.add("WIDE_ECONOMY");
        if (has(mask, BIT_OFFLINE_BUFF_UI)) names.add("OFFLINE_BUFF_UI");
        if (has(mask, BIT_PACKET_TIMING_REPORT)) names.add("PACKET_TIMING_REPORT");
        if (has(mask, BIT_AUGMENT_SCROLL_V2)) names.add("AUGMENT_SCROLL_V2");
        if (has(mask, BIT_HEALING_FEEDBACK)) names.add("HEALING_FEEDBACK");
        if (has(mask, BIT_MONSTER_DEBUFF_HUD)) names.add("MONSTER_DEBUFF_HUD");
        if (has(mask, BIT_ARMOR_SET_AURA)) names.add("ARMOR_SET_AURA");
        if (has(mask, BIT_BUFF_REMOVE_SELF)) names.add("BUFF_REMOVE_SELF");
        return Collections.unmodifiableSet(names);
    }

    public static boolean isClientMaskValid(int clientMask) {
        if (clientMask < 0) {
            return false;
        }
        return (clientMask & MASK_RESERVED_HIGH) == 0;
    }

    public static int firstReservedSubOpcode() {
        return 0x0013;
    }
}
