package services.randomcraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Onde o estado fica gravado (tabela character_random_craft, criada sozinha). */
public interface RandomCraftStore
{
	RandomCraftData load(int objId) throws Exception;

	void save(RandomCraftData d) throws Exception;

	final class Jdbc implements RandomCraftStore
	{
		private static final Logger _log = LoggerFactory.getLogger(Jdbc.class);

		public static final String CREATE = "CREATE TABLE IF NOT EXISTS character_random_craft ("
			+ " char_id INT NOT NULL,"
			+ " points INT NOT NULL DEFAULT 0,"
			+ " exp BIGINT NOT NULL DEFAULT 0,"
			+ " slots VARCHAR(255) NOT NULL DEFAULT '',"
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
				_log.warn("RandomCraft: não consegui criar a tabela character_random_craft (rode database/sql/character_random_craft.sql): " + e.getMessage());
			}
			finally
			{
				DbUtils.closeQuietly(con, st);
			}
		}

		@Override
		public RandomCraftData load(int objId) throws Exception
		{
			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				ps = con.prepareStatement("SELECT points, exp, slots FROM character_random_craft WHERE char_id=?");
				ps.setInt(1, objId);
				rs = ps.executeQuery();
				if (!rs.next())
					return null;
				RandomCraftData d = new RandomCraftData(objId);
				d.points = rs.getInt(1);
				d.exp = rs.getLong(2);
				d.decodeSlots(rs.getString(3));
				return d;
			}
			finally
			{
				DbUtils.closeQuietly(con, ps, rs);
			}
		}

		@Override
		public void save(RandomCraftData d) throws Exception
		{
			int points;
			long exp;
			String slots;
			synchronized (d)
			{
				points = d.points;
				exp = d.exp;
				slots = d.encodeSlots();
			}
			Connection con = null;
			PreparedStatement ps = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				ps = con.prepareStatement("REPLACE INTO character_random_craft (char_id, points, exp, slots) VALUES (?,?,?,?)");
				ps.setInt(1, d.objId);
				ps.setInt(2, points);
				ps.setLong(3, exp);
				ps.setString(4, slots);
				ps.executeUpdate();
			}
			finally
			{
				DbUtils.closeQuietly(con, ps);
			}
		}
	}
}
