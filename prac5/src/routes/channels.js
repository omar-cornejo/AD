const express = require('express');
const fs = require('fs').promises;
const path = require('path');
const router = express.Router();

router.get('/', async (req, res) => {
  try {
    const streamsDir = path.join(process.cwd(), 'streams');
    
    try {
      await fs.access(streamsDir);
    } catch {
      return res.json([]);
    }
    
    const files = await fs.readdir(streamsDir);
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
          continue;
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
