import { getGameState, playCard, declareCombo, readyForPlay, showCombos } from './api.js';
import { getSession } from './storage.js';
import { startPolling } from './polling.js';
import { SUIT_SYMBOLS, SUIT_COLORS, RANK_LABELS, cardEl } from './cards.js';

const { nickname, roomId } = getSession();
if (!nickname || !roomId) window.location.href = 'index.html';

const scoreA          = document.getElementById('scoreA');
const trumpInfo       = document.getElementById('trumpInfo');
const scoreB          = document.getElementById('scoreB');
const pTop            = document.getElementById('pTop');
const pLeft           = document.getElementById('pLeft');
const pRight          = document.getElementById('pRight');
const pBottom         = document.getElementById('pBottom');
const trickArea       = document.getElementById('trickArea');
const tableInfo       = document.getElementById('tableInfo');
const actionsDiv      = document.getElementById('actions');
const handDiv         = document.getElementById('hand');
const errorDiv        = document.getElementById('error');
const comboOverlay    = document.getElementById('comboOverlay');
const comboHandDiv    = document.getElementById('comboHand');
const comboButtons    = document.getElementById('comboButtons');
const comboListDiv    = document.getElementById('comboList');
const readyBtn        = document.getElementById('readyBtn');
const comboHeader     = document.getElementById('comboHeader');
const comboError      = document.getElementById('comboError');
const roundEndOverlay = document.getElementById('roundEndOverlay');
const roundEndContent = document.getElementById('roundEndContent');
const gameOverOverlay = document.getElementById('gameOverOverlay');
const gameOverContent = document.getElementById('gameOverContent');

const SLOTS           = { 0: pBottom, 1: pLeft, 2: pTop, 3: pRight };
const RANK_ORDER      = ['SHVIDI', 'RVA', 'CXRA', 'ATI', 'VALETI', 'DAMA', 'KAROLI', 'TUZI'];
const TRUMP_RANK_ORDER = ['SHVIDI', 'RVA', 'ATI', 'DAMA', 'KAROLI', 'TUZI', 'CXRA', 'VALETI'];

// combo phase state (reset on each new phase entry)
let comboReady = false;
let selectedIdx = new Set();
let lockedIdx = new Set();
let pendingCombos = []; // [{ combo, indices: Set }]

// belote state
let pendingBeloteCard = null;
let beloteDeclared = false; // true once player has said "Belote" this round

// round-end display
let prevFaza = null;
let roundEndUntil = 0;
let prevScores = { a: 0, b: 0 };

// last trick display
let prevTrickCards = [];
let lastTrick = null; // { cards, winner }
let trickWonUntil = 0;

// trick counter
let tricksCompleted = 0;

// ─── SHARED HELPERS ─────────────────────────────────────────────────────────

function renderTopBar(state) {
  const myTeam = state.motamasheebi?.find(p => p.zedmetsaxeli === nickname)?.gundi;
  scoreA.textContent = `A: ${state.qula.gundiAQula}${myTeam === 'A' ? ' (you)' : ''}`;
  scoreB.textContent = `B: ${state.qula.gundiBQula}${myTeam === 'B' ? ' (you)' : ''}`;
  let html = '';
  if (state.qula.gayinuliQula > 0) html = `Frozen: ${state.qula.gayinuliQula}`;
  trumpInfo.innerHTML = html;
}

function renderPlayers(state) {
  const me = state.motamasheebi.find(p => p.zedmetsaxeli === nickname);
  if (!me) return;
  for (const p of state.motamasheebi) {
    const diff = (p.pozicia - me.pozicia + 4) % 4;
    const el = SLOTS[diff];
    if (!el) continue;
    const isActive   = p.zedmetsaxeli === state.mimdinareMotamashisZedmetsaxeli;
    const isMe       = p.zedmetsaxeli === nickname;
    const isMokozire = state.mokozire?.zedmetsaxeli === p.zedmetsaxeli && state.koziriCveti;
    el.className = `player-slot team-${p.gundi.toLowerCase()}${isActive ? ' active' : ''}`;
    const initial   = p.zedmetsaxeli.charAt(0).toUpperCase();
    const youBadge  = isMe ? `<span class="badge-you">you</span>` : '';
    const trumpBadge = isMokozire
      ? `<span class="badge-trump" style="color:${SUIT_COLORS[state.koziriCveti]};">${SUIT_SYMBOLS[state.koziriCveti]}</span>`
      : '';
    el.innerHTML = `<span class="avatar">${initial}</span>${youBadge}${p.zedmetsaxeli}${trumpBadge}`;
  }
}

function hideAllOverlays() {
  comboOverlay.style.display = 'none';
  roundEndOverlay.style.display = 'none';
  gameOverOverlay.style.display = 'none';
}

// ─── KARTIS_DARIGEBA ────────────────────────────────────────────────────────

function renderTableInfo(state) {
  if (!state.koziriCveti) { tableInfo.style.display = 'none'; return; }
  tableInfo.style.display = 'flex';
  const sym = SUIT_SYMBOLS[state.koziriCveti];
  const col = SUIT_COLORS[state.koziriCveti];
  const trickLine = state.faza === 'KRUGEBI'
    ? `<div style="opacity:0.85;">Trick ${Math.min(tricksCompleted + 1, 8)} / 8</div>`
    : '';
  const mokoziresLine = state.mokozire
    ? `<div style="opacity:0.65;">${state.mokozire.zedmetsaxeli} called trump</div>`
    : '';
  tableInfo.innerHTML = `<div style="font-size:1.4rem; line-height:1; color:${col};">${sym}</div>${trickLine}${mokoziresLine}`;
}

function renderDealing() {
  hideAllOverlays();
  trickArea.innerHTML = '';
  handDiv.innerHTML = '';
  actionsDiv.textContent = 'Dealing cards...';
  beloteDeclared = false;
  tricksCompleted = 0;
}

// ─── KOMBINACIIS_DEKLARACIA ─────────────────────────────────────────────────

function renderComboPhase(state) {
  hideAllOverlays();
  comboOverlay.style.display = 'flex';
  if (comboReady) return;
  comboReady = true;

  if (state.koziriCveti) {
    const sym = SUIT_SYMBOLS[state.koziriCveti];
    const col = SUIT_COLORS[state.koziriCveti];
    comboHeader.innerHTML = `Declare Combos &nbsp;<span style="color:${col}">${sym}</span>`;
  }

  selectedIdx.clear();
  lockedIdx.clear();
  pendingCombos = [];
  comboListDiv.innerHTML = '';
  comboError.textContent = '';
  comboButtons.style.display = 'none';

  renderComboHand(state.sheniKartebi, state.koziriCveti);

  readyBtn.onclick = async () => {
    readyBtn.disabled = true;
    comboError.textContent = '';
    try {
      if (pendingCombos.length > 0) {
        await declareCombo(roomId, nickname, pendingCombos.map(p => p.combo));
      }
      await readyForPlay(roomId, nickname);
      comboReady = false;
    } catch (err) {
      comboError.textContent = err.message;
      readyBtn.disabled = false;
    }
  };
}

function renderComboHand(cards, trumpSuit) {
  comboHandDiv.innerHTML = '';

  cards.forEach((card, i) => {
    const el = cardEl(card.cveti, card.ranki);
    el.style.margin = '0.2rem';

    if (lockedIdx.has(i)) {
      el.style.opacity = '0.3';
    } else {
      el.classList.add('playable');
      if (selectedIdx.has(i)) {
        el.style.transform = 'translateY(-8px)';
        el.style.boxShadow = '0 8px 16px rgba(245,124,0,0.5)';
      }
      el.addEventListener('click', () => {
        if (selectedIdx.has(i)) selectedIdx.delete(i); else selectedIdx.add(i);
        renderComboHand(cards, trumpSuit);
        updateComboTypeButtons(cards, trumpSuit);
      });
    }

    comboHandDiv.appendChild(el);
  });
}

