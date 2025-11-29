import { useEffect, useRef, useState, useCallback } from 'react';
import videojs from 'video.js';
import { useTimeFormat } from '../hooks';
import 'video.js/dist/video-js.css';
import './VideoPlayer.css';

const VideoPlayer = ({ url, isActive, channelName }) => {
  const videoRef = useRef(null);
  const playerRef = useRef(null);
  const hideControlsTimeout = useRef(null);
  
  const [isMuted, setIsMuted] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [showControls, setShowControls] = useState(false);
  
  const { formatTime } = useTimeFormat();

  // Inicializar player
  useEffect(() => {
    if (!videoRef.current) return;

    // Esperar a que el elemento esté en el DOM
    const initPlayer = () => {
      const player = videojs(videoRef.current, {
        controls: false,
        autoplay: true,
        preload: 'auto',
        fluid: false,
        responsive: false,
        fill: true,
        muted: false,
        loop: true,
        playsinline: true,
        html5: {
          vhs: {
            overrideNative: true,
            enableLowInitialPlaylist: true
          },
          nativeVideoTracks: false,
          nativeAudioTracks: false,
          nativeTextTracks: false
        }
      });

      playerRef.current = player;

      player.src({
        src: url,
        type: 'application/x-mpegURL'
      });

      // Event listeners
      player.on('play', () => setIsPlaying(true));
      player.on('pause', () => setIsPlaying(false));
      player.on('timeupdate', () => setCurrentTime(player.currentTime()));
      player.on('loadedmetadata', () => setDuration(player.duration()));
      player.on('durationchange', () => setDuration(player.duration()));
      player.on('error', (e) => {
        console.error('Video player error:', e, player.error());
      });
    };

    // Usar timeout para asegurar que el elemento esté en el DOM
    const timer = setTimeout(initPlayer, 0);

    return () => {
      clearTimeout(timer);
      if (playerRef.current && !playerRef.current.isDisposed()) {
        playerRef.current.dispose();
        playerRef.current = null;
      }
    };
  }, [url]);

  // Control de reproducción según visibilidad
  useEffect(() => {
    const player = playerRef.current;
    if (!player) return;

    if (isActive) {
      player.play().catch(err => console.log('Autoplay prevented:', err));
    } else {
      player.pause();
    }
  }, [isActive]);

  const toggleMute = useCallback(() => {
    const player = playerRef.current;
    if (player) {
      const newMutedState = !player.muted();
      player.muted(newMutedState);
      setIsMuted(newMutedState);
    }
  }, []);

  const togglePlay = useCallback(() => {
    const player = playerRef.current;
    if (player) {
      if (player.paused()) {
        player.play();
      } else {
        player.pause();
      }
    }
  }, []);

  const handleSeek = useCallback((e) => {
    const player = playerRef.current;
    if (!player || !duration) return;

    const rect = e.currentTarget.getBoundingClientRect();
    const pos = (e.clientX - rect.left) / rect.width;
    const time = pos * duration;
    
    player.currentTime(time);
    setCurrentTime(time);
  }, [duration]);

  const handleTouchMove = useCallback(() => {
    setShowControls(true);
    clearTimeout(hideControlsTimeout.current);
    hideControlsTimeout.current = setTimeout(() => {
      setShowControls(false);
    }, 3000);
  }, []);

  return (
    <div 
      className="video-player-container" 
      onMouseMove={handleTouchMove} 
      onTouchMove={handleTouchMove}
    >
      <video
        ref={videoRef}
        className="video-js vjs-big-play-centered"
        playsInline
        webkit-playsinline="true"
        style={{ 
          width: '100%', 
          height: '100%',
          objectFit: 'contain',
          position: 'absolute',
          top: 0,
          left: 0
        }}
      />

      <div className="custom-controls">
        <button 
          className="control-btn play-pause" 
          onClick={togglePlay}
          aria-label={isPlaying ? 'Pausar' : 'Reproducir'}
        >
          {isPlaying ? '⏸️' : '▶️'}
        </button>

        <button 
          className="control-btn mute-btn" 
          onClick={toggleMute}
          aria-label={isMuted ? 'Activar sonido' : 'Silenciar'}
        >
          {isMuted ? '🔇' : '🔊'}
        </button>
      </div>

      <div className={`progress-container ${showControls || !isPlaying ? 'visible' : ''}`}>
        <div className="time-display">
          <span className="current-time">{formatTime(currentTime)}</span>
          <span className="duration">{formatTime(duration)}</span>
        </div>
        <div className="progress-bar" onClick={handleSeek}>
          <div 
            className="progress-filled"
            style={{ width: `${duration > 0 ? (currentTime / duration) * 100 : 0}%` }}
          >
            <div className="progress-handle" />
          </div>
        </div>
      </div>

      <div className="tap-overlay" onClick={togglePlay} />
    </div>
  );
};

export default VideoPlayer;
