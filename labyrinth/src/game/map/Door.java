package game.map;

import game.interfaces.Activatable;
import game.objects.GameObject;
import game.objects.Key;
import game.world.Entity;
import game.world.World;

public class Door extends Entity implements Activatable {
    private Room roomA;
    private Room roomB;
    private Lock lock;   // null = no lock, open freely
    private boolean opened;

    public Door(String name, Room roomA, Room roomB, World world) {
        super(name, world);
        this.roomA = roomA;
        this.roomB = roomB;
        opened = false;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public boolean hasLock() {
        return lock != null;
    }

    public Room getOtherSide(Room room) {
        if (room == roomA) return roomB;
        if (room == roomB) return roomA;
        return null;
    }

    // Returns true if door was already open OR successfully opened now
    public boolean activate(GameObject object) {
        if (opened) return true;

        // No lock — open freely
        if (lock == null) {
            opened = true;
            return true;
        }

        // Locked door: needs matching key
        if (object instanceof Key) {
            Key key = (Key) object;
            if (lock.canUnlockWith(key)) {
                opened = true;
                return true;
            }
        }

        return false;
    }

    public boolean isActivated() {
        return opened;
    }
}