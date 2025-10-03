<%-- 
    Document   : buscarImagen
    Created on : 25 sept 2025, 14:47:58
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="WEB-INF/jspf/checkSession.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Find Images</title>
        <link rel="stylesheet" href="css/styles.css">
        <style>
            .hint{opacity:.75; font-size:.9rem;margin:.5rem 0}
        </style>
    </head>
    <body>
        <div class="card">
            <h1>Find Images</h1>

            <form method="get" action="buscarImagen">
              <div class="row">
                <input class="input" name="titulo" placeholder="Title"
                       value="<%= request.getParameter("titulo") != null ? request.getParameter("titulo") : "" %>">
                <input class="input" name="autor" placeholder="Author"
                       value="<%= request.getParameter("autor") != null ? request.getParameter("autor") : "" %>">
              </div>
              <div class="row">
                <input class="input" name="palabrasClave" placeholder="Key words(separated with commas)"
                       value="<%= request.getParameter("palabrasClave") != null ? request.getParameter("palabrasClave") : "" %>">
                <input class="input" type="date" name="fechaCreacion"
                       value="<%= request.getParameter("fechaCreacion") != null ? request.getParameter("fechaCreacion") : "" %>">
              </div>
              <button class="btn" type="submit">Buscar</button>
            </form>

            <hr/>
            <h1 style="margin-top:8px">Resultados</h1>

            <table class="table">
              <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Author</th>
                <th>Creation date</th>
                <th>File</th>
                <th>Actionss</th>
              </tr>

              <tr>
                <td>12</td>
                <td>Sunset over Bay</td>
                <td>Laura M.</td>
                <td>2024-06-18</td>
                <td><span class="badge">c1a2b3_sunset.jpg</span></td>
                <td><span style="opacity:.6">—</span></td>
              </tr>

              
            </table>

          </div>
    </body>
</html>
