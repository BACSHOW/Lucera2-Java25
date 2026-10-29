/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.concurrent.atomic.AtomicReference;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.listener.PlayerListener;
import l2.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2.gameserver.listener.actor.OnCurrentMpReduceListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExUseSharedGroupItem;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.skills.TimeStamp;
import l2.gameserver.stats.Stats;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ACP
implements IVoicedCommandHandler,
OnPlayerEnterListener,
OnPlayerExitListener,
ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(ACP.class);
    private static final long bVR = 100L;
    private String[] zF = new String[]{"acp"};

    @Override
    public String[] getVoicedCommandList() {
        return this.zF;
    }

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (ACP.dQ() && string.equalsIgnoreCase("acp")) {
            String[] stringArray = string2.split("\\s+");
            if (stringArray.length > 1) {
                String string3 = stringArray[0];
                String string4 = stringArray[1];
                for (ACPType aCPType : ACPType.VALUES) {
                    if (!aCPType.isEnabled()) {
                        aCPType.remove(player);
                        continue;
                    }
                    if (!aCPType.getCfgName().equalsIgnoreCase(string3)) continue;
                    if (string4.equalsIgnoreCase("true") || string4.equalsIgnoreCase("on") || string4.equalsIgnoreCase("enable") || string4.equalsIgnoreCase("1") || string4.equalsIgnoreCase("yes")) {
                        aCPType.apply(player);
                        aCPType.setEnabled(player, true);
                        player.sendMessage(new CustomMessage("services.ACP.Enabled", player, aCPType.getCfgName()));
                        continue;
                    }
                    if (string4.equalsIgnoreCase("false") || string4.equalsIgnoreCase("of") || string4.equalsIgnoreCase("off") || string4.equalsIgnoreCase("disable") || string4.equalsIgnoreCase("0") || string4.equalsIgnoreCase("no")) {
                        aCPType.remove(player);
                        aCPType.setEnabled(player, false);
                        player.sendMessage(new CustomMessage("services.ACP.Disabled", player, aCPType.getCfgName()));
                        continue;
                    }
                    if (!aCPType.isEnabled(player)) continue;
                    int n = aCPType.setActPercent(player, NumberUtils.toInt(string4, aCPType.getActDefPercent()));
                    aCPType.apply(player);
                    player.sendMessage(new CustomMessage("services.ACP.ActPercentSet", player, aCPType.getCfgName(), n));
                }
            }
            ACP.an(player);
            return true;
        }
        return false;
    }

    private static void an(Player player) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        npcHtmlMessage.setFile("command/acp.htm");
        if (ACPType.HPACP.isEnabled(player)) {
            npcHtmlMessage.replace("%hpap%", String.valueOf(ACPType.HPACP.getActPercent(player)) + "%");
        } else {
            npcHtmlMessage.replace("%hpap%", "Off");
        }
        if (ACPType.CPACP.isEnabled(player)) {
            npcHtmlMessage.replace("%cpap%", String.valueOf(ACPType.CPACP.getActPercent(player)) + "%");
        } else {
            npcHtmlMessage.replace("%cpap%", "Off");
        }
        if (ACPType.MPACP.isEnabled(player)) {
            npcHtmlMessage.replace("%mpap%", String.valueOf(ACPType.MPACP.getActPercent(player)) + "%");
        } else {
            npcHtmlMessage.replace("%mpap%", "Off");
        }
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private static boolean dQ() {
        return Config.SERVICES_HPACP_ENABLE || Config.SERVICES_CPACP_ENABLE || Config.SERVICES_MPACP_ENABLE;
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (ACP.dQ()) {
            for (ACPType aCPType : ACPType.VALUES) {
                if (!aCPType.isEnabled() || !aCPType.isEnabled(player)) continue;
                aCPType.apply(player);
            }
        }
    }

    @Override
    public void onPlayerExit(Player player) {
        if (ACP.dQ()) {
            for (ACPType aCPType : ACPType.VALUES) {
                aCPType.remove(player);
            }
        }
    }

    @Override
    public void onLoad() {
        if (ACP.dQ()) {
            PlayerListenerList.addGlobal(this);
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
            LOG.info("ACPService: Loaded");
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        PlayerListenerList.removeGlobal(this);
    }

    private static final class MPACPHelper
    extends ACPHelper
    implements OnCurrentMpReduceListener {
        protected MPACPHelper(Player player) {
            super(player, ACPType.MPACP);
        }

        @Override
        protected boolean canUse(Player player) {
            if (super.canUse(player)) {
                double d = player.calcStat(Stats.MP_LIMIT, null, null) * (double)player.getMaxMp() / 100.0 * ((double)this.getType().getActPercent(player) / 100.0);
                return player.getCurrentMp() < d;
            }
            return false;
        }

        @Override
        public void onCurrentMpReduce(Creature creature, double d, Creature creature2) {
            Player player = creature.getPlayer();
            if (player == null) {
                creature.removeListener(this);
                return;
            }
            if (!this.getType().isEnabled(player)) {
                creature.removeListener(this);
            }
            this.act(player);
        }
    }

    private static final class CPACPHelper
    extends ACPHelper
    implements OnCurrentHpDamageListener {
        protected CPACPHelper(Player player) {
            super(player, ACPType.CPACP);
        }

        @Override
        protected boolean canUse(Player player) {
            if (super.canUse(player)) {
                double d = player.calcStat(Stats.CP_LIMIT, null, null) * (double)player.getMaxCp() / 100.0 * ((double)this.getType().getActPercent(player) / 100.0);
                return player.getCurrentCp() < d;
            }
            return false;
        }

        @Override
        public void onCurrentHpDamage(Creature creature, double d, Creature creature2, Skill skill) {
            Player player = creature.getPlayer();
            if (player == null) {
                creature.removeListener(this);
                return;
            }
            if (!this.getType().isEnabled(player)) {
                creature.removeListener(this);
            }
            this.act(player);
        }
    }

    private static final class HPACPHelper
    extends ACPHelper
    implements OnCurrentHpDamageListener {
        protected HPACPHelper(Player player) {
            super(player, ACPType.HPACP);
        }

        @Override
        protected boolean canUse(Player player) {
            if (super.canUse(player)) {
                double d = player.calcStat(Stats.HP_LIMIT, null, null) * (double)player.getMaxHp() / 100.0 * ((double)this.getType().getActPercent(player) / 100.0);
                return player.getCurrentHp() < d;
            }
            return false;
        }

        @Override
        public void onCurrentHpDamage(Creature creature, double d, Creature creature2, Skill skill) {
            Player player = creature.getPlayer();
            if (player == null) {
                creature.removeListener(this);
                return;
            }
            if (!this.getType().isEnabled(player)) {
                creature.removeListener(this);
            }
            this.act(player);
        }
    }

    private static abstract class ACPHelper
    implements Runnable,
    PlayerListener {
        private final HardReference<Player> bVS;
        private final AtomicReference<ACPHelperState> bVT;
        private final ACPType bVU;

        protected ACPHelper(Player player, ACPType aCPType) {
            this.bVU = aCPType;
            this.bVS = player.getRef();
            this.bVT = new AtomicReference<ACPHelperState>(ACPHelperState.IDLE);
        }

        protected Player getPlayer() {
            return this.bVS.get();
        }

        public ACPType getType() {
            return this.bVU;
        }

        protected void act(Player player) {
            if (this.getPlayer() != player) {
                if (player != null) {
                    player.removeListener(this);
                }
                if (this.getPlayer() != null) {
                    this.getPlayer().removeListener(this);
                }
                return;
            }
            if (this.bVT.compareAndSet(ACPHelperState.IDLE, ACPHelperState.APPLY)) {
                this.schedule(100L);
            }
        }

        @Override
        public void run() {
            Player player = this.getPlayer();
            if (player == null) {
                return;
            }
            try {
                if (this.bVT.compareAndSet(ACPHelperState.APPLY, ACPHelperState.USE)) {
                    this.ao(player);
                }
            }
            catch (Exception exception) {
                LOG.error("Exception in ACP helper task", exception);
                this.bVT.set(ACPHelperState.IDLE);
            }
        }

        private void schedule(long l) {
            ThreadPoolManager.getInstance().schedule(this, l);
        }

        protected boolean canUse(Player player) {
            if (player == null) {
                return false;
            }
            if (player.isDead() || player.isOutOfControl() || player.isInStoreMode()) {
                return false;
            }
            if (player.isFishing() || player.isHealBlocked() || player.isOlyParticipant()) {
                return false;
            }
            return Config.SERVICES_HPACP_WORK_IN_PEACE_ZONE || !player.isInZonePeace();
        }

        private void ao(Player player) {
            if (!this.canUse(player)) {
                this.bVT.compareAndSet(ACPHelperState.USE, ACPHelperState.IDLE);
                return;
            }
            ItemInstance itemInstance = this.ap(player);
            if (itemInstance == null) {
                this.bVT.compareAndSet(ACPHelperState.USE, ACPHelperState.IDLE);
                return;
            }
            long l = this.a(itemInstance, player);
            if (l <= 0L) {
                this.d(player, itemInstance);
                if (this.canUse(player) && this.bVT.compareAndSet(ACPHelperState.USE, ACPHelperState.APPLY)) {
                    this.schedule(100L);
                } else {
                    this.bVT.compareAndSet(ACPHelperState.USE, ACPHelperState.IDLE);
                }
            } else if (this.bVT.compareAndSet(ACPHelperState.USE, ACPHelperState.APPLY)) {
                this.schedule(100L + l);
            }
        }

        private long a(ItemInstance itemInstance, Player player) {
            if ((long)itemInstance.getTemplate().getReuseDelay() == 0L) {
                return 0L;
            }
            TimeStamp timeStamp = player.getSharedGroupReuse(itemInstance.getTemplate().getReuseGroup());
            if (timeStamp == null || !timeStamp.hasNotPassed()) {
                return 0L;
            }
            return Math.max(0L, timeStamp.getEndTime() - System.currentTimeMillis());
        }

        private ItemInstance ap(Player player) {
            if (player.isInStoreMode() || player.isOutOfControl()) {
                return null;
            }
            int[] nArray = this.bVU.getPotionsItemIds();
            ItemInstance itemInstance = null;
            for (int i = 0; i < nArray.length; ++i) {
                int n = nArray[i];
                ItemInstance itemInstance2 = player.getInventory().getItemByItemId(n);
                if (itemInstance2 == null || !itemInstance2.getTemplate().testCondition(player, itemInstance2, false) || player.getInventory().isLockedItem(itemInstance2)) continue;
                if (itemInstance == null) {
                    itemInstance = itemInstance2;
                    continue;
                }
                TimeStamp timeStamp = player.getSharedGroupReuse(itemInstance2.getTemplate().getReuseGroup());
                if (timeStamp == null) {
                    itemInstance = itemInstance2;
                    continue;
                }
                TimeStamp timeStamp2 = player.getSharedGroupReuse(itemInstance.getTemplate().getReuseGroup());
                if (timeStamp2 == null || timeStamp2.getEndTime() <= timeStamp.getEndTime()) continue;
                itemInstance = itemInstance2;
            }
            return itemInstance;
        }

        private boolean d(Player player, ItemInstance itemInstance) {
            if (itemInstance.getTemplate().getHandler().useItem(player, itemInstance, false)) {
                long l = itemInstance.getTemplate().getReuseType().next(itemInstance);
                if (l > System.currentTimeMillis()) {
                    TimeStamp timeStamp = new TimeStamp(itemInstance.getItemId(), l, (long)itemInstance.getTemplate().getReuseDelay());
                    player.addSharedGroupReuse(itemInstance.getTemplate().getReuseGroup(), timeStamp);
                    if (itemInstance.getTemplate().getReuseDelay() > 0) {
                        player.sendPacket((IStaticPacket)new ExUseSharedGroupItem(itemInstance.getTemplate().getDisplayReuseGroup(), timeStamp));
                    }
                }
                return true;
            }
            return false;
        }

        static enum ACPHelperState {
            IDLE,
            APPLY,
            USE;

        }
    }

    static enum ACPType {
        HPACP("HP"){

            @Override
            public boolean isEnabled() {
                return Config.SERVICES_HPACP_ENABLE;
            }

            @Override
            public void apply(Player player) {
                if (!player.getListeners().getListeners(HPACPHelper.class).isEmpty()) {
                    return;
                }
                player.addListener(new HPACPHelper(player));
            }

            @Override
            public void remove(Player player) {
                player.getListeners().getListeners(HPACPHelper.class).forEach(player.getListeners()::remove);
            }

            @Override
            public int[] getPotionsItemIds() {
                return Config.SERVICES_HPACP_POTIONS_ITEM_IDS;
            }

            @Override
            protected int getActMinPercent() {
                return Config.SERVICES_HPACP_MIN_PERCENT;
            }

            @Override
            protected int getActMaxPercent() {
                return Config.SERVICES_HPACP_MAX_PERCENT;
            }

            @Override
            protected int getActDefPercent() {
                return Config.SERVICES_HPACP_DEF_PERCENT;
            }
        }
        ,
        CPACP("CP"){

            @Override
            public boolean isEnabled() {
                return Config.SERVICES_CPACP_ENABLE;
            }

            @Override
            public void apply(Player player) {
                if (!player.getListeners().getListeners(CPACPHelper.class).isEmpty()) {
                    return;
                }
                player.addListener(new CPACPHelper(player));
            }

            @Override
            public void remove(Player player) {
                player.getListeners().getListeners(CPACPHelper.class).forEach(player.getListeners()::remove);
            }

            @Override
            public int[] getPotionsItemIds() {
                return Config.SERVICES_CPACP_POTIONS_ITEM_IDS;
            }

            @Override
            protected int getActMinPercent() {
                return Config.SERVICES_CPACP_MIN_PERCENT;
            }

            @Override
            protected int getActMaxPercent() {
                return Config.SERVICES_CPACP_MAX_PERCENT;
            }

            @Override
            protected int getActDefPercent() {
                return Config.SERVICES_CPACP_DEF_PERCENT;
            }
        }
        ,
        MPACP("MP"){

            @Override
            public boolean isEnabled() {
                return Config.SERVICES_MPACP_ENABLE;
            }

            @Override
            public void apply(Player player) {
                if (!player.getListeners().getListeners(MPACPHelper.class).isEmpty()) {
                    return;
                }
                player.addListener(new MPACPHelper(player));
            }

            @Override
            public void remove(Player player) {
                player.getListeners().getListeners(MPACPHelper.class).forEach(player.getListeners()::remove);
            }

            @Override
            public int[] getPotionsItemIds() {
                return Config.SERVICES_MPACP_POTIONS_ITEM_IDS;
            }

            @Override
            protected int getActMinPercent() {
                return Config.SERVICES_MPACP_MIN_PERCENT;
            }

            @Override
            protected int getActMaxPercent() {
                return Config.SERVICES_MPACP_MAX_PERCENT;
            }

            @Override
            protected int getActDefPercent() {
                return Config.SERVICES_MPACP_DEF_PERCENT;
            }
        };

        private final String bVW;
        private static String bVX;
        private static String bVY;
        public static final ACPType[] VALUES;

        private ACPType(String string2) {
            this.bVW = string2;
        }

        public String getCfgName() {
            return this.bVW;
        }

        public abstract boolean isEnabled();

        public boolean isEnabled(Player player) {
            if (Config.SERVICES_HPACP_AVAILABLE_ONLY_PREMIUM && !player.hasBonus()) {
                return false;
            }
            return this.isEnabled() && player.getVarB(this.name() + bVX, Config.ENABLE_ACP_ON_CHARACTER_CREATE);
        }

        public int getActPercent(Player player) {
            int n = player.getVarInt(this.name() + bVY, this.getActDefPercent());
            return Math.min(Math.max(this.getActMinPercent(), n), this.getActMaxPercent());
        }

        public int setActPercent(Player player, int n) {
            n = Math.min(Math.max(this.getActMinPercent(), n), this.getActMaxPercent());
            player.setVar(this.name() + bVY, n, -1L);
            return n;
        }

        public void setEnabled(Player player, boolean bl) {
            if (bl) {
                player.setVar(this.name() + bVX, "true", -1L);
                return;
            }
            player.unsetVar(this.name() + bVX);
            player.unsetVar(this.name() + bVY);
        }

        public abstract void apply(Player var1);

        public abstract void remove(Player var1);

        public abstract int[] getPotionsItemIds();

        protected abstract int getActMinPercent();

        protected abstract int getActMaxPercent();

        protected abstract int getActDefPercent();

        static {
            bVX = "Enabled";
            bVY = "ActPercent";
            VALUES = ACPType.values();
        }
    }
}

