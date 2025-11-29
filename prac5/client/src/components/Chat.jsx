import { useState, useEffect, useRef } from 'react';
import { io } from 'socket.io-client';
import './Chat.css';

const Chat = ({ isOpen, onToggle }) => {
  const [socket, setSocket] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [username, setUsername] = useState('');
  const [isJoined, setIsJoined] = useState(false);
  const [userCount, setUserCount] = useState(0);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    const socketUrl = import.meta.env.PROD 
      ? window.location.origin 
      : 'http://localhost:8080';
    
    const newSocket = io(socketUrl);
    setSocket(newSocket);

    newSocket.on('chat_message', (message) => {
      setMessages((prev) => [...prev, message]);
    });

    newSocket.on('message_history', (history) => {
      setMessages(history);
    });

    newSocket.on('user_count', (count) => {
      setUserCount(count);
    });

    return () => newSocket.close();
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleJoin = (e) => {
    e.preventDefault();
    if (username.trim() && socket) {
      socket.emit('join', username.trim());
      setIsJoined(true);
    }
  };

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (inputMessage.trim() && socket) {
      socket.emit('chat_message', { message: inputMessage.trim() });
      setInputMessage('');
    }
  };

  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <>
      <button className="chat-toggle-btn" onClick={onToggle}>
        💬
        {userCount > 0 && <span className="user-count-badge">{userCount}</span>}
      </button>

      <div className={`chat-container ${isOpen ? 'open' : ''}`}>
        <div className="chat-header">
          <div>
            <h3>Chat en vivo</h3>
            <span className="online-users">👥 {userCount} en línea</span>
          </div>
          <button className="close-btn" onClick={onToggle}>✕</button>
        </div>

        {!isJoined ? (
          <form className="join-form" onSubmit={handleJoin}>
            <h4>Unirse al chat</h4>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Tu nombre..."
              maxLength={20}
              autoFocus
            />
            <button type="submit">Unirse</button>
          </form>
        ) : (
          <>
            <div className="chat-messages">
              {messages.map((msg, index) => (
                <div key={index} className={`message ${msg.type}`}>
                  {msg.type === 'system' ? (
                    <span className="system-message">{msg.message}</span>
                  ) : (
                    <>
                      <div className="message-header">
                        <span className="username">{msg.username}</span>
                        <span className="timestamp">{formatTime(msg.timestamp)}</span>
                      </div>
                      <div className="message-text">{msg.message}</div>
                    </>
                  )}
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>

            <form className="chat-input-form" onSubmit={handleSendMessage}>
              <input
                type="text"
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                placeholder="Escribe un mensaje..."
                maxLength={200}
              />
              <button type="submit">Enviar</button>
            </form>
          </>
        )}
      </div>

      {isOpen && <div className="chat-overlay" onClick={onToggle} />}
    </>
  );
};

export default Chat;
