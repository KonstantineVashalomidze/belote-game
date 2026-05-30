import { getGameState } from './api.js';
import { getSession } from './storage.js';
import { startPolling } from './polling.js';

const { nickname, roomId } = getSession();
if (!nickname || !roomId) window.location.href = 'index.html';

const roomLink = document.getElementById('roomLink');
const copyBtn  = document.getElementById('copyBtn');
const teamADiv = document.getElementById('teamA');
const teamBDiv = document.getElementById('teamB');
const statusP  = document.getElementById('status');

const joinUrl = `${window.location.origin}?room=${roomId}`;
roomLink.textContent = joinUrl;

copyBtn.addEventListener('click', () => {
  navigator.clipboard.writeText(joinUrl);
  copyBtn.textContent = 'Copied!';
  setTimeout(() => (copyBtn.textContent = 'Copy link'), 2000);
});

function renderSlots(container, players, team, max = 2) {
  container.innerHTML = '';
  for (let i = 0; i < max; i++) {
    const div = document.createElement('div');
    div.className = `player-badge ${players[i] ? `team-${team.toLowerCase()}` : 'empty'}`;
    div.textContent = players[i] ? players[i].zedmetsaxeli : 'Waiting...';
    container.appendChild(div);
  }
}

const stopPolling = startPolling(async () => {
  try {
    const state = await getGameState(roomId, nickname);

    const teamA = state.motamasheebi.filter(p => p.gundi === 'A');
    const teamB = state.motamasheebi.filter(p => p.gundi === 'B');
    renderSlots(teamADiv, teamA, 'A');
    renderSlots(teamBDiv, teamB, 'B');

    const remaining = 4 - state.motamasheebi.length;
    if (remaining > 0) {
      statusP.innerHTML = `Waiting for ${remaining} more player${remaining > 1 ? 's' : ''} <span class="waiting-dots"><span>·</span><span>·</span><span>·</span></span>`;
    } else {
      statusP.textContent = 'Starting game!';
    }

    if (state.faza !== 'LODINI') {
      stopPolling();
      window.location.href = state.faza === 'KOZIROBA' ? 'bidding.html' : 'game.html';
    }
  } catch (err) {
    statusP.textContent = err.message;
  }
});
