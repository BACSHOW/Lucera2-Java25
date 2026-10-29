/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.Iterator;
import l2.commons.collections.EmptyIterator;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;

public interface PlayerGroup
extends Iterable<Player> {
    public static final PlayerGroup EMPTY = new PlayerGroup(){

        @Override
        public void broadCast(IStaticPacket ... iStaticPacketArray) {
        }

        @Override
        public Iterator<Player> iterator() {
            return EmptyIterator.getInstance();
        }
    };

    public void broadCast(IStaticPacket ... var1);
}

