/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.items.listeners;

import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillCoolTime;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.templates.OptionDataTemplate;

public final class ItemAugmentationListener
implements OnEquipListener {
    private static final ItemAugmentationListener XS = new ItemAugmentationListener();

    public static ItemAugmentationListener getInstance() {
        return XS;
    }

    @Override
    public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
        if (!itemInstance.isEquipable()) {
            return;
        }
        if (!itemInstance.isAugmented()) {
            return;
        }
        Player player = playable.getPlayer();
        int[] nArray = new int[]{itemInstance.getVariationStat1(), itemInstance.getVariationStat2()};
        boolean bl = false;
        for (int n2 : nArray) {
            OptionDataTemplate optionDataTemplate = OptionDataHolder.getInstance().getTemplate(n2);
            if (optionDataTemplate == null) continue;
            player.removeStatsOwner(optionDataTemplate);
            for (Skill skill : optionDataTemplate.getSkills()) {
                bl = true;
                player.removeSkill(skill);
            }
            player.removeTriggers(optionDataTemplate);
        }
        if (bl) {
            player.sendPacket((IStaticPacket)new SkillList(player));
        }
        player.updateStats();
    }

    @Override
    public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
        if (!itemInstance.isEquipable()) {
            return;
        }
        if (!itemInstance.isAugmented()) {
            return;
        }
        Player player = playable.getPlayer();
        if (player.getExpertisePenalty(itemInstance) > 0) {
            return;
        }
        int[] nArray = new int[]{itemInstance.getVariationStat1(), itemInstance.getVariationStat2()};
        boolean bl = false;
        boolean bl2 = false;
        for (int n2 : nArray) {
            OptionDataTemplate optionDataTemplate = OptionDataHolder.getInstance().getTemplate(n2);
            if (optionDataTemplate == null) continue;
            player.addStatFuncs(optionDataTemplate.getStatFuncs(optionDataTemplate));
            for (Skill skill : optionDataTemplate.getSkills()) {
                bl = true;
                player.addSkill(skill);
                if (!player.isSkillDisabled(skill)) continue;
                bl2 = true;
            }
            player.addTriggers(optionDataTemplate);
        }
        if (bl) {
            player.sendPacket((IStaticPacket)new SkillList(player));
        }
        if (bl2) {
            player.sendPacket((IStaticPacket)new SkillCoolTime(player));
        }
        player.updateStats();
    }
}

