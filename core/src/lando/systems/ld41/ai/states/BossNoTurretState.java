package lando.systems.ld41.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.FinalBoss;

public class BossNoTurretState implements State {

    FinalBoss owner;
    private Vector2 targetPos;
    float fireDelay;
    private Vector2 screenCenter;


    public BossNoTurretState(FinalBoss owner){
        this.owner = owner;
        this.targetPos = new Vector2();
    }

    @Override
    public void update(float dt) {
        owner.mainTurretRotation = (float)(Math.atan2(
                owner.screen.playerTank.position.y - owner.position.y,
                owner.screen.playerTank.position.x - owner.position.x) * 180 / Math.PI);
        fireDelay -= dt;

        if (fireDelay < 0){
            owner.bulletSize = 15;
            owner.bulletSpeed = 200;
            owner.shootMainGun();
            fireDelay += 4f;
        }
        if (owner.position.dst(screenCenter) > 10) {
            if (owner.moveTo(targetPos, dt, 40, 120)) {
                if (targetPos.epsilonEquals(screenCenter)) {
                    targetPos.set(owner.position.x + MathUtils.random(-200, 200), owner.position.y + MathUtils.random(-200, 200));
                } else {
                    targetPos.set(screenCenter);
                }
            }
        }

    }

    @Override
    public void onEnter() {
        screenCenter = new Vector2(20*32, 20*32);
        fireDelay = 3f;
        targetPos.set(screenCenter);
    }

    @Override
    public void onExit() {

    }
}
