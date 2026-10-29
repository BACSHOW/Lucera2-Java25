/*
 * Decompiled with CFR 0.152.
 */
package services.villagemasters;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.VillageMasterInstance;
import l2.gameserver.scripts.Functions;

public class Ally
extends Functions {
    public void CheckCreateAlly() {
        if (this.getNpc() == null || this.getSelf() == null) {
            return;
        }
        Player player = this.getSelf();
        String string = "ally-01.htm";
        if (player.isClanLeader()) {
            string = "ally-02.htm";
        }
        ((VillageMasterInstance)this.getNpc()).showChatWindow(player, "villagemaster/" + string, new Object[0]);
    }

    public void CheckDissolveAlly() {
        if (this.getNpc() == null || this.getSelf() == null) {
            return;
        }
        Player player = this.getSelf();
        String string = "ally-01.htm";
        if (player.isAllyLeader()) {
            string = "ally-03.htm";
        }
        ((VillageMasterInstance)this.getNpc()).showChatWindow(player, "villagemaster/" + string, new Object[0]);
    }
}

