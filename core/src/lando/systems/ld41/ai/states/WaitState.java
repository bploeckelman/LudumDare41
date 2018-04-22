package lando.systems.ld41.ai.states;

import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.GameObject;

public class WaitState extends State{
    public WaitState(GameObject owner) {
        super(owner);
    }

    @Override
    public void update(float dt) {
        ((EnemyTank)owner).turrentTargetRotation += 100*dt;
        if (((EnemyTank)owner).turrentTargetRotation > 360){
            ((EnemyTank)owner).turrentTargetRotation -= 360;
        }
    }

    @Override
    public void onEnter() {

    }

    @Override
    public void onExit() {

    }
}
