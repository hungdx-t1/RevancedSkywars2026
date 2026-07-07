package com.walrusone.skywarsreloaded.database;

import com.google.common.io.Resources;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class Database {
    private final String connectionUri;
    private final String username;
    private final String password;
    private Connection connection;

    public Database() throws ClassNotFoundException, SQLException {
        FileConfiguration config = SkyWarsReloaded.get().getConfig();
        final String hostname = config.getString("sqldatabase.hostname");
        final int port = config.getInt("sqldatabase.port");
        final String database = config.getString("sqldatabase.database");
        final boolean ssl = config.getBoolean("sqldatabase.ssl", true);
        final boolean verifyCert = ssl && config.getBoolean("sqldatabase.verifyCertificate", true);
        final boolean pubKeyRetrieval = config.getBoolean("sqldatabase.publicKeyRetrieval", false);

        connectionUri = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=%s&verifyServerCertificate=%s&allowPublicKeyRetrieval=%s",
                hostname, port, database, ssl, verifyCert, pubKeyRetrieval);
        username = config.getString("sqldatabase.username");
        password = config.getString("sqldatabase.password");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connect();
        } catch (SQLException e) {
            close();
            throw e;
        }
    }

    private void connect() throws SQLException {
        if (connection != null) {
            try {
                connection.createStatement().execute("SELECT 1;");
            } catch (SQLException e) {
                if (e.getSQLState().equals("08S01")) {
                    try {
                        connection.close();
                    } catch (SQLException ignored) { }
                }
            }
        }

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(connectionUri, username, password);
        }
    }

    Connection getConnection() {
        return connection;
    }

    private void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) { }
        connection = null;
    }

    boolean checkConnection() {
        try {
            connect();
        } catch (SQLException e) {
            close();
            e.printStackTrace();
            return true;
        }
        return false;
    }

    public void createTables() throws IOException, SQLException {
        URL resource = Resources.getResource(SkyWarsReloaded.class, "/tables.sql");
        String[] databaseStructure = Resources.toString(resource, StandardCharsets.UTF_8).split(";");
        connection.setAutoCommit(false);

        if (databaseStructure.length == 0) return;

        try (Statement stmt = connection.createStatement()) {
            for (String query : databaseStructure) {
                query = query.trim();
                if (query.isEmpty()) continue;
                stmt.execute(query);
            }
            connection.commit();
        }
    }

    boolean doesPlayerExist(String fId) {
        if (checkConnection()) return false;

        int count = 0;
        String query = "SELECT Count(`player_id`) FROM `sw_player` WHERE `uuid` = ? LIMIT 1;";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count > 0;
    }

    void createNewPlayer(String fId, String name) {
        if (checkConnection()) {
            return;
        }

        String query = "INSERT INTO `sw_player` (`player_id`, `uuid`, `player_name`, `wins`, `losses`, `kills`, `deaths`, `xp`, " +
                "`pareffect`, `proeffect`, `glasscolor`, `killsound`, `winsound`, `taunt`) VALUES (NULL, ?, ?, 0, 0, 0, 0, 0, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, fId);
            stmt.setString(2, name);
            stmt.setString(3, "none");
            stmt.setString(4, "none");
            stmt.setString(5, "none");
            stmt.setString(6, "none");
            stmt.setString(7, "none");
            stmt.setString(8, "none");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}