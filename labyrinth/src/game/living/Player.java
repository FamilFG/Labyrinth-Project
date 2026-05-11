package game.living;

import java.util.List;
import game.map.Door;
import game.map.Lock;
import game.map.Room;
import game.objects.Chest;
import game.objects.GameObject;
import game.world.World;

public class Player extends LivingBeing {
    private int armorPoints;

    public Player(String name, int healthPoints, int strengthPoints,
                  int armorPoints, Room room, World world) {
        super(name, healthPoints, strengthPoints, room, world);
        this.armorPoints = armorPoints;
    }

    public int getArmorPoints() { return armorPoints; }
    public void setArmorPoints(int armorPoints) { this.armorPoints = armorPoints; }

    public boolean placeObjectInRoom(GameObject object) {
        if (!getInventory().contains(object)) return false;
        removeFromInventory(object);
        getRoom().addObject(object);
        return true;
    }

    public boolean pickUpObject(GameObject object) {
        if (!getRoom().getObjects().contains(object)) return false;
        getRoom().removeObject(object);
        addToInventory(object);
        return true;
    }

    public boolean placeObjectInChest(Chest chest, GameObject object) {
        if (!getRoom().getObjects().contains(chest)) return false;
        if (!getInventory().contains(object)) return false;
        removeFromInventory(object);
        chest.addObject(object);
        return true;
    }

    public boolean takeObjectFromChest(Chest chest, GameObject object) {
        if (!getRoom().getObjects().contains(chest)) return false;
        if (!chest.isActivated()) return false;
        if (!chest.getContents().contains(object)) return false;
        chest.getContents().remove(object);
        addToInventory(object);
        return true;
    }

    public boolean attachLockToDoor(Door door, Lock lock) {
        if (!getRoom().getDoors().contains(door)) return false;
        door.setLock(lock);
        return true;
    }

    public boolean attachLockToChest(Chest chest, Lock lock) {
        if (!getRoom().getObjects().contains(chest)) return false;
        chest.setLock(lock);
        return true;
    }

    /**
     * Attack a living being. Room check removed — combat is map-based, not room-based.
     * Returns damage dealt.
     */
    public int attack(LivingBeing target) {
        int damage = Math.max(1, getStrengthPoints());
        target.takeDamage(damage);
        return damage;
    }

    @Override
    public void execute() {}
}