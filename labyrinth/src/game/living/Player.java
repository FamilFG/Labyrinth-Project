package game.living;

import game.map.Room;
import game.world.World;

public class Player extends LivingBeing {
    private int armorPoints;

    public Player(String name, int healthPoints, int strengthPoints, int armorPoints, Room room, World world) {
        super(name, healthPoints, strengthPoints, room, world);
        this.armorPoints = armorPoints;
    }

    public int getArmorPoints() { return armorPoints; }
    public void setArmorPoints(int armorPoints) { this.armorPoints = armorPoints; }

    // Player is controlled by input — execute() does nothing
    @Override
    public void execute() {}
}