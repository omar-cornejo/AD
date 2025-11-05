/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.Imagen;

/**
 *
 * @author ASUS GAMING
 */
@WebServlet(name = "buscarImagen", urlPatterns = {"/buscarImagen"})
public class buscarImagen extends HttpServlet {

    private static final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd")  
        .create();

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
        
        String titulo = normalize(request.getParameter("titulo"));
        String autor = normalize(request.getParameter("autor"));
        String palabrasClave = normalize(request.getParameter("palabrasClave"));
        String fechaCreacion = normalize(request.getParameter("fechaCreacion"));

        if (fechaCreacion != null && !fechaCreacion.isEmpty() && !isValidDate(fechaCreacion)) {
            response.sendRedirect("error?error=12");
            return;
        }
        
        List<Imagen> resultados = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        
        
        
        try {
            List<Imagen> rTitulo = (titulo != null) ? callRest("searchTitle/" + safeEncode(titulo)) : null;
            List<Imagen> rAutor = (autor != null) ? callRest("searchAuthor/" + safeEncode(autor)) : null;
            List<Imagen> rKeys = (palabrasClave != null) ? callRest("searchKeywords/" + safeEncode(palabrasClave)) : null;
            List<Imagen> rFecha = (fechaCreacion != null) ? callRest("searchCreationDate/" + safeEncode(fechaCreacion)) : null;

            List<List<Imagen>> listas = java.util.stream.Stream.of(rTitulo, rAutor, rKeys, rFecha)
                    .filter(java.util.Objects::nonNull).toList();

            if (listas.isEmpty()) {
                resultados = callRest("allImages"); 
            } else {
                java.util.Map<Integer, Imagen> idx = new java.util.LinkedHashMap<>();
                listas.stream().flatMap(java.util.Collection::stream)
                        .filter(java.util.Objects::nonNull)
                        .forEach(i -> {
                            idx.putIfAbsent(i.getId(), i);
                        });

                java.util.Set<Integer> inter = new java.util.HashSet<>(
                        listas.get(0).stream().map(Imagen::getId).toList()
                );
                for (int k = 1; k < listas.size(); k++) {
                    inter.retainAll(listas.get(k).stream().map(Imagen::getId).toList());
                }

                resultados = inter.stream().map(idx::get).toList();
            }

            

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error?error=100");
            return;
        }
        

        request.setAttribute("resultados", resultados);
        request.getRequestDispatcher("buscarImagen.jsp").forward(request, response);
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
    
    
 
    private boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false; 
        }
    }
    
    private static String normalize(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    
    private List<Imagen> callRest(String endpoint) throws IOException {
        URL url = new URL("http://localhost:8080/RestAD_server/resources/api/" + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            return Collections.emptyList();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String json = reader.lines().collect(Collectors.joining());
            Imagen[] arr = gson.fromJson(json, Imagen[].class); 
            return Arrays.asList(arr);
        } finally {
            conn.disconnect();
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
