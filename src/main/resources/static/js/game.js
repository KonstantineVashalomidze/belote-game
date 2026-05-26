import * as api from './api.js';

const SUIT_SYMBOLS = { GULI: '♥', WKENTI: '♠', JVARI: '♣', YVAVI: '♦' };
const SUIT_COLORS = { GULI: 'red', WKENTI: 'black', JVARI: 'black', YVAVI: 'red' };
const RANK_LABELS = { SHVIDI: '7', RVA: '8', CXRA: '9', ATI: '10', VALETI: 'J', DAMA: 'Q', KAROLI: 'K', TUZI: 'A' };

let nickname = localStorage.getItem('zedmetsaxeli');
let otaxisId = localStorage.getItem('otaxisId');
let currentState = null;
let declarePhaseStart = 0;
let declaredCombos = [];

if (!nickname || !otaxisId) {
  window.location.href = 'index.html';
}

function showError(msg) {
  const el = document.getElementById('error-display');
  el.textContent = msg;
  el.style.display = 'block';
  setTimeout(() => { el.style.display = 'none'; }, 4000);
}

function hideAllPhases() {
  ['s-waiting', 's-bidding', 's-declare', 's-tricks', 's-gameover'].forEach(id => {
    document.getElementById(id).classList.add('hidden');
  });
}

function formatCard(cveti, ranki) {
  return `${RANK_LABELS[ranki]}${SUIT_SYMBOLS[cveti]}`;
}

function createCardEl(cveti, ranki, clickable = false, onclick = null) {
  const el = document.createElement('div');
  el.className = 'card';
  el.className += ' ' + SUIT_COLORS[cveti];
  el.textContent = formatCard(cveti, ranki);
  if (clickable && onclick) {
    el.style.cursor = 'pointer';
    el.onclick = onclick;
  }
  return el;
}

async function poll() {
  try {
    currentState = await api.getState(otaxisId, nickname);
    if (!currentState) {
      showError('Empty state response');
      return;
    }
    document.getElementById('room-display').textContent = otaxisId;
    if (currentState.qula) {
      document.getElementById('score-a').textContent = currentState.qula.gundiAQula || 0;
      document.getElementById('score-b').textContent = currentState.qula.gundiBQula || 0;
    }
    render(currentState);
  } catch (err) {
    console.error('Poll error:', err);
    showError('Poll failed: ' + err.message);
  }
}

function render(state) {
  hideAllPhases();

  if (state.gamarjvebuliGundi !== null) {
    renderGameOver(state);
    return;
  }

  switch (state.faza) {
    case 'LODINI':
      renderWaiting(state);
      break;
    case 'KOZIROBA':
      renderBidding(state);
      break;
    case 'KOMBINACIIS_DEKLARACIA':
      renderDeclare(state);
      break;
    case 'KRUGEBI':
    case 'QULEBIS_DATVLA':
      renderTricks(state);
      break;
    default:
      showError('Unknown phase: ' + state.faza);
  }
}

function renderWaiting(state) {
  document.getElementById('s-waiting').classList.remove('hidden');

  const count = (state.motamasheebi || []).length;
  const needed = 4 - count;
  document.getElementById('waiting-text').textContent =
    needed > 0 ? `Waiting for ${needed} more player${needed > 1 ? 's' : ''}...` : 'All players joined!';

  const list = document.getElementById('waiting-players');
  list.innerHTML = '';
  (state.motamasheebi || []).forEach(p => {
    const el = document.createElement('div');
    el.className = 'player-item team-' + p.gundi.toLowerCase();
    el.textContent = p.zedmetsaxeli + ' (Team ' + p.gundi + ')';
    list.appendChild(el);
  });
}

