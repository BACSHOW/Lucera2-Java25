/*
 * Decompiled with CFR 0.152.
 */
package ai.door;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.GameTimeController;
import l2.gameserver.ai.DoorAI;
import l2.gameserver.listener.game.OnDayNightChangeListener;
import l2.gameserver.model.instances.DoorInstance;

public class OnNightOpen
extends DoorAI {
    public OnNightOpen(DoorInstance doorInstance) {
        super(doorInstance);
        GameTimeController.getInstance().addListener(new NightDoorOpenController(doorInstance));
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    private static class NightDoorOpenController
    implements OnDayNightChangeListener {
        private HardReference<DoorInstance> dI;

        private NightDoorOpenController(DoorInstance doorInstance) {
            this.dI = (HardReference<DoorInstance>)(HardReference<?>)doorInstance.getRef();
        }

        @Override
        public void onDay() {
        }

        @Override
        public void onNight() {
            DoorInstance doorInstance = this.dI.get();
            if (doorInstance != null) {
                doorInstance.openMe();
            } else {
                _log.warn("Zaken door is null");
            }
        }
    }
}

