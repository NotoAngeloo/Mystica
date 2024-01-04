package me.angeloo.mystica.Utility;

import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.Managers.BuffAndDebuffManager;
import me.angeloo.mystica.Managers.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.Stats;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class ChangeResourceHandler {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final Map<UUID, Long> lastDamaged = new HashMap<>();

    private final BuffAndDebuffManager buffAndDebuffManager;

    private final Map<UUID, BukkitTask> savedTask = new HashMap<>();
    private final Map<UUID, Double> damageSlot = new HashMap<>();
    private final Map<UUID, LinkedList<Double>> allSaved = new HashMap<>();

    public ChangeResourceHandler(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
    }

    public void subtractHealthFromEntity(LivingEntity entity, Double damage, LivingEntity damager){

        if(buffAndDebuffManager.getWindWallBuff().getIfWindWallActive(entity)){

            if(damager == null){
                return;
            }

            double reflectedDamage = buffAndDebuffManager.getWindWallBuff().calculateHowMuchDamageIsReflected(entity, damage);
            subtractHealthFromEntity(damager, reflectedDamage, entity);

            if(buffAndDebuffManager.getWindWallBuff().getIfOverflow(entity)){

                subtractHealthFromEntity(entity, buffAndDebuffManager.getWindWallBuff().getOverflowAmount(entity), damager);
            }

            return;
        }

        if(buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(entity) > 0){
            damage = buffAndDebuffManager.getGenericShield().removeSomeShieldAndReturnHowMuchOver(entity, damage);
        }

        if(entity instanceof Player){
            subtractHealthFromPlayer((Player) entity, damage);
            return;
        }

        double trueHearts = entity.getHealth();
        Stats stats = profileManager.getAnyProfile(entity).getStats();

        double actualMaxHealth = stats.getHealth();
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        if(damage > actualCurrentHealth){
            damage = actualCurrentHealth;
        }

        double newCurrentHealth = actualCurrentHealth - damage;
        profileManager.getAnyProfile(entity).setCurrentHealth(newCurrentHealth);

        double ratio = newCurrentHealth / actualMaxHealth;

        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        int hearts = (int) Math.ceil(ratio * maxHealth);

        if(hearts < 0){
            hearts = 0;
        }

        if(newCurrentHealth <= 0){
            hearts = 0;
        }

        entity.setHealth(hearts);

        double newTrueHearts = entity.getHealth();

        double trueDifference = trueHearts - newTrueHearts;

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, trueDifference, false));

    }

    private void subtractHealthFromPlayer(Player player, Double damage) {

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        if(profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("divine")){
            addToSlot(player, damage);
            startTask(player);
        }

        double trueHearts = player.getHealth();

        Profile playerProfile = profileManager.getAnyProfile(player);

        double actualMaxHealth = playerProfile.getTotalHealth();

        //this changes bar
        double actualCurrentHealth = profileManager.getAnyProfile(player).getCurrentHealth();

        if(damage > actualCurrentHealth){
            damage = actualCurrentHealth;
        }

        double newCurrentHealth = actualCurrentHealth - damage;
        profileManager.getAnyProfile(player).setCurrentHealth(newCurrentHealth);


        //now i gotta change hearts
        double ratio = newCurrentHealth / actualMaxHealth;

        int hearts = (int) Math.ceil(ratio * 20);

        if(hearts < 0){
            hearts = 0;
        }

        if(newCurrentHealth <= 0){
            hearts = 0;
        }

        player.setHealth(hearts);

        double newTrueHearts = player.getHealth();

        double trueDifference = trueHearts - newTrueHearts;

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(player, trueDifference, false));

        lastDamaged.put(player.getUniqueId(), (System.currentTimeMillis()/1000));
    }

    public void addHealthToEntity(LivingEntity entity, Double health, LivingEntity entityWhoHealed){

        if(entity instanceof Player){
            addHealthToPlayer((Player) entity, health);
            return;
        }

        double trueHearts = entity.getHealth();

        Stats stats = profileManager.getAnyProfile(entity).getStats();
        double actualMaxHealth = stats.getHealth();
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        double newCurrentHealth = actualCurrentHealth + health;

        if(newCurrentHealth > actualMaxHealth){
            newCurrentHealth = actualMaxHealth;
        }

        profileManager.getAnyProfile(entity).setCurrentHealth(newCurrentHealth);

        double ratio = newCurrentHealth / actualMaxHealth;

        double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        double hearts =  Math.ceil(ratio * maxHealth);
        if(hearts > maxHealth){
            hearts = maxHealth;
        }

        if(newCurrentHealth == actualMaxHealth){
            hearts = maxHealth;
        }
        entity.setHealth(hearts);

        double newTrueHearts = entity.getHealth();

        double trueDifference = trueHearts - newTrueHearts;

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, trueDifference, true));
    }

    private void addHealthToPlayer(Player player, Double health){

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        double trueHearts = player.getHealth();

        Profile playerProfile = profileManager.getAnyProfile(player);

        double actualMaxHealth = playerProfile.getTotalHealth();

        //this changes bar
        double actualCurrentHealth = profileManager.getAnyProfile(player).getCurrentHealth();


        double newCurrentHealth = actualCurrentHealth + health;

        if(newCurrentHealth > actualMaxHealth){
            newCurrentHealth = actualMaxHealth;
        }

        profileManager.getAnyProfile(player).setCurrentHealth(newCurrentHealth);

        //now i gotta change hearts
        double ratio = newCurrentHealth / actualMaxHealth;

        int hearts = (int) Math.ceil(ratio * 20);
        if(hearts > 20){
            hearts = 20;
        }

        if(newCurrentHealth == actualMaxHealth){
            hearts = 20;
        }
        player.setHealth(hearts);

        double newTrueHearts = player.getHealth();

        double trueDifference = trueHearts - newTrueHearts;

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(player, trueDifference, true));

    }

    public void subTractManaFromPlayer(Player player, Double cost){

        double currentMana = profileManager.getAnyProfile(player).getCurrentMana();
        double newCurrentMana = currentMana - cost;
        profileManager.getAnyProfile(player).setCurrentMana(newCurrentMana);
    }

    public void addManaToPlayer(Player player, Double amount){

        Profile playerProfile = profileManager.getAnyProfile(player);

        double actualMaxMana = playerProfile.getTotalMana();

        double currentMana = profileManager.getAnyProfile(player).getCurrentMana();
        double newCurrentMana = currentMana + amount;

        if(newCurrentMana > actualMaxMana){
            newCurrentMana = actualMaxMana;
        }

        profileManager.getAnyProfile(player).setCurrentMana(newCurrentMana);
    }

    public void healPlayerToFull(Player player){

        Profile playerProfile = profileManager.getAnyProfile(player);

        double actualMaxHealth = playerProfile.getTotalHealth();
        profileManager.getAnyProfile(player).setCurrentHealth(actualMaxHealth);
        player.setHealth(20);
    }

    public Long getLastDamaged(UUID uuid){

        if(!lastDamaged.containsKey(uuid)){
            lastDamaged.put(uuid, (System.currentTimeMillis() / 1000) - 3);
        }

        return lastDamaged.get(uuid);
    }

    private void startTask(Player player){

        if(savedTask.containsKey(player.getUniqueId())){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            int ran = 0;
            @Override
            public void run(){
                addSaved(player, getSaved(player));
                clearSaved(player);

                if(ran>=3 && getAllSaved(player)==0.0){
                    this.cancel();
                    removeAllSaved(player);
                    savedTask.remove(player.getUniqueId());
                }

                if(ran<3){
                    ran++;
                }
            }
        }.runTaskTimer(main, 20, 20);


        savedTask.put(player.getUniqueId(), task);
    }

    public double getAllSaved(Player player){

        if(!allSaved.containsKey(player.getUniqueId())){
            return 0.0;
        }

        LinkedList<Double> values = allSaved.get(player.getUniqueId());

        double sum = 0;
        for(Double value : values){
            sum+=value;
        }

        //Bukkit.getLogger().info(String.valueOf(sum));
        //Bukkit.getLogger().info(String.valueOf(values));

        return sum;
    }

    private void addToSlot(Player player, double damage){

        double saved = damageSlot.getOrDefault(player.getUniqueId(), 0.0);

        saved = saved + damage;

        damageSlot.put(player.getUniqueId(), saved);
    }

    private void addSaved(Player player, double amount){

        LinkedList<Double> values = allSaved.getOrDefault(player.getUniqueId(), new LinkedList<>());

        values.add(amount);

        if(values.size()>3){
            values.removeFirst();
        }

        allSaved.put(player.getUniqueId(), values);
    }

    private void clearSaved(Player player){
        damageSlot.remove(player.getUniqueId());
    }

    private void removeAllSaved(Player player){
        allSaved.remove(player.getUniqueId());
    }

    private double getSaved(Player player){
        return damageSlot.getOrDefault(player.getUniqueId(), 0.0);
    }

}
