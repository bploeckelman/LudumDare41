package lando.systems.ld41.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.FinalBoss;

public class BossFullHealthState implements State {

    FinalBoss owner;
    private Vector2 targetPos;
    float fireDelay;


    public BossFullHealthState(FinalBoss owner){
        this.owner = owner;
        this.targetPos = new Vector2();
    }

    @Override
    public void update(float dt) {
        fireDelay -= dt;
        if (fireDelay < 0){
            fireDelay += 4f;
            owner.bulletSize = 10;
            owner.bulletTimeToLive = 5;
            owner.shootLeftTurret();
            owner.shootRightTurret();

        }
        if (owner.moveTo(targetPos, dt, 20, 90)){
            targetPos.set(owner.position.x + MathUtils.random(-200, 200), owner.position.y + MathUtils.random(-200,200));
        }

    }

    @Override
    public void onEnter() {
        fireDelay = 2f;
        targetPos.set(owner.position.x + MathUtils.random(-200, 200), owner.position.y + MathUtils.random(-200,200));
    }

    @Override
    public void onExit() {

    }
}
