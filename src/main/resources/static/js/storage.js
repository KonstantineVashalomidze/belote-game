const NICKNAME_KEY = 'belote_nickname';
const ROOM_ID_KEY = 'belote_roomId';

export function saveSession(nickname, roomId) {
  localStorage.setItem(NICKNAME_KEY, nickname);
  localStorage.setItem(ROOM_ID_KEY, roomId);
}

export function getSession() {
  const nickname = localStorage.getItem(NICKNAME_KEY);
  const roomId = localStorage.getItem(ROOM_ID_KEY);
  return { nickname, roomId };
}

export function clearSession() {
  localStorage.removeItem(NICKNAME_KEY);
  localStorage.removeItem(ROOM_ID_KEY);
}
