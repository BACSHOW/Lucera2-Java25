/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.commons.collections.MultiValueSet;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.taskmanager.SpawnTaskManager;
import l2.gameserver.templates.npc.NpcTemplate;
import org.apache.commons.lang3.StringUtils;

public class AdminSpawn
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditNPC) {
            return false;
        }
        switch (commands) {
            case admin_show_spawns: {
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/spawns.htm"));
                break;
            }
            case admin_show_npcs: {
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/npcs.htm"));
                break;
            }
            case admin_spawn_index: {
                StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                try {
                    String string2 = string.substring(18);
                    player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/spawns/" + string2 + ".htm"));
                    stringTokenizer.nextToken();
                    String string3 = stringTokenizer.nextToken();
                    int n = 0;
                    try {
                        n = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    catch (NoSuchElementException noSuchElementException) {
                        // empty catch block
                    }
                    this.c(player, string3, n);
                }
                catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
                break;
            }
            case admin_spawn1: {
                StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                try {
                    stringTokenizer.nextToken();
                    String string4 = stringTokenizer.nextToken();
                    int n = 1;
                    if (stringTokenizer.hasMoreTokens()) {
                        n = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    this.a(player, string4, 0, n);
                }
                catch (Exception exception) {}
                break;
            }
            case admin_spawn: 
            case admin_spawn_monster: {
                StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                try {
                    stringTokenizer.nextToken();
                    String string5 = stringTokenizer.nextToken();
                    int n = 30;
                    int n2 = 1;
                    if (stringTokenizer.hasMoreTokens()) {
                        n2 = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        n = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    this.a(player, string5, n, n2);
                }
                catch (Exception exception) {}
                break;
            }
            case admin_spawn_all: {
                ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        SpawnManager.getInstance().spawnAll();
                    }
                });
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/spawns.htm"));
                break;
            }
            case admin_spawn_day: {
                ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        SpawnManager.getInstance().spawnDay();
                    }
                });
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/spawns.htm"));
                break;
            }
            case admin_spawn_night: {
                ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        SpawnManager.getInstance().spawnNight();
                    }
                });
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/spawns.htm"));
                break;
            }
            case admin_despawn_all: {
                ThreadPoolManager.getInstance().execute(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        SpawnManager.getInstance().despawnAll();
                    }
                });
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/spawns.htm"));
                player.sendMessage("All NPCs have been removed from the world!.");
                break;
            }
            case admin_setai: {
                if (player.getTarget() == null || !player.getTarget().isNpc()) {
                    player.sendMessage("Please select target NPC or mob.");
                    return false;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                stringTokenizer.nextToken();
                if (!stringTokenizer.hasMoreTokens()) {
                    player.sendMessage("Please specify AI name.");
                    return false;
                }
                String string6 = stringTokenizer.nextToken();
                NpcInstance npcInstance = (NpcInstance)player.getTarget();
                Constructor<?> constructor = null;
                try {
                    if (!string6.equalsIgnoreCase("npc")) {
                        constructor = Class.forName("l2.gameserver.ai." + string6).getConstructors()[0];
                    }
                }
                catch (Exception exception) {
                    try {
                        constructor = Scripts.getInstance().getClasses().get("ai." + string6).getConstructors()[0];
                    }
                    catch (Exception exception2) {
                        player.sendMessage("This type AI not found.");
                        return false;
                    }
                }
                if (constructor == null) break;
                try {
                    npcInstance.setAI((CharacterAI)constructor.newInstance(npcInstance));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                npcInstance.getAI().startAITask();
                break;
            }
            case admin_setaiparam: {
                if (player.getTarget() == null || !player.getTarget().isNpc()) {
                    player.sendMessage("Please select target NPC or mob.");
                    return false;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                stringTokenizer.nextToken();
                if (!stringTokenizer.hasMoreTokens()) {
                    player.sendMessage("Please specify AI parameter name.");
                    player.sendMessage("USAGE: //setaiparam <param> <value>");
                    return false;
                }
                String string7 = stringTokenizer.nextToken();
                if (!stringTokenizer.hasMoreTokens()) {
                    player.sendMessage("Please specify AI parameter value.");
                    player.sendMessage("USAGE: //setaiparam <param> <value>");
                    return false;
                }
                String string8 = stringTokenizer.nextToken();
                NpcInstance npcInstance = (NpcInstance)player.getTarget();
                npcInstance.setParameter(string7, string8);
                npcInstance.decayMe();
                npcInstance.spawnMe();
                player.sendMessage("AI parameter " + string7 + " succesfully setted to " + string8);
                break;
            }
            case admin_dumpparams: {
                if (player.getTarget() == null || !player.getTarget().isNpc()) {
                    player.sendMessage("Please select target NPC or mob.");
                    return false;
                }
                NpcInstance npcInstance = (NpcInstance)player.getTarget();
                MultiValueSet<String> multiValueSet = npcInstance.getParameters();
                if (!multiValueSet.isEmpty()) {
                    System.out.println("Dump of Parameters:\r\n" + multiValueSet.toString());
                    break;
                }
                System.out.println("Parameters is empty.");
                break;
            }
            case admin_setheading: {
                GameObject gameObject = player.getTarget();
                if (!gameObject.isNpc()) {
                    player.sendMessage("Target is incorrect!");
                    return false;
                }
                NpcInstance npcInstance = (NpcInstance)gameObject;
                npcInstance.setHeading(player.getHeading());
                npcInstance.decayMe();
                npcInstance.spawnMe();
                player.sendMessage("New heading : " + player.getHeading());
                Spawner spawner = npcInstance.getSpawn();
                if (spawner != null) break;
                player.sendMessage("Spawn for this npc == null!");
                return false;
            }
            case admin_generate_loc: {
                if (stringArray.length < 2) {
                    player.sendMessage("Incorrect argument count!");
                    return false;
                }
                int n = Integer.parseInt(stringArray[1]);
                int n3 = 0;
                if (stringArray.length > 2) {
                    n3 = Integer.parseInt(stringArray[2]);
                }
                int n4 = Integer.MIN_VALUE;
                int n5 = Integer.MIN_VALUE;
                int n6 = Integer.MIN_VALUE;
                int n7 = Integer.MAX_VALUE;
                int n8 = Integer.MAX_VALUE;
                int n9 = Integer.MAX_VALUE;
                String string9 = "";
                for (NpcInstance npcInstance : World.getAroundNpc(player)) {
                    if (npcInstance.getNpcId() != n && npcInstance.getNpcId() != n3) continue;
                    string9 = npcInstance.getName();
                    n4 = Math.min(n4, npcInstance.getX());
                    n5 = Math.min(n5, npcInstance.getY());
                    n6 = Math.min(n6, npcInstance.getZ());
                    n7 = Math.max(n7, npcInstance.getX());
                    n8 = Math.max(n8, npcInstance.getY());
                    n9 = Math.max(n9, npcInstance.getZ());
                }
                System.out.println("(0,'" + string9 + "'," + (n4 -= 500) + "," + (n5 -= 500) + "," + n6 + "," + n9 + ",0),");
                System.out.println("(0,'" + string9 + "'," + n4 + "," + (n8 += 500) + "," + n6 + "," + n9 + ",0),");
                System.out.println("(0,'" + string9 + "'," + (n7 += 500) + "," + n8 + "," + n6 + "," + n9 + ",0),");
                System.out.println("(0,'" + string9 + "'," + n7 + "," + n5 + "," + n6 + "," + n9 + ",0),");
                System.out.println("delete from spawnlist where npc_templateid in (" + n + ", " + n3 + ") and locx <= " + n4 + " and locy <= " + n5 + " and locz <= " + n6 + " and locx >= " + n7 + " and locy >= " + n8 + " and locz >= " + n9 + ";");
                break;
            }
            case admin_dumpspawntasks: {
                System.out.println(SpawnTaskManager.getInstance().toString());
                break;
            }
            case admin_dumpspawn: {
                StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                try {
                    stringTokenizer.nextToken();
                    String string10 = stringTokenizer.nextToken();
                    int n = 30;
                    int n10 = 1;
                    this.a(player, string10, n, n10);
                    try {
                        new File("dumps").mkdir();
                        File file = new File("dumps/spawndump.txt");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileWriter fileWriter = new FileWriter(file, true);
                        fileWriter.write("<spawn count=\"1\" respawn=\"60\" respawn_random=\"0\" period_of_day=\"none\">\n\t<point x=\"" + player.getLoc().x + "\" y=\"" + player.getLoc().y + "\" z=\"" + player.getLoc().z + "\" h=\"" + player.getLoc().h + "\" />\n\t<npc id=\"" + Integer.parseInt(string10) + "\" /><!--" + NpcHolder.getInstance().getTemplate(Integer.parseInt(string10)).getName() + "-->\n</spawn>\n");
                        fileWriter.close();
                    }
                    catch (Exception exception) {}
                    break;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void a(Player player, String string, int n, int n2) {
        NpcTemplate npcTemplate;
        Pattern pattern;
        Matcher matcher;
        GameObject gameObject = player.getTarget();
        if (gameObject == null) {
            gameObject = player;
        }
        if ((matcher = (pattern = Pattern.compile("[0-9]*")).matcher(string)).matches()) {
            int n3 = Integer.parseInt(string);
            npcTemplate = NpcHolder.getInstance().getTemplate(n3);
        } else {
            string = string.replace('_', ' ');
            npcTemplate = NpcHolder.getInstance().getTemplateByName(string);
        }
        if (npcTemplate == null) {
            player.sendMessage("Incorrect monster template.");
            return;
        }
        try {
            SimpleSpawner simpleSpawner = new SimpleSpawner(npcTemplate);
            simpleSpawner.setLoc(gameObject.getLoc());
            simpleSpawner.setAmount(n2);
            simpleSpawner.setHeading(player.getHeading());
            simpleSpawner.setRespawnDelay(n);
            simpleSpawner.setReflection(player.getReflection());
            if (RaidBossSpawnManager.getInstance().isDefined(npcTemplate.getNpcId())) {
                player.sendMessage("Raid Boss " + npcTemplate.name + " already spawned.");
            } else {
                simpleSpawner.init();
                if (n == 0) {
                    simpleSpawner.stopRespawn();
                }
                player.sendMessage("Created " + npcTemplate.name + " on " + gameObject.getObjectId() + ".");
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            player.sendMessage("Target is not ingame.");
        }
    }

    public List<NpcTemplate> getAllNpcStartingWith(String string) {
        ArrayList<NpcTemplate> arrayList = new ArrayList<NpcTemplate>();
        for (NpcTemplate npcTemplate : NpcHolder.getInstance().getAll()) {
            if (npcTemplate == null || StringUtils.isBlank(npcTemplate.getName()) || !npcTemplate.getName().toLowerCase().startsWith(string.toLowerCase())) continue;
            arrayList.add(npcTemplate);
        }
        return arrayList;
    }

    private void c(Player player, String string, int n) {
        List<NpcTemplate> list = this.getAllNpcStartingWith(string);
        int n2 = list.size();
        StringBuilder stringBuilder = new StringBuilder(500 + n2 * 80);
        stringBuilder.append("<html><title>Spawn Monster:</title><body><p> There are " + n2 + " Npcs whose name starts with " + string + ":<br>");
        int n3 = n;
        for (int i = 0; n3 < n2 && i < 50; ++n3, ++i) {
            stringBuilder.append("<a action=\"bypass -h admin_spawn_monster " + list.get(n3).getNpcId() + "\">" + list.get(n3).getName() + " " + list.get(n3).getNpcId() + "</a><br1>");
        }
        if (n3 == n2) {
            stringBuilder.append("<br><center><button value=\"Back\" action=\"bypass -h admin_show_npcs\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center></body></html>");
        } else {
            stringBuilder.append("<br><center><button value=\"Next\" action=\"bypass -h admin_spawn_index " + string + " " + n3 + "\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"><button value=\"Back\" action=\"bypass -h admin_show_npcs\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center></body></html>");
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, 5, stringBuilder.toString(), 0));
    }

    private static enum Commands {
        admin_show_spawns,
        admin_show_npcs,
        admin_spawn,
        admin_despawn_all,
        admin_spawn_day,
        admin_spawn_all,
        admin_spawn_night,
        admin_spawn_monster,
        admin_spawn_index,
        admin_spawn1,
        admin_setheading,
        admin_setai,
        admin_setaiparam,
        admin_dumpparams,
        admin_generate_loc,
        admin_dumpspawntasks,
        admin_dumpspawn;

    }
}

