package com.studentmanager.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection and initialization class
 */
public class DatabaseConnection {
    private static final String DATABASE_NAME = "student_management.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_NAME;
    private static Connection connection = null;

    /**
     * Get database connection
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DATABASE_URL);
                System.out.println("Database connection established successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
            System.err.println("Please add sqlite-jdbc jar to your classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Initialize database tables
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                createTables(conn);
                System.out.println("Database initialized successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create necessary tables
     * @param conn Database connection
     * @throws SQLException
     */
    private static void createTables(Connection conn) throws SQLException {
        // Create students table
        String createStudentsTable = """
            CREATE TABLE IF NOT EXISTS students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id VARCHAR(20) UNIQUE NOT NULL,
                full_name VARCHAR(100) NOT NULL,
                email VARCHAR(100),
                phone_number VARCHAR(15),
                major VARCHAR(100)
            )
        """;

        // Create grades table
        String createGradesTable = """
            CREATE TABLE IF NOT EXISTS grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                subject VARCHAR(100) NOT NULL,
                score REAL NOT NULL CHECK(score >= 0 AND score <= 10),
                coefficient REAL NOT NULL DEFAULT 1.0,
                semester VARCHAR(20) NOT NULL,
                year INTEGER NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
            )
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createStudentsTable);
            stmt.execute(createGradesTable);
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}