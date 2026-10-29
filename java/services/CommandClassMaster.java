/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.listener.actor.player.OnGainExpSpListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class CommandClassMaster
extends Functions
implements IVoicedCommandHandler,
OnGainExpSpListener,
ScriptFile {
    private static final CommandClassMaster bWO = new CommandClassMaster();
    private static final String SCRIPT_BYPASS_CLASS = "scripts_" + CommandClassMaster.class.getName();
    private static final String bWP = "cmd_class_master_show_cnt";
    private static Map<ClassId, List<ClassMasterPath>> bWQ = Collections.emptyMap();
    private static String[] aO = ArrayUtils.EMPTY_STRING_ARRAY;

    public static CommandClassMaster getInstance() {
        return bWO;
    }

    private static List<ClassMasterPath> b(ClassId classId) {
        return bWQ.getOrDefault((Object)classId, Collections.emptyList());
    }

    public static String getMinPlayerLevelForClassIdMessageAddress(int n) {
        switch (n) {
            case 2: {
                return "ClassMaster.Need20Level";
            }
            case 3: {
                return "ClassMaster.Need40Level";
            }
            case 4: {
                return "ClassMaster.Need76Level";
            }
        }
        return "ClassMaster.NothingToUp";
    }

    private static List<Pair<Integer, Long>> G(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ",");
        LinkedList<Pair<Integer, Long>> linkedList = new LinkedList<Pair<Integer, Long>>();
        while (stringTokenizer.hasMoreTokens()) {
            String string2 = stringTokenizer.nextToken().trim();
            if (string2.isEmpty()) continue;
            int n = string2.indexOf(":");
            if (n < 0) {
                throw new RuntimeException("Can't parse items \"" + string + "\"");
            }
            int n2 = Integer.parseInt(string2.substring(0, n).trim());
            long l = Long.parseLong(string2.substring(n + 1).trim());
            linkedList.add(Pair.of(n2, l));
        }
        return Collections.unmodifiableList(linkedList);
    }

    private boolean f(Player player, boolean bl) {
        if (player == null || player.isLogoutStarted() || player.isOutOfControl() || player.isDead() || player.isInDuel() || player.isAlikeDead() || player.isOlyParticipant() || player.isFlying() || player.isSitting() || player.getTeam() != TeamType.NONE || player.isInStoreMode() || player.entering) {
            if (bl && player != null && !player.entering) {
                player.sendMessage(new CustomMessage("common.TryLater", player, new Object[0]));
            }
            return false;
        }
        return true;
    }

    private void b(Player player, List<ClassMasterPath> list) {
        if (!this.f(player, false) || list == null || list.isEmpty()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = false;
        for (int i = 0; i < list.size(); ++i) {
            Object object;
            ClassMasterPath classMasterPath = list.get(i);
            if (classMasterPath.getAvailableClassIds().isEmpty()) continue;
            String string = StringHolder.getInstance().getNotNull(player, "scripts.services.CommandClassMaster.pathHtml");
            StringBuilder stringBuilder2 = new StringBuilder();
            for (ClassId classId : classMasterPath.getAvailableClassIds()) {
                stringBuilder2.append("<button width=140 height=21 back=\"l2ui_ch3.msnbutton\" fore=\"l2ui_ch3.msnbutton\" ").append("action=\"bypass -h ").append(SCRIPT_BYPASS_CLASS).append(":classMaster ").append(i).append(' ').append(classId.getId()).append("\" ").append("value=\"").append(HtmlUtils.htmlClassName(classId.getId(), player)).append("\">").append("<br1>");
            }
            string = string.replace("%class_master_list%", stringBuilder2.toString());
            StringBuilder stringBuilder3 = new StringBuilder();
            for (Pair<Integer, Long> pair : classMasterPath.getPrice()) {
                Object object2 = ItemHolder.getInstance().getTemplate(pair.getKey());
                object = StringHolder.getInstance().getNotNull(player, "scripts.services.CommandClassMaster.requiredItem");
                object = ((String)object).replace("%item_id%", String.valueOf(pair.getKey()));
                object = ((String)object).replace("%item_name%", ((ItemTemplate)object2).getName());
                object = ((String)object).replace("%item_count%", String.valueOf(pair.getValue()));
                stringBuilder3.append((String)object);
            }
            string = string.replace("%required_items_list%", stringBuilder3.toString());
            StringBuilder object42 = new StringBuilder();
            for (Object object2 : classMasterPath.getReward()) {
                object = ItemHolder.getInstance().getTemplate((Integer)((Pair)object2).getKey());
                String string2 = StringHolder.getInstance().getNotNull(player, "scripts.services.CommandClassMaster.rewardItem");
                string2 = string2.replace("%item_id%", String.valueOf(((Pair)object2).getKey()));
                string2 = string2.replace("%item_name%", ((ItemTemplate)object).getName());
                string2 = string2.replace("%item_count%", String.valueOf(((Pair)object2).getValue()));
                object42.append(string2);
            }
            string = string.replace("%reward_items_list%", object42.length() > 0 ? object42.toString() : " ");
            stringBuilder.append(string);
            bl = true;
        }
        if (!bl) {
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, null);
        npcHtmlMessage.setFile("scripts/services/command_class_master.htm");
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private void ax(Player player) {
        List<ClassMasterPath> list = CommandClassMaster.b(player.getClassId());
        if (list == null || list.isEmpty() || !list.stream().map(ClassMasterPath::getAvailableClassIds).flatMap(Collection::stream).findAny().isPresent()) {
            return;
        }
        this.b(player, list);
    }

    public void classMaster() {
        this.classMaster(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public void classMaster(String ... stringArray) {
        int n;
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.COMMAND_CLASS_MASTER_ENABLED) {
            player.sendMessage(new CustomMessage("common.Disabled", player, new Object[0]));
            return;
        }
        if (!this.f(player, true)) {
            return;
        }
        ClassId classId = player.getClassId();
        List<ClassMasterPath> list = CommandClassMaster.b(classId);
        if (list == null || list.isEmpty()) {
            CommandClassMaster.show(new CustomMessage("ClassMaster.NothingToUp", player, new Object[0]), player);
            return;
        }
        if (list.stream().mapToInt(ClassMasterPath::getMinPlayerLevel).min().orElse(Integer.MAX_VALUE) > player.getLevel()) {
            player.sendMessage(new CustomMessage(CommandClassMaster.getMinPlayerLevelForClassIdMessageAddress(classId.getLevel() + 1), player, new Object[0]));
            return;
        }
        if (stringArray.length > 1) {
            ClassMasterPath classMasterPath;
            n = Integer.parseInt(stringArray[0]);
            int n2 = Integer.parseInt(stringArray[1]);
            if (list == null || list.isEmpty() || n < 0 || n >= list.size() || (classMasterPath = list.get(n)) == null || classMasterPath.getAvailableClassIds() == null || classMasterPath.getAvailableClassIds().isEmpty()) {
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/npc_class_master_nothingtoup.htm"));
                return;
            }
            ClassId classId2 = null;
            for (ClassId object : classMasterPath.getAvailableClassIds()) {
                if (object.getId() != n2) continue;
                classId2 = object;
                break;
            }
            if (classId2 == null) {
                return;
            }
            for (Pair pair : classMasterPath.getPrice()) {
                if (ItemFunctions.getItemCount(player, (Integer)pair.getKey()) >= (Long)pair.getValue()) continue;
                if ((Integer)pair.getKey() == 57) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                } else {
                    player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                }
                return;
            }
            long l = 0L;
            long l2 = 0L;
            for (Pair<Integer, Long> pair : classMasterPath.getReward()) {
                ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(pair.getKey());
                l += (long)itemTemplate.getWeight() * pair.getValue();
                l2 += itemTemplate.isStackable() ? 1L : pair.getValue();
            }
            if (!player.getInventory().validateWeight(l)) {
                player.sendPacket((IStaticPacket)Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                return;
            }
            if (!player.getInventory().validateCapacity(l2)) {
                player.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                return;
            }
            for (Pair<Integer, Long> pair : classMasterPath.getPrice()) {
                if (ItemFunctions.removeItem(player, pair.getKey(), pair.getValue(), true) >= pair.getValue()) continue;
                return;
            }
            this.a(player, classId2);
            for (Pair<Integer, Long> pair : classMasterPath.getReward()) {
                ItemFunctions.addItem((Playable)player, pair.getKey(), (long)pair.getValue(), true);
            }
            Log.service("CommandClassMaster", player, " got a classId " + (Object)((Object)classId2));
        }
        if ((n = CommandClassMaster.ag(player.getClassId().getLevel() + 1)) > 0 && n <= player.getLevel()) {
            this.ax(player);
        } else if (n < 0) {
            CommandClassMaster.sendMessage(new CustomMessage("ClassMaster.NothingToUp", player, new Object[0]), player);
        } else if (n <= 20) {
            CommandClassMaster.sendMessage(new CustomMessage("ClassMaster.Need20Level", player, new Object[0]), player);
        } else if (n <= 40) {
            CommandClassMaster.sendMessage(new CustomMessage("ClassMaster.Need40Level", player, new Object[0]), player);
        } else if (n <= 76) {
            CommandClassMaster.sendMessage(new CustomMessage("ClassMaster.Need76Level", player, new Object[0]), player);
        }
    }

    private void a(Player player, ClassId classId) {
        if (player.getClassId().getLevel() == 3) {
            player.sendPacket((IStaticPacket)Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
        } else {
            player.sendPacket((IStaticPacket)Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS);
        }
        player.setClassId(classId.getId(), false, false);
        player.broadcastCharInfo();
        player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
        player.broadcastPacket(new MagicSkillUse(player, player, 4339, 1, 0, 0L));
    }

    @Override
    public void onGainExpSp(Player player, long l, long l2) {
        if (!Config.COMMAND_CLASS_MASTER_ENABLED || Config.COMMAND_CLASS_POPUP_LIMIT == 0) {
            return;
        }
        if (!this.f(player, false)) {
            return;
        }
        List<ClassMasterPath> list = CommandClassMaster.b(player.getClassId());
        if (list == null || list.isEmpty() || list.stream().mapToInt(ClassMasterPath::getMinPlayerLevel).min().orElse(Integer.MAX_VALUE) > player.getLevel()) {
            return;
        }
        if (Config.COMMAND_CLASS_POPUP_LIMIT == -1) {
            this.ax(player);
        } else if (Config.COMMAND_CLASS_POPUP_LIMIT > 0) {
            int n = player.getVarInt(bWP, 0);
            int n2 = n & 0xFF;
            int n3 = n >> 8;
            if (n2 != player.getActiveClassId() || n3 < Config.COMMAND_CLASS_POPUP_LIMIT) {
                player.setVar(bWP, n3 + 1 << 8 | player.getActiveClassId() & 0xFF, -1L);
                this.ax(player);
            }
        }
    }

    @Override
    public void onLoad() {
        if (Config.COMMAND_CLASS_MASTER_ENABLED) {
            bWQ = CommandClassMaster.parseConfig(Config.COMMAND_CLASS_MASTER_CLASSES);
            if (Config.COMMAND_CLASS_POPUP_LIMIT != 0) {
                PlayerListenerList.addGlobal(CommandClassMaster.getInstance());
            }
            if (Config.COMMAND_CLASS_MASTER_VOICE_COMMANDS.length > 0) {
                aO = Config.COMMAND_CLASS_MASTER_VOICE_COMMANDS;
                VoicedCommandHandler.getInstance().registerVoicedCommandHandler(bWO);
            }
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMAND_CLASS_MASTER_ENABLED) {
            bWQ = CommandClassMaster.parseConfig(Config.COMMAND_CLASS_MASTER_CLASSES);
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        for (String string3 : aO) {
            if (string3 == null || string3.isEmpty() || !string3.equalsIgnoreCase(string)) continue;
            Scripts.getInstance().callScripts(player, CommandClassMaster.class.getName(), "classMaster");
            return true;
        }
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        if (!Config.COMMAND_CLASS_MASTER_ENABLED) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return Config.COMMAND_CLASS_MASTER_VOICE_COMMANDS;
    }

    public static Map<ClassId, List<ClassMasterPath>> parseConfig(String string) {
        if (StringUtils.isEmpty(string)) {
            return Collections.emptyMap();
        }
        TreeMap<ClassId, List<ClassMasterPath>> treeMap = new TreeMap<ClassId, List<ClassMasterPath>>();
        StringTokenizer stringTokenizer = new StringTokenizer(string.trim(), ";");
        while (stringTokenizer.hasMoreTokens()) {
            int n;
            String string2 = stringTokenizer.nextToken().trim();
            int n2 = string2.indexOf("-");
            List<Pair<Integer, Long>> list3 = Collections.emptyList();
            List<Pair<Integer, Long>> list4 = Collections.emptyList();
            if (n2 < 0) {
                n = Integer.parseInt(string2.trim());
            } else {
                n = Integer.parseInt(string2.substring(0, n2).trim());
                String string3 = string2.substring(n2 + 1).trim();
                int n3 = string3.indexOf("/");
                if (n3 < 0) {
                    list3 = CommandClassMaster.G(string3);
                } else {
                    list3 = CommandClassMaster.G(string3.substring(0, n3).trim());
                    list4 = CommandClassMaster.G(string3.substring(n3 + 1).trim());
                }
            }
            for (ClassId classId : ClassId.VALUES) {
                if (classId.getLevel() != n) continue;
                LinkedList<ClassId> linkedList = new LinkedList<ClassId>();
                for (ClassId classId2 : ClassId.VALUES) {
                    if (!classId2.childOf(classId) || classId.getLevel() + 1 != classId2.getLevel()) continue;
                    linkedList.add(classId2);
                }
                treeMap.merge(classId, Collections.singletonList(new ClassMasterPath(linkedList, classId, list3, list4)),
                        (existing, added) -> {
                            ArrayList<ClassMasterPath> merged = new ArrayList<ClassMasterPath>(existing.size() + added.size());
                            merged.addAll(existing);
                            merged.addAll(added);
                            return merged;
                        });
            }
        }
        return Collections.unmodifiableMap(treeMap);
    }

    private static int ag(int n) {
        switch (n) {
            case 1: {
                return 1;
            }
            case 2: {
                return 20;
            }
            case 3: {
                return 40;
            }
            case 4: {
                return 76;
            }
        }
        return -1;
    }

    public static class ClassMasterPath {
        private final List<ClassId> bWR;
        private final ClassId bWS;
        private final List<Pair<Integer, Long>> bWT;
        private final List<Pair<Integer, Long>> bWU;

        public ClassMasterPath(List<ClassId> list, ClassId classId, List<Pair<Integer, Long>> list2, List<Pair<Integer, Long>> list3) {
            this.bWR = list;
            this.bWS = classId;
            this.bWT = list2;
            this.bWU = list3;
        }

        public int getMinPlayerLevel() {
            return CommandClassMaster.ag(this.bWS.getLevel() + 1);
        }

        public List<ClassId> getAvailableClassIds() {
            return this.bWR;
        }

        public ClassId getFromClassId() {
            return this.bWS;
        }

        public List<Pair<Integer, Long>> getPrice() {
            return this.bWT;
        }

        public List<Pair<Integer, Long>> getReward() {
            return this.bWU;
        }
    }
}

