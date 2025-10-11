<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="WEB-INF/jspf/checkSession.jspf" %>
<%@ page import="model.Imagen" %>
<%
    Imagen img = (Imagen) request.getAttribute("imagen");
    if (img == null) {
        response.sendRedirect("buscarImagen");
        return;
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Eliminar Imagen</title>
        <link rel="stylesheet" href="css/styles.css">
        <style>
            .preview {
                max-width: 300px;
                max-height: 300px;
                display: block;
                margin-bottom: 15px;
            }
            .btn {
                padding: 8px 15px;
                background-color: #dc3545;
                color: white;
                border: none;
                border-radius: 3px;
                cursor: pointer;
                text-decoration: none;
            }
        </style>
    </head>
    <body>
        <div class="card">
            <h1>Delete Image</h1>
            <p>Are you sure you want to delete this image?</p>

            <img class="preview" src="mostrarImagen/<%= java.net.URLEncoder.encode(img.getNombreFichero(), "UTF-8") %>" 
                 alt="<%= img.getTitulo() %>">

            <p><strong>Title:</strong> <%= img.getTitulo() %></p>
            <p><strong>Author:</strong> <%= img.getAutor() %></p>

            <form method="post" action="eliminarImagen">
                <input type="hidden" name="id" value="<%= img.getId() %>">
                <input type="hidden" name="nombreFichero" value="<%= img.getNombreFichero() %>">
                <button class="btn" type="submit" onclick="return confirm('Are you sure you want to delete this image?');">Delete</button>
                <a href="buscarImagen" class="btn" 
                   style="background:#6b7280; display:inline-flex; align-items:center; justify-content:center; text-align:center; line-height:1; height:48px;">
                    Cancel
                </a>
            </form>
        </div>
    </body>
</html>
