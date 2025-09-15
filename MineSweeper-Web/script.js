const API = "http://localhost:8080/api/v1";

const boardElement = document.getElementById("board");
const logElement = document.getElementById("log");
const rowsInput = document.getElementById("rows");
const colsInput = document.getElementById("cols");
const newGameButton = document.getElementById("newGame");

let gameId = null;
let lastState = null;

function renderFromState(state) {
  state.cells = state.cells ?? state.board;
  lastState = state;

  boardElement.style.gridTemplateColumns = `repeat(${state.cols}, var(--cell))`;
  boardElement.innerHTML = "";

  const over = state.status !== "playing";

  for (let r = 0; r < state.rows; r++) {
    for (let c = 0; c < state.cols; c++) {
      const cell = state.cells[r][c];
      const btn = document.createElement("button");
      btn.className = "cell";
      btn.setAttribute("data-r", r);
      btn.setAttribute("data-c", c);
      btn.title = `r${r}, c${c}`;

      if (cell.revealed) btn.classList.add("revealed");
      if (!cell.revealed && cell.flagged) btn.classList.add("flagged");
      if (over && cell.mine) btn.classList.add("mine");

      if (cell.revealed && !cell.mine && Number(cell.adjacent || 0) > 0) {
        btn.textContent = String(cell.adjacent);
      } else if (cell.revealed && cell.mine) {
        btn.textContent = "💣";
      } else if (!cell.revealed && cell.flagged) {
        btn.textContent = "🚩";
      } else {
        btn.textContent = "";
      }

      btn.addEventListener("click", onLeftClick);
      btn.addEventListener("contextmenu", onRightClick);

      boardElement.appendChild(btn);
    }
  }

  if (state.status === "won") log("You won! 🎉");
  else if (state.status === "lost") log("You lost! 💥");
}

async function onLeftClick(e) {
  console.log("[Probe] left click fired");
  if (!gameId) return log("Create a game first");

  if (!lastState || lastState.status !== "playing") return;

  const btn = e.currentTarget;
  const r = +btn.getAttribute("data-r");
  const c = +btn.getAttribute("data-c");
  try {
    const state = await apiReveal(gameId, r, c);
    renderFromState(state);
  } catch (err) {
    log(`Error: ${err.message}`);
  }
}

async function onRightClick(e) {
  e.preventDefault();
  if (!gameId) return log("Create a game first");
  if (!lastState || lastState.status !== "playing") return;
  const btn = e.currentTarget;
  const r = +btn.getAttribute("data-r");
  const c = +btn.getAttribute("data-c");
  try {
    const state = await apiFlag(gameId, r, c);
    renderFromState(state);
  } catch (err) {
    log(`Error: ${err.message}`);
  }
}

function log(message) {
  const ts = new Date().toLocaleTimeString();
  logElement.textContent = `[${ts}] ${message}\n` + logElement.textContent;
}

newGameButton.addEventListener("click", async () => {
  const rows = Math.max(2, +rowsInput.value || 10);
  const cols = Math.max(2, +colsInput.value || 10);
  const mines = Math.max(
    1,
    Math.min(rows * cols - 1, Math.floor(rows * cols * 0.15))
  );
  try {
    const state = await apiCreateGame(rows, cols, mines);
    gameId = state.gameId;
    log(`New game: ${rows}x${cols}, mines= ${state.mines}, id= ${gameId}`);
    renderFromState(state);
  } catch (err) {
    log(`Create game error: ${err.message}`);
  }
});

async function apiJson(url, opts = {}) {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...opts,
  });
  let data;
  try {
    data = await res.json();
  } catch (e) {
    throw new Error(`Invalid JSON response: ${e.message}`);
  }

  if (!res.ok) {
    const msg = data?.message || `HTTP ${res.status}`;
    throw new Error(msg);
  }
  return data;
}

async function apiCreateGame(rows, cols, mines, extras = {}) {
  return apiJson(`${API}/games`, {
    method: "POST",
    body: JSON.stringify({
      rows,
      cols,
      mines,
      firstClickSafe: true,
      revealMinesOnGameOver: true,
      ...extras,
    }),
  });
}

async function apiGetState(id) {
  return apiJson(`${API}/games/${encodeURIComponent(id)}`);
}

async function apiReveal(id, row, col) {
  return apiJson(`${API}/games/${encodeURIComponent(id)}/reveal`, {
    method: "POST",
    body: JSON.stringify({ row, col }),
  });
}

async function apiFlag(id, row, col) {
  return apiJson(`${API}/games/${encodeURIComponent(id)}/flag`, {
    method: "POST",
    body: JSON.stringify({ row, col }),
  });
}

// window.addEventListener("DOMContentLoaded", () => newGameButton.click());
