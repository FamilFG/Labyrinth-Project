package game.utils;

class MovementSystem {
    private final GameEngine engine;

    MovementSystem(GameEngine engine) {
        this.engine = engine;
    }

    boolean movePlayer(int dr, int dc) {
        if (engine.isGameOver()) return false;
        engine.lastMessage = "";

        int nr = engine.playerRow + dr;
        int nc = engine.playerCol + dc;

        // Out of bounds or wall — illegal move, time does not advance
        if (nr < 0 || nr >= engine.map.length || nc < 0 || nc >= engine.map[nr].length) return false;

        char target = engine.map[nr][nc];
        if (target == '#') return false;

        if (target == 'C') {
            engine.say("Chest nearby. Use 'open' or press E to open it.");
            return false;
        }

        if (target == 'D') {
            engine.say("Closed door. Use 'open' or press E to unlock it.");
            return false;
        }

        if (target == 'X') {
            placePlayer(nr, nc);
            engine.levelComplete = true;
            engine.say("Exit reached! Loading next level...");
            return true;
        }

        if (target == 'E') {
            boolean killed = engine.attackEnemy(nr, nc);
            if (killed) placePlayer(nr, nc);
            return true;
        }

        placePlayer(nr, nc);
        return true;
    }

    private void placePlayer(int row, int col) {
        engine.map[engine.playerRow][engine.playerCol] = '.';
        engine.playerRow = row;
        engine.playerCol = col;
        engine.map[engine.playerRow][engine.playerCol] = 'P';
    }
}
