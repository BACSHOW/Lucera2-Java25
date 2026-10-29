/*
 * Decompiled with CFR 0.152.
 */
package handler.admincommands;

import handler.admincommands.ScriptAdminCommand;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminClientSupport
extends ScriptAdminCommand {
    private static final Logger _log = LoggerFactory.getLogger(AdminClientSupport.class);

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        GameObject gameObject = player.getTarget();
        switch (commands) {
            case admin_setskill: {
                if (stringArray.length != 3) {
                    return false;
                }
                if (!player.getPlayerAccess().CanEditChar) {
                    return false;
                }
                if (gameObject == null || !gameObject.isPlayer()) {
                    return false;
                }
                try {
                    Skill skill = SkillTable.getInstance().getInfo(Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2]));
                    gameObject.getPlayer().addSkill(skill, true);
                    gameObject.getPlayer().sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));
                    break;
                }
                catch (NumberFormatException numberFormatException) {
                    _log.info("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + numberFormatException, numberFormatException);
                    return false;
                }
            }
            case admin_summon: {
                if (stringArray.length != 3) {
                    return false;
                }
                if (!player.getPlayerAccess().CanEditChar) {
                    return false;
                }
                try {
                    int n = Integer.parseInt(stringArray[1]);
                    long l = Long.parseLong(stringArray[2]);
                    if (n >= 1000000) {
                        if (gameObject == null) {
                            gameObject = player;
                        }
                        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n - 1000000);
                        int n2 = 0;
                        while ((long)n2 < l) {
                            NpcInstance npcInstance = npcTemplate.getNewInstance();
                            npcInstance.setSpawnedLoc(gameObject.getLoc());
                            npcInstance.setCurrentHpMp(npcInstance.getMaxHp(), npcInstance.getMaxMp(), true);
                            npcInstance.spawnMe(npcInstance.getSpawnedLoc());
                            ++n2;
                        }
                    } else {
                        if (gameObject == null) {
                            gameObject = player;
                        }
                        if (!gameObject.isPlayer()) {
                            return false;
                        }
                        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
                        if (itemTemplate == null) {
                            return false;
                        }
                        if (itemTemplate.isStackable()) {
                            ItemInstance itemInstance = ItemFunctions.createItem(n);
                            itemInstance.setCount(l);
                            gameObject.getPlayer().getInventory().addItem(itemInstance);
                            gameObject.getPlayer().sendPacket((IStaticPacket)SystemMessage2.obtainItems(itemInstance));
                            break;
                        }
                        int n3 = 0;
                        while ((long)n3 < l) {
                            ItemInstance itemInstance = ItemFunctions.createItem(n);
                            gameObject.getPlayer().getInventory().addItem(itemInstance);
                            gameObject.getPlayer().sendPacket((IStaticPacket)SystemMessage2.obtainItems(itemInstance));
                            ++n3;
                        }
                    }
                    break;
                }
                catch (NumberFormatException numberFormatException) {
                    _log.info("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + numberFormatException, numberFormatException);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    public static enum Commands {
        admin_setskill,
        admin_summon;

    }
}

