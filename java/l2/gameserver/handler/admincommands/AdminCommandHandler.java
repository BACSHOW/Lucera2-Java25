/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.handler.admincommands.impl.AdminAdmin;
import l2.gameserver.handler.admincommands.impl.AdminNpcDebug;
import l2.gameserver.handler.admincommands.impl.AdminAnnouncements;
import l2.gameserver.handler.admincommands.impl.AdminBan;
import l2.gameserver.handler.admincommands.impl.AdminCamera;
import l2.gameserver.handler.admincommands.impl.AdminCancel;
import l2.gameserver.handler.admincommands.impl.AdminChangeAccessLevel;
import l2.gameserver.handler.admincommands.impl.AdminClanHall;
import l2.gameserver.handler.admincommands.impl.AdminCreateItem;
import l2.gameserver.handler.admincommands.impl.AdminCursedWeapons;
import l2.gameserver.handler.admincommands.impl.AdminDelete;
import l2.gameserver.handler.admincommands.impl.AdminDisconnect;
import l2.gameserver.handler.admincommands.impl.AdminDoorControl;
import l2.gameserver.handler.admincommands.impl.AdminEditChar;
import l2.gameserver.handler.admincommands.impl.AdminEffects;
import l2.gameserver.handler.admincommands.impl.AdminEnchant;
import l2.gameserver.handler.admincommands.impl.AdminEvents;
import l2.gameserver.handler.admincommands.impl.AdminGeodata;
import l2.gameserver.handler.admincommands.impl.AdminGm;
import l2.gameserver.handler.admincommands.impl.AdminGmChat;
import l2.gameserver.handler.admincommands.impl.AdminHeal;
import l2.gameserver.handler.admincommands.impl.AdminHelpPage;
import l2.gameserver.handler.admincommands.impl.AdminIP;
import l2.gameserver.handler.admincommands.impl.AdminInstance;
import l2.gameserver.handler.admincommands.impl.AdminKill;
import l2.gameserver.handler.admincommands.impl.AdminLevel;
import l2.gameserver.handler.admincommands.impl.AdminMammon;
import l2.gameserver.handler.admincommands.impl.AdminManor;
import l2.gameserver.handler.admincommands.impl.AdminMenu;
import l2.gameserver.handler.admincommands.impl.AdminMonsterRace;
import l2.gameserver.handler.admincommands.impl.AdminMove;
import l2.gameserver.handler.admincommands.impl.AdminNochannel;
import l2.gameserver.handler.admincommands.impl.AdminOlympiad;
import l2.gameserver.handler.admincommands.impl.AdminPetition;
import l2.gameserver.handler.admincommands.impl.AdminPledge;
import l2.gameserver.handler.admincommands.impl.AdminPolymorph;
import l2.gameserver.handler.admincommands.impl.AdminQuests;
import l2.gameserver.handler.admincommands.impl.AdminReload;
import l2.gameserver.handler.admincommands.impl.AdminRepairChar;
import l2.gameserver.handler.admincommands.impl.AdminRes;
import l2.gameserver.handler.admincommands.impl.AdminRide;
import l2.gameserver.handler.admincommands.impl.AdminSS;
import l2.gameserver.handler.admincommands.impl.AdminScripts;
import l2.gameserver.handler.admincommands.impl.AdminServer;
import l2.gameserver.handler.admincommands.impl.AdminShop;
import l2.gameserver.handler.admincommands.impl.AdminShutdown;
import l2.gameserver.handler.admincommands.impl.AdminSkill;
import l2.gameserver.handler.admincommands.impl.AdminSpawn;
import l2.gameserver.handler.admincommands.impl.AdminTarget;
import l2.gameserver.handler.admincommands.impl.AdminTeleport;
import l2.gameserver.handler.admincommands.impl.AdminTest;
import l2.gameserver.handler.admincommands.impl.AdminZone;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.utils.Log;

