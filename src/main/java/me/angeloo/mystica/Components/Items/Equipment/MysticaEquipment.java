package me.angeloo.mystica.Components.Items.Equipment;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.Items.MysticaItem;
import me.angeloo.mystica.Components.Items.MysticaItemFormat;
import me.angeloo.mystica.Utility.EquipmentSlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MysticaEquipment implements MysticaItem {

    /*
    model data convention
    1= mainhand
    2=offhand (sometimes nothing)
    3=sheathed
     */

    //assasin=pink
    //elementalist=cyan
    //mystic=purple
    //paladin=yellow
    //ranger=lime
    //shadow_knight=red
    //warrior=orange


    private final EquipmentSlot slot;

    private final PlayerClass playerClass;

    private final int level;
    private final int tier;

    private final List<StatRoll> statRolls;

    private final List<SkillRoll> skillRolls;

    public MysticaEquipment(
            EquipmentSlot slot,
            PlayerClass playerClass,
            int level,
            int tier,
            List<StatRoll> statRolls,
            List<SkillRoll> skillRolls
    ) {
        this.slot = slot;
        this.playerClass = playerClass;
        this.level = level;
        this.tier = tier;

        this.statRolls = List.copyOf(statRolls);
        this.skillRolls = List.copyOf(skillRolls);
    }

    @Override
    public String getId() {
        return playerClass.getDisplayName() + "_" + slot.name();
    }

    @Override
    public MysticaItemFormat format() {
        return MysticaItemFormat.EQUIPMENT;
    }

    @Override
    public boolean questItem() {
        return false;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public int getLevel() {
        return level;
    }

    public List<StatRoll> getStatRolls() {
        return statRolls;
    }

    public List<SkillRoll> getSkillRolls() {
        return skillRolls;
    }

    //return to this with better names later
    @Override
    public String getName() {
        return switch (slot) {
            case WEAPON -> switch (playerClass) {
                case WARRIOR -> "Axe";
                case PALADIN -> "Sword";
                case SHADOW_KNIGHT -> "GreatSword";
                case RANGER -> "Bow";
                case MYSTIC -> "Staff";
                case ELEMENTALIST -> "Catalyst";
                case ASSASSIN -> "Dagger";
                default -> "Weapon";
            };

            //this names are specific due to current optifine CIT, will change when i move from that
            case HEAD -> switch (playerClass) {
                case ASSASSIN -> "Assassin's Scarf"; //model data for later 7
                case ELEMENTALIST -> "Elementalist's Hood"; //1
                case MYSTIC -> "Mystic's Hood"; //3
                case RANGER -> "Ranger's Hood"; //2
                case PALADIN -> "Paladin's Helmet"; //5
                case WARRIOR -> "Warrior's Helmet"; //6
                case SHADOW_KNIGHT -> "Shadow Knight's Helmet"; //4
                default -> "Hat";
            };

            case CHEST -> switch (playerClass) {
                case ASSASSIN -> "Assassin's Tunic";
                case WARRIOR -> "Warrior's Plate";
                case PALADIN -> "Paladin's Plate";
                case RANGER -> "Ranger's Tunic";
                case ELEMENTALIST -> "Elementalist's Tunic";
                case MYSTIC -> "Mystic's Tunic";
                case SHADOW_KNIGHT -> "Shadow Knight's Plate";
                default -> "Shirt";
            };

            case LEGS -> switch (playerClass) {
                case MYSTIC -> "Mystic's Breeches";
                case ELEMENTALIST -> "Elementalist's Breeches";
                case PALADIN -> "Paladin's Breeches";
                case RANGER -> "Ranger's Breeches";
                case WARRIOR -> "Warrior's Breeches";
                case SHADOW_KNIGHT -> "Shadow Knight's Breeches";
                case ASSASSIN -> "Assassin's Breeches";
                default -> "Breeches";
            };

            case BOOTS -> switch (playerClass) {
                case ASSASSIN -> "Assassin's Boots";
                case WARRIOR -> "Warrior's Boots";
                case PALADIN -> "Paladin's Boots";
                case RANGER -> "Ranger's Boots";
                case ELEMENTALIST -> "Elementalist's Boots";
                case MYSTIC -> "Mystic's Boots";
                case SHADOW_KNIGHT -> "Shadow Knight's Boots";
                default -> "Boots";
            };
        };
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();

        map.put("id", getId());
        map.put("slot", slot.name());
        map.put("class", playerClass.name());
        map.put("level", level);
        map.put("tier", tier);
        map.put("format", format().name());

        // NEW: full roll lists
        map.put("statRolls", statRolls);
        map.put("skillRolls", skillRolls);

        return map;
    }

    public static MysticaEquipment deserialize(Map<String, Object> map) {

        EquipmentSlot slot =
                EquipmentSlot.valueOf((String) map.get("slot"));

        PlayerClass playerClass =
                PlayerClass.valueOf((String) map.get("class"));

        int level = (int) map.get("level");

        int tier = (int) map.get("tier");

        List<StatRoll> statRolls = (List<StatRoll>) map.getOrDefault("statRolls", List.of());

        List<SkillRoll> skillRolls = (List<SkillRoll>) map.getOrDefault("skillRolls", List.of());

        return new MysticaEquipment(
                slot,
                playerClass,
                level,
                tier,
                statRolls,
                skillRolls
        );
    }
}




/*public class MysticaEquipment extends MysticaItem{



    //cosmetics later

    private final EquipmentSlot equipmentSlot;
    private PlayerClass playerClass;
    private int level;

    private StatType highStat;
    private StatType lowStat;

    private List<Integer> skillOne;
    private List<Integer> skillTwo;


    //Tier 1
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level){
        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;
    }


    //Tier 2
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass,int level, StatType highStat, StatType lowStat){
        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;
        this.highStat = highStat;
        this.lowStat = lowStat;
    }

    //Tier 3
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass,int level,StatType highStat,StatType lowStat,List<Integer> skillOne,List<Integer> skillTwo){
        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;
        this.highStat = highStat;
        this.lowStat = lowStat;
        this.skillOne = skillOne;
        this.skillTwo = skillTwo;
    }

    //generate a completely random one
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level, int tier){

        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;

        if(tier == 1){
            return;
        }

        List<StatType> availableStats = new ArrayList<>();
        availableStats.add(StatType.Attack);
        availableStats.add(StatType.Health);
        availableStats.add(StatType.Defense);
        availableStats.add(StatType.Magic_Defense);
        availableStats.add(StatType.Crit);
        Collections.shuffle(availableStats);

        this.highStat = availableStats.get(0);
        this.lowStat = availableStats.get(1);

        if(tier == 2){
            return;
        }

        int statAmount = new Random().nextInt(5) + 1;
        int statAmount2 = new Random().nextInt(5) + 1;
        int skillNumber = new Random().nextInt(8) + 1;
        int skillNumber2 = new Random().nextInt(8) + 1;

        this.skillOne = List.of(skillNumber, statAmount);
        this.skillTwo = List.of(skillNumber2, statAmount2);
    }

    //generate one with random stat types
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level, int tier, List<Integer> skillOne, List<Integer> skillTwo){

        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;

        if(tier == 1){
            return;
        }

        List<StatType> availableStats = new ArrayList<>();
        availableStats.add(StatType.Attack);
        availableStats.add(StatType.Health);
        availableStats.add(StatType.Defense);
        availableStats.add(StatType.Magic_Defense);
        availableStats.add(StatType.Crit);
        Collections.shuffle(availableStats);

        this.highStat = availableStats.get(0);
        this.lowStat = availableStats.get(1);

        if(tier == 2){
            return;
        }


        this.skillOne = skillOne;
        this.skillTwo = skillTwo;
    }

    //generate one with random skill levels
    public MysticaEquipment(EquipmentSlot equipmentSlot, PlayerClass playerClass, int level, int tier, StatType highStat, StatType lowStat){

        this.equipmentSlot = equipmentSlot;
        this.playerClass = playerClass;
        this.level = level;

        if(tier == 1){
            return;
        }

        this.highStat = highStat;
        this.lowStat = lowStat;

        if(tier == 2){
            return;
        }

        int statAmount = new Random().nextInt(5) + 1;
        int statAmount2 = new Random().nextInt(5) + 1;
        int skillNumber = new Random().nextInt(8) + 1;
        int skillNumber2 = new Random().nextInt(8) + 1;

        this.skillOne = List.of(skillNumber, statAmount);
        this.skillTwo = List.of(skillNumber2, statAmount2);
    }




    @Override
    public MysticaItemFormat format() {
        return MysticaItemFormat.EQUIPMENT;
    }

    @Override
    public String identifier() {
        return this.equipmentSlot.name();
    }


    @Override
    public ItemStack build(){


        Material material = Material.KELP;
        PlayerClass playerClass = this.playerClass;
        EquipmentSlot equipmentSlot = this.equipmentSlot;
        int level = this.level;

        switch (equipmentSlot) {
            case HEAD -> {
                material = Material.CHAIN;
            }
            case CHEST -> {
                material = Material.CHAINMAIL_CHESTPLATE;
            }
            case LEGS -> {
                material = Material.CHAINMAIL_LEGGINGS;
            }
            case BOOTS -> {
                material = Material.CHAINMAIL_BOOTS;
            }
            case WEAPON -> {
                switch (playerClass){
                    case RANGER -> {
                        material = Material.FEATHER;
                    }
                    case ELEMENTALIST -> {
                        material = Material.STICK;
                    }
                    case MYSTIC -> {
                        material = Material.BLAZE_ROD;
                    }
                    case ASSASSIN -> {
                        material = Material.FLINT;
                    }
                    case SHADOW_KNIGHT -> {
                        material = Material.DIAMOND_SWORD;
                    }
                    case WARRIOR -> {
                        material = Material.BRICK;
                    }
                    case PALADIN -> {
                        material = Material.IRON_SWORD;
                    }
                    case NONE -> {
                    }
                }
            }
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;


        //look into this
        //meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        AttributeModifier zeroer = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage",
                0, AttributeModifier.Operation.ADD_NUMBER, org.bukkit.inventory.EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, zeroer);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        List<String> lores = new ArrayList<>();

        //name and model data
        switch (equipmentSlot){
            case WEAPON -> {
                switch (playerClass){
                    case ELEMENTALIST -> {
                        meta.setDisplayName(ChatColor.of(playerClass.getColor()) + "Catalyst");
                    }
                    case MYSTIC -> {
                        meta.setDisplayName(ChatColor.of(playerClass.getColor()) + "Staff");
                    }
                    case RANGER -> {
                        meta.setDisplayName(ChatColor.of(playerClass.getColor()) + "Bow");
                    }
                    case SHADOW_KNIGHT -> {
                        meta.setDisplayName(ChatColor.of(playerClass.getColor()) + "Greatsword");
                    }
                    case PALADIN -> {
                        meta.setDisplayName(ChatColor.of(playerClass.getColor()) + "Sword");
                    }
                    case WARRIOR -> {
                        meta.setDisplayName(ChatColor.of(playerClass.getColor()) + "Axe");
                    }
                    case ASSASSIN -> {
                        meta.setDisplayName(ChatColor.of(playerClass.getColor()) + "Dagger");
                    }

                }

                meta.setCustomModelData(1);
            }

            }
        }

        //TODO: gearscore tiers

        lores.add(ChatColor.of(menuColor) + "Level: " + this.level);
        lores.add("");

        //base stats
        switch (equipmentSlot){
            case WEAPON -> {
                lores.add(ChatColor.WHITE + "Attack + " + getWeaponBaseAttack(level));
                lores.add(ChatColor.WHITE + "Health + " + getWeaponBaseHealth(level));
                lores.add(ChatColor.WHITE + "Defense + " + getWeaponBaseDefense(level));
                lores.add(ChatColor.WHITE + "Magic Defense + " + getWeaponBaseDefense(level));
            }
            case HEAD -> {
                lores.add(ChatColor.WHITE + "Health + " + getHelmetBaseHealth(level));
            }
            case CHEST -> {
                lores.add(ChatColor.WHITE + "Health + " + getChestBaseHealth(level));
                lores.add(ChatColor.WHITE + "Defense + " + getChestBaseDefense(level));
                lores.add(ChatColor.WHITE + "Magic Defense + " + getChestBaseDefense(level));
            }
            case LEGS -> {
                lores.add(ChatColor.WHITE + "Attack + " + getLeggingBaseAttack(level));
            }
            case BOOTS -> {
                lores.add(ChatColor.WHITE + "Attack + " + getBootsBaseAttack(level));
            }
        }

        NamespacedKey key = new NamespacedKey(Mystica.getPlugin(), "equipment_data");
        Gson gson = new Gson();
        String json = gson.toJson(this);
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, json);

        //Bukkit.getLogger().info(json);

        if(this.highStat == null){

            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(uncommonColor) + this.highStat.toString().replaceAll("_"," ") + " + " + getHighStatAmount(this.highStat, level));
        lores.add(ChatColor.of(uncommonColor) + this.lowStat.toString().replaceAll("_"," ") + " + " + getLowStatAmount(this.lowStat, level));

        if(this.skillOne == null){
            meta.setLore(lores);
            item.setItemMeta(meta);
            return item;
        }

        lores.add(ChatColor.of(rareColor) + "Skill " + this.skillOne.get(0) + " + " + this.skillOne.get(1));
        lores.add(ChatColor.of(rareColor) + "Skill " + this.skillTwo.get(0) + " + " + this.skillTwo.get(1));

        //lores.add(ChatColor.of(rareColor) + "Skill " + this.skillOne.getKey() + " + " + this.skillOne.getValue());
        //lores.add(ChatColor.of(rareColor) + "Skill " + this.skillTwo.getKey() + " + " + this.skillTwo.getValue());

        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean questItem() {
        return false;
    }

    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> map = new HashMap<>();
        map.put("identifier",identifier());
        map.put("slot",equipmentSlot.name());
        map.put("class",playerClass.name());
        map.put("level",level);
        map.put("format", format().name());

        if(highStat != null){
            map.put("highstat",highStat.name());
        }

        if(lowStat != null){
            map.put("lowstat",lowStat.name());
        }

        if(skillOne != null){
            map.put("skill_roll_1", skillOne);
        }

        if(skillTwo != null){
            map.put("skill_roll_2", skillTwo);
        }


        return map;
    }

    public static MysticaEquipment deserialize(Map<String, Object> map){

        EquipmentSlot slot = EquipmentSlot.valueOf((String)map.get("slot"));
        PlayerClass playerClass = PlayerClass.valueOf((String) map.get("class"));
        int level = (int) map.get("level");
        StatType highstat = null;
        StatType lowstat = null;
        List<Integer> skillOne = null;
        List<Integer> skillTwo = null;


        if(map.containsKey("highstat")){
            highstat = StatType.valueOf((String) map.get("highstat"));
        }

        if(map.containsKey("lowstat")){
            lowstat = StatType.valueOf((String) map.get("lowstat"));
        }

        if(map.containsKey("skill_roll_1")){
            skillOne = (List<Integer>) map.get("skill_roll_1");
        }

        if(map.containsKey("skill_roll_2")){
            skillTwo = (List<Integer>) map.get("skill_roll_2");

        }

        if(highstat == null){
            return new MysticaEquipment(slot,playerClass,level);
        }

        if(skillOne == null){
            return new MysticaEquipment(slot,playerClass,level,highstat,lowstat);
        }


        return new MysticaEquipment(slot,playerClass,level,highstat,lowstat,skillOne,skillTwo);
    }


    public int getWeaponBaseAttack(int level){
        return 3 * level;
    }
    public int getWeaponBaseHealth(int level){
        return 18 * level;
    }
    public int getWeaponBaseDefense(int level){
        return 4 * level;
    }
    public int getHelmetBaseHealth(int level){
        return 50 * level;
    }
    public int getChestBaseHealth(int level){
        return 31 * level;
    }
    public int getChestBaseDefense(int level){
        return 4 * level;
    }
    public int getLeggingBaseAttack(int level){
        return 4 * level;
    }
    public int getBootsBaseAttack(int level){
        return 2 * level;
    }






    public EquipmentSlot getEquipmentSlot(){
        return this.equipmentSlot;
    }

    public PlayerClass getPlayerClass(){
        return this.playerClass;
    }

    public StatType getHighStat(){
        return this.highStat;
    }

    public StatType getLowStat(){
        return this.lowStat;
    }

    public List<Integer> getSkillOne(){
        return skillOne;
    }

    public List<Integer> getSkillTwo(){
        return skillTwo;
    }

    public void setLevel(int newLevel){
        this.level = newLevel;
    }

    public void setPlayerClass(PlayerClass playerClass){this.playerClass = playerClass;}*/

