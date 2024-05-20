package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.Activities.DemonInvasion;
import me.angeloo.mystica.Mystica;

public class DailyEventManager {

    private final DemonInvasion demonInvasion;

    public DailyEventManager(Mystica main){
        demonInvasion = new DemonInvasion(main);
    }

    public DemonInvasion getDemonInvasion(){
        return demonInvasion;
    }
}
