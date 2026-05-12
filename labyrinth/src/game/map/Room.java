package game.map;

import java.util.ArrayList;
import java.util.List;
import game.living.LivingBeing;
import game.objects.GameObject;
import game.world.Entity;
import game.world.World;

public class Room extends Entity {
    private List<Door> doors;
    private List<LivingBeing> livingBeings;
    private List<GameObject> objects;

    public Room(String name, World world) {
        super(name, world);
        doors = new ArrayList<>();
        livingBeings = new ArrayList<>();
        objects = new ArrayList<>();
    }

    public List<Door> getDoors() { return doors; }
    public List<LivingBeing> getLivingBeings() { return livingBeings; }
    public List<GameObject> getObjects() { return objects; }

    public void addDoor(Door door) { doors.add(door); }
    public void addLivingBeing(LivingBeing lb) { livingBeings.add(lb); }
    public void removeLivingBeing(LivingBeing lb) { livingBeings.remove(lb); }
    public void addObject(GameObject obj) { objects.add(obj); }
    public void removeObject(GameObject obj) { objects.remove(obj); }
}
