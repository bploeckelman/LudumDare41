package lando.systems.ld41.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.FinalBoss;

public class BossDeadState implements State {

    FinalBoss owner;
    private Vector2 targetPos;
    float fireDelay;
    private Vector2 screenCenter;


    public BossDeadState(FinalBoss owner){
        this.owner = owner;
        this.targetPos = new Vector2();
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
