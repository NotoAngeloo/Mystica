package me.angeloo.mystica.Components.Abilities.Warrior;

import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class FlamingSigil {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, BukkitTask> cooldownTask = new HashMap<>();
    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public FlamingSigil(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
    }

    public void use(Player player){
        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (getCooldown(player) > 0) {
            return;
        }

        Block block = player.getLocation().subtract(0,1,0).getBlock();

        if(block.getType() == Material.AIR){
            return;
        }


        if(profileManager.getAnyProfile(player).getCurrentMana()<getCost()){
            return;
        }

        changeResourceHandler.subTractManaFromPlayer(player, getCost());

        combatManager.startCombatTimer(player);

        execute(player);

        if(cooldownTask.containsKey(player.getUniqueId())){
            cooldownTask.get(player.getUniqueId()).cancel();
        }

        abilityReadyInMap.put(player.getUniqueId(), 10);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                if (getCooldown(player) <= 0) {
                    cooldownDisplayer.displayCooldown(player, 6);
                    this.cancel();
                    return;
                }

                int cooldown = getCooldown(player) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 6);

            }
        }.runTaskTimer(main, 0, 20);
        cooldownTask.put(player.getUniqueId(), task);
    }

    private void execute(Player player){

        boolean executioner = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("executioner");
        boolean gladiator = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("gladiator");



        Location spawnStart = player.getLocation().clone();

        ArmorStand sigil = player.getWorld().spawn(spawnStart.clone().subtract(0,5,0), ArmorStand.class);
        sigil.setInvisible(true);
        sigil.setGravity(false);
        sigil.setCollidable(false);
        sigil.setInvulnerable(true);
        sigil.setMarker(true);

        EntityEquipment entityEquipment = sigil.getEquipment();

        ItemStack item = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(7);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);
        sigil.teleport(spawnStart);

        Location center = sigil.getLocation();

        Set<LivingEntity> hitBySkill = new HashSet<>();
        double finalBuffAmount = getBuffAmount(player);
        new BukkitRunnable(){
            int ran = 0;
            @Override
            public void run(){

                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = center.getX() + (4 * Math.cos(angle));
                    double y = center.getY() + 1;
                    double z = center.getZ() + (4 * Math.sin(angle));
                    Location loc = new Location(center.getWorld(), x, y, z);
                    player.getWorld().spawnParticle(Particle.CRIT, loc, 1,0, 0, 0, 0);
                }

                BoundingBox hitBox = new BoundingBox(
                        center.getX() - 4,
                        center.getY() - 2,
                        center.getZ() - 4,
                        center.getX() + 4,
                        center.getY() + 4,
                        center.getZ() + 4
                );

                for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {


                    if(!(entity instanceof LivingEntity)){
                        continue;
                    }

                    if(entity instanceof ArmorStand){
                        continue;
                    }

                    LivingEntity thisEntity = (LivingEntity) entity;

                    if(hitBySkill.contains(thisEntity)){
                        continue;
                    }

                    if(entity == player){
                        buffAndDebuffManager.getFlamingSigilBuff().applyAttackBuff(player, finalBuffAmount);
                        buffAndDebuffManager.getFlamingSigilBuff().applyHealthBuff(player, finalBuffAmount);
                        hitBySkill.add(player);
                        continue;
                    }


                    if(entity instanceof Player){
                        if (pvpManager.pvpLogic(player, (Player) entity)) {
                            continue;
                        }
                    }

                    if(!(entity instanceof Player)){
                        if(pveChecker.pveLogic(thisEntity)){
                            continue;
                        }
                    }


                    if(executioner){
                        buffAndDebuffManager.getFlamingSigilBuff().applyAttackBuff(thisEntity, finalBuffAmount);
                    }

                    if(gladiator){
                        buffAndDebuffManager.getFlamingSigilBuff().applyHealthBuff(thisEntity, finalBuffAmount);
                    }

                    hitBySkill.add(thisEntity);

                }

                if(ran>=20*5){
                    this.cancel();
                    sigil.remove();
                }

                ran++;
            }
        }.runTaskTimer(main, 0, 1);
    }

    public double getBuffAmount(Player player){
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(player).getStats().getLevel()) +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();
        return 5 + ((int)(skillLevel/3));
    }

    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

    public double getCost(){
        return 10;
    }


    public void resetCooldown(Player player){
        abilityReadyInMap.remove(player.getUniqueId());
    }

}
