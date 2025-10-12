/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import java.time.LocalDate;
import java.io.File;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Imagen;
import model.ImagenDAO;

/**
 *
 * @author alumne
 */
@WebServlet(name = "registrarImagen", urlPatterns = {"/registrarImagen"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
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

        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");
        String palabrasClave = request.getParameter("palabrasClave");
        String descripcion = request.getParameter("descripcion");
        String fechaCreacion = request.getParameter("fechaCreacion");

        LocalDate fechaAlta = LocalDate.now();

        HttpSession session = request.getSession(false);

        String creador = (String) session.getAttribute("usuario");

        String uploadPath = "/home/alumne/AD/uploads";

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Part filePart = request.getPart("fichero");
        String nombreOriginal = filePart.getSubmittedFileName();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        String timestamp = now.format(formatter);

        String nombreFichero = timestamp + "_" + nombreOriginal;;

        File destino = new File(uploadDir, nombreFichero);
        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            Imagen img = new Imagen();
            img.setTitulo(titulo);
            img.setDescripcion(descripcion);
            img.setPalabrasClave(palabrasClave);
            img.setAutor(autor);
            img.setCreador(creador);
            img.setFechaCreacion(java.sql.Date.valueOf(fechaCreacion));
            img.setFechaAlta(java.sql.Date.valueOf(fechaAlta));
            img.setNombreFichero(nombreFichero);
            img.setNombreOriginal(nombreOriginal);

            ImagenDAO dao = new ImagenDAO();
            try {
                dao.create(img);
            } finally {
                dao.close();
            }

            request.setAttribute("title", "Imagen registrada");
            request.setAttribute("message", "Imagen registrada correctamente");
            request.setAttribute("backUrl", "menu.jsp");
            request.setAttribute("registerUrl", "registrarImagen.jsp");
            request.getRequestDispatcher("message.jsp").forward(request, response);

        } catch (Exception ex) {
            Logger.getLogger(registrarImagen.class.getName()).log(Level.SEVERE, null, ex);
            response.sendRedirect("error?error=11");
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