function renderBidding(state) {
  document.getElementById('s-bidding').classList.remove('hidden');

  const flipped = state.amotrialebuliKarti;
  if (flipped) {
    const [cveti, ranki] = flipped.split('_');
    document.getElementById('flipped-card').textContent = formatCard(cveti, ranki);
  }

  const actions = document.getElementById('bidding-actions');
  actions.innerHTML = '';

  const isMyTurn = state.mimdinareMotamashisZedmetsaxeli === nickname;

  if (!isMyTurn) {
    const msg = document.createElement('div');
    msg.className = 'waiting-msg';
    msg.textContent = `Waiting for ${state.mimdinareMotamashisZedmetsaxeli}...`;
    actions.appendChild(msg);
    return;
  }

  if (state.kozirobisFaza === 'PIRVELI_KRUGI') {
    const take = document.createElement('button');
    take.textContent = 'Take Trump';
    take.onclick = () => bidTake();

    const pass = document.createElement('button');
    pass.textContent = 'Pass';
    pass.onclick = () => bidPass();

    actions.appendChild(take);
    actions.appendChild(pass);
  } else if (state.kozirobisFaza === 'MEORE_KRUGI') {
    const suit = document.createElement('select');
    const suits = ['GULI', 'WKENTI', 'JVARI', 'YVAVI'];
    suits.forEach(s => {
      if (flipped && !flipped.startsWith(s)) {
        const opt = document.createElement('option');
        opt.value = s;
        opt.textContent = `${SUIT_SYMBOLS[s]} ${s}`;
        suit.appendChild(opt);
      }
    });
    actions.appendChild(suit);

    if (state.sityvaVinujdenzea) {
      const take = document.createElement('button');
      take.textContent = 'Take (Forced)';
      take.onclick = () => {
        const s = suit.value;
        bidForced(s);
      };
      actions.appendChild(take);
    } else {
      const take = document.createElement('button');
      take.textContent = 'Take Trump';
      take.onclick = () => {
        const s = suit.value;
        bidNameTrump(s);
      };

      const pass = document.createElement('button');
      pass.textContent = 'Pass';
      pass.onclick = () => bidPass();

      actions.appendChild(take);
      actions.appendChild(pass);
    }
  }
}

function renderDeclare(state) {
  document.getElementById('s-declare').classList.remove('hidden');

  if (declarePhaseStart === 0) {
    declarePhaseStart = Date.now();
    declaredCombos = [];
  }

  const elapsed = Math.floor((Date.now() - declarePhaseStart) / 1000);
  const remaining = Math.max(0, 30 - elapsed);
  document.getElementById('timer-text').textContent = remaining;

  if (remaining === 0 && declaredCombos.length === 0) {
    declaredCombos = [];
  }

  const hand = document.getElementById('declare-hand');
  hand.innerHTML = '';
  (state.sheniKartebi || []).forEach(card => {
    hand.appendChild(createCardEl(card.cveti, card.ranki));
  });

  updateComboListDisplay();
}

function updateComboForm() {
  const type = document.getElementById('combo-type').value;
  const suit = document.getElementById('combo-suit');
  const len = document.getElementById('combo-length');

  if (type === 'ERTNAIREBI') {
    suit.disabled = true;
    suit.value = '';
    len.value = 4;
    len.disabled = true;
  } else {
    suit.disabled = false;
    len.disabled = false;
    if (len.value === '4' && len.previousValue !== '4') len.value = '3';
  }
}

function addCombo() {
  const type = document.getElementById('combo-type').value;
  const suit = type === 'ERTNAIREBI' ? null : document.getElementById('combo-suit').value;
  const rank = document.getElementById('combo-rank').value;
  const length = parseInt(document.getElementById('combo-length').value) || 3;

  if (!suit && type !== 'ERTNAIREBI') {
    showError('Select a suit');
    return;
  }

  declaredCombos.push({ tipi: type, cveti: suit, umaghlesiRanki: rank, sigrdze: length });
  updateComboListDisplay();
}

function updateComboListDisplay() {
  const list = document.getElementById('combo-list-display');
  list.innerHTML = '';

  declaredCombos.forEach((combo, idx) => {
    const item = document.createElement('div');
    item.className = 'combo-item';

    const suit = combo.cveti ? `${SUIT_SYMBOLS[combo.cveti]} ` : '';
    const label = combo.tipi === 'ERTNAIREBI'
      ? `Four of a Kind (${RANK_LABELS[combo.umaghlesiRanki]}) - ${combo.sigrdze}`
      : `Sequence ${suit}(${RANK_LABELS[combo.umaghlesiRanki]}) - ${combo.sigrdze} cards`;

    const text = document.createElement('span');
    text.textContent = label;

    const btn = document.createElement('button');
    btn.textContent = 'Remove';
    btn.onclick = () => {
      declaredCombos.splice(idx, 1);
      updateComboListDisplay();
    };

    item.appendChild(text);
    item.appendChild(btn);
    list.appendChild(item);
  });
}

