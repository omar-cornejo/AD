<%-- 
    Document   : menu
    Created on : 17 sept 2025, 15:59:28
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="WEB-INF/jspf/checkSession.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="css/styles.css">
        <title>JSP Page</title>
    </head>
    <body>
        <div class="card">
            <div style="display:flex;justify-content:space-between;align-items:center">
                <h1>Menu</h1>
                <form method="post" action="logout"><button class="btn" type="submit">Salir</button></form>
            </div>
            <div class="nav">
              <a href="registrarImagen.jsp">Register Image</a>
              <a href="buscarImagen.jsp">Find Image</a>
            </div>
            <p>Hi, ${sessionScope.usuario}</p>
        </div>
    </body>
</html>
