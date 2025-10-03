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
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author alumne
 */
@WebServlet(name = "eliminarImagen", urlPatterns = {"/eliminarImagen"})
public class eliminarImagen extends HttpServlet {

    private static final String UPLOAD_DIR = "/home/alumne/AD/uploads";

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
            out.println("<title>Servlet eliminarImagen</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet eliminarImagen at " + request.getContextPath() + "</h1>");
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
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/DB_practica2;user=lab;password=lab");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM imagenes WHERE idimagen = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                request.setAttribute("id", id);
                request.setAttribute("titulo", rs.getString("titulo"));
                request.setAttribute("autor", rs.getString("autor"));
                request.setAttribute("nombreFichero", rs.getString("nombreFichero"));
                request.setAttribute("nombreOriginal", rs.getString("nombreOriginal"));
                request.getRequestDispatcher("eliminarImagen.jsp").forward(request, response);
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

        String idStr = request.getParameter("id");
        String nombreFichero = request.getParameter("nombreFichero");

        if (idStr == null || nombreFichero == null) {
            response.sendRedirect("buscarImagen");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/DB_practica2;user=lab;password=lab");

            PreparedStatement ps = con.prepareStatement("DELETE FROM imagenes WHERE idimagen = ?");
            ps.setInt(1, id);
            int result = ps.executeUpdate();

            ps.close();
            con.close();

            File file = new File(UPLOAD_DIR, nombreFichero);
            if (file.exists()) {
                file.delete();
            }

            if (result > 0) {
                response.setContentType("text/html;charset=UTF-8");
                try (PrintWriter out = response.getWriter()) {
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<meta charset='UTF-8'>");
                    out.println("<title>Imagen eliminada</title>");
                    out.println("<link rel='stylesheet' href='css/styles.css'>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<div class='card' style='text-align:center;'>");
                    out.println("<h2 style='color:#38bdf8;'>Imagen eliminada correctamente</h2>");
                    out.println("<a class='btn' href='menu.jsp'>Ir al menú</a>");
                    out.println("</div>");
                    out.println("</body>");
                    out.println("</html>");
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
