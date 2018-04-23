package lando.systems.ld41.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.FinalBoss;

public class BossOneTurretState implements State {

    FinalBoss owner;
    private Vector2 targetPos;
    float fireDelay;
    float mainCannonDelay;


    public BossOneTurretState(FinalBoss owner){
        this.owner = owner;
        this.targetPos = new Vector2();
    }

    @Override
    public void update(float dt) {
        fireDelay -= dt;
        if (fireDelay < 0){
            fireDelay += 3f;
            owner.bulletSize = 10;
            owner.bulletSpeed = 200;
            owner.bulletTimeToLive = 4;
            owner.shootRightTurret();

        }

        owner.mainTurretRotation += 30 * dt;
        mainCannonDelay -= dt;
        if (mainCannonDelay < 0){
            owner.bulletSize = 15;
            owner.bulletSpeed = 200;
            owner.shootMainGun();
            mainCannonDelay += 5f;
        }


        if (owner.moveTo(owner.screen.playerTank.position, dt, 20, 90)){
            targetPos.set(owner.position.x + MathUtils.random(-200, 200), owner.position.y + MathUtils.random(-200,200));
        }

    }

    @Override
    public void onEnter() {
        fireDelay = 1f;
        mainCannonDelay = 2f;
        targetPos.set(owner.position.x + MathUtils.random(-200, 200), owner.position.y + MathUtils.random(-200,200));
    }

    @Override
    public void onExit() {

    }
}
