package me.angeloo.mystica.Components.ProfileComponents;


import java.util.Map;

public class Milestones {

    private final Map<String, Boolean> allMilestones;

    public Milestones(Map<String, Boolean> allMilestones){
        this.allMilestones = allMilestones;
    }

    public void setMilestone(String milestone, boolean to){
        allMilestones.put(milestone, to);
    }

    public boolean getMilestone(String milestone){
        return allMilestones.getOrDefault(milestone, false);
    }

    public Map<String, Boolean> getAllMilestones(){
        return allMilestones;
    }
}
