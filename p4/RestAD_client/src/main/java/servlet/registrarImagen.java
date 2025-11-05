/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.InputStream;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author alumne
 */
@WebServlet(name = "registrarImagen", urlPatterns = {"/registrarImagen"})
public class registrarImagen extends HttpServlet {

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
        request.getRequestDispatcher("registrarImagen.jsp").forward(request, response);
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

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/menu.jsp");
            return;
        }
        
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String palabrasClave = request.getParameter("palabrasClave");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");

        String creador = (String) session.getAttribute("usuario");

        URL url = new URL("http://localhost:8080/RestAD_server/resources/api/register");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        String body = "title=" + safeEncode(titulo)
                + "&description=" + safeEncode(descripcion)
                + "&keywords=" + safeEncode(palabrasClave)
                + "&author=" + safeEncode(autor)
                + "&creator=" + safeEncode(creador)
                + "&capture=" + safeEncode(fechaCreacion);
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
        String title = "";
        String message = "";
        String backUrl = "";
        String registerUrl = "";
        int ok = 0;
        try (JsonReader jr = Json.createReader(new StringReader(jsonText))) {
            JsonObject obj = jr.readObject();
            if (obj.containsKey("title")) {
                title = obj.getString("title");
            }
            if (obj.containsKey("message")) {
                message = obj.getString("message");
            }
            if (obj.containsKey("backUrl")) {
                backUrl = obj.getString("backUrl");
            }
            if (obj.containsKey("registerUrl")) {
                registerUrl = obj.getString("registerUrl");
            }
            if (obj.containsKey("code")) {
                ok = obj.getInt("code");
            }
        } catch (Exception e) {
            ok = 100;
        }
        if (ok != 0) {
            response.sendRedirect("error?error=" + ok);
        }
        else {
            request.setAttribute("title", title);
            request.setAttribute("message", message);
            request.setAttribute("backUrl", backUrl);
            request.setAttribute("registerUrl", registerUrl);
            request.getRequestDispatcher("message.jsp").forward(request, response);
        }

    }
    
    private static String safeEncode(String value) {
    if (value == null) {
        return "";
    }
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
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
