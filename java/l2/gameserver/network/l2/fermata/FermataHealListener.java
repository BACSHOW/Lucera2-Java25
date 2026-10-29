package l2.gameserver.network.l2.fermata;

import l2.gameserver.listener.actor.OnCurrentHpHealListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.fermata.s2c.Healing;

/**
 * Bridge entre o listener global de cura e o broadcast S2C Fermata
 * ({@link Healing}). Convertemos o delta para o inteiro de HP e o actor
 * (caster) e skill (opcional, null em regen) para o sourceId/skillId.
 *
 * Casters ou skills nulos sao normalizados para 0; o cliente usa isso
 * para distinguir cura anonima (regen) de cura atribuida a uma skill.
 */
public final class FermataHealListener implements OnCurrentHpHealListener {

    public static final FermataHealListener INSTANCE = new FermataHealListener();

    static {
        l2.gameserver.model.actor.listener.CharListenerList.addGlobal(INSTANCE);
    }

    @Override
    public void onCurrentHpHeal(Creature target, double delta, Creature caster, Skill skill) {
        if (target == null || delta <= 0.0) {
            return;
        }
        int amount = (int) Math.min(Integer.MAX_VALUE, Math.max(0, Math.round(delta)));
        if (amount <= 0) {
            return;
        }
        int sourceId = caster == null ? 0 : caster.getObjectId();
        int skillId = skill == null ? 0 : skill.getId();
        int skillLevel = skill == null ? 0 : skill.getLevel();
        Healing packet = new Healing(sourceId, target.getObjectId(), skillId, skillLevel, amount);
        FermataBroadcaster.broadcastIfCapable(target, packet, FermataCapabilities.BIT_HEALING_FEEDBACK);
    }
}
