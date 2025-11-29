import { useEffect, useRef, useState } from 'react';

/**
 * Hook personalizado para detectar gestos táctiles y scroll
 */
export const useSwipe = (onSwipeUp, onSwipeDown, threshold = 50) => {
  const touchStartY = useRef(0);
  const isScrolling = useRef(false);

  const handleTouchStart = (e) => {
    touchStartY.current = e.touches[0].clientY;
  };

  const handleTouchEnd = (e) => {
    if (isScrolling.current) return;
    
    const touchEndY = e.changedTouches[0].clientY;
    const diff = touchStartY.current - touchEndY;

    if (Math.abs(diff) > threshold) {
      isScrolling.current = true;
      
      if (diff > 0) {
        onSwipeUp?.();
      } else {
        onSwipeDown?.();
      }

      setTimeout(() => {
        isScrolling.current = false;
      }, 500);
    }
  };

  const handleWheel = (e) => {
    e.preventDefault();
    if (isScrolling.current) return;

    isScrolling.current = true;

    if (e.deltaY > 0) {
      onSwipeUp?.();
    } else {
      onSwipeDown?.();
    }

    setTimeout(() => {
      isScrolling.current = false;
    }, 500);
  };

  return {
    handleTouchStart,
    handleTouchEnd,
    handleWheel
  };
};

/**
 * Hook para cargar canales desde la API
 */
export const useChannels = () => {
  const [channels, setChannels] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchChannels = async () => {
    try {
      setIsLoading(true);
      const response = await fetch('/api/channels');
      
      if (!response.ok) {
        throw new Error('Error al cargar canales');
      }
      
      const data = await response.json();
      setChannels(data);
      setError(null);
    } catch (err) {
      console.error('Error cargando canales:', err);
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const addChannel = (channel) => {
    setChannels(prev => [channel, ...prev]);
  };

  useEffect(() => {
    fetchChannels();
  }, []);

  return { channels, isLoading, error, addChannel, refetch: fetchChannels };
};

/**
 * Hook para formatear tiempo
 */
export const useTimeFormat = () => {
  const formatTime = (seconds) => {
    if (!seconds || isNaN(seconds) || !isFinite(seconds)) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return { formatTime };
};
