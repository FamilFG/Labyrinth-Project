package game.objects;

import game.world.Entity;
import game.world.World;

public abstract class GameObject extends Entity {
    public GameObject(String name, World world) {
        super(name, world);
    }
}
