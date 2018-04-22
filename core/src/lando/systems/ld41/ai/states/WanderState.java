package lando.systems.ld41.ai.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public class WanderState extends State{

    private Vector2 targetPos;
    private Vector2 tempVector;

    public WanderState(GameObject owner) {
        super(owner);
        targetPos = new Vector2();
        tempVector = new Vector2();
    }

    @Override
    public void update(float dt) {
        if (((EnemyTank)owner).rotateAndMove(targetPos, dt)){
            findNewTarget();
        }
        ((EnemyTank)owner).turrentTargetRotation += 100*dt;
        if (((EnemyTank)owner).turrentTargetRotation > 360){
            ((EnemyTank)owner).turrentTargetRotation -= 360;
        }
    }

    @Override
    public void onEnter() {
        findNewTarget();
        Gdx.app.log("AI:", "Enter Wander");

    }

    @Override
    public void onExit() {

    }

    private void findNewTarget(){
        targetPos.set(owner.position.x + MathUtils.randomSign() * MathUtils.random(100, 200f), owner.position.y + MathUtils.randomSign() * MathUtils.random(100, 200f));
    }
}
