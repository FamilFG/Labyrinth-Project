package game.living;

import java.util.ArrayDeque;
import java.util.Queue;

import game.map.Room;
import game.utils.GameEngine;
import game.world.World;

public class Enemy extends LivingBeing {
    private static final int CHASE_DISTANCE = 7;
    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private int direction;
    private char[][] map;
    private int row;
    private int col;
    private boolean dead = false;
    private Player player;
    private GameEngine engine;

    public Enemy(String name, int healthPoints, int strengthPoints,
                 Room room, World world,
                 char[][] map, int row, int col, Player player, GameEngine engine) {
        super(name, healthPoints, strengthPoints, room, world);
        this.map = map;
        this.row = row;
        this.col = col;
        this.player = player;
        this.engine = engine;
        this.direction = 1;
    }

    @Override
    public void execute() {
        if (dead) return;

        int[] playerPos = findPlayer();
        if (playerPos == null) return;

        if (isAdjacent(playerPos[0], playerPos[1])) {
            attackPlayer();
            return;
        }

        int distance = Math.abs(playerPos[0] - row) + Math.abs(playerPos[1] - col);
        if (distance <= CHASE_DISTANCE && moveTowardPlayer(playerPos[0], playerPos[1])) {
            return;
        }

        patrol();
    }

    private boolean moveTowardPlayer(int playerRow, int playerCol) {
        int[] nextStep = findNextStep(playerRow, playerCol);
        if (nextStep == null) return false;

        moveTo(nextStep[0], nextStep[1]);
        return true;
    }

    private int[] findNextStep(int targetRow, int targetCol) {
        boolean[][] visited = new boolean[map.length][];
        int[][] previousRow = new int[map.length][];
        int[][] previousCol = new int[map.length][];

        for (int i = 0; i < map.length; i++) {
            visited[i] = new boolean[map[i].length];
            previousRow[i] = new int[map[i].length];
            previousCol[i] = new int[map[i].length];
            for (int j = 0; j < map[i].length; j++) {
                previousRow[i][j] = -1;
                previousCol[i][j] = -1;
            }
        }

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{row, col});
        visited[row][col] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.remove();
            if (current[0] == targetRow && current[1] == targetCol) {
                return backtrackFirstStep(targetRow, targetCol, previousRow, previousCol);
            }

            for (int[] dir : DIRECTIONS) {
                int nextRow = current[0] + dir[0];
                int nextCol = current[1] + dir[1];
                if (!isInside(nextRow, nextCol) || visited[nextRow][nextCol]) continue;
                if (!canPathThrough(nextRow, nextCol, targetRow, targetCol)) continue;

                visited[nextRow][nextCol] = true;
                previousRow[nextRow][nextCol] = current[0];
                previousCol[nextRow][nextCol] = current[1];
                queue.add(new int[]{nextRow, nextCol});
            }
        }

        return null;
    }

    private int[] backtrackFirstStep(int targetRow, int targetCol, int[][] previousRow, int[][] previousCol) {
        int currentRow = targetRow;
        int currentCol = targetCol;

        while (previousRow[currentRow][currentCol] != row || previousCol[currentRow][currentCol] != col) {
            int nextRow = previousRow[currentRow][currentCol];
            int nextCol = previousCol[currentRow][currentCol];
            if (nextRow < 0 || nextCol < 0) return null;
            currentRow = nextRow;
            currentCol = nextCol;
        }

        if (map[currentRow][currentCol] == 'P') {
            attackPlayer();
            return null;
        }

        return new int[]{currentRow, currentCol};
    }

    private void patrol() {
        int newCol = col + direction;

        if (isPlayerAt(row, newCol)) {
            attackPlayer();
            return;
        }

        if (isBlocked(row, newCol)) {
            direction = -direction;
            newCol = col + direction;
            if (isPlayerAt(row, newCol)) {
                attackPlayer();
                return;
            }
            if (isBlocked(row, newCol)) return;
        }

        moveTo(row, newCol);
    }

    private void moveTo(int nextRow, int nextCol) {
        map[row][col] = '.';
        row = nextRow;
        col = nextCol;
        map[row][col] = 'E';
    }

    private int[] findPlayer() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 'P') return new int[]{i, j};
            }
        }
        return null;
    }

    private boolean isAdjacent(int targetRow, int targetCol) {
        return Math.abs(targetRow - row) + Math.abs(targetCol - col) == 1;
    }

    private boolean canPathThrough(int r, int c, int targetRow, int targetCol) {
        if (r == targetRow && c == targetCol) return true;
        return !isBlocked(r, c);
    }

    private boolean isPlayerAt(int r, int c) {
        return isInside(r, c) && map[r][c] == 'P';
    }

    private boolean isInside(int r, int c) {
        return r >= 0 && r < map.length && c >= 0 && c < map[r].length;
    }

    private boolean isBlocked(int r, int c) {
        if (!isInside(r, c)) return true;
        char tile = map[r][c];
        return tile == '#' || tile == 'E' || tile == 'P' || tile == 'X'
                || tile == 'C' || tile == 'D' || tile == 'O';
    }

    private void attackPlayer() {
        int damage = Math.max(1, getStrengthPoints() - player.getArmorPoints());
        player.takeDamage(damage);
        engine.showMessage("Enemy attacks you! Damage: " + damage
                + " | Your HP: " + player.getHealthPoints());

        if (!player.isAlive()) {
            engine.markGameOver();
        }
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public void removeFromMap() {
        dead = true;
        map[row][col] = '.';
    }
}
