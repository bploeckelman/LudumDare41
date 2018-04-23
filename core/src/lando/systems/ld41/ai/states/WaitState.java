package lando.systems.ld41.ai.states;

import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public class WaitState implements State{

    GameObject owner;

    public WaitState(GameObject owner) {
        this.owner = owner;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }
}
