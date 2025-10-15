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
import jakarta.servlet.http.HttpSession;
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
@WebServlet(name = "modificarImagen", urlPatterns = {"/modificarImagen"})
public class modificarImagen extends HttpServlet {


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
                    request.getRequestDispatcher("modificarImagen.jsp").forward(request, response);
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
        
        
        request.setCharacterEncoding("UTF-8");
        String idStr = request.getParameter("id");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String palabrasClave = request.getParameter("palabrasClave");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");
        String creador = request.getParameter("creador");
        
        HttpSession session = request.getSession();
        if(!session.getAttribute("usuario").equals(creador)) {
            response.sendRedirect("buscarImagen");
            return;
        }
        
        if (idStr == null) {
            response.sendRedirect("buscarImagen");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            ImagenDAO dao = new ImagenDAO();
            try {
                Imagen img = dao.findById(id);
                if (img == null) {
                    response.sendRedirect("buscarImagen");
                    return;
                }

                img.setTitulo(titulo);
                img.setAutor(autor);
                img.setPalabrasClave(palabrasClave);
                img.setDescripcion(descripcion);
                if (fechaCreacion != null && !fechaCreacion.isEmpty()) {
                    img.setFechaCreacion(java.sql.Date.valueOf(fechaCreacion));
                } else {
                    img.setFechaCreacion(null);
                }

                dao.update(img);
            } finally {
                dao.close();
            }

            request.setAttribute("title", "Imagen modificada");
            request.setAttribute("message", "Image data updated successfully!");
            request.setAttribute("backUrl", "menu.jsp");
            request.getRequestDispatcher("message.jsp").forward(request, response);

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
