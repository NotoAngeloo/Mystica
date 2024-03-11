package me.angeloo.mystica.Components.ProfileComponents;


public class Milestones {

    private boolean Tutorial;
    private boolean Divine;
    private boolean Chaos;
    private boolean FirstDungeon;

    public Milestones(
            boolean tutorial,
            boolean divine,
            boolean chaos,
        boolean firstDungeon
    ){
        Tutorial = tutorial;
        Divine = divine;
        Chaos = chaos;
        FirstDungeon = firstDungeon;
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

    public boolean getFirstDungeon(){return FirstDungeon;}

    private void setTutorial(boolean tutorial) {
        Tutorial = tutorial;
    }

    private void setDivine(boolean divine){
        Divine = divine;
    }

    private void setChaos(boolean chaos){
        Chaos = chaos;
    }

    private void setFirstDungeon(boolean firstDungeon){FirstDungeon = firstDungeon;}

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
            case "firstdungeon":{
                setFirstDungeon(to);
                break;
            }
        }

    }
}
