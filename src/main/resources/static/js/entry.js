import { createGame, joinGame } from './api.js';
import { saveSession } from './storage.js';

const nicknameInput = document.getElementById('nickname');
const teamABtn      = document.getElementById('teamA');
const teamBBtn      = document.getElementById('teamB');
const playBtn       = document.getElementById('play');
const errorDiv      = document.getElementById('error');
const subtitleEl    = document.getElementById('subtitle');
const teamSelector  = document.getElementById('teamSelector');

const params = new URLSearchParams(window.location.search);
const roomId = params.get('room');
let selectedTeam = null;

if (!roomId) {
  teamSelector.style.display = 'none';
} else {
  subtitleEl.textContent = `Joining room ${roomId.slice(0, 8)}…`;
  playBtn.textContent = 'Join Game';
}

teamABtn.addEventListener('click', () => {
  selectedTeam = 'A';
  teamABtn.classList.add('selected');
  teamBBtn.classList.remove('selected');
});

teamBBtn.addEventListener('click', () => {
  selectedTeam = 'B';
  teamBBtn.classList.add('selected');
  teamABtn.classList.remove('selected');
});

playBtn.addEventListener('click', async () => {
  errorDiv.textContent = '';
  const nickname = nicknameInput.value.trim();

  if (!nickname) {
    errorDiv.textContent = 'Please enter a nickname';
    return;
  }

  if (roomId && !selectedTeam) {
    errorDiv.textContent = 'Please select a team';
    return;
  }

  playBtn.disabled = true;

  try {
    if (roomId) {
      await joinGame(nickname, roomId, selectedTeam);
      saveSession(nickname, roomId);
    } else {
      const res = await createGame(nickname);
      saveSession(nickname, res.otaxisId);
    }
    window.location.href = 'waiting.html';
  } catch (err) {
    errorDiv.textContent = err.message;
    playBtn.disabled = false;
  }
});
