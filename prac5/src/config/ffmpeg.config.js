module.exports = {
  hls: {
    segmentDuration: 10,
    listSize: 0,
    startNumber: 0,
  },

  profiles: {
    low: {
      videoBitrate: "500k",
      audioBitrate: "64k",
      resolution: "640x360",
      preset: "faster",
    },
    medium: {
      videoBitrate: "1500k",
      audioBitrate: "128k",
      resolution: "1280x720",
      preset: "medium",
    },
    high: {
      videoBitrate: "3000k",
      audioBitrate: "192k",
      resolution: "1920x1080",
      preset: "slow",
    },
    source: {
      copy: true,
    },
  },

  defaultProfile: "medium",
};
