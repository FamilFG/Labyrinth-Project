package game.objects;

import java.util.ArrayList;
import java.util.List;
import game.interfaces.Activatable;
import game.map.Lock;
import game.world.World;

public class Chest extends GameObject implements Activatable {
    private List<GameObject> contents;
    private Lock lock;
    private boolean opened;

    public Chest(String name, World world) {
        super(name, world);
        contents = new ArrayList<>();
        opened = false;
    }

    public void setLock(Lock lock) { this.lock = lock; }
    public boolean hasLock() { return lock != null; }

    public void addObject(GameObject object) { contents.add(object); }

    public List<GameObject> takeContents() {
        List<GameObject> taken = new ArrayList<>(contents);
        contents.clear();
        return taken;
    }

    public List<GameObject> getContents() { return contents; }

    @Override
    public boolean activate(GameObject object) {
        if (opened) return true;

        // No lock — opens freely
        if (lock == null) {
            opened = true;
            return true;
        }

        // Try key
        if (object instanceof Key) {
            if (lock.canUnlockWith((Key) object)) {
                opened = true;
                return true;
            }
        }

        // Try crowbar
        if (object instanceof Crowbar) {
            if (lock.canUnlockWith((Crowbar) object)) {
                opened = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isActivated() { return opened; }
}