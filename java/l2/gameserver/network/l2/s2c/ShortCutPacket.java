/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.skills.TimeStamp;

public abstract class ShortCutPacket
extends L2GameServerPacket {
    public static ShortcutInfo convert(Player player, ShortCut shortCut) {
        ShortcutInfo shortcutInfo = null;
        int n = shortCut.getSlot() + shortCut.getPage() * 12;
        switch (shortCut.getType()) {
            case 1: {
                int n2 = -1;
                int n3 = 0;
                int n4 = 0;
                int n5 = 0;
                int n6 = 0;
                ItemInstance itemInstance = player.getInventory().getItemByObjectId(shortCut.getId());
                if (itemInstance != null) {
                    TimeStamp timeStamp;
                    n5 = itemInstance.getVariationStat1();
                    n6 = itemInstance.getVariationStat2();
                    n2 = itemInstance.getTemplate().getDisplayReuseGroup();
                    if (itemInstance.getTemplate().getReuseDelay() > 0 && (timeStamp = player.getSharedGroupReuse(itemInstance.getTemplate().getReuseGroup())) != null) {
                        n3 = (int)(timeStamp.getReuseCurrent() / 1000L);
                        n4 = (int)(timeStamp.getReuseBasic() / 1000L);
                    }
                }
                shortcutInfo = new ItemShortcutInfo(shortCut.getType(), n, shortCut.getId(), n2, n3, n4, n5, n6, shortCut.getCharacterType());
                break;
            }
            case 2: {
                shortcutInfo = new SkillShortcutInfo(shortCut.getType(), n, shortCut.getId(), shortCut.getLevel(), shortCut.getCharacterType());
                break;
            }
            default: {
                shortcutInfo = new ShortcutInfo(shortCut.getType(), n, shortCut.getId(), shortCut.getCharacterType());
            }
        }
        return shortcutInfo;
    }

    protected static class ShortcutInfo {
        protected final int _type;
        protected final int _page;
        protected final int _id;
        protected final int _characterType;

        public ShortcutInfo(int n, int n2, int n3, int n4) {
            this._type = n;
            this._page = n2;
            this._id = n3;
            this._characterType = n4;
        }

        protected void write(ShortCutPacket shortCutPacket) {
            shortCutPacket.writeD(this._type);
            shortCutPacket.writeD(this._page);
            this.write0(shortCutPacket);
        }

        protected void write0(ShortCutPacket shortCutPacket) {
            shortCutPacket.writeD(this._id);
            shortCutPacket.writeD(this._characterType);
        }
    }

    protected static class SkillShortcutInfo
    extends ShortcutInfo {
        private final int _level;

        public SkillShortcutInfo(int n, int n2, int n3, int n4, int n5) {
            super(n, n2, n3, n5);
            this._level = n4;
        }

        public int getLevel() {
            return this._level;
        }

        @Override
        protected void write0(ShortCutPacket shortCutPacket) {
            shortCutPacket.writeD(this._id);
            shortCutPacket.writeD(this._level);
            shortCutPacket.writeC(0);
            shortCutPacket.writeD(this._characterType);
        }
    }

    protected static class ItemShortcutInfo
    extends ShortcutInfo {
        private int amo;
        private int amp;
        private int amq;
        private int amr;
        private int ams;

        public ItemShortcutInfo(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
            super(n, n2, n3, n9);
            this.amo = n4;
            this.amp = n5;
            this.amq = n6;
            this.amr = n7;
            this.ams = n8;
        }

        @Override
        protected void write0(ShortCutPacket shortCutPacket) {
            shortCutPacket.writeD(this._id);
            shortCutPacket.writeD(this._characterType);
            shortCutPacket.writeD(this.amo);
            shortCutPacket.writeD(this.amp);
            shortCutPacket.writeD(this.amq);
            shortCutPacket.writeH(this.amr);
            shortCutPacket.writeH(this.ams);
        }
    }
}

