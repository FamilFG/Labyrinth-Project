package game.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.living.Enemy;
import game.living.Player;
import game.map.Lock;
import game.map.Room;
import game.objects.*;
import game.world.World;

public class GameEngine {

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

    private String lockedDoorKeyId;

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
        Room newRoom = new Room("Room_L" + level, world);
        existingPlayer.moveTo(newRoom);
        this.player = existingPlayer;
        world.registerExecutable(player);
        initEntities();
    }

    public GameEngine nextLevel() {
        int next = currentLevel + 1;
        if (next >= MAP_FILES.length) return null;
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

    private String buildDoorKeyId(int r, int c) { return "KEY_" + r + "_" + c; }

    private void initEntities() {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j] == '=') lockedDoorKeyId = buildDoorKeyId(i, j);

        Room sharedRoom = new Room("EnemyRoom", world);
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j] == 'E') {
                    Enemy e = new Enemy("Enemy", 30, 10, sharedRoom, world, map, i, j, player);
                    enemies.add(e);
                }
    }

    // ---------------------------------------------------------------- STEP

    public void step() { world.step(); }

    // ---------------------------------------------------------------- MOVEMENT

    public void movePlayer(int dr, int dc) {
        int nr = playerRow + dr;
        int nc = playerCol + dc;

        if (nr < 0 || nr >= map.length || nc < 0 || nc >= map[0].length) return;

        char target = map[nr][nc];

        if (target == '#') return;

        if (target == 'c') {
            System.out.println("Chest nearby. Use 'chest' to open it.");
            return;
        }

        if (target == '=') {
            System.out.println("Locked door. Use 'open' to unlock it.");
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

        if (target == 'E') {
            boolean killed = attackEnemy(nr, nc);
            // Move onto the cell only if enemy was killed
            if (killed) {
                map[playerRow][playerCol] = '.';
                playerRow = nr;
                playerCol = nc;
                map[playerRow][playerCol] = 'P';
            }
            return;
        }

        // Normal move (includes '-' open door)
        map[playerRow][playerCol] = '.';
        playerRow = nr;
        playerCol = nc;
        map[playerRow][playerCol] = 'P';
    }

    // ---------------------------------------------------------------- DOOR

    public void openDoor() {
        int[] pos = findNearby('=');
        if (pos == null) { System.out.println("No locked door nearby."); return; }

        int r = pos[0], c = pos[1];
        String needed = buildDoorKeyId(r, c);

        if (hasKeyId(needed)) {
            map[r][c] = '-';
            System.out.println("Door unlocked with key!");
            return;
        }

        Crowbar crowbar = findCrowbarInInventory();
        if (crowbar != null) {
            map[r][c] = '-';
            System.out.println("Door forced open with crowbar!");
            return;
        }

        System.out.println("You need a key or crowbar to open this door.");
    }

    // ---------------------------------------------------------------- CHEST

    public void openChest() {
        int[] pos = findNearby('c');
        if (pos == null) { System.out.println("No chest nearby."); return; }

        int r = pos[0], c = pos[1];
        map[r][c] = '.';

        int loot = rand.nextInt(3);
        if (loot == 0 && lockedDoorKeyId != null) {
            Key key = new Key("Key", lockedDoorKeyId, world);
            player.addToInventory(key);
            System.out.println("Chest opened! Found a Key for the locked door.");
        } else if (loot == 1) {
            player.setArmorPoints(player.getArmorPoints() + 2);
            System.out.println("Chest opened! Armor +2. Now: " + player.getArmorPoints());
        } else {
            if (findCrowbarInInventory() == null) {
                Crowbar crowbar = new Crowbar("Crowbar", world);
                player.addToInventory(crowbar);
                System.out.println("Chest opened! Found a Crowbar (can force-open doors).");
            } else {
                player.heal(10);
                System.out.println("Chest opened! Healed 10 HP. Now: " + player.getHealthPoints());
            }
        }
    }

    // ---------------------------------------------------------------- COMBAT

    // Returns true if enemy was killed
    public boolean attackEnemy(int r, int c) {
        Enemy target = null;
        for (Enemy e : enemies) {
            if (e.getRow() == r && e.getCol() == c) { target = e; break; }
        }

        if (target != null) {
            int damage = player.attack(target);
            if (damage > 0)
                System.out.println("You hit enemy for " + damage + " damage!"
                        + " | Enemy HP: " + target.getHealthPoints());
            if (!target.isAlive()) {
                target.removeFromMap();
                enemies.remove(target);
                System.out.println("Enemy defeated!");
                return true;
            }
            return false; // enemy still alive — player stays put
        } else {
            // Safety fallback
            map[r][c] = '.';
            System.out.println("Enemy defeated!");
            return true;
        }
    }

    // ---------------------------------------------------------------- LOCK COMMAND

    public void attachLockToDoor() {
        int[] pos = findNearby('-');
        if (pos == null) { System.out.println("No open door nearby to lock."); return; }

        Key key = findKeyInInventory();
        if (key == null) { System.out.println("You need a key in inventory to create a lock."); return; }

        Lock lock = new Lock();
        lock.addAcceptedKey(key);
        map[pos[0]][pos[1]] = '=';
        System.out.println("Door locked with " + key.getName() + ".");
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
        for (GameObject obj : player.getInventory())
            if (obj instanceof Key && ((Key) obj).getKeyId().equals(keyId)) return true;
        return false;
    }

    private Key findKeyInInventory() {
        for (GameObject obj : player.getInventory())
            if (obj instanceof Key) return (Key) obj;
        return null;
    }

    private Crowbar findCrowbarInInventory() {
        for (GameObject obj : player.getInventory())
            if (obj instanceof Crowbar) return (Crowbar) obj;
        return null;
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
                    case '-': System.out.print(CYAN   + "- " + RESET); break;
                    default:  System.out.print(ch + " ");
                }
            }
            System.out.println();
        }
    }

    public void printStats() {
        System.out.println("HP: " + player.getHealthPoints()
                + " | STR: " + player.getStrengthPoints()
                + " | Armor: " + player.getArmorPoints()
                + " | Enemies: " + enemies.size());
    }

    public void printInventory() {
        List<GameObject> inv = player.getInventory();
        if (inv.isEmpty()) { System.out.println("Inventory empty."); return; }
        System.out.println("Inventory:");
        for (GameObject obj : inv) {
            if (obj instanceof Key)
                System.out.println("  - Key (id=" + ((Key) obj).getKeyId() + ")");
            else if (obj instanceof Crowbar)
                System.out.println("  - Crowbar");
            else
                System.out.println("  - " + obj.getName());
        }
    }
}