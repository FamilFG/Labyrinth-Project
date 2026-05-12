package game.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {
    public static char[][] loadMap(String filePath) {
        List<String> lines = new ArrayList<>();
        File mapFile = resolveMapFile(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(mapFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading map: " + e.getMessage());
            return null;
        }

        if (lines.isEmpty()) {
            System.out.println("Error loading map: map is empty.");
            return null;
        }

        int rows = lines.size();
        int cols = 0;
        for (String line : lines) {
            cols = Math.max(cols, line.length());
        }

        char[][] map = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String row = lines.get(i);
            for (int j = 0; j < cols; j++) {
                map[i][j] = j < row.length() ? row.charAt(j) : '#';
            }
        }
        return map;
    }

    private static File resolveMapFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) return file;

        File fromRepoRoot = new File("labyrinth", filePath);
        if (fromRepoRoot.exists()) return fromRepoRoot;

        if (filePath.startsWith("labyrinth/") || filePath.startsWith("labyrinth\\")) {
            File fromModuleRoot = new File(filePath.substring("labyrinth/".length()));
            if (fromModuleRoot.exists()) return fromModuleRoot;
        }

        return file;
    }
}
