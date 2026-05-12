package game.utils;

class LevelManager {
    private static final String[] MAP_FILES = {
            "maps/map1.txt",
            "maps/map2.txt",
            "maps/map3.txt"
    };

    private final GameEngine engine;

    LevelManager(GameEngine engine) {
        this.engine = engine;
    }

    GameEngine nextLevel() {
        int next = engine.currentLevel + 1;
        if (next >= MAP_FILES.length) return null;

        char[][] nextMap = MapLoader.loadMap(MAP_FILES[next]);
        if (nextMap == null) return null;
        return new GameEngine(nextMap, next, engine.player);
    }
}
