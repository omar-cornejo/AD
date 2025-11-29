module.exports = {
  // Configuración de conversión HLS
  hls: {
    segmentDuration: 10, // segundos por segmento
    listSize: 0, // 0 = todos los segmentos
    startNumber: 0
  },
  
  // Perfiles de calidad de video
  profiles: {
    low: {
      videoBitrate: '500k',
      audioBitrate: '64k',
      resolution: '640x360',
      preset: 'faster'
    },
    medium: {
      videoBitrate: '1500k',
      audioBitrate: '128k',
      resolution: '1280x720',
      preset: 'medium'
    },
    high: {
      videoBitrate: '3000k',
      audioBitrate: '192k',
      resolution: '1920x1080',
      preset: 'slow'
    },
    source: {
      // Copia los codecs originales sin recodificar
      copy: true
    }
  },
  
  // Codec por defecto
  defaultProfile: 'medium'
};
