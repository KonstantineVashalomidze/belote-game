import { getGameState } from './api.js';
import { getSession } from './storage.js';
import { startPolling } from './polling.js';

const { nickname, roomId } = getSession();
if (!nickname || !roomId) {
  window.location.href = 'index.html';
}

const roomLink = document.getElementById('roomLink');
const copyBtn = document.getElementById('copyBtn');
const teamADiv = document.getElementById('teamA');
const teamBDiv = document.getElementById('teamB');
const statusP = document.getElementById('status');

const joinUrl = `${window.location.origin}?room=${roomId}`;
roomLink.textContent = joinUrl;

copyBtn.addEventListener('click', () => {
  navigator.clipboard.writeText(joinUrl);
  copyBtn.textContent = 'Copied!';
  setTimeout(() => (copyBtn.textContent = 'Copy'), 2000);
});

function renderSlots(container, players, max = 2) {
  container.innerHTML = '';
  for (let i = 0; i < max; i++) {
    const p = document.createElement('p');
    p.style.cssText = 'padding: 0.5rem 0; border-bottom: 1px solid #eee; color: #333;';
    p.textContent = players[i] ? players[i].zedmetsaxeli : 'Waiting...';
    if (!players[i]) p.style.color = '#aaa';
    container.appendChild(p);
  }
}

const stopPolling = startPolling(async () => {
  try {
    const state = await getGameState(roomId, nickname);

    const teamA = state.motamasheebi.filter(p => p.gundi === 'A');
    const teamB = state.motamasheebi.filter(p => p.gundi === 'B');
    renderSlots(teamADiv, teamA);
    renderSlots(teamBDiv, teamB);

    const total = state.motamasheebi.length;
    const remaining = 4 - total;
    statusP.textContent = remaining > 0
      ? `Waiting for ${remaining} more player${remaining > 1 ? 's' : ''}...`
      : 'Starting game...';

    if (state.faza !== 'LODINI') {
      stopPolling();
      window.location.href = state.faza === 'KOZIROBA' ? 'bidding.html' : 'game.html';
    }
  } catch (err) {
    statusP.textContent = err.message;
  }
});
