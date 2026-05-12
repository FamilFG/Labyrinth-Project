package game.utils;

import game.living.Enemy;

class CombatSystem {
    private final GameEngine engine;

    CombatSystem(GameEngine engine) {
        this.engine = engine;
    }

    boolean attackEnemy(int r, int c) {
        Enemy target = null;
        for (Enemy enemy : engine.enemies) {
            if (enemy.getRow() == r && enemy.getCol() == c) {
                target = enemy;
                break;
            }
        }

        if (target == null) {
            engine.map[r][c] = '.';
            return true;
        }

        int damage = engine.player.attack(target);
        engine.say("You hit enemy for " + damage + " damage! | Enemy HP: " + target.getHealthPoints());

        if (!target.isAlive()) {
            target.removeFromMap();
            engine.enemies.remove(target);
            engine.say("Enemy defeated!");
            return true;
        }

        return false;
    }
}
