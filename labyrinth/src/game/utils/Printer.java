package game.utils;

public class Printer {
    public static void printMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            System.out.println(new String(map[i]));
        }
    }
}