package lando.systems.ld41.ai.states;

import lando.systems.ld41.gameobjects.GameObject;

public abstract class State {
    GameObject owner;

    public State(GameObject owner){
        this.owner = owner;
    }

    public abstract void update(float dt);
    public abstract void onEnter();
    public abstract void onExit();

}