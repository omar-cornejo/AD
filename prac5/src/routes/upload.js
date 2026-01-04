const express = require("express");
const multer = require("multer");
const path = require("path");
const fs = require("fs").promises;
const { exec } = require("child_process");
const { promisify } = require("util");
const execPromise = promisify(exec);

const router = express.Router();

let dropbox = null;
if (process.env.DROPBOX_ACCESS_TOKEN) {
  const { Dropbox } = require("dropbox");
  dropbox = new Dropbox({ accessToken: process.env.DROPBOX_ACCESS_TOKEN });
}

const storage = multer.diskStorage({
  destination: async (req, file, cb) => {
    const uploadDir = path.join(__dirname, "../../uploads");
    try {
      await fs.mkdir(uploadDir, { recursive: true });
      cb(null, uploadDir);
    } catch (error) {
      cb(error);
    }
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1e9);
    cb(null, `video-${uniqueSuffix}${path.extname(file.originalname)}`);
  },
});

const upload = multer({
  storage,
  limits: {
    fileSize: 500 * 1024 * 1024, // 500MB máximo
  },
  fileFilter: (req, file, cb) => {
    const allowedTypes = /mp4|mkv|avi|mov|webm/;
    const extname = allowedTypes.test(
      path.extname(file.originalname).toLowerCase()
    );
    const mimetype = allowedTypes.test(file.mimetype);

    if (extname && mimetype) {
      cb(null, true);
    } else {
      cb(
        new Error(
          "Solo se permiten archivos de video (mp4, mkv, avi, mov, webm)"
        )
      );
    }
  },
});

async function convertToHLS(videoPath, channelName) {
  const convertScript = path.join(__dirname, "../../convert-to-hls.js");
  const command = `node "${convertScript}" "${videoPath}" "${channelName}" source`;

  console.log(`Convirtiendo video a HLS: ${channelName}`);
  const { stdout, stderr } = await execPromise(command);

  if (stderr && !stderr.includes("frame=")) {
    console.error("Advertencia FFmpeg:", stderr);
  }

  console.log(`Video convertido exitosamente: ${channelName}`);
  return true;
}

async function uploadToDropbox(filePath, fileName) {
  if (!dropbox) {
    throw new Error("Dropbox no configurado. Falta DROPBOX_ACCESS_TOKEN");
  }

  console.log(`Subiendo a Dropbox: ${fileName}`);

  const fileContent = await fs.readFile(filePath);
  const dropboxPath = `/videos/${fileName}`;

  const uploadResult = await dropbox.filesUpload({
    path: dropboxPath,
    contents: fileContent,
    mode: "overwrite",
    autorename: false,
  });

  console.log(`Archivo subido a Dropbox: ${dropboxPath}`);

  try {
    const sharedLink = await dropbox.sharingCreateSharedLinkWithSettings({
      path: dropboxPath,
      settings: {
        requested_visibility: "public",
      },
    });

    const url = sharedLink.result?.url || sharedLink.url;
    if (!url) {
      console.warn("No se pudo obtener URL del link compartido");
      return null;
    }

    const directUrl = url.replace("dl=0", "dl=1");
    console.log(`Link público creado: ${directUrl}`);
    return directUrl;
  } catch (error) {
    if (error.error?.error?.[".tag"] === "shared_link_already_exists") {
      console.log("Link público ya existe, obteniendo...");
      try {
        const links = await dropbox.sharingListSharedLinks({
          path: dropboxPath,
        });
        const url = links.result?.links?.[0]?.url || links.links?.[0]?.url;
        if (url) {
          const directUrl = url.replace("dl=0", "dl=1");
          console.log(`Link público obtenido: ${directUrl}`);
          return directUrl;
        }
      } catch (listError) {
        console.error("Error al obtener links existentes:", listError);
      }
    } else {
      console.error("Error al crear link compartido:", error);
    }
    console.warn("Video subido pero sin link público disponible");
    return null;
  }
}

router.post("/", upload.single("video"), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: "No se recibió ningún archivo" });
    }

    const { channelName } = req.body;
    if (!channelName) {
      return res
        .status(400)
        .json({ error: "Falta el nombre del canal (channelName)" });
    }

    if (!/^[a-zA-Z0-9_-]+$/.test(channelName)) {
      return res.status(400).json({
        error:
          "Nombre de canal inválido. Solo se permiten letras, números, guiones y guiones bajos",
      });
    }

    const tempFilePath = req.file.path;
    const originalName = req.file.originalname;
    const fileSize = req.file.size;

    console.log(
      `Video recibido: ${originalName} (${(fileSize / 1024 / 1024).toFixed(
        2
      )} MB)`
    );

    await convertToHLS(tempFilePath, channelName);

    if (process.env.NODE_ENV === "production" && dropbox) {
      const finalFileName = `${channelName}${path.extname(originalName)}`;
      const dropboxUrl = await uploadToDropbox(tempFilePath, finalFileName);

      await fs.unlink(tempFilePath);

      return res.json({
        success: true,
        message: "Video subido y convertido exitosamente",
        channel: channelName,
        dropboxUrl: dropboxUrl || "Video subido sin link público",
        size: fileSize,
      });
    } else {
      const videosDir = path.join(__dirname, "../../videos");
      const finalPath = path.join(
        videosDir,
        `${channelName}${path.extname(originalName)}`
      );

      await fs.mkdir(videosDir, { recursive: true });
      await fs.rename(tempFilePath, finalPath);

      return res.json({
        success: true,
        message: "Video subido y convertido exitosamente",
        channel: channelName,
        localPath: `/videos/${channelName}${path.extname(originalName)}`,
        size: fileSize,
      });
    }
  } catch (error) {
  console.error("Error al procesar video:", error);

    if (req.file && req.file.path) {
      try {
        await fs.unlink(req.file.path);
      } catch (unlinkError) {
        console.error("Error al eliminar archivo temporal:", unlinkError);
      }
    }

    res.status(500).json({
      error: "Error al procesar el video",
      details: error.message,
    });
  }
});

router.get("/status", (req, res) => {
  res.json({
    uploadEnabled: true,
    maxFileSize: "500MB",
    allowedFormats: ["mp4", "mkv", "avi", "mov", "webm"],
    environment: process.env.NODE_ENV || "development",
    dropboxEnabled: !!dropbox,
  });
});

module.exports = router;
