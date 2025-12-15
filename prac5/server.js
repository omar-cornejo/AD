const express = require("express");
const cors = require("cors");
const path = require("path");
const { createServer } = require("http");
const { Server } = require("socket.io");
const config = require("./src/config/server.config");
const channelsRouter = require("./src/routes/channels");
const playlistRouter = require("./src/routes/playlist");
const uploadRouter = require("./src/routes/upload");
const { verifyToken } = require("./src/middleware/auth");
const jwt = require("jsonwebtoken");
const jwtConfig = require("./src/config/jwt.config");

const app = express();
const httpServer = createServer(app);
const io = new Server(httpServer, {
  cors: {
    origin:
      config.env === "production"
        ? process.env.CLIENT_URL || true
        : "http://localhost:3000",
    methods: ["GET", "POST"],
    credentials: true,
  },
});

app.use(cors(config.cors));
app.use(express.json());

app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);
  next();
});

if (config.env === "production") {
  const clientPath = path.join(__dirname, config.paths.client);
  app.use(express.static(clientPath));
}

app.use(
  "/streams",
  express.static(config.paths.streams, {
    setHeaders: (res, filePath) => {
      if (filePath.endsWith(".m3u8")) {
        res.setHeader("Content-Type", "application/vnd.apple.mpegurl");
        res.setHeader("Cache-Control", "no-cache");
      } else if (filePath.endsWith(".ts")) {
        res.setHeader("Content-Type", "video/mp2t");
      }
    },
  })
);

app.use("/api/channels", channelsRouter);
app.use("/playlist.m3u8", playlistRouter);

// Auth (login) routes
const authRouter = require("./src/routes/auth");
app.use("/api/auth", authRouter);

// Protect upload endpoints
app.use("/api/upload", verifyToken, uploadRouter);

app.get("/api/health", verifyToken, (req, res) => {
  res.json({ status: "ok", timestamp: new Date().toISOString() });
});

app.get("*", (req, res) => {
  if (config.env === "production") {
    const clientPath = path.join(__dirname, config.paths.client);
    res.sendFile(path.join(clientPath, "index.html"));
  } else {
    res.json({
      message: "API Server - Modo desarrollo",
      frontend: "http://localhost:3000",
      api: `http://localhost:${config.port}/api/channels`,
    });
  }
});

app.use((err, req, res, next) => {
  console.error("Error:", err);
  res.status(500).json({
    error: config.env === "production" ? "Error del servidor" : err.message,
  });
});

// Socket.IO
const users = new Map();
const messageHistory = [];

io.on("connection", (socket) => {
  console.log(`Usuario conectado: ${socket.id}`);

  socket.on("join", (username) => {
    users.set(socket.id, { username, id: socket.id });
    socket.emit("message_history", messageHistory);
    io.emit("user_count", users.size);
    io.emit("chat_message", {
      type: "system",
      message: `${username} se ha unido al chat`,
      timestamp: Date.now(),
    });
  });

  socket.on("chat_message", (data) => {
    const user = users.get(socket.id);
    if (user) {
      const message = {
        type: "user",
        username: user.username,
        message: data.message,
        timestamp: Date.now(),
      };
      messageHistory.push(message);
      if (messageHistory.length > 100) messageHistory.shift();
      io.emit("chat_message", message);
    }
  });

  socket.on("disconnect", () => {
    const user = users.get(socket.id);
    if (user) {
      users.delete(socket.id);
      io.emit("user_count", users.size);
      io.emit("chat_message", {
        type: "system",
        message: `${user.username} ha salido del chat`,
        timestamp: Date.now(),
      });
    }
    console.log(`Usuario desconectado: ${socket.id}`);
  });
});

const HOST = process.env.HOST || "0.0.0.0";

const server = httpServer.listen(config.port, HOST, () => {
  console.log(`\n${"=".repeat(50)}`);
  console.log(`Servidor IPTV HLS iniciado`);
  console.log(`${"=".repeat(50)}`);
  console.log(`Entorno: ${config.env}`);
  console.log(`Puerto: ${config.port}`);
  console.log(`\nAcceso Local:`);
  console.log(`  http://localhost:${config.port}`);
  console.log(`\nAcceso LAN (otros dispositivos en tu red):`);
  console.log(`  http://TU_IP_LOCAL:${config.port}`);
  console.log(`  Ejemplo: http://192.168.1.100:${config.port}`);
  console.log(`\nEndpoints:`);
  console.log(`  Canales: /api/channels`);
  console.log(`  Health: /api/health`);
  console.log(`${"=".repeat(50)}\n`);
});

process.on("SIGTERM", () => {
  console.log("\nDeteniendo servidor...");
  server.close(() => {
    console.log("Servidor detenido correctamente");
    process.exit(0);
  });
});
