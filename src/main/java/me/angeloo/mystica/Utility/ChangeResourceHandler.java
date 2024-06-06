package me.angeloo.mystica.Utility;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.Stats;
import me.angeloo.mystica.CustomEvents.AiSignalEvent;
import me.angeloo.mystica.CustomEvents.BoardValueUpdateEvent;
import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ChangeResourceHandler {

    private final Mystica main;

    private final DailyData dailyData;
    private final AggroManager aggroManager;
    private final ProfileManager profileManager;
    private final QuestManager questManager;
    private final ProtocolManager protocolManager;
    private final Map<UUID, Long> lastDamaged = new HashMap<>();
    private final Map<UUID, Long> lastManaed = new HashMap<>();

    private final BuffAndDebuffManager buffAndDebuffManager;
    private final DpsManager dpsManager;

    private final Map<UUID, BukkitTask> savedTask = new HashMap<>();
    private final Map<UUID, Double> damageSlot = new HashMap<>();
    private final Map<UUID, LinkedList<Double>> allSaved = new HashMap<>();
    private final Map<UUID, Boolean> seeingRawDamage = new HashMap<>();

    public ChangeResourceHandler(Mystica main){
        this.main = main;
        aggroManager = main.getAggroManager();
        questManager = main.getQuestManager();
        dailyData = main.getDailyData();
        protocolManager = main.getProtocolManager();
        profileManager = main.getProfileManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        dpsManager = main.getDpsManager();
    }

    public void subtractHealthFromEntity(LivingEntity entity, Double damage, LivingEntity damager){


        if(profileManager.getIfResetProcessing(entity)){
            return;
        }

        if(buffAndDebuffManager.getImmune().getImmune(entity)){
            return;
        }

        if(buffAndDebuffManager.getPassThrough().getIfPassingToPlayer(entity)){
            subtractHealthFromEntity(buffAndDebuffManager.getPassThrough().getPassingToCaster(entity), damage, damager);
            return;
        }

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
            Bukkit.getServer().getPluginManager().callEvent(new BoardValueUpdateEvent((Player) entity));
            return;
        }

        if(profileManager.getAnyProfile(entity).fakePlayer()){
            subtractHealthFromFakePlayer(entity, damage);
            return;
        }

        if(damager instanceof Player){

            if(profileManager.getAnyProfile(damager).getMilestones().getMilestone("new_hunter_accept") &&
            !profileManager.getAnyProfile(damager).getMilestones().getMilestone("new_hunter_complete")){
                questManager.completeQuest((Player)damager, "new_hunter");
            }

            //displayDamage((Player) damager, entity, damage);

            if(seeingRawDamage.getOrDefault(damager.getUniqueId(), false)){
                damager.sendMessage("you deal " + damage);
            }

            dpsManager.addToDamageDealt(damager, damage);

            //here
            if(!profileManager.getCompanions((Player) damager).isEmpty()){
                for(LivingEntity companion : profileManager.getCompanions((Player) damager)){
                    Bukkit.getServer().getPluginManager().callEvent(new AiSignalEvent(companion, "attack"));
                }
            }
        }

        if(profileManager.getAnyProfile(entity).getImmortality()){

            if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){

                AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getEntity();
                MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).signalMob(abstractEntity, "damage");
            }

            return;
        }

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

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, false));

    }

    private void subtractHealthFromFakePlayer(LivingEntity entity, double damage){

        if(profileManager.getAnyProfile(entity).getIfDead()){
            return;
        }

        if(profileManager.getAnyProfile(entity).getPlayerSubclass().equalsIgnoreCase("divine")){
            addToSlot(entity, damage);
            startTask(entity);
        }

        //this changes bar
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        if(damage > actualCurrentHealth){
            damage = actualCurrentHealth;
        }

        double newCurrentHealth = actualCurrentHealth - damage;
        profileManager.getAnyProfile(entity).setCurrentHealth(newCurrentHealth);

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, false));

        lastDamaged.put(entity.getUniqueId(), (System.currentTimeMillis()/1000));

        if(newCurrentHealth <= 0){
            //fake kill entity
            buffAndDebuffManager.removeAllBuffsAndDebuffs(entity);
            profileManager.getAnyProfile(entity).setIfDead(true);
            profileManager.getAnyProfile(entity).setCurrentHealth(profileManager.getAnyProfile(entity).getTotalHealth());
            dpsManager.removeDps(entity);
            aggroManager.removeFromAllAttackerLists(entity);
            entity.setAI(false);
            if(MythicBukkit.inst().getAPIHelper().isMythicMob(entity.getUniqueId())){
                AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).getEntity();
                MythicBukkit.inst().getAPIHelper().getMythicMobInstance(entity).signalMob(abstractEntity, "die");
                Bukkit.getServer().getPluginManager().callEvent(new AiSignalEvent(entity, "stop"));
            }
        }
    }

    private void subtractHealthFromPlayer(Player player, double damage) {

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }

        if(profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("divine")){
            addToSlot(player, damage);
            startTask(player);
        }

        if(seeingRawDamage.containsKey(player.getUniqueId())){

            if(seeingRawDamage.get(player.getUniqueId())){
                player.sendMessage("you take " + damage);
            }

        }


        Profile playerProfile = profileManager.getAnyProfile(player);

        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(player);

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

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(player, false));

        lastDamaged.put(player.getUniqueId(), (System.currentTimeMillis()/1000));
    }

    public void addHealthToEntity(LivingEntity entity, Double health, LivingEntity healer){

        if(healer != null){
            if(healer instanceof Player){
                if(seeingRawDamage.getOrDefault(healer.getUniqueId() ,false)){
                    healer.sendMessage("you heal " + health);
                }
            }
        }

        if(entity instanceof Player){
            addHealthToPlayer((Player) entity, health);
            return;
        }


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


        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    private void addHealthToPlayer(Player player, Double health){

        if(profileManager.getAnyProfile(player).getIfDead()){
            return;
        }


        Profile playerProfile = profileManager.getAnyProfile(player);

        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(player);

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

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(player, true));

    }

    public void subTractManaFromEntity(LivingEntity caster, Double cost){

        double currentMana = profileManager.getAnyProfile(caster).getCurrentMana();
        double newCurrentMana = currentMana - cost;
        profileManager.getAnyProfile(caster).setCurrentMana(newCurrentMana);

        lastManaed.put(caster.getUniqueId(), (System.currentTimeMillis()/1000));
    }

    public void addManaToEntity(LivingEntity entity, Double amount){

        Profile profile = profileManager.getAnyProfile(entity);

        double actualMaxMana = profile.getTotalMana();

        double currentMana = profileManager.getAnyProfile(entity).getCurrentMana();
        double newCurrentMana = currentMana + amount;

        if(newCurrentMana > actualMaxMana){
            newCurrentMana = actualMaxMana;
        }

        profileManager.getAnyProfile(entity).setCurrentMana(newCurrentMana);
        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(entity, true));
    }

    public void addXpToPlayer(Player player, float amount) {

        float currentXp = player.getExp();
        int currentLevel = player.getLevel();

        // Able to change this later

        if (currentLevel >= dailyData.getMaxLevel()) {
            currentLevel = dailyData.getMaxLevel();
            player.setLevel(currentLevel);
            profileManager.getAnyProfile(player).getStats().setLevel(currentLevel);
            return;
        }

        currentXp += amount;

        while (currentXp >= 1.0f) {
            currentLevel += 1;
            currentXp -= 1.0f;
        }

        player.setLevel(currentLevel);
        player.setExp(currentXp);
        profileManager.getAnyProfile(player).getStats().setLevel(currentLevel);
    }


    public Long getLastDamaged(UUID uuid){

        if(!lastDamaged.containsKey(uuid)){
            lastDamaged.put(uuid, (System.currentTimeMillis() / 1000) - 20);
        }

        return lastDamaged.get(uuid);
    }

    private void startTask(LivingEntity entity){

        if(savedTask.containsKey(entity.getUniqueId())){
            return;
        }

        BukkitTask task = new BukkitRunnable(){
            int ran = 0;
            @Override
            public void run(){
                addSaved(entity, getSaved(entity));
                clearSaved(entity);

                if(ran>=3 && getAllSaved(entity)==0.0){
                    this.cancel();
                    removeAllSaved(entity);
                    savedTask.remove(entity.getUniqueId());
                }

                if(ran<3){
                    ran++;
                }
            }
        }.runTaskTimer(main, 20, 20);


        savedTask.put(entity.getUniqueId(), task);
    }

    public double getAllSaved(LivingEntity entity){

        if(!allSaved.containsKey(entity.getUniqueId())){
            return 0.0;
        }

        LinkedList<Double> values = allSaved.get(entity.getUniqueId());

        double sum = 0;
        for(Double value : values){
            sum+=value;
        }

        //Bukkit.getLogger().info(String.valueOf(sum));
        //Bukkit.getLogger().info(String.valueOf(values));

        return sum;
    }

    private void addToSlot(LivingEntity entity, double damage){

        double saved = damageSlot.getOrDefault(entity.getUniqueId(), 0.0);

        saved = saved + damage;

        damageSlot.put(entity.getUniqueId(), saved);
    }

    private void addSaved(LivingEntity entity, double amount){

        LinkedList<Double> values = allSaved.getOrDefault(entity.getUniqueId(), new LinkedList<>());

        values.add(amount);

        if(values.size()>3){
            values.removeFirst();
        }

        allSaved.put(entity.getUniqueId(), values);
    }

    private void clearSaved(LivingEntity entity){
        damageSlot.remove(entity.getUniqueId());
    }

    private void removeAllSaved(LivingEntity entity){
        allSaved.remove(entity.getUniqueId());
    }

    private double getSaved(LivingEntity entity){
        return damageSlot.getOrDefault(entity.getUniqueId(), 0.0);
    }

    public void toggleSeeingRawDamage(Player player){

        if(!seeingRawDamage.containsKey(player.getUniqueId())){
            seeingRawDamage.put(player.getUniqueId(), false);
        }

        if(seeingRawDamage.get(player.getUniqueId())){
            seeingRawDamage.put(player.getUniqueId(), false);
            player.sendMessage("see all damage dealt toggled false");
            return;
        }

        if(!seeingRawDamage.get(player.getUniqueId())){
            seeingRawDamage.put(player.getUniqueId(), true);
            player.sendMessage("see all damage dealt toggled true");
        }

    }

    public Long getLastManaed(UUID uuid){

        if(!lastManaed.containsKey(uuid)){
            lastManaed.put(uuid, (System.currentTimeMillis() / 1000) - 20);
        }

        return lastManaed.get(uuid);
    }

    public void healPlayerToFull(Player player){

        double actualMaxHealth = profileManager.getAnyProfile(player).getTotalHealth();
        profileManager.getAnyProfile(player).setCurrentHealth(actualMaxHealth);
        player.setHealth(20);
    }

    public void displayDamage(Player player, LivingEntity target, double amount){

        PacketContainer container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        container.getIntegers().write(1, 100);

        container.getDoubles().write(0, target.getLocation().getX());
        container.getDoubles().write(1, target.getLocation().getY() + 1);
        container.getDoubles().write(2, target.getLocation().getZ());

        protocolManager.sendServerPacket(player, container);


    }

}
