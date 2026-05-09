package game.map;

import java.util.HashSet;
import java.util.Set;
import game.objects.Key;

public class Lock {
    private Set<String> acceptedKeyIds;

    public Lock() {
        acceptedKeyIds = new HashSet<>();
    }

    public void addAcceptedKey(Key key) {
        acceptedKeyIds.add(key.getKeyId());
    }

    public boolean canUnlockWith(Key key) {
        return acceptedKeyIds.contains(key.getKeyId());
    }
}