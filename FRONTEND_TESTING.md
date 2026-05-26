# Belote Frontend - Testing Guide

## Issue Found & Fixed

**Problem:** When testing with 4 browsers, only one player saw updates and got error:
```
JSON.parse: unexpected end of data at line 1 column 1 of the JSON data
```

**Root Cause:** Some API endpoints (like `/shesvla` - join game) return HTTP 200 with an empty response body. The original `handleResponse()` function tried to parse empty text as JSON, causing the parse error.

**Solution:** Updated `js/api.js` to check if response body is empty before parsing as JSON. Empty responses now return `{}` instead of trying to parse nothing.

---

## How to Test from Your Browsers

### 1. **Start the application**
```bash
mvn spring-boot:run
```
Server runs on `http://localhost:8080`

### 2. **Open 4 browser windows/tabs**
- Browser 1: Edge normal
- Browser 2: Edge private/incognito
- Browser 3: Firefox normal
- Browser 4: Firefox private/incognito

### 3. **Create game in Browser 1**
- Navigate to `http://localhost:8080`
- Enter nickname: `alice` (only lowercase letters, no numbers/spaces)
- Click "Create Game"
- You'll be redirected to the game screen
- Save your Room ID (shown at top)

### 4. **Join game in other 3 browsers**
- In each of the other 3 browser tabs:
  - Navigate to `http://localhost:8080`
  - Click "Join Game" tab
  - Nickname: use different names for each (`bob`, `charlie`, `diana`)
  - Room ID: paste the Room ID from step 3
  - Team: select A or B (max 2 players per team, so use A, B, A, B pattern)
  - Click "Join Game"
  - Each should redirect to game screen

### 5. **Verify synchronization**
Once all 4 players join:
- All 4 browsers should show: **LODINI phase** (waiting room)
- Each should see all 4 players listed
- Each should show their own 8-card hand

When 4 players are joined, game auto-starts:
- All 4 should transition to **KOZIROBA phase** (bidding)
- Flipped card shown in all 4 browsers
- All see the same active player

### 6. **Test each phase**

**Bidding Phase (KOZIROBA):**
- Active player clicks "Take Trump" or "Pass"
- After player 1 passes, player 2 takes (or passes)
- Other players pass to complete round
- Game advances to next phase

**Combo Declaration (KOMBINACIIS_DEKLARACIA):**
- 30-second timer shown
- Players can declare combinations or just click "No Combos"
- Each player clicks ready
- All 4 must ready before advancing

**Card Play (KRUGEBI):**
- Active player clicks a card from their hand to play
- If card is trump King or Queen, belote radio appears
- Played cards appear in "Current Trick" section
- After 8 tricks, scores update and new round starts

### 7. **Expected behavior**
✅ All 4 browsers always see the same game state
✅ Each player only sees their own cards
✅ Clicking buttons in one browser updates the display in all 4
✅ No JSON parsing errors
✅ No error messages (unless a move is illegal)

---

## Important Nickname Rules
- **Only lowercase letters a-z**
- No numbers, spaces, or special characters
- Examples: `alice`, `bob`, `charlie`, `playerone` ✅
- Examples: `player1`, `Bob`, `alice123`, `alice smith` ❌

## Troubleshooting

**Issue: "Unexpected JSON parse error"**
- Reload the browser
- Check browser console (F12) for full error
- Make sure server is running: `mvn spring-boot:run`

**Issue: Only one player joined**
- Check you selected different teams (A/B alternating)
- Each team has max 2 players
- If team is full, use the other team

**Issue: Nothing happens when clicking buttons**
- Check if it's your turn (you should be highlighted as active)
- Try refreshing the page
- Ensure all 4 players have joined

**Issue: Browser shows "waiting for player X"**
- That player hasn't taken their action yet
- Click their button/action in their browser tab
- All 4 must complete each action before advancing

---

## Files Modified

- `src/main/resources/static/js/api.js` - Fixed empty response handling
- `src/main/resources/static/js/game.js` - Added better error logging
- All other files unchanged and working correctly
