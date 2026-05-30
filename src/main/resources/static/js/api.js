const API_BASE = '/api/tamashi';

async function post(url, body) {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text);
  }
  const text = await res.text();
  return text.trim() ? JSON.parse(text) : null;
}

export const createGame = (nickname) =>
  post(`${API_BASE}/sheqmeni`, { zedmetsaxeli: nickname });

export const joinGame = (nickname, roomId, team) =>
  post(`${API_BASE}/shesvla`, { zedmetsaxeli: nickname, otaxisId: roomId, gundi: team });

export const bidAccept = (roomId, nickname) =>
  post(`${API_BASE}/${roomId}/koziroba/pirvelshi`, { zedmetsaxeli: nickname });

export const bidPass = (roomId, nickname) =>
  post(`${API_BASE}/${roomId}/koziroba/pasi`, { zedmetsaxeli: nickname });

export const bidSuitRound2 = (roomId, nickname, suit) =>
  post(`${API_BASE}/${roomId}/koziroba/meoreshi`, { zedmetsaxeli: nickname, cveti: suit });

export const bidSuitForced = (roomId, nickname, suit) =>
  post(`${API_BASE}/${roomId}/koziroba/vinujdeni`, { zedmetsaxeli: nickname, cveti: suit });

export async function getGameState(roomId, nickname) {
  const res = await fetch(`${API_BASE}/${roomId}/mdgomareoba?zedmetsaxeli=${encodeURIComponent(nickname)}`);
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text);
  }
  return res.json();
}
