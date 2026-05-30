export const SUIT_SYMBOLS = { GULI: '♥', WKENTI: '♠', JVARI: '♣', YVAVI: '♦' };
export const SUIT_COLORS  = { GULI: '#d32f2f', WKENTI: '#212121', JVARI: '#212121', YVAVI: '#d32f2f' };
export const RANK_LABELS  = { SHVIDI: '7', RVA: '8', CXRA: '9', ATI: '10', VALETI: 'J', DAMA: 'Q', KAROLI: 'K', TUZI: 'A' };

export function parseCard(str) {
  const i = str.indexOf('_');
  return { suit: str.slice(0, i), rank: str.slice(i + 1) };
}
