package lando.systems.ld41.ai.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public class WanderState implements State{

    private EnemyTank owner;
    private Vector2 targetPos;
    private Vector2 tempVector;

    public WanderState(EnemyTank owner) {
        this.owner = owner;
        targetPos = new Vector2();
        tempVector = new Vector2();
    }

    @Override
    public void update(float dt) {
        if (owner.rotateAndMove(targetPos, dt)){
            findNewTarget();
        }
        owner.turrentTargetRotation += 100*dt;
        if (owner.turrentTargetRotation > 360){
            owner.turrentTargetRotation -= 360;
        }
    }

    @Override
    public void onEnter() {
        findNewTarget();

    }

    @Override
    public void onExit() {

    }

    private void findNewTarget(){
        targetPos.set(owner.position.x + MathUtils.randomSign() * MathUtils.random(100, 200f), owner.position.y + MathUtils.randomSign() * MathUtils.random(100, 200f));
    }
}
