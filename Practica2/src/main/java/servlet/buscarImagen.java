/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Imagen;

/**
 *
 * @author alumne
 */
@WebServlet(name = "buscarImagen", urlPatterns = {"/buscarImagen"})
public class buscarImagen extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet buscarImagen</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet buscarImagen at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String palabrasClave = request.getParameter("palabrasClave");
        String fechaCreacion = request.getParameter("fechaCreacion");

        List<Imagen> resultados = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:derby://localhost:1527/DB_practica2;user=lab;password=lab")) {

            Class.forName("org.apache.derby.jdbc.ClientDriver");

            StringBuilder sql = new StringBuilder("SELECT * FROM imagenes WHERE 1=1 ");

            
            if (titulo != null && !titulo.isEmpty()) {
                String[] palabras = titulo.toLowerCase().split("\\s+");
                sql.append("AND (");
                for (int i = 0; i < palabras.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("LOWER(titulo) LIKE ?");
                }
                sql.append(") ");
            }

            
            if (autor != null && !autor.isEmpty()) {
                String[] palabras = autor.toLowerCase().split("\\s+");
                sql.append("AND (");
                for (int i = 0; i < palabras.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("LOWER(autor) LIKE ?");
                }
                sql.append(") ");
            }

            
            if (palabrasClave != null && !palabrasClave.isEmpty()) {
                String[] palabras = palabrasClave.toLowerCase().split("\\s+|,");
                sql.append("AND (");
                for (int i = 0; i < palabras.length; i++) {
                    if (i > 0) sql.append(" OR ");
                    sql.append("LOWER(palabrasClave) LIKE ?");
                }
                sql.append(") ");
            }

            
            if (fechaCreacion != null && !fechaCreacion.isEmpty()) {
                sql.append("AND fechaCreacion = ? ");
            }

            PreparedStatement ps = connection.prepareStatement(sql.toString());

            int index = 1;

            if (titulo != null && !titulo.isEmpty()) {
                for (String palabra : titulo.toLowerCase().split("\\s+")) {
                    ps.setString(index++, "%" + palabra + "%");
                }
            }

            if (autor != null && !autor.isEmpty()) {
                for (String palabra : autor.toLowerCase().split("\\s+")) {
                    ps.setString(index++, "%" + palabra + "%");
                }
            }

            if (palabrasClave != null && !palabrasClave.isEmpty()) {
                for (String palabra : palabrasClave.toLowerCase().split("\\s+|,")) {
                    ps.setString(index++, "%" + palabra + "%");
                }
            }

            if (fechaCreacion != null && !fechaCreacion.isEmpty()) {
                ps.setDate(index++, java.sql.Date.valueOf(fechaCreacion));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Imagen img = new Imagen();
                img.setId(rs.getInt("idImagen"));
                img.setTitulo(rs.getString("titulo"));
                img.setAutor(rs.getString("autor"));
                img.setFechaCreacion(rs.getDate("fechaCreacion"));
                img.setNombreOriginal(rs.getString("nombreOriginal"));
                img.setNombreFichero(rs.getString("nombreFichero"));
                resultados.add(img);
            }

            rs.close();
            ps.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("resultados", resultados);
        request.getRequestDispatcher("buscarImagen.jsp").forward(request, response);
    }
    

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
