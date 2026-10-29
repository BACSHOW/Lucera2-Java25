/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates;

import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.CommandChannel;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.ItemFunctions;

public enum InstantZoneEntryType {
    SOLO{

        @Override
        public boolean canEnter(Player player, InstantZone instantZone) {
            if (player.isInParty()) {
                player.sendMessage(new CustomMessage("A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA", player, new Object[0]));
                return false;
            }
            CustomMessage customMessage = InstantZoneEntryType.a(player, instantZone);
            if (customMessage != null) {
                player.sendMessage(customMessage);
                return false;
            }
            return true;
        }

        @Override
        public boolean canReEnter(Player player, InstantZone instantZone) {
            if (player.isCursedWeaponEquipped() || player.isInFlyingTransform()) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]));
                return false;
            }
            return true;
        }
    }
    ,
    PARTY{

        @Override
        public boolean canEnter(Player player, InstantZone instantZone) {
            Party party = player.getParty();
            if (party == null) {
                player.sendMessage(new CustomMessage("YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER", player, new Object[0]));
                return false;
            }
            if (!party.isLeader(player)) {
                player.sendMessage(new CustomMessage("ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER", player, new Object[0]));
                return false;
            }
            if (party.getMemberCount() < instantZone.getMinParty()) {
                player.sendMessage(new CustomMessage("YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE", player, new Object[0]).addNumber(instantZone.getMinParty()));
                return false;
            }
            if (party.getMemberCount() > instantZone.getMaxParty()) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
                return false;
            }
            for (Player player2 : party.getPartyMembers()) {
                if (!player.isInRange(player2, 500L)) {
                    for (Player player3 : player2) {
                        player3.sendMessage(new CustomMessage("C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED", player3, player2));
                    }
                    return false;
                }
                CustomMessage customMessage = InstantZoneEntryType.a(player2, instantZone);
                if (customMessage == null) continue;
                player.sendMessage(customMessage);
                return false;
            }
            return true;
        }

        @Override
        public boolean canReEnter(Player player, InstantZone instantZone) {
            Party party = player.getParty();
            if (party == null) {
                player.sendMessage(new CustomMessage("YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER", player, new Object[0]));
                return false;
            }
            if (party.getMemberCount() > instantZone.getMaxParty()) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
                return false;
            }
            if (player.isCursedWeaponEquipped() || player.isInFlyingTransform()) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]));
                return false;
            }
            return true;
        }
    }
    ,
    COMMAND_CHANNEL{

        @Override
        public boolean canEnter(Player player, InstantZone instantZone) {
            Party party = player.getParty();
            if (party == null || party.getCommandChannel() == null) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL", player, new Object[0]));
                return false;
            }
            CommandChannel commandChannel = party.getCommandChannel();
            if (commandChannel.getMemberCount() < instantZone.getMinParty()) {
                player.sendMessage(new CustomMessage("YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE", player, new Object[0]).addNumber(instantZone.getMinParty()));
                return false;
            }
            if (commandChannel.getMemberCount() > instantZone.getMaxParty()) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
                return false;
            }
            for (Player player2 : commandChannel) {
                if (!player.isInRange(player2, 500L)) {
                    for (Player player3 : commandChannel) {
                        player3.sendMessage(new CustomMessage("C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED", player3, player2));
                    }
                    return false;
                }
                CustomMessage customMessage = InstantZoneEntryType.a(player2, instantZone);
                if (customMessage == null) continue;
                player.sendMessage(customMessage);
                return false;
            }
            return true;
        }

        @Override
        public boolean canReEnter(Player player, InstantZone instantZone) {
            Party party = player.getParty();
            if (party == null || party.getCommandChannel() == null) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL", player, new Object[0]));
                return false;
            }
            CommandChannel commandChannel = party.getCommandChannel();
            if (commandChannel.getMemberCount() > instantZone.getMaxParty()) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT", player, new Object[0]));
                return false;
            }
            if (player.isCursedWeaponEquipped() || player.isInFlyingTransform()) {
                player.sendMessage(new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]));
                return false;
            }
            return true;
        }
    };


    public abstract boolean canEnter(Player var1, InstantZone var2);

    public abstract boolean canReEnter(Player var1, InstantZone var2);

    private static CustomMessage a(Player player, InstantZone instantZone) {
        Quest quest;
        QuestState questState;
        if (player.getActiveReflection() != null) {
            return new CustomMessage("YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON", player, new Object[0]);
        }
        if (player.getLevel() < instantZone.getMinLevel() || player.getLevel() > instantZone.getMaxLevel()) {
            return new CustomMessage("C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY", player, new Object[0]).addCharName(player);
        }
        if (player.isCursedWeaponEquipped() || player.isInFlyingTransform()) {
            return new CustomMessage("YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS", player, new Object[0]);
        }
        if (instantZone.getMinutesToNextEntrance(player) > 0) {
            return new CustomMessage("C1_MAY_NOT_REENTER_YET", player, new Object[0]).addCharName(player);
        }
        if (instantZone.getRemovedItemId() > 0 && instantZone.getRemovedItemNecessity() && ItemFunctions.getItemCount(player, instantZone.getRemovedItemId()) < 1L) {
            return new CustomMessage("C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED", player, new Object[0]).addCharName(player);
        }
        if (instantZone.getRequiredQuestId() > 0 && ((questState = player.getQuestState((quest = QuestManager.getQuest(instantZone.getRequiredQuestId())).getClass())) == null || questState.getState() != 2)) {
            return new CustomMessage("C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED", player, new Object[0]).addCharName(player);
        }
        return null;
    }
}

