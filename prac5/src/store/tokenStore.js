const tokens = new Map();

function setToken(subject, token) {
  if (!subject) return;
  tokens.set(subject, token);
}

function getToken(subject) {
  return tokens.get(subject);
}

function deleteToken(subject) {
  return tokens.delete(subject);
}

function findSubjectByToken(token) {
  for (const [sub, t] of tokens.entries()) {
    if (t === token) return sub;
  }
  return null;
}

module.exports = { setToken, getToken, deleteToken, findSubjectByToken };
