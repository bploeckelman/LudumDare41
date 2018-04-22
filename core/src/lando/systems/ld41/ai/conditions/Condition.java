package lando.systems.ld41.ai.conditions;

import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public abstract class Condition {
    GameObject owner;

    public Condition(GameObject owner){
        this.owner = owner;
    }

    // find a way to make this a condition check

    public abstract boolean isTrue();
}
