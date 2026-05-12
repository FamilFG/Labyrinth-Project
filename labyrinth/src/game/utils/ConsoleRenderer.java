package game.utils;

class ConsoleRenderer {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private final GameEngine engine;

    ConsoleRenderer(GameEngine engine) {
        this.engine = engine;
    }

    void printMap() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int i = 0; i < engine.map.length; i++) {
            for (int j = 0; j < engine.map[i].length; j++) {
                char ch = engine.map[i][j];
                switch (ch) {
                    case 'P': System.out.print(GREEN + "P " + RESET); break;
                    case '#': System.out.print(RED + "# " + RESET); break;
                    case 'E': System.out.print(PURPLE + "E " + RESET); break;
                    case 'C': System.out.print(YELLOW + "C " + RESET); break;
                    case 'D': System.out.print(BLUE + "D " + RESET); break;
                    case 'O': System.out.print(CYAN + "O " + RESET); break;
                    case 'X': System.out.print(CYAN + "X " + RESET); break;
                    default: System.out.print(ch + " ");
                }
            }
            System.out.println();
        }
    }

    void printStats() {
        System.out.println("HP: " + engine.player.getHealthPoints()
                + " | STR: " + engine.player.getStrengthPoints()
                + " | Armor: " + engine.player.getArmorPoints()
                + " | Enemies: " + engine.enemies.size());
    }
}
