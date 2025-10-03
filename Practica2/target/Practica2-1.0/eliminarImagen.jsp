<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="WEB-INF/jspf/checkSession.jspf" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Eliminar Imagen</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .preview { max-width: 300px; max-height: 300px; display: block; margin-bottom: 15px; }
        .btn { padding: 8px 15px; background-color: #dc3545; color: white; border: none; border-radius: 3px; cursor: pointer; text-decoration: none; }
    </style>
</head>
<body>
    <div class="card">
        <h1>Eliminar Imagen</h1>
        <p>¿Estás seguro que deseas eliminar esta imagen?</p>

        <img class="preview" src="mostrarImagen/<%= java.net.URLEncoder.encode((String)request.getAttribute("nombreFichero"), "UTF-8") %>" 
             alt="<%= request.getAttribute("titulo") %>">

        <p><strong>Título:</strong> <%= request.getAttribute("titulo") %></p>
        <p><strong>Autor:</strong> <%= request.getAttribute("autor") %></p>

        <form method="post" action="eliminarImagen">
            <input type="hidden" name="id" value="<%= request.getAttribute("id") %>">
            <input type="hidden" name="nombreFichero" value="<%= request.getAttribute("nombreFichero") %>">
            <button class="btn" type="submit" onclick="return confirm('¿Confirmas que quieres eliminar esta imagen?');">Eliminar</button>
            <a href="buscarImagen" style="margin-left:10px;">Cancelar</a>
        </form>
    </div>
</body>
</html>
