const express = require('express');
const fs = require('fs').promises;
const path = require('path');
const router = express.Router();

/**
 * GET /api/channels
 * Lista todos los canales disponibles
 */
router.get('/', async (req, res) => {
  try {
    const streamsDir = path.join(process.cwd(), 'streams');
    
    // Verificar si existe el directorio
    try {
      await fs.access(streamsDir);
    } catch {
      return res.json([]);
    }
    
    // Leer directorios
    const files = await fs.readdir(streamsDir);
    
    // Filtrar solo directorios con playlist.m3u8
    const channels = [];
    for (const file of files) {
      const filePath = path.join(streamsDir, file);
      const stats = await fs.stat(filePath);
      
      if (stats.isDirectory()) {
        const playlistPath = path.join(filePath, 'playlist.m3u8');
        try {
          await fs.access(playlistPath);
          channels.push({
            id: file,
            name: file.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase()),
            url: `/streams/${file}/playlist.m3u8`
          });
        } catch {
          // El directorio no tiene playlist, omitir
        }
      }
    }
    
    res.json(channels);
  } catch (error) {
    console.error('Error al cargar canales:', error);
    res.status(500).json({ error: 'Error al cargar canales' });
  }
});

module.exports = router;
