<%-- 
    Document   : register
    Created on : 16 sept 2025, 20:52:20
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Register</h1>
        
        <form action="register" method="POST">
        <label for="usuario">Usuario:</label><br>
        <input type="text" id="usuario" name="usuario" required><br><br>
        
        <label for="password">Password:</label><br>
        <input type="password" id="password" name="password" required><br><br>
        
        <label for="rep_password">Repite_Password:</label><br>
        <input type="password" id="rep_password" name="rep_password" required><br><br>
        
        <input type="submit" value="Register user">
        </form>
        <br>
        <nav>
            <a href="login.jsp" style="color: blue; text-decoration: underline;">Login</a>
        </nav>
        
    <p style="color: ${not empty error ? 'red' : 'green'};">
        ${not empty error ? error : valid}
    </p>
    
    

    </body>
</html>
