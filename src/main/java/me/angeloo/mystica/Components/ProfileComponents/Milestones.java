package me.angeloo.mystica.Components.ProfileComponents;


public class Milestones {

    private boolean Tutorial;
    private boolean Divine;
    private boolean Chaos;

    public Milestones(
            boolean tutorial,
            boolean divine,
            boolean chaos){
        Tutorial = tutorial;
        Divine = divine;
        Chaos = chaos;
    }

    public boolean getTutorial(){
        return Tutorial;
    }

    public boolean getDivine(){
        return Divine;
    }

    public boolean getChaos(){
        return Chaos;
    }


    private void setTutorial(boolean tutorial) {
        Tutorial = tutorial;
    }

    private void setDivine(boolean divine){
        Divine = divine;
    }

    private void setChaos(boolean chaos){
        Chaos = chaos;
    }

    public void setMilestone(String milestone, Boolean to){

        switch (milestone.toLowerCase()){
            case "tutorial":{
                setTutorial(to);
                break;
            }
            case "divine":{
                setDivine(to);
                break;
            }
            case "chaos":{
                setChaos(to);
                break;
            }
        }

    }
}
