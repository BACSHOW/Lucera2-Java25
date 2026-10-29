/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.SkillLearn;
import l2.gameserver.model.base.AcquireType;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.AcquireSkillInfo;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.SkillTable;

public class RequestAquireSkillInfo
extends L2GameClientPacket {
    private static final AcquireType[] aba = new AcquireType[]{AcquireType.NORMAL, AcquireType.FISHING, AcquireType.CLAN};
    private int _id;
    private int _level;
    private AcquireType abb;

    @Override
    protected void readImpl() {
        this._id = this.readD();
        this._level = this.readD();
        this.abb = ArrayUtils.valid(aba, this.readD());
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || player.getTransformation() != 0 || player.isCursedWeaponEquipped() || SkillTable.getInstance().getInfo(this._id, this._level) == null || this.abb == null) {
            return;
        }
        NpcInstance npcInstance = player.getLastNpc();
        if (npcInstance == null || !npcInstance.isInActingRange(player)) {
            return;
        }
        int n = player.getVarInt("AcquireSkillClassId", player.getClassId().getId());
        ClassId classId = n >= 0 && n < ClassId.VALUES.length ? ClassId.VALUES[n] : null;
        SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, classId, this._id, this._level, this.abb);
        if (skillLearn == null) {
            return;
        }
        if (Config.ALT_DISABLE_SPELLBOOKS && this.abb == AcquireType.NORMAL) {
            this.sendPacket((L2GameServerPacket)new AcquireSkillInfo(this.abb, skillLearn, 0, 0));
        } else {
            this.sendPacket((L2GameServerPacket)new AcquireSkillInfo(this.abb, skillLearn, skillLearn.getItemId(), (int)skillLearn.getItemCount()));
        }
    }
}

