/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.SummonInstance;
import l2.gameserver.skills.EffectType;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectsDAO {
    private static final int uQ = 100000;
    private static final Logger _log = LoggerFactory.getLogger(EffectsDAO.class);
    private static final EffectsDAO uR = new EffectsDAO();

    EffectsDAO() {
    }

    public static EffectsDAO getInstance() {
        return uR;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void restoreEffects(Playable playable) {
        int n;
        int n2;
        if (playable.isPlayer()) {
            n2 = playable.getObjectId();
            n = ((Player)playable).getActiveClassId();
        } else if (playable.isSummon()) {
            n2 = playable.getPlayer().getObjectId();
            n = ((SummonInstance)playable).getEffectIdentifier() + 100000;
        } else {
            return;
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration` FROM `character_effects_save` WHERE `object_id`=? AND `id`=? ORDER BY `order` ASC");
            preparedStatement.setInt(1, n2);
            preparedStatement.setInt(2, n);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int n3 = resultSet.getInt("skill_id");
                int n4 = resultSet.getInt("skill_level");
                int n5 = resultSet.getInt("effect_count");
                long l = resultSet.getLong("effect_cur_time");
                long l2 = resultSet.getLong("duration");
                Skill skill = SkillTable.getInstance().getInfo(n3, n4);
                if (skill == null) continue;
                for (EffectTemplate effectTemplate : skill.getEffectTemplates()) {
                    Env env;
                    Effect effect;
                    if (effectTemplate == null || (effect = effectTemplate.getEffect(env = new Env(playable, playable, skill))) == null || effect.isOneTime()) continue;
                    effect.setCount(n5);
                    effect.setPeriod(n5 == 1 ? l2 - l : l2);
                    playable.getEffectList().addEffect(effect);
                }
            }
            DbUtils.closeQuietly(preparedStatement, resultSet);
            preparedStatement = connection.prepareStatement("DELETE FROM `character_effects_save` WHERE `object_id` = ? AND `id`=?");
            preparedStatement.setInt(1, n2);
            preparedStatement.setInt(2, n);
            preparedStatement.execute();
            DbUtils.close(preparedStatement);
        }
        catch (Exception exception) {
            _log.error("Could not restore active effects data!", exception);
        }
        finally {
            DbUtils.closeQuietly(connection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteEffects(int n, int n2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM `character_effects_save` WHERE `object_id` = ? AND `id`=?");
            preparedStatement.setInt(1, n);
            preparedStatement.setInt(2, 100000 + n2);
            preparedStatement.execute();
        }
        catch (Exception exception) {
            try {
                _log.error("Could not delete effects active effects data!" + exception, exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(Playable playable) {
        Statement statement;
        Connection connection;
        block12: {
            int n;
            int n2;
            if (playable.isPlayer()) {
                n2 = playable.getObjectId();
                n = ((Player)playable).getActiveClassId();
            } else if (playable.isSummon()) {
                n2 = playable.getPlayer().getObjectId();
                n = ((SummonInstance)playable).getEffectIdentifier() + 100000;
            } else {
                return;
            }
            List<Effect> list = playable.getEffectList().getAllEffects();
            if (list.isEmpty()) {
                return;
            }
            connection = null;
            statement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                statement = connection.createStatement();
                int n3 = 0;
                SqlBatch sqlBatch = new SqlBatch("INSERT IGNORE INTO `character_effects_save` (`object_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`order`,`id`) VALUES");
                for (Effect effect : list) {
                    StringBuilder stringBuilder;
                    if (effect == null || !effect.isInUse() || effect.getSkill().isToggle() || effect.getEffectType() == EffectType.HealOverTime || effect.getEffectType() == EffectType.CombatPointHealOverTime) continue;
                    if (effect.isSaveable()) {
                        stringBuilder = new StringBuilder("(");
                        stringBuilder.append(n2).append(",");
                        stringBuilder.append(effect.getSkill().getId()).append(",");
                        stringBuilder.append(effect.getSkill().getLevel()).append(",");
                        stringBuilder.append(effect.getCount()).append(",");
                        stringBuilder.append(effect.getTime()).append(",");
                        stringBuilder.append(effect.getPeriod()).append(",");
                        stringBuilder.append(n3).append(",");
                        stringBuilder.append(n).append(")");
                        sqlBatch.write(stringBuilder.toString());
                    }
                    while ((effect = effect.getNext()) != null && effect.isSaveable()) {
                        stringBuilder = new StringBuilder("(");
                        stringBuilder.append(n2).append(",");
                        stringBuilder.append(effect.getSkill().getId()).append(",");
                        stringBuilder.append(effect.getSkill().getLevel()).append(",");
                        stringBuilder.append(effect.getCount()).append(",");
                        stringBuilder.append(effect.getTime()).append(",");
                        stringBuilder.append(effect.getPeriod()).append(",");
                        stringBuilder.append(n3).append(",");
                        stringBuilder.append(n).append(")");
                        sqlBatch.write(stringBuilder.toString());
                    }
                    ++n3;
                }
                if (sqlBatch.isEmpty()) break block12;
                statement.executeUpdate(sqlBatch.close());
            }
            catch (Exception exception) {
                try {
                    _log.error("Could not store active effects data!", exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, statement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, statement);
            }
        }
        DbUtils.closeQuietly(connection, statement);
    }
}

