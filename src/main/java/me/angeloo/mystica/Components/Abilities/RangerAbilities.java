package me.angeloo.mystica.Components.Abilities;

import me.angeloo.mystica.Components.Abilities.Ranger.BitingRain;
import me.angeloo.mystica.Components.Abilities.Ranger.RangerBasic;
import me.angeloo.mystica.Components.Abilities.Ranger.Relentless;
import me.angeloo.mystica.Components.Abilities.Ranger.ShadowCrows;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RangerAbilities {

    private final ProfileManager profileManager;

    private final Map<Player, Boolean> castMap = new HashMap<>();
    private final Map<Player, Double> percentCastBar = new HashMap<>();

    private final Relentless relentless;
    private final ShadowCrows shadowCrows;
    private final BitingRain bitingRain;
    private final RangerBasic rangerBasic;

    public RangerAbilities(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        //buff skill first
        relentless = new Relentless(main, manager);
        shadowCrows = new ShadowCrows(main, manager);
        bitingRain = new BitingRain(main, manager);
        rangerBasic = new RangerBasic(main, manager);
    }

    public void useRangerAbility(Player player, int abilityNumber){

        if(getIfCasting(player)){
            return;
        }

        switch (abilityNumber){
            case 1:{
                bitingRain.use(player);
                return;
            }
            case 2:{
                shadowCrows.use(player);
                return;
            }
            case 3:{
                relentless.use(player);
                return;
            }
        }
    }

    public void useRangerUltimate(Player player){

        if(getIfCasting(player)){
            return;
        }

        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();

    }

    public void useRangerBasic(Player player){

        if(getIfCasting(player)){
            return;
        }

        rangerBasic.useBasic(player);
    }

    public int getAbilityCooldown(Player player, int abilityNumber){

        switch (abilityNumber){
            case 1:
                return bitingRain.getCooldown(player);
            case 2:
                return shadowCrows.getCooldown(player);
            case 3:
                return relentless.getCooldown(player);
        }

        return 0;
    }

    public int getUltimateCooldown(Player player){
        String subclass = profileManager.getAnyProfile(player).getPlayerSubclass();


        return 0;
    }

    public boolean getIfCasting(Player player){
        return castMap.getOrDefault(player, false);
    }

    public void setCasting(Player player, boolean casting){
        castMap.put(player, casting);
    }

    public void setCastBar(Player player, double percent){
        percentCastBar.put(player, percent);
    }

    public double getCastPercent(Player player){
        return percentCastBar.getOrDefault(player, 0.0);
    }

}
