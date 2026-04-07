package me.angeloo.mystica.Components.ProfileComponents;

public class PlayerBossLevel {

    private int BossLevel;

    public PlayerBossLevel(int bossLevel){
        BossLevel = bossLevel;
    }

    public int getBossLevel() {
        return BossLevel;
    }

    public void increase(){

        this.BossLevel ++;

    }

    public void decrease(){


        if(this.BossLevel==1){
            return;
        }

        this.BossLevel --;

    }

    public void setBossLevel(int bossLevel) {
        BossLevel = bossLevel;
    }

}
