package game.objects;

import java.util.ArrayList;
import java.util.List;
import game.interfaces.Activatable;
import game.world.World;

public class Chest extends GameObject implements Activatable {
    private List<GameObject> contents;
    private boolean opened;

    public Chest(String name, World world) {
        super(name, world);
        contents = new ArrayList<>();
        opened = false;
    }

    public void addObject(GameObject object) {
        contents.add(object);
    }

    // Returns items and clears them (player takes them)
    public List<GameObject> takeContents() {
        List<GameObject> taken = new ArrayList<>(contents);
        contents.clear();
        return taken;
    }

    public List<GameObject> getContents() {
        return contents;
    }

    // Chest has no lock — always opens
    public boolean activate(GameObject object) {
        if (opened) return true;
        opened = true;
        return true;
    }

    public boolean isActivated() {
        return opened;
    }
}