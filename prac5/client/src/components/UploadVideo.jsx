import { useState } from "react";
import "./UploadVideo.css";

export default function UploadVideo() {
  const [file, setFile] = useState(null);
  const [channelName, setChannelName] = useState("");
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [message, setMessage] = useState(null);
  const [dragActive, setDragActive] = useState(false);

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const droppedFile = e.dataTransfer.files[0];
      if (droppedFile.type.startsWith("video/")) {
        setFile(droppedFile);
        if (!channelName) {
          const baseName = droppedFile.name
            .replace(/\.[^/.]+$/, "")
            .replace(/[^a-zA-Z0-9]/g, "_");
          setChannelName(baseName);
        }
      } else {
        setMessage({
          type: "error",
          text: "Solo se permiten archivos de video",
        });
      }
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      const selectedFile = e.target.files[0];
      setFile(selectedFile);
      if (!channelName) {
        const baseName = selectedFile.name
          .replace(/\.[^/.]+$/, "")
          .replace(/[^a-zA-Z0-9]/g, "_");
        setChannelName(baseName);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!file || !channelName) {
      setMessage({
        type: "error",
        text: "Por favor selecciona un video y proporciona un nombre de canal",
      });
      return;
    }

    if (!/^[a-zA-Z0-9_-]+$/.test(channelName)) {
      setMessage({
        type: "error",
        text: "El nombre del canal solo puede contener letras, números, guiones y guiones bajos",
      });
      return;
    }

    setUploading(true);
    setProgress(0);
    setMessage(null);

    const formData = new FormData();
    formData.append("video", file);
    formData.append("channelName", channelName);

    try {
      const xhr = new XMLHttpRequest();

      xhr.upload.addEventListener("progress", (e) => {
        if (e.lengthComputable) {
          const percentComplete = Math.round((e.loaded / e.total) * 100);
          setProgress(percentComplete);
        }
      });

      xhr.addEventListener("load", () => {
        if (xhr.status === 200) {
          const response = JSON.parse(xhr.responseText);
          setMessage({
            type: "success",
            text: `✅ Video subido exitosamente! Canal: ${response.channel}`,
          });
          setFile(null);
          setChannelName("");
          setProgress(0);

          setTimeout(() => {
            window.location.href = "/";
          }, 2000);
        } else {
          const error = JSON.parse(xhr.responseText);
          setMessage({
            type: "error",
            text: `❌ Error: ${error.error || "Error desconocido"}`,
          });
        }
        setUploading(false);
      });

      xhr.addEventListener("error", () => {
        setMessage({
          type: "error",
          text: "❌ Error de red al subir el video",
        });
        setUploading(false);
      });

      xhr.open("POST", "/api/upload");
      const token = localStorage.getItem("apiToken");
      if (token) xhr.setRequestHeader("Authorization", `Bearer ${token}`);
      xhr.send(formData);
    } catch (error) {
      console.error("Error:", error);
      setMessage({ type: "error", text: `❌ Error: ${error.message}` });
      setUploading(false);
    }
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
  };

  return (
    <div className="upload-container">
      <div className="upload-card">
        <h1>Subir Video</h1>
        <p className="upload-subtitle">
          Sube un video y se convertirá automáticamente a HLS para streaming
        </p>

        <form onSubmit={handleSubmit}>
          <div
            className={`drop-zone ${dragActive ? "drag-active" : ""} ${
              file ? "has-file" : ""
            }`}
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
          >
            {file ? (
              <div className="file-preview">
                <div className="file-icon">🎬</div>
                <div className="file-info">
                  <div className="file-name">{file.name}</div>
                  <div className="file-size">{formatFileSize(file.size)}</div>
                </div>
                <button
                  type="button"
                  className="remove-file"
                  onClick={() => setFile(null)}
                  disabled={uploading}
                >
                  ✕
                </button>
              </div>
            ) : (
              <>
                <div className="drop-icon">📹</div>
                <p className="drop-text">Arrastra y suelta tu video aquí</p>
                <p className="drop-subtext">o</p>
                <label className="file-input-label">
                  <input
                    type="file"
                    accept="video/*"
                    onChange={handleFileChange}
                    disabled={uploading}
                    style={{ display: "none" }}
                  />
                  Seleccionar archivo
                </label>
                <p className="drop-hint">
                  MP4, MKV, AVI, MOV, WEBM (máx. 500MB)
                </p>
              </>
            )}
          </div>

          <div className="form-group">
            <label htmlFor="channelName">Nombre del Canal</label>
            <input
              type="text"
              id="channelName"
              value={channelName}
              onChange={(e) =>
                setChannelName(e.target.value.replace(/[^a-zA-Z0-9_-]/g, ""))
              }
              placeholder="mi_canal"
              required
              disabled={uploading}
              pattern="[a-zA-Z0-9_-]+"
              title="Solo letras, números, guiones y guiones bajos"
            />
            <small>Solo letras, números, guiones (-) y guiones bajos (_)</small>
          </div>

          {uploading && (
            <div className="progress-container">
              <div className="progress-bar">
                <div
                  className="progress-fill"
                  style={{ width: `${progress}%` }}
                />
              </div>
              <div className="progress-text">
                {progress < 100
                  ? `Subiendo... ${progress}%`
                  : "Procesando video..."}
              </div>
            </div>
          )}

          {message && (
            <div className={`message ${message.type}`}>{message.text}</div>
          )}

          <div className="button-group">
            <button
              type="submit"
              className="submit-button"
              disabled={!file || !channelName || uploading}
            >
              {uploading ? "Subiendo..." : "Subir Video"}
            </button>
            <a href="/" className="cancel-button">
              ← Volver
            </a>
          </div>
        </form>
      </div>
    </div>
  );
}
