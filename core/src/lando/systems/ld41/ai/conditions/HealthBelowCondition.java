package lando.systems.ld41.ai.conditions;

import lando.systems.ld41.gameobjects.FinalBoss;

public class HealthBelowCondition implements Condition {

    FinalBoss owner;
    int health;

    public HealthBelowCondition(FinalBoss owner, int amount) {
        this.owner = owner;
        this.health = amount;
    }

    @Override
    public boolean isTrue() {
        return owner.health < health;
    }
}
