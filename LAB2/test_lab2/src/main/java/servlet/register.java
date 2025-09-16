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

/**
 *
 * @author alumne
 */
@WebServlet(name = "register", urlPatterns = {"/register"})
public class register extends HttpServlet {

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
            String query;
            String usuario = request.getParameter("usuario");
            String password = request.getParameter("password");
            String rep_password = request.getParameter("rep_password");

            if (!password.equals(rep_password)) {
                request.setAttribute("error", "Las contraseñas no coinciden");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet rs = null;

            try {

                Class.forName("org.apache.derby.jdbc.ClientDriver");

                connection = DriverManager.getConnection(
                        "jdbc:derby://localhost:1527/test;user=test;password=test"
                );

                /*
            es mejor ya asumir que existen las tablas ? Es decir que a la hora
            de crearlas hacemos las querys para ya tener tablas creadas
            
            drop table users;
            create table users (
                    id_usuario varchar (256) primary key,
                    password varchar (256)
            );
            
            // si la tabla ya existe
            query = "drop table users";
            statement = connection.prepareStatement(query);
            statement.setQueryTimeout(30);  // set timeout to 30 sec.                      
            statement.executeUpdate();

            //si no existe        
            query = "create table users (id_usuario varchar (256) primary key, password varchar (256))";
            statement = connection.prepareStatement(query);                        
            statement.executeUpdate();    
                 */
                String queryUser = "SELECT 1 FROM users WHERE id_usuario = ?";
                statement = connection.prepareStatement(queryUser);
                statement.setString(1, usuario);
                rs = statement.executeQuery();

                if (rs.next()) {
                    request.setAttribute("error", "El usuario ya existe");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                    return;
                } else {
                    query = "insert into users values(?,?)";
                    statement = connection.prepareStatement(query);    
                    statement.setString(1, usuario);
                    statement.setString(2, password);
                    statement.executeUpdate();
                    
                    request.setAttribute("valid", "Usuario registrado");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                    return;
                }
                

            } catch (Exception e) {
                out.println("<p>Error: " + e.getMessage() + "</p>");
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException ex) {
                }
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                }
            }
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
