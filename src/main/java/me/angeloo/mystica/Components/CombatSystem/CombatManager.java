package me.angeloo.mystica.Components.CombatSystem;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.Items.Equipment.EquipmentDisplayRenderer;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.PlayerEquipment;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Tasks.CombatTick;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CombatManager {

    private final Mystica main;
    private final DpsManager dpsManager;
    private final EquipmentDisplayRenderer equipmentDisplayRenderer;
    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final AbilityManager abilityManager;
    private final CombatTick combatTick;
    private final CooldownManager cooldownManager;

    private final Map<UUID, Boolean> sheathed = new HashMap<>();
    private final Map<UUID, Long> lastCalledCombat = new HashMap<>();

    public CombatManager(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        equipmentDisplayRenderer = main.getEquipmentDisplayRenderer();
        mysticaPartyManager = main.getMysticaPartyManager();
        abilityManager = manager;
        dpsManager = main.getDpsManager();
        cooldownManager = main.getCooldownManager();
        combatTick = new CombatTick(main, this, manager);
    }


    public void startCombatTimer(LivingEntity caster){

        if(profileManager.getAnyProfile(caster).fakePlayer()){
            profileManager.getAnyProfile(caster).setIfInCombat(true);
        }

        if(!(caster instanceof Player player)){
            return;
        }

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if (!combatStatus){
            profileManager.getAnyProfile(player).setIfInCombat(true);
            //player.sendMessage("You are now in combat");

            player.closeInventory();
            player.getInventory().clear();
            //displayWeapons.displayArmor(player);

            //cooldownDisplayer.initializeItems(player);

            unSheathWeapon(player);


            //make rest of team in combat
            for(LivingEntity member :getMParty(player)){
                if(member instanceof Player pPlayer){
                    startCombatTimer(pPlayer);
                }


            }

            dpsManager.removeDps(player);
            //remove for companions
            if(!profileManager.getCompanions(player).isEmpty()){
                Bukkit.getScheduler().runTask(main, ()->{
                    for(UUID companionId : profileManager.getCompanions(player)){

                        if(!(Bukkit.getEntity(companionId) instanceof LivingEntity companion)){
                            continue;
                        }

                        dpsManager.removeDps(companion);
                    }


                });
            }

            combatTick.startCombatTickFor(player);
        }

        profileManager.getAnyProfile(player).setIfInCombat(true);
        lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        //Bukkit.getServer().getPluginManager().callEvent(new HudUpdateEvent(player, "status", false));
    }


    private long getLastCalledCombat(Player player){

        if(!lastCalledCombat.containsKey(player.getUniqueId())){
            lastCalledCombat.put(player.getUniqueId(), System.currentTimeMillis());
        }

        return lastCalledCombat.get(player.getUniqueId());
    }

    public boolean canLeaveCombat(Player player){

        //first check mparty

        for(LivingEntity member : getMParty(player)){

            if(member == player){
                continue;
            }

            boolean partyMemberDeathStatus = profileManager.getAnyProfile(member).getIfDead();

            if(partyMemberDeathStatus){
                continue;
            }


            if(member instanceof Player mPlayer){

                if(combatTimeEnough(mPlayer)){
                    continue;
                }

            }

            boolean partyMemberCombatStatus = profileManager.getAnyProfile(member).getIfInCombat();


            if(partyMemberCombatStatus){
                return false;
            }


        }

        return combatTimeEnough(player);
    }

    private boolean combatTimeEnough(Player player){
        long currentTime = System.currentTimeMillis();
        long lastCalled = getLastCalledCombat(player);
        //Bukkit.getLogger().info(String.valueOf(currentTime - lastCalled));

        return currentTime - lastCalled > 5000;
    }

    public void forceCombatEnd(Player player){

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        profileManager.getAnyProfile(player).setIfInCombat(false);

        abilityManager.resetResource(player);

        //player.sendMessage("You are no longer in combat");

        //and restore their inventories


        //dpsManager.removeDps(player);
        cooldownManager.clearAll(player.getUniqueId());


        if(!profileManager.getAnyProfile(player).getIfDead()){
            player.setInvisible(false);
            sheathWeapon(player);
        }
    }

    public void unSheathWeapon(LivingEntity caster){

        if(!(caster instanceof Player player)){
            return;
        }

        //check if shealthed already
        if(!ifSheathed(player)){
            return;
        }

        PlayerEquipment playerEquipment = profileManager.getAnyProfile(player).getPlayerEquipment();

        if(playerEquipment.getWeapon() != null){
            player.getInventory().setItemInMainHand(equipmentDisplayRenderer.render(playerEquipment.getWeapon()));
            equipmentDisplayRenderer.renderOffHand(player);
        }

        sheathed.put(player.getUniqueId(), true);

        new BukkitRunnable(){
            @Override
            public void run(){


                Player refreshedPlayer = Bukkit.getOfflinePlayer(player.getUniqueId()).getPlayer();

                if(refreshedPlayer == null){
                    return;
                }

                if(!refreshedPlayer.isOnline()){
                    return;
                }



                sheathWeapon(player);

            }
        }.runTaskLaterAsynchronously(main, 200);

    }

    public void sheathWeapon(Player player){

        if(profileManager.getAnyProfile(player).getIfInCombat()){
            return;
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        equipmentDisplayRenderer.renderAllArmor(player);
        equipmentDisplayRenderer.renderSheathedWeapons(player);
        sheathed.remove(player.getUniqueId());
        abilityManager.interruptBasic(player);
    }

    private boolean ifSheathed(Player player){
        return sheathed.getOrDefault(player.getUniqueId(), true);
    }

    private List<LivingEntity> getMParty(LivingEntity entity){
        return new ArrayList<>(mysticaPartyManager.getMysticaParty(entity));
    }

}
