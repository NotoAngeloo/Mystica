package me.angeloo.mystica.Components.Abilities.Elementalist;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.angeloo.mystica.Components.Abilities.ElementalistAbilities;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Managers.*;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageCalculator;
import me.angeloo.mystica.Utility.PveChecker;
import org.bukkit.*;
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

public class ElementalMatrix {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final CombatManager combatManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final ChangeResourceHandler changeResourceHandler;


    private final Map<UUID, Integer> abilityReadyInMap = new HashMap<>();

    public ElementalMatrix(Mystica main, AbilityManager manager){
        this.main = main;
        profileManager = main.getProfileManager();
        combatManager = manager.getCombatManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        changeResourceHandler = main.getChangeResourceHandler();


    }

    public void use(Player player){

        if(!abilityReadyInMap.containsKey(player.getUniqueId())){
            abilityReadyInMap.put(player.getUniqueId(), 0);
        }

        double baseRange = 20;
        double extraRange = buffAndDebuffManager.getTotalRangeModifier(player);
        double totalRange = baseRange + extraRange;

        targetManager.setTargetToNearestValid(player, totalRange);

        LivingEntity target = targetManager.getPlayerTarget(player);

        if(target != null){
            if(target instanceof Player){
                if(!pvpManager.pvpLogic(player, (Player) target)){
                    return;
                }
            }

            if(!(target instanceof Player)){
                if(!pveChecker.pveLogic(target)){
                    return;
                }
            }

            double distance = player.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return;
            }
        }

        if(target == null){
            return;
        }

        if(abilityReadyInMap.get(player.getUniqueId()) > 0){
            return;
        }

        combatManager.startCombatTimer(player);

        execute(player);

        abilityReadyInMap.put(player.getUniqueId(), 10);
        new BukkitRunnable(){
            @Override
            public void run(){

                if(abilityReadyInMap.get(player.getUniqueId()) <= 0){
                    this.cancel();
                    return;
                }

                int cooldown = abilityReadyInMap.get(player.getUniqueId()) - 1;
                cooldown = cooldown - buffAndDebuffManager.getHaste().getHasteLevel(player);


                abilityReadyInMap.put(player.getUniqueId(), cooldown);

            }
        }.runTaskTimer(main, 0,20);

    }

    private void execute(Player player){

        boolean cryomancer = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("cryomancer");
        boolean conjurer = profileManager.getAnyProfile(player).getPlayerSubclass().equalsIgnoreCase("conjurer");

        PartiesAPI api = Parties.getApi();
        PartyPlayer partyPlayer = api.getPartyPlayer(player.getUniqueId());
        assert partyPlayer != null;
        if(partyPlayer.isInParty()){

            Party party = api.getParty(partyPlayer.getPartyId());

            assert party != null;
            Set<UUID> partyMemberList = party.getMembers();

            for(UUID partyMemberId : partyMemberList){

                Player partyMember = Bukkit.getPlayer(partyMemberId);

                if(partyMember == null){
                    continue;
                }

                if(!partyMember.isOnline()){
                    continue;
                }

                if(partyMember == player){
                    continue;
                }

                boolean deathStatus = profileManager.getAnyProfile(partyMember).getIfDead();

                if(deathStatus){
                    continue;
                }

                double maxHp = profileManager.getAnyProfile(partyMember).getTotalHealth();

                changeResourceHandler.addHealthToEntity(partyMember, maxHp * .05, player);

            }
        }

        double maxHp = profileManager.getAnyProfile(player).getTotalHealth();
        changeResourceHandler.addHealthToEntity(player, maxHp * .05, player);

        double maxMp = profileManager.getAnyProfile(player).getTotalMana();
        changeResourceHandler.addManaToPlayer(player, maxMp * .05);

        LivingEntity target = targetManager.getPlayerTarget(player);

        Location spawnLoc = target.getLocation().subtract(0,1.9,0);

        ArmorStand armorStand = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack matrixItem = new ItemStack(Material.DRAGON_BREATH);
        ItemMeta meta = matrixItem.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(6);
        matrixItem.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(matrixItem);

        double skillDamage = 5;

        double skillLevel = profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level() +
                profileManager.getAnyProfile(player).getSkillLevels().getSkill_8_Level_Bonus();

        skillDamage = skillDamage + ((int)(skillLevel/10));

        if(cryomancer){
            skillDamage = skillDamage * 2;
        }

        if(conjurer){

            double maxMana = profileManager.getAnyProfile(player).getTotalMana();
            double currentMana = profileManager.getAnyProfile(player).getCurrentMana();

            double percent = maxMana/currentMana;

            skillDamage = skillDamage * (1 + percent);
        }


        double finalSkillDamage = skillDamage;
        new BukkitRunnable(){
            int ran = 0;
            Vector initialDirection;
            double angle = 0;
            @Override
            public void run(){


                Location targetLoc = target.getLocation();

                if (initialDirection == null) {
                    initialDirection = targetLoc.getDirection().setY(0).normalize();
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);
                direction.rotateAroundY(radians);


                if(!targetStillValid(target)){
                    cancelTask();
                    return;
                }

                targetLoc = targetLoc.subtract(0,1.5,0);

                targetLoc.setDirection(direction);

                armorStand.teleport(targetLoc);

                if(ran%20 == 0){
                    //tick damage

                    boolean crit = damageCalculator.checkIfCrit(player, 0);
                    double damage = (damageCalculator.calculateDamage(player, target, "Magical", finalSkillDamage, crit));
                    Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, player));
                    changeResourceHandler.subtractHealthFromEntity(target, damage, player);
                }


                angle += 10; // adjust the rotation speed here
                if (angle >= 360) {
                    angle = 0;
                }

                ran++;

                if(ran >= 100){
                    cancelTask();

                    Set<LivingEntity> hitBySkill = new HashSet<>();

                    BoundingBox hitBox = new BoundingBox(
                            target.getLocation().getX() - 4,
                            target.getLocation().getY() - 2,
                            target.getLocation().getZ() - 4,
                            target.getLocation().getX() + 4,
                            target.getLocation().getY() + 4,
                            target.getLocation().getZ() + 4
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
                        double damage = (damageCalculator.calculateDamage(player, livingEntity, "Magical", finalSkillDamage * 3, crit));

                        //pvp logic
                        if(entity instanceof Player){
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                            continue;
                        }

                        if(pveChecker.pveLogic(livingEntity)){
                            Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(livingEntity, player));
                            changeResourceHandler.subtractHealthFromEntity(livingEntity, damage, player);
                        }

                    }

                }
            }

            private boolean targetStillValid(LivingEntity target){

                if(target instanceof Player){

                    if(!((Player) target).isOnline()){
                        return false;
                    }

                }

                return !target.isDead();
            }

            private void cancelTask() {
                this.cancel();
                armorStand.remove();
            }

        }.runTaskTimer(main, 0, 1);

    }

    public int getCooldown(Player player){

        int cooldown = abilityReadyInMap.getOrDefault(player.getUniqueId(), 0);

        if(cooldown < 0){
            cooldown = 0;
        }

        return cooldown;
    }

}
