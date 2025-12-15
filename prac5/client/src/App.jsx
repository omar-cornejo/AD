import {
  Navigate,
  Route,
  Routes,
  useLocation,
  useNavigate,
} from "react-router-dom";
import "./App.css";
import Login from "./components/Login";
import ReelsView from "./components/ReelsView";
import UploadVideo from "./components/UploadVideo";
import { AuthProvider, useAuth } from "./context/AuthContext";

function AppContent() {
  const { isAuthenticated, isLoading, login, logout, user } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  if (isLoading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Cargando...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Login onLoginSuccess={login} />;
  }

  return (
    <div className="app">
      {user && (
        <div className="header-bar">
          <div className="user-info">
            <span>👤 {user?.username}</span>
          </div>
          <div className="header-buttons">
            {location?.pathname !== "/upload" && (
              <button
                className="upload-btn"
                onClick={() => navigate("/upload")}
                aria-label="Subir video"
              >
                📤
              </button>
            )}
            <button
              className="logout-btn"
              onClick={logout}
              aria-label="Cerrar sesión"
            >
              Salir
            </button>
          </div>
        </div>
      )}
      <div className="app-content">
        <Routes>
          <Route path="/" element={<ReelsView />} />
          <Route path="/upload" element={<UploadVideo />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
