package game.living;

import game.map.Room;
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

    public boolean hasObjectType(Class<?> type) {
        for (GameObject object : getInventory()) {
            if (type.isInstance(object)) return true;
        }
        return false;
    }

    public int attack(LivingBeing target) {
        int damage = Math.max(1, getStrengthPoints());
        target.takeDamage(damage);
        return damage;
    }

    @Override
    public void execute() {}
}
