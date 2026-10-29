/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.GMHennaInfo;
import l2.gameserver.network.l2.s2c.GMViewCharacterInfo;
import l2.gameserver.network.l2.s2c.GMViewItemList;
import l2.gameserver.network.l2.s2c.GMViewPledgeInfo;
import l2.gameserver.network.l2.s2c.GMViewQuestInfo;
import l2.gameserver.network.l2.s2c.GMViewSkillInfo;
import l2.gameserver.network.l2.s2c.GMViewWarehouseWithdrawList;

public class RequestGMCommand
extends L2GameClientPacket {
    private String abX;
    private int aaG;

    @Override
    protected void readImpl() {
        this.abX = this.readS();
        this.aaG = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        Player player2 = World.getPlayer(this.abX);
        if (player == null || player2 == null) {
            return;
        }
        if (!player.getPlayerAccess().CanViewChar) {
            return;
        }
        switch (this.aaG) {
            case 1: {
                player.sendPacket((IStaticPacket)new GMViewCharacterInfo(player2));
                player.sendPacket((IStaticPacket)new GMHennaInfo(player2));
                break;
            }
            case 2: {
                if (player2.getClan() == null) break;
                player.sendPacket((IStaticPacket)new GMViewPledgeInfo(player2));
                break;
            }
            case 3: {
                player.sendPacket((IStaticPacket)new GMViewSkillInfo(player2));
                break;
            }
            case 4: {
                player.sendPacket((IStaticPacket)new GMViewQuestInfo(player2));
                break;
            }
            case 5: {
                ItemInstance[] itemInstanceArray = player2.getInventory().getItems();
                int n = 0;
                player.sendPacket((IStaticPacket)new GMViewItemList(player2, itemInstanceArray, itemInstanceArray.length - n));
                break;
            }
            case 6: {
                player.sendPacket((IStaticPacket)new GMViewWarehouseWithdrawList(player2));
            }
        }
    }
}

