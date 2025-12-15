import { useState } from "react";
import "../styles/Login.css";

export default function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLogin, setIsLogin] = useState(true);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !password) return;

    setError("");
    setLoading(true);

    try {
      const endpoint = isLogin ? "/api/auth/login" : "/api/auth/register";
      const response = await fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: username.trim(), password }),
      });

      if (!response.ok) {
        const err = await response.json().catch(() => ({}));
        throw new Error(
          err.error || `${isLogin ? "Login" : "Register"} failed`
        );
      }

      const data = await response.json();
      onLoginSuccess(data.token, data.user);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page-simple">
      <div className="login-container">
        <div className="login-card">
          <h1>🎬 IPTV HLS</h1>
          <h2>{isLogin ? "Iniciar Sesión" : "Registrarse"}</h2>

          {error && <div className="error-message">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Usuario"
                maxLength={20}
                autoFocus
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Contraseña"
                disabled={loading}
              />
            </div>

            <button
              type="submit"
              disabled={loading || !username.trim() || !password}
            >
              {loading ? "..." : isLogin ? "Entrar" : "Crear Cuenta"}
            </button>
          </form>

          <div className="login-toggle">
            <p>
              {isLogin ? "¿No tienes cuenta?" : "¿Ya tienes cuenta?"}
              <button
                type="button"
                onClick={() => {
                  setIsLogin(!isLogin);
                  setError("");
                  setUsername("");
                  setPassword("");
                }}
                className="toggle-btn"
              >
                {isLogin ? "Regístrate" : "Inicia sesión"}
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
