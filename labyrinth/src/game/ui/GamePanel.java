package game.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import game.utils.GameEngine;
import game.utils.MapLoader;

public class GamePanel extends JPanel {
    private static final int TILE_SIZE = 34;
    private static final int SIDE_WIDTH = 260;
    private static final int PADDING = 18;

    private static final Color FLOOR = new Color(232, 226, 214);
    private static final Color WALL = new Color(48, 52, 59);
    private static final Color PLAYER = new Color(46, 150, 84);
    private static final Color ENEMY = new Color(142, 64, 146);
    private static final Color CHEST = new Color(205, 151, 45);
    private static final Color DOOR = new Color(60, 110, 180);
    private static final Color OPEN_DOOR = new Color(64, 155, 162);
    private static final Color EXIT = new Color(28, 150, 190);
    private static final Color TEXT = new Color(37, 40, 45);

    private GameEngine engine;
    private boolean won;

    public GamePanel() {
        char[][] firstMap = MapLoader.loadMap("maps/map1.txt");
        if (firstMap == null) {
            throw new IllegalStateException("Could not load maps/map1.txt");
        }

        engine = new GameEngine(firstMap);
        setBackground(new Color(245, 243, 238));
        setFocusable(true);
        installKeyBindings();
        updatePreferredSize();
    }

    private void installKeyBindings() {
        bind("W", "up", new Runnable() { public void run() { movePlayer(-1, 0); } });
        bind("S", "down", new Runnable() { public void run() { movePlayer(1, 0); } });
        bind("A", "left", new Runnable() { public void run() { movePlayer(0, -1); } });
        bind("D", "right", new Runnable() { public void run() { movePlayer(0, 1); } });
        bind("UP", "upArrow", new Runnable() { public void run() { movePlayer(-1, 0); } });
        bind("DOWN", "downArrow", new Runnable() { public void run() { movePlayer(1, 0); } });
        bind("LEFT", "leftArrow", new Runnable() { public void run() { movePlayer(0, -1); } });
        bind("RIGHT", "rightArrow", new Runnable() { public void run() { movePlayer(0, 1); } });
        bind("E", "open", new Runnable() { public void run() { openNearby(); } });
        bind("I", "inventory", new Runnable() { public void run() { showInventory(); } });
    }

    private void bind(String key, String name, final Runnable action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(key), name);
        actionMap.put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    private void movePlayer(int dr, int dc) {
        if (won || engine.isGameOver()) return;
        boolean moved = engine.movePlayer(dr, dc);
        if (moved) finishTurn();
        else repaint(); // still refresh to show any message (e.g. "wall!")
    }

    private void openNearby() {
        if (won || engine.isGameOver()) return;
        engine.openNearby();
        finishTurn();
    }

    private void showInventory() {
        JOptionPane.showMessageDialog(this, engine.getInventoryText(), "Inventory", JOptionPane.INFORMATION_MESSAGE);
        requestFocusInWindow();
    }

    private void finishTurn() {
        if (engine.isLevelComplete()) {
            GameEngine next = engine.nextLevel();
            if (next == null) {
                won = true;
                engine.showMessage("You completed all levels. You win!");
            } else {
                engine = next;
                engine.showMessage("Level " + engine.getCurrentLevel());
                updatePreferredSize();
            }
        } else {
            engine.step();
            if (engine.isGameOver()) {
                engine.showMessage("You died. Game over.");
            }
        }
        repaint();
    }

    private void updatePreferredSize() {
        char[][] map = engine.getMap();
        int rows = map.length;
        int cols = 0;
        for (char[] row : map) {
            cols = Math.max(cols, row.length);
        }
        setPreferredSize(new Dimension(cols * TILE_SIZE + SIDE_WIDTH + PADDING * 3,
                Math.max(rows * TILE_SIZE + PADDING * 2, 420)));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        char[][] map = engine.getMap();
        drawMap(g2, map);
        drawSidebar(g2, map);

        g2.dispose();
    }

