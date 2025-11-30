const express = require('express');
const fs = require('fs').promises;
const path = require('path');
const router = express.Router();

router.get('/', async (req, res) => {
  try {
    const playlistPath = path.join(process.cwd(), 'playlist.m3u8');
    await fs.access(playlistPath);
    const content = await fs.readFile(playlistPath, 'utf-8');
    
    res.setHeader('Content-Type', 'application/vnd.apple.mpegurl');
    res.setHeader('Cache-Control', 'no-cache');
    res.send(content);
  } catch (error) {
    res.status(404).json({ error: 'Playlist no encontrada' });
  }
});

module.exports = router;
