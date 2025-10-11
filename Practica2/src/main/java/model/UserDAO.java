/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author alumne
 */
public class UserDAO {

    private static final String JDBC_URL = "jdbc:derby://localhost:1527/DB_practica2;user=lab;password=lab";
    private static final String JDBC_DRIVER = "org.apache.derby.jdbc.ClientDriver";

    private Connection conn;

    public void open() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(JDBC_URL);
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {

            }
        }
    }

    public int authenticate(String usuario, String password) throws SQLException, ClassNotFoundException {
        try {
            open();
            String sql = "SELECT password FROM Users WHERE id_usuario = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String pw = rs.getString("password");
                        if (pw != null && pw.equals(password)) {
                            return 0;
                        } else {
                            return 1;
                        }
                    } else {
                        return 5;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 100;
        } finally {
            close();
        }
    }

    public boolean exists(String usuario) throws SQLException, ClassNotFoundException {
        try {
            open();
            String sql = "SELECT 1 FROM Users WHERE id_usuario = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } finally {
            close();
        }
    }

    public boolean create(String usuario, String password) throws SQLException, ClassNotFoundException {
        try {
            open();
            String sql = "INSERT INTO Users (id_usuario, password) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario);
                ps.setString(2, password);
                int rows = ps.executeUpdate();
                return rows == 1;
            }
        } finally {
            close();
        }
    }
}
