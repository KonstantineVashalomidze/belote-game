import { createGame, joinGame } from './api.js';
import { saveSession } from './storage.js';

const nicknameInput = document.getElementById('nickname');
const teamABtn = document.getElementById('teamA');
const teamBBtn = document.getElementById('teamB');
const playBtn = document.getElementById('play');
const errorDiv = document.getElementById('error');

const params = new URLSearchParams(window.location.search);
const roomId = params.get('room');
let selectedTeam = null;

const teamSelector = document.getElementById('teamSelector');
if (!roomId) {
  teamSelector.style.display = 'none';
}

teamABtn.addEventListener('click', () => {
  selectedTeam = 'A';
  teamABtn.style.opacity = '1';
  teamBBtn.style.opacity = '0.5';
});

teamBBtn.addEventListener('click', () => {
  selectedTeam = 'B';
  teamBBtn.style.opacity = '1';
  teamABtn.style.opacity = '0.5';
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