public class AdminCommandHandler
extends AbstractHolder {
    private static final AdminCommandHandler yd = new AdminCommandHandler();
    private Map<String, IAdminCommandHandler> ye = new HashMap<String, IAdminCommandHandler>();

    public static AdminCommandHandler getInstance() {
        return yd;
    }

    private AdminCommandHandler() {
        this.registerAdminCommandHandler(new AdminAdmin());
        this.registerAdminCommandHandler(new AdminNpcDebug());
        this.registerAdminCommandHandler(new AdminAnnouncements());
        this.registerAdminCommandHandler(new AdminBan());
        this.registerAdminCommandHandler(new AdminCamera());
        this.registerAdminCommandHandler(new AdminCancel());
        this.registerAdminCommandHandler(new AdminChangeAccessLevel());
        this.registerAdminCommandHandler(new AdminClanHall());
        this.registerAdminCommandHandler(new AdminCreateItem());
        this.registerAdminCommandHandler(new AdminCursedWeapons());
        this.registerAdminCommandHandler(new AdminDelete());
        this.registerAdminCommandHandler(new AdminDisconnect());
        this.registerAdminCommandHandler(new AdminDoorControl());
        this.registerAdminCommandHandler(new AdminEditChar());
        this.registerAdminCommandHandler(new AdminEffects());
        this.registerAdminCommandHandler(new AdminEnchant());
        this.registerAdminCommandHandler(new AdminEvents());
        this.registerAdminCommandHandler(new AdminGeodata());
        this.registerAdminCommandHandler(new AdminGm());
        this.registerAdminCommandHandler(new AdminGmChat());
        this.registerAdminCommandHandler(new AdminHeal());
        this.registerAdminCommandHandler(new AdminHelpPage());
        this.registerAdminCommandHandler(new AdminInstance());
        this.registerAdminCommandHandler(new AdminIP());
        this.registerAdminCommandHandler(new AdminLevel());
        this.registerAdminCommandHandler(new AdminMammon());
        this.registerAdminCommandHandler(new AdminManor());
        this.registerAdminCommandHandler(new AdminMenu());
        this.registerAdminCommandHandler(new AdminMonsterRace());
        this.registerAdminCommandHandler(new AdminMove());
        this.registerAdminCommandHandler(new AdminNochannel());
        this.registerAdminCommandHandler(new AdminOlympiad());
        this.registerAdminCommandHandler(new AdminPetition());
        this.registerAdminCommandHandler(new AdminPledge());
        this.registerAdminCommandHandler(new AdminPolymorph());
        this.registerAdminCommandHandler(new AdminQuests());
        this.registerAdminCommandHandler(new AdminReload());
        this.registerAdminCommandHandler(new AdminRepairChar());
        this.registerAdminCommandHandler(new AdminRes());
        this.registerAdminCommandHandler(new AdminRide());
        this.registerAdminCommandHandler(new AdminServer());
        this.registerAdminCommandHandler(new AdminShop());
        this.registerAdminCommandHandler(new AdminShutdown());
        this.registerAdminCommandHandler(new AdminSkill());
        this.registerAdminCommandHandler(new AdminScripts());
        this.registerAdminCommandHandler(new AdminSpawn());
        this.registerAdminCommandHandler(new AdminSS());
        this.registerAdminCommandHandler(new AdminTarget());
        this.registerAdminCommandHandler(new AdminTeleport());
        this.registerAdminCommandHandler(new AdminZone());
        this.registerAdminCommandHandler(new AdminKill());
        this.registerAdminCommandHandler(new AdminTest());
    }

    public void registerAdminCommandHandler(IAdminCommandHandler iAdminCommandHandler) {
        for (Enum enum_ : iAdminCommandHandler.getAdminCommandEnum()) {
            this.ye.put(enum_.toString().toLowerCase(), iAdminCommandHandler);
        }
    }

    public IAdminCommandHandler getAdminCommandHandler(String string) {
        String string2 = string;
        if (string.indexOf(" ") != -1) {
            string2 = string.substring(0, string.indexOf(" "));
        }
        return this.ye.get(string2);
    }

    public void useAdminCommandHandler(Player player, String string) {
        if (!player.isGM() && !player.getPlayerAccess().CanUseGMCommand) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess", player, new Object[0]).addString(string));
            return;
        }
        String[] stringArray = string.split(" ");
        IAdminCommandHandler iAdminCommandHandler = this.ye.get(stringArray[0]);
        if (iAdminCommandHandler != null) {
            boolean bl = false;
            try {
                for (Enum enum_ : iAdminCommandHandler.getAdminCommandEnum()) {
                    if (!enum_.toString().equalsIgnoreCase(stringArray[0])) continue;
                    bl = iAdminCommandHandler.useAdminCommand(enum_, stringArray, string, player);
                    break;
                }
            }
            catch (Exception exception) {
                this.error("", exception);
            }
            Log.LogCommand(player, player.getTarget(), string, bl);
        }
    }

    @Override
    public void process() {
    }

    @Override
    public int size() {
        return this.ye.size();
    }

    @Override
    public void clear() {
        this.ye.clear();
    }

    public Set<String> getAllCommands() {
        return this.ye.keySet();
    }
}

