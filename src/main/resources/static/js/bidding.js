import { getGameState, bidAccept, bidPass, bidSuitRound2, bidSuitForced } from './api.js';
import { getSession } from './storage.js';
import { startPolling } from './polling.js';
import { SUIT_SYMBOLS, SUIT_COLORS, RANK_LABELS, parseCard } from './cards.js';

const { nickname, roomId } = getSession();
if (!nickname || !roomId) window.location.href = 'index.html';

const pTop    = document.getElementById('pTop');
const pLeft   = document.getElementById('pLeft');
const pRight  = document.getElementById('pRight');
const pBottom = document.getElementById('pBottom');
const centerCard = document.getElementById('centerCard');
const trumpBar   = document.getElementById('trumpBar');
const actionsDiv = document.getElementById('actions');
const errorDiv   = document.getElementById('error');

const SLOTS = { 0: pBottom, 1: pRight, 2: pTop, 3: pLeft };
const SUITS = ['GULI', 'WKENTI', 'JVARI', 'YVAVI'];

function renderPlayer(el, player, isActive) {
  el.textContent = player ? player.zedmetsaxeli : '';
  el.className = 'player-slot' + (isActive ? ' active' : '');
}

function renderFlippedCard(str) {
  if (!str) { centerCard.innerHTML = ''; return; }
  const { suit, rank } = parseCard(str);
  const sym = SUIT_SYMBOLS[suit];
  const col = SUIT_COLORS[suit];
  centerCard.innerHTML = `<span class="playing-card" style="color:${col}">${sym} ${RANK_LABELS[rank]}</span>`;
}

function suitButton(suit, onClick) {
  const btn = document.createElement('button');
  btn.style.cssText = `background:white; border:2px solid ${SUIT_COLORS[suit]}; color:${SUIT_COLORS[suit]}; margin:0.25rem;`;
  btn.textContent = SUIT_SYMBOLS[suit];
  btn.addEventListener('click', onClick);
  return btn;
}

function setActionsDisabled(disabled) {
  actionsDiv.querySelectorAll('button').forEach(b => b.disabled = disabled);
}

async function doAction(fn) {
  errorDiv.textContent = '';
  setActionsDisabled(true);
  try {
    await fn();
  } catch (err) {
    errorDiv.textContent = err.message;
    setActionsDisabled(false);
  }
}

function renderActions(state) {
  actionsDiv.innerHTML = '';
  const { kozirobisFaza, mimdinareMotamashisZedmetsaxeli, sityvaVinujdenzea, amotrialebuliKarti } = state;
  const isMyTurn = mimdinareMotamashisZedmetsaxeli === nickname;

  if (kozirobisFaza === 'PIRVELI_KRUGI') {
    if (!isMyTurn) {
      actionsDiv.textContent = `Waiting for ${mimdinareMotamashisZedmetsaxeli}...`;
      return;
    }
    const acceptBtn = document.createElement('button');
    acceptBtn.textContent = 'Accept';
    acceptBtn.style.cssText = 'background:#388e3c; color:white; margin:0.25rem;';
    acceptBtn.addEventListener('click', () => doAction(() => bidAccept(roomId, nickname)));

    const passBtn = document.createElement('button');
    passBtn.textContent = 'Pass';
    passBtn.style.cssText = 'background:#757575; color:white; margin:0.25rem;';
    passBtn.addEventListener('click', () => doAction(() => bidPass(roomId, nickname)));

    actionsDiv.append(acceptBtn, passBtn);
    return;
  }

  if (kozirobisFaza === 'MEORE_KRUGI') {
    if (!isMyTurn) {
      actionsDiv.textContent = sityvaVinujdenzea
        ? 'Waiting for dealer...'
        : `Waiting for ${mimdinareMotamashisZedmetsaxeli}...`;
      return;
    }
    const flippedSuit = amotrialebuliKarti ? parseCard(amotrialebuliKarti).suit : null;
    SUITS.filter(s => s !== flippedSuit).forEach(suit => {
      actionsDiv.appendChild(suitButton(suit, () => doAction(() => bidSuitRound2(roomId, nickname, suit))));
    });
    if (!sityvaVinujdenzea) {
      const passBtn = document.createElement('button');
      passBtn.textContent = 'Pass';
      passBtn.style.cssText = 'background:#757575; color:white; margin:0.25rem;';
      passBtn.addEventListener('click', () => doAction(() => bidPass(roomId, nickname)));
      actionsDiv.appendChild(passBtn);
    }
    return;
  }
}

const stopPolling = startPolling(async () => {
  try {
    const state = await getGameState(roomId, nickname);

    if (state.faza !== 'KOZIROBA') {
      stopPolling();
      window.location.href = 'game.html';
      return;
    }

    // position players relative to me
    const me = state.motamasheebi.find(p => p.zedmetsaxeli === nickname);
    if (me) {
      for (const p of state.motamasheebi) {
        const diff = (p.pozicia - me.pozicia + 4) % 4;
        const el = SLOTS[diff];
        if (el) renderPlayer(el, p, p.zedmetsaxeli === state.mimdinareMotamashisZedmetsaxeli);
      }
    }

    renderFlippedCard(state.amotrialebuliKarti);

    // trump bar
    if (state.koziriCveti) {
      const sym = SUIT_SYMBOLS[state.koziriCveti];
      const col = SUIT_COLORS[state.koziriCveti];
      let text = `Trump: <span style="color:${col}; font-weight:bold;">${sym}</span>`;
      if (state.mokozire) text += ` &mdash; declared by ${state.mokozire.zedmetsaxeli}`;
      trumpBar.innerHTML = text;
    }

    if (state.kozirobisFaza === 'DASRULEBULI') {
      stopPolling();
      window.location.href = 'game.html';
      return;
    }

    renderActions(state);
  } catch (err) {
    errorDiv.textContent = err.message;
  }
});
