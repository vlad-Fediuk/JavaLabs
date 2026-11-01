package journal.repository.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    private static final String INIT_SCRIPT = "init.sql";
    private static final String SEED_SCRIPT = "seed.sql";

    public static void initialize() {
        String sql = loadScript(INIT_SCRIPT);
        executeScript(sql);
    }

    public static void seedIfEmpty() {
        if (isTableEmpty("students")) {
            String sql = loadScript(SEED_SCRIPT);
            executeScript(sql);
        }
    }

    private static boolean isTableEmpty(String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static String loadScript(String scriptName) {
        try (InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(scriptName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Script not found: " + scriptName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + scriptName, e);
        }
    }

    private static void executeScript(String sqlScript) {
        String[] statements = sqlScript.split(";");
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement()) {
            for (String rawStatement : statements) {
                String trimmed = rawStatement.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute database script", e);
        }
    }
}
