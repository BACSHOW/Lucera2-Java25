/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.CursedWeapon;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExCursedWeaponLocation;
import l2.gameserver.utils.Location;

public class RequestCursedWeaponLocation
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        ArrayList<ExCursedWeaponLocation.CursedWeaponInfo> arrayList = new ArrayList<ExCursedWeaponLocation.CursedWeaponInfo>();
        for (CursedWeapon cursedWeapon : CursedWeaponsManager.getInstance().getCursedWeapons()) {
            Location location = cursedWeapon.getWorldPosition();
            if (location == null) continue;
            arrayList.add(new ExCursedWeaponLocation.CursedWeaponInfo(location, cursedWeapon.getItemId(), cursedWeapon.isActivated() ? 1 : 0));
        }
        ((Creature)player).sendPacket((IStaticPacket)new ExCursedWeaponLocation(arrayList));
    }
}

