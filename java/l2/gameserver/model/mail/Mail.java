/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.mail;

import java.util.HashSet;
import java.util.Set;
import l2.commons.dao.JdbcEntity;
import l2.commons.dao.JdbcEntityState;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.items.ItemInstance;

public class Mail
implements Comparable<Mail>,
JdbcEntity {
    private static final long serialVersionUID = -8704970972611917153L;
    public static final int DELETED = 0;
    public static final int READED = 1;
    public static final int REJECTED = 2;
    private static final MailDAO XX = MailDAO.getInstance();
    private int messageId;
    private int XY;
    private String XZ;
    private int Ya;
    private String Yb;
    private int Yc;
    private String Yd;
    private String body;
    private long price;
    private SenderType Ye = SenderType.NORMAL;
    private boolean Yf;
    private Set<ItemInstance> Yg = new HashSet<ItemInstance>();
    private JdbcEntityState Yh = JdbcEntityState.CREATED;

    public int getMessageId() {
        return this.messageId;
    }

    public void setMessageId(int n) {
        this.messageId = n;
    }

    public int getSenderId() {
        return this.XY;
    }

    public void setSenderId(int n) {
        this.XY = n;
    }

    public String getSenderName() {
        return this.XZ;
    }

    public void setSenderName(String string) {
        this.XZ = string;
    }

    public int getReceiverId() {
        return this.Ya;
    }

    public void setReceiverId(int n) {
        this.Ya = n;
    }

    public String getReceiverName() {
        return this.Yb;
    }

    public void setReceiverName(String string) {
        this.Yb = string;
    }

    public int getExpireTime() {
        return this.Yc;
    }

    public void setExpireTime(int n) {
        this.Yc = n;
    }

    public String getTopic() {
        return this.Yd;
    }

    public void setTopic(String string) {
        this.Yd = string;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String string) {
        this.body = string;
    }

    public boolean isPayOnDelivery() {
        return this.price > 0L;
    }

    public long getPrice() {
        return this.price;
    }

    public void setPrice(long l) {
        this.price = l;
    }

    public boolean isUnread() {
        return this.Yf;
    }

    public void setUnread(boolean bl) {
        this.Yf = bl;
    }

    public Set<ItemInstance> getAttachments() {
        return this.Yg;
    }

    public void addAttachment(ItemInstance itemInstance) {
        this.Yg.add(itemInstance);
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
        return ((Mail)object).getMessageId() == this.getMessageId();
    }

    @Override
    public void setJdbcState(JdbcEntityState jdbcEntityState) {
        this.Yh = jdbcEntityState;
    }

    @Override
    public JdbcEntityState getJdbcState() {
        return this.Yh;
    }

    @Override
    public void save() {
        XX.save(this);
    }

    @Override
    public void update() {
        XX.update(this);
    }

    @Override
    public void delete() {
        XX.delete(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Mail reject() {
        Mail mail = new Mail();
        mail.setSenderId(this.getReceiverId());
        mail.setSenderName(this.getReceiverName());
        mail.setReceiverId(this.getSenderId());
        mail.setReceiverName(this.getSenderName());
        mail.setTopic(this.getTopic());
        mail.setBody(this.getBody());
        Set<ItemInstance> set = this.getAttachments();
        synchronized (set) {
            for (ItemInstance itemInstance : this.getAttachments()) {
                mail.addAttachment(itemInstance);
            }
            this.getAttachments().clear();
        }
        mail.setType(SenderType.NEWS_INFORMER);
        mail.setUnread(true);
        return mail;
    }

    public Mail reply() {
        Mail mail = new Mail();
        mail.setSenderId(this.getReceiverId());
        mail.setSenderName(this.getReceiverName());
        mail.setReceiverId(this.getSenderId());
        mail.setReceiverName(this.getSenderName());
        mail.setTopic("[Re]" + this.getTopic());
        mail.setBody(this.getBody());
        mail.setType(SenderType.NEWS_INFORMER);
        mail.setUnread(true);
        return mail;
    }

    @Override
    public int compareTo(Mail mail) {
        return mail.getMessageId() - this.getMessageId();
    }

    public SenderType getType() {
        return this.Ye;
    }

    public void setType(SenderType senderType) {
        this.Ye = senderType;
    }

    public static enum SenderType {
        NORMAL,
        NEWS_INFORMER,
        NONE,
        BIRTHDAY;

        public static SenderType[] VALUES;

        static {
            VALUES = SenderType.values();
        }
    }
}

