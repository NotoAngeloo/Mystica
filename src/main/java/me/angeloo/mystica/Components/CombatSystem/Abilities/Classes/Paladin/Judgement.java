package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Paladin;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.PaladinAbilities;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.PlayerStateManager;
import me.angeloo.mystica.Components.CombatSystem.AggroManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.FakePlayerTargetManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.SkillOnEnemyEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.DamageUtils.ChangeResourceHandler;
import me.angeloo.mystica.Utility.DamageUtils.DamageCalculator;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Judgement extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final FakePlayerTargetManager fakePlayerTargetManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final DamageCalculator damageCalculator;
    private final StatusEffectManager statusEffectManager;
    private final ChangeResourceHandler changeResourceHandler;
    private final AggroManager aggroManager;
    private final CooldownManager cooldownManager;
    private final PlayerStateManager playerStateManager;

    private final Purity purity;


    public Judgement(Mystica main, AbilityManager manager){
        super("judgement");
        this.main = main;
        profileManager = main.getProfileManager();
        fakePlayerTargetManager = main.getFakePlayerTargetManager();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        damageCalculator = main.getDamageCalculator();
        statusEffectManager = main.getStatusEffectManager();
        changeResourceHandler = main.getChangeResourceHandler();
        aggroManager = main.getAggroManager();
        cooldownManager = manager.getCooldownManager();
        purity = manager.getPurity();
        playerStateManager = manager.getPlayerStateManager();
    }

    private final int baseCooldown = 10;
    private final int baseDamage = 30;

    @Override
    public void use(LivingEntity caster){


        if(!usable(caster)){
            return;
        }

        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(target == null){
            target = caster;
        }

        if(profileManager.getAnyProfile(target).getIfDead()){
            target = caster;
        }

        execute(caster, target);

        if(profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Dawn)){
            purity.add(caster, 8);
        }

        cooldownManager.start(caster.getUniqueId(), 8, (long) (baseCooldown * 1000));

    }

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        boolean templar = profileManager.getAnyProfile(caster).getPlayerSubclass().equals(SubClass.Templar);

        Location start = target.getLocation();

        ArmorStand armorStand = caster.getWorld().spawn(start.clone(), ArmorStand.class);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);

        EntityEquipment entityEquipment = armorStand.getEquipment();

        ItemStack item = new ItemStack(Material.SUGAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(8);
        item.setItemMeta(meta);
        assert entityEquipment != null;
        entityEquipment.setHelmet(item);



        double finalSkillDamage = getSkillDamage(caster);
        new BukkitRunnable(){
            double down = 0;
            @Override
            public void run(){


                double increment = (2 * Math.PI) / 16; // angle between particles

                for (int i = 0; i < 16; i++) {
                    double angle = i * increment;
                    double x = start.getX() + (1 * Math.cos(angle));
                    double y = start.clone().add(0,7-down,0).getY();
                    double z = start.getZ() + (1 * Math.sin(angle));
                    Location loc = new Location(start.getWorld(), x, y, z);

                    caster.getWorld().spawnParticle(Particle.FLAME, loc, 1,0, 0, 0, 0);
                }

                if(down>=7){
                    this.cancel();
                    armorStand.remove();
                    doSomething(target);
                }

                down+=0.7;
            }

            private void doSomething(LivingEntity target){

                if(checkValid(target)){
                    return;
                }

                boolean crit = damageCalculator.checkIfCrit(caster, 0);

                if(target instanceof Player){

                    if(!pvpManager.pvpLogic(caster, (Player) target)){

                        double healPower = 5;
                        double healAmount = damageCalculator.calculateHealing(caster, healPower, crit);
                        changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                        return;
                    }

                }

                if(!(target instanceof Player)){
                    if(!pveChecker.pveLogic(target)){
                        double healPower = 5;
                        double healAmount = damageCalculator.calculateHealing(caster, healPower, crit);
                        changeResourceHandler.addHealthToEntity(target, healAmount, caster);
                        return;
                    }
                }

                double damage = damageCalculator.calculateDamage(caster, target, "Physical", finalSkillDamage, crit);
                damage = damage * decisionMultiplier(caster);


                Bukkit.getServer().getPluginManager().callEvent(new SkillOnEnemyEvent(target, caster));
                changeResourceHandler.subtractHealthFromEntity(target, damage, caster, crit);

                if(templar){
                    aggroManager.setAsHighPriorityTarget(target, caster);

                    if(target instanceof Player){

                        targetManager.setPlayerTarget((Player)target, caster);
                        return;
                    }
                }

                playerStateManager.get(caster.getUniqueId()).remove("decision");
            }

            private boolean checkValid(LivingEntity target){

                if(target instanceof Player){
                    if(((Player)target).isOnline()){
                        return true;
                    }

                    if(profileManager.getAnyProfile(target).getIfDead()){
                        return true;
                    }
                }

                return target.isDead();
            }

        }.runTaskTimer(main, 0, 1);
    }

    private double decisionMultiplier(LivingEntity caster){

        if(playerStateManager.get(caster.getUniqueId()).has("decision")){
            return 1.8;
        }

        return 1;
    }



    public double getSkillDamage(LivingEntity caster){
        double skillLevel = profileManager.getAnyProfile(caster).getSkillLevels().getSkillLevel(profileManager.getAnyProfile(caster).getStats().getLevel()) +
                profileManager.getAnyProfile(caster).getSkillLevels().getSkill_5_Level_Bonus();

        double damage = baseDamage + ((int)(skillLevel/3));

        if(purity.active(caster)){
            damage = damage * 3;
            purity.reset(caster);
        }

        return damage;
    }

    @Override
    public boolean usable(LivingEntity caster){
        double baseRange = 15;
        double extraRange = statusEffectManager.getAdditionalRange(caster);
        double totalRange = baseRange + extraRange;

        LivingEntity target;

        if(caster instanceof Player){
            target = targetManager.getPlayerTarget(caster);
        }
        else{
            target = fakePlayerTargetManager.getTarget(caster);
        }


        if(target != null){

            double distance = caster.getLocation().distance(target.getLocation());

            if(distance > totalRange){
                return false;
            }


        }

        return cooldownManager.isReady(caster.getUniqueId(), 8, statusEffectManager.getHastePercent(caster));
    }


}
