/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.GameTimeController;
import l2.gameserver.ai.Mystic;
import l2.gameserver.listener.game.OnDayNightChangeListener;
import l2.gameserver.model.instances.NpcInstance;

public class NightAgressionMystic
extends Mystic {
    public NightAgressionMystic(NpcInstance npcInstance) {
        super(npcInstance);
        GameTimeController.getInstance().addListener(new NightAgressionDayNightListener());
    }

    private class NightAgressionDayNightListener
    implements OnDayNightChangeListener {
        private NightAgressionDayNightListener() {
            if (GameTimeController.getInstance().isNowNight()) {
                this.onNight();
            } else {
                this.onDay();
            }
        }

        @Override
        public void onDay() {
            NightAgressionMystic.this.getActor().setAggroRange(0);
        }

        @Override
        public void onNight() {
            NightAgressionMystic.this.getActor().setAggroRange(-1);
        }
    }
}

