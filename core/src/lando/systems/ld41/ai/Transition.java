package lando.systems.ld41.ai;

import lando.systems.ld41.ai.conditions.Condition;
import lando.systems.ld41.ai.states.State;

public class Transition {
    State from;
    Condition condition;
    State to;

    public Transition(State from, Condition condition, State to){
        this.from = from;
        this.condition = condition;
        this.to = to;
    }
}