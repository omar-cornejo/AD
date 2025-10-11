/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alumne
 */
public class ImagenDAO {

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
                // Ignorar
            }
        }
    }

    public Imagen findById(int id) throws SQLException, ClassNotFoundException {
        Imagen img = null;
        try {
            open();
            String sql = "SELECT * FROM imagenes WHERE idImagen = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        img = mapImagen(rs);
                    }
                }
            }
        } finally {
            close();
        }
        return img;
    }

    public List<Imagen> findAll() throws SQLException, ClassNotFoundException {
        List<Imagen> lista = new ArrayList<>();
        try {
            open();
            String sql = "SELECT * FROM imagenes";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapImagen(rs));
                }
            }
        } finally {
            close();
        }
        return lista;
    }

    public List<Imagen> search(String titulo, String autor, String palabrasClave, String fechaCreacion)
            throws SQLException, ClassNotFoundException {
        List<Imagen> lista = new ArrayList<>();
        try {
            open();
            StringBuilder sql = new StringBuilder("SELECT * FROM imagenes WHERE 1=1");
            if (titulo != null && !titulo.isEmpty()) sql.append(" AND LOWER(titulo) LIKE ?");
            if (autor != null && !autor.isEmpty()) sql.append(" AND LOWER(autor) LIKE ?");
            if (palabrasClave != null && !palabrasClave.isEmpty()) sql.append(" AND LOWER(palabrasClave) LIKE ?");
            if (fechaCreacion != null && !fechaCreacion.isEmpty()) sql.append(" AND fechaCreacion = ?");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int index = 1;
                if (titulo != null && !titulo.isEmpty()) ps.setString(index++, "%" + titulo.toLowerCase() + "%");
                if (autor != null && !autor.isEmpty()) ps.setString(index++, "%" + autor.toLowerCase() + "%");
                if (palabrasClave != null && !palabrasClave.isEmpty()) ps.setString(index++, "%" + palabrasClave.toLowerCase() + "%");
                if (fechaCreacion != null && !fechaCreacion.isEmpty()) ps.setDate(index++, java.sql.Date.valueOf(fechaCreacion));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapImagen(rs));
                    }
                }
            }
        } finally {
            close();
        }
        return lista;
    }

    public boolean create(Imagen imagen) throws SQLException, ClassNotFoundException {
        try {
            open();
            String sql = "INSERT INTO imagenes (titulo, descripcion, palabrasClave, autor, creador, fechaCreacion, fechaAlta, nombreFichero, nombreOriginal) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, imagen.getTitulo());
                ps.setString(2, imagen.getDescripcion());
                ps.setString(3, imagen.getPalabrasClave());
                ps.setString(4, imagen.getAutor());
                ps.setString(5, imagen.getCreador());
                if (imagen.getFechaCreacion() != null)
                    ps.setDate(6, new java.sql.Date(imagen.getFechaCreacion().getTime()));
                else
                    ps.setNull(6, Types.DATE);
                if (imagen.getFechaAlta() != null)
                    ps.setDate(7, new java.sql.Date(imagen.getFechaAlta().getTime()));
                else
                    ps.setDate(7, new java.sql.Date(System.currentTimeMillis()));
                ps.setString(8, imagen.getNombreFichero());
                ps.setString(9, imagen.getNombreOriginal());
                int rows = ps.executeUpdate();
                return rows == 1;
            }
        } finally {
            close();
        }
    }

    public boolean update(Imagen imagen) throws SQLException, ClassNotFoundException {
        try {
            open();
            String sql = "UPDATE imagenes SET titulo=?, descripcion=?, palabrasClave=?, autor=?, creador=?, fechaCreacion=?, fechaAlta=?, nombreFichero=?, nombreOriginal=? WHERE idImagen=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, imagen.getTitulo());
                ps.setString(2, imagen.getDescripcion());
                ps.setString(3, imagen.getPalabrasClave());
                ps.setString(4, imagen.getAutor());
                ps.setString(5, imagen.getCreador());
                if (imagen.getFechaCreacion() != null)
                    ps.setDate(6, new java.sql.Date(imagen.getFechaCreacion().getTime()));
                else
                    ps.setNull(6, Types.DATE);
                ps.setDate(7, new java.sql.Date(imagen.getFechaAlta().getTime()));
                ps.setString(8, imagen.getNombreFichero());
                ps.setString(9, imagen.getNombreOriginal());
                ps.setInt(10, imagen.getId());
                int rows = ps.executeUpdate();
                return rows == 1;
            }
        } finally {
            close();
        }
    }

    public boolean delete(int id) throws SQLException, ClassNotFoundException {
        try {
            open();
            String sql = "DELETE FROM imagenes WHERE idImagen = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                return rows == 1;
            }
        } finally {
            close();
        }
    }

    private Imagen mapImagen(ResultSet rs) throws SQLException {
        Imagen img = new Imagen();
        img.setId(rs.getInt("idImagen"));
        img.setTitulo(rs.getString("titulo"));
        img.setDescripcion(rs.getString("descripcion"));
        img.setPalabrasClave(rs.getString("palabrasClave"));
        img.setAutor(rs.getString("autor"));
        img.setCreador(rs.getString("creador"));
        img.setFechaCreacion(rs.getDate("fechaCreacion"));
        img.setFechaAlta(rs.getDate("fechaAlta"));
        img.setNombreFichero(rs.getString("nombreFichero"));
        img.setNombreOriginal(rs.getString("nombreOriginal"));
        return img;
    }
}
