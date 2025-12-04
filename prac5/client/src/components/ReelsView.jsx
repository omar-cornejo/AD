import { useState, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { useChannels, useSwipe } from '../hooks';
import VideoPlayer from './VideoPlayer';
import Chat from './Chat';
import './ReelsView.css';

const ReelsView = () => {
  const navigate = useNavigate();
  const { channels, isLoading, addChannel } = useChannels();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [showUrlInput, setShowUrlInput] = useState(false);
  const [customUrl, setCustomUrl] = useState('');
  const [isChatOpen, setIsChatOpen] = useState(false);
  const containerRef = useRef(null);

  const scrollToVideo = useCallback((index) => {
    if (containerRef.current?.children[index]) {
      containerRef.current.children[index].scrollIntoView({ 
        behavior: 'smooth', 
        block: 'start' 
      });
    }
  }, []);

  const handleSwipeUp = useCallback(() => {
    if (currentIndex < channels.length - 1) {
      const newIndex = currentIndex + 1;
      setCurrentIndex(newIndex);
      scrollToVideo(newIndex);
    }
  }, [currentIndex, channels.length, scrollToVideo]);

  const handleSwipeDown = useCallback(() => {
    if (currentIndex > 0) {
      const newIndex = currentIndex - 1;
      setCurrentIndex(newIndex);
      scrollToVideo(newIndex);
    }
  }, [currentIndex, scrollToVideo]);

  const { handleTouchStart, handleTouchEnd, handleWheel } = useSwipe(
    handleSwipeUp,
    handleSwipeDown
  );

  const handleAddCustomUrl = useCallback(() => {
    const trimmedUrl = customUrl.trim();
    if (trimmedUrl && (trimmedUrl.includes('.m3u8') || trimmedUrl.includes('m3u'))) {
      addChannel({
        id: `external_${Date.now()}`,
        name: 'Stream Externo',
        url: trimmedUrl
      });
      setCurrentIndex(0);
      scrollToVideo(0);
      setCustomUrl('');
      setShowUrlInput(false);
    } else {
      alert('Por favor ingresa una URL válida de HLS (.m3u8)');
    }
  }, [customUrl, addChannel, scrollToVideo]);

  const handleIndicatorClick = useCallback((index) => {
    setCurrentIndex(index);
    scrollToVideo(index);
  }, [scrollToVideo]);

  if (isLoading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Cargando canales...</p>
      </div>
    );
  }

  if (channels.length === 0) {
    return (
      <div className="empty-container">
        <div className="empty-icon">📺</div>
        <h2>No hay canales disponibles</h2>
        <p>Agrega videos usando: node convert-to-hls.js</p>
        <button 
          onClick={() => window.location.reload()} 
          style={{
            marginTop: '20px',
            padding: '10px 20px',
            background: '#007bff',
            border: 'none',
            borderRadius: '5px',
            color: 'white',
            cursor: 'pointer'
          }}
        >
          Recargar
        </button>
      </div>
    );
  }

  return (
    <>
      <Chat isOpen={isChatOpen} onToggle={() => setIsChatOpen(!isChatOpen)} />
      
      <button 
        className="upload-btn"
        onClick={() => navigate('/upload')}
        aria-label="Subir video"
      >
        📤
      </button>

      <button 
        className="add-url-btn"
        onClick={() => setShowUrlInput(!showUrlInput)}
        aria-label="Agregar stream externo"
      >
        {showUrlInput ? '✕' : '+'}
      </button>

      {showUrlInput && (
        <div className="url-modal">
          <div className="url-modal-content">
            <h3>Agregar Stream Externo</h3>
            <input
              type="text"
              value={customUrl}
              onChange={(e) => setCustomUrl(e.target.value)}
              placeholder="https://ejemplo.com/stream.m3u8"
              className="url-input-field"
              onKeyPress={(e) => e.key === 'Enter' && handleAddCustomUrl()}
            />
            <div className="url-modal-actions">
              <button onClick={handleAddCustomUrl} className="btn-add">
                Agregar
              </button>
              <button onClick={() => setShowUrlInput(false)} className="btn-cancel">
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}

      <div 
        className="reels-container"
        ref={containerRef}
        onTouchStart={handleTouchStart}
        onTouchEnd={handleTouchEnd}
        onWheel={handleWheel}
      >
        {channels.map((channel, index) => (
          <div key={channel.id || channel.name} className="reel-item">
            <VideoPlayer
              url={channel.url}
              isActive={index === currentIndex}
              channelName={channel.name}
            />
            
            <div className="reel-overlay">
              <div className="reel-info">
                <h2 className="channel-title">{channel.name}</h2>
                <p className="channel-subtitle">Canal {index + 1} de {channels.length}</p>
              </div>
            </div>

            <div className="scroll-indicators">
              {channels.map((_, idx) => (
                <div
                  key={idx}
                  className={`indicator ${idx === currentIndex ? 'active' : ''}`}
                  onClick={() => handleIndicatorClick(idx)}
                />
              ))}
            </div>
          </div>
        ))}
      </div>
    </>
  );
};

export default ReelsView;
