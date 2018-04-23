package lando.systems.ld41.ai.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public class EvadePlayerState implements State{

    EnemyTank owner;
    private Vector2 targetPos;

    public EvadePlayerState(EnemyTank owner) {
        this.owner = owner;
        targetPos = new Vector2();
    }


    @Override
    public void update(float dt) {
        if (owner.rotateAndMove(targetPos, dt)){
            findNewTarget();
        }

        owner.turrentTargetRotation = (float)(Math.atan2(
                owner.screen.playerTank.position.y - owner.position.y,
                owner.screen.playerTank.position.x - owner.position.x) * 180 / Math.PI);

        owner.shoot(dt);

    }

    @Override
    public void onEnter() {
        findNewTarget();
    }

    @Override
    public void onExit() {

    }

    private void findNewTarget(){
        targetPos.set(owner.screen.playerTank.position).sub(owner.position);
        targetPos.rotate(120f * MathUtils.randomSign());
        targetPos.nor().scl(200).add(owner.position);
    }
}
