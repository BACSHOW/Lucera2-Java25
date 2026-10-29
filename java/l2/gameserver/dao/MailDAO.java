package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import l2.commons.dao.JdbcDAO;
import l2.commons.dao.JdbcEntityState;
import l2.commons.dao.JdbcEntityStats;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailDAO implements JdbcDAO<Integer, Mail> {
   private static final Logger _log = LoggerFactory.getLogger(MailDAO.class);
   private static final String uZ = "SELECT sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread FROM mail WHERE message_id = ?";
   private static final String va = "INSERT INTO mail(sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread) VALUES (?,?,?,?,?,?,?,?,?,?)";
   private static final String vb = "UPDATE mail SET sender_id = ?, sender_name = ?, receiver_id = ?, receiver_name = ?, expire_time = ?, topic = ?, body = ?, price = ?, type = ?, unread = ? WHERE message_id = ?";
   private static final String vc = "DELETE FROM mail WHERE message_id = ?";
   private static final String vd = "SELECT message_id FROM mail WHERE expire_time <= ?";
   private static final String ve = "SELECT message_id FROM character_mail WHERE char_id = ? AND is_sender = ?";
   private static final String vf = "INSERT INTO character_mail(char_id, message_id, is_sender) VALUES (?,?,?)";
   private static final String vg = "DELETE FROM character_mail WHERE char_id = ? AND message_id = ? AND is_sender = ?";
   private static final String vh = "SELECT item_id FROM mail_attachments WHERE message_id = ?";
   private static final String vi = "REPLACE INTO mail_attachments(message_id, item_id) VALUES (?,?)";
   private static final String vj = "DELETE FROM mail_attachments WHERE message_id = ?";
   private static final MailDAO vk = new MailDAO();
   private AtomicLong vl = new AtomicLong();
   private AtomicLong vm = new AtomicLong();
   private AtomicLong vn = new AtomicLong();
   private AtomicLong vo = new AtomicLong();
   private final Cache ur;
   private final JdbcEntityStats vp = new JdbcEntityStats() {
      @Override
      public long getLoadCount() {
         return MailDAO.this.vl.get();
      }

      @Override
      public long getInsertCount() {
         return MailDAO.this.vm.get();
      }

      @Override
      public long getUpdateCount() {
         return MailDAO.this.vn.get();
      }

      @Override
      public long getDeleteCount() {
         return MailDAO.this.vo.get();
      }
   };

   public static MailDAO getInstance() {
      return vk;
   }

   private MailDAO() {
      this.ur = CacheManager.getInstance().getCache(Mail.class.getName());
   }

   public Cache getCache() {
      return this.ur;
   }

   @Override
   public JdbcEntityStats getStats() {
      return this.vp;
   }

   private void a(Mail var1) throws SQLException {
      Connection var2 = null;
      PreparedStatement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement(
            "INSERT INTO mail(sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread) VALUES (?,?,?,?,?,?,?,?,?,?)",
            1
         );
         var3.setInt(1, var1.getSenderId());
         var3.setString(2, var1.getSenderName());
         var3.setInt(3, var1.getReceiverId());
         var3.setString(4, var1.getReceiverName());
         var3.setInt(5, var1.getExpireTime());
         var3.setString(6, var1.getTopic());
         var3.setString(7, var1.getBody());
         var3.setLong(8, var1.getPrice());
         var3.setInt(9, var1.getType().ordinal());
         var3.setBoolean(10, var1.isUnread());
         var3.execute();
         var4 = var3.getGeneratedKeys();
         var4.next();
         var1.setMessageId(var4.getInt(1));
         if (!var1.getAttachments().isEmpty()) {
            DbUtils.close(var3);
            var3 = var2.prepareStatement("REPLACE INTO mail_attachments(message_id, item_id) VALUES (?,?)");

            for (ItemInstance var6 : var1.getAttachments()) {
               var3.setInt(1, var1.getMessageId());
               var3.setInt(2, var6.getObjectId());
               var3.addBatch();
            }

            var3.executeBatch();
         }

         DbUtils.close(var3);
         if (var1.getType() == Mail.SenderType.NORMAL) {
            var3 = var2.prepareStatement("INSERT INTO character_mail(char_id, message_id, is_sender) VALUES (?,?,?)");
            var3.setInt(1, var1.getSenderId());
            var3.setInt(2, var1.getMessageId());
            var3.setBoolean(3, true);
            var3.execute();
         }

         DbUtils.close(var3);
         var3 = var2.prepareStatement("INSERT INTO character_mail(char_id, message_id, is_sender) VALUES (?,?,?)");
         var3.setInt(1, var1.getReceiverId());
         var3.setInt(2, var1.getMessageId());
         var3.setBoolean(3, false);
         var3.execute();
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      this.vm.incrementAndGet();
   }

   private Mail n(int var1) throws SQLException {
      Mail var2 = null;
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement(
            "SELECT sender_id, sender_name, receiver_id, receiver_name, expire_time, topic, body, price, type, unread FROM mail WHERE message_id = ?"
         );
         var4.setInt(1, var1);
         var5 = var4.executeQuery();
         if (var5.next()) {
            var2 = new Mail();
            var2.setMessageId(var1);
            var2.setSenderId(var5.getInt(1));
            var2.setSenderName(var5.getString(2));
            var2.setReceiverId(var5.getInt(3));
            var2.setReceiverName(var5.getString(4));
            var2.setExpireTime(var5.getInt(5));
            var2.setTopic(var5.getString(6));
            var2.setBody(var5.getString(7));
            var2.setPrice(var5.getLong(8));
            var2.setType(Mail.SenderType.VALUES[var5.getInt(9)]);
            var2.setUnread(var5.getBoolean(10));
            DbUtils.close(var4, var5);
            var4 = var3.prepareStatement("SELECT item_id FROM mail_attachments WHERE message_id = ?");
            var4.setInt(1, var1);
            var5 = var4.executeQuery();

            while (var5.next()) {
               int var7 = var5.getInt(1);
               ItemInstance var6 = ItemsDAO.getInstance().load(var7);
               if (var6 != null) {
                  var2.addAttachment(var6);
               }
            }
         }
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      this.vl.incrementAndGet();
      return var2;
   }

   private void b(Mail var1) throws SQLException {
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement(
            "UPDATE mail SET sender_id = ?, sender_name = ?, receiver_id = ?, receiver_name = ?, expire_time = ?, topic = ?, body = ?, price = ?, type = ?, unread = ? WHERE message_id = ?"
         );
         var3.setInt(1, var1.getSenderId());
         var3.setString(2, var1.getSenderName());
         var3.setInt(3, var1.getReceiverId());
         var3.setString(4, var1.getReceiverName());
         var3.setInt(5, var1.getExpireTime());
         var3.setString(6, var1.getTopic());
         var3.setString(7, var1.getBody());
         var3.setLong(8, var1.getPrice());
         var3.setInt(9, var1.getType().ordinal());
         var3.setBoolean(10, var1.isUnread());
         var3.setInt(11, var1.getMessageId());
         var3.execute();
         if (var1.getAttachments().isEmpty()) {
            DbUtils.close(var3);
            var3 = var2.prepareStatement("DELETE FROM mail_attachments WHERE message_id = ?");
            var3.setInt(1, var1.getMessageId());
            var3.execute();
         }
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }

      this.vn.incrementAndGet();
   }

   private void c(Mail var1) throws SQLException {
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("DELETE FROM mail WHERE message_id = ?");
         var3.setInt(1, var1.getMessageId());
         var3.execute();
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }

      this.vo.incrementAndGet();
   }

   private List<Mail> a(int var1, boolean var2) {
      List var3 = Collections.emptyList();
      Connection var4 = null;
      PreparedStatement var5 = null;
      ResultSet var6 = null;

      try {
         var4 = DatabaseFactory.getInstance().getConnection();
         var5 = var4.prepareStatement("SELECT message_id FROM character_mail WHERE char_id = ? AND is_sender = ?");
         var5.setInt(1, var1);
         var5.setBoolean(2, var2);
         var6 = var5.executeQuery();
         var3 = new ArrayList();

         while (var6.next()) {
            var3.add(var6.getInt(1));
         }
      } catch (SQLException var11) {
         _log.error("Error while restore mail of owner : " + var1, var11);
         var3.clear();
      } finally {
         DbUtils.closeQuietly(var4, var5, var6);
      }

      return this.load(var3);
   }

   private boolean a(int var1, int var2, boolean var3) {
      Connection var4 = null;
      PreparedStatement var5 = null;

      try {
         var4 = DatabaseFactory.getInstance().getConnection();
         var5 = var4.prepareStatement("DELETE FROM character_mail WHERE char_id = ? AND message_id = ? AND is_sender = ?");
         var5.setInt(1, var1);
         var5.setInt(2, var2);
         var5.setBoolean(3, var3);
         return var5.execute();
      } catch (SQLException var11) {
         _log.error("Error while deleting mail of owner : " + var1, var11);
         return false;
      } finally {
         DbUtils.closeQuietly(var4, var5);
      }
   }

   public List<Mail> getReceivedMailByOwnerId(int var1) {
      return this.a(var1, false);
   }

   public List<Mail> getSentMailByOwnerId(int var1) {
      return this.a(var1, true);
   }

   public Mail getReceivedMailByMailId(int var1, int var2) {
      for (Mail var5 : this.a(var1, false)) {
         if (var5.getMessageId() == var2) {
            return var5;
         }
      }

      return null;
   }

   public Mail getSentMailByMailId(int var1, int var2) {
      for (Mail var5 : this.a(var1, true)) {
         if (var5.getMessageId() == var2) {
            return var5;
         }
      }

      return null;
   }

   public boolean deleteReceivedMailByMailId(int var1, int var2) {
      return this.a(var1, var2, false);
   }

   public boolean deleteSentMailByMailId(int var1, int var2) {
      return this.a(var1, var2, true);
   }

   public List<Mail> getExpiredMail(int var1) {
      List var2 = Collections.emptyList();
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT message_id FROM mail WHERE expire_time <= ?");
         var4.setInt(1, var1);
         var5 = var4.executeQuery();
         var2 = new ArrayList();

         while (var5.next()) {
            var2.add(var5.getInt(1));
         }
      } catch (SQLException var10) {
         _log.error("Error while restore expired mail!", var10);
         var2.clear();
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return this.load(var2);
   }

   public Mail load(Integer var1) {
      Element var3 = this.ur.get(var1);
      if (var3 != null) {
         return (Mail)var3.getObjectValue();
      }

      try {
         Mail var2 = this.n(var1);
         if (var2 != null) {
            var2.setJdbcState(JdbcEntityState.STORED);
            this.ur.put(new Element(var2.getMessageId(), var2));
         }

         return var2;
      } catch (SQLException var5) {
         _log.error("Error while restoring mail : " + var1, var5);
         return null;
      }
   }

   public List<Mail> load(Collection<Integer> var1) {
      if (var1.isEmpty()) {
         return Collections.emptyList();
      }

      ArrayList var2 = new ArrayList(var1.size());

      for (Integer var5 : var1) {
         Mail var3 = this.load(var5);
         if (var3 != null) {
            var2.add(var3);
         }
      }

      return var2;
   }

   public void save(Mail var1) {
      if (var1.getJdbcState().isSavable()) {
         try {
            this.a(var1);
            var1.setJdbcState(JdbcEntityState.STORED);
         } catch (SQLException var3) {
            _log.error("Error while saving mail!", var3);
            return;
         }

         this.ur.put(new Element(var1.getMessageId(), var1));
      }
   }

   public void update(Mail var1) {
      if (var1.getJdbcState().isUpdatable()) {
         try {
            this.b(var1);
            var1.setJdbcState(JdbcEntityState.STORED);
         } catch (SQLException var3) {
            _log.error("Error while updating mail : " + var1.getMessageId(), var3);
            return;
         }

         this.ur.putIfAbsent(new Element(var1.getMessageId(), var1));
      }
   }

   public void saveOrUpdate(Mail var1) {
      if (var1.getJdbcState().isSavable()) {
         this.save(var1);
      } else if (var1.getJdbcState().isUpdatable()) {
         this.update(var1);
      }
   }

   public void delete(Mail var1) {
      if (var1.getJdbcState().isDeletable()) {
         try {
            this.c(var1);
            var1.setJdbcState(JdbcEntityState.DELETED);
         } catch (SQLException var3) {
            _log.error("Error while deleting mail : " + var1.getMessageId(), var3);
            return;
         }

         this.ur.remove(var1.getExpireTime());
      }
   }
}
