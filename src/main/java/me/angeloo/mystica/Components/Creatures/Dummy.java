package me.angeloo.mystica.Components.Creatures;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Dummy {

    private final Mystica main;
    private final ProfileManager profileManager;

    public Dummy(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
    }

    public void spawn() throws InvalidMobTypeException {

        World world = Bukkit.getWorld("world");
        assert world != null;

        Location spawnLoc = new Location(world, 63, 99, -367, 0, 0);


        new BukkitRunnable(){
            @Override
            public void run(){

                BoundingBox hitBox = new BoundingBox(
                        spawnLoc.getX() - 20,
                        spawnLoc.getY() - 20,
                        spawnLoc.getZ() - 20,
                        spawnLoc.getX() + 20,
                        spawnLoc.getY() + 20,
                        spawnLoc.getZ() + 20
                );

                for(Entity entity : world.getNearbyEntities(hitBox)){
                    if(entity instanceof Player){
                        try {
                            MythicBukkit.inst().getAPIHelper().spawnMythicMob("Dummy", spawnLoc);
                        } catch (InvalidMobTypeException e) {
                            throw new RuntimeException(e);
                        }
                        removeNearbyEntities();
                        this.cancel();
                    }
                }

            }

            private void removeNearbyEntities(){

                for(Entity entity : world.getNearbyEntities(spawnLoc, 2, 2, 2)){
                    if(entity instanceof Player){
                        continue;
                    }

                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;

                    if(!profileManager.getAnyProfile(livingEntity).getImmortality()){
                        livingEntity.remove();
                    }
                }

            }

        }.runTaskTimer(main, 0, 20);

    }

    public void makeProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,10,1,0);
        Boolean isMovable = false;
        Boolean immortal = true;
        Boolean object = false;
        Boolean passive = false;
        Yield yield = new Yield(0.0f, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(10, stats, isMovable, immortal, passive, object, yield) {

            @Override
            public Bal getBal() {
                return null;
            }

            @Override
            public Boolean getIfDead() {

                Entity entity = Bukkit.getEntity(uuid);

                if(entity != null){
                    return entity.isDead();
                }

                return true;
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
                return 0;
            }

            @Override
            public int getTotalAttack() {
                return 0;
            }

            @Override
            public int getTotalDefense() {
                return 0;
            }

            @Override
            public int getTotalMagicDefense() {
                return 0;
            }


            @Override
            public int getTotalCrit() {
                return 0;
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
        profileManager.addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }

}
