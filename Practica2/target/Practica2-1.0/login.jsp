<%-- 
    Document   : login
    Created on : 17 sept 2025, 15:32:04
    Author     : alumne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login Page</title>
        <link rel="stylesheet" href="css/styles.css"
    </head>
    <body>
        <div class="card">
            <h1>Login</h1>
            <form action="login" method="POST"> 
                <label for="usuario">Name:</label><br>
                <input type="text" id="usuario" name="usuario" required><br><br>
                <label for="password">Password:</label><br>
                <input type="password" id="password" name="password" required><br><br>
                <button class='btn' type='submit'>Log in</button>
            </form>
            <p>Doesn't have an account? <a href='register.jsp'> Create an account</a></p>
        </div>
    </body>
</html>
