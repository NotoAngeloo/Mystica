package me.angeloo.mystica.Components.CombatSystem.Targeting;

public class TargetingContext {

    public final double range;
    public final boolean prioritizePlayers;
    public double fov;

    public TargetingContext(double range, boolean prioritizePlayers){
        this.range = range;
        this.prioritizePlayers = prioritizePlayers;
        //cone
        fov  = Math.toRadians(90);
    }

}
