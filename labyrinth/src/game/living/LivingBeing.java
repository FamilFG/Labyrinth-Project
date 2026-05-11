package game.living;

import java.util.ArrayList;
import java.util.List;
import game.interfaces.Executable;
import game.map.Room;
import game.objects.GameObject;
import game.world.Entity;
import game.world.World;

public abstract class LivingBeing extends Entity implements Executable {
    private int healthPoints;
    private int strengthPoints;
    private Room room;
    private List<GameObject> inventory;

    public LivingBeing(String name, int healthPoints, int strengthPoints, Room room, World world) {
        super(name, world);
        this.healthPoints = healthPoints;
        this.strengthPoints = strengthPoints;
        this.room = room;
        this.inventory = new ArrayList<>();

        if (world != null) world.registerExecutable(this);
        if (room != null) room.addLivingBeing(this);
    }

    public int getHealthPoints() { return healthPoints; }
    public int getStrengthPoints() { return strengthPoints; }
    public Room getRoom() { return room; }

    public void moveTo(Room nextRoom) {
        if (room != null) room.removeLivingBeing(this);
        room = nextRoom;
        if (room != null) room.addLivingBeing(this);
    }

    public void addToInventory(GameObject object) { inventory.add(object); }

    public void removeFromInventory(GameObject object) { inventory.remove(object); }

    /**
     * Give an object from this being's inventory to another LivingBeing.
     * Returns true if transfer succeeded.
     */
    public boolean giveObjectTo(GameObject object, LivingBeing target) {
        if (!inventory.contains(object)) return false;
        inventory.remove(object);
        target.addToInventory(object);
        return true;
    }

    public List<GameObject> getInventory() { return inventory; }

    public void takeDamage(int amount) {
        healthPoints -= amount;
        if (healthPoints < 0) healthPoints = 0;
    }

    public void heal(int amount) {
        healthPoints += amount;
    }

    public boolean isAlive() { return healthPoints > 0; }

    public abstract void execute();
}