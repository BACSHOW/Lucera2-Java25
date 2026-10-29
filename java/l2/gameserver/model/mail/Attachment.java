/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.mail;

import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;

public class Attachment {
    private int messageId;
    private ItemInstance item;
    private Mail XW;

    public int getMessageId() {
        return this.messageId;
    }

    public void setMessageId(int n) {
        this.messageId = n;
    }

    public ItemInstance getItem() {
        return this.item;
    }

    public void setItem(ItemInstance itemInstance) {
        this.item = itemInstance;
    }

    public Mail getMail() {
        return this.XW;
    }

    public void setMail(Mail mail) {
        this.XW = mail;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        return ((Attachment)object).getItem() == this.getItem();
    }
}

