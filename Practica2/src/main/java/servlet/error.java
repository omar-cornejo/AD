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
        String errorParam = request.getParameter("error");
        int error = 0;
        if (errorParam != null) {
            try {
                error = Integer.parseInt(errorParam);
            } catch (NumberFormatException e) {
                error = 100;
            }
        } else {
            response.sendRedirect("message.jsp");
            return;
        }

        String mensaje;
        String backUrl;

        switch (error) {
            case 1:
                mensaje = "Incorrect password";
                backUrl = "login.jsp";
                break;
            case 2:
                mensaje = "Some field is null";
                backUrl = "login.jsp";
                break;
            case 3:
                mensaje = "Passwords don't match";
                backUrl = "login.jsp";
                break;
            case 4:
                mensaje = "Username already exists";
                backUrl = "login.jsp";
                break;
            case 5:
                mensaje = "Username not exists";
                backUrl = "login.jsp";
                break;
            case 11:
                mensaje = "Something went wrong during insertion";
                backUrl = "menu.jsp";
                break;
            case 12:
                mensaje = "Wrong date of creation";
                backUrl = "menu.jsp";
                break;
            case 13:
                mensaje = "Wrong id number";
                backUrl = "menu.jsp";
                break;
            default:
                mensaje = "Unknown error";
                backUrl = "menu.jsp";
                break;
        }
            
        
        request.setAttribute("title", "Error");
        request.setAttribute("message", mensaje);
        request.setAttribute("backUrl", backUrl);

        request.getRequestDispatcher("message.jsp").forward(request, response);
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
        doGet(request, response);
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
