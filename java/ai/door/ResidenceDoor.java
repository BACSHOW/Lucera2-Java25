/*
 * Decompiled with CFR 0.152.
 */
package ai.door;

import l2.gameserver.ai.DoorAI;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;

public class ResidenceDoor
extends DoorAI {
    public ResidenceDoor(DoorInstance doorInstance) {
        super(doorInstance);
    }

    @Override
    public void onEvtTwiceClick(final Player player) {
        final DoorInstance doorInstance = this.getActor();
        Object r = ResidenceHolder.getInstance().getResidence(doorInstance.getTemplate().getAIParams().getInteger("residence_id"));
        if (((Residence)r).getOwner() != null && player.getClan() != null && player.getClan() == ((Residence)r).getOwner() && (player.getClanPrivileges() & 0x8000) == 32768) {
            SystemMsg systemMsg = doorInstance.isOpen() ? SystemMsg.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE : SystemMsg.WOULD_YOU_LIKE_TO_OPEN_THE_GATE;
            player.ask(new ConfirmDlg(systemMsg, 0), new OnAnswerListener(){

                @Override
                public void sayYes() {
                    if (doorInstance.isOpen()) {
                        doorInstance.closeMe(player, true);
                    } else {
                        doorInstance.openMe(player, true);
                    }
                }

                @Override
                public void sayNo() {
                }
            });
        }
    }
}

