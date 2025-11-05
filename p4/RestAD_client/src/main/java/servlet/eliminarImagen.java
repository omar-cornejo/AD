/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author ASUS GAMING
 */
@WebServlet(name = "eliminarImagen", urlPatterns = {"/eliminarImagen"})
public class eliminarImagen extends HttpServlet {

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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/menu.jsp");
            return;
        }
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect("buscarImagen");
            return;
        }

        if (!isValidInt(idStr)) {
            response.sendRedirect("error?error=13");
            return;
        }
        int id = Integer.parseInt(idStr);
        URL url = new URL("http://localhost:8080/RestAD_server/resources/api/searchID/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }

        String jsonText = sb.toString();
        try (jakarta.json.JsonReader jr = jakarta.json.Json.createReader(new java.io.StringReader(jsonText))) {
            jakarta.json.JsonObject obj = jr.readObject();

            if (obj.containsKey("code")) {
                int err = obj.getInt("code");
                response.sendRedirect("error?error=" + err);
                return;
            }

            String creador = obj.getString("creator", null);
            if (creador == null || !session.getAttribute("usuario").equals(creador)) {
                response.sendRedirect("buscarImagen");
                return;
            }

            model.Imagen img = new model.Imagen();
            img.setId(obj.getInt("id"));
            img.setTitulo(obj.getString("title", ""));
            img.setDescripcion(obj.getString("description", ""));
            img.setPalabrasClave(obj.getString("keywords", ""));
            img.setAutor(obj.getString("author", ""));
            img.setCreador(creador);

            String capture = obj.isNull("capture") ? null : obj.getString("capture", null);
            if (capture != null && !capture.isBlank()) {
                try {
                    img.setFechaCreacion(java.sql.Date.valueOf(capture));
                } catch (Exception ignored) {
                }
            }
            request.setAttribute("imagen", img);
            request.getRequestDispatcher("eliminarImagen.jsp").forward(request, response);

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
        String nombreFichero = request.getParameter("nombreFichero");
        String creador = request.getParameter("creador");
        
        HttpSession session = request.getSession();
        if (!session.getAttribute("usuario").equals(creador)) {
            response.sendRedirect("buscarImagen");
            return;
        }
        
        if (idStr == null || nombreFichero == null) {
            response.sendRedirect("buscarImagen");
            return;
        }
        
        URL url = new URL("http://localhost:8080/RestAD_server/resources/api/delete");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        String body = "creator=" + URLEncoder.encode(creador, StandardCharsets.UTF_8)
                + "&id=" + URLEncoder.encode(idStr, StandardCharsets.UTF_8);
        
        try (OutputStream os = con.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int code = con.getResponseCode();
        InputStream is = (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        String jsonText = sb.toString();
        int result = 0;
        try (JsonReader jr = Json.createReader(new StringReader(jsonText))) {
            JsonObject obj = jr.readObject();
            if (obj.containsKey("result")) {
                result = obj.getInt("result");
            }
        } catch (Exception e) {
            result = 100;
        }
        if (result > 0) {
            response.sendRedirect("error?error=" + result);
        }
        else if (result == 0) {
            request.setAttribute("title", "Imagen eliminada");
                request.setAttribute("message", "Imagen eliminada correctamente");
                request.setAttribute("backUrl", "menu.jsp");
                request.getRequestDispatcher("message.jsp").forward(request, response);
        }
        else {
            response.sendRedirect("buscarImagen");
        }
    }
    
    private boolean isValidInt(String numberStr) {
        try {
            Integer.parseInt(numberStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
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
