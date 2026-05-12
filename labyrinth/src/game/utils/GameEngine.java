package game.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.living.Enemy;
import game.living.Player;
import game.map.Door;
import game.map.Room;
import game.objects.Chest;
import game.world.World;

public class GameEngine {
    char[][] map;
    int playerRow;
    int playerCol;

    World world;
    Room room;
    Player player;
    List<Enemy> enemies;
    Map<String, Door> doors;
    Map<String, Chest> chests;
    List<String> doorKeyIds;

    boolean levelComplete = false;
    boolean gameOver = false;
    int currentLevel;
    String lastMessage = "Find a chest, open the door, and reach the exit.";

    private final CombatSystem combatSystem;
    private final MovementSystem movementSystem;
    private final InteractionSystem interactionSystem;
    private final LevelManager levelManager;
    private final ConsoleRenderer renderer;
    private final EntityFactory entityFactory;

    public GameEngine(char[][] map) {
        this(map, 0, null);
    }

    GameEngine(char[][] map, int level, Player existingPlayer) {
        this.map = map;
        this.currentLevel = level;
        enemies = new ArrayList<>();
        doors = new HashMap<>();
        chests = new HashMap<>();
        doorKeyIds = new ArrayList<>();
        world = new World("Dungeon");
        room = new Room("Level_" + (level + 1), world);

        findPlayer();
        if (existingPlayer == null) {
            player = new Player("Hero", 100, 15, 5, room, world);
        } else {
            existingPlayer.moveTo(room);
            player = existingPlayer;
            world.registerExecutable(player);
        }

        combatSystem = new CombatSystem(this);
        movementSystem = new MovementSystem(this);
        interactionSystem = new InteractionSystem(this);
        levelManager = new LevelManager(this);
        renderer = new ConsoleRenderer(this);
        entityFactory = new EntityFactory(this);
        entityFactory.initEntities();
    }

    public GameEngine nextLevel() { return levelManager.nextLevel(); }
    public void step() { if (!isGameOver()) world.step(); }
    public boolean movePlayer(int dr, int dc) { return movementSystem.movePlayer(dr, dc); }
    public void openNearby() { interactionSystem.openNearby(); }
    public boolean attackEnemy(int r, int c) { return combatSystem.attackEnemy(r, c); }
    public void printMap() { renderer.printMap(); }
    public void printStats() { renderer.printStats(); }
    public void printInventory() { System.out.println(getInventoryText()); }

    public boolean isLevelComplete() { return levelComplete; }
    public boolean isGameOver() { return gameOver || !player.isAlive(); }
    public int getCurrentLevel() { return currentLevel + 1; }
    public int getPlayerHealth() { return player.getHealthPoints(); }
    public int getPlayerStrength() { return player.getStrengthPoints(); }
    public int getPlayerArmor() { return player.getArmorPoints(); }
    public int getEnemyCount() { return enemies.size(); }
    public String getLastMessage() { return lastMessage; }
    public String getInventoryText() { return interactionSystem.getInventoryText(); }

    public char[][] getMap() {
        char[][] copy = new char[map.length][];
        for (int i = 0; i < map.length; i++) {
            copy[i] = new char[map[i].length];
            System.arraycopy(map[i], 0, copy[i], 0, map[i].length);
        }
        return copy;
    }

    public void markGameOver() {
        gameOver = true;
    }

    public void showMessage(String message) {
        say(message);
    }

    void say(String message) {
        lastMessage = message;
        System.out.println(message);
    }

    String buildDoorKeyId(int r, int c) {
        return "LEVEL_" + (currentLevel + 1) + "_DOOR_" + r + "_" + c;
    }

    String positionKey(int r, int c) {
        return r + "," + c;
    }

    int[] findNearby(char symbol) {
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : dirs) {
            int r = playerRow + dir[0];
            int c = playerCol + dir[1];
            if (r < 0 || r >= map.length || c < 0 || c >= map[r].length) continue;
            if (map[r][c] == symbol) return new int[]{r, c};
        }
        return null;
    }

    private void findPlayer() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 'P') {
                    playerRow = i;
                    playerCol = j;
                    return;
                }
            }
        }
    }
}
