/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.time.LocalDate;
import java.io.File;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.sql.DriverManager;
import java.sql.ResultSet;
import static java.time.LocalDateTime.now;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alumne
 */
@WebServlet(name = "registrarImagen", urlPatterns = {"/registrarImagen"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class registrarImagen extends HttpServlet {

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
            out.println("<title>Servlet registrarImagen</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet registrarImagen at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
        request.setCharacterEncoding("UTF-8");

        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String palabrasClave = request.getParameter("palabrasClave");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");

        LocalDate fechaAlta = LocalDate.now();

        HttpSession session = request.getSession(false);

        String creador = (String) session.getAttribute("usuario");

        String uploadPath = "/home/alumne/AD/uploads";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }


        Part filePart = request.getPart("fichero");
        String nombreFichero = now() + filePart.getSubmittedFileName();
        File destino = new File(uploadDir, nombreFichero);
        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            // create a database connection
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/DB_practica2;user=lab;password=lab");

            String insertSql = "INSERT INTO imagenes ("
                    + "titulo, "
                    + "descripcion, "
                    + "palabrasClave, "
                    + "autor, "
                    + "creador, "
                    + "fechaCreacion, "
                    + "fechaAlta, "
                    + "nombreFichero"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            insertStmt.setString(1, titulo);
            insertStmt.setString(2, descripcion);
            insertStmt.setString(3, palabrasClave);
            insertStmt.setString(4, autor);
            insertStmt.setString(5, creador);
            insertStmt.setString(6, fechaCreacion);
            insertStmt.setString(7, fechaAlta.toString());
            insertStmt.setString(8, nombreFichero);

            int insertedRows = insertStmt.executeUpdate();

            if (insertedRows > 0) {
                // Registro correcto → mostrar mensaje de éxito
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<div class='card'>");
                    out.println("<h2>Imagen registrada correctamente</h2>");
                    out.println("<a href='menu.jsp'>Volver al menú</a><br>");
                    out.println("<a href='registrarImagen.jsp'>Registrar otra imagen</a>");
                    out.println("</div>");
                }
            } else {
                response.sendRedirect("error?error=11");
            }
            insertStmt.close();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(registrarImagen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(registrarImagen.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

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
