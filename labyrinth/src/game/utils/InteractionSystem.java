package game.utils;

import java.util.ArrayList;
import java.util.List;

import game.interfaces.Activatable;
import game.map.Door;
import game.objects.GameObject;
import game.objects.Key;
import game.objects.Chest;

class InteractionSystem {
    private final GameEngine engine;

    InteractionSystem(GameEngine engine) {
        this.engine = engine;
    }

    void openNearby() {
        engine.lastMessage = "";
        int[] chestPos = engine.findNearby('C');
        if (chestPos != null) {
            openChest(chestPos[0], chestPos[1]);
            return;
        }

        int[] doorPos = engine.findNearby('D');
        if (doorPos != null) {
            openDoor(doorPos[0], doorPos[1]);
            return;
        }

        engine.say("No closed door or chest nearby.");
    }

    String getInventoryText() {
        List<GameObject> inventory = engine.player.getInventory();
        if (inventory.isEmpty()) return "Inventory empty.";
        return "Inventory: " + describeObjects(inventory);
    }

    private void openChest(int r, int c) {
        Chest chest = engine.chests.get(engine.positionKey(r, c));
        if (chest == null) {
            engine.map[r][c] = '.';
            engine.say("The chest was empty.");
            return;
        }

        if (!activateWithInventory(chest)) {
            engine.say("The chest is locked. You need a key or crowbar.");
            return;
        }

        List<GameObject> loot = chest.takeContents();
        for (GameObject object : loot) {
            engine.player.addToInventory(object);
        }
        engine.map[r][c] = '.';
        engine.room.removeObject(chest);
        engine.chests.remove(engine.positionKey(r, c));

        if (loot.isEmpty()) {
            engine.say("Chest opened. It was empty.");
        } else {
            engine.say("Chest opened. Found: " + describeObjects(loot));
        }
    }

    private void openDoor(int r, int c) {
        Door door = engine.doors.get(engine.positionKey(r, c));
        if (door == null) {
            engine.map[r][c] = 'O';
            engine.say("Door opened.");
            return;
        }

        if (!activateWithInventory(door)) {
            engine.say("The door is locked. Open a chest to find a key, or use a crowbar.");
            return;
        }

        engine.map[r][c] = 'O';
        engine.say("Door opened.");
    }

    private boolean activateWithInventory(Activatable activatable) {
        if (activatable.activate(null)) return true;

        for (GameObject object : engine.player.getInventory()) {
            if (activatable.activate(object)) return true;
        }
        return false;
    }

    private String describeObjects(List<GameObject> objects) {
        List<String> names = new ArrayList<>();
        for (GameObject object : objects) {
            if (object instanceof Key) {
                names.add("Key");
            } else {
                names.add(object.getName());
            }
        }
        return String.join(", ", names);
    }
}
