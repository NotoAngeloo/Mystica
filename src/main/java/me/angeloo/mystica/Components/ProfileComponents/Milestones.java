package me.angeloo.mystica.Components.ProfileComponents;


public class Milestones {

    private boolean Tutorial;

    public Milestones(boolean tutorial){
        Tutorial = tutorial;
    }

    public boolean getTutorial(){
        return Tutorial;
    }


    private void setTutorial(boolean tutorial) {
        Tutorial = tutorial;
    }


    public void setMilestone(String milestone, Boolean to){

        switch (milestone.toLowerCase()){
            case "tutorial":{
                setTutorial(to);
                break;
            }
        }

    }
}