function updateComboTypeButtons(cards, trumpSuit) {
  comboButtons.innerHTML = '';
  if (selectedIdx.size === 0) { comboButtons.style.display = 'none'; return; }
  comboButtons.style.display = 'block';

  const selected = [...selectedIdx].map(i => cards[i]);

  [['Sequence (Miyoleba)', 'MIYOLEBA'], ['Four of a Kind (Ertnairebi)', 'ERTNAIREBI']].forEach(([label, type]) => {
    const btn = document.createElement('button');
    btn.textContent = label;
    btn.style.cssText = 'background:#1976d2; color:white; margin:0.2rem;';
    btn.addEventListener('click', () => {
      const rankOrder = RANK_ORDER;
      const umaghlesiRanki = selected.reduce((best, c) =>
        rankOrder.indexOf(c.ranki) > rankOrder.indexOf(best) ? c.ranki : best
      , selected[0].ranki);

      const combo = {
        tipi: type,
        cveti: type === 'ERTNAIREBI' ? null : selected[0].cveti,
        umaghlesiRanki,
        sigrdze: selected.length
      };

      pendingCombos.push({ combo, indices: new Set(selectedIdx) });
      selectedIdx.forEach(i => lockedIdx.add(i));
      selectedIdx.clear();
      comboButtons.style.display = 'none';
      renderComboHand(cards, trumpSuit);
      renderComboList(cards, trumpSuit);
    });
    comboButtons.appendChild(btn);
  });
}

function renderComboList(cards, trumpSuit) {
  comboListDiv.innerHTML = '';
  pendingCombos.forEach(({ combo }, idx) => {
    const row = document.createElement('div');
    row.style.cssText = 'display:flex; justify-content:space-between; padding:0.3rem 0; border-bottom:1px solid #eee;';

    const label = combo.tipi === 'ERTNAIREBI'
      ? `Four ${RANK_LABELS[combo.umaghlesiRanki]}s`
      : `Sequence of ${combo.sigrdze}: ${SUIT_SYMBOLS[combo.cveti]} up to ${RANK_LABELS[combo.umaghlesiRanki]}`;

    const removeBtn = document.createElement('button');
    removeBtn.textContent = '×';
    removeBtn.style.cssText = 'background:none; color:#d32f2f; font-weight:bold; padding:0 0.3rem;';
    removeBtn.addEventListener('click', () => {
      pendingCombos[idx].indices.forEach(i => lockedIdx.delete(i));
      pendingCombos.splice(idx, 1);
      renderComboHand(cards, trumpSuit);
      renderComboList(cards, trumpSuit);
    });

    row.innerHTML = `<span>${label}</span>`;
    row.appendChild(removeBtn);
    comboListDiv.appendChild(row);
  });
}

// ─── KRUGEBI ────────────────────────────────────────────────────────────────

function renderTrickCards(cards) {
  cards.forEach(played => {
    const div = document.createElement('div');
    div.style.textAlign = 'center';
    div.appendChild(cardEl(played.cveti, played.ranki));
    const name = document.createElement('div');
    name.textContent = played.zedmetsaxeli;
    name.style.cssText = 'font-size:0.7rem; color:rgba(255,255,255,0.7); margin-top:0.15rem;';
    div.appendChild(name);
    trickArea.appendChild(div);
  });
}

