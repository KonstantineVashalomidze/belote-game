const API_BASE = '/api/tamashi';

async function handleResponse(response) {
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `HTTP ${response.status}`);
  }
  const text = await response.text();
  if (!text || text.trim().length === 0) {
    return {};
  }
  return JSON.parse(text);
}

export async function createGame(nickname) {
  const response = await fetch(`${API_BASE}/sheqmeni`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname })
  });
  return handleResponse(response);
}

export async function joinGame(otaxisId, nickname, team) {
  const response = await fetch(`${API_BASE}/shesvla`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ otaxisId, zedmetsaxeli: nickname, gundi: team })
  });
  return handleResponse(response);
}

export async function getState(otaxisId, nickname) {
  const response = await fetch(`${API_BASE}/${otaxisId}/mdgomareoba?zedmetsaxeli=${nickname}`);
  return handleResponse(response);
}

export async function bidTake(otaxisId, nickname) {
  const response = await fetch(`${API_BASE}/${otaxisId}/koziroba/pirvelshi`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname })
  });
  return handleResponse(response);
}

export async function bidPass(otaxisId, nickname) {
  const response = await fetch(`${API_BASE}/${otaxisId}/koziroba/pasi`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname })
  });
  return handleResponse(response);
}

export async function bidNameTrump(otaxisId, nickname, suit) {
  const response = await fetch(`${API_BASE}/${otaxisId}/koziroba/meoreshi`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname, cveti: suit })
  });
  return handleResponse(response);
}

export async function bidForced(otaxisId, nickname, suit) {
  const response = await fetch(`${API_BASE}/${otaxisId}/koziroba/vinujdeni`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname, cveti: suit })
  });
  return handleResponse(response);
}

export async function declareCombos(otaxisId, nickname, combos) {
  const response = await fetch(`${API_BASE}/${otaxisId}/kombinacia/cxadeba`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname, kombinaciebi: combos })
  });
  return handleResponse(response);
}

export async function markReady(otaxisId, nickname) {
  const response = await fetch(`${API_BASE}/${otaxisId}/kombinacia/mzadyofna`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname })
  });
  return handleResponse(response);
}

export async function showCombos(otaxisId, nickname) {
  const response = await fetch(`${API_BASE}/${otaxisId}/kombinacia/chveneba`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname })
  });
  return handleResponse(response);
}

export async function playCard(otaxisId, nickname, suit, rank, belote) {
  const response = await fetch(`${API_BASE}/${otaxisId}/kartis-chamosvla`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ zedmetsaxeli: nickname, cveti: suit, ranki: rank, belotisCxadeba: belote })
  });
  return handleResponse(response);
}
