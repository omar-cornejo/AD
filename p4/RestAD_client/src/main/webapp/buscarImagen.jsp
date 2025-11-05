<%-- 
    Document   : buscarImagen
    Created on : 2025年11月1日, 18:18:55
    Author     : ASUS GAMING
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="WEB-INF/jspf/checkSession.jspf" %>
<%@ page import="java.util.List, model.Imagen" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Find Images</title>
        <link rel="stylesheet" href="css/styles.css">
        <style>
            .hint{
                opacity:.75;
                font-size:.9rem;
                margin:.5rem 0;
            }
            .btn-action{
                margin-right: 5px;
                padding: 3px 6px;
                background-color: #007bff;
                color: white;
                border: none;
                border-radius: 3px;
                cursor: pointer;
                text-decoration: none;
            }
            .btn-action.delete{
                background-color: #dc3545;
            }
        </style>
    </head>
    <body>
        <div class="card">
            <div  style="display:flex;justify-content:space-between;align-items:center">
                <h1>Find Images</h1>
                <button class="btn" onclick="location.href = 'menu.jsp'">Exit</button>
            </div>

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
                <button class="btn" type="submit">Search</button>
            </form>

            <hr/>
            <h1 style="margin-top:8px">Results</h1>

            <table class="table">
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Author</th>
                    <th>Creation date</th>
                    <th>File</th>
                    <th>Actions</th>
                </tr>

                <%
                  List<Imagen> resultados = (List<Imagen>) request.getAttribute("resultados");
                  if (resultados != null && resultados.size() != 0) {
                      for (Imagen img : resultados) {     
                %>
                <tr>
                    <td><%= img.getId() %></td>
                    <td><%= img.getTitulo() %></td>
                    <td><%= img.getAutor() %></td>
                    <td><%= img.getFechaCreacion() != null ? img.getFechaCreacion() : "" %></td>
                    <td>
                        <a href="#">
                            null
                        </a>

                    </td>
                    <td>
                        <%  if (session.getAttribute("usuario").equals(img.getCreador()) ) {
                         
                        %>
                        <a class="btn-action" href="modificarImagen?id=<%= img.getId() %>">Modify</a>

                        <a class="btn-action delete" href="eliminarImagen?id=<%= img.getId() %>">Delete</a>

                        <%
                            }
                        %>
                    </td>
                </tr>
                <%
                      }
                  } else {
                %>
                <tr>
                    <td
                        <p>No results found</p>
                    </td>

                </tr>

                <%
                }
                %>
            </table>
        </div>

    </body>
</html>
