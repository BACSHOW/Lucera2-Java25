/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.components;

import l2.gameserver.data.StringHolder;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.item.ItemTemplate;

public class CustomMessage {
    private String add;
    private int mark = 0;

    public CustomMessage(String string, Player player, Object ... objectArray) {
        this.add = StringHolder.getInstance().getNotNull(player, string);
        this.add(objectArray);
    }

    public CustomMessage addNumber(long l) {
        this.add = this.add.replace("{" + this.mark + "}", String.valueOf(l));
        ++this.mark;
        return this;
    }

    public CustomMessage add(Object ... objectArray) {
        for (Object object : objectArray) {
            if (object instanceof String) {
                this.addString((String)object);
                continue;
            }
            if (object instanceof Integer) {
                this.addNumber(((Integer)object).intValue());
                continue;
            }
            if (object instanceof Long) {
                this.addNumber((Long)object);
                continue;
            }
            if (object instanceof ItemTemplate) {
                this.addItemName((ItemTemplate)object);
                continue;
            }
            if (object instanceof ItemInstance) {
                this.addItemName((ItemInstance)object);
                continue;
            }
            if (object instanceof Creature) {
                this.addCharName((Creature)object);
                continue;
            }
            if (object instanceof Skill) {
                this.addSkillName((Skill)object);
                continue;
            }
            System.out.println("unknown CustomMessage arg type: " + object);
            Thread.dumpStack();
        }
        return this;
    }

    public CustomMessage addString(String string) {
        this.add = this.add.replace("{" + this.mark + "}", string);
        ++this.mark;
        return this;
    }

    public CustomMessage addSkillName(Skill skill) {
        this.add = this.add.replace("{" + this.mark + "}", skill.getName());
        ++this.mark;
        return this;
    }

    public CustomMessage addSkillName(int n, int n2) {
        return this.addSkillName(SkillTable.getInstance().getInfo(n, n2));
    }

    public CustomMessage addItemName(ItemTemplate itemTemplate) {
        this.add = this.add.replace("{" + this.mark + "}", itemTemplate.getName());
        ++this.mark;
        return this;
    }

    public CustomMessage addItemName(int n) {
        return this.addItemName(ItemHolder.getInstance().getTemplate(n));
    }

    public CustomMessage addItemName(ItemInstance itemInstance) {
        return this.addItemName(itemInstance.getTemplate());
    }

    public CustomMessage addCharName(Creature creature) {
        this.add = this.add.replace("{" + this.mark + "}", creature.getName());
        ++this.mark;
        return this;
    }

    public String toString() {
        return this.add;
    }
}

