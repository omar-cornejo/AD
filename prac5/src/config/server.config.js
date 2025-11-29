module.exports = {
  port: process.env.PORT || 8080,
  env: process.env.NODE_ENV || 'development',
  cors: {
    origin: process.env.CORS_ORIGIN || '*',
    credentials: true
  },
  paths: {
    streams: 'streams',
    videos: 'videos',
    client: 'client/dist'
  }
};
