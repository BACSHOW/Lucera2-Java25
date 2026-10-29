/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import gnu.trove.TIntHashSet;
import handler.items.ScriptItemHandler;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;

public class ItemSkills
extends ScriptItemHandler {
    private int[] iv;

    public ItemSkills() {
        TIntHashSet tIntHashSet = new TIntHashSet();
        for (ItemTemplate itemTemplate : ItemHolder.getInstance().getAllTemplates()) {
            if (itemTemplate == null) continue;
            for (Skill skill : itemTemplate.getAttachedSkills()) {
                if (!skill.isHandler()) continue;
                tIntHashSet.add(itemTemplate.getItemId());
            }
        }
        this.iv = tIntHashSet.toArray();
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        Player player;
        if (playable.isPlayer()) {
            player = (Player)playable;
        } else if (playable.isPet()) {
            player = playable.getPlayer();
        } else {
            return false;
        }
        Skill[] skillArray = itemInstance.getTemplate().getAttachedSkills();
        for (int i = 0; i < skillArray.length; ++i) {
            Skill skill = skillArray[i];
            Creature creature = skill.getAimingTarget(player, player.getTarget());
            if (skill.checkCondition(player, creature, bl, false, true)) {
                player.getAI().Cast(skill, creature, bl, false);
                continue;
            }
            if (i != 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public int[] getItemIds() {
        return this.iv;
    }
}

