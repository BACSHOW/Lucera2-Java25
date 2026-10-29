/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.taskmanager.Task;
import l2.gameserver.taskmanager.TaskTypes;
import l2.gameserver.taskmanager.tasks.RecommendationUpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TaskManager {
    private static final Logger _log = LoggerFactory.getLogger(TaskManager.class);
    private static TaskManager arw;
    static final String[] SQL_STATEMENTS;
    private final Map<String, Task> arx = new ConcurrentHashMap<String, Task>();
    final List<ExecutedTask> _currentTasks = new ArrayList<ExecutedTask>();

    public static TaskManager getInstance() {
        if (arw == null) {
            arw = new TaskManager();
        }
        return arw;
    }

    public TaskManager() {
        this.init();
        this.dC();
    }

    public void init() {
        this.registerTask(new RecommendationUpdateTask());
    }

    public void registerTask(Task task) {
        String string = task.getName();
        if (!this.arx.containsKey(string)) {
            this.arx.put(string, task);
            task.init();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void dC() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS[0]);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ExecutedTask executedTask;
                TaskTypes taskTypes;
                Task task = this.arx.get(resultSet.getString("task"));
                if (task == null || (taskTypes = TaskTypes.valueOf(resultSet.getString("type"))) == TaskTypes.TYPE_NONE || !this.a(executedTask = new ExecutedTask(task, taskTypes, resultSet))) continue;
                this._currentTasks.add(executedTask);
            }
        }
        catch (Exception exception) {
            try {
                _log.error("error while loading Global Task table " + exception);
                _log.error("", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
    }

    private boolean a(ExecutedTask executedTask) {
        ThreadPoolManager threadPoolManager = ThreadPoolManager.getInstance();
        TaskTypes taskTypes = executedTask.getType();
        if (taskTypes == TaskTypes.TYPE_STARTUP) {
            executedTask.run();
            return false;
        }
        if (taskTypes == TaskTypes.TYPE_SHEDULED) {
            long l = Long.valueOf(executedTask.getParams()[0]);
            executedTask._scheduled = threadPoolManager.schedule(executedTask, l);
            return true;
        }
        if (taskTypes == TaskTypes.TYPE_FIXED_SHEDULED) {
            long l = Long.valueOf(executedTask.getParams()[0]);
            long l2 = Long.valueOf(executedTask.getParams()[1]);
            executedTask._scheduled = threadPoolManager.scheduleAtFixedRate(executedTask, l, l2);
            return true;
        }
        if (taskTypes == TaskTypes.TYPE_TIME) {
            try {
                Date date = DateFormat.getInstance().parse(executedTask.getParams()[0]);
                long l = date.getTime() - System.currentTimeMillis();
                if (l >= 0L) {
                    executedTask._scheduled = threadPoolManager.schedule(executedTask, l);
                    return true;
                }
                _log.info("Task " + executedTask.getId() + " is obsoleted.");
            }
            catch (Exception exception) {}
        } else if (taskTypes == TaskTypes.TYPE_SPECIAL) {
            ScheduledFuture<?> scheduledFuture = executedTask.getTask().launchSpecial(executedTask);
            if (scheduledFuture != null) {
                executedTask._scheduled = scheduledFuture;
                return true;
            }
        } else if (taskTypes == TaskTypes.TYPE_GLOBAL_TASK) {
            long l = Long.valueOf(executedTask.getParams()[0]) * 86400000L;
            String[] stringArray = executedTask.getParams()[1].split(":");
            if (stringArray.length != 3) {
                _log.warn("Task " + executedTask.getId() + " has incorrect parameters");
                return false;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(executedTask.getLastActivation() + l);
            Calendar calendar2 = Calendar.getInstance();
            try {
                calendar2.set(11, Integer.valueOf(stringArray[0]));
                calendar2.set(12, Integer.valueOf(stringArray[1]));
                calendar2.set(13, Integer.valueOf(stringArray[2]));
            }
            catch (Exception exception) {
                _log.warn("Bad parameter on task " + executedTask.getId() + ": " + exception.getMessage());
                return false;
            }
            long l3 = calendar2.getTimeInMillis() - System.currentTimeMillis();
            if (calendar.after(calendar2) || l3 < 0L) {
                l3 += l;
            }
            executedTask._scheduled = threadPoolManager.scheduleAtFixedRate(executedTask, l3, l);
            return true;
        }
        return false;
    }

    public static boolean addUniqueTask(String string, TaskTypes taskTypes, String string2, String string3, String string4) {
        return TaskManager.addUniqueTask(string, taskTypes, string2, string3, string4, 0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean addUniqueTask(String string, TaskTypes taskTypes, String string2, String string3, String string4, long l) {
        boolean bl;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS[2]);
            preparedStatement.setString(1, string);
            resultSet = preparedStatement.executeQuery();
            boolean bl2 = resultSet.next();
            DbUtils.close(preparedStatement, resultSet);
            if (!bl2) {
                preparedStatement = connection.prepareStatement(SQL_STATEMENTS[3]);
                preparedStatement.setString(1, string);
                preparedStatement.setString(2, taskTypes.toString());
                preparedStatement.setLong(3, l / 1000L);
                preparedStatement.setString(4, string2);
                preparedStatement.setString(5, string3);
                preparedStatement.setString(6, string4);
                preparedStatement.execute();
            }
            bl = true;
        }
        catch (SQLException sQLException) {
            try {
                _log.warn("cannot add the unique task: " + sQLException.getMessage());
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
            return false;
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        return bl;
    }

    public static boolean addTask(String string, TaskTypes taskTypes, String string2, String string3, String string4) {
        return TaskManager.addTask(string, taskTypes, string2, string3, string4, 0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static boolean addTask(String string, TaskTypes taskTypes, String string2, String string3, String string4, long l) {
        boolean bl;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement(SQL_STATEMENTS[3]);
            preparedStatement.setString(1, string);
            preparedStatement.setString(2, taskTypes.toString());
            preparedStatement.setLong(3, l / 1000L);
            preparedStatement.setString(4, string2);
            preparedStatement.setString(5, string3);
            preparedStatement.setString(6, string4);
            preparedStatement.execute();
            bl = true;
        }
        catch (SQLException sQLException) {
            try {
                _log.warn("cannot add the task:\t" + sQLException.getMessage());
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement);
            return false;
        }
        DbUtils.closeQuietly(connection, preparedStatement);
        return bl;
    }

    static {
        SQL_STATEMENTS = new String[]{"SELECT `id`,`task`,`type`,`last_activation`,`param1`,`param2`,`param3` FROM `global_tasks`", "UPDATE `global_tasks` SET `last_activation`=? WHERE `id`=?", "SELECT `id` FROM `global_tasks` WHERE `task`=?", "INSERT INTO `global_tasks` (`task`,`type`,`last_activation`,`param1`,`param2`,`param3`) VALUES(?,?,?,?,?,?)"};
    }

    public class ExecutedTask
    extends RunnableImpl {
        int _id;
        long _lastActivation;
        Task _task;
        TaskTypes _type;
        String[] _params;
        ScheduledFuture<?> _scheduled;

        public ExecutedTask(Task task, TaskTypes taskTypes, ResultSet resultSet) throws SQLException {
            this._task = task;
            this._type = taskTypes;
            this._id = resultSet.getInt("id");
            this._lastActivation = resultSet.getLong("last_activation") * 1000L;
            this._params = new String[]{resultSet.getString("param1"), resultSet.getString("param2"), resultSet.getString("param3")};
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void runImpl() throws Exception {
            this._task.onTimeElapsed(this);
            this._lastActivation = System.currentTimeMillis();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement(SQL_STATEMENTS[1]);
                preparedStatement.setLong(1, this._lastActivation / 1000L);
                preparedStatement.setInt(2, this._id);
                preparedStatement.executeUpdate();
            }
            catch (SQLException sQLException) {
                try {
                    _log.warn("cannot updated the Global Task " + this._id + ": " + sQLException.getMessage());
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
            DbUtils.closeQuietly(connection, preparedStatement);
            if (this._type == TaskTypes.TYPE_SHEDULED || this._type == TaskTypes.TYPE_TIME) {
                this.stopTask();
            }
        }

        public boolean equals(Object object) {
            return this._id == ((ExecutedTask)object)._id;
        }

        public Task getTask() {
            return this._task;
        }

        public TaskTypes getType() {
            return this._type;
        }

        public int getId() {
            return this._id;
        }

        public String[] getParams() {
            return this._params;
        }

        public long getLastActivation() {
            return this._lastActivation;
        }

        public void stopTask() {
            this._task.onDestroy();
            if (this._scheduled != null) {
                this._scheduled.cancel(false);
            }
            TaskManager.this._currentTasks.remove(this);
        }
    }
}

