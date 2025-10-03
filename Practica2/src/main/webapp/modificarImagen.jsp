<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="WEB-INF/jspf/checkSession.jspf"%>
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
    <title>Modify Image</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
<div class="card">
    <h1>Modify Image</h1>

    <form method="POST" action="modificarImagen">
        <input type="hidden" name="id" value="<%= img.getId() %>">

        <div class="row">
            <input class="input" name="titulo" placeholder="Title" value="<%= img.getTitulo() %>" required>
            <input class="input" name="autor" placeholder="Author" value="<%= img.getAutor() %>" required>
        </div>

        <div class="row">
            <input class="input" name="palabrasClave" placeholder="Key words" value="<%= img.getPalabrasClave() %>">
            <input class="input" type="date" name="fechaCreacion"
                   value="<%= img.getFechaCreacion() != null ? img.getFechaCreacion() : "" %>">
        </div>

        <textarea class="input" name="descripcion" rows="4" placeholder="Description"><%= img.getDescripcion() %></textarea>

        <button class="btn" type="submit">Save Changes</button>
        <a href="buscarImagen" class="btn" style="background:#6b7280;">Cancel</a>
    </form>
</div>
</body>
</html>
