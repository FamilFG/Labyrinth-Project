package game.map;

import java.util.HashSet;
import java.util.Set;
import game.objects.Crowbar;
import game.objects.Key;

public class Lock {
    private Set<String> acceptedKeyIds;
    private boolean crowbarAllowed;

    public Lock() {
        acceptedKeyIds = new HashSet<>();
        crowbarAllowed = false;
    }

    public void addAcceptedKey(Key key) {
        acceptedKeyIds.add(key.getKeyId());
    }

    public boolean canUnlockWith(Key key) {
        return acceptedKeyIds.contains(key.getKeyId());
    }

    // Crowbar can force-open any lock
    public boolean canUnlockWith(Crowbar crowbar) {
        return true;
    }
}