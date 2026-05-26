import * as api from './api.js';

function showError(msg) {
  const el = document.getElementById('error-display');
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => { el.style.display = 'none'; }, 4000);
}

function validateNickname(nick) {
  if (!nick || nick.trim().length === 0) {
    throw new Error('Nickname cannot be empty');
  }
  if (!/^[a-z]+$/.test(nick)) {
    throw new Error('Nickname must be lowercase letters only');
  }
  if (nick.length > 30) {
    throw new Error('Nickname too long');
  }
  return nick.trim();
}

async function handleCreate(e) {
  e.preventDefault();
  try {
    const nick = validateNickname(document.getElementById('create-nick').value);
    const result = await api.createGame(nick);
    localStorage.setItem('zedmetsaxeli', nick);
    localStorage.setItem('otaxisId', result.otaxisId);
    window.location.href = 'game.html';
  } catch (err) {
    showError(err.message);
  }
}

async function handleJoin(e) {
  e.preventDefault();
  try {
    const nick = validateNickname(document.getElementById('join-nick').value);
    const roomId = document.getElementById('room-id').value.trim();
    if (!roomId) throw new Error('Room ID required');
    const team = document.querySelector('input[name="team"]:checked').value;
    await api.joinGame(roomId, nick, team);
    localStorage.setItem('zedmetsaxeli', nick);
    localStorage.setItem('otaxisId', roomId);
    window.location.href = 'game.html';
  } catch (err) {
    showError(err.message);
  }
}

document.getElementById('create-form').addEventListener('submit', handleCreate);
document.getElementById('join-form').addEventListener('submit', handleJoin);

if (localStorage.getItem('zedmetsaxeli') && localStorage.getItem('otaxisId')) {
  window.location.href = 'game.html';
}
