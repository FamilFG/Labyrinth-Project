package game.map;

import game.interfaces.Activatable;
import game.objects.Crowbar;
import game.objects.GameObject;
import game.objects.Key;
import game.world.Entity;
import game.world.World;

public class Door extends Entity implements Activatable {
    private Room roomA;
    private Room roomB;
    private Lock lock;
    private boolean opened;

    public Door(String name, Room roomA, Room roomB, World world) {
        super(name, world);
        this.roomA = roomA;
        this.roomB = roomB;
        this.opened = false;
    }

    public void setLock(Lock lock) { this.lock = lock; }
    public boolean hasLock() { return lock != null; }

    public Room getOtherSide(Room room) {
        if (room == roomA) return roomB;
        if (room == roomB) return roomA;
        return null;
    }

    @Override
    public boolean activate(GameObject object) {
        if (opened) return true;
        if (lock == null) { opened = true; return true; }

        if (object instanceof Key) {
            if (lock.canUnlockWith((Key) object)) {
                opened = true;
                return true;
            }
        }
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