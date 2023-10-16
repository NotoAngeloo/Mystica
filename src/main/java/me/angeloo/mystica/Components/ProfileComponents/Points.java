package me.angeloo.mystica.Components.ProfileComponents;

public class Points {

    private int TalentPoints;
    private int Bal;

    public Points(int talentPoints, int bal){
        TalentPoints = talentPoints;
        Bal = bal;
    }

    public int getTalentPoints(){return TalentPoints;}
    public int getBal(){return Bal;}

    public void setTalentPoints(int talentPoints){TalentPoints = talentPoints;}
    public void setBal(int bal){Bal = bal;}
}
