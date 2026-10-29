/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public abstract class SysMsgContainer<T extends SysMsgContainer<T>>
extends L2GameServerPacket {
    protected SystemMsg _message;
    protected List<IArgument> _arguments;

    @Deprecated
    protected SysMsgContainer(int n) {
        this(SystemMsg.valueOf(n));
    }

    protected SysMsgContainer(SystemMsg systemMsg) {
        if (systemMsg == null) {
            throw new IllegalArgumentException("SystemMsg is null");
        }
        this._message = systemMsg;
        this._arguments = new ArrayList<IArgument>(this._message.size());
    }

    protected void writeElements() {
        if (this._message.size() != this._arguments.size()) {
            throw new IllegalArgumentException("Wrong count of arguments: " + this._message);
        }
        this.writeD(this._message.getId());
        this.writeD(this._arguments.size());
        for (IArgument iArgument : this._arguments) {
            iArgument.write(this);
        }
    }

    public T addName(GameObject gameObject) {
        if (gameObject == null) {
            return this.add(new StringArgument(null));
        }
        if (gameObject.isNpc()) {
            return this.add(new NpcNameArgument(((NpcInstance)gameObject).getNpcId() + 1000000));
        }
        if (gameObject instanceof Summon) {
            return this.add(new NpcNameArgument(((Summon)gameObject).getNpcId() + 1000000));
        }
        if (gameObject.isItem()) {
            return this.add(new ItemNameArgument(((ItemInstance)gameObject).getItemId()));
        }
        if (gameObject.isPlayer()) {
            return this.add(new PlayerNameArgument((Player)gameObject));
        }
        if (gameObject.isDoor()) {
            return this.add(new StaticObjectNameArgument(((DoorInstance)gameObject).getDoorId()));
        }
        if (gameObject instanceof StaticObjectInstance) {
            return this.add(new StaticObjectNameArgument(((StaticObjectInstance)gameObject).getUId()));
        }
        return this.add(new StringArgument(gameObject.getName()));
    }

    public T addInstanceName(int n) {
        return this.add(new InstanceNameArgument(n));
    }

    public T addSysString(int n) {
        return this.add(new SysStringArgument(n));
    }

    public T addSkillName(Skill skill) {
        return this.addSkillName(skill.getDisplayId(), skill.getDisplayLevel());
    }

    public T addSkillName(int n, int n2) {
        return this.add(new SkillArgument(n, n2));
    }

    public T addItemName(int n) {
        return this.add(new ItemNameArgument(n));
    }

    @Deprecated
    public T addItemNameWithAugmentation(ItemInstance itemInstance) {
        return this.add(new ItemNameWithAugmentationArgument(itemInstance.getItemId(), itemInstance.getVariationStat1(), itemInstance.getVariationStat2()));
    }

    public T addZoneName(Creature creature) {
        return this.addZoneName(creature.getX(), creature.getY(), creature.getZ());
    }

    public T addZoneName(Location location) {
        return this.add(new ZoneArgument(location.x, location.y, location.z));
    }

    public T addZoneName(int n, int n2, int n3) {
        return this.add(new ZoneArgument(n, n2, n3));
    }

    public T addResidenceName(Residence residence) {
        return this.add(new ResidenceArgument(residence.getId()));
    }

    public T addResidenceName(int n) {
        return this.add(new ResidenceArgument(n));
    }

    public T addElementName(int n) {
        return this.add(new ElementNameArgument(n));
    }

    public T addElementName(Element element) {
        return this.add(new ElementNameArgument(element.getId()));
    }

    public T addInteger(double d) {
        return this.add(new IntegerArgument((int)d));
    }

    public T addLong(long l) {
        return this.add(new LongArgument(l));
    }

    public T addString(String string) {
        return this.add(new StringArgument(string));
    }

    protected T add(IArgument iArgument) {
        this._arguments.add(iArgument);
        return (T)this;
    }

    public static class PlayerNameArgument
    extends StringArgument {
        public PlayerNameArgument(Creature creature) {
            super(creature.getName());
        }

        @Override
        Types getType() {
            return Types.TEXT;
        }
    }

    public static class ElementNameArgument
    extends IntegerArgument {
        public ElementNameArgument(int n) {
            super(n);
        }

        @Override
        Types getType() {
            return Types.ELEMENT_NAME;
        }
    }

    public static class ZoneArgument
    extends IArgument {
        private final int _x;
        private final int _y;
        private final int Di;

        public ZoneArgument(int n, int n2, int n3) {
            this._x = n;
            this._y = n2;
            this.Di = n3;
        }

        @Override
        void writeData(SysMsgContainer sysMsgContainer) {
            sysMsgContainer.writeD(this._x);
            sysMsgContainer.writeD(this._y);
            sysMsgContainer.writeD(this.Di);
        }

        @Override
        Types getType() {
            return Types.ZONE_NAME;
        }
    }

    public static class SkillArgument
    extends IArgument {
        private final int _skillId;
        private final int _skillLevel;

        public SkillArgument(int n, int n2) {
            this._skillId = n;
            this._skillLevel = n2;
        }

        @Override
        void writeData(SysMsgContainer sysMsgContainer) {
            sysMsgContainer.writeD(this._skillId);
            sysMsgContainer.writeD(this._skillLevel);
        }

        @Override
        Types getType() {
            return Types.SKILL_NAME;
        }
    }

    public static class StringArgument
    extends IArgument {
        private final String anb;

        public StringArgument(String string) {
            this.anb = string == null ? "null" : string;
        }

        @Override
        void writeData(SysMsgContainer sysMsgContainer) {
            sysMsgContainer.writeS(this.anb);
        }

        @Override
        Types getType() {
            return Types.TEXT;
        }
    }

    public static class LongArgument
    extends IArgument {
        private final long ana;

        public LongArgument(long l) {
            this.ana = l;
        }

        @Override
        void writeData(SysMsgContainer sysMsgContainer) {
            sysMsgContainer.writeQ(this.ana);
        }

        @Override
        Types getType() {
            return Types.LONG;
        }
    }

    public static class StaticObjectNameArgument
    extends IntegerArgument {
        public StaticObjectNameArgument(int n) {
            super(n);
        }

        @Override
        Types getType() {
            return Types.STATIC_OBJECT_NAME;
        }
    }

    public static class ResidenceArgument
    extends IntegerArgument {
        public ResidenceArgument(int n) {
            super(n);
        }

        @Override
        Types getType() {
            return Types.RESIDENCE_NAME;
        }
    }

    public static class SysStringArgument
    extends IntegerArgument {
        public SysStringArgument(int n) {
            super(n);
        }

        @Override
        Types getType() {
            return Types.SYSTEM_STRING;
        }
    }

    public static class InstanceNameArgument
    extends IntegerArgument {
        public InstanceNameArgument(int n) {
            super(n);
        }

        @Override
        Types getType() {
            return Types.INSTANCE_NAME;
        }
    }

    public static class ItemNameWithAugmentationArgument
    extends IArgument {
        private final int _itemId;
        private final int amY;
        private final int amZ;

        public ItemNameWithAugmentationArgument(int n, int n2, int n3) {
            this._itemId = n;
            this.amY = n2;
            this.amZ = n3;
        }

        @Override
        Types getType() {
            return Types.ITEM_NAME_WITH_AUGMENTATION;
        }

        @Override
        void writeData(SysMsgContainer sysMsgContainer) {
            sysMsgContainer.writeD(this._itemId);
            sysMsgContainer.writeH(this.amY);
            sysMsgContainer.writeH(this.amZ);
        }
    }

    public static class ItemNameArgument
    extends IntegerArgument {
        public ItemNameArgument(int n) {
            super(n);
        }

        @Override
        Types getType() {
            return Types.ITEM_NAME;
        }
    }

    public static class NpcNameArgument
    extends IntegerArgument {
        public NpcNameArgument(int n) {
            super(n);
        }

        @Override
        Types getType() {
            return Types.NPC_NAME;
        }
    }

    public static class IntegerArgument
    extends IArgument {
        private final int amX;

        public IntegerArgument(int n) {
            this.amX = n;
        }

        @Override
        public void writeData(SysMsgContainer sysMsgContainer) {
            sysMsgContainer.writeD(this.amX);
        }

        @Override
        Types getType() {
            return Types.NUMBER;
        }
    }

    public static abstract class IArgument {
        void write(SysMsgContainer sysMsgContainer) {
            sysMsgContainer.writeD(this.getType().ordinal());
            this.writeData(sysMsgContainer);
        }

        abstract Types getType();

        abstract void writeData(SysMsgContainer var1);
    }

    public static enum Types {
        TEXT,
        NUMBER,
        NPC_NAME,
        ITEM_NAME,
        SKILL_NAME,
        RESIDENCE_NAME,
        LONG,
        ZONE_NAME,
        ITEM_NAME_WITH_AUGMENTATION,
        ELEMENT_NAME,
        INSTANCE_NAME,
        STATIC_OBJECT_NAME,
        PLAYER_NAME,
        SYSTEM_STRING;

    }
}

