package game.utils;

import game.living.Enemy;
import game.map.Door;
import game.map.Lock;
import game.objects.Chest;
import game.objects.Crowbar;
import game.objects.Key;

class EntityFactory {
    private final GameEngine engine;

    EntityFactory(GameEngine engine) {
        this.engine = engine;
    }

    void initEntities() {
        initDoors();
        initChests();
        initEnemies();
    }

    private void initDoors() {
        for (int i = 0; i < engine.map.length; i++) {
            for (int j = 0; j < engine.map[i].length; j++) {
                if (engine.map[i][j] == 'D') {
                    String keyId = engine.buildDoorKeyId(i, j);
                    Lock lock = new Lock();
                    lock.addAcceptedKeyId(keyId);

                    Door door = new Door("Door", engine.room, engine.room, engine.world);
                    door.setLock(lock);
                    engine.room.addDoor(door);

                    engine.doors.put(engine.positionKey(i, j), door);
                    engine.doorKeyIds.add(keyId);
                }
            }
        }
    }

    private void initChests() {
        int chestIndex = 0;
        for (int i = 0; i < engine.map.length; i++) {
            for (int j = 0; j < engine.map[i].length; j++) {
                if (engine.map[i][j] == 'C') {
                    Chest chest = new Chest("Chest", engine.world);
                    if (chestIndex == 0) {
                        for (String keyId : engine.doorKeyIds) {
                            chest.addObject(new Key("Key", keyId, engine.world));
                        }
                    } else {
                        chest.addObject(new Crowbar("Crowbar", engine.world));
                    }
                    engine.room.addObject(chest);
                    engine.chests.put(engine.positionKey(i, j), chest);
                    chestIndex++;
                }
            }
        }
    }

    private void initEnemies() {
        for (int i = 0; i < engine.map.length; i++) {
            for (int j = 0; j < engine.map[i].length; j++) {
                if (engine.map[i][j] == 'E') {
                    Enemy enemy = new Enemy("Enemy", 30, 10, engine.room, engine.world,
                            engine.map, i, j, engine.player, engine);
                    engine.enemies.add(enemy);
                }
            }
        }
    }
}
