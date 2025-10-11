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
import model.Imagen;
import model.ImagenDAO;

/**
 *
 * @author alumne
 */
@WebServlet(name = "eliminarImagen", urlPatterns = {"/eliminarImagen"})
public class eliminarImagen extends HttpServlet {

    private static final String UPLOAD_DIR = "/home/alumne/AD/uploads";

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
            ImagenDAO dao = new ImagenDAO();
            try {
                Imagen img = dao.findById(id);
                if (img != null) {
                    request.setAttribute("imagen", img);
                    request.getRequestDispatcher("eliminarImagen.jsp").forward(request, response);
                } else {
                    response.sendRedirect("buscarImagen");
                }
            } finally {
                dao.close();
            }

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
            ImagenDAO dao = new ImagenDAO();
            int result = 0;
            try {
                dao.delete(id);
                result = 1;
            } finally {
                dao.close();
            }

            File file = new File(UPLOAD_DIR, nombreFichero);
            if (file.exists()) {
                file.delete();
            }

            if (result > 0) {
                request.setAttribute("title", "Imagen eliminada");
                request.setAttribute("message", "Imagen eliminada correctamente");
                request.setAttribute("backUrl", "menu.jsp");
                request.getRequestDispatcher("message.jsp").forward(request, response);
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
