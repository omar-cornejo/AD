<%-- 
    Document   : message
    Created on : 11 oct 2025, 13:10:57
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<%
    if (request.getAttribute("title") == null ||
        request.getAttribute("message") == null ||
        request.getAttribute("backUrl") == null) {

        
        response.sendRedirect("menu.jsp"); 
        return; 
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
<div class="card" style="text-align:center;">
    <h2>${message}</h2>
    
    <c:choose>
        <c:when test="${backUrl == 'login.jsp'}">
            <a class="btn" href="${backUrl}">Volver a login</a>
        </c:when>
        <c:otherwise>
            <a class="btn" href="${backUrl}">Ir al menú</a>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
