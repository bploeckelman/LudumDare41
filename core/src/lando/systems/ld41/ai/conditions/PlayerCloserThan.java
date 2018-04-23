package lando.systems.ld41.ai.conditions;

import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public class PlayerCloserThan implements Condition{

    private float distance;
    private GameObject owner;

    public PlayerCloserThan(GameObject owner, float distance) {
        this.owner = owner;
        this.distance = distance;
    }

    @Override
    public boolean isTrue() {
        return owner.screen.playerTank.isVisible
                && owner.position.dst(owner.screen.playerTank.position) < distance
                && owner.screen.level.canSeeBetween(owner.position, owner.screen.playerTank.position);
    }
}
