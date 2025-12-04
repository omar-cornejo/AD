const { spawn } = require('child_process');
const fs = require('fs').promises;
const path = require('path');
const ffmpegConfig = require('./src/config/ffmpeg.config');

async function checkFFmpeg() {
  return new Promise((resolve) => {
    const ffmpeg = spawn('ffmpeg', ['-version']);
    ffmpeg.on('close', (code) => resolve(code === 0));
  });
}

async function getVideoInfo(inputFile) {
  return new Promise((resolve, reject) => {
    const ffprobe = spawn('ffprobe', [
      '-v', 'quiet',
      '-print_format', 'json',
      '-show_format',
      '-show_streams',
      inputFile
    ]);
    
    let output = '';
    ffprobe.stdout.on('data', (data) => {
      output += data.toString();
    });
    
    ffprobe.on('close', (code) => {
      if (code === 0) {
        try {
          resolve(JSON.parse(output));
        } catch (e) {
          reject(e);
        }
      } else {
        reject(new Error('Error al obtener información del video'));
      }
    });
  });
}

async function convertToHLS(inputFile, channelName, profile = 'source') {
  try {
    const hasFFmpeg = await checkFFmpeg();
    if (!hasFFmpeg) {
      throw new Error('FFmpeg no está instalado');
    }
    
    try {
      await fs.access(inputFile);
    } catch {
      throw new Error(`El archivo ${inputFile} no existe`);
    }
    
    console.log('Analizando video...');
    const videoInfo = await getVideoInfo(inputFile);
    const videoStream = videoInfo.streams.find(s => s.codec_type === 'video');
    
    if (videoStream) {
      console.log(`Resolución: ${videoStream.width}x${videoStream.height}`);
      console.log(`Codec: ${videoStream.codec_name}`);
      console.log(`Duración: ${Math.floor(videoInfo.format.duration)}s`);
    }
    
    const outputDir = path.join(__dirname, 'streams', channelName);
    await fs.mkdir(outputDir, { recursive: true });
    
    const outputPlaylist = path.join(outputDir, 'playlist.m3u8');
    const segmentPattern = path.join(outputDir, 'playlist%d.ts');
    
    console.log(`\nConvirtiendo ${inputFile} a HLS...`);
    console.log(`Canal: ${channelName}`);
    console.log(`Perfil: ${profile}\n`);
    
    const profileConfig = ffmpegConfig.profiles[profile] || ffmpegConfig.profiles[ffmpegConfig.defaultProfile];
    const hlsConfig = ffmpegConfig.hls;
    
    const args = [
      '-i', inputFile,
      '-y'
    ];
    
    // Configurar codecs según el perfil
    if (profileConfig.copy) {
      args.push('-c', 'copy');
    } else {
      args.push(
        '-c:v', 'libx264',
        '-b:v', profileConfig.videoBitrate,
        '-c:a', 'aac',
        '-b:a', profileConfig.audioBitrate,
        '-preset', profileConfig.preset,
        '-s', profileConfig.resolution
      );
    }
    
    // Configuración HLS
    args.push(
      '-start_number', String(hlsConfig.startNumber),
      '-hls_time', String(hlsConfig.segmentDuration),
      '-hls_list_size', String(hlsConfig.listSize),
      '-hls_segment_filename', segmentPattern,
      '-f', 'hls',
      outputPlaylist
    );
    
    const ffmpeg = spawn('ffmpeg', args);
    
    let lastProgress = 0;
    ffmpeg.stderr.on('data', (data) => {
      const output = data.toString();
      const timeMatch = output.match(/time=(\d{2}):(\d{2}):(\d{2})/);
      
      if (timeMatch && videoInfo.format.duration) {
        const [, hours, minutes, seconds] = timeMatch;
        const currentTime = parseInt(hours) * 3600 + parseInt(minutes) * 60 + parseInt(seconds);
        const progress = Math.floor((currentTime / videoInfo.format.duration) * 100);
        
        if (progress > lastProgress && progress <= 100) {
          lastProgress = progress;
          process.stdout.write(`\rProgreso: ${progress}%`);
        }
      }
    });
    
    return new Promise((resolve, reject) => {
      ffmpeg.on('close', (code) => {
        process.stdout.write('\r');
        
        if (code === 0) {
          console.log(`Conversión completada: ${channelName}`);
          console.log(`Stream disponible en: /streams/${channelName}/playlist.m3u8\n`);
          resolve();
        } else {
          reject(new Error(`Error en la conversión. Código: ${code}`));
        }
      });
      
      ffmpeg.on('error', reject);
    });
    
  } catch (error) {
    console.error(error.message);
    process.exit(1);
  }
}

if (require.main === module) {
  const args = process.argv.slice(2);
  
  if (args.length < 2) {
    console.log('\nConversor de Video a HLS\n');
    console.log('Uso: node convert-to-hls.js <archivo_video> <nombre_canal> [perfil]\n');
    console.log('Perfiles disponibles:');
    console.log('  source  - Sin recodificar (rápido)');
    console.log('  low     - 360p, 500kbps');
    console.log('  medium  - 720p, 1500kbps');
    console.log('  high    - 1080p, 3000kbps\n');
    console.log('Ejemplo: node convert-to-hls.js video.mp4 mi_canal source\n');
    process.exit(1);
  }
  
  const [inputFile, channelName, profile] = args;
  convertToHLS(inputFile, channelName, profile);
}

module.exports = { convertToHLS, getVideoInfo, checkFFmpeg };
