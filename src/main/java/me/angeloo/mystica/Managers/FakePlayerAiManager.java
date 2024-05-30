package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Mystica;
import org.bukkit.entity.LivingEntity;


public class FakePlayerAiManager {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;


    public FakePlayerAiManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        abilityManager = main.getAbilityManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
    }


    public void signalAttack(LivingEntity companion){

        switch (profileManager.getAnyProfile(companion).getPlayerClass().toLowerCase()){
            case "paladin":{
                //abilityManager.getPaladinAbilities().getJudgement().use();
                break;
            }
        }

    }



}
