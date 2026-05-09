package game.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public static char[][] loadMap(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading map: " + e.getMessage());
            return null;
        }

        int rows = lines.size();
        int cols = lines.get(0).length();
        char[][] map = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String row = lines.get(i);
            for (int j = 0; j < cols; j++) {
                map[i][j] = j < row.length() ? row.charAt(j) : ' ';
            }
        }
        return map;
    }
}