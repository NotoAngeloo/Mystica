package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpectralSteed {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SpectralSteed(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        changeResourceHandler = main.getChangeResourceHandler();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        Block block = player.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 20);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    cooldownDisplayer.displayCooldown(player, 7);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 7);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        Location spawnLoc = player.getLocation();
        World world = spawnLoc.getWorld();
        assert world != null;
        SkeletonHorse horse = (SkeletonHorse) world.spawnEntity(spawnLoc, EntityType.SKELETON_HORSE);

        Stats stats = new Stats(profileManager.getAnyProfile(player).getStats().getLevel(), 1, 1, 1, 1, 1, 1, 1, 1, 1);
        Yield yield = new Yield(0, new ArrayList<>());

        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(1,stats,false,true, true, true, yield) {
            @Override
            public Boolean getIfDead() {
                return null;
            }

            @Override
            public Boolean getIfInCombat() {
                return null;
            }

            @Override
            public void setIfDead(Boolean ifDead) {

            }

            @Override
            public void setIfInCombat(Boolean ifInCombat) {

            }

            @Override
            public double getCurrentMana() {
                return 0;
            }

            @Override
            public void setCurrentMana(double currentMana) {

            }

            @Override
            public void setLevelStats(int level, String subclass) {

            }

            @Override
            public StatsFromGear getGearStats() {
                return null;
            }

            @Override
            public Bal getBal() {
                return null;
            }

            @Override
            public void setGearStats(StatsFromGear statsFromGear) {

            }

            @Override
            public int getTotalHealth() {
                return 1;
            }

            @Override
            public int getTotalMana() {
                return 1;
            }

            @Override
            public int getTotalAttack() {
                return 1;
            }

            @Override
            public int getTotalMagic() {
                return 1;
            }

            @Override
            public int getTotalDefense() {
                return 1;
            }

            @Override
            public int getTotalMagicDefense() {
                return 1;
            }

            @Override
            public int getTotalRegen() {
                return 1;
            }

            @Override
            public int getTotalManaRegen() {
                return 1;
            }

            @Override
            public int getTotalCrit() {
                return 1;
            }

            @Override
            public String getPlayerClass() {
                return null;
            }

            @Override
            public void setPlayerClass(String playerClass) {

            }

            @Override
            public String getPlayerSubclass() {
                return null;
            }

            @Override
            public void setPlayerSubclass(String playerSubclass) {

            }

            @Override
            public ItemStack[] getSavedInv() {
                return new ItemStack[0];
            }

            @Override
            public void setSavedInv(ItemStack[] inv) {

            }

            @Override
            public void removeSavedInv() {

            }

            @Override
            public PlayerBag getPlayerBag() {
                return null;
            }

            @Override
            public PlayerEquipment getPlayerEquipment() {
                return null;
            }

            @Override
            public PlayerBossLevel getPlayerBossLevel() {
                return null;
            }

            @Override
            public Skill_Level getSkillLevels() {
                return null;
            }

            @Override
            public EquipSkills getEquipSkills() {
                return null;
            }

            @Override
            public Milestones getMilestones() {
                return null;
            }
        };
        profileManager.addToNonPlayerProfiles(horse.getUniqueId(), nonPlayerProfile);

        horse.setTamed(true);
        horse.setAdult();

        horse.addPassenger(player);

        moveHorseInDirectionThePlayerLooks(player, horse);

    }

    public void moveHorseInDirectionThePlayerLooks(Player player, SkeletonHorse horse){

        new BukkitRunnable(){

            int count = 0;

            @Override
            public void run(){

                count += 1;

                if(!player.isOnline()){
                    horse.remove();
                    this.cancel();
                    return;
                }

                boolean deathStatus = profileManager.getAnyProfile(player).getIfDead();
                boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

                if(deathStatus || !combatStatus){
                    horse.remove();
                    this.cancel();
                    return;
                }

                if(buffAndDebuffManager.getIfInterrupt(player)){
                    horse.remove();
                    this.cancel();
                    return;
                }

                if(count >= 30){
                    horse.remove();
                    this.cancel();
                    return;
                }

                if(!player.isInsideVehicle()){
                    horse.remove();
                    this.cancel();
                    return;
                }

                //check if player dismounted horse as well

                Location current = player.getEyeLocation();
                Vector direction = current.getDirection().normalize();

                Vector runVector = direction.multiply(1).setY(0);

                double desiredYaw = Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
                horse.setRotation((float) desiredYaw, horse.getLocation().getPitch());

                horse.setVelocity(runVector);

            }
        }.runTaskTimer(main, 0, 5);
    }


    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
