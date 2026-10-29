/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public class FestivalMonsterInstance
extends MonsterInstance {
    protected int _bonusMultiplier = 1;

    public FestivalMonsterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this._hasRandomWalk = false;
    }

    public void setOfferingBonus(int n) {
        this._bonusMultiplier = n;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        List<Player> list = World.getAroundPlayers(this);
        if (list.isEmpty()) {
            return;
        }
        ArrayList<Player> arrayList = new ArrayList<Player>(9);
        for (Player player : list) {
            if (player.isDead()) continue;
            arrayList.add(player);
        }
        if (arrayList.isEmpty()) {
            return;
        }
        Player player = (Player)arrayList.get(Rnd.get(arrayList.size()));
        this.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 1);
    }

    @Override
    public void rollRewards(Map.Entry<RewardType, RewardList> entry, Creature creature, Creature creature2) {
        super.rollRewards(entry, creature, creature2);
        if (entry.getKey() != RewardType.RATED_GROUPED) {
            return;
        }
        if (!creature2.isPlayable()) {
            return;
        }
        Player player = creature2.getPlayer();
        Party party = player.getParty();
        if (party == null) {
            return;
        }
        Player player2 = party.getPartyLeader();
        if (player2 == null) {
            return;
        }
        ItemInstance itemInstance = ItemFunctions.createItem(5901);
        itemInstance.setCount(this._bonusMultiplier);
        player2.getInventory().addItem(itemInstance);
        player2.sendPacket((IStaticPacket)SystemMessage2.obtainItems(5901, this._bonusMultiplier, 0));
    }

    @Override
    public boolean isAggressive() {
        return true;
    }

    @Override
    public int getAggroRange() {
        return 1000;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}

