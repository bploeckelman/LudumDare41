package lando.systems.ld41.ai.states;

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
        tempVector.set(targetPos.x - owner.position.x, targetPos.y - owner.position.y);
        float angleToLerp = (float)(Math.atan2(
                targetPos.y - owner.position.y,
                targetPos.x - owner.position.x) * 180 / Math.PI) - 90;
        if (angleToLerp - owner.rotation < -180) owner.rotation -= 360;
        if (angleToLerp - owner.rotation > 180) owner.rotation += 360;
        if (angleToLerp != owner.rotation){
            float rotationAmount = 120 * dt;
            if (Math.abs(angleToLerp - owner.rotation) < rotationAmount){
                owner.rotation = angleToLerp;
            } else {
                owner.rotation += Math.signum(angleToLerp - owner.rotation) * rotationAmount;
            }
        } else {
            float distanceToMove = owner.speed * dt;
            float distanceToTarget = owner.position.dst(targetPos);
            if (distanceToMove > distanceToTarget) {
                distanceToMove = distanceToTarget;
            }

            if (distanceToMove == 0 || ((EnemyTank)owner).updatePosition(distanceToMove)) {
                targetPos.set(owner.position.x + MathUtils.randomSign() * MathUtils.random(60, 100f), owner.position.y + MathUtils.randomSign() * MathUtils.random(60, 100f));
            }

        }
    }

    @Override
    public void onEnter() {
        targetPos.set(owner.position.x + MathUtils.random(-80, 80f), owner.position.y + MathUtils.random(-80, 80f));
    }

    @Override
    public void onExit() {

    }
}
