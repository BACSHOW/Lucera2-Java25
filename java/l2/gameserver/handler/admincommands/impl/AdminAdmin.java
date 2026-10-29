/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.EventTrigger;
import l2.gameserver.network.l2.s2c.ExChangeClientEffectInfo;
import l2.gameserver.network.l2.s2c.ExSendUIEvent;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.scripts.Functions;
import l2.gameserver.stats.Stats;
import l2.gameserver.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class AdminAdmin
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (player.getPlayerAccess().Menu) {
            switch (commands) {
                case admin_admin: {
                    player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/admin.htm"));
                    break;
                }
                case admin_play_sounds: {
                    if (stringArray.length == 1) {
                        player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/songs/songs.htm"));
                        break;
                    }
                    try {
                        player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/songs/songs" + stringArray[1] + ".htm"));
                    }
                    catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
                    break;
                }
                case admin_play_sound: {
                    try {
                        this.playAdminSound(player, stringArray[1]);
                    }
                    catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
                    break;
                }
                case admin_silence: {
                    if (player.getMessageRefusal()) {
                        player.unsetVar("gm_silence");
                        player.setMessageRefusal(false);
                        player.sendPacket((IStaticPacket)Msg.MESSAGE_ACCEPTANCE_MODE);
                        player.sendEtcStatusUpdate();
                        break;
                    }
                    if (Config.SAVE_GM_EFFECTS) {
                        player.setVar("gm_silence", "true", -1L);
                    }
                    player.setMessageRefusal(true);
                    player.sendPacket((IStaticPacket)Msg.MESSAGE_REFUSAL_MODE);
                    player.sendEtcStatusUpdate();
                    break;
                }
                case admin_tradeoff: {
                    try {
                        if (stringArray[1].equalsIgnoreCase("on")) {
                            player.setTradeRefusal(true);
                            Functions.sendDebugMessage(player, "tradeoff enabled");
                            break;
                        }
                        if (!stringArray[1].equalsIgnoreCase("off")) break;
                        player.setTradeRefusal(false);
                        Functions.sendDebugMessage(player, "tradeoff disabled");
                    }
                    catch (Exception exception) {
                        if (player.getTradeRefusal()) {
                            Functions.sendDebugMessage(player, "tradeoff currently enabled");
                            break;
                        }
                        Functions.sendDebugMessage(player, "tradeoff currently disabled");
                    }
                    break;
                }
                case admin_show_html: {
                    String string2 = stringArray[1];
                    try {
                        if (string2 != null) {
                            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/" + string2));
                            break;
                        }
                        Functions.sendDebugMessage(player, "Html page not found");
                    }
                    catch (Exception exception) {
                        Functions.sendDebugMessage(player, "Html page not found");
                    }
                    break;
                }
                case admin_setnpcstate: {
                    int n;
                    if (stringArray.length < 2) {
                        Functions.sendDebugMessage(player, "USAGE: //setnpcstate state");
                        return false;
                    }
                    GameObject gameObject = player.getTarget();
                    try {
                        n = Integer.parseInt(stringArray[1]);
                    }
                    catch (NumberFormatException numberFormatException) {
                        Functions.sendDebugMessage(player, "You must specify state");
                        return false;
                    }
                    if (!gameObject.isNpc()) {
                        Functions.sendDebugMessage(player, "You must target an NPC");
                        return false;
                    }
                    NpcInstance npcInstance = (NpcInstance)gameObject;
                    npcInstance.setNpcState(n);
                    break;
                }
                case admin_setareanpcstate: {
                    try {
                        String string3 = string.substring(15).trim();
                        String[] stringArray2 = string3.split(" ");
                        int n = NumberUtils.toInt(stringArray2[0], 0);
                        int n2 = stringArray2.length > 1 ? NumberUtils.toInt(stringArray2[1], 0) : 0;
                        for (NpcInstance npcInstance : player.getAroundNpc(n, 200)) {
                            npcInstance.setNpcState(n2);
                        }
                        break;
                    }
                    catch (Exception exception) {
                        Functions.sendDebugMessage(player, "Usage: //setareanpcstate [range] [state]");
                        break;
                    }
                }
                case admin_showmovie: {
                    int n;
                    if (stringArray.length < 2) {
                        Functions.sendDebugMessage(player, "USAGE: //showmovie id");
                        return false;
                    }
                    try {
                        n = Integer.parseInt(stringArray[1]);
                    }
                    catch (NumberFormatException numberFormatException) {
                        Functions.sendDebugMessage(player, "You must specify id");
                        return false;
                    }
                    player.showQuestMovie(n);
                    break;
                }
                case admin_setzoneinfo: {
                    int n;
                    if (stringArray.length < 2) {
                        Functions.sendDebugMessage(player, "USAGE: //setzoneinfo id");
                        return false;
                    }
                    try {
                        n = Integer.parseInt(stringArray[1]);
                    }
                    catch (NumberFormatException numberFormatException) {
                        Functions.sendDebugMessage(player, "You must specify id");
                        return false;
                    }
                    player.broadcastPacket(new ExChangeClientEffectInfo(n));
                    break;
                }
                case admin_eventtrigger: {
                    int n;
                    if (stringArray.length < 2) {
                        Functions.sendDebugMessage(player, "USAGE: //eventtrigger id");
                        return false;
                    }
                    try {
                        n = Integer.parseInt(stringArray[1]);
                    }
                    catch (NumberFormatException numberFormatException) {
                        Functions.sendDebugMessage(player, "You must specify id");
                        return false;
                    }
                    player.broadcastPacket(new EventTrigger(n, true));
                    break;
                }
                case admin_debug: {
                    GameObject gameObject = player.getTarget();
                    if (!gameObject.isPlayer()) {
                        Functions.sendDebugMessage(player, "Only player target is allowed");
                        return false;
                    }
                    Player player2 = gameObject.getPlayer();
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add("==========TARGET STATS:");
                    arrayList.add("==Magic Resist: " + player2.calcStat(Stats.MAGIC_RESIST, null, null));
                    arrayList.add("==Magic Power: " + player2.calcStat(Stats.MAGIC_POWER, 1.0, null, null));
                    arrayList.add("==Skill Power: " + player2.calcStat(Stats.SKILL_POWER, 1.0, null, null));
                    arrayList.add("==Cast Break Rate: " + player2.calcStat(Stats.CAST_INTERRUPT, 1.0, null, null));
                    arrayList.add("==========Powers:");
                    arrayList.add("==Bleed: " + player2.calcStat(Stats.BLEED_POWER, 1.0, null, null));
                    arrayList.add("==Poison: " + player2.calcStat(Stats.POISON_POWER, 1.0, null, null));
                    arrayList.add("==Stun: " + player2.calcStat(Stats.STUN_POWER, 1.0, null, null));
                    arrayList.add("==Root: " + player2.calcStat(Stats.ROOT_POWER, 1.0, null, null));
                    arrayList.add("==Mental: " + player2.calcStat(Stats.MENTAL_POWER, 1.0, null, null));
                    arrayList.add("==Sleep: " + player2.calcStat(Stats.SLEEP_POWER, 1.0, null, null));
                    arrayList.add("==Paralyze: " + player2.calcStat(Stats.PARALYZE_POWER, 1.0, null, null));
                    arrayList.add("==Cancel: " + player2.calcStat(Stats.CANCEL_POWER, 1.0, null, null));
                    arrayList.add("==Debuff: " + player2.calcStat(Stats.DEBUFF_POWER, 1.0, null, null));
                    arrayList.add("==========PvP Stats:");
                    arrayList.add("==Phys Attack Dmg: " + player2.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1.0, null, null));
                    arrayList.add("==Phys Skill Dmg: " + player2.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1.0, null, null));
                    arrayList.add("==Magic Skill Dmg: " + player2.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1.0, null, null));
                    arrayList.add("==Phys Attack Def: " + player2.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1.0, null, null));
                    arrayList.add("==Phys Skill Def: " + player2.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1.0, null, null));
                    arrayList.add("==Magic Skill Def: " + player2.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1.0, null, null));
                    arrayList.add("==========Reflects:");
                    arrayList.add("==Phys Dmg Chance: " + player2.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, null, null));
                    arrayList.add("==Phys Skill Dmg Chance: " + player2.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, null, null));
                    arrayList.add("==Magic Skill Dmg Chance: " + player2.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, null, null));
                    arrayList.add("==Counterattack: Phys Dmg Chance: " + player2.calcStat(Stats.REFLECT_DAMAGE_PERCENT, null, null));
                    arrayList.add("==Counterattack: Phys Skill Dmg Chance: " + player2.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, null, null));
                    arrayList.add("==Counterattack: Magic Skill Dmg Chance: " + player2.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, null, null));
                    arrayList.add("==========MP Consume Rate:");
                    arrayList.add("==Magic Skills: " + player2.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, 1.0, null, null));
                    arrayList.add("==Phys Skills: " + player2.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, 1.0, null, null));
                    arrayList.add("==Music: " + player2.calcStat(Stats.MP_DANCE_SKILL_CONSUME, 1.0, null, null));
                    arrayList.add("==========Shield:");
                    arrayList.add("==Shield Defence: " + player2.calcStat(Stats.SHIELD_DEFENCE, null, null));
                    arrayList.add("==Shield Defence Rate: " + player2.calcStat(Stats.SHIELD_RATE, null, null));
                    arrayList.add("==Shield Defence Angle: " + player2.calcStat(Stats.SHIELD_ANGLE, null, null));
                    arrayList.add("==========Etc:");
                    arrayList.add("==Fatal Blow Rate: " + player2.calcStat(Stats.FATALBLOW_RATE, null, null));
                    arrayList.add("==Phys Skill Evasion Rate: " + player2.calcStat(Stats.PSKILL_EVASION, null, null));
                    arrayList.add("==Counterattack Rate: " + player2.calcStat(Stats.COUNTER_ATTACK, null, null));
                    arrayList.add("==Pole Attack Angle: " + player2.calcStat(Stats.POLE_ATTACK_ANGLE, null, null));
                    arrayList.add("==Pole Target Count: " + player2.calcStat(Stats.POLE_TARGET_COUNT, 1.0, null, null));
                    arrayList.add("==========DONE.");
                    for (String string4 : arrayList) {
                        Functions.sendDebugMessage(player, string4);
                    }
                    break;
                }
                case admin_uievent: {
                    String string5;
                    int n;
                    int n3;
                    boolean bl;
                    boolean bl2;
                    if (stringArray.length < 5) {
                        Functions.sendDebugMessage(player, "USAGE: //uievent isHide doIncrease startTime endTime Text");
                        return false;
                    }
                    try {
                        bl2 = Boolean.parseBoolean(stringArray[1]);
                        bl = Boolean.parseBoolean(stringArray[2]);
                        n3 = Integer.parseInt(stringArray[3]);
                        n = Integer.parseInt(stringArray[4]);
                        string5 = stringArray[5];
                    }
                    catch (NumberFormatException numberFormatException) {
                        Functions.sendDebugMessage(player, "Invalid format");
                        return false;
                    }
                    player.broadcastPacket(new ExSendUIEvent(player, bl2, bl, n3, n, string5));
                    break;
                }
                case admin_forcenpcinfo: {
                    GameObject gameObject = player.getTarget();
                    if (!gameObject.isNpc()) {
                        Functions.sendDebugMessage(player, "Only NPC target is allowed");
                        return false;
                    }
                    ((NpcInstance)gameObject).broadcastCharInfo();
                    break;
                }
                case admin_loc: {
                    Functions.sendDebugMessage(player, "Coords: X:" + player.getLoc().x + " Y:" + player.getLoc().y + " Z:" + player.getLoc().z + " H:" + player.getLoc().h);
                    System.out.println("Coords: X:" + player.getLoc().x + " Y:" + player.getLoc().y + " Z:" + player.getLoc().z + " H:" + player.getLoc().h);
                    break;
                }
                case admin_spawn_loc: {
                    if (stringArray.length > 2) {
                        try {
                            int n = Math.max(16, Integer.parseInt(stringArray[1])) / 2;
                            int n4 = player.getLoc().getX() - n;
                            int n5 = player.getLoc().getY() - n;
                            int n6 = player.getLoc().getX() + n;
                            int n7 = player.getLoc().getY() + n;
                            int n8 = player.getLoc().getZ();
                            int n9 = player.getLoc().getZ() + 128;
                            Functions.sendDebugMessage(player, "Spawn location Dumped. Check Server Log");
                            System.out.printf("<spawn name=\"[custom_spawn]\">\n\t<mesh>\n\t\t<vertex x=\"%d\" y=\"%d\" minz=\"%d\" maxz=\"%d\" />\n\t\t<vertex x=\"%d\" y=\"%d\" minz=\"%d\" maxz=\"%d\" />\n\t\t<vertex x=\"%d\" y=\"%d\" minz=\"%d\" maxz=\"%d\" />\n\t\t<vertex x=\"%d\" y=\"%d\" minz=\"%d\" maxz=\"%s\" />\n\t</mesh>\n\t<npc id=\"%s\" count=\"1\" respawn=\"60\" />\n</spawn>\n", n4, n5, n8, n9, n6, n5, n8, n9, n6, n7, n8, n9, n4, n7, n8, n9, stringArray[2]);
                        }
                        catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        break;
                    }
                    player.sendMessage("usage: //spawn_loc <side_len> <npc_id>");
                    break;
                }
                case admin_spawn_pos: {
                    if (stringArray.length > 1) {
                        Functions.sendDebugMessage(player, "Spawn position Dumped. Check Server Log");
                        System.out.printf("<spawn name=\"[custom_spawn]\">\n\t<npc id=\"%s\" count=\"1\" respawn=\"60\" pos=\"%s\" />\n</spawn>\n", stringArray[1], player.getLoc().toXYZHString());
                        break;
                    }
                    player.sendMessage("usage: //spawn_pos <npc_id>");
                    break;
                }
                case admin_locdump: {
                    StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                    try {
                        stringTokenizer.nextToken();
                        try {
                            new File("dumps").mkdir();
                            File file = new File("dumps/locdump.txt");
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            Functions.sendDebugMessage(player, "Coords: X:" + player.getLoc().x + " Y:" + player.getLoc().y + " Z:" + player.getLoc().z + " H:" + player.getLoc().h);
                            FileWriter fileWriter = new FileWriter(file, true);
                            fileWriter.write("Loc: " + player.getLoc().x + ", " + player.getLoc().y + ", " + player.getLoc().z + "\n");
                            fileWriter.close();
                        }
                        catch (Exception exception) {
                        }
                    }
                    catch (Exception exception) {}
                    break;
                }
                case admin_undying: {
                    if (player.isUndying()) {
                        player.setUndying(false);
                        Functions.sendDebugMessage(player, "Undying state has been disabled.");
                        break;
                    }
                    player.setUndying(true);
                    Functions.sendDebugMessage(player, "Undying state has been enabled.");
                    break;
                }
                case admin_online: {
                    int[] nArray = new int[]{0, 0, 0, 0, 0};
                    HashSet<String> hashSet = new HashSet<String>();
                    int n = 0;
                    for (Player player3 : GameObjectsStorage.getAllPlayersForIterate()) {
                        if (player3 == null || !player3.isOnline() || player3.getNetConnection() == null) continue;
                        if (StringUtils.isNotBlank(player3.getNetConnection().getHwid())) {
                            hashSet.add(player3.getNetConnection().getHwid());
                        }
                        if (player3.isGM()) {
                            ++n;
                        }
                        switch (player3.getRace()) {
                            case human: {
                                nArray[0] = nArray[0] + 1;
                                break;
                            }
                            case darkelf: {
                                nArray[1] = nArray[1] + 1;
                                break;
                            }
                            case dwarf: {
                                nArray[2] = nArray[2] + 1;
                                break;
                            }
                            case elf: {
                                nArray[3] = nArray[3] + 1;
                                break;
                            }
                            case orc: {
                                nArray[4] = nArray[4] + 1;
                            }
                        }
                    }
                    Object object = HtmCache.getInstance().getNotNull("admin/online.htm", player);
                    int n10 = ServerVariables.getInt("maxTotalOnline", 0);
                    object = ((String)object).replace("%online%", Util.formatAdena(GameObjectsStorage.getAllPlayersCount() - GameObjectsStorage.getAllOfflineCount()));
                    object = ((String)object).replace("%dirty_online%", Util.formatAdena(GameObjectsStorage.getAllPlayersCount()));
                    object = ((String)object).replace("%offline_online%", Util.formatAdena(GameObjectsStorage.getAllOfflineCount()));
                    object = ((String)object).replace("%day_online%", Util.formatAdena(GameServer.getInstance().getCurrentMaxOnline()));
                    object = ((String)object).replace("%all_online%", Util.formatAdena(Math.max(n10, GameServer.getInstance().getCurrentMaxOnline())));
                    object = ((String)object).replace("%hwids%", Util.formatAdena(hashSet.size()));
                    object = ((String)object).replace("%gms%", Util.formatAdena(n));
                    object = ((String)object).replace("%race_1%", Util.formatAdena(nArray[0]));
                    object = ((String)object).replace("%race_2%", Util.formatAdena(nArray[1]));
                    object = ((String)object).replace("%race_3%", Util.formatAdena(nArray[2]));
                    object = ((String)object).replace("%race_4%", Util.formatAdena(nArray[3]));
                    object = ((String)object).replace("%race_5%", Util.formatAdena(nArray[4]));
                    Functions.show((String)object, player, null, new Object[0]);
                }
            }
            return true;
        }
        if (player.getPlayerAccess().CanTeleport) {
            switch (commands) {
                case admin_show_html: {
                    String string6 = stringArray[1];
                    try {
                        if (string6 != null) {
                            if (string6.startsWith("tele")) {
                                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/" + string6));
                                break;
                            }
                            player.sendMessage("Access denied");
                            break;
                        }
                        player.sendMessage("Html page not found");
                        break;
                    }
                    catch (Exception exception) {
                        player.sendMessage("Html page not found");
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    public void playAdminSound(Player player, String string) {
        player.sendPacket((IStaticPacket)new PlaySound(PlaySound.Type.MUSIC, string, 0, 0, 0, 0, 0));
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/admin.htm"));
        player.sendMessage("Playing " + string + ".");
    }

    private static enum Commands {
        admin_admin,
        admin_play_sounds,
        admin_play_sound,
        admin_silence,
        admin_tradeoff,
        admin_cfg,
        admin_online,
        admin_config,
        admin_show_html,
        admin_setnpcstate,
        admin_setareanpcstate,
        admin_showmovie,
        admin_setzoneinfo,
        admin_eventtrigger,
        admin_debug,
        admin_uievent,
        admin_forcenpcinfo,
        admin_loc,
        admin_spawn_loc,
        admin_spawn_pos,
        admin_locdump,
        admin_undying;

    }
}