function renderTricks(state) {
  document.getElementById('s-tricks').classList.remove('hidden');

  const hand = document.getElementById('tricks-hand');
  hand.innerHTML = '';

  const isMyTurn = state.mimdinareMotamashisZedmetsaxeli === nickname;

  (state.sheniKartebi || []).forEach(card => {
    const el = createCardEl(card.cveti, card.ranki, isMyTurn, () => {
      selectCard(card.cveti, card.ranki);
    });
    if (!isMyTurn) {
      el.classList.add('disabled');
    }
    hand.appendChild(el);
  });

  const trick = document.getElementById('trick-display');
  trick.innerHTML = '';

  const players = state.motamasheebi || [];
  if (state.mimdinareKrugi && state.mimdinareKrugi.length > 0) {
    state.mimdinareKrugi.forEach(played => {
      const player = players.find(p => p.zedmetsaxeli === played.zedmetsaxeli);
      const div = document.createElement('div');
      div.className = 'trick-player';

      const name = document.createElement('div');
      name.className = 'name';
      name.textContent = played.zedmetsaxeli;

      const card = createCardEl(played.cveti, played.ranki);

      div.appendChild(name);
      div.appendChild(card);
      trick.appendChild(div);
    });
  }

  const msg = document.getElementById('tricks-message');
  if (isMyTurn) {
    msg.textContent = 'Your turn - click a card to play';
  } else {
    msg.textContent = `Waiting for ${state.mimdinareMotamashisZedmetsaxeli}...`;
  }

  const btns = document.getElementById('tricks-buttons');
  btns.innerHTML = '';
  if (state.romeliGundisKombinaciebiGadis && state.romeliGundisKombinaciebiGadis !== '') {
    const show = document.createElement('button');
    show.textContent = 'Show Combos';
    show.onclick = () => {
      showCombo();
    };
    btns.appendChild(show);
  }
}

function renderGameOver(state) {
  document.getElementById('s-gameover').classList.remove('hidden');

  const winner = state.gamarjvebuliGundi;
  document.getElementById('winner-text').textContent = `Team ${winner} Wins!`;
  document.getElementById('final-score-a').textContent = state.qula.gundiAQula;
  document.getElementById('final-score-b').textContent = state.qula.gundiBQula;
}

async function bidTake() {
  try {
    await api.bidTake(otaxisId, nickname);
    await poll();
  } catch (err) {
    showError('Bid failed: ' + err.message);
  }
}

async function bidPass() {
  try {
    await api.bidPass(otaxisId, nickname);
    await poll();
  } catch (err) {
    showError('Bid failed: ' + err.message);
  }
}

async function bidNameTrump(suit) {
  try {
    await api.bidNameTrump(otaxisId, nickname, suit);
    await poll();
  } catch (err) {
    showError('Bid failed: ' + err.message);
  }
}

async function bidForced(suit) {
  try {
    await api.bidForced(otaxisId, nickname, suit);
    await poll();
  } catch (err) {
    showError('Bid failed: ' + err.message);
  }
}

async function declareAllCombos() {
  try {
    await api.declareCombos(otaxisId, nickname, declaredCombos);
    declarePhaseStart = 0;
    declaredCombos = [];
    await poll();
  } catch (err) {
    showError('Declare failed: ' + err.message);
  }
}

async function declareNoCombos() {
  try {
    await api.markReady(otaxisId, nickname);
    declarePhaseStart = 0;
    declaredCombos = [];
    await poll();
  } catch (err) {
    showError('Ready failed: ' + err.message);
  }
}

let selectedCard = null;

async function selectCard(cveti, ranki) {
  selectedCard = { cveti, ranki };

  if ((cveti === currentState.koziriCveti && ranki === 'KAROLI') ||
      (cveti === currentState.koziriCveti && ranki === 'DAMA')) {

    const belote = document.createElement('div');
    belote.className = 'belote-radio';

    const none = document.createElement('label');
    none.innerHTML = '<input type="radio" name="belote" value="ARAA_NACXADEBI" checked> No announcement';

    const bel = document.createElement('label');
    bel.innerHTML = '<input type="radio" name="belote" value="BELOTIA_NACXADEBI"> Belote';

    const rebel = document.createElement('label');
    rebel.innerHTML = '<input type="radio" name="belote" value="REBELOTIA_NACXADEBI"> Rebelote';

    const confirmBtn = document.createElement('button');
    confirmBtn.textContent = 'Play';
    confirmBtn.onclick = async () => {
      const beloteVal = document.querySelector('input[name="belote"]:checked').value;
      await playCard(selectedCard.cveti, selectedCard.ranki, beloteVal);
      belote.remove();
    };

    belote.appendChild(none);
    belote.appendChild(bel);
    belote.appendChild(rebel);
    belote.appendChild(confirmBtn);

    document.getElementById('tricks-buttons').appendChild(belote);
  } else {
    await playCard(cveti, ranki, 'ARAA_NACXADEBI');
  }
}

async function playCard(cveti, ranki, belote) {
  try {
    await api.playCard(otaxisId, nickname, cveti, ranki, belote);
    selectedCard = null;
    await poll();
  } catch (err) {
    showError('Play failed: ' + err.message);
  }
}

async function showCombo() {
  try {
    await api.showCombos(otaxisId, nickname);
    await poll();
  } catch (err) {
    showError('Show combos failed: ' + err.message);
  }
}

function backToLobby() {
  localStorage.removeItem('zedmetsaxeli');
  localStorage.removeItem('otaxisId');
  window.location.href = 'index.html';
}

updateComboForm();
poll();
setInterval(poll, 2000);
