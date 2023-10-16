package me.angeloo.mystica.Managers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.angeloo.mystica.Components.ProfileComponents.NonPlayerStuff.Yield;
import me.angeloo.mystica.CustomEvents.HealthChangeEvent;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Components.NonPlayerProfile;
import me.angeloo.mystica.Components.PlayerProfile;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.ProfileComponents.*;
import me.angeloo.mystica.Utility.DisplayWeapons;
import me.angeloo.mystica.Utility.ProfileFileWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class ProfileManager {

    private final Mystica main;

    private final File dataFolder;
    private final ProfileFileWriter profileFileWriter;

    private final Map<UUID, PlayerProfile> playerProfiles = new HashMap<>();
    private final Map<UUID, NonPlayerProfile> nonPlayerProfiles = new HashMap<>();

    private final Map<String, Player> playerNameMap = new HashMap<>();
    private final Map<UUID, String> nonPlayerNameMap = new HashMap<>();
    private final Map<UUID, Location> bossHomes = new HashMap<>();

    private final Map<Player, String> classTrialMap = new HashMap<>();

    public ProfileManager(Mystica main) {
        this.main = main;
        dataFolder = main.getDataFolder();
        profileFileWriter = main.getProfileFileWriter();
    }

    public void saveProfilesToConfig(){
        for(UUID uuid : playerProfiles.keySet()) {
            String id = uuid.toString();
            Profile profile = playerProfiles.get(uuid);
            Stats stats = profile.getStats();
            StatsFromGear gearStats = profile.getGearStats();
            Points points = profile.getPoints();

            String playerClass = profile.getPlayerClass();
            String playerSubclass = profile.getPlayerSubclass();

            ItemStack[] savedInv = profile.getSavedInv();

            PlayerBag playerBag = profile.getPlayerBag();
            PlayerEquipment playerEquipment = profile.getPlayerEquipment();

            Skill_Level skillLevel = profile.getSkillLevels();
            EquipSkills equipSkills = profile.getEquipSkills();

            PlayerBossLevel playerBossLevel = profile.getPlayerBossLevel();

            String playername = Bukkit.getOfflinePlayer(uuid).getName();

            YamlConfiguration config = profileFileWriter.createOrLoadProfileFile(uuid);

            config.set(id + ".name", playername);

            config.set(id + ".points.talentpoints", points.getTalentPoints());
            config.set(id + ".points.bal", points.getBal());

            config.set(id + ".stats.level", stats.getLevel());
            config.set(id + ".stats.atk", stats.getAttack());
            config.set(id + ".stats.mag", stats.getMagic());
            config.set(id + ".stats.hp", stats.getHealth());
            config.set(id + ".stats.mana", stats.getMana());
            config.set(id + ".stats.regen", stats.getRegen());
            config.set(id + ".stats.mana_regen", stats.getMana_Regen());
            config.set(id + ".stats.def", stats.getDefense());
            config.set(id + ".stats.mag_def", stats.getMagic_Defense());
            config.set(id + ".stats.crit", stats.getCrit());

            config.set(id + ".gstats.atk", gearStats.getAttack());
            config.set(id + ".gstats.mag", gearStats.getMagic());
            config.set(id + ".gstats.hp", gearStats.getHealth());
            config.set(id + ".gstats.mana", gearStats.getMana());
            config.set(id + ".gstats.regen", gearStats.getRegen());
            config.set(id + ".gstats.mana_regen", gearStats.getMana_Regen());
            config.set(id + ".gstats.def", gearStats.getDefense());
            config.set(id + ".gstats.mag_def", gearStats.getMagic_Defense());
            config.set(id + ".gstats.crit", gearStats.getCrit());

            config.set(id + ".class", playerClass);
            config.set(id + ".subclass", playerSubclass);

            config.set(id + ".savedInv", savedInv);

            //bag
            config.set(id + ".bag", playerBag.getItems());
            config.set(id + ".bagUnlocks", playerBag.getNumUnlocks());

            config.set(id + ".equipment", playerEquipment.getEquipment());

            config.set(id + ".skill_level.skill1", skillLevel.getSkill_1_Level());
            config.set(id + ".skill_level.skill2", skillLevel.getSkill_2_Level());
            config.set(id + ".skill_level.skill3", skillLevel.getSkill_3_Level());
            config.set(id + ".skill_level.skill4", skillLevel.getSkill_4_Level());
            config.set(id + ".skill_level.skill5", skillLevel.getSkill_5_Level());
            config.set(id + ".skill_level.skill6", skillLevel.getSkill_6_Level());
            config.set(id + ".skill_level.skill7", skillLevel.getSkill_7_Level());
            config.set(id + ".skill_level.skill8", skillLevel.getSkill_8_Level());

            config.set(id + ".skill_level.skill1_bonus", skillLevel.getSkill_1_Level_Bonus());
            config.set(id + ".skill_level.skill2_bonus", skillLevel.getSkill_2_Level_Bonus());
            config.set(id + ".skill_level.skill3_bonus", skillLevel.getSkill_3_Level_Bonus());
            config.set(id + ".skill_level.skill4_bonus", skillLevel.getSkill_4_Level_Bonus());
            config.set(id + ".skill_level.skill5_bonus", skillLevel.getSkill_5_Level_Bonus());
            config.set(id + ".skill_level.skill6_bonus", skillLevel.getSkill_6_Level_Bonus());
            config.set(id + ".skill_level.skill7_bonus", skillLevel.getSkill_7_Level_Bonus());
            config.set(id + ".skill_level.skill8_bonus", skillLevel.getSkill_8_Level_Bonus());

            config.set(id + ".skill_slots.0", equipSkills.getAnySlot()[0]);
            config.set(id + ".skill_slots.1", equipSkills.getAnySlot()[1]);
            config.set(id + ".skill_slots.2", equipSkills.getAnySlot()[2]);
            config.set(id + ".skill_slots.3", equipSkills.getAnySlot()[3]);
            config.set(id + ".skill_slots.4", equipSkills.getAnySlot()[4]);
            config.set(id + ".skill_slots.5", equipSkills.getAnySlot()[5]);
            config.set(id + ".skill_slots.6", equipSkills.getAnySlot()[6]);
            config.set(id + ".skill_slots.7", equipSkills.getAnySlot()[7]);

            config.set(id + ".boss_level", playerBossLevel.getBossLevel());

            profileFileWriter.saveProfileFile(uuid, config);
        }
    }

    public void loadProfilesFromConfig(){

        File[] profileFiles = dataFolder.listFiles();

        if(profileFiles != null){
            for(File file : profileFiles){

                if(file.isFile() && file.getName().endsWith(".yml")){
                    String fileName = file.getName();
                    String uuidString = fileName.replace(".yml", "");

                    UUID id;

                    try{
                        id = UUID.fromString(uuidString);
                    } catch (IllegalArgumentException exception){
                        continue;
                    }

                    YamlConfiguration config = profileFileWriter.createOrLoadProfileFile(id);

                    //player points
                    int skillpoints = config.getInt(id + ".points.talentpoints");
                    int bal = config.getInt(id + ".points.bal");
                    Points points = new Points(skillpoints, bal);

                    //stats
                    int level = config.getInt(id + ".stats.level");
                    int atk = config.getInt(id + ".stats.atk");
                    int mag = config.getInt(id + ".stats.mag");
                    int hp = config.getInt(id + ".stats.hp");
                    int mana = config.getInt(id + ".stats.mana");
                    int regen = config.getInt(id + ".stats.regen");
                    int mregen = config.getInt(id + ".stats.mana_regen");
                    int def = config.getInt(id + ".stats.def");
                    int mdef = config.getInt(id + ".stats.mag_def");
                    int crit = config.getInt(id + ".stats.crit");

                    Stats stats = new Stats(level, atk, mag, hp, mana, regen, mregen, def, mdef, crit);

                    //gearstats
                    int gatk = config.getInt(id + ".gstats.atk");
                    int gmag = config.getInt(id + ".gstats.mag");
                    int ghp = config.getInt(id + ".gstats.hp");
                    int gmana = config.getInt(id + ".gstats.mana");
                    int gregen = config.getInt(id + ".gstats.regen");
                    int gmregen = config.getInt(id + ".gstats.mana_regen");
                    int gdef = config.getInt(id + ".gstats.def");
                    int gmdef = config.getInt(id + ".gstats.mag_def");
                    int gcrit = config.getInt(id + ".gstats.crit");

                    StatsFromGear gearStats = new StatsFromGear(gatk, gmag, ghp, gmana, gregen, gmregen, gdef, gmdef, gcrit);

                    int currentHealth = hp + ghp;
                    int currentMana = mana + gmana;

                    String playerClass = config.getString(id + ".class");
                    String playerSubclass = config.getString(id + ".subclass");

                    ItemStack[] savedInv = ((List<ItemStack>) config.get(id + ".savedInv")).toArray(new ItemStack[41]);

                    ArrayList<ItemStack> items = (ArrayList<ItemStack>) config.getList(id + ".bag", new ArrayList<ItemStack>());
                    int bagUnlocks = config.getInt(id + ".bagUnlocks");
                    PlayerBag playerBag = new PlayerBag(items, bagUnlocks);

                    //equipment
                    ItemStack[] equipment = ((List<ItemStack>) config.get(id + ".equipment")).toArray(new ItemStack[6]);
                    PlayerEquipment playerEquipment = new PlayerEquipment(equipment);

                    int skill1 = config.getInt(id + ".skill_level.skill1");
                    int skill2 = config.getInt(id + ".skill_level.skill2");
                    int skill3 = config.getInt(id + ".skill_level.skill3");
                    int skill4 = config.getInt(id + ".skill_level.skill4");
                    int skill5 = config.getInt(id + ".skill_level.skill5");
                    int skill6 = config.getInt(id + ".skill_level.skill6");
                    int skill7 = config.getInt(id + ".skill_level.skill7");
                    int skill8 = config.getInt(id + ".skill_level.skill8");

                    int skill1_bonus = config.getInt(id + ".skill_level.skill1_bonus");
                    int skill2_bonus = config.getInt(id + ".skill_level.skill2_bonus");
                    int skill3_bonus = config.getInt(id + ".skill_level.skill3_bonus");
                    int skill4_bonus = config.getInt(id + ".skill_level.skill4_bonus");
                    int skill5_bonus = config.getInt(id + ".skill_level.skill5_bonus");
                    int skill6_bonus = config.getInt(id + ".skill_level.skill6_bonus");
                    int skill7_bonus = config.getInt(id + ".skill_level.skill7_bonus");
                    int skill8_bonus = config.getInt(id + ".skill_level.skill8_bonus");

                    //instead of this, get every individual skill
                    int[]slots = new int[8];
                    slots[0] = config.getInt(id + ".skill_slots.0");
                    slots[1] = config.getInt(id + ".skill_slots.1");
                    slots[2] = config.getInt(id + ".skill_slots.2");
                    slots[3] = config.getInt(id + ".skill_slots.3");
                    slots[4] = config.getInt(id + ".skill_slots.4");
                    slots[5] = config.getInt(id + ".skill_slots.5");
                    slots[6] = config.getInt(id + ".skill_slots.6");
                    slots[7] = config.getInt(id + ".skill_slots.7");

                    EquipSkills equipSkills = new EquipSkills(slots);

                    Skill_Level skillLevel = new Skill_Level(skill1, skill2, skill3, skill4, skill5, skill6, skill7, skill8,
                            skill1_bonus, skill2_bonus, skill3_bonus, skill4_bonus, skill5_bonus, skill6_bonus, skill7_bonus, skill8_bonus);

                    int bossLevel = config.getInt(id + ".boss_level");

                    PlayerBossLevel playerBossLevel = new PlayerBossLevel(bossLevel);
                    PlayerProfile profile = new PlayerProfile(false, false, currentHealth, currentMana,
                            stats,
                            gearStats,
                            points,
                            playerClass,
                            playerSubclass,
                            savedInv,
                            playerBag,
                            playerEquipment,
                            skillLevel,
                            equipSkills,
                            playerBossLevel) {

                        @Override
                        public Boolean getIsPassive() {
                            return false;
                        }

                        @Override
                        public Boolean getIsMovable() {
                            return false;
                        }

                        @Override
                        public void setIsMovable(Boolean which) {

                        }


                        @Override
                        public Boolean getImmortality() {
                            return null;
                        }

                        @Override
                        public Boolean getIfObject() {
                            return false;
                        }

                        @Override
                        public Yield getYield() {
                            return null;
                        }

                        @Override
                        public void setImmortality(boolean immortality) {

                        }
                    };

                    playerProfiles.put(id, profile);

                }

            }
        }
    }

    public Profile getAnyProfile(LivingEntity entity){

        if(entity instanceof Player){
            return getPlayerProfile(entity.getUniqueId());
        }

        CreaturesAndCharactersManager creaturesAndCharactersManager = new CreaturesAndCharactersManager(main);

        if(nonPlayerProfiles.get(entity.getUniqueId()) == null){

            if(nonPlayerNameMap.containsKey(entity.getUniqueId())){
                String name = nonPlayerNameMap.get(entity.getUniqueId());

                creaturesAndCharactersManager.makeNpcProfile(name, entity.getUniqueId());
            }
            else{
                createNewDefaultNonPlayerProfile(entity.getUniqueId());
            }


        }

        return nonPlayerProfiles.get(entity.getUniqueId());
    }


    private Profile getPlayerProfile(UUID uuid){
        //returns null if doesnt exist

        if(playerProfiles.get(uuid) == null){
            createNewPlayerProfile(uuid);
        }

        return playerProfiles.get(uuid);
    }


    public void createNewPlayerProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,20,20,1,1,5,5, 1);
        StatsFromGear gearStats = new StatsFromGear( 0, 0,0,0,0,0,0,0,0);

        Points points = new Points(1, 0);

        int currentHealth = 20;
        int currentMana = 20;

        PlayerBag playerBag = new PlayerBag(new ArrayList<>(), 0);
        PlayerEquipment playerEquipment = new PlayerEquipment(new ItemStack[6]);

        Skill_Level skillLevel = new Skill_Level(1,1,1,1,1,1,1,1,
                0,0,0,0,0,0,0,0);

        EquipSkills equipSkills = new EquipSkills(new int[8]);

        PlayerBossLevel playerBossLevel = new PlayerBossLevel(1);

        PlayerProfile profile = new PlayerProfile(false, false, currentHealth, currentMana,
                stats,
                gearStats,
                points,
                "None",
                "None",
                new ItemStack[41],
                playerBag,
                playerEquipment,
                skillLevel,
                equipSkills,
                playerBossLevel) {


            @Override
            public Boolean getIsPassive() {
                return false;
            }

            @Override
            public Boolean getIsMovable() {
                return true;
            }

            @Override
            public void setIsMovable(Boolean which) {

            }

            @Override
            public Boolean getImmortality() {
                return false;
            }

            @Override
            public void setImmortality(boolean immortality) {

            }

            @Override
            public Boolean getIfObject() {
                return false;
            }

            @Override
            public Yield getYield() {
                return null;
            }
        };
        playerProfiles.put(uuid, profile);

        Player newPlayer = Bukkit.getPlayer(uuid);
        assert newPlayer != null;
        newPlayer.setLevel(1);
        newPlayer.setExp(0);

        AttributeInstance maxHealthAttribute = newPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealthAttribute != null;
        maxHealthAttribute.setBaseValue(20);

        World world = Bukkit.getWorld("world");
        Location spawnLoc = new Location(world,74,100,-357,90,7);

        newPlayer.teleport(spawnLoc);
        newPlayer.getWorld().setSpawnLocation(spawnLoc);
        newPlayer.getInventory().clear();
        newPlayer.sendMessage("You are playing a pre-release\nYour items and progress are subjected to being removed");

    }

    public void createNewDefaultNonPlayerProfile(UUID uuid){
        Stats stats = new Stats(1,1,1,1,0,0,0,1,1,0);
        double currentHealth = 1;
        Yield yield = new Yield(0, null);
        NonPlayerProfile nonPlayerProfile = new NonPlayerProfile(currentHealth, stats, true, false, false, false, yield) {
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
            public StatsFromGear getGearStats() {
                return null;
            }


            @Override
            public int getTotalHealth() {
                return getStats().getHealth();
            }

            @Override
            public int getTotalMana() {
                return getStats().getMana();
            }

            @Override
            public int getTotalAttack() {
                return getStats().getAttack();
            }

            @Override
            public int getTotalMagic() {
                return getStats().getMagic();
            }

            @Override
            public int getTotalDefense() {
                return getStats().getDefense();
            }

            @Override
            public int getTotalMagicDefense() {
                return getStats().getMagic_Defense();
            }

            @Override
            public int getTotalRegen() {
                return getStats().getRegen();
            }

            @Override
            public int getTotalManaRegen() {
                return getStats().getMana_Regen();
            }

            @Override
            public int getTotalCrit() {
                return getStats().getCrit();
            }

            @Override
            public Points getPoints() {
                return null;
            }

            @Override
            public void setGearStats(StatsFromGear statsFromGear) {

            }

            @Override
            public String getPlayerClass() {
                return "None";
            }

            @Override
            public void setPlayerClass(String playerClass) {

            }

            @Override
            public String getPlayerSubclass() {
                return "none";
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

        };
        addToNonPlayerProfiles(uuid, nonPlayerProfile);
    }

    public void addToNonPlayerProfiles(UUID uuid, NonPlayerProfile profile){
        nonPlayerProfiles.put(uuid, profile);
    }

    public Map<String, Player> getPlayerNameMap(){
        return playerNameMap;
    }
    public void addToPlayerNameMap(Player player){
        playerNameMap.put(player.getName(), player);
    }
    public boolean getIfEntityIsBoss(UUID uuid){return bossHomes.containsKey(uuid);}
    public void addToNonPlayerNameMap(String name, UUID id){
        nonPlayerNameMap.put(id, name);
    }

    public boolean getIfClassTrial(Player player){return classTrialMap.containsKey(player);}
    public String getTrialClass(Player player){
        if(getIfClassTrial(player)){
            return classTrialMap.get(player);
        }
        return "None";
    }
    public void removeClassTrial(Player player){
        classTrialMap.remove(player);
        DisplayWeapons displayWeapons = new DisplayWeapons(main);
        displayWeapons.displayWeapons(player);

    }
    public void setClassTrial(Player player, String clazz){classTrialMap.put(player, clazz);}

    public void setBossHome(UUID uuid){
        Entity entity = Bukkit.getEntity(uuid);

        LivingEntity boss = (LivingEntity) entity;

        assert boss != null;
        Location home = boss.getLocation();

        bossHomes.put(uuid, home);

    }


    public boolean resetBoss(UUID uuid){

        if(!bossHomes.containsKey(uuid)){
            return false;
        }


        Entity entity = Bukkit.getEntity(uuid);

        LivingEntity boss = (LivingEntity) entity;

        Location home = bossHomes.get(uuid);

        assert boss != null;

        AttributeInstance maxHealthAttribute = boss.getAttribute(Attribute.GENERIC_MAX_HEALTH);

        double currentHealth = boss.getHealth();

        assert maxHealthAttribute != null;

        double change = maxHealthAttribute.getBaseValue() - currentHealth;

        Bukkit.getServer().getPluginManager().callEvent(new HealthChangeEvent(boss, change, true));


        boss.setAI(false);
        boss.teleport(home);

        if(MythicBukkit.inst().getAPIHelper().isMythicMob(uuid)){

            AbstractEntity abstractEntity = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).getEntity();

            MythicBukkit.inst().getAPIHelper().getMythicMobInstance(boss).signalMob(abstractEntity, "reset");


        }

        return true;
    }

}
