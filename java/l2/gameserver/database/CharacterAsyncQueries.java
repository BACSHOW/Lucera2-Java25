package l2.gameserver.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utilitário de Consultas Assíncronas Não-Bloqueantes com Virtual Threads e Reader Pool.
 * Descarrega queries de leitura pesadas sem concorrer com ticks de IA ou com o Writer Pool.
 */
public final class CharacterAsyncQueries {
    private static final Logger LOG = LoggerFactory.getLogger(CharacterAsyncQueries.class);
    
    // Pool de Virtual Threads (Loom / Java 21+) para I/O concorrente de banco de dados
    private static final ExecutorService VIRTUAL_DB_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    private CharacterAsyncQueries() {}

    /**
     * Busca o nome do personagem pelo seu objId de forma assíncrona.
     */
    public static CompletableFuture<String> getCharacterNameAsync(int objId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseFactory.getInstance().getReadConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT `char_name` FROM `characters` WHERE `obj_Id`=? LIMIT 1")) {
                ps.setInt(1, objId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("char_name");
                    }
                }
            } catch (SQLException e) {
                LOG.error("CharacterAsyncQueries: Erro ao buscar nome para objId {}", objId, e);
            }
            return null;
        }, VIRTUAL_DB_EXECUTOR);
    }

    /**
     * Busca a contagem de membros online de um clã de forma assíncrona.
     */
    public static CompletableFuture<Integer> getClanOnlineMembersCountAsync(int clanId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseFactory.getInstance().getReadConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT COUNT(*) FROM `characters` WHERE `clanid`=? AND `online`=1")) {
                ps.setInt(1, clanId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                LOG.error("CharacterAsyncQueries: Erro ao contar membros online do clã {}", clanId, e);
            }
            return 0;
        }, VIRTUAL_DB_EXECUTOR);
    }
}
