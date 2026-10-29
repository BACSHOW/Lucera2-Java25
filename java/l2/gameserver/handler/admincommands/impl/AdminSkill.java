/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExEnchantSkillList;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.stats.Calculator;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Log;

public class AdminSkill
implements IAdminCommandHandler {
    private static Skill[] zh;

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditChar) {
            return false;
        }
        switch (commands) {
            case admin_show_skills: {
                this.D(player);
                break;
            }
            case admin_show_effects: {
                this.E(player);
                break;
            }
            case admin_stop_effect: {
                this.a(player, stringArray);
                break;
            }
            case admin_remove_skills: {
                this.C(player);
                break;
            }
            case admin_skill_list: {
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/skills.htm"));
                break;
            }
            case admin_skill_index: {
                if (stringArray.length <= 1) break;
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/skills/" + stringArray[1] + ".htm"));
                break;
            }
            case admin_add_skill: {
                this.b(player, stringArray);
                break;
            }
            case admin_remove_skill: {
                this.c(player, stringArray);
                break;
            }
            case admin_get_skills: {
                this.F(player);
                break;
            }
            case admin_reset_skills: {
                this.G(player);
                break;
            }
            case admin_give_all_skills: {
                this.B(player);
                break;
            }
            case admin_debug_stats: {
                this.A(player);
                break;
            }
            case admin_remove_cooldown: {
                Player player2 = player.getTarget() != null ? player.getTarget().getPlayer() : (stringArray.length > 1 ? GameObjectsStorage.getPlayer(stringArray[1]) : null);
                if (player2 != null) {
                    player2.resetReuse();
                    player2.sendPacket((IStaticPacket)new SkillCoolTime(player2));
                    player.sendMessage("Skills reuse delay reseted.");
                    break;
                }
                player.sendMessage("Usage: //remove_cooldown [<target>|player_name]");
                break;
            }
            case admin_buff: {
                for (int i = 7041; i <= 7064; ++i) {
                    player.addSkill(SkillTable.getInstance().getInfo(i, 1));
                }
                player.sendPacket((IStaticPacket)new SkillList(player));
                break;
            }
            case admin_skill_ench: 
            case admin_skill_enchant: {
                player.sendPacket((IStaticPacket)ExEnchantSkillList.packetFor(player));
            }
        }
        return true;
    }

    private void A(Player player) {
        GameObject gameObject = player.getTarget();
        if (!gameObject.isCreature()) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Creature creature = (Creature)gameObject;
        Calculator[] calculatorArray = creature.getCalculators();
        String string = "--- Debug for " + creature.getName() + " ---\r\n";
        for (Calculator calculator : calculatorArray) {
            if (calculator == null) continue;
            Env env = new Env(creature, player, null);
            env.value = calculator.getBase();
            string = string + "Stat: " + calculator._stat.getValue() + ", prevValue: " + calculator.getLast() + "\r\n";
            Func[] funcArray = calculator.getFunctions();
            for (int i = 0; i < funcArray.length; ++i) {
                String string2 = Integer.toHexString(funcArray[i].order).toUpperCase();
                if (string2.length() == 1) {
                    string2 = "0" + string2;
                }
                string = string + "\tFunc #" + i + "@ [0x" + string2 + "]" + funcArray[i].getClass().getSimpleName() + "\t" + env.value;
                if (funcArray[i].getCondition() == null || funcArray[i].getCondition().test(env)) {
                    funcArray[i].calc(env);
                }
                string = string + " -> " + env.value + (funcArray[i].owner != null ? "; owner: " + funcArray[i].owner.toString() : "; no owner") + "\r\n";
            }
        }
        player.sendMessage("Debug stats for " + creature.getName() + " dumped to */log/game.log");
        Log.add(string, "debug_stats");
    }

    private void B(Player player) {
        GameObject gameObject = player.getTarget();
        Player player2 = null;
        if (gameObject == null || !gameObject.isPlayer() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        player2 = (Player)gameObject;
        int n = 0;
        int n2 = 0;
        Collection<SkillLearn> collection = SkillAcquireHolder.getInstance().getAvailableSkills(player2, AcquireType.NORMAL);
        while (collection.size() > n) {
            n = 0;
            for (SkillLearn skillLearn : collection) {
                Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
                if (skill == null || !skill.getCanLearn(player2.getClassId())) {
                    ++n;
                    continue;
                }
                if (player2.getSkillLevel(skill.getId()) == -1) {
                    ++n2;
                }
                player2.addSkill(skill, true);
            }
            collection = SkillAcquireHolder.getInstance().getAvailableSkills(player2, AcquireType.NORMAL);
        }
        player2.sendMessage("Admin gave you " + n2 + " skills.");
        player2.sendPacket((IStaticPacket)new SkillList(player2));
        player.sendMessage("You gave " + n2 + " skills to " + player2.getName());
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void C(Player player) {
        GameObject gameObject = player.getTarget();
        if (!gameObject.isPlayer() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Player player2 = (Player)gameObject;
        List list = new ArrayList<Skill>();
        list.addAll(player2.getAllSkills());
        list = list.subList(0, Math.min(list.size(), 50));
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<table width=260><tr>");
        stringBuilder.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td width=180><center>Character Selection Menu</center></td>");
        stringBuilder.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("</tr></table>");
        stringBuilder.append("<br><br>");
        stringBuilder.append("<center>Editing character: " + player2.getName() + "</center>");
        stringBuilder.append("<br><table width=270><tr><td>Lv: " + player2.getLevel() + " " + player2.getTemplate().className + "</td></tr></table>");
        stringBuilder.append("<br><center>Click on the skill you wish to remove:</center>");
        stringBuilder.append("<br><table width=270>");
        stringBuilder.append("<tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
        for (Object __raw_skill_209 : (Iterable<?>)(Object)(list)) {
           Skill skill = (Skill)__raw_skill_209;
            stringBuilder.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill " + skill.getId() + "\">" + skill.getName() + "</a></td><td width=60>" + skill.getLevel() + "</td><td width=40>" + skill.getId() + "</td></tr>");
        }
        stringBuilder.append("</table>");
        stringBuilder.append("<br><center><table>");
        stringBuilder.append("Remove custom skill:");
        stringBuilder.append("<tr><td>Id: </td>");
        stringBuilder.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
        stringBuilder.append("</table></center>");
        stringBuilder.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center>");
        stringBuilder.append("<br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private void D(Player player) {
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isPlayer() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Player player2 = (Player)gameObject;
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<table width=260><tr>");
        stringBuilder.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td width=180><center>Character Selection Menu</center></td>");
        stringBuilder.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("</tr></table>");
        stringBuilder.append("<br><br>");
        stringBuilder.append("<center>Editing character: " + player2.getName() + "</center>");
        stringBuilder.append("<br><table width=270><tr><td>Lv: " + player2.getLevel() + " " + player2.getTemplate().className + "</td></tr></table>");
        stringBuilder.append("<br><center><table>");
        stringBuilder.append("<tr><td><button value=\"Add skills\" action=\"bypass -h admin_skill_list\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td><button value=\"Get skills\" action=\"bypass -h admin_get_skills\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("<tr><td><button value=\"Delete skills\" action=\"bypass -h admin_remove_skills\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td><button value=\"Reset skills\" action=\"bypass -h admin_reset_skills\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("<tr><td><button value=\"Give All Skills\" action=\"bypass -h admin_give_all_skills\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
        stringBuilder.append("</table></center>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private void E(Player player) {
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isPlayable() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Playable playable = (Playable)gameObject;
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<table width=260><tr>");
        stringBuilder.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("<td width=180><center>Character Selection Menu</center></td>");
        stringBuilder.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
        stringBuilder.append("</tr></table>");
        stringBuilder.append("<br><br>");
        stringBuilder.append("<center>Editing character: " + playable.getName() + "</center>");
        stringBuilder.append("<br><center><button value=\"Refresh\" action=\"bypass -h admin_show_effects\" width=100 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\" /></center>");
        stringBuilder.append("<br>");
        List<Effect> list = playable.getEffectList().getAllEffects();
        if (list != null && !list.isEmpty()) {
            for (Effect effect : list) {
                stringBuilder.append("&nbsp;<a action=\"bypass -h admin_stop_effect ").append(effect.getSkill().getId()).append("\">");
                stringBuilder.append(effect.getSkill().getName()).append(" ").append(effect.getSkill().getLevel());
                stringBuilder.append("</a> - ").append(effect.getSkill().isToggle() ? "Infinity" : effect.getTimeLeft() + " seconds").append("<br1>");
            }
        }
        stringBuilder.append("<br></body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private void a(Player player, String[] stringArray) {
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isPlayable() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Playable playable = (Playable)gameObject;
        if (stringArray.length == 2) {
            int n = Integer.parseInt(stringArray[1]);
            List<Effect> list = playable.getEffectList().getEffectsBySkillId(n);
            if (list != null && !list.isEmpty()) {
                for (Effect effect : list) {
                    effect.exit();
                    playable.getPlayer().sendMessage("Admin removed effect of " + effect.getSkill().getName() + ".");
                    playable.sendChanges();
                    playable.updateStats();
                    playable.updateEffectIcons();
                    playable.broadcastStatusUpdate();
                    player.sendMessage("You removed effect of " + effect.getSkill().getName() + " from " + playable.getName() + ".");
                }
            } else {
                player.sendMessage("Error: there is no such skill.");
            }
        }
        this.E(player);
    }

    private void F(Player player) {
        GameObject gameObject = player.getTarget();
        if (!gameObject.isPlayer() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Player player2 = (Player)gameObject;
        if (player2.getName().equals(player.getName())) {
            player2.sendMessage("There is no point in doing it on your character.");
        } else {
            Collection<Skill> collection = player2.getAllSkills();
            zh = player.getAllSkillsArray();
            for (Skill skill : zh) {
                player.removeSkill(skill, true);
            }
            for (Skill skill : collection) {
                player.addSkill(skill, true);
            }
            player.sendMessage("You now have all the skills of  " + player2.getName() + ".");
        }
        this.D(player);
    }

    private void G(Player player) {
        GameObject gameObject = player.getTarget();
        Player player2 = null;
        if (!gameObject.isPlayer() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        player2 = (Player)gameObject;
        Skill[] skillArray = player2.getAllSkillsArray();
        int n = 0;
        for (Skill skill : skillArray) {
            if (skill.isClanSkill() || skill.isCommon() || SkillAcquireHolder.getInstance().isSkillPossible(player2, skill)) continue;
            player2.removeSkill(skill, true);
            player2.removeSkillFromShortCut(skill.getId());
            ++n;
        }
        player2.checkSkills();
        player2.sendPacket((IStaticPacket)new SkillList(player2));
        player2.sendMessage("[GM]" + player.getName() + " has updated your skills.");
        player.sendMessage(n + " skills removed.");
        this.D(player);
    }

    private void b(Player player, String[] stringArray) {
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isPlayer() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        Player player2 = (Player)gameObject;
        if (stringArray.length == 3) {
            int n = Integer.parseInt(stringArray[1]);
            int n2 = Integer.parseInt(stringArray[2]);
            Skill skill = SkillTable.getInstance().getInfo(n, n2);
            if (skill != null) {
                player2.sendMessage("Admin gave you the skill " + skill.getName() + ".");
                player2.addSkill(skill, true);
                player2.sendPacket((IStaticPacket)new SkillList(player2));
                player.sendMessage("You gave the skill " + skill.getName() + " to " + player2.getName() + ".");
            } else {
                player.sendMessage("Error: there is no such skill.");
            }
        }
        this.D(player);
    }

    private void c(Player player, String[] stringArray) {
        GameObject gameObject = player.getTarget();
        Player player2 = null;
        if (!gameObject.isPlayer() || player != gameObject && !player.getPlayerAccess().CanEditCharAll) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        player2 = (Player)gameObject;
        if (stringArray.length == 2) {
            int n = Integer.parseInt(stringArray[1]);
            int n2 = player2.getSkillLevel(n);
            Skill skill = SkillTable.getInstance().getInfo(n, n2);
            if (skill != null) {
                player2.sendMessage("Admin removed the skill " + skill.getName() + ".");
                player2.removeSkill(skill, true);
                player2.sendPacket((IStaticPacket)new SkillList(player2));
                player.sendMessage("You removed the skill " + skill.getName() + " from " + player2.getName() + ".");
            } else {
                player.sendMessage("Error: there is no such skill.");
            }
        }
        this.C(player);
    }

    private static enum Commands {
        admin_show_skills,
        admin_remove_skills,
        admin_skill_list,
        admin_skill_index,
        admin_add_skill,
        admin_remove_skill,
        admin_get_skills,
        admin_reset_skills,
        admin_give_all_skills,
        admin_show_effects,
        admin_stop_effect,
        admin_debug_stats,
        admin_remove_cooldown,
        admin_buff,
        admin_skill_ench,
        admin_skill_enchant;

    }
}

