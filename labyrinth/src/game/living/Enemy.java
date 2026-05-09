package game.living;

import game.map.Room;
import game.world.World;

public class Enemy extends LivingBeing {

    // direction: +1 = right (col+1), -1 = left (col-1)
    private int direction;

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
        this.direction = 1; // start moving right
    }

    @Override
    public void execute() {
        if (dead) return;

        int newCol = col + direction;

        // Check bounds and walls
        if (newCol < 0 || newCol >= map[row].length
                || map[row][newCol] == '#'
                || map[row][newCol] == '='
                || map[row][newCol] == '-'
                || map[row][newCol] == 'c'
                || map[row][newCol] == 'E') {
            // Bounce
            direction = -direction;
            newCol = col + direction;

            // Still blocked after bounce — stay
            if (newCol < 0 || newCol >= map[row].length
                    || map[row][newCol] == '#'
                    || map[row][newCol] == '='
                    || map[row][newCol] == '-'
                    || map[row][newCol] == 'c'
                    || map[row][newCol] == 'E') {
                return;
            }
        }

        // Stepped onto player
        if (map[row][newCol] == 'P') {
            attackPlayer();
            return;
        }

        // Move
        map[row][col] = '.';
        col = newCol;
        map[row][col] = 'E';
    }

    private void attackPlayer() {
        int damage = Math.max(1, 10 - player.getArmorPoints());
        player.takeDamage(damage);
        System.out.println("Enemy attacks you! Damage: " + damage + " | Your HP: " + player.getHealthPoints());

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