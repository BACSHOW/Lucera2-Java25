/*
 * Decompiled with CFR 0.152.
 */
package handler.admincommands;

import events.TvT2.PvPEvent;
import handler.admincommands.ScriptAdminCommand;
import l2.gameserver.instancemanager.ServerVariables;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Scripts;

public class AdminPvPEvent
extends ScriptAdminCommand {
    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().IsEventGm) {
            return false;
        }
        switch (commands) {
            case admin_pvpevent: {
                if (stringArray.length > 1) {
                    if (stringArray[1].equalsIgnoreCase("tvt")) {
                        this.a(player, "tvt");
                        break;
                    }
                    if (stringArray[1].equalsIgnoreCase("ctf")) {
                        this.a(player, "ctf");
                        break;
                    }
                    if (!stringArray[1].equalsIgnoreCase("dm")) break;
                    this.a(player, "dm");
                    break;
                }
                this.j(player);
                break;
            }
            case admin_pvpevent_active: {
                if (stringArray.length > 1) {
                    boolean bl = Boolean.parseBoolean(stringArray[1]);
                    ServerVariables.set("PvP_is_active", bl);
                    PvPEvent.getInstance().LoadVars();
                    if (bl) {
                        PvPEvent.getInstance().Activate();
                    } else {
                        PvPEvent.getInstance().Deativate();
                    }
                }
                this.j(player);
                break;
            }
            case admin_pvpevent_setanntime: {
                if (stringArray.length > 1) {
                    ServerVariables.set("PvP_announce_time", stringArray[1]);
                    PvPEvent.getInstance().LoadVars();
                }
                this.j(player);
                break;
            }
            case admin_pvpevent_setannredu: {
                if (stringArray.length > 1) {
                    ServerVariables.set("PvP_announce_reduct", stringArray[1]);
                    PvPEvent.getInstance().LoadVars();
                }
                this.j(player);
                break;
            }
            case admin_pvpevent_countdown: {
                if (stringArray.length > 1) {
                    ServerVariables.set("PvP_event_countdown", stringArray[1]);
                    PvPEvent.getInstance().LoadVars();
                }
                this.j(player);
                break;
            }
            case admin_pvpevent_settime: {
                if (stringArray.length > 1 && !stringArray[1].isEmpty()) {
                    ServerVariables.set("PvP_start_time", ServerVariables.getString("PvP_start_time", "") + stringArray[1]);
                    PvPEvent.getInstance().LoadVars();
                } else {
                    ServerVariables.set("PvP_start_time", "");
                    PvPEvent.getInstance().LoadVars();
                }
                this.j(player);
                break;
            }
            case admin_pvpevent_setinstsid: {
                if (stringArray.length > 1) {
                    ServerVariables.set("PvP_instances_ids", stringArray[1]);
                    PvPEvent.getInstance().LoadVars();
                }
                this.j(player);
                break;
            }
            case admin_pvpevent_enable: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_enabled", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_capcha: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_use_capcha", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_hwid_restrict: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_use_hwid_restrict", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_ip_restrict: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_use_ip_restrict", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_hide_identiti: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_hide_identiti", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_dispell: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_dispell", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_dispell_after: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_dispell_after", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_kill_count_protection: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_min_kill_reward", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_reqpart: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_req_parts", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_maxpart: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_max_parts", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_minlvl: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_min_lvl", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_maxlvl: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_max_lvl", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_evetime: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_time", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_prohclid: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_prohibited_class_ids", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_revdel: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_revive_delay", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_buffprotect: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_buff_protection", Boolean.parseBoolean(stringArray[2]));
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_ipkill: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_item_per_kill", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_herorevhours: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_herorevhours", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_reward_team: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_rev_team", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_reward_top: {
                if (stringArray.length <= 2) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    ServerVariables.set("PvP_" + pvPEventRule.name() + "_rev_top", stringArray[2]);
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_pvpevent_start: {
                if (stringArray.length <= 1) break;
                try {
                    PvPEvent.PvPEventRule pvPEventRule = Enum.valueOf(PvPEvent.PvPEventRule.class, stringArray[1].toUpperCase());
                    PvPEvent.getInstance().LoadVars();
                    PvPEvent.getInstance().setRule(pvPEventRule);
                    PvPEvent.getInstance().goRegistration();
                    this.a(player, pvPEventRule.name().toLowerCase());
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            }
            case admin_capt1: {
                Scripts.getInstance().callScripts(player, "Util", "RequestCapcha", new Object[]{"handler.admincommands.AdminPvPEvent:CapchaAccespted", player.getStoredId(), 60});
                return true;
            }
            case admin_tvt1: {
                PvPEvent.getInstance().LoadVars();
                PvPEvent.getInstance().setRule(PvPEvent.PvPEventRule.TVT);
                PvPEvent.getInstance().goRegistration();
                break;
            }
            case admin_dm1: {
                PvPEvent.getInstance().LoadVars();
                PvPEvent.getInstance().setRule(PvPEvent.PvPEventRule.DM);
                PvPEvent.getInstance().goRegistration();
                break;
            }
            case admin_ctf1: {
                PvPEvent.getInstance().LoadVars();
                PvPEvent.getInstance().setRule(PvPEvent.PvPEventRule.CTF);
                PvPEvent.getInstance().goRegistration();
            }
        }
        return false;
    }

    private void j(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><title>PvPEvent</title><body>");
        this.a(stringBuilder);
        this.b(stringBuilder);
        this.a(stringBuilder, "Active", ServerVariables.getBool("PvP_is_active", false), "admin_pvpevent_active");
        this.b(stringBuilder, "Announce time", ServerVariables.getString("PvP_announce_time", "5"), "anntime", "admin_pvpevent_setanntime");
        this.b(stringBuilder, "Announce reduct", ServerVariables.getString("PvP_announce_reduct", "1"), "annredu", "admin_pvpevent_setannredu");
        this.a(stringBuilder, "On Event Countdown", ServerVariables.getBool("PvP_event_countdown", true), "admin_pvpevent_countdown");
        this.a(stringBuilder, "Start time (add)", ServerVariables.getString("PvP_start_time", ""), "sttime", "admin_pvpevent_settime");
        this.a(stringBuilder, "Instances (delim ';')", ServerVariables.getString("PvP_instances_ids", ""), "insid", "admin_pvpevent_setinstsid");
        this.b(stringBuilder);
        PvPEvent.PvPEventRule pvPEventRule = PvPEvent.getInstance().getNextRule(PvPEvent.getInstance().getRule());
        stringBuilder.append("<center>Next event: " + (pvPEventRule != null ? pvPEventRule.name() : "none") + "</center><br>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private void a(Player player, String string) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><title>PvPEvent - " + string + "</title><body>");
        this.a(stringBuilder);
        this.b(stringBuilder);
        this.a(stringBuilder, string);
        this.b(stringBuilder);
        if (string.equalsIgnoreCase("tvt")) {
            this.a(stringBuilder, "tvt", true, true);
        } else if (string.equalsIgnoreCase("ctf")) {
            this.a(stringBuilder, "ctf", true, false);
        } else if (string.equalsIgnoreCase("dm")) {
            this.a(stringBuilder, "dm", false, true);
        }
        this.b(stringBuilder);
        stringBuilder.append("<center><button value=\"Start " + string + "\" action=\"bypass -h admin_pvpevent_start " + string + "\" width=100 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></center>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private void a(StringBuilder stringBuilder) {
        stringBuilder.append("<center><table width=260><tr>");
        stringBuilder.append("<td><button value=\"Main\" action=\"bypass -h admin_pvpevent\" width=60 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td>");
        stringBuilder.append("<td><button value=\"TVT\" action=\"bypass -h admin_pvpevent tvt\" width=60 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td>");
        stringBuilder.append("<td><button value=\"CTF\" action=\"bypass -h admin_pvpevent ctf\" width=60 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td>");
        stringBuilder.append("<td><button value=\"DM\" action=\"bypass -h admin_pvpevent dm\" width=60 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td>");
        stringBuilder.append("</tr></table></center>");
    }

    private void a(StringBuilder stringBuilder, String string, String string2, String string3, String string4) {
        stringBuilder.append("<center>" + string + ":</center><br1>");
        stringBuilder.append("<center><table width=260><tr>");
        stringBuilder.append("<td width=40>Curr:</td><td width=210>").append(string2).append("</td>");
        stringBuilder.append("</tr></table></center><br1><center><table width=260><tr>");
        stringBuilder.append("<td width=40>New:</td><td width=180><edit var=\"" + string3 + "\" width=180 length=72></td><td width=40>");
        stringBuilder.append("<button value=\"Set\" action=\"bypass -h " + string4 + " $" + string3 + "\" width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td>");
        stringBuilder.append("</tr></table></center><br>");
    }

    private void b(StringBuilder stringBuilder) {
        stringBuilder.append("<br><center><img src=\"L2UI.SquareWhite\" width=260 height=1></center><br>");
    }

    private void b(StringBuilder stringBuilder, String string, String string2, String string3, String string4) {
        stringBuilder.append("<center><table width=260><tr><td width=120 align=left>");
        stringBuilder.append(string);
        stringBuilder.append(":</td><td width=40>");
        stringBuilder.append(string2);
        stringBuilder.append("</td><td width=40><edit var=\"");
        stringBuilder.append(string3);
        stringBuilder.append("\" width=40 length=5></td><td width=40><button value=\"set\" action=\"bypass -h ");
        stringBuilder.append(string4);
        stringBuilder.append(" $");
        stringBuilder.append(string3);
        stringBuilder.append("\" width=40 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td></tr></table></center><br>");
    }

    private void a(StringBuilder stringBuilder, String string, boolean bl, String string2) {
        stringBuilder.append("<center><table width=260><tr><td width=100>");
        stringBuilder.append(string);
        stringBuilder.append(":</td><td width=70>");
        stringBuilder.append(bl ? "on" : "off");
        stringBuilder.append(":</td><td width=60><td width=30 align=right>");
        stringBuilder.append("<button value=\"on\" action=\"bypass -h ");
        stringBuilder.append(string2);
        stringBuilder.append(" true\" width=30 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td><td width=30 align=left>");
        stringBuilder.append("<button value=\"off\" action=\"bypass -h ");
        stringBuilder.append(string2);
        stringBuilder.append(" false\" width=30 height=20 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\"></td></tr></table></center><br>");
    }

    private void a(StringBuilder stringBuilder, String string) {
        this.a(stringBuilder, "Enabled", ServerVariables.getBool("PvP_" + string.toUpperCase() + "_enabled", false), "admin_pvpevent_enable " + string.toLowerCase());
        this.a(stringBuilder, "Captcha", ServerVariables.getBool("PvP_" + string.toUpperCase() + "_use_capcha", false), "admin_pvpevent_capcha " + string.toLowerCase());
        this.a(stringBuilder, "HWID Check", ServerVariables.getBool("PvP_" + string.toUpperCase() + "_use_hwid_restrict", false), "admin_pvpevent_hwid_restrict " + string.toLowerCase());
        this.b(stringBuilder, "Kill Count Check", ServerVariables.getString("PvP_" + string.toUpperCase() + "_min_kill_reward", "0"), "count_protect", "admin_kill_count_protection " + string.toLowerCase());
        this.a(stringBuilder, "Hide identiti", ServerVariables.getBool("PvP_" + string.toUpperCase() + "_hide_identiti", false), "admin_pvpevent_hide_identiti " + string.toLowerCase());
        this.a(stringBuilder, "Remove Buff To", ServerVariables.getBool("PvP_" + string.toUpperCase() + "_dispell", false), "admin_pvpevent_dispell " + string.toLowerCase());
        this.a(stringBuilder, "Remove Buff From", ServerVariables.getBool("PvP_" + string.toUpperCase() + "_dispell_after", false), "admin_pvpevent_dispell_after " + string.toLowerCase());
        this.a(stringBuilder, "Buff Protect", ServerVariables.getBool("PvP_" + string.toUpperCase() + "_buff_protection", false), "admin_pvpevent_buffprotect " + string.toLowerCase());
        this.b(stringBuilder, "Req part", ServerVariables.getString("PvP_" + string.toUpperCase() + "_req_parts", "50"), "reqpart", "admin_pvpevent_reqpart " + string.toLowerCase());
        this.b(stringBuilder, "Max part", ServerVariables.getString("PvP_" + string.toUpperCase() + "_max_parts", "100"), "maxpart", "admin_pvpevent_maxpart " + string.toLowerCase());
        this.b(stringBuilder, "Min lvl", ServerVariables.getString("PvP_" + string.toUpperCase() + "_min_lvl", "1"), "minlvl", "admin_pvpevent_minlvl " + string.toLowerCase());
        this.b(stringBuilder, "Max lvl", ServerVariables.getString("PvP_" + string.toUpperCase() + "_max_lvl", "86"), "maxlvl", "admin_pvpevent_maxlvl " + string.toLowerCase());
        this.b(stringBuilder, "Time", ServerVariables.getString("PvP_" + string.toUpperCase() + "_time", "10"), "evetime", "admin_pvpevent_evetime " + string.toLowerCase());
        this.b(stringBuilder, "Perkill item", ServerVariables.getString("PvP_" + string.toUpperCase() + "_item_per_kill", "0"), "iperkill", "admin_pvpevent_ipkill " + string.toLowerCase());
        this.b(stringBuilder, "Revive delay", ServerVariables.getString("PvP_" + string.toUpperCase() + "_revive_delay", "1"), "revdel", "admin_pvpevent_revdel " + string.toLowerCase());
        this.a(stringBuilder, "Class id restrict", ServerVariables.getString("PvP_" + string.toUpperCase() + "_prohibited_class_ids", "0"), "prohclid", "admin_pvpevent_prohclid " + string.toLowerCase());
    }

    private void a(StringBuilder stringBuilder, String string, boolean bl, boolean bl2) {
        if (bl) {
            stringBuilder.append("<center>Team reward list</center><br>");
            stringBuilder.append("<center>Simple: 57:100;4037:100</center><br>");
            this.a(stringBuilder, "Field title", ServerVariables.getString("PvP_" + string.toUpperCase() + "_rev_team", ""), "trev", "admin_pvpevent_reward_team " + string.toLowerCase());
        }
        if (bl2) {
            stringBuilder.append("<center>Top reward list</center><br>");
            stringBuilder.append("<center>Simple: 57:100;4037:100</center><br>");
            this.a(stringBuilder, "Field title", ServerVariables.getString("PvP_" + string.toUpperCase() + "_rev_top", ""), "srev", "admin_pvpevent_reward_top " + string.toLowerCase());
            this.b(stringBuilder, "Hours of hero status", ServerVariables.getString("PvP_" + string.toUpperCase() + "_herorevhours", "0"), "hhrev", "admin_pvpevent_herorevhours " + string.toLowerCase());
        }
    }

    private static enum Commands {
        admin_pvpevent,
        admin_pvpevent_active,
        admin_pvpevent_setanntime,
        admin_pvpevent_setannredu,
        admin_pvpevent_countdown,
        admin_pvpevent_settime,
        admin_pvpevent_setinstsid,
        admin_pvpevent_enable,
        admin_pvpevent_capcha,
        admin_pvpevent_hwid_restrict,
        admin_pvpevent_ip_restrict,
        admin_pvpevent_hide_identiti,
        admin_pvpevent_dispell,
        admin_pvpevent_dispell_after,
        admin_kill_count_protection,
        admin_pvpevent_reqpart,
        admin_pvpevent_maxpart,
        admin_pvpevent_minlvl,
        admin_pvpevent_maxlvl,
        admin_pvpevent_evetime,
        admin_pvpevent_ipkill,
        admin_pvpevent_herorevhours,
        admin_pvpevent_prohclid,
        admin_pvpevent_revdel,
        admin_pvpevent_buffprotect,
        admin_pvpevent_reward_team,
        admin_pvpevent_reward_top,
        admin_pvpevent_start,
        admin_capt1,
        admin_tvt1,
        admin_dm1,
        admin_ctf1;

    }
}

