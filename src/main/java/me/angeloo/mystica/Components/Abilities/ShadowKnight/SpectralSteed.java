package me.angeloo.mystica.Components.Abilities.ShadowKnight;

import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Managers.AbilityManager;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.CombatManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.CooldownDisplayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class SpectralSteed {

    private final Mystica main;
    private final ProfileManager profileManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final CombatManager combatManager;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public SpectralSteed(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        combatManager = manager.getCombatManager();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(LivingEntity caster){

        if(!abilityReadyInMap.containsKey(caster.getUniqueId())){
            abilityReadyInMap.put(caster.getUniqueId(), 0);
        }

        if(!usable(caster)){
            return;
        }

        combatManager.startCombatTimer(caster);

        execute(caster);

        if(cooldownTask.containsKey(caster.getUniqueId())){
            cooldownTask.get(caster.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(caster.getUniqueId(), 20);
        BukkitTask task = new BukkitRunnable(){
            @Override
            public void run(){

                if(getCooldown(caster) <= 0){
                    cooldownDisplayer.displayCooldown(caster, 7);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(caster) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(caster);

                abilityReadyInMap.put(caster.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(caster, 7);

            }
        }.runTaskTimer(main, 0,20);
        cooldownTask.put(caster.getUniqueId(), task);

    }

    private void execute(LivingEntity caster){

        Location spawnLoc = caster.getLocation();
        World world = spawnLoc.getWorld();
        assert world != null;
        SkeletonHorse horse = (SkeletonHorse) world.spawnEntity(spawnLoc, EntityType.SKELETON_HORSE);

        Stats stats = new Stats(profileManager.getAnyProfile(caster).getStats().getLevel(), 1,  1,  1, 1, 1);
        Yield yield = new Yield(0, new ArrayList<>());

        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(false, 1,stats,false,true, true, true, yield) {

            @Override
            public Boolean getIfInCombat() {
                return null;
            }


            @Override
            public void setIfInCombat(Boolean ifInCombat) {

            }


            @Override
            public void setLevelStats(int level, String subclass) {

            }

            @Override
            public StatsFromGear getGearStats() {
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
            public int getTotalAttack() {
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
            public Boolean fakePlayer() {
                return false;
            }

            @Override
            public Milestones getMilestones() {
                return null;
            }

            @Override
            public void getVoidsOnDeath(Set<Player> players) {

            }


        };
        profileManager.addToNonPlayerProfiles(horse.getUniqueId(), nonPlayerProfile);

        horse.setTamed(true);
        horse.setAdult();

        horse.addPassenger(caster);

        moveHorseInDirectionThePlayerLooks(caster, horse);

    }

    public void moveHorseInDirectionThePlayerLooks(LivingEntity caster, SkeletonHorse horse){

        new BukkitRunnable(){

            int count = 0;

            @Override
            public void run(){

                count += 1;

                if(caster instanceof Player){
                    if(!((Player)caster).isOnline()){
                        horse.remove();
                        this.cancel();
                        return;
                    }
                }

                boolean deathStatus = profileManager.getAnyProfile(caster).getIfDead();
                boolean combatStatus = profileManager.getAnyProfile(caster).getIfInCombat();

                if(deathStatus || !combatStatus){
                    horse.remove();
                    this.cancel();
                    return;
                }

                if(buffAndDebuffManager.getIfInterrupt(caster)){
                    horse.remove();
                    this.cancel();
                    return;
                }

                if(count >= 30){
                    horse.remove();
                    this.cancel();
                    return;
                }

                if(!caster.isInsideVehicle()){
                    horse.remove();
                    this.cancel();
                    return;
                }

                //check if player dismounted horse as well

                Location current = caster.getEyeLocation();
                Vector direction = current.getDirection().normalize();

                Vector runVector = direction.multiply(1).setY(0);

                double desiredYaw = Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
                horse.setRotation((float) desiredYaw, horse.getLocation().getPitch());

                horse.setVelocity(runVector);

            }
        }.runTaskTimer(main, 0, 5);
    }


    public int getCooldown(LivingEntity caster){
        int cooldown = abilityReadyInMap.getOrDefault(caster.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public void resetCooldown(LivingEntity caster){
        abilityReadyInMap.remove(caster.getUniqueId());
    }

    public boolean usable(LivingEntity caster){
        if(getCooldown(caster) > 0){
            return false;
        }

        Block block = caster.getLocation().subtract(0,1,0).getBlock();

        return block.getType() != Material.AIR;
    }


}
