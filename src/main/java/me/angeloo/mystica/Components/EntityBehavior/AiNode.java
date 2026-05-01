package me.angeloo.mystica.Components.EntityBehavior;

import java.util.function.Supplier;

public class AiNode {

    private final Supplier<Boolean> condition;
    private final Runnable action;

    public AiNode(Supplier<Boolean> condition, Runnable action){
        this.condition = condition;
        this.action = action;
    }

    public boolean tryExecute(){

        if(condition.get()){
            action.run();
            return true;
        }

        return false;
    }


}
