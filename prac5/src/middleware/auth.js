const jwt = require("jsonwebtoken");
const jwtConfig = require("../config/jwt.config");
const tokenStore = require("../store/tokenStore");

function verifyToken(req, res, next) {
  const auth = req.headers.authorization || req.headers.Authorization;
  if (!auth)
    return res.status(401).json({ error: "Missing Authorization header" });

  const parts = auth.split(" ");
  const token =
    parts.length === 2 && parts[0].toLowerCase() === "bearer"
      ? parts[1]
      : parts[0];

  if (!token) return res.status(401).json({ error: "Token not provided" });

  jwt.verify(token, jwtConfig.secret, (err, decoded) => {
    if (err) return res.status(401).json({ error: "Invalid or expired token" });
    const stored = tokenStore.getToken(decoded.sub);
    if (!stored || stored !== token) {
      return res
        .status(401)
        .json({ error: "Token not recognized (maybe logged out)" });
    }
    req.user = decoded;
    next();
  });
}

module.exports = { verifyToken };
