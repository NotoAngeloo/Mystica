package me.angeloo.mystica.Components.Abilities.Assassin;

import me.angeloo.mystica.Components.Abilities.AssassinAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.CooldownDisplayer;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class BladeTempest {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final DamageCalculator damageCalculator;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final CooldownDisplayer cooldownDisplayer;

    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    private final Combo combo;

    public BladeTempest(Mystica main, AbilityManager manager, AssassinAbilities assassinAbilities){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();
        damageCalculator = main.getDamageCalculator();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        cooldownDisplayer = new CooldownDisplayer(main, manager);
        combo = assassinAbilities.getCombo();
    }

    public void use(Player player){
        if (!abilityReadyInMap.containsKey(player.getUniqueId())) {
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        if (abilityReadyInMap.get(player.getUniqueId()) > 0) {
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 17);
        new BukkitRunnable() {
            @Override
            public void run() {

                if (abilityReadyInMap.get(player.getUniqueId()) <= 0) {
                    cooldownDisplayer.displayCooldown(player, 6);
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;

                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);

                abilityReadyInMap.put(player.getUniqueId(), cooldown);
                cooldownDisplayer.displayCooldown(player, 6);

            }
        }.runTaskTimer(main, 0, 20);
    }

    private void execute(Player player){

        boolean duelist = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("duelist");

        if(duelist){
            buffAndDebuffManager.getBladeTempestCrit().applyBonus(player);
        }

        Location start = player.getLocation().clone();

        Vector direction = start.getDirection().setY(0).normalize();

        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(2);
        item.setItemMeta(meta);

        double skillDamage = 6;
        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_6_Level_Bonus();
        skillDamage = skillDamage + ((int)(skillLevel/10));

        final boolean[] trigger = {false};
        Set<LivingEntity> hitBySkill = new HashSet<>();
        double finalSkillDamage = skillDamage;
        for(int i = 0;i<6;i++){

            direction.rotateAroundY(Math.toRadians(60));

            ArmorStand stand = player.getWorld().spawn(start.clone().subtract(0,10,0), ArmorStand.class);
            stand.setInvisible(true);
            stand.setGravity(false);
            stand.setCollidable(false);
            stand.setInvulnerable(true);
            stand.setMarker(true);
            EntityEquipment entityEquipment = stand.getEquipment();
            assert entityEquipment != null;
            entityEquipment.setHelmet(item);
            stand.teleport(start.clone().setDirection(direction));

            new BukkitRunnable(){
                boolean hit = false;
                final Vector finalDirection = direction.clone();
                double traveled = 0;
                @Override
                public void run(){

                    Location current = stand.getLocation();
                    double distanceThisTick = .4;
                    current.add(finalDirection.normalize().multiply(distanceThisTick));
                    current.setDirection(finalDirection);
                    traveled+=distanceThisTick;
                    stand.teleport(current);

                    BoundingBox hitBox = new BoundingBox(
                            current.getX() - 2,
                            current.getY() - 2,
                            current.getZ() - 2,
                            current.getX() + 2,
                            current.getY() + 4,
                            current.getZ() + 2
                    );

                    for (Entity entity : player.getWorld().getNearbyEntities(hitBox)) {

                        if(entity == player){
                            continue;
                        }

                        if(!(entity instanceof LivingEntity)){
                            continue;
                        }

                        if(entity instanceof ArmorStand){
                            continue;
                        }

                        LivingEntity livingEntity = (LivingEntity) entity;

                        if(hitBySkill.contains(livingEntity)){
                            continue;
                        }

                        hitBySkill.add(livingEntity);

                        boolean crit = damageCalculator.checkIfCrit(player, 0);

                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Physical", finalSkillDamage, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            if(pvpManager.pvpLogic(player, (Player) entity)){
                                changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                                hit = true;

                                if(!trigger[0]){
                                    trigger[0] = true;
                                    combo.addComboPoint(player);
                                }

                                break;
                            }
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                            hit = true;

                            if(!trigger[0]){
                                trigger[0] = true;
                                combo.addComboPoint(player);
                            }

                            break;
                        }

                    }

                    if(traveled>=8 || hit){
                        this.cancel();
                        stand.remove();
                    }
                }
            }.runTaskTimer(main, 0, 1);

        }

    }



    public int getCooldown(Player player){
        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
