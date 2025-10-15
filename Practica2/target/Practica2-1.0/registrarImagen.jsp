<%-- 
    Document   : registrarImagen
    Created on : 19 sept 2025, 12:24:12
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="WEB-INF/jspf/checkSession.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/styles.css">
        <title>JSP Page</title>
    </head>
    <body>
        <div class="card">
            
            <div  style="display:flex;justify-content:space-between;align-items:center">
                <h1>Register image</h1>
                <button class="btn" onclick="location.href = 'menu.jsp'">Salir</button>
            </div>
            <form method="POST" action="registrarImagen" enctype="multipart/form-data">
                <div class="row">
                    <input class="input" name="titulo" placeholder="Title" required>
                    <input class="input" name="autor" placeholder="Author" required>
                </div>
                <div class="row">
                    <input class="input" name="palabrasClave" placeholder="Key words(separed with comma)">
                    <input class="input" name="fechaCreacion" type="date" placeholder="Creation date">
                </div>
                <textarea class="input" name="descripcion" rows="4" placeholder="Description"></textarea>
                <input class="input" type="file" name="fichero" accept="image/*" required>
                <button class="btn">Save</button>
            </form>
        </div>
    </body>
</html>
