package lando.systems.ld41.ai.states;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.FinalBoss;

public class BossDeadState implements State {

    FinalBoss owner;

    public BossDeadState(FinalBoss owner){
        this.owner = owner;
    }

    @Override
    public void update(float dt) {


    }

    @Override
    public void onEnter() {
        owner.screen.removeAllBullets();
        owner.screen.level.hole.position.x = 20*32;
    }

    @Override
    public void onExit() {

    }
}
