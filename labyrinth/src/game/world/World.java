package game.world;

import java.util.ArrayList;
import java.util.List;
import game.interfaces.Executable;

public class World {
    private String name;
    private List<Entity> entities;
    private List<Executable> executables;

    public World(String name) {
        this.name = name;
        entities = new ArrayList<>();
        executables = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void registerEntity(Entity entity) {
        entities.add(entity);
    }

    public void registerExecutable(Executable executable) {
        executables.add(executable);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Executable> getExecutables() {
        return executables;
    }

    // Called each game tick — all enemies move via execute()
    public void step() {
        for (Executable e : executables) {
            e.execute();
        }
    }
}