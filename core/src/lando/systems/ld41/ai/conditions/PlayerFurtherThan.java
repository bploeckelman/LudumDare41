package lando.systems.ld41.ai.conditions;

import lando.systems.ld41.gameobjects.GameObject;

public class PlayerFurtherThan implements Condition{

    private float distance;
    GameObject owner;

    public PlayerFurtherThan(GameObject owner, float distance) {
        this.owner = owner;
        this.distance = distance;
    }

    @Override
    public boolean isTrue() {
        return owner.position.dst(owner.screen.playerTank.position) > distance;
    }
}
