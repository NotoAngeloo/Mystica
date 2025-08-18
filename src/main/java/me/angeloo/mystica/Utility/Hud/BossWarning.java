package me.angeloo.mystica.Utility.Hud;

public class BossWarning {

    private final String warning;
    private final boolean overridable;

    public BossWarning(String warning, boolean overridable){
        this.warning = warning;
        this.overridable = overridable;
    }

    public String getWarning(){
        return warning;
    }

    public boolean getIfOverridable(){
        return overridable;
    }

}
