/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.GmListTable;

public final class RequestPetitionCancel
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (PetitionManager.getInstance().isPlayerInConsultation(player)) {
            if (player.isGM()) {
                PetitionManager.getInstance().endActivePetition(player);
            } else {
                player.sendPacket((IStaticPacket)new SystemMessage(407));
            }
        } else if (PetitionManager.getInstance().isPlayerPetitionPending(player)) {
            if (PetitionManager.getInstance().cancelActivePetition(player)) {
                int n = Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPlayerTotalPetitionCount(player);
                player.sendPacket((IStaticPacket)new SystemMessage(736).addString(String.valueOf(n)));
                String string = player.getName() + " has canceled a pending petition.";
                GmListTable.broadcastToGMs(new Say2(player.getObjectId(), ChatType.HERO_VOICE, "Petition System", string));
            } else {
                player.sendPacket((IStaticPacket)new SystemMessage(393));
            }
        } else {
            player.sendPacket((IStaticPacket)new SystemMessage(738));
        }
    }
}

