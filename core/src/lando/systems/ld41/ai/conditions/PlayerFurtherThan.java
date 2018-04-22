package lando.systems.ld41.ai.conditions;

import lando.systems.ld41.gameobjects.EnemyTank;

public class PlayerFurtherThan extends Condition{

    private float distance;

    public PlayerFurtherThan(EnemyTank owner, float distance) {
        super(owner);
        this.distance = distance;
    }

    @Override
    public boolean isTrue() {
        return owner.position.dst(owner.screen.playerTank.position) > distance;
    }
}
