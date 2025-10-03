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

/**
 *
 * @author alumne
 */
@WebServlet(name = "modificarImagen", urlPatterns = {"/modificarImagen"})
public class modificarImagen extends HttpServlet {

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
            out.println("<title>Servlet modificarImagen</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet modificarImagen at " + request.getContextPath() + "</h1>");
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

        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("buscarImagen");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/DB_practica2;user=lab;password=lab");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM imagenes WHERE idimagen = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                model.Imagen img = new model.Imagen();
                img.setId(rs.getInt("idimagen"));
                img.setTitulo(rs.getString("titulo"));
                img.setAutor(rs.getString("autor"));
                img.setPalabrasClave(rs.getString("palabrasClave"));
                img.setDescripcion(rs.getString("descripcion"));
                img.setFechaCreacion(rs.getDate("fechaCreacion"));
                img.setNombreFichero(rs.getString("nombreFichero"));
                img.setNombreOriginal(rs.getString("nombreOriginal"));

                request.setAttribute("imagen", img);
                request.getRequestDispatcher("modificarImagen.jsp").forward(request, response);
            } else {
                response.sendRedirect("buscarImagen");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?error=11");
        }
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
        String idStr = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String palabrasClave = request.getParameter("palabrasClave");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");

        if (idStr == null) {
            response.sendRedirect("buscarImagen");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/DB_practica2;user=lab;password=lab");

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE imagenes SET titulo=?, autor=?, palabrasClave=?, descripcion=?, fechaCreacion=? WHERE idimagen=?"
            );
            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.setString(3, palabrasClave);
            ps.setString(4, descripcion);
            ps.setString(5, fechaCreacion);
            ps.setInt(6, id);

            int result = ps.executeUpdate();

            ps.close();
            con.close();

            if (result > 0) {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<!DOCTYPE html>");
                    out.println("<html><head><meta charset='UTF-8'><title>Image modified</title>");
                    out.println("<link rel='stylesheet' href='css/styles.css'></head>");
                    out.println("<body>");
                    out.println("<div class='card' style='text-align:center;'>");
                    out.println("<h2 style='color:#38bdf8;'>Image data updated successfully!</h2>");
                    out.println("<a class='btn' href='menu.jsp'>Back to menu</a>");
                    out.println("</div></body></html>");
                }
            } else {
                response.sendRedirect("error.jsp?error=11");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?error=11");
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
