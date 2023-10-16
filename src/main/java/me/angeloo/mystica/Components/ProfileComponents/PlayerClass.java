package me.angeloo.mystica.Components.ProfileComponents;

public class PlayerClass {

    private String playerClass;
    private String subClass;


    public PlayerClass(String playerClass, String subClass){
        this.playerClass = playerClass;
        this.subClass = subClass;
    }

    public String getPlayerClass(){
        return playerClass;
    }

    public void setPlayerClass(String playerClass){
        this.playerClass = playerClass;
    }

    public String getSubClass(){
        return subClass;
    }

    public void setSubClass(String subClass){
        this.subClass = subClass;
    }
}
