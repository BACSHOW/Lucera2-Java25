/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.clanhall.ManagerInstance;

public class AuctionedManagerInstance
extends ManagerInstance {
    public AuctionedManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void setDialogs() {
        this._mainDialog = this.getTemplate().getAIParams().getString("main_dialog", "residence2/clanhall/black001.htm");
        this._failDialog = this.getTemplate().getAIParams().getString("fail_dialog", "residence2/clanhall/black002.htm");
    }

    @Override
    protected int getCond(Player player) {
        Residence residence = this.getResidence();
        Clan clan = residence.getOwner();
        if (clan != null && player.getClan() == clan) {
            return 2;
        }
        return 0;
    }

    @Override
    protected boolean canInteractWithKarmaPlayer() {
        return true;
    }
}

