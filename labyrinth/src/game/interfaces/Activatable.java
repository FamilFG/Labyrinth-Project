package game.interfaces;

import game.objects.GameObject;

public interface Activatable {
    boolean activate(GameObject object);
    boolean isActivated();
}
