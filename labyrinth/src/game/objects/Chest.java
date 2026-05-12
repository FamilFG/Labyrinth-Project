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

    @Override
    public boolean activate(GameObject object) {
        if (opened) return true;
        if (lock == null) {
            opened = true;
            return true;
        }
        if (object instanceof Key && lock.canUnlockWith((Key) object)) {
            opened = true;
            return true;
        }
        if (object instanceof Crowbar && lock.canUnlockWith((Crowbar) object)) {
            opened = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isActivated() { return opened; }
}
