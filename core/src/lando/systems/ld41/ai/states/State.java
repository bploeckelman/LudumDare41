package lando.systems.ld41.ai.states;


public interface State {

    void update(float dt);
    void onEnter();
    void onExit();

}