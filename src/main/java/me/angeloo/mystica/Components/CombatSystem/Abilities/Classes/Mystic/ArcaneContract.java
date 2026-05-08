package me.angeloo.mystica.Components.CombatSystem.Abilities.Classes.Mystic;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BaseAbility;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.GravestoneManager;
import me.angeloo.mystica.Components.CombatSystem.PvpManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.CustomEvents.AiSignalEvent;
import me.angeloo.mystica.CustomEvents.PlayerRezByPlayerEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Logic.PveChecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ArcaneContract extends BaseAbility {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final TargetManager targetManager;
    private final PvpManager pvpManager;
    private final PveChecker pveChecker;
    private final GravestoneManager gravestoneManager;
    private final StatusEffectManager statusEffectManager;
    private final CooldownManager cooldownManager;
    private final Mana mana;


    public ArcaneContract(Mystica main, AbilityManager manager){
        super("arcane_contract");
        this.main = main;
        profileManager = main.getProfileManager();
        mysticaPartyManager = main.getMysticaPartyManager();
        mana = manager.getMana();
        targetManager = main.getTargetManager();
        pvpManager = main.getPvpManager();
        pveChecker = main.getPveChecker();
        statusEffectManager = main.getStatusEffectManager();
        cooldownManager = main.getCooldownManager();
        gravestoneManager = main.getGravestoneManager();
    }

    private final double range = 10;
    private final int cost = 100;
    private final int baseCooldown = 120;

    @Override
    public int cooldown() {
        return baseCooldown;
    }

    @Override
    public boolean use(LivingEntity caster){


        LivingEntity target = targetManager.getPlayerTarget(caster);

        if(!usable(caster, target)){
            return false;
        }

        mana.subTractManaFromEntity(caster, cost);

        List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(caster));
        for(LivingEntity member : mParty){
            putOnCooldown(member.getUniqueId());
        }

        if(gravestoneManager.isGravestone(target)){
            target = gravestoneManager.getPlayer(target);
        }

        execute(caster, target);

        return true;
    }

    private void execute(LivingEntity caster, LivingEntity target){

        //create custom event
        //deathManager.playerNowLive(target, true, caster);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerRezByPlayerEvent(target, caster));
        Bukkit.getServer().getPluginManager().callEvent(new AiSignalEvent(target, "reset"));

        new BukkitRunnable(){
            double height = 0;
            final double radius = 1;
            double angle = 0;
            Vector initialDirection;

            @Override
            public void run(){
                Location playerLoc = target.getLocation();

                if (initialDirection == null) {
                    initialDirection = playerLoc.getDirection().setY(0).normalize();
                    initialDirection.rotateAroundY(Math.toRadians(-45));
                }

                Vector direction = initialDirection.clone();
                double radians = Math.toRadians(angle);

                direction.rotateAroundY(radians);

                double x = playerLoc.getX() + direction.getX() * radius;
                double z = playerLoc.getZ() + direction.getZ() * radius;

                Location particleLoc = new Location(target.getWorld(), x, target.getLocation().getY() + height, z);

                target.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLoc, 1, 0, 0, 0, 0);

                height += .05;
                angle += 11;
                if(height >= 2){
                    this.cancel();
                }
            }
        }.runTaskTimer(main, 0L, 1);

    }

    @Override
    public boolean usable(LivingEntity caster, LivingEntity target) {

        if(target == null){
            return false;
        }

        //Bukkit.getLogger().info("target not null");

        double distance = caster.getLocation().distance(target.getLocation());

        if(distance>range + statusEffectManager.getAdditionalRange(caster)){
            return false;
        }

        //Bukkit.getLogger().info("distance fine");

        //this is unaffected by haste.
        if(!cooldownManager.isReady(caster.getUniqueId(), 7, 0)){
            return false;
        }

        //Bukkit.getLogger().info("not on cooldown");

        if(mana.getCurrentMana(caster)<cost){
            return false;
        }

        //Bukkit.getLogger().info("has mana");

        //not gravestone
        if(!gravestoneManager.isGravestone(target)){

            //Bukkit.getLogger().info("not gravestone");

            if(profileManager.getAnyProfile(target).fakePlayer()){
                return false;
            }

            //Bukkit.getLogger().info("not fake player");

            return profileManager.getAnyProfile(target).getIfDead();
        }


        Player actualTarget = gravestoneManager.getPlayer(target);

        if(pvpManager.pvpLogic(caster, actualTarget)){
            return false;
        }


        return profileManager.getAnyProfile(actualTarget).getIfDead();
    }

    private void putOnCooldown(UUID id){
        cooldownManager.start(id, 7, (long) (baseCooldown * 1000));
    }


    @Override
    public void useAsCompanion(LivingEntity caster, LivingEntity target){

        //Bukkit.getLogger().info("using as companion");

        cooldownManager.start(caster.getUniqueId(), 7, (long) (baseCooldown * 1000));

        new BukkitRunnable(){

            @Override
            public void run(){

                Bukkit.getScheduler().runTask(main,()->{

                    List<LivingEntity> mParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(caster));
                    for(LivingEntity member : mParty){
                        putOnCooldown(member.getUniqueId());
                    }

                    if(target instanceof Player player){
                        player.sendMessage("Wings: Hey, be more careful!");
                    }


                    Bukkit.getServer().getPluginManager().callEvent(new PlayerRezByPlayerEvent(target, caster));
                    Bukkit.getServer().getPluginManager().callEvent(new AiSignalEvent(target, "reset"));

                    new BukkitRunnable(){
                        double height = 0;
                        final double radius = 1;
                        double angle = 0;
                        Vector initialDirection;

                        @Override
                        public void run(){
                            Location playerLoc = target.getLocation();

                            if (initialDirection == null) {
                                initialDirection = playerLoc.getDirection().setY(0).normalize();
                                initialDirection.rotateAroundY(Math.toRadians(-45));
                            }

                            Vector direction = initialDirection.clone();
                            double radians = Math.toRadians(angle);

                            direction.rotateAroundY(radians);

                            double x = playerLoc.getX() + direction.getX() * radius;
                            double z = playerLoc.getZ() + direction.getZ() * radius;

                            Location particleLoc = new Location(target.getWorld(), x, target.getLocation().getY() + height, z);

                            target.getWorld().spawnParticle(Particle.SPELL_WITCH, particleLoc, 1, 0, 0, 0, 0);

                            height += .05;
                            angle += 11;
                            if(height >= 2){
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(main, 0L, 1);
                });

            }

        }.runTaskLater(main, 60);

    }

    @Override
    public String skillBarIcon(LivingEntity entity) {

        if(mana.getCurrentMana(entity)<cost){
            return "\ue3d0";
        }

        return "\ue3cf";
    }
}
