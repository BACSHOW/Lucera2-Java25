/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import l2.gameserver.data.xml.holder.OptionDataHolder;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.stats.triggers.TriggerInfo;
import l2.gameserver.templates.OptionDataTemplate;

public class Augments
implements IVoicedCommandHandler {
    private final String[] zF = new String[]{"aug", "augments"};

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        for (int i = 0; i < 17; ++i) {
            ItemInstance itemInstance = player.getInventory().getPaperdollItem(i);
            if (itemInstance == null || !itemInstance.isAugmented()) continue;
            StringBuilder stringBuilder = new StringBuilder(30);
            stringBuilder.append("<<Detail augments info>>");
            stringBuilder.append("\n");
            stringBuilder.append(itemInstance.getName() + " with enchant level " + itemInstance.getEnchantLevel() + " have augment:");
            stringBuilder.append("\n");
            stringBuilder.append("Option id 1 : " + itemInstance.getVariationStat1());
            stringBuilder.append("\n");
            stringBuilder.append("Option id 2 : " + itemInstance.getVariationStat2());
            this.a(stringBuilder, itemInstance.getVariationStat1());
            this.a(stringBuilder, itemInstance.getVariationStat2());
            player.sendMessage(stringBuilder.toString());
        }
        return true;
    }

    private void a(StringBuilder stringBuilder, int n) {
        OptionDataTemplate optionDataTemplate = OptionDataHolder.getInstance().getTemplate(n);
        if (optionDataTemplate != null) {
            if (!optionDataTemplate.getSkills().isEmpty()) {
                for (Skill object : optionDataTemplate.getSkills()) {
                    stringBuilder.append(" ");
                    stringBuilder.append("\n");
                    stringBuilder.append("Skill name: " + object.getName() + " (id: " + object.getId() + ") - level " + object.getLevel());
                    stringBuilder.append("\n");
                }
            }
            if (!optionDataTemplate.getTriggerList().isEmpty()) {
                for (TriggerInfo triggerInfo : optionDataTemplate.getTriggerList()) {
                    stringBuilder.append("\n");
                    stringBuilder.append("Chance skill id: " + triggerInfo.id);
                    stringBuilder.append(" - level " + triggerInfo.level);
                    stringBuilder.append("\n");
                    stringBuilder.append("Activation type " + (Object)((Object)triggerInfo.getType()));
                    stringBuilder.append("\n");
                    stringBuilder.append("Activation chance : " + triggerInfo.getChance() + "%");
                }
            }
        }
    }
}

