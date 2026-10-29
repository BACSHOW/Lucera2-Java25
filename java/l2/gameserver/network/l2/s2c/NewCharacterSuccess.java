/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import l2.gameserver.data.xml.holder.CharacterTemplateHolder;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.PlayerTemplate;

public class NewCharacterSuccess
extends L2GameServerPacket {
    public static final NewCharacterSuccess STATIC_DEFAULT = new NewCharacterSuccess();
    private List<PlayerTemplate> ajQ = new ArrayList<PlayerTemplate>();

    public void addChar(PlayerTemplate playerTemplate) {
        this.ajQ.add(playerTemplate);
    }

    @Override
    protected final void writeImpl() {
        this.writeC(23);
        this.writeD(this.ajQ.size());
        for (PlayerTemplate playerTemplate : this.ajQ) {
            this.writeD(playerTemplate.race.ordinal());
            this.writeD(playerTemplate.classId.getId());
            this.writeD(70);
            this.writeD(playerTemplate.baseSTR);
            this.writeD(10);
            this.writeD(70);
            this.writeD(playerTemplate.baseDEX);
            this.writeD(10);
            this.writeD(70);
            this.writeD(playerTemplate.baseCON);
            this.writeD(10);
            this.writeD(70);
            this.writeD(playerTemplate.baseINT);
            this.writeD(10);
            this.writeD(70);
            this.writeD(playerTemplate.baseWIT);
            this.writeD(10);
            this.writeD(70);
            this.writeD(playerTemplate.baseMEN);
            this.writeD(10);
        }
    }

    static {
        for (ClassId classId : Arrays.asList(ClassId.fighter, ClassId.mage, ClassId.elven_fighter, ClassId.elven_mage, ClassId.dark_fighter, ClassId.dark_mage, ClassId.orc_fighter, ClassId.orc_mage, ClassId.dwarven_fighter)) {
            STATIC_DEFAULT.addChar(CharacterTemplateHolder.getInstance().getTemplate(classId, true));
        }
    }
}

