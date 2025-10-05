<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="WEB-INF/jspf/checkSession.jspf" %>

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

            <img class="preview" src="mostrarImagen/<%= java.net.URLEncoder.encode((String)request.getAttribute("nombreFichero"), "UTF-8") %>" 
                 alt="<%= request.getAttribute("titulo") %>">

            <p><strong>Title:</strong> <%= request.getAttribute("titulo") %></p>
            <p><strong>Author:</strong> <%= request.getAttribute("autor") %></p>

            <form method="post" action="eliminarImagen">
                <input type="hidden" name="id" value="<%= request.getAttribute("id") %>">
                <input type="hidden" name="nombreFichero" value="<%= request.getAttribute("nombreFichero") %>">
                <button class="btn" type="submit" onclick="return confirm('Are you sure you want to delete this image?');">Delete</button>
                <a href="buscarImagen" class="btn" 
                   style="background:#6b7280; display:inline-flex; align-items:center; justify-content:center; text-align:center; line-height:1; height:48px;">
                    Cancel
                </a>
            </form>
        </div>
    </body>
</html>
