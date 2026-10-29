/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences;

import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.data.StringHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.TeleportLocation;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.ReflectionUtils;
import l2.gameserver.utils.TimeUtils;
import l2.gameserver.utils.WarehouseFunctions;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ResidenceManager
extends MerchantInstance {
    protected static final int COND_FAIL = 0;
    protected static final int COND_SIEGE = 1;
    protected static final int COND_OWNER = 2;
    protected String _siegeDialog;
    protected String _mainDialog;
    protected String _failDialog;
    protected int[] _doors;

    public ResidenceManager(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setDialogs();
        this._doors = npcTemplate.getAIParams().getIntegerArray("doors", ArrayUtils.EMPTY_INT_ARRAY);
    }

    protected void setDialogs() {
        this._siegeDialog = this.getTemplate().getAIParams().getString("siege_dialog", "npcdefault.htm");
        this._mainDialog = this.getTemplate().getAIParams().getString("main_dialog", "npcdefault.htm");
        this._failDialog = this.getTemplate().getAIParams().getString("fail_dialog", "npcdefault.htm");
    }

    protected abstract Residence getResidence();

    protected abstract L2GameServerPacket decoPacket();

    protected abstract int getPrivUseFunctions();

    protected abstract int getPrivSetFunctions();

    protected abstract int getPrivDismiss();

    protected abstract int getPrivDoors();

    public void broadcastDecoInfo() {
        L2GameServerPacket l2GameServerPacket = this.decoPacket();
        if (l2GameServerPacket == null) {
            return;
        }
        for (Player player : World.getAroundPlayers(this)) {
            player.sendPacket((IStaticPacket)l2GameServerPacket);
        }
    }

    protected int getCond(Player player) {
        Residence residence = this.getResidence();
        Clan clan = residence.getOwner();
        if (clan != null && player.getClan() == clan) {
            if (((SiegeEvent)residence.getSiegeEvent()).isInProgress()) {
                return 1;
            }
            return 2;
        }
        return 0;
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        String string = null;
        int n2 = this.getCond(player);
        switch (n2) {
            case 2: {
                string = this._mainDialog;
                break;
            }
            case 1: {
                string = this._siegeDialog;
                break;
            }
            case 0: {
                string = this._failDialog;
            }
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, string, n));
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!ResidenceManager.canBypassCheck(player, this)) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, " ");
        String string2 = stringTokenizer.nextToken();
        String string3 = "";
        if (stringTokenizer.countTokens() >= 1) {
            string3 = stringTokenizer.nextToken();
        }
        int n = this.getCond(player);
        switch (n) {
            case 1: {
                this.showChatWindow(player, this._siegeDialog, new Object[0]);
                return;
            }
            case 0: {
                this.showChatWindow(player, this._failDialog, new Object[0]);
                return;
            }
        }
        if (string2.equalsIgnoreCase("banish")) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
            npcHtmlMessage.setFile("residence/Banish.htm");
            this.a(player, npcHtmlMessage);
        } else {
            if (string2.equalsIgnoreCase("banish_foreigner")) {
                if (!this.isHaveRigths(player, this.getPrivDismiss())) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                    return;
                }
                this.getResidence().banishForeigner();
                return;
            }
            if (string2.equalsIgnoreCase("Buy")) {
                if (string3.equals("")) {
                    return;
                }
                this.showShopWindow(player, Integer.valueOf(string3), true);
            } else {
                if (string2.equalsIgnoreCase("manage_vault")) {
                    if (string3.equalsIgnoreCase("deposit")) {
                        WarehouseFunctions.showDepositWindowClan(player);
                    } else if (string3.equalsIgnoreCase("withdraw")) {
                        int n2 = Integer.valueOf(stringTokenizer.nextToken());
                        if (n2 == 99) {
                            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                            npcHtmlMessage.setFile("residence/clan.htm");
                            npcHtmlMessage.replace("%npcname%", this.getName());
                            player.sendPacket((IStaticPacket)npcHtmlMessage);
                        } else {
                            WarehouseFunctions.showWithdrawWindowClan(player, n2);
                        }
                    } else {
                        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                        npcHtmlMessage.setFile("residence/vault.htm");
                        this.a(player, npcHtmlMessage);
                    }
                    return;
                }
                if (string2.equalsIgnoreCase("door")) {
                    this.showChatWindow(player, "residence/door.htm", new Object[0]);
                } else if (string2.equalsIgnoreCase("openDoors")) {
                    if (this.isHaveRigths(player, this.getPrivDoors())) {
                        for (int n3 : this._doors) {
                            ReflectionUtils.getDoor(n3).openMe();
                        }
                        this.showChatWindow(player, "residence/door.htm", new Object[0]);
                    } else {
                        player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                    }
                } else if (string2.equalsIgnoreCase("closeDoors")) {
                    if (this.isHaveRigths(player, this.getPrivDoors())) {
                        for (int n4 : this._doors) {
                            ReflectionUtils.getDoor(n4).closeMe();
                        }
                        this.showChatWindow(player, "residence/door.htm", new Object[0]);
                    } else {
                        player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                    }
                } else if (string2.equalsIgnoreCase("functions")) {
                    if (!this.isHaveRigths(player, this.getPrivUseFunctions())) {
                        player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                        return;
                    }
                    if (string3.equalsIgnoreCase("tele")) {
                        if (!this.getResidence().isFunctionActive(1)) {
                            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                            npcHtmlMessage.setFile("residence/teleportNotActive.htm");
                            this.a(player, npcHtmlMessage);
                            return;
                        }
                        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                        npcHtmlMessage.setFile("residence/teleport.htm");
                        TeleportLocation[] teleportLocationArray = this.getResidence().getFunction(1).getTeleports();
                        StringBuilder stringBuilder = new StringBuilder(100 * teleportLocationArray.length);
                        for (TeleportLocation objectArray2 : teleportLocationArray) {
                            String string4 = String.valueOf(objectArray2.getPrice());
                            String skill = new CustomMessage(objectArray2.getName(), player, new Object[0]).toString();
                            stringBuilder.append("<a action=\"bypass -h scripts_Util:Gatekeeper ");
                            stringBuilder.append(objectArray2.getX());
                            stringBuilder.append(" ");
                            stringBuilder.append(objectArray2.getY());
                            stringBuilder.append(" ");
                            stringBuilder.append(objectArray2.getZ());
                            stringBuilder.append(" ");
                            stringBuilder.append(string4);
                            stringBuilder.append("\" msg=\"811;F;");
                            stringBuilder.append(skill);
                            stringBuilder.append("\">");
                            stringBuilder.append(skill);
                            stringBuilder.append(" - ");
                            stringBuilder.append(string4);
                            stringBuilder.append(" ");
                            stringBuilder.append("Adena");
                            stringBuilder.append("</a><br1>");
                        }
                        npcHtmlMessage.replace("%teleList%", stringBuilder.toString());
                        this.a(player, npcHtmlMessage);
                    } else if (string3.equalsIgnoreCase("item_creation")) {
                        if (!this.getResidence().isFunctionActive(2)) {
                            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                            npcHtmlMessage.setFile("residence/itemNotActive.htm");
                            this.a(player, npcHtmlMessage);
                            return;
                        }
                        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                        npcHtmlMessage.setFile("residence/item.htm");
                        String string6 = StringHolder.getInstance().getNotNull(player, "residencemanager.buyitems.button");
                        string6 = string6.replaceAll("%id%", String.valueOf(this.getResidence().getFunction(2).getBuylist()[1])).replace("%objectId%", String.valueOf(this.getObjectId()));
                        npcHtmlMessage.replace("%itemList%", string6);
                        this.a(player, npcHtmlMessage);
                    } else if (string3.equalsIgnoreCase("support")) {
                        if (!this.getResidence().isFunctionActive(6)) {
                            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                            npcHtmlMessage.setFile("residence/supportNotActive.htm");
                            this.a(player, npcHtmlMessage);
                            return;
                        }
                        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                        npcHtmlMessage.setFile("residence/support.htm");
                        Object[][] objectArray = this.getResidence().getFunction(6).getBuffs();
                        StringBuilder stringBuilder = new StringBuilder(objectArray.length * 50);
                        int n5 = 0;
                        for (Object[] objectArray2 : objectArray) {
                            Skill skill = (Skill)objectArray2[0];
                            stringBuilder.append("<a action=\"bypass -h npc_%objectId%_support ");
                            stringBuilder.append(String.valueOf(skill.getId()));
                            stringBuilder.append(" ");
                            stringBuilder.append(String.valueOf(skill.getLevel()));
                            stringBuilder.append("\">");
                            stringBuilder.append(skill.getName());
                            stringBuilder.append(" Lv.");
                            stringBuilder.append(String.valueOf(skill.getDisplayLevel()));
                            stringBuilder.append("</a><br1>");
                            if (++n5 % 5 != 0) continue;
                            stringBuilder.append("<br>");
                        }
                        String skill = StringHolder.getInstance().getNotNull(player, "residencemanager.buff.strings");
                        npcHtmlMessage.replace("%magicList%", stringBuilder.toString());
                        npcHtmlMessage.replace("%mp%", String.valueOf(Math.round(this.getCurrentMp())));
                        npcHtmlMessage.replace("%all%", Config.ALT_CH_ALL_BUFFS ? skill : "");
                        this.a(player, npcHtmlMessage);
                    } else if (string3.equalsIgnoreCase("back")) {
                        this.showChatWindow(player, 0, new Object[0]);
                    } else {
                        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                        npcHtmlMessage.setFile("residence/functions.htm");
                        if (this.getResidence().isFunctionActive(5)) {
                            npcHtmlMessage.replace("%xp_regen%", String.valueOf(this.getResidence().getFunction(5).getLevel()) + "%");
                        } else {
                            npcHtmlMessage.replace("%xp_regen%", "0%");
                        }
                        if (this.getResidence().isFunctionActive(3)) {
                            npcHtmlMessage.replace("%hp_regen%", String.valueOf(this.getResidence().getFunction(3).getLevel()) + "%");
                        } else {
                            npcHtmlMessage.replace("%hp_regen%", "0%");
                        }
                        if (this.getResidence().isFunctionActive(4)) {
                            npcHtmlMessage.replace("%mp_regen%", String.valueOf(this.getResidence().getFunction(4).getLevel()) + "%");
                        } else {
                            npcHtmlMessage.replace("%mp_regen%", "0%");
                        }
                        this.a(player, npcHtmlMessage);
                    }
                } else {
                    if (string2.equalsIgnoreCase("manage")) {
                        if (!this.isHaveRigths(player, this.getPrivSetFunctions())) {
                            player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                            return;
                        }
                        if (string3.equalsIgnoreCase("recovery")) {
                            if (stringTokenizer.countTokens() >= 1) {
                                string3 = stringTokenizer.nextToken();
                                boolean bl = true;
                                if (string3.equalsIgnoreCase("hp")) {
                                    bl = this.getResidence().updateFunctions(3, Integer.valueOf(stringTokenizer.nextToken()));
                                } else if (string3.equalsIgnoreCase("mp")) {
                                    bl = this.getResidence().updateFunctions(4, Integer.valueOf(stringTokenizer.nextToken()));
                                } else if (string3.equalsIgnoreCase("exp")) {
                                    bl = this.getResidence().updateFunctions(5, Integer.valueOf(stringTokenizer.nextToken()));
                                }
                                if (!bl) {
                                    player.sendPacket((IStaticPacket)SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
                                } else {
                                    this.broadcastDecoInfo();
                                }
                            }
                            this.ah(player);
                        } else if (string3.equalsIgnoreCase("other")) {
                            if (stringTokenizer.countTokens() >= 1) {
                                string3 = stringTokenizer.nextToken();
                                boolean bl = true;
                                if (string3.equalsIgnoreCase("item")) {
                                    bl = this.getResidence().updateFunctions(2, Integer.valueOf(stringTokenizer.nextToken()));
                                } else if (string3.equalsIgnoreCase("tele")) {
                                    bl = this.getResidence().updateFunctions(1, Integer.valueOf(stringTokenizer.nextToken()));
                                } else if (string3.equalsIgnoreCase("support")) {
                                    bl = this.getResidence().updateFunctions(6, Integer.valueOf(stringTokenizer.nextToken()));
                                }
                                if (!bl) {
                                    player.sendPacket((IStaticPacket)SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
                                } else {
                                    this.broadcastDecoInfo();
                                }
                            }
                            this.ai(player);
                        } else if (string3.equalsIgnoreCase("deco")) {
                            if (stringTokenizer.countTokens() >= 1) {
                                string3 = stringTokenizer.nextToken();
                                boolean bl = true;
                                if (string3.equalsIgnoreCase("platform")) {
                                    bl = this.getResidence().updateFunctions(8, Integer.valueOf(stringTokenizer.nextToken()));
                                } else if (string3.equalsIgnoreCase("curtain")) {
                                    bl = this.getResidence().updateFunctions(7, Integer.valueOf(stringTokenizer.nextToken()));
                                }
                                if (!bl) {
                                    player.sendPacket((IStaticPacket)SystemMsg.THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE);
                                } else {
                                    this.broadcastDecoInfo();
                                }
                            }
                            this.aj(player);
                        } else if (string3.equalsIgnoreCase("back")) {
                            this.showChatWindow(player, 0, new Object[0]);
                        } else {
                            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                            npcHtmlMessage.setFile("residence/manage.htm");
                            this.a(player, npcHtmlMessage);
                        }
                        return;
                    }
                    if (string2.equalsIgnoreCase("support")) {
                        if (!this.isHaveRigths(player, this.getPrivUseFunctions())) {
                            player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                            return;
                        }
                        this.setTarget(player);
                        if (string3.equals("")) {
                            return;
                        }
                        if (!this.getResidence().isFunctionActive(6)) {
                            return;
                        }
                        if (string3.startsWith("all")) {
                            for (Object[] objectArray : this.getResidence().getFunction(6).getBuffs()) {
                                Skill skill;
                                if (string3.equals("allM") && objectArray[1] == "W" || string3.equals("allW") && objectArray[1] == "M" || this.a((skill = (Skill)objectArray[0]).getId(), skill.getLevel(), player)) {
                                    continue;
                                }
                                break;
                            }
                        } else {
                            int n6 = Integer.parseInt(string3);
                            int n7 = 0;
                            if (stringTokenizer.countTokens() >= 1) {
                                n7 = Integer.parseInt(stringTokenizer.nextToken());
                            }
                            this.a(n6, n7, player);
                        }
                        this.onBypassFeedback(player, "functions support");
                        return;
                    }
                }
            }
        }
        super.onBypassFeedback(player, string);
    }

    private boolean a(int n, int n2, Player player) {
        Skill skill = SkillTable.getInstance().getInfo(n, n2);
        if (skill == null) {
            player.sendMessage("Invalid skill " + n);
            return true;
        }
        if (skill.getMpConsume() > this.getCurrentMp()) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
            npcHtmlMessage.setFile("residence/NeedCoolTime.htm");
            npcHtmlMessage.replace("%mp%", String.valueOf(Math.round(this.getCurrentMp())));
            this.a(player, npcHtmlMessage);
            return false;
        }
        this.altUseSkill(skill, player);
        return true;
    }

    private void a(Player player, NpcHtmlMessage npcHtmlMessage) {
        npcHtmlMessage.replace("%npcname%", HtmlUtils.htmlNpcName(this.getNpcId()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private void a(NpcHtmlMessage npcHtmlMessage, int n, String string, String string2) {
        boolean bl;
        boolean bl2 = bl = n == 3 || n == 4 || n == 5;
        if (this.getResidence().isFunctionActive(n)) {
            npcHtmlMessage.replace("%" + string + "%", String.valueOf(this.getResidence().getFunction(n).getLevel()) + (bl ? "%" : ""));
            npcHtmlMessage.replace("%" + string + "Price%", String.valueOf(this.getResidence().getFunction(n).getLease()));
            npcHtmlMessage.replace("%" + string + "Date%", TimeUtils.toSimpleFormat(this.getResidence().getFunction(n).getEndTimeInMillis()));
        } else {
            npcHtmlMessage.replace("%" + string + "%", "0");
            npcHtmlMessage.replace("%" + string + "Price%", "0");
            npcHtmlMessage.replace("%" + string + "Date%", "0");
        }
        if (this.getResidence().getFunction(n) != null && this.getResidence().getFunction(n).getLevels().size() > 0) {
            String string3 = "[<a action=\"bypass -h npc_%objectId%_manage " + string2 + " " + string + " 0\">Stop</a>]";
            for (int n2 : this.getResidence().getFunction(n).getLevels()) {
                string3 = string3 + "[<a action=\"bypass -h npc_%objectId%_manage " + string2 + " " + string + " " + n2 + "\">" + n2 + (bl ? "%" : "") + "</a>]";
            }
            npcHtmlMessage.replace("%" + string + "Manage%", string3);
        } else {
            npcHtmlMessage.replace("%" + string + "Manage%", "Not Available");
        }
    }

    private void ah(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile("residence/edit_recovery.htm");
        this.a(npcHtmlMessage, 5, "exp", "recovery");
        this.a(npcHtmlMessage, 3, "hp", "recovery");
        this.a(npcHtmlMessage, 4, "mp", "recovery");
        this.a(player, npcHtmlMessage);
    }

    private void ai(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile("residence/edit_other.htm");
        this.a(npcHtmlMessage, 1, "tele", "other");
        this.a(npcHtmlMessage, 6, "support", "other");
        this.a(npcHtmlMessage, 2, "item", "other");
        this.a(player, npcHtmlMessage);
    }

    private void aj(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile("residence/edit_deco.htm");
        this.a(npcHtmlMessage, 7, "curtain", "deco");
        this.a(npcHtmlMessage, 8, "platform", "deco");
        this.a(player, npcHtmlMessage);
    }

    protected boolean isHaveRigths(Player player, int n) {
        return player.getClan() != null && (player.getClanPrivileges() & n) == n;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player player, Creature creature) {
        // L2OFF standard: NPC de praça pública não envia decoração de Clan Hall para jogadores externos no spawn
        return super.addPacketList(player, creature);
    }
}

