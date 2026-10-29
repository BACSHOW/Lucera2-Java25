/*
 * Decompiled with CFR 0.152.
 */
package services.villagemasters;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.VillageMasterInstance;
import l2.gameserver.scripts.Functions;

public class Occupation
extends Functions {
    public void onTalk30026() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.fighter ? "bitz003h.htm" : (classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue ? "bitz004.htm" : (classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasure_hunter ? "bitz005.htm" : (classId == ClassId.gladiator || classId == ClassId.dark_avenger || classId == ClassId.hawkeye ? "bitz005.htm" : "bitz002.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30026/" + string, new Object[0]);
    }

    public void onTalk30031() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.wizard || classId == ClassId.cleric ? "06.htm" : (classId == ClassId.sorcerer || classId == ClassId.necromancer || classId == ClassId.warlock || classId == ClassId.bishop || classId == ClassId.prophet ? "07.htm" : (classId == ClassId.mage ? "01.htm" : "08.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30031/" + string, new Object[0]);
    }

    public void onTalk30037() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.elven_mage ? "01.htm" : (classId == ClassId.mage ? "08.htm" : (classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elven_wizard || classId == ClassId.oracle ? "31.htm" : (classId == ClassId.sorcerer || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet ? "32.htm" : (classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elemental_summoner ? "32.htm" : "33.htm"))));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30037/" + string, new Object[0]);
    }

    public void onChange30037(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 1201;
        int n2 = 1230;
        int n3 = 1235;
        int n4 = 1292;
        int n5 = Integer.parseInt(stringArray[0]);
        int n6 = player.getLevel();
        String string = "33.htm";
        if (n5 == 26 && player.getClassId() == ClassId.elven_mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n2) == null) {
                string = "15.htm";
            } else if (n6 <= 19 && player.getInventory().getItemByItemId(n2) != null) {
                string = "16.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n2) == null) {
                string = "17.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n2) != null) {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.setClassId(n5, false, true);
                string = "18.htm";
            }
        } else if (n5 == 29 && player.getClassId() == ClassId.elven_mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n3) == null) {
                string = "19.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n3) != null) {
                string = "20.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n3) == null) {
                string = "21.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n3) != null) {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n5, false, true);
                string = "22.htm";
            }
        } else if (n5 == 11 && player.getClassId() == ClassId.mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n4) == null) {
                string = "23.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n4) != null) {
                string = "24.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n4) == null) {
                string = "25.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n4) != null) {
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.setClassId(n5, false, true);
                string = "26.htm";
            }
        } else if (n5 == 15 && player.getClassId() == ClassId.mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n) == null) {
                string = "27.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n) != null) {
                string = "28.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n) == null) {
                string = "29.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n) != null) {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.setClassId(n5, false, true);
                string = "30.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30037/" + string, new Object[0]);
    }

    public void onTalk30066() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.elven_fighter ? "01.htm" : (classId == ClassId.fighter ? "08.htm" : (classId == ClassId.elven_knight || classId == ClassId.elven_scout || classId == ClassId.warrior || classId == ClassId.knight || classId == ClassId.rogue ? "38.htm" : (classId == ClassId.temple_knight || classId == ClassId.plain_walker || classId == ClassId.swordsinger || classId == ClassId.silver_ranger ? "39.htm" : (classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasure_hunter ? "39.htm" : (classId == ClassId.gladiator || classId == ClassId.dark_avenger || classId == ClassId.hawkeye ? "39.htm" : "40.htm")))));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30066/" + string, new Object[0]);
    }

    public void onChange30066(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 1145;
        int n2 = 1161;
        int n3 = 1190;
        int n4 = 1204;
        int n5 = 1217;
        int n6 = Integer.parseInt(stringArray[0]);
        int n7 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n6 == 19 && classId == ClassId.elven_fighter) {
            if (n7 <= 19 && player.getInventory().getItemByItemId(n4) == null) {
                string = "18.htm";
            }
            if (n7 <= 19 && player.getInventory().getItemByItemId(n4) != null) {
                string = "19.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n4) == null) {
                string = "20.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n4) != null) {
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.setClassId(n6, false, true);
                string = "21.htm";
            }
        }
        if (n6 == 22 && classId == ClassId.elven_fighter) {
            if (n7 <= 19 && player.getInventory().getItemByItemId(n5) == null) {
                string = "22.htm";
            }
            if (n7 <= 19 && player.getInventory().getItemByItemId(n5) != null) {
                string = "23.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n5) == null) {
                string = "24.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n5) != null) {
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.setClassId(n6, false, true);
                string = "25.htm";
            }
        }
        if (n6 == 1 && classId == ClassId.fighter) {
            if (n7 <= 19 && player.getInventory().getItemByItemId(n) == null) {
                string = "26.htm";
            }
            if (n7 <= 19 && player.getInventory().getItemByItemId(n) != null) {
                string = "27.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n) == null) {
                string = "28.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n) != null) {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.setClassId(n6, false, true);
                string = "29.htm";
            }
        }
        if (n6 == 4 && classId == ClassId.fighter) {
            if (n7 <= 19 && player.getInventory().getItemByItemId(n2) == null) {
                string = "30.htm";
            }
            if (n7 <= 19 && player.getInventory().getItemByItemId(n2) != null) {
                string = "31.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n2) == null) {
                string = "32.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n2) != null) {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.setClassId(n6, false, true);
                string = "33.htm";
            }
        }
        if (n6 == 7 && classId == ClassId.fighter) {
            if (n7 <= 19 && player.getInventory().getItemByItemId(n3) == null) {
                string = "34.htm";
            }
            if (n7 <= 19 && player.getInventory().getItemByItemId(n3) != null) {
                string = "35.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n3) == null) {
                string = "36.htm";
            }
            if (n7 >= 20 && player.getInventory().getItemByItemId(n3) != null) {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n6, false, true);
                string = "37.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30066/" + string, new Object[0]);
    }

    public void onTalk30511() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.scavenger ? "01.htm" : (classId == ClassId.dwarven_fighter ? "09.htm" : (classId == ClassId.bounty_hunter || classId == ClassId.warsmith ? "10.htm" : "11.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30511/" + string, new Object[0]);
    }

    public void onChange30511(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 2809;
        int n2 = 3119;
        int n3 = 3238;
        int n4 = Integer.parseInt(stringArray[0]);
        int n5 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n4 == 55 && classId == ClassId.scavenger) {
            if (n5 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null ? "05.htm" : "06.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null) {
                string = "07.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n4, false, true);
                string = "08.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30511/" + string, new Object[0]);
    }

    public void onTalk30070() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.elven_mage ? "01.htm" : (classId == ClassId.wizard || classId == ClassId.cleric || classId == ClassId.elven_wizard || classId == ClassId.oracle ? "31.htm" : (classId == ClassId.sorcerer || classId == ClassId.necromancer || classId == ClassId.bishop || classId == ClassId.warlock || classId == ClassId.prophet || classId == ClassId.spellsinger || classId == ClassId.elder || classId == ClassId.elemental_summoner ? "32.htm" : (classId == ClassId.mage ? "08.htm" : "33.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30070/" + string, new Object[0]);
    }

    public void onChange30070(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 1201;
        int n2 = 1230;
        int n3 = 1235;
        int n4 = 1292;
        int n5 = Integer.parseInt(stringArray[0]);
        int n6 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n5 == 26 && classId == ClassId.elven_mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n2) == null) {
                string = "15.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n2) != null) {
                string = "16.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n2) == null) {
                string = "17.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n2) != null) {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.setClassId(n5, false, true);
                string = "18.htm";
            }
        } else if (n5 == 29 && classId == ClassId.elven_mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n3) == null) {
                string = "19.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n3) != null) {
                string = "20.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n3) == null) {
                string = "21.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n3) != null) {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n5, false, true);
                string = "22.htm";
            }
        } else if (n5 == 11 && classId == ClassId.mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n4) == null) {
                string = "23.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n4) != null) {
                string = "24.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n4) == null) {
                string = "25.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n4) != null) {
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.setClassId(n5, false, true);
                string = "26.htm";
            }
        } else if (n5 == 15 && classId == ClassId.mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n) == null) {
                string = "27.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n) != null) {
                string = "28.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n) == null) {
                string = "29.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n) != null) {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.setClassId(n5, false, true);
                string = "30.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30070/" + string, new Object[0]);
    }

    public void onTalk30154() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.elven_fighter ? "01.htm" : (classId == ClassId.elven_mage ? "02.htm" : (classId == ClassId.elven_wizard || classId == ClassId.oracle || classId == ClassId.elven_knight || classId == ClassId.elven_scout ? "12.htm" : (player.getRace() == Race.elf ? "13.htm" : "11.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30154/" + string, new Object[0]);
    }

    public void onTalk30358() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.dark_fighter ? "01.htm" : (classId == ClassId.dark_mage ? "02.htm" : (classId == ClassId.dark_wizard || classId == ClassId.shillien_oracle || classId == ClassId.palus_knight || classId == ClassId.assasin ? "12.htm" : (player.getRace() == Race.darkelf ? "13.htm" : "11.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30358/" + string, new Object[0]);
    }

    public void onTalk30498() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.dwarven_fighter ? "01.htm" : (classId == ClassId.scavenger || classId == ClassId.artisan ? "09.htm" : (player.getRace() == Race.dwarf ? "10.htm" : "11.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30498/" + string, new Object[0]);
    }

    public void onChange30498(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 1642;
        int n2 = Integer.parseInt(stringArray[0]);
        int n3 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n2 == 54 && classId == ClassId.dwarven_fighter) {
            if (n3 <= 19 && player.getInventory().getItemByItemId(n) == null) {
                string = "05.htm";
            }
            if (n3 <= 19 && player.getInventory().getItemByItemId(n) != null) {
                string = "06.htm";
            }
            if (n3 >= 20 && player.getInventory().getItemByItemId(n) == null) {
                string = "07.htm";
            }
            if (n3 >= 20 && player.getInventory().getItemByItemId(n) != null) {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.setClassId(n2, false, true);
                string = "08.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30498/" + string, new Object[0]);
    }

    public void onTalk30499() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.dwarven_fighter ? "01.htm" : (classId == ClassId.scavenger || classId == ClassId.artisan ? "09.htm" : (player.getRace() == Race.dwarf ? "10.htm" : "11.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30499/" + string, new Object[0]);
    }

    public void onChange30499(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 1635;
        int n2 = Integer.parseInt(stringArray[0]);
        int n3 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n2 == 56 && classId == ClassId.dwarven_fighter) {
            if (n3 <= 19 && player.getInventory().getItemByItemId(n) == null) {
                string = "05.htm";
            }
            if (n3 <= 19 && player.getInventory().getItemByItemId(n) != null) {
                string = "06.htm";
            }
            if (n3 >= 20 && player.getInventory().getItemByItemId(n) == null) {
                string = "07.htm";
            }
            if (n3 >= 20 && player.getInventory().getItemByItemId(n) != null) {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.setClassId(n2, false, true);
                string = "08.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30499/" + string, new Object[0]);
    }

    public void onTalk30525() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.dwarven_fighter ? "01.htm" : (classId == ClassId.artisan ? "05.htm" : (classId == ClassId.warsmith ? "06.htm" : "07.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30525/" + string, new Object[0]);
    }

    public void onTalk30520() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.dwarven_fighter ? "01.htm" : (classId == ClassId.artisan || classId == ClassId.scavenger ? "05.htm" : (classId == ClassId.warsmith || classId == ClassId.bounty_hunter ? "06.htm" : "07.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30520/" + string, new Object[0]);
    }

    public void onTalk30512() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.artisan ? "01.htm" : (classId == ClassId.dwarven_fighter ? "09.htm" : (classId == ClassId.warsmith || classId == ClassId.bounty_hunter ? "10.htm" : "11.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30512/" + string, new Object[0]);
    }

    public void onChange30512(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 2867;
        int n2 = 3119;
        int n3 = 3238;
        int n4 = Integer.parseInt(stringArray[0]);
        int n5 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n4 == 57 && classId == ClassId.artisan) {
            if (n5 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null ? "05.htm" : "06.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null) {
                string = "07.htm";
            } else {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n4, false, true);
                string = "08.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30512/" + string, new Object[0]);
    }

    public void onTalk30565() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.orc_fighter ? "01.htm" : (classId == ClassId.orc_raider || classId == ClassId.orc_monk || classId == ClassId.orc_shaman ? "09.htm" : (classId == ClassId.orc_mage ? "16.htm" : (player.getRace() == Race.orc ? "10.htm" : "11.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30565/" + string, new Object[0]);
    }

    public void onTalk30109() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.elven_knight ? "01.htm" : (classId == ClassId.knight ? "08.htm" : (classId == ClassId.rogue ? "15.htm" : (classId == ClassId.elven_scout ? "22.htm" : (classId == ClassId.warrior ? "29.htm" : (classId == ClassId.elven_fighter || classId == ClassId.fighter ? "76.htm" : (classId == ClassId.temple_knight || classId == ClassId.plain_walker || classId == ClassId.swordsinger || classId == ClassId.silver_ranger ? "77.htm" : (classId == ClassId.warlord || classId == ClassId.paladin || classId == ClassId.treasure_hunter ? "77.htm" : (classId == ClassId.gladiator || classId == ClassId.dark_avenger || classId == ClassId.hawkeye ? "77.htm" : "78.htm"))))))));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30109/" + string, new Object[0]);
    }

    public void onChange30109(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 2627;
        int n2 = 2633;
        int n3 = 2673;
        int n4 = 2734;
        int n5 = 2762;
        int n6 = 2809;
        int n7 = 2820;
        int n8 = 3140;
        int n9 = 3276;
        int n10 = 3293;
        int n11 = 3307;
        int n12 = Integer.parseInt(stringArray[0]);
        int n13 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n12 == 20 && classId == ClassId.elven_knight) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n7) == null ? "36.htm" : "37.htm";
            } else if (player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n7) == null) {
                string = "38.htm";
            } else {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n8, 1L);
                player.getInventory().destroyItemByItemId(n7, 1L);
                player.setClassId(n12, false, true);
                string = "39.htm";
            }
        } else if (n12 == 21 && classId == ClassId.elven_knight) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n5) == null ? "40.htm" : "41.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n5) == null) {
                string = "42.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n8, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.setClassId(n12, false, true);
                string = "43.htm";
            }
        } else if (n12 == 5 && classId == ClassId.knight) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n7) == null ? "44.htm" : "45.htm";
            } else if (player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n7) == null) {
                string = "46.htm";
            } else {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n7, 1L);
                player.setClassId(n12, false, true);
                string = "47.htm";
            }
        } else if (n12 == 6 && classId == ClassId.knight) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n11) == null ? "48.htm" : "49.htm";
            } else if (player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n11) == null) {
                string = "50.htm";
            } else {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n11, 1L);
                player.setClassId(n12, false, true);
                string = "51.htm";
            }
        } else if (n12 == 8 && classId == ClassId.rogue) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n6) == null ? "52.htm" : "53.htm";
            } else if (player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n6) == null) {
                string = "54.htm";
            } else {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n6, 1L);
                player.setClassId(n12, false, true);
                string = "55.htm";
            }
        } else if (n12 == 9 && classId == ClassId.rogue) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n10) == null ? "56.htm" : "57.htm";
            } else if (player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n10) == null) {
                string = "58.htm";
            } else {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.setClassId(n12, false, true);
                string = "59.htm";
            }
        } else if (n12 == 23 && classId == ClassId.elven_scout) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n6) == null ? "60.htm" : "61.htm";
            } else if (player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n6) == null) {
                string = "62.htm";
            } else {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.getInventory().destroyItemByItemId(n8, 1L);
                player.getInventory().destroyItemByItemId(n6, 1L);
                player.setClassId(n12, false, true);
                string = "63.htm";
            }
        } else if (n12 == 24 && classId == ClassId.elven_scout) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n10) == null ? "64.htm" : "65.htm";
            } else if (player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n8) == null || player.getInventory().getItemByItemId(n10) == null) {
                string = "66.htm";
            } else {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.getInventory().destroyItemByItemId(n8, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.setClassId(n12, false, true);
                string = "67.htm";
            }
        } else if (n12 == 2 && classId == ClassId.warrior) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n5) == null ? "68.htm" : "69.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n5) == null) {
                string = "70.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.setClassId(n12, false, true);
                string = "71.htm";
            }
        } else if (n12 == 3 && classId == ClassId.warrior) {
            if (n13 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n9) == null ? "72.htm" : "73.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n9) == null) {
                string = "74.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n9, 1L);
                player.setClassId(n12, false, true);
                string = "75.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30109/" + string, new Object[0]);
    }

    public void onTalk30115() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.elven_wizard ? "01.htm" : (classId == ClassId.wizard ? "08.htm" : (classId == ClassId.sorcerer || classId == ClassId.necromancer || classId == ClassId.warlock ? "39.htm" : (classId == ClassId.spellsinger || classId == ClassId.elemental_summoner ? "39.htm" : ((player.getRace() == Race.elf || player.getRace() == Race.human) && classId.isMage() ? "38.htm" : "40.htm"))));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30115/" + string, new Object[0]);
    }

    public void onChange30115(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 2674;
        int n2 = 2734;
        int n3 = 2840;
        int n4 = 3140;
        int n5 = 3307;
        int n6 = 3336;
        int n7 = Integer.parseInt(stringArray[0]);
        int n8 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n7 == 27 && classId == ClassId.elven_wizard) {
            if (n8 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n3) == null ? "18.htm" : "19.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n3) == null) {
                string = "20.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n7, false, true);
                string = "21.htm";
            }
        } else if (n7 == 28 && classId == ClassId.elven_wizard) {
            if (n8 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n6) == null ? "22.htm" : "23.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n6) == null) {
                string = "24.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n6, 1L);
                player.setClassId(n7, false, true);
                string = "25.htm";
            }
        } else if (n7 == 12 && classId == ClassId.wizard) {
            if (n8 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null ? "26.htm" : "27.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null) {
                string = "28.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n7, false, true);
                string = "29.htm";
            }
        } else if (n7 == 13 && classId == ClassId.wizard) {
            if (n8 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n5) == null ? "30.htm" : "31.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n5) == null) {
                string = "32.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.setClassId(n7, false, true);
                string = "33.htm";
            }
        } else if (n7 == 14 && classId == ClassId.wizard) {
            if (n8 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n6) == null ? "34.htm" : "35.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n6) == null) {
                string = "36.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n6, 1L);
                player.setClassId(n7, false, true);
                string = "37.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30115/" + string, new Object[0]);
    }

    public void onTalk30120() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.oracle ? "01.htm" : (classId == ClassId.cleric ? "05.htm" : (classId == ClassId.elder || classId == ClassId.bishop || classId == ClassId.prophet ? "25.htm" : ((player.getRace() == Race.human || player.getRace() == Race.elf) && classId.isMage() ? "24.htm" : "26.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30120/" + string, new Object[0]);
    }

    public void onChange30120(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 2721;
        int n2 = 2734;
        int n3 = 2820;
        int n4 = 2821;
        int n5 = 3140;
        int n6 = Integer.parseInt(stringArray[0]);
        int n7 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n6 == 30 || classId == ClassId.oracle) {
            if (n7 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n3) == null ? "12.htm" : "13.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n3) == null) {
                string = "14.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n6, false, true);
                string = "15.htm";
            }
        } else if (n6 == 16 && classId == ClassId.cleric) {
            if (n7 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null ? "16.htm" : "17.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n3) == null) {
                string = "18.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n6, false, true);
                string = "19.htm";
            }
        } else if (n6 == 17 && classId == ClassId.cleric) {
            if (n7 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n4) == null ? "20.htm" : "21.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n4) == null) {
                string = "22.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.setClassId(n6, false, true);
                string = "23.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30120/" + string, new Object[0]);
    }

    public void onTalk30500() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.orc_fighter ? "01.htm" : (classId == ClassId.orc_mage ? "06.htm" : (classId == ClassId.orc_raider || classId == ClassId.orc_monk || classId == ClassId.orc_shaman ? "21.htm" : (classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer ? "22.htm" : "23.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30500/" + string, new Object[0]);
    }

    public void onChange30500(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 1592;
        int n2 = 1615;
        int n3 = 1631;
        int n4 = Integer.parseInt(stringArray[0]);
        int n5 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n4 == 45 && classId == ClassId.orc_fighter) {
            if (n5 <= 19 && player.getInventory().getItemByItemId(n) == null) {
                string = "09.htm";
            }
            if (n5 <= 19 && player.getInventory().getItemByItemId(n) != null) {
                string = "10.htm";
            }
            if (n5 >= 20 && player.getInventory().getItemByItemId(n) == null) {
                string = "11.htm";
            }
            if (n5 >= 20 && player.getInventory().getItemByItemId(n) != null) {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.setClassId(n4, false, true);
                string = "12.htm";
            }
        } else if (n4 == 47 && classId == ClassId.orc_fighter) {
            if (n5 <= 19 && player.getInventory().getItemByItemId(n2) == null) {
                string = "13.htm";
            }
            if (n5 <= 19 && player.getInventory().getItemByItemId(n2) != null) {
                string = "14.htm";
            }
            if (n5 >= 20 && player.getInventory().getItemByItemId(n2) == null) {
                string = "15.htm";
            }
            if (n5 >= 20 && player.getInventory().getItemByItemId(n2) != null) {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.setClassId(n4, false, true);
                string = "16.htm";
            }
        } else if (n4 == 50 && classId == ClassId.orc_mage) {
            if (n5 <= 19 && player.getInventory().getItemByItemId(n3) == null) {
                string = "17.htm";
            }
            if (n5 <= 19 && player.getInventory().getItemByItemId(n3) != null) {
                string = "18.htm";
            }
            if (n5 >= 20 && player.getInventory().getItemByItemId(n3) == null) {
                string = "19.htm";
            }
            if (n5 >= 20 && player.getInventory().getItemByItemId(n3) != null) {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n4, false, true);
                string = "20.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30500/" + string, new Object[0]);
    }

    public void onTalk30290() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.dark_fighter ? "01.htm" : (classId == ClassId.dark_mage ? "08.htm" : (classId == ClassId.palus_knight || classId == ClassId.assasin || classId == ClassId.dark_wizard || classId == ClassId.shillien_oracle ? "31.htm" : (player.getRace() == Race.darkelf ? "32.htm" : "33.htm")));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30290/" + string, new Object[0]);
    }

    public void onChange30290(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 1244;
        int n2 = 1252;
        int n3 = 1261;
        int n4 = 1270;
        int n5 = Integer.parseInt(stringArray[0]);
        int n6 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n5 == 32 && classId == ClassId.dark_fighter) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n) == null) {
                string = "15.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n) != null) {
                string = "16.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n) == null) {
                string = "17.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n) != null) {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.setClassId(n5, false, true);
                string = "18.htm";
            }
        } else if (n5 == 35 && classId == ClassId.dark_fighter) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n2) == null) {
                string = "19.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n2) != null) {
                string = "20.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n2) == null) {
                string = "21.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n2) != null) {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.setClassId(n5, false, true);
                string = "22.htm";
            }
        } else if (n5 == 39 && classId == ClassId.dark_mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n3) == null) {
                string = "23.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n3) != null) {
                string = "24.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n3) == null) {
                string = "25.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n3) != null) {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n5, false, true);
                string = "26.htm";
            }
        } else if (n5 == 42 && classId == ClassId.dark_mage) {
            if (n6 <= 19 && player.getInventory().getItemByItemId(n4) == null) {
                string = "27.htm";
            }
            if (n6 <= 19 && player.getInventory().getItemByItemId(n4) != null) {
                string = "28.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n4) == null) {
                string = "29.htm";
            }
            if (n6 >= 20 && player.getInventory().getItemByItemId(n4) != null) {
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.setClassId(n5, false, true);
                string = "30.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30290/" + string, new Object[0]);
    }

    public void onTalk30513() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = classId == ClassId.orc_monk ? "01.htm" : (classId == ClassId.orc_raider ? "05.htm" : (classId == ClassId.orc_shaman ? "09.htm" : (classId == ClassId.destroyer || classId == ClassId.tyrant || classId == ClassId.overlord || classId == ClassId.warcryer ? "32.htm" : (classId == ClassId.orc_fighter || classId == ClassId.orc_mage ? "33.htm" : "34.htm"))));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30513/" + string, new Object[0]);
    }

    public void onChange30513(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 2627;
        int n2 = 2721;
        int n3 = 2762;
        int n4 = 2879;
        int n5 = 3203;
        int n6 = 3276;
        int n7 = 3390;
        int n8 = Integer.parseInt(stringArray[0]);
        int n9 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n8 == 48 && classId == ClassId.orc_monk) {
            if (n9 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n3) == null ? "16.htm" : "17.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n3) == null) {
                string = "18.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.setClassId(n8, false, true);
                string = "19.htm";
            }
        } else if (n8 == 46 && classId == ClassId.orc_raider) {
            if (n9 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n6) == null ? "20.htm" : "21.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n6) == null) {
                string = "22.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.getInventory().destroyItemByItemId(n6, 1L);
                player.setClassId(n8, false, true);
                string = "23.htm";
            }
        } else if (n8 == 51 && classId == ClassId.orc_shaman) {
            if (n9 <= 39) {
                string = player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n7) == null ? "24.htm" : "25.htm";
            } else if (player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n7) == null) {
                string = "26.htm";
            } else {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.getInventory().destroyItemByItemId(n7, 1L);
                player.setClassId(n8, false, true);
                string = "27.htm";
            }
        } else if (n8 == 52 && classId == ClassId.orc_shaman) {
            if (n9 <= 39) {
                string = player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n4) == null ? "28.htm" : "29.htm";
            } else if (player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n4) == null) {
                string = "30.htm";
            } else {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.setClassId(n8, false, true);
                string = "31.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30513/" + string, new Object[0]);
    }

    public void onTalk30474() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        String string = npcInstance.getNpcId() == 30175 ? (classId == ClassId.shillien_oracle ? "08.htm" : (classId == ClassId.dark_wizard ? "19.htm" : (classId == ClassId.spellhowler || classId == ClassId.shillien_elder || classId == ClassId.phantom_summoner ? "54.htm" : (classId == ClassId.dark_mage ? "55.htm" : "56.htm")))) : (classId == ClassId.palus_knight ? "01.htm" : (classId == ClassId.shillien_oracle ? "08.htm" : (classId == ClassId.assasin ? "12.htm" : (classId == ClassId.dark_wizard ? "19.htm" : (classId == ClassId.shillien_knight || classId == ClassId.abyss_walker || classId == ClassId.bladedancer || classId == ClassId.phantom_ranger ? "54.htm" : (classId == ClassId.spellhowler || classId == ClassId.shillien_elder || classId == ClassId.phantom_summoner ? "54.htm" : (classId == ClassId.dark_fighter || classId == ClassId.dark_mage ? "55.htm" : "56.htm")))))));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30474/" + string, new Object[0]);
    }

    public void onChange30474(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 2627;
        int n2 = 2633;
        int n3 = 2673;
        int n4 = 2674;
        int n5 = 2721;
        int n6 = 2762;
        int n7 = 2809;
        int n8 = 2821;
        int n9 = 2840;
        int n10 = 3172;
        int n11 = 3293;
        int n12 = 3307;
        int n13 = 3336;
        int n14 = Integer.parseInt(stringArray[0]);
        int n15 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "No Quest";
        if (n14 == 33 && classId == ClassId.palus_knight) {
            if (n15 <= 39) {
                string = player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n12) == null ? "26.htm" : "27.htm";
            } else if (player.getInventory().getItemByItemId(n2) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n12) == null) {
                string = "28.htm";
            } else {
                player.getInventory().destroyItemByItemId(n2, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.getInventory().destroyItemByItemId(n12, 1L);
                player.setClassId(n14, false, true);
                string = "29.htm";
            }
        } else if (n14 == 34 && classId == ClassId.palus_knight) {
            if (n15 <= 39) {
                string = player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n6) == null ? "30.htm" : "31.htm";
            } else if (player.getInventory().getItemByItemId(n) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n6) == null) {
                string = "32.htm";
            } else {
                player.getInventory().destroyItemByItemId(n, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.getInventory().destroyItemByItemId(n6, 1L);
                player.setClassId(n14, false, true);
                string = "33.htm";
            }
        } else if (n14 == 43 && classId == ClassId.shillien_oracle) {
            if (n15 <= 39) {
                string = player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n8) == null ? "34.htm" : "35.htm";
            } else if (player.getInventory().getItemByItemId(n5) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n8) == null) {
                string = "36.htm";
            } else {
                player.getInventory().destroyItemByItemId(n5, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.getInventory().destroyItemByItemId(n8, 1L);
                player.setClassId(n14, false, true);
                string = "37.htm";
            }
        } else if (n14 == 36 && classId == ClassId.assasin) {
            if (n15 <= 39) {
                string = player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n7) == null ? "38.htm" : "39.htm";
            } else if (player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n7) == null) {
                string = "40.htm";
            } else {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.getInventory().destroyItemByItemId(n7, 1L);
                player.setClassId(n14, false, true);
                string = "41.htm";
            }
        } else if (n14 == 37 && classId == ClassId.assasin) {
            if (n15 <= 39) {
                string = player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n11) == null ? "42.htm" : "43.htm";
            } else if (player.getInventory().getItemByItemId(n3) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n11) == null) {
                string = "44.htm";
            } else {
                player.getInventory().destroyItemByItemId(n3, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.getInventory().destroyItemByItemId(n11, 1L);
                player.setClassId(n14, false, true);
                string = "45.htm";
            }
        } else if (n14 == 40 && classId == ClassId.dark_wizard) {
            if (n15 <= 39) {
                string = player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n9) == null ? "46.htm" : "47.htm";
            } else if (player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n9) == null) {
                string = "48.htm";
            } else {
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.getInventory().destroyItemByItemId(n9, 1L);
                player.setClassId(n14, false, true);
                string = "49.htm";
            }
        } else if (n14 == 41 && classId == ClassId.dark_wizard) {
            if (n15 <= 39) {
                string = player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n13) == null ? "50.htm" : "51.htm";
            } else if (player.getInventory().getItemByItemId(n4) == null || player.getInventory().getItemByItemId(n10) == null || player.getInventory().getItemByItemId(n13) == null) {
                string = "52.htm";
            } else {
                player.getInventory().destroyItemByItemId(n4, 1L);
                player.getInventory().destroyItemByItemId(n10, 1L);
                player.getInventory().destroyItemByItemId(n13, 1L);
                player.setClassId(n14, false, true);
                string = "53.htm";
            }
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/30474/" + string, new Object[0]);
    }

    public void onChange32145(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 9772;
        int n2 = Integer.parseInt(stringArray[0]);
        int n3 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "04.htm";
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/32145/" + string, new Object[0]);
    }

    public void onTalk32145() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/32145/02.htm", new Object[0]);
    }

    public void onChange32146(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 9753;
        int n2 = Integer.parseInt(stringArray[0]);
        int n3 = player.getLevel();
        ClassId classId = player.getClassId();
        String string = "04.htm";
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/32146/" + string, new Object[0]);
    }

    public void onTalk32146() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        npcInstance.showChatWindow(player, "villagemaster/32146/02.htm", new Object[0]);
    }

    public void onTalk32199() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        npcInstance.showChatWindow(player, "villagemaster/32199/02.htm", new Object[0]);
    }

    public void onTalk32157() {
        String string = "head_blacksmith_mokabred";
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        Race race = player.getRace();
        String string2 = race != Race.dwarf ? "002.htm" : (classId == ClassId.dwarven_fighter ? "003f.htm" : (classId.getLevel() == 3 ? "004.htm" : "005.htm"));
        npcInstance.showChatWindow(player, "villagemaster/32157/" + string + string2, new Object[0]);
    }

    public void onTalk32160() {
        String string = "grandmagister_devon";
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        Race race = player.getRace();
        String string2 = race != Race.darkelf ? "002.htm" : (classId == ClassId.dark_fighter ? "003f.htm" : (classId == ClassId.dark_mage ? "003m.htm" : (classId.getLevel() == 3 ? "004.htm" : "005.htm")));
        npcInstance.showChatWindow(player, "villagemaster/32160/" + string + string2, new Object[0]);
    }

    public void onChange32199(String[] stringArray) {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        int n = 9782;
        int n2 = 9806;
        int n3 = 9760;
        int n4 = Integer.parseInt(stringArray[0]);
        int n5 = player.getLevel();
        npcInstance.showChatWindow(player, "villagemaster/32199/02.htm", new Object[0]);
    }

    public void onTalk32158() {
        String string = "warehouse_chief_fisser";
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        Race race = player.getRace();
        String string2 = race != Race.dwarf ? "002.htm" : (classId == ClassId.dwarven_fighter ? "003f.htm" : (classId.getLevel() == 3 ? "004.htm" : "005.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/32158/" + string + string2, new Object[0]);
    }

    public void onTalk32171() {
        String string = "warehouse_chief_hufran";
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!(npcInstance instanceof VillageMasterInstance)) {
            Occupation.show("I have nothing to say you", player, npcInstance, new Object[0]);
            return;
        }
        ClassId classId = player.getClassId();
        Race race = player.getRace();
        String string2 = race != Race.dwarf ? "002.htm" : (classId == ClassId.dwarven_fighter ? "003f.htm" : (classId.getLevel() == 3 ? "004.htm" : "005.htm"));
        ((VillageMasterInstance)npcInstance).showChatWindow(player, "villagemaster/32171/" + string + string2, new Object[0]);
    }

    public void onTalk32213() {
        this.onTalk32199();
    }

    public void onChange32213(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32214() {
        this.onTalk32199();
    }

    public void onChange32214(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32217() {
        this.onTalk32199();
    }

    public void onChange32217(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32218() {
        this.onTalk32199();
    }

    public void onChange32218(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32221() {
        this.onTalk32199();
    }

    public void onChange32221(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32222() {
        this.onTalk32199();
    }

    public void onChange32222(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32205() {
        this.onTalk32199();
    }

    public void onChange32205(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32206() {
        this.onTalk32199();
    }

    public void onChange32206(String[] stringArray) {
        this.onChange32199(stringArray);
    }

    public void onTalk32147() {
        this.onTalk30037();
    }

    public void onTalk32150() {
        this.onTalk30565();
    }

    public void onTalk32153() {
        this.onTalk30037();
    }

    public void onTalk32154() {
        this.onTalk30066();
    }

    public void onTalk32226() {
        this.onTalk32199();
    }

    public void onTalk32225() {
        this.onTalk32199();
    }

    public void onTalk32230() {
        this.onTalk32199();
    }

    public void onTalk32229() {
        this.onTalk32199();
    }

    public void onTalk32233() {
        this.onTalk32199();
    }

    public void onTalk32234() {
        this.onTalk32199();
    }

    public void onTalk32202() {
        this.onTalk32199();
    }

    public void onTalk32210() {
        this.onTalk32199();
    }

    public void onTalk32209() {
        this.onTalk32199();
    }
}

