/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.HtmlUtils;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.tuple.Pair;
import services.CommandClassMaster;

public final class ClassMasterInstance
extends MerchantInstance {
    private Map<ClassId, List<CommandClassMaster.ClassMasterPath>> axA = CommandClassMaster.parseConfig(Config.CLASS_MASTERS_CLASSES);

    public ClassMasterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
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

    private void b(Player player, List<CommandClassMaster.ClassMasterPath> list) {
        if (!this.f(player, false) || list == null || list.isEmpty()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            Object object;
            CommandClassMaster.ClassMasterPath classMasterPath = list.get(i);
            if (classMasterPath.getAvailableClassIds().isEmpty()) continue;
            String string = StringHolder.getInstance().getNotNull(player, "scripts.services.NpcClassMaster.pathHtml");
            StringBuilder stringBuilder2 = new StringBuilder();
            for (ClassId classId : classMasterPath.getAvailableClassIds()) {
                stringBuilder2.append("<a action=\"bypass -h npc_").append(this.getObjectId()).append("_change_class ").append(i).append(' ').append(classId.getId()).append("\">").append(HtmlUtils.htmlClassName(classId.getId(), player)).append("</a>").append("<br>");
            }
            string = string.replace("%class_master_list%", stringBuilder2.toString());
            StringBuilder stringBuilder3 = new StringBuilder();
            for (Pair<Integer, Long> pair : classMasterPath.getPrice()) {
                Object object2 = ItemHolder.getInstance().getTemplate(pair.getKey());
                object = StringHolder.getInstance().getNotNull(player, "scripts.services.NpcClassMaster.requiredItem");
                object = ((String)object).replace("%item_id%", String.valueOf(pair.getKey()));
                object = ((String)object).replace("%item_name%", ((ItemTemplate)object2).getName());
                object = ((String)object).replace("%item_count%", String.valueOf(pair.getValue()));
                stringBuilder3.append((String)object);
            }
            string = string.replace("%required_items_list%", stringBuilder3.toString());
            StringBuilder object42 = new StringBuilder();
            for (Object object2 : classMasterPath.getReward()) {
                object = ItemHolder.getInstance().getTemplate((Integer)((Pair)object2).getKey());
                String string2 = StringHolder.getInstance().getNotNull(player, "scripts.services.NpcClassMaster.rewardItem");
                string2 = string2.replace("%item_id%", String.valueOf(((Pair)object2).getKey()));
                string2 = string2.replace("%item_name%", ((ItemTemplate)object).getName());
                string2 = string2.replace("%item_count%", String.valueOf(((Pair)object2).getValue()));
                object42.append(string2);
            }
            string = string.replace("%reward_items_list%", object42.length() > 0 ? object42.toString() : " ");
            stringBuilder.append(string);
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile("scripts/services/npc_class_master.htm");
        npcHtmlMessage.replace("%list%", stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        if (!ClassMasterInstance.canBypassCheck(player, this)) {
            return;
        }
        List<CommandClassMaster.ClassMasterPath> list = this.axA.get((Object)player.getClassId());
        if (list == null || list.isEmpty() || !list.stream().map(CommandClassMaster.ClassMasterPath::getAvailableClassIds).flatMap(Collection::stream).findAny().isPresent()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this).setFile("scripts/services/npc_class_master_nothingtoup.htm"));
            return;
        }
        if (list.stream().mapToInt(CommandClassMaster.ClassMasterPath::getMinPlayerLevel).min().orElse(Integer.MAX_VALUE) > player.getLevel()) {
            Functions.show(new CustomMessage(CommandClassMaster.getMinPlayerLevelForClassIdMessageAddress(player.getClassId().getLevel() + 1), player, new Object[0]), player);
            return;
        }
        this.b(player, list);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!ClassMasterInstance.canBypassCheck(player, this) || !this.f(player, true)) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string);
        if (stringTokenizer.nextToken().equals("change_class")) {
            CommandClassMaster.ClassMasterPath classMasterPath;
            ClassId classId = player.getClassId();
            List<CommandClassMaster.ClassMasterPath> list = this.axA.get((Object)classId);
            int n = Integer.parseInt(stringTokenizer.nextToken());
            int n2 = Integer.parseInt(stringTokenizer.nextToken());
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
            Log.service("NpcClassMaster", player, " got a classId " + (Object)((Object)classId2));
        } else {
            super.onBypassFeedback(player, string);
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
}