function renderKrugebi(state) {
  hideAllOverlays();
  comboReady = false;

  const currTrick = state.mimdinareKrugi || [];

  // Detect trick completion: trick cards dropped away
  if (prevTrickCards.length > 0 && currTrick.length < prevTrickCards.length) {
    lastTrick = { cards: prevTrickCards, winner: state.mimdinareMotamashisZedmetsaxeli };
    trickWonUntil = Date.now() + 2000;
    tricksCompleted = Math.min(tricksCompleted + 1, 8);
  }
  prevTrickCards = currTrick;

  trickArea.innerHTML = '';

  if (Date.now() < trickWonUntil && lastTrick) {
    const label = document.createElement('div');
    label.textContent = `${lastTrick.winner} took the trick`;
    label.style.cssText = 'width:100%; text-align:center; color:#a5d6a7; font-size:0.8rem; font-weight:bold; margin-bottom:0.3rem;';
    trickArea.appendChild(label);
    renderTrickCards(lastTrick.cards);
  } else {
    renderTrickCards(currTrick);
  }

  if (pendingBeloteCard) {
    renderBeloteActions(pendingBeloteCard);
  } else {
    renderKrugebiActions(state);
  }

  const isMyTurn = state.mimdinareMotamashisZedmetsaxeli === nickname && !pendingBeloteCard;
  handDiv.innerHTML = '';
  const N = state.sheniKartebi.length;
  const spread = Math.min(3.5 * (N - 1), 24);
  const degPerCard = N > 1 ? spread / (N - 1) : 0;
  state.sheniKartebi.forEach((card, i) => {
    const el = cardEl(card.cveti, card.ranki);
    const angle = (i - (N - 1) / 2) * degPerCard;
    el.style.transform = `rotate(${angle}deg)`;
    el.style.zIndex = i;
    if (isMyTurn) {
      el.classList.add('playable');
      el.addEventListener('mouseenter', () => {
        el.style.transform = 'translateY(-12px)';
        el.style.zIndex = '20';
        el.style.boxShadow = '0 12px 24px rgba(0,0,0,0.35)';
      });
      el.addEventListener('mouseleave', () => {
        el.style.transform = `rotate(${angle}deg)`;
        el.style.zIndex = String(i);
        el.style.boxShadow = '';
      });
      el.addEventListener('click', () => handleCardClick(card, state));
    } else {
      el.style.opacity = '0.6';
    }
    handDiv.appendChild(el);
  });
}

function renderKrugebiActions(state) {
  actionsDiv.innerHTML = '';

  const showBtn = document.createElement('button');
  showBtn.textContent = 'Show Combos';
  showBtn.style.cssText = 'background:rgba(255,255,255,0.15); color:white; font-size:0.8rem; padding:0.4rem 0.8rem;';
  showBtn.addEventListener('click', async () => {
    errorDiv.textContent = '';
    try { await showCombos(roomId, nickname); } catch (err) { errorDiv.textContent = err.message; }
  });
  actionsDiv.appendChild(showBtn);

  if (state.kombinaciebRomblebicChaitvala?.length > 0) {
    const info = document.createElement('div');
    info.style.cssText = 'color:rgba(255,255,255,0.75); font-size:0.8rem; margin-top:0.3rem;';
    info.textContent = `Combos counted for Team ${state.romeliGundisKombinaciebiGadis}`;
    actionsDiv.appendChild(info);
  }
}

function handleCardClick(card, state) {
  const isTrumpKQ = card.cveti === state.koziriCveti &&
    (card.ranki === 'KAROLI' || card.ranki === 'DAMA');

  if (isTrumpKQ) {
    const hasK = state.sheniKartebi.some(c => c.cveti === state.koziriCveti && c.ranki === 'KAROLI');
    const hasQ = state.sheniKartebi.some(c => c.cveti === state.koziriCveti && c.ranki === 'DAMA');

    if (hasK && hasQ) {
      // First card of the K-Q pair — offer Belote or skip
      pendingBeloteCard = card;
      renderBeloteActions(card, false);
      return;
    }
    if (beloteDeclared) {
      // Previously declared Belote; this is the second card — offer Rebelote or skip
      pendingBeloteCard = card;
      renderBeloteActions(card, true);
      return;
    }
  }

  doPlayCard(card, 'ARAA_NACXADEBI');
}

function renderBeloteActions(card, isSecond) {
  const options = isSecond
    ? [['Rebelote', 'REBELOTIA_NACXADEBI'], ['No announcement', 'ARAA_NACXADEBI']]
    : [['Belote',   'BELOTIA_NACXADEBI'],   ['No announcement', 'ARAA_NACXADEBI']];

  actionsDiv.innerHTML = '';
  options.forEach(([label, val]) => {
    const btn = document.createElement('button');
    btn.textContent = label;
    btn.style.cssText = 'background:rgba(255,255,255,0.9); color:#333; margin:0.2rem;';
    btn.addEventListener('click', () => {
      pendingBeloteCard = null;
      if (val === 'BELOTIA_NACXADEBI') beloteDeclared = true;
      doPlayCard(card, val);
    });
    actionsDiv.appendChild(btn);
  });
}

