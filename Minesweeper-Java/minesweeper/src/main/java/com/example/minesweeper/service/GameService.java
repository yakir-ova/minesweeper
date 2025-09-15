package com.example.minesweeper.service;

import com.example.minesweeper.model.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final SecureRandom rng = new SecureRandom();

    public GameState create(NewGameRequest req) {
        int rows = req.rows != null ? req.rows : 10;
        int cols = req.cols != null ? req.cols : 10;
        int mines = req.mines != null ? req.mines : Math.max(1, (rows * cols) / 6);
        boolean firstClickSafe = req.firstClickSafe == null || req.firstClickSafe;
        boolean revealMines = req.revealMinesOnGameOver == null || req.revealMinesOnGameOver;

        validateParams(rows, cols, mines);

        String id = UUID.randomUUID().toString();
        Game game = new Game(rows, cols, mines, firstClickSafe, revealMines);
        games.put(id, game);
        return toState(id, game);
    }

    public GameState get(String id) {
        Game game = require(id);
        return toState(id, game);
    }

    public GameState reveal(String id, RevealRequest body) {
        Game game = require(id);
        if (game.status != Game.Status.PLAYING)
            return toState(id, game);

        int r = body.row, c = body.col;
        requireInBounds(game, r, c);

        if (!game.initialized) {
            placeMines(game, r, c);
            computeAdjacents(game);
            game.initialized = true;
        }

        Cell cell = game.grid[r][c];
        if (cell.revealed || cell.flagged)
            return toState(id, game);

        game.moves++;
        revealFlood(game, r, c);

        updateWinLoss(game, cell);
        tickElapsed(game);
        return toState(id, game);
    }

    public GameState flag(String id, FlagRequest body) {
        Game game = require(id);
        if (game.status != Game.Status.PLAYING)
            return toState(id, game);

        int r = body.row, c = body.col;
        requireInBounds(game, r, c);

        Cell cell = game.grid[r][c];
        if (cell.revealed)
            return toState(id, game);

        boolean explicit = body.flagged != null;
        boolean next = explicit ? body.flagged : !cell.flagged;

        if (cell.flagged != next) {
            cell.flagged = next;
            game.flagsPlaced += next ? 1 : -1;
            game.moves++;
            tickElapsed(game);
        }
        return toState(id, game);
    }

    public GameState chord(String id, ChordRequest body) {
        Game game = require(id);
        if (game.status != Game.Status.PLAYING)
            return toState(id, game);

        int r = body.row, c = body.col;
        requireInBounds(game, r, c);

        Cell cell = game.grid[r][c];
        if (!cell.revealed || cell.adjacent <= 0)
            return toState(id, game);

        int flags = countFlagsAround(game, r, c);
        if (flags != cell.adjacent)
            return toState(id, game);

        forEachNeighbor(game, r, c, (nr, nc) -> {
            Cell n = game.grid[nr][nc];
            if (!n.revealed && !n.flagged) {
                revealFlood(game, nr, nc);
            }
        });

        game.moves++;
        updateWinLoss(game, cell);
        tickElapsed(game);
        return toState(id, game);
    }

    public GameState reset(String id, ResetRequest req) {
        Game old = require(id);

        Game fresh = new Game(old.rows, old.cols, old.mines, old.firstClickSafe, old.revealMinesOnGameOver);

        games.put(id, fresh);
        return toState(id, fresh);
    }

    private void validateParams(int rows, int cols, int mines) {
        if (rows < 2 || cols < 2)
            throw new IllegalArgumentException("rows and cols must be at least 2");
        if (mines < 1 || mines >= rows * cols)
            throw new IllegalArgumentException("invalid mines count");
    }

    private Game require(String id) {
        Game game = games.get(id);
        if (game == null)
            throw new NoSuchElementException("game not found");
        return game;
    }

    private void requireInBounds(Game game, int r, int c) {
        if (r < 0 || r >= game.rows || c < 0 || c >= game.cols)
            throw new IndexOutOfBoundsException("row or col out of bounds");
    }

    private void placeMines(Game game, int safeR, int safeC) {
        int placed = 0;
        while (placed < game.mines) {
            int r = rng.nextInt(game.rows);
            int c = rng.nextInt(game.cols);

            if (r == safeR && c == safeC)
                continue;

            Cell cell = game.grid[r][c];
            if (!cell.mine) {
                cell.mine = true;
                placed++;
            }
        }
    }

    private void computeAdjacents(Game game) {
        for (int r = 0; r < game.rows; r++) {
            for (int c = 0; c < game.cols; c++) {
                Cell cell = game.grid[r][c];
                if (cell.mine) {
                    cell.adjacent = -1;
                    continue;
                }
                final int[] count = { 0 };
                forEachNeighbor(game, r, c, (nr, nc) -> {
                    if (game.grid[nr][nc].mine) {
                        count[0]++;
                    }
                });
                cell.adjacent = count[0];
            }
        }
    }

    private interface Neighbor {
        void apply(int r, int c);
    }

    private void forEachNeighbor(Game game, int r, int c, Neighbor fn) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0)
                    continue;
                int nr = r + dr, nc = c + dc;
                if (nr >= 0 && nr < game.rows && nc >= 0 && nc < game.cols)
                    fn.apply(nr, nc);
            }
        }
    }

    private void revealFlood(Game game, int r, int c) {
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[] { r, c });

        while (!stack.isEmpty()) {
            int[] cur = stack.pop();
            int rr = cur[0], cc = cur[1];
            Cell cell = game.grid[rr][cc];

            if (cell.revealed || cell.flagged)
                continue;
            cell.revealed = true;

            if (cell.mine) {
                game.status = Game.Status.LOST;
                continue;
            }

            game.safeCellsRemaining--;
            if (cell.adjacent == 0) {
                forEachNeighbor(game, rr, cc, (nr, nc) -> {
                    Cell n = game.grid[nr][nc];
                    if (!n.revealed && !n.flagged)
                        stack.push(new int[] { nr, nc });
                });
            }
        }
    }

    private void updateWinLoss(Game game, Cell lastRevealed) {
        if (game.status == Game.Status.LOST)
            return;
        if (game.safeCellsRemaining == 0) {
            game.status = Game.Status.WON;
        }
    }

    private int countFlagsAround(Game game, int r, int c) {
        final int[] count = { 0 };
        forEachNeighbor(game, r, c, (nr, nc) -> count[0] += game.grid[nr][nc].flagged ? 1 : 0);
        return count[0];
    }

    private void tickElapsed(Game game) {
        game.elapsedMs = Duration.between(game.startedAt, java.time.Instant.now()).toMillis();
    }

    public GameState toState(String id, Game game) {
        CellDTO[][] cells = new CellDTO[game.rows][game.cols];
        boolean showMines = game.status != Game.Status.PLAYING && game.revealMinesOnGameOver;

        for (int r = 0; r < game.rows; r++) {
            for (int c = 0; c < game.cols; c++) {
                Cell cell = game.grid[r][c];
                boolean mineOut = showMines && cell.mine;
                int adjOut = cell.revealed && !cell.mine ? cell.adjacent : 0;
                cells[r][c] = new CellDTO(cell.revealed, cell.flagged, adjOut, mineOut);
            }
        }

        StatsDTO stats = new StatsDTO(
                game.flagsPlaced,
                game.safeCellsRemaining,
                game.moves,
                game.startedAt.toString(),
                game.elapsedMs);

        String statusStr = switch (game.status) {
            case PLAYING -> "playing";
            case WON -> "won";
            case LOST -> "lost";
        };

        return new GameState(id, game.rows, game.cols, game.mines, statusStr, stats, cells);
    }
}