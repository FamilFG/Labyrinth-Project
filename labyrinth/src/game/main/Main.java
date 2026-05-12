package game.main;

import java.util.Scanner;
import game.utils.GameEngine;
import game.utils.MapLoader;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        char[][] firstMap = MapLoader.loadMap("maps/map1.txt");
        if (firstMap == null) return;

        GameEngine engine = new GameEngine(firstMap);

        while (true) {
            engine.printMap();
            engine.printStats();

            System.out.println("[P]=You [#]=Wall [C]=Chest [D]=Door [O]=Open door [E]=Enemy [X]=Exit");
            System.out.println("w/a/s/d=move | open=open nearby door/chest | inv=inventory | q=quit");
            System.out.print(">> ");

            String cmd = sc.nextLine().trim().toLowerCase();
            if (cmd.equals("q")) break;

            switch (cmd) {
                case "w": engine.movePlayer(-1, 0); break;
                case "s": engine.movePlayer(1, 0); break;
                case "a": engine.movePlayer(0, -1); break;
                case "d": engine.movePlayer(0, 1); break;
                case "open": engine.openNearby(); break;
                case "inv": engine.printInventory(); break;
                default: System.out.println("Unknown command."); break;
            }

            if (engine.isLevelComplete()) {
                GameEngine next = engine.nextLevel();
                if (next == null) {
                    System.out.println("=== You completed all levels! You win! ===");
                    break;
                }
                engine = next;
                System.out.println("=== Level " + engine.getCurrentLevel() + " ===");
                continue;
            }

            engine.step();
            if (engine.isGameOver()) {
                System.out.println("You died. Game over.");
                break;
            }
            System.out.println();
        }

        sc.close();
    }
}
