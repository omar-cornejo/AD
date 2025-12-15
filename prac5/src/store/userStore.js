const users = new Map();

users.set("alice", { username: "alice", password: "alicepass" });
users.set("bob", { username: "bob", password: "bobpass" });

function verifyCredentials(username, password) {
  if (!username || !password) return false;
  const u = users.get(username);
  if (!u) return false;
  return u.password === password;
}

function getUser(username) {
  return users.get(username) || null;
}

function addUser(username, password) {
  if (!username || !password) return false;
  if (users.has(username)) return false;
  users.set(username, { username, password });
  return true;
}

module.exports = { verifyCredentials, getUser, addUser };