    private void drawMap(Graphics2D g2, char[][] map) {
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                int x = PADDING + c * TILE_SIZE;
                int y = PADDING + r * TILE_SIZE;
                drawTile(g2, map[r][c], x, y);
            }
        }
    }

    private void drawTile(Graphics2D g2, char tile, int x, int y) {
        g2.setColor(tile == '#' ? WALL : FLOOR);
        g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g2.setColor(new Color(190, 185, 174));
        g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);

        int inset = 7;
        switch (tile) {
            case 'P':
                g2.setColor(PLAYER);
                g2.fillOval(x + inset, y + inset, TILE_SIZE - inset * 2, TILE_SIZE - inset * 2);
                break;
            case 'E':
                g2.setColor(ENEMY);
                g2.fillOval(x + inset, y + inset, TILE_SIZE - inset * 2, TILE_SIZE - inset * 2);
                drawCentered(g2, "!", x, y + 1, TILE_SIZE, Color.WHITE, 18, Font.BOLD);
                break;
            case 'C':
                g2.setColor(CHEST);
                g2.fillRoundRect(x + 6, y + 9, TILE_SIZE - 12, TILE_SIZE - 15, 6, 6);
                g2.setColor(new Color(120, 82, 25));
                g2.drawLine(x + 8, y + 18, x + TILE_SIZE - 8, y + 18);
                break;
            case 'D':
                g2.setColor(DOOR);
                g2.fillRect(x + 8, y + 5, TILE_SIZE - 16, TILE_SIZE - 10);
                g2.setColor(Color.WHITE);
                g2.fillOval(x + TILE_SIZE - 13, y + TILE_SIZE / 2, 4, 4);
                break;
            case 'O':
                g2.setColor(OPEN_DOOR);
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(x + 9, y + 6, x + TILE_SIZE - 8, y + TILE_SIZE - 7);
                g2.setStroke(new BasicStroke(1));
                break;
            case 'X':
                g2.setColor(EXIT);
                g2.fillRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 8, 8);
                drawCentered(g2, "X", x, y + 1, TILE_SIZE, Color.WHITE, 16, Font.BOLD);
                break;
            default:
                break;
        }
    }

    private void drawSidebar(Graphics2D g2, char[][] map) {
        int mapWidth = 0;
        for (char[] row : map) {
            mapWidth = Math.max(mapWidth, row.length);
        }

        int x = PADDING * 2 + mapWidth * TILE_SIZE;
        int y = PADDING;
        int width = SIDE_WIDTH - PADDING;

        g2.setColor(TEXT);
        g2.setFont(new Font("SansSerif", Font.BOLD, 24));
        g2.drawString("Labyrinth", x, y + 26);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        int line = y + 62;
        g2.drawString("Level: " + engine.getCurrentLevel(), x, line);
        line += 24;
        g2.drawString("HP: " + engine.getPlayerHealth(), x, line);
        line += 24;
        g2.drawString("STR: " + engine.getPlayerStrength(), x, line);
        line += 24;
        g2.drawString("Armor: " + engine.getPlayerArmor(), x, line);
        line += 24;
        g2.drawString("Enemies: " + engine.getEnemyCount(), x, line);

        line += 42;
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.drawString("Controls", x, line);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        line += 24;
        g2.drawString("WASD / arrows: move", x, line);
        line += 22;
        g2.drawString("E: open chest or door", x, line);
        line += 22;
        g2.drawString("I: inventory", x, line);

        line += 42;
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));
        g2.drawString("Message", x, line);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        drawWrapped(g2, engine.getLastMessage(), x, line + 24, width, 20);
    }

    private void drawCentered(Graphics2D g2, String text, int x, int y, int size, Color color, int fontSize, int style) {
        g2.setColor(color);
        g2.setFont(new Font("SansSerif", style, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (size - fm.stringWidth(text)) / 2;
        int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);
    }

    private void drawWrapped(Graphics2D g2, String text, int x, int y, int width, int lineHeight) {
        if (text == null || text.isEmpty()) return;

        String[] words = text.split(" ");
        String line = "";
        for (String word : words) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (g2.getFontMetrics().stringWidth(candidate) > width && !line.isEmpty()) {
                g2.drawString(line, x, y);
                y += lineHeight;
                line = word;
            } else {
                line = candidate;
            }
        }
        if (!line.isEmpty()) {
            g2.drawString(line, x, y);
        }
    }
}
