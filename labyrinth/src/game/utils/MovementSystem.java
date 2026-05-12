package game.utils;

class MovementSystem {
    private final GameEngine engine;

    MovementSystem(GameEngine engine) {
        this.engine = engine;
    }

    void movePlayer(int dr, int dc) {
        if (engine.isGameOver()) return;
        engine.lastMessage = "";

        int nr = engine.playerRow + dr;
        int nc = engine.playerCol + dc;

        if (nr < 0 || nr >= engine.map.length || nc < 0 || nc >= engine.map[nr].length) return;

        char target = engine.map[nr][nc];
        if (target == '#') return;

        if (target == 'C') {
            engine.say("Chest nearby. Use 'open' or press E to open it.");
            return;
        }

        if (target == 'D') {
            engine.say("Closed door. Use 'open' or press E to unlock it.");
            return;
        }

        if (target == 'X') {
            placePlayer(nr, nc);
            engine.levelComplete = true;
            engine.say("Exit reached! Loading next level...");
            return;
        }

        if (target == 'E') {
            boolean killed = engine.attackEnemy(nr, nc);
            if (killed) placePlayer(nr, nc);
            return;
        }

        placePlayer(nr, nc);
    }

    private void placePlayer(int row, int col) {
        engine.map[engine.playerRow][engine.playerCol] = '.';
        engine.playerRow = row;
        engine.playerCol = col;
        engine.map[engine.playerRow][engine.playerCol] = 'P';
    }
}
