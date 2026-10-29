/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.data.xml.holder.CharacterTemplateHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.CharSelectInfoPackage;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.templates.PlayerTemplate;
import l2.gameserver.utils.AutoBan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterSelectionInfo
extends L2GameServerPacket {
    private static final Logger _log = LoggerFactory.getLogger(CharacterSelectionInfo.class);
    private String aaA;
    private int pp;
    private CharSelectInfoPackage[] afm;

    public CharacterSelectionInfo(String string, int n) {
        this.pp = n;
        this.aaA = string;
        this.afm = CharacterSelectionInfo.loadCharacterSelectInfo(string);
    }

    public CharSelectInfoPackage[] getCharInfo() {
        return this.afm;
    }

    @Override
    protected final void writeImpl() {
        int n;
        int n2 = this.afm != null ? this.afm.length : 0;
        this.writeC(19);
        this.writeD(n2);
        long l = -1L;
        int n3 = -1;
        for (n = 0; n < n2; ++n) {
            if (l >= this.afm[n].getLastAccess()) continue;
            l = this.afm[n].getLastAccess();
            n3 = n;
        }
        for (n = 0; n < n2; ++n) {
            CharSelectInfoPackage charSelectInfoPackage = this.afm[n];
            this.writeS(charSelectInfoPackage.getName());
            this.writeD(charSelectInfoPackage.getCharId());
            this.writeS(this.aaA);
            this.writeD(this.pp);
            this.writeD(charSelectInfoPackage.getClanId());
            this.writeD(0);
            this.writeD(charSelectInfoPackage.getSex());
            this.writeD(charSelectInfoPackage.getRace());
            this.writeD(charSelectInfoPackage.getBaseClassId());
            this.writeD(1);
            this.writeD(charSelectInfoPackage.getX());
            this.writeD(charSelectInfoPackage.getY());
            this.writeD(charSelectInfoPackage.getZ());
            this.writeF(charSelectInfoPackage.getCurrentHp());
            this.writeF(charSelectInfoPackage.getCurrentMp());
            this.writeD(charSelectInfoPackage.getSp());
            this.writeQ(charSelectInfoPackage.getExp());
            int n4 = charSelectInfoPackage.getLevel();
            this.writeD(n4);
            this.writeD(charSelectInfoPackage.getKarma());
            this.writeD(charSelectInfoPackage.getPk());
            this.writeD(charSelectInfoPackage.getPvP());
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            this.writeD(0);
            for (int n5 : Inventory.PAPERDOLL_ORDER) {
                this.writeD(charSelectInfoPackage.getPaperdollObjectId(n5));
            }
            for (int n5 : Inventory.PAPERDOLL_ORDER) {
                this.writeD(charSelectInfoPackage.getPaperdollItemId(n5));
            }
            this.writeD(charSelectInfoPackage.getHairStyle());
            this.writeD(charSelectInfoPackage.getHairColor());
            this.writeD(charSelectInfoPackage.getFace());
            this.writeF(charSelectInfoPackage.getMaxHp());
            this.writeF(charSelectInfoPackage.getMaxMp());
            this.writeD(charSelectInfoPackage.getAccessLevel() > -100 ? charSelectInfoPackage.getDeleteTimer() : -1);
            this.writeD(charSelectInfoPackage.getClassId());
            this.writeD(n == n3 ? 1 : 0);
            this.writeC(Math.min(charSelectInfoPackage.getPaperdollEnchantEffect(7), 127));
            this.writeD(charSelectInfoPackage.getPaperdollAugmentationId(7));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CharSelectInfoPackage[] loadCharacterSelectInfo(String string) {
        ArrayList<CharSelectInfoPackage> arrayList = new ArrayList<CharSelectInfoPackage>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM `characters` AS `c` LEFT JOIN `character_subclasses` AS `cs` ON (`c`.`obj_Id`=`cs`.`char_obj_id` AND `cs`.`active`=1) WHERE `account_name`=? LIMIT 7");
            preparedStatement.setString(1, string);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CharSelectInfoPackage charSelectInfoPackage = CharacterSelectionInfo.b(resultSet);
                if (charSelectInfoPackage == null) continue;
                arrayList.add(charSelectInfoPackage);
            }
        }
        catch (Exception exception) {
            try {
                _log.error("could not restore charinfo:", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return arrayList.toArray(new CharSelectInfoPackage[arrayList.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int U(int n) {
        int n2 = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=? AND `isBase`=1");
            preparedStatement.setInt(1, n);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                n2 = resultSet.getInt("class_id");
            }
        }
        catch (Exception exception) {
            try {
                _log.error("could not restore base class id:", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return n2;
    }

    private static CharSelectInfoPackage b(ResultSet resultSet) {
        CharSelectInfoPackage charSelectInfoPackage = null;
        try {
            boolean bl;
            int n;
            int n2 = resultSet.getInt("obj_Id");
            int n3 = n = resultSet.getInt("class_id");
            boolean bl2 = bl = resultSet.getInt("isBase") > 0;
            if (!bl) {
                n3 = CharacterSelectionInfo.U(n2);
            }
            boolean bl3 = resultSet.getInt("sex") == 1;
            PlayerTemplate playerTemplate = CharacterTemplateHolder.getInstance().getTemplate(ClassId.getClassById(n3), !bl3);
            if (playerTemplate == null) {
                _log.error("restoreChar fail | templ == null | objectId: " + n2 + " | classid: " + n3 + " | female: " + bl3);
                return null;
            }
            String string = resultSet.getString("char_name");
            charSelectInfoPackage = new CharSelectInfoPackage(n2, string);
            charSelectInfoPackage.setLevel(resultSet.getInt("level"));
            charSelectInfoPackage.setMaxHp(resultSet.getInt("maxHp"));
            charSelectInfoPackage.setCurrentHp(resultSet.getDouble("curHp"));
            charSelectInfoPackage.setMaxMp(resultSet.getInt("maxMp"));
            charSelectInfoPackage.setCurrentMp(resultSet.getDouble("curMp"));
            charSelectInfoPackage.setX(resultSet.getInt("x"));
            charSelectInfoPackage.setY(resultSet.getInt("y"));
            charSelectInfoPackage.setZ(resultSet.getInt("z"));
            charSelectInfoPackage.setPk(resultSet.getInt("pkkills"));
            charSelectInfoPackage.setPvP(resultSet.getInt("pvpkills"));
            charSelectInfoPackage.setFace(resultSet.getInt("face"));
            charSelectInfoPackage.setHairStyle(resultSet.getInt("hairstyle"));
            charSelectInfoPackage.setHairColor(resultSet.getInt("haircolor"));
            charSelectInfoPackage.setSex(bl3 ? 1 : 0);
            charSelectInfoPackage.setExp(resultSet.getLong("exp"));
            charSelectInfoPackage.setSp(resultSet.getInt("sp"));
            charSelectInfoPackage.setClanId(resultSet.getInt("clanid"));
            charSelectInfoPackage.setKarma(resultSet.getInt("karma"));
            charSelectInfoPackage.setRace(playerTemplate.race.ordinal());
            charSelectInfoPackage.setClassId(n);
            charSelectInfoPackage.setBaseClassId(n3);
            long l = resultSet.getLong("deletetime");
            int n4 = 0;
            if (Config.DELETE_DAYS > 0) {
                if (l > 0L) {
                    l = (int)(System.currentTimeMillis() / 1000L - l);
                    n4 = (int)(l / 3600L / 24L);
                    if (n4 >= Config.DELETE_DAYS) {
                        CharacterDAO.getInstance().deleteCharacterDataByObjId(n2);
                        return null;
                    }
                    l = (long)(Config.DELETE_DAYS * 3600 * 24) - l;
                } else {
                    l = 0L;
                }
            }
            charSelectInfoPackage.setDeleteTimer((int)l);
            charSelectInfoPackage.setLastAccess(resultSet.getLong("lastAccess") * 1000L);
            charSelectInfoPackage.setAccessLevel(resultSet.getInt("accesslevel"));
            int n5 = resultSet.getInt("vitality") + (int)((double)(System.currentTimeMillis() - charSelectInfoPackage.getLastAccess()) / 15.0);
            if (n5 > 20000) {
                n5 = 20000;
            } else if (n5 < 0) {
                n5 = 0;
            }
            charSelectInfoPackage.setVitalityPoints(n5);
            if (charSelectInfoPackage.getAccessLevel() < 0 && !AutoBan.isBanned(n2)) {
                charSelectInfoPackage.setAccessLevel(0);
            }
        }
        catch (Exception exception) {
            _log.error("", exception);
        }
        return charSelectInfoPackage;
    }
}