async function doPlayCard(card, belote) {
  errorDiv.textContent = '';
  handDiv.querySelectorAll('.playing-card').forEach(e => e.style.pointerEvents = 'none');
  try {
    await playCard(roomId, nickname, card.cveti, card.ranki, belote);
  } catch (err) {
    errorDiv.textContent = err.message;
    handDiv.querySelectorAll('.playing-card').forEach(e => e.style.pointerEvents = '');
  }
}

// ─── QULEBIS_DATVLA ─────────────────────────────────────────────────────────

function renderRoundEnd(state, deltaA = 0, deltaB = 0) {
  roundEndOverlay.style.display = 'flex';
  const delta = n => n > 0 ? `<span style="color:#4caf50; margin-left:0.4rem;">+${n}</span>`
                   : n < 0 ? `<span style="color:#f44336; margin-left:0.4rem;">${n}</span>` : '';
  roundEndContent.innerHTML = `
    <p style="margin:0.5rem 0;">Team A: <strong>${state.qula.gundiAQula}</strong>${delta(deltaA)}</p>
    <p style="margin:0.5rem 0;">Team B: <strong>${state.qula.gundiBQula}</strong>${delta(deltaB)}</p>
    ${state.qula.gayinuliQula > 0 ? `<p style="margin:0.5rem 0; color:#f57c00;">Frozen: ${state.qula.gayinuliQula}</p>` : ''}
    <p style="margin-top:1rem; color:#777; font-size:0.85rem;">Next round starting...</p>
  `;
}

// ─── GAME OVER ──────────────────────────────────────────────────────────────

function renderGameOver(state) {
  gameOverOverlay.style.display = 'flex';
  gameOverContent.innerHTML = `
    <p style="font-size:1.5rem; font-weight:bold; margin-bottom:1rem;">Team ${state.gamarjvebuliGundi} wins!</p>
    <p>Team A: <strong>${state.qula.gundiAQula}</strong></p>
    <p>Team B: <strong>${state.qula.gundiBQula}</strong></p>
  `;
}

// ─── MAIN POLL ──────────────────────────────────────────────────────────────

const stopPolling = startPolling(async () => {
  try {
    const state = await getGameState(roomId, nickname);
    errorDiv.textContent = '';

    renderTopBar(state);
    renderPlayers(state);
    renderTableInfo(state);

    if (state.gamarjvebuliGundi) {
      stopPolling();
      renderGameOver(state);
      return;
    }

    if (state.faza === 'KOZIROBA') {
      stopPolling();
      window.location.href = 'bidding.html';
      return;
    }

    // Detect round end: faza just left KRUGEBI
    if (prevFaza === 'KRUGEBI' && state.faza !== 'KRUGEBI') {
      const deltaA = state.qula.gundiAQula - prevScores.a;
      const deltaB = state.qula.gundiBQula - prevScores.b;
      prevScores = { a: state.qula.gundiAQula, b: state.qula.gundiBQula };
      renderRoundEnd(state, deltaA, deltaB);
      roundEndUntil = Date.now() + 3000;
    }
    prevFaza = state.faza;

    // Hold rendering while round end overlay is showing
    if (Date.now() < roundEndUntil) return;

    if (state.faza !== 'KOMBINACIIS_DEKLARACIA') comboReady = false;
    if (state.faza !== 'KRUGEBI') { prevTrickCards = []; lastTrick = null; }

    switch (state.faza) {
      case 'KARTIS_DARIGEBA':        renderDealing();           break;
      case 'KOMBINACIIS_DEKLARACIA': renderComboPhase(state);   break;
      case 'KRUGEBI':                renderKrugebi(state);      break;
      case 'QULEBIS_DATVLA':         renderRoundEnd(state);     break;
    }
  } catch (err) {
    errorDiv.textContent = err.message;
  }
});
