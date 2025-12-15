const express = require("express");
const jwt = require("jsonwebtoken");
const jwtConfig = require("../config/jwt.config");
const tokenStore = require("../store/tokenStore");
const { verifyCredentials, getUser } = require("../store/userStore");
const { addUser } = require("../store/userStore");

const router = express.Router();

router.post("/login", (req, res) => {
  const { username, password } = req.body || {};
  if (!username || !password)
    return res.status(400).json({ error: "Missing username or password" });

  if (!verifyCredentials(username, password)) {
    return res.status(401).json({ error: "Invalid credentials" });
  }

  const payload = { sub: username, role: "user" };
  try {
    const token = jwt.sign(payload, jwtConfig.secret, {
      expiresIn: jwtConfig.expiresIn,
    });
    tokenStore.setToken(username, token);

    const user = getUser(username);

    res.json({
      token,
      expiresIn: jwtConfig.expiresIn,
      user: { username: user.username },
    });
  } catch (err) {
    console.error("Login error", err);
    res.status(500).json({ error: "Could not create token" });
  }
});

router.post("/register", (req, res) => {
  const { username, password } = req.body || {};
  if (!username || !password)
    return res.status(400).json({ error: "Missing username or password" });

  const ok = addUser(username, password);
  if (!ok)
    return res.status(409).json({ error: "User already exists or invalid" });

  const payload = { sub: username, role: "user" };
  try {
    const token = jwt.sign(payload, jwtConfig.secret, {
      expiresIn: jwtConfig.expiresIn,
    });
    tokenStore.setToken(username, token);
    res
      .status(201)
      .json({ token, expiresIn: jwtConfig.expiresIn, user: { username } });
  } catch (err) {
    console.error("Register error", err);
    res.status(500).json({ error: "Could not create token" });
  }
});

module.exports = router;
