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
import java.sql.SQLException;

/**
 *
 * @author alumne
 */
@WebServlet(name = "error", urlPatterns = {"/error"})
public class error extends HttpServlet {

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
            out.println("<title>Servlet error</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet error at " + request.getContextPath() + "</h1>");
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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String errorParam = request.getParameter("error");
            int error = 0;
            if (errorParam != null) {
                try {
                    error = Integer.parseInt(errorParam);
                } catch (NumberFormatException e) {
                    error = 0;
                }
            }

            switch (error) {
                case 1:
                    out.println("<p>Incorrect password</p><br><br>");
                    break;
                case 11:
                    out.println("<p>Something went wrong during insertion</p><br><br>");
                    break;
                case 12:
                     out.println("<p>La imagen ya está en el sistema</p><br><br>");   
                     break;
                default:
                    out.println("<p>Unknown error</p><br><br>");
                    break;
            }

            // Enlace según si el error es mayor o menor que 10
            String enlace = (error > 10) ? "menu.jsp" : "login.jsp";
            String textoEnlace = (error > 10) ? "Volver al menú" : "Volver a login";
            out.println("<a href='" + enlace + "' style='color: blue; text-decoration:underline;'>" + textoEnlace + "</a>");

        } catch (Exception e) {
            System.err.println(e.getMessage());
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
