package l2.gameserver.network.l2.fermata.c2s;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardGroup;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.fermata.FermataClientPacket;
import l2.gameserver.network.l2.fermata.s2c.NpcDrops;
import l2.gameserver.network.l2.fermata.s2c.NpcDrops.DropGroup;
import l2.gameserver.network.l2.fermata.s2c.NpcDrops.DropItem;
import l2.gameserver.templates.npc.NpcTemplate;

public class RequestFermataNpcDrops extends FermataClientPacket {

    public static final int SUB_OPCODE = 0x0004;

    private int _npcId;

    @Override
    protected void readImpl() {
        _npcId = readD();
    }

    @Override
    protected void runImpl() {
        NpcTemplate template = NpcHolder.getInstance().getTemplate(_npcId);
        if (template == null) {
            getClient().sendPacket(new NpcDrops(_npcId, null));
            return;
        }

        List<DropGroup> groups = new ArrayList<>();
        Map<RewardType, RewardList> rewards = template.getRewards();
        if (rewards != null) {
            for (Map.Entry<RewardType, RewardList> entry : rewards.entrySet()) {
                int kind = entry.getKey() == RewardType.SWEEP ? 0 : 2;
                for (RewardGroup rg : entry.getValue()) {
                    List<DropItem> items = new ArrayList<>();
                    for (RewardData rd : rg.getItems()) {
                        items.add(new DropItem(rd.getItemId(), (int) rd.getMinDrop(), (int) rd.getMaxDrop(), (int) rd.getChance()));
                    }
                    groups.add(new DropGroup(kind, (int) rg.getChance(), 1, items));
                }
            }
        }

        getClient().sendPacket(new NpcDrops(_npcId, groups));
    }
}
