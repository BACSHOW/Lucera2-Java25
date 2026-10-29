/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.lang.reflect.Field;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.WorldRegion;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;

public class AdminServer
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Menu) {
            return false;
        }
        switch (commands) {
            case admin_server: {
                try {
                    String string2 = string.substring(13);
                    AdminServer.showHelpPage(player, string2);
                }
                catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
                break;
            }
            case admin_check_actor: {
                GameObject gameObject = player.getTarget();
                if (gameObject == null) {
                    player.sendMessage("target == null");
                    return false;
                }
                if (!gameObject.isCreature()) {
                    player.sendMessage("target is not a character");
                    return false;
                }
                Creature creature = (Creature)gameObject;
                CharacterAI characterAI = creature.getAI();
                if (characterAI == null) {
                    player.sendMessage("ai == null");
                    return false;
                }
                Creature creature2 = characterAI.getActor();
                if (creature2 == null) {
                    player.sendMessage("actor == null");
                    return false;
                }
                player.sendMessage("actor: " + creature2);
                break;
            }
            case admin_setvar: {
                if (stringArray.length != 3) {
                    player.sendMessage("Incorrect argument count!!!");
                    return false;
                }
                ServerVariables.set(stringArray[1], stringArray[2]);
                player.sendMessage("Value changed.");
                break;
            }
            case admin_set_ai_interval: {
                if (stringArray.length != 2) {
                    player.sendMessage("Incorrect argument count!!!");
                    return false;
                }
                int n = Integer.parseInt(stringArray[1]);
                int n2 = 0;
                int n3 = 0;
                for (NpcInstance npcInstance : GameObjectsStorage.getAllNpcsForIterate()) {
                    CharacterAI characterAI;
                    if (npcInstance == null || npcInstance instanceof RaidBossInstance || !((characterAI = npcInstance.getAI()) instanceof DefaultAI)) continue;
                    try {
                        Field field = DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
                        field.setAccessible(true);
                        field.set(characterAI, n);
                        if (!characterAI.isActive()) continue;
                        characterAI.stopAITask();
                        ++n2;
                        WorldRegion worldRegion = npcInstance.getCurrentRegion();
                        if (worldRegion == null || !worldRegion.isActive()) continue;
                        characterAI.startAITask();
                        ++n3;
                    }
                    catch (Exception exception) {}
                }
                player.sendMessage(n2 + " AI stopped, " + n3 + " AI started");
                break;
            }
            case admin_spawn2: {
                StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
                try {
                    stringTokenizer.nextToken();
                    String string3 = stringTokenizer.nextToken();
                    int n = 30;
                    int n4 = 1;
                    if (stringTokenizer.hasMoreTokens()) {
                        n4 = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        n = Integer.parseInt(stringTokenizer.nextToken());
                    }
                    this.a(player, string3, n, n4);
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

    public static void showHelpPage(Player player, String string) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        npcHtmlMessage.setFile("admin/" + string);
        player.sendPacket((IStaticPacket)npcHtmlMessage);
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
            simpleSpawner.init();
            if (n == 0) {
                simpleSpawner.stopRespawn();
            }
            player.sendMessage("Created " + npcTemplate.name + " on " + gameObject.getObjectId() + ".");
        }
        catch (Exception exception) {
            player.sendMessage("Target is not ingame.");
        }
    }

    private static enum Commands {
        admin_server,
        admin_check_actor,
        admin_setvar,
        admin_set_ai_interval,
        admin_spawn2;

    }
}

