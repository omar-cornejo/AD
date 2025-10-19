<%-- 
    Document   : register
    Created on : 21 sept 2025, 21:13:22
    Author     : alumne
--%>


<%
if (session != null) session.invalidate();    
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register</title>
        <link rel='stylesheet' href='css/styles.css'>
    </head>
    <body>
        <div class='card'>
            <h1>Create account</h1>
            <form method="post" action="register">
              <div class="row">
                <input class="input" type="text" name="username" placeholder="Username" required>
              </div>
              <div class="row">
                  <input class="input" type="password" name="password" placeholder="Password" required><br>
                <input class="input" type="password" name="password2" placeholder="Repite password" required>
              </div>
              <button class="btn" type="submit">Register</button>
            </form>
            <p>Already have an account? <a href="login.jsp">Log in</a></p>
        </div>
        
    </body>
</html>
