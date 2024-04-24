package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {

    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try{

                Properties prop = loadProperties();
                String url = prop.getProperty("dburl");
                conn = DriverManager.getConnection(url, prop);

            } catch (SQLException e) {

                throw new RuntimeException(e);
            }

        }
        return conn;
    }

    public static void closeConnection() {

        if (conn != null) try {
            conn.close();

        } catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
    }
    private static Properties loadProperties() {

        try(FileInputStream fis = new FileInputStream("db.properties")) {

            Properties prop = new Properties();
            prop.load(fis);
            return prop;

        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    public static void closeStatement(Statement stmt) {

        if (stmt != null) try {

            stmt.close();
        } catch (SQLException e) {

            throw new DbException(e.getMessage());
        }
    }

    public static void closeResultSet(ResultSet rs) {

        if (rs != null) try {

            rs.close();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    public static void closePreparedStatement(PreparedStatement stmt) {
        if (stmt != null) try {
            stmt.close();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }
}
