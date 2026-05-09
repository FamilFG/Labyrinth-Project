package game.world;

public abstract class Entity {
    private String name;
    private World world;

    public Entity(String name, World world) {
        this.name = name;
        this.world = world;
        if (world != null) {
            world.registerEntity(this);
        }
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }
}