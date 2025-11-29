const express = require('express');
const cors = require('cors');
const path = require('path');
const { createServer } = require('http');
const { Server } = require('socket.io');
const config = require('./src/config/server.config');
const channelsRouter = require('./src/routes/channels');
const playlistRouter = require('./src/routes/playlist');

const app = express();
const httpServer = createServer(app);
const io = new Server(httpServer, {
  cors: {
    origin: config.env === 'production' 
      ? (process.env.CLIENT_URL || true) 
      : 'http://localhost:3000',
    methods: ['GET', 'POST'],
    credentials: true
  }
});

// Middleware
app.use(cors(config.cors));
app.use(express.json());

// Logging middleware
app.use((req, res, next) => {
  console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);
  next();
});

// Servir archivos estáticos del cliente (solo en producción)
if (config.env === 'production') {
  const clientPath = path.join(__dirname, config.paths.client);
  app.use(express.static(clientPath));
}

// Servir archivos HLS desde la carpeta streams
app.use('/streams', express.static(config.paths.streams, {
  setHeaders: (res, filePath) => {
    if (filePath.endsWith('.m3u8')) {
      res.setHeader('Content-Type', 'application/vnd.apple.mpegurl');
      res.setHeader('Cache-Control', 'no-cache');
    } else if (filePath.endsWith('.ts')) {
      res.setHeader('Content-Type', 'video/mp2t');
    }
  }
}));

// Rutas de API
app.use('/api/channels', channelsRouter);
app.use('/playlist.m3u8', playlistRouter);

// Health check
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// Fallback para SPA (React Router) - solo en producción
app.get('*', (req, res) => {
  if (config.env === 'production') {
    const clientPath = path.join(__dirname, config.paths.client);
    res.sendFile(path.join(clientPath, 'index.html'));
  } else {
    res.json({ 
      message: 'API Server - Modo desarrollo',
      frontend: 'http://localhost:3000',
      api: `http://localhost:${config.port}/api/channels`
    });
  }
});

// Manejo de errores
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(500).json({ 
    error: config.env === 'production' ? 'Error del servidor' : err.message 
  });
});

// Socket.IO - Chat en tiempo real
const users = new Map();
const messageHistory = [];

io.on('connection', (socket) => {
  console.log(`👤 Usuario conectado: ${socket.id}`);
  
  socket.on('join', (username) => {
    users.set(socket.id, { username, id: socket.id });
    socket.emit('message_history', messageHistory);
    io.emit('user_count', users.size);
    io.emit('chat_message', {
      type: 'system',
      message: `${username} se ha unido al chat`,
      timestamp: Date.now()
    });
  });
  
  socket.on('chat_message', (data) => {
    const user = users.get(socket.id);
    if (user) {
      const message = {
        type: 'user',
        username: user.username,
        message: data.message,
        timestamp: Date.now()
      };
      messageHistory.push(message);
      if (messageHistory.length > 100) messageHistory.shift();
      io.emit('chat_message', message);
    }
  });
  
  socket.on('disconnect', () => {
    const user = users.get(socket.id);
    if (user) {
      users.delete(socket.id);
      io.emit('user_count', users.size);
      io.emit('chat_message', {
        type: 'system',
        message: `${user.username} ha salido del chat`,
        timestamp: Date.now()
      });
    }
    console.log(`👤 Usuario desconectado: ${socket.id}`);
  });
});

// Iniciar servidor
const server = httpServer.listen(config.port, () => {
  console.log(`\n${'='.repeat(50)}`);
  console.log(`🚀 Servidor IPTV HLS iniciado`);
  console.log(`${'='.repeat(50)}`);
  console.log(`📡 Entorno: ${config.env}`);
  console.log(`🌐 URL: http://localhost:${config.port}`);
  console.log(`📺 Canales: http://localhost:${config.port}/api/channels`);
  console.log(`${'='.repeat(50)}\n`);
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('\n🛑 Deteniendo servidor...');
  server.close(() => {
    console.log('✅ Servidor detenido correctamente');
    process.exit(0);
  });
});
