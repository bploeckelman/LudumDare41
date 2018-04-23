package lando.systems.ld41.ai.conditions;

import lando.systems.ld41.gameobjects.EnemyTank;

public class PlayerCloserThan extends Condition{

    private float distance;

    public PlayerCloserThan(EnemyTank owner, float distance) {
        super(owner);
        this.distance = distance;
    }

    @Override
    public boolean isTrue() {
        return owner.screen.playerTank.isVisible
                && owner.position.dst(owner.screen.playerTank.position) < distance
                && owner.screen.level.canSeeBetween(owner.position, owner.screen.playerTank.position);
    }
}
