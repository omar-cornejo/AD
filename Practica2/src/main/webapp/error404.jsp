<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Error 404 - Página no encontrada</title>
<style>
:root{
  --bg:#0f172a;
  --panel:#111827;
  --text:#e5e7eb;
  --acc:#38bdf8;
}
*{box-sizing:border-box}
body{
  margin:0;
  font-family:system-ui,Segoe UI,Roboto,Ubuntu,'Helvetica Neue',Arial;
  background:linear-gradient(180deg,var(--bg),#020617);
  color:var(--text);
  min-height:100vh;
  display:flex;
  align-items:center;
  justify-content:center;
}
.card{
  width:min(600px,92vw);
  background:var(--panel);
  border-radius:16px;
  padding:32px;
  text-align:center;
  box-shadow:0 20px 80px rgba(0,0,0,.5);
}
h1{
  margin:0 0 8px 0;
  font-size:52px;
  color:var(--acc);
  letter-spacing:2px;
}
h2{
  margin:0 0 16px 0;
  font-size:22px;
}
p{
  font-size:16px;
  color:#cbd5e1;
  margin-bottom:24px;
}
.btn{
  padding:12px 20px;
  border:0;
  border-radius:12px;
  background:var(--acc);
  color:#001018;
  font-weight:700;
  cursor:pointer;
  text-decoration:none;
  display:inline-block;
  transition:all .2s ease-in-out;
}
.btn:hover{
  transform:translateY(-2px);
  box-shadow:0 4px 10px rgba(56,189,248,.4);
}
</style>
</head>
<body>
  <div class="card">
    <h1>404</h1>
    <h2>Page not found</h2>
    <a href="${pageContext.request.contextPath}/menu.jsp" class="btn">Return</a>
  </div>
</body>
</html>
