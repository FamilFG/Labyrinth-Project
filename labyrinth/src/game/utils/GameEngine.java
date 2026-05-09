package game.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.living.Enemy;
import game.living.Player;
import game.map.Room;
import game.objects.GameObject;
import game.objects.Key;
import game.world.World;

public class GameEngine {

    // ANSI colors
    public static final String RESET  = "\u001B[0m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN   = "\u001B[36m";

    private char[][] map;
    private int playerRow;
    private int playerCol;

    private World world;
    private Player player;
    private List<Enemy> enemies;

    // Key for the locked door on this map (one key per map)
    private String lockedDoorKeyId;

    // Level transition
    private boolean levelComplete = false;
    private int currentLevel;
    private static final String[] MAP_FILES = {
            "maps/map1.txt",
            "maps/map2.txt",
            "maps/map3.txt"
    };

    private Random rand;

    // First-time init (level 1, fresh player)
    public GameEngine(char[][] map) {
        this.map = map;
        this.currentLevel = 0;
        rand = new Random();
        enemies = new ArrayList<>();
        world = new World("Dungeon");
        findPlayer();
        Room playerRoom = new Room("StartRoom", world);
        player = new Player("Hero", 100, 15, 5, playerRoom, world);
        initEntities();
    }

    // Called when transitioning to next level — reuse existing player
    private GameEngine(char[][] map, int level, Player existingPlayer) {
        this.map = map;
        this.currentLevel = level;
        rand = new Random();
        enemies = new ArrayList<>();
        world = new World("Dungeon");
        findPlayer();
        // Transfer player to new room but keep all stats/inventory
        Room newRoom = new Room("Room_L" + level, world);
        existingPlayer.moveTo(newRoom);
        this.player = existingPlayer;
        world.registerExecutable(player);
        initEntities();
    }

    // Load next level. Returns new GameEngine or null if no more levels.
    public GameEngine nextLevel() {
        int next = currentLevel + 1;
        if (next >= MAP_FILES.length) return null; // game finished
        char[][] nextMap = MapLoader.loadMap(MAP_FILES[next]);
        if (nextMap == null) return null;
        return new GameEngine(nextMap, next, player);
    }

    public boolean isLevelComplete() { return levelComplete; }
    public int getCurrentLevel()     { return currentLevel + 1; }

    // ---------------------------------------------------------------- INIT

    private void findPlayer() {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j] == 'P') { playerRow = i; playerCol = j; return; }
    }

    // Find the locked door ('=') position to know which keyId matches it
    private String buildDoorKeyId(int r, int c) {
        return "KEY_" + r + "_" + c;
    }

    private void initEntities() {
        // Find the locked door first so we know the keyId
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j] == '=') {
                    lockedDoorKeyId = buildDoorKeyId(i, j);
                }

        // Create Enemy objects for each 'E' on map
        Room sharedRoom = new Room("EnemyRoom", world);
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j] == 'E') {
                    Enemy e = new Enemy("Enemy", 30, 10, sharedRoom, world, map, i, j, player);
                    enemies.add(e);
                }
    }

    // ---------------------------------------------------------------- STEP

    // Called each turn — delegates to World which calls execute() on all enemies
    public void step() {
        world.step();
    }

    // ---------------------------------------------------------------- MOVEMENT

    public void movePlayer(int dr, int dc) {
        int nr = playerRow + dr;
        int nc = playerCol + dc;

        if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length) return;

        char target = map[nr][nc];

        if (target == '#') return;

        if (target == 'c') {
            System.out.println("Chest nearby. Use 'chest' command to open it.");
            return;
        }

        if (target == '=') {
            System.out.println("Locked door. Use 'open' command next to it.");
            return;
        }

        if (target == 'X') {
            map[playerRow][playerCol] = '.';
            playerRow = nr;
            playerCol = nc;
            map[playerRow][playerCol] = 'P';
            levelComplete = true;
            System.out.println("Exit reached! Loading next level...");
            return;
        }

        if (target == '-') {
            // Door already opened — walk through freely
        }

        if (target == 'E') {
            attackEnemy(nr, nc);
            // After killing enemy, player steps onto that cell
            map[playerRow][playerCol] = '.';
            playerRow = nr;
            playerCol = nc;
            map[playerRow][playerCol] = 'P';
            return;
        }

        // Move player
        map[playerRow][playerCol] = '.';
        playerRow = nr;
        playerCol = nc;
        map[playerRow][playerCol] = 'P';
    }

    // ---------------------------------------------------------------- DOOR

    public void openDoor() {
        int[] pos = findNearby('=');
        if (pos == null) {
            System.out.println("No locked door nearby.");
            return;
        }

        int r = pos[0], c = pos[1];
        String needed = buildDoorKeyId(r, c);

        if (hasKeyId(needed)) {
            map[r][c] = '-'; // opened door symbol
            System.out.println("Door unlocked! You can walk through.");
        } else {
            System.out.println("You need a key to open this door. (need: " + needed + ")");
        }
    }

    // ---------------------------------------------------------------- CHEST

    public void openChest() {
        int[] pos = findNearby('c');
        if (pos == null) {
            System.out.println("No chest nearby.");
            return;
        }

        int r = pos[0], c = pos[1];
        map[r][c] = '.'; // chest disappears after opening

        // Randomly give: key for the locked door, or armor upgrade
        int loot = rand.nextInt(2);
        if (loot == 0 && lockedDoorKeyId != null) {
            Key key = new Key("Key", lockedDoorKeyId, world);
            player.addToInventory(key);
            System.out.println("Chest opened! Found Key for the locked door.");
        } else {
            player.setArmorPoints(player.getArmorPoints() + 2);
            System.out.println("Chest opened! Armor +2. Now: " + player.getArmorPoints());
        }
    }

    // ---------------------------------------------------------------- COMBAT

    public void attackEnemy(int r, int c) {
        // Find enemy whose current position on the map matches r,c
        // (enemy moves each tick so stored row/col is always up to date in the object)
        Enemy target = null;
        for (Enemy e : enemies) {
            if (e.getRow() == r && e.getCol() == c) {
                target = e;
                break;
            }
        }

        if (target != null) {
            target.removeFromMap(); // set map[r][c] = '.'
            enemies.remove(target);
            System.out.println("Enemy defeated!");
        } else {
            // Safety fallback — clear the cell anyway
            map[r][c] = '.';
            System.out.println("Enemy defeated!");
        }
    }

    // ---------------------------------------------------------------- HELPERS

    private int[] findNearby(char symbol) {
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int r = playerRow + d[0];
            int c = playerCol + d[1];
            if (r < 0 || r >= map.length || c < 0 || c >= map[0].length) continue;
            if (map[r][c] == symbol) return new int[]{r, c};
        }
        return null;
    }

    private boolean hasKeyId(String keyId) {
        for (GameObject obj : player.getInventory()) {
            if (obj instanceof Key) {
                if (((Key) obj).getKeyId().equals(keyId)) return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- DISPLAY

    public void printMap() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                char ch = map[i][j];
                switch (ch) {
                    case 'P': System.out.print(GREEN  + "P " + RESET); break;
                    case '#': System.out.print(RED    + "# " + RESET); break;
                    case 'E': System.out.print(PURPLE + "E " + RESET); break;
                    case 'c': System.out.print(YELLOW + "c " + RESET); break;
                    case '=': System.out.print(BLUE   + "= " + RESET); break;
                    case '-': System.out.print(CYAN   + "- " + RESET); break; // opened door
                    default:  System.out.print(ch + " ");
                }
            }
            System.out.println();
        }
    }

    public void printStats() {
        System.out.println("HP: " + player.getHealthPoints()
                + " | Strength: " + player.getStrengthPoints()
                + " | Armor: " + player.getArmorPoints()
                + " | Enemies left: " + enemies.size());
    }

    public void printInventory() {
        List<GameObject> inv = player.getInventory();
        if (inv.isEmpty()) { System.out.println("Inventory empty."); return; }
        System.out.println("Inventory:");
        for (GameObject obj : inv) {
            if (obj instanceof Key) {
                System.out.println("  - Key (id=" + ((Key) obj).getKeyId() + ")");
            } else {
                System.out.println("  - " + obj.getName());
            }
        }
    }
}