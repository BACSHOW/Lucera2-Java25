package services.battlepass;

import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MonsterInstance;

/**
 * Pontos do Battle Pass por monstro morto.
 *
 * Global, com ignorePetOrSummon = true: o núcleo chama uma vez por morte, com
 * o jogador dono de quem matou (morte pelo pet / summon conta para o dono).
 * Em party (PartyShare), cada membro vivo perto do monstro (PartyRange) ganha
 * os pontos da conta dele (o limite de nível vale para cada um).
 */
public final class BattlePassKillListener implements OnKillListener
{
	@Override
	public void onKill(Creature actor, Creature victim)
	{
		if (!BattlePassConfig.ENABLED || actor == null || victim == null || !actor.isPlayer() || !victim.isMonster())
			return;
		Player killer = actor.getPlayer();
		if (killer == null)
			return;
		boolean raid = victim.isRaid();
		boolean boss = victim.isBoss();
		int champion = victim instanceof MonsterInstance ? ((MonsterInstance) victim).getChampion() : 0;
		int mobLevel = victim.getLevel();

		Party party = killer.getParty();
		if (!BattlePassConfig.PARTY_SHARE || party == null)
		{
			BattlePassService.addPoints(killer, BattlePassService.killPoints(killer.getLevel(), mobLevel, raid, boss, champion));
			return;
		}
		for (Player m : party.getPartyMembers())
		{
			if (m == null || !m.isOnline() || m.isDead())
				continue;
			if (m != killer && BattlePassConfig.PARTY_RANGE > 0 && !m.isInRange(victim, BattlePassConfig.PARTY_RANGE))
				continue;
			BattlePassService.addPoints(m, BattlePassService.killPoints(m.getLevel(), mobLevel, raid, boss, champion));
		}
	}

	@Override
	public boolean ignorePetOrSummon()
	{
		return true;
	}
}
