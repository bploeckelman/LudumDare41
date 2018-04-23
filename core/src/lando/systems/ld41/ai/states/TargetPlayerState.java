package lando.systems.ld41.ai.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public class TargetPlayerState implements State{

    EnemyTank owner;
    private Vector2 targetPos;

    public TargetPlayerState(EnemyTank owner) {
        this.owner = owner;
        targetPos = new Vector2();
    }


    @Override
    public void update(float dt) {
        if (targetPos.epsilonEquals(-1000, -1000)) {
            if (owner.rotateAndMove(owner.screen.playerTank.position, dt)) {
                // it ran into a wall, try to move around randomly
                findNewTarget();
            }
        } else {
            if (owner.rotateAndMove(targetPos, dt)) {
                targetPos.set(-1000,-1000);
            }
        }

        owner.turrentTargetRotation = (float)(Math.atan2(
                owner.screen.playerTank.position.y - owner.position.y,
                owner.screen.playerTank.position.x - owner.position.x) * 180 / Math.PI);

        owner.shoot(dt);
    }

    @Override
    public void onEnter() {
    }

    @Override
    public void onExit() {

    }

    private void findNewTarget(){
        targetPos.set(owner.position.x + MathUtils.randomSign() * MathUtils.random(100, 200f), owner.position.y + MathUtils.randomSign() * MathUtils.random(100, 200f));
    }
}
