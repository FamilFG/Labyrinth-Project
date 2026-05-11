package game.living;

import game.map.Room;
import game.world.World;

public class Enemy extends LivingBeing {

    private int direction; // +1 = right, -1 = left
    private char[][] map;
    private int row;
    private int col;
    private boolean dead = false;
    private Player player;

    public Enemy(String name, int healthPoints, int strengthPoints,
                 Room room, World world,
                 char[][] map, int row, int col, Player player) {
        super(name, healthPoints, strengthPoints, room, world);
        this.map = map;
        this.row = row;
        this.col = col;
        this.player = player;
        this.direction = 1;
    }

    @Override
    public void execute() {
        if (dead) return;

        int newCol = col + direction;

        if (isBlocked(newCol)) {
            direction = -direction;
            newCol = col + direction;
            if (isBlocked(newCol)) return;
        }

        if (map[row][newCol] == 'P') {
            attackPlayer();
            return;
        }

        map[row][col] = '.';
        col = newCol;
        map[row][col] = 'E';
    }

    private boolean isBlocked(int newCol) {
        if (newCol < 0 || newCol >= map[row].length) return true;
        char c = map[row][newCol];
        // Added 'P' — enemy is blocked by player (attacks instead of moving through)
        return c == '#' || c == '=' || c == '-' || c == 'c' || c == 'E' || c == 'P';
    }

    private void attackPlayer() {
        int damage = Math.max(1, getStrengthPoints() - player.getArmorPoints());
        player.takeDamage(damage);
        System.out.println("Enemy attacks you! Damage: " + damage
                + " | Your HP: " + player.getHealthPoints());

        if (!player.isAlive()) {
            System.out.println("You died. Game over.");
            System.exit(0);
        }
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public void removeFromMap() {
        dead = true;
        map[row][col] = '.';
    }
}