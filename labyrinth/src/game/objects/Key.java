package game.objects;

import game.world.World;

public class Key extends GameObject {
    private String keyId;

    public Key(String name, String keyId, World world) {
        super(name, world);
        this.keyId = keyId;
    }

    public String getKeyId() {
        return keyId;
    }
}