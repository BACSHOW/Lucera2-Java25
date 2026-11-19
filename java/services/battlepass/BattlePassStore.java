package services.battlepass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Onde o progresso fica gravado. O serviço usa a interface; o servidor usa a
 * tabela character_battle_pass (criada sozinha no carregamento).
 */
public interface BattlePassStore
{
	/** Linha gravada ou null (nunca jogou o pass). */
	BattlePassData load(int objId) throws Exception;

	void save(BattlePassData d) throws Exception;

	/** Tabela no MySQL. */
	final class Jdbc implements BattlePassStore
	{
		private static final Logger _log = LoggerFactory.getLogger(Jdbc.class);

		public static final String CREATE = "CREATE TABLE IF NOT EXISTS character_battle_pass ("
			+ " char_id INT NOT NULL,"
			+ " season INT NOT NULL DEFAULT 0,"
			+ " points INT NOT NULL DEFAULT 0,"
			+ " premium TINYINT NOT NULL DEFAULT 0,"
			+ " free_claimed BIGINT NOT NULL DEFAULT 0,"
			+ " premium_claimed BIGINT NOT NULL DEFAULT 0,"
			+ " PRIMARY KEY (char_id)"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8";

		public static void createTable()
		{
			Connection con = null;
			Statement st = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				st = con.createStatement();
				st.executeUpdate(CREATE);
			}
			catch (Exception e)
			{
				_log.warn("BattlePass: não consegui criar a tabela character_battle_pass (rode database/sql/character_battle_pass.sql): " + e.getMessage());
			}
			finally
			{
				DbUtils.closeQuietly(con, st);
			}
		}

		@Override
		public BattlePassData load(int objId) throws Exception
		{
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				ps = con.prepareStatement("SELECT season, points, premium, free_claimed, premium_claimed FROM character_battle_pass WHERE char_id=?");
				ps.setInt(1, objId);
				rs = ps.executeQuery();
				if (!rs.next())
					return null;
				BattlePassData d = new BattlePassData(objId);
				d.season = rs.getInt(1);
				d.points = rs.getInt(2);
				d.premium = rs.getInt(3) != 0;
				d.freeClaimed = rs.getLong(4);
				d.premClaimed = rs.getLong(5);
				return d;
			}
			finally
			{
				DbUtils.closeQuietly(con, ps, rs);
			}
		}

		@Override
		public void save(BattlePassData d) throws Exception
		{
			int season, points;
			boolean premium;
			long free, prem;
			synchronized (d)
			{
				season = d.season;
				points = d.points;
				premium = d.premium;
				free = d.freeClaimed;
				prem = d.premClaimed;
			}
			Connection con = null;
			PreparedStatement ps = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				ps = con.prepareStatement("REPLACE INTO character_battle_pass (char_id, season, points, premium, free_claimed, premium_claimed) VALUES (?,?,?,?,?,?)");
				ps.setInt(1, d.objId);
				ps.setInt(2, season);
				ps.setInt(3, points);
				ps.setInt(4, premium ? 1 : 0);
				ps.setLong(5, free);
				ps.setLong(6, prem);
				ps.executeUpdate();
			}
			finally
			{
				DbUtils.closeQuietly(con, ps);
			}
		}
	}
}
