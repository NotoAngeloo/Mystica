package me.angeloo.mystica.Components.Hud;

import me.angeloo.mystica.Components.Hud.Abilties.AbilityBarRenderer;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.EntityBehavior.AggroManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.GravestoneManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Hud.StatusEffects.StatusEffectRenderer;
import me.angeloo.mystica.Components.ProfileComponents.Profile;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.BossManager;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Logic.DamageBoardPlaceholders;
import me.angeloo.mystica.Utility.Enums.SubClass;
import me.angeloo.mystica.Utility.TextRenderer.CharGlyphAtlas;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static me.angeloo.mystica.Mystica.assassinColor;
import static me.angeloo.mystica.Mystica.mysticColor;
import static me.angeloo.mystica.Mystica.elementalistColor;
import static me.angeloo.mystica.Mystica.rangerColor;
import static me.angeloo.mystica.Mystica.paladinColor;
import static me.angeloo.mystica.Mystica.warriorColor;
import static me.angeloo.mystica.Mystica.shadowKnightColor;


public class HudManager {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final BossManager bossManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final DamageBoardPlaceholders damageBoardPlaceholders;
    private final BossWarningSender bossWarningSender;
    private final AggroManager aggroManager;


    private final AbilityManager abilityManager;
    private final StatusEffectManager statusEffectManager;
    private final GravestoneManager gravestoneManager;
    private final TargetManager targetManager;
    private final BossCastingManager bossCastingManager;
    private final AbilityBarRenderer abilityBarRenderer;
    private final StatusEffectRenderer statusEffectRenderer;

    private final SkinGrabber skinGrabber;

    //temp, maybe
    private final CharGlyphAtlas charGlyphAtlas;

    //this is updated when needed. when a player requests data about x entity, grabs it from here
    private final Map<UUID, String> entityBarData = new HashMap<>();


    private static final Map<Character, Integer> MINECRAFT_CHAR_WIDTHS = Map.<Character, Integer>ofEntries(
            Map.entry('!', 1),
            Map.entry('"', 4),
            Map.entry('#', 5),
            Map.entry('$', 5),
            Map.entry('%', 5),
            Map.entry('&', 5),
            Map.entry('\'', 2),
            Map.entry('(', 4),
            Map.entry(')', 4),
            Map.entry('*', 4),
            Map.entry('+', 5),
            Map.entry(',', 1),
            Map.entry('-', 5),
            Map.entry('.', 1),
            Map.entry('/', 5),
            Map.entry('0', 5),
            Map.entry('1', 5),
            Map.entry('2', 5),
            Map.entry('3', 5),
            Map.entry('4', 5),
            Map.entry('5', 5),
            Map.entry('6', 5),
            Map.entry('7', 5),
            Map.entry('8', 5),
            Map.entry('9', 5),
            Map.entry(':', 1),
            Map.entry(';', 1),
            Map.entry('<', 4),
            Map.entry('=', 5),
            Map.entry('>', 4),
            Map.entry('?', 5),
            Map.entry('@', 6),
            Map.entry('A', 5),
            Map.entry('B', 5),
            Map.entry('C', 5),
            Map.entry('D', 5),
            Map.entry('E', 5),
            Map.entry('F', 5),
            Map.entry('G', 5),
            Map.entry('H', 5),
            Map.entry('I', 3),
            Map.entry('J', 5),
            Map.entry('K', 5),
            Map.entry('L', 5),
            Map.entry('M', 5),
            Map.entry('N', 5),
            Map.entry('O', 5),
            Map.entry('P', 5),
            Map.entry('Q', 5),
            Map.entry('R', 5),
            Map.entry('S', 5),
            Map.entry('T', 5),
            Map.entry('U', 5),
            Map.entry('V', 5),
            Map.entry('W', 5),
            Map.entry('X', 5),
            Map.entry('Y', 5),
            Map.entry('Z', 5),
            Map.entry('[', 3),
            Map.entry('\\', 5),
            Map.entry(']', 3),
            Map.entry('^', 5),
            Map.entry('_', 5),
            Map.entry('`', 2),
            Map.entry('a', 5),
            Map.entry('b', 5),
            Map.entry('c', 5),
            Map.entry('d', 5),
            Map.entry('e', 5),
            Map.entry('f', 4),
            Map.entry('g', 5),
            Map.entry('h', 5),
            Map.entry('i', 1),
            Map.entry('j', 5),
            Map.entry('k', 4),
            Map.entry('l', 2),
            Map.entry('m', 5),
            Map.entry('n', 5),
            Map.entry('o', 5),
            Map.entry('p', 5),
            Map.entry('q', 5),
            Map.entry('r', 5),
            Map.entry('s', 5),
            Map.entry('t', 3),
            Map.entry('u', 5),
            Map.entry('v', 5),
            Map.entry('w', 5),
            Map.entry('x', 5),
            Map.entry('y', 5),
            Map.entry('z', 5),
            Map.entry('{', 4),
            Map.entry('|', 1),
            Map.entry('}', 4),
            Map.entry('~', 6),
            Map.entry('¡', 1),
            Map.entry('¢', 3),
            Map.entry('£', 5),
            Map.entry('¤', 3),
            Map.entry('¥', 3),
            Map.entry('¦', 0),
            Map.entry('§', -2),
            Map.entry('¨', 2),
            Map.entry('©', 4),
            Map.entry('ª', 5),
            Map.entry('«', 5),
            Map.entry('¬', 5),
            Map.entry('®', 6),
            Map.entry('¯', 3),
            Map.entry('°', 6),
            Map.entry('±', 6),
            Map.entry('²', 5),
            Map.entry('³', 2),
            Map.entry('´', 1),
            Map.entry('µ', 2),
            Map.entry('¶', 3),
            Map.entry('·', 5),
            Map.entry('¸', 1),
            Map.entry('¹', 1),
            Map.entry('º', 5),
            Map.entry('»', 5),
            Map.entry('¼', 5),
            Map.entry('½', 5),
            Map.entry('¾', 3),
            Map.entry('¿', 5),
            Map.entry('À', 5),
            Map.entry('Á', 5),
            Map.entry('Â', 5),
            Map.entry('Ã', 3),
            Map.entry('Ä', 5),
            Map.entry('Å', 5),
            Map.entry('Æ', 5),
            Map.entry('Ç', 5),
            Map.entry('È', 5),
            Map.entry('É', 5),
            Map.entry('Ê', 5),
            Map.entry('Ë', 5),
            Map.entry('Ì', 2),
            Map.entry('Í', 3),
            Map.entry('Î', 2),
            Map.entry('Ï', 2),
            Map.entry('Ð', 3),
            Map.entry('Ñ', 5),
            Map.entry('Ò', 3),
            Map.entry('Ó', 5),
            Map.entry('Ô', 5),
            Map.entry('Õ', 5),
            Map.entry('Ö', 5),
            Map.entry('×', 3),
            Map.entry('Ø', 5),
            Map.entry('Ù', 3),
            Map.entry('Ú', 5),
            Map.entry('Û', 3),
            Map.entry('Ü', 5),
            Map.entry('Ý', 3),
            Map.entry('Þ', 3),
            Map.entry('ß', 5),
            Map.entry('à', 5),
            Map.entry('á', 5),
            Map.entry('â', 5),
            Map.entry('ã', 5),
            Map.entry('ä', 5),
            Map.entry('å', 5),
            Map.entry('æ', 5),
            Map.entry('ç', 5),
            Map.entry('è', 5),
            Map.entry('é', 5),
            Map.entry('ê', 5),
            Map.entry('ë', 5),
            Map.entry('ì', 2),
            Map.entry('í', 2),
            Map.entry('î', 5),
            Map.entry('ï', 3),
            Map.entry('ð', 3),
            Map.entry('ñ', 5),
            Map.entry('ò', 5),
            Map.entry('ó', 5),
            Map.entry('ô', 5),
            Map.entry('õ', 5),
            Map.entry('ö', 5),
            Map.entry('÷', 6),
            Map.entry('ø', 5),
            Map.entry('ù', 5),
            Map.entry('ú', 5),
            Map.entry('û', 5),
            Map.entry('ü', 5),
            Map.entry('ý', 3),
            Map.entry('þ', 2)
    );

    private static final int[] PIXELS = {64, 32, 16, 8, 4, 2, 1};

    private static final String[] GLYPHS = {
            "\uF82B",
            "\uF82A",
            "\uF829",
            "\uF828",
            "\uF824",
            "\uF822",
            "\uF821"
    };


    //action bar
    private final String actionBarResourceBackground = "\uE000";

    //0-40 (action bar)
    private final String[] actionBarResource = {"\uE029","\uE028","\uE027","\uE026","\uE025","\uE024","\uE023","\uE022","\uE021","\uE020","\uE01F","\uE01E","\uE01D","\uE01C","\uE01B","\uE01A","\uE019","\uE018","\uE017","\uE016","\uE015","\uE014","\uE013","\uE012","\uE011","\uE010","\uE00F","\uE00E","\uE00D","\uE00C","\uE00B","\uE00A","\uE009","\uE008","\uE007","\uE006","\uE005","\uE004","\uE003","\uE002","\uE001"};

    private final String[] comboResource4 = {"\uE02a","\uE02b","\uE02c","\uE02d","\uE02e"};

    private final String[] comboResource5 = {"\uE02f","\uE030","\uE031","\uE032","\uE033","\uE034"};

    private final Map<UUID, String> healthBarCache = new ConcurrentHashMap<>();

    private final Map<UUID, String> resourceBarCache = new ConcurrentHashMap<>();

    //abilities
    private final Map<UUID, String> abilityBarCache = new ConcurrentHashMap<>();

    //casting

    private final String playerCastBarBackground = "\ue1ef";
    private final String[] playerCastBar = {"\uE218","\uE217","\uE216","\uE215","\uE214","\uE213","\uE212","\uE211","\uE210","\uE20F","\uE20E","\uE20D","\uE20C","\uE20B","\uE20A","\uE209","\uE208","\uE207","\uE206","\uE205","\uE204","\uE203","\uE202","\uE201","\uE200","\uE1FF","\uE1FE","\uE1FD","\uE1FC","\uE1FB","\uE1FA","\uE1F9","\uE1F8","\uE1F7","\uE1F6","\uE1F5","\uE1F4","\uE1F3","\uE1F2","\uE1F1","\uE1F0"};

    private final Map<UUID, String> castBarCache = new ConcurrentHashMap<>();


    //targeting
    private final String bossResourceBackground = "\uE035";
    //0-40, boss
    private final String[] bossBarResource = {"\uE05E","\uE05D","\uE05C","\uE05B","\uE05A","\uE059","\uE058","\uE057","\uE056","\uE055","\uE054","\uE053","\uE052","\uE051","\uE050","\uE04F","\uE04E","\uE04D","\uE04C","\uE04B","\uE04A","\uE049","\uE048","\uE047","\uE046","\uE045","\uE044","\uE043","\uE042","\uE041","\uE040","\uE03F","\uE03E","\uE03D","\uE03C","\uE03B","\uE03A","\uE039","\uE038","\uE037","\uE036"};
    //0-40 boss cast and also fury
    private final String[] bossCastBar = {"\uE087","\uE086","\uE085","\uE084","\uE083","\uE082","\uE081","\uE080","\uE07F","\uE07E","\uE07D","\uE07C","\uE07B","\uE07A","\uE079","\uE078","\uE077","\uE076","\uE075","\uE074","\uE073","\uE072","\uE071","\uE070","\uE06F","\uE06E","\uE06D","\uE06C","\uE06B","\uE06A","\uE069","\uE068","\uE067","\uE066","\uE065","\uE064","\uE063","\uE062","\uE061","\uE060","\uE05F"};

    private final String targetResourceBackground = "\uE088";

    private final String[] targetResource  = {"\uE0B1","\uE0B0","\uE0AF","\uE0AE","\uE0AD","\uE0AC","\uE0AB","\uE0AA","\uE0A9","\uE0A8","\uE0A7","\uE0A6","\uE0A5","\uE0A4","\uE0A3","\uE0A2","\uE0A1","\uE0A0","\uE09F","\uE09E","\uE09D","\uE09C","\uE09B","\uE09A","\uE099","\uE098","\uE097","\uE096","\uE095","\uE094","\uE093","\uE092","\uE091","\uE090","\uE08F","\uE08E","\uE08D","\uE08C","\uE08B","\uE08A","\uE089"};

    //team data
    private final String teamResourceBackground0 = "\ue0d8";
    private final String[] teamResource0 = {"\uE101","\uE100","\uE0FF","\uE0FE","\uE0FD","\uE0FC","\uE0FB","\uE0FA","\uE0F9","\uE0F8","\uE0F7","\uE0F6","\uE0F5","\uE0F4","\uE0F3","\uE0F2","\uE0F1","\uE0F0","\uE0EF","\uE0EE","\uE0ED","\uE0EC","\uE0EB","\uE0EA","\uE0E9","\uE0E8","\uE0E7","\uE0E6","\uE0E5","\uE0E4","\uE0E3","\uE0E2","\uE0E1","\uE0E0","\uE0DF","\uE0DE","\uE0DD","\uE0DC","\uE0DB","\uE0DA","\uE0D9"};
    private final Map<UUID, String> teamResource0Cache = new ConcurrentHashMap<>();

    private final String teamResourceBackground1 = "\ue102";
    private final String[] teamResource1  = {"\uE12B","\uE12A","\uE129","\uE128","\uE127","\uE126","\uE125","\uE124","\uE123","\uE122","\uE121","\uE120","\uE11F","\uE11E","\uE11D","\uE11C","\uE11B","\uE11A","\uE119","\uE118","\uE117","\uE116","\uE115","\uE114","\uE113","\uE112","\uE111","\uE110","\uE10F","\uE10E","\uE10D","\uE10C","\uE10B","\uE10A","\uE109","\uE108","\uE107","\uE106","\uE105","\uE104","\uE103"};
    private final Map<UUID, String> teamResource1Cache = new ConcurrentHashMap<>();

    private final String teamResourceBackground2 = "\ue12c";
    private final String[] teamResource2 = {"\uE155","\uE154","\uE153","\uE152","\uE151","\uE150","\uE14F","\uE14E","\uE14D","\uE14C","\uE14B","\uE14A","\uE149","\uE148","\uE147","\uE146","\uE145","\uE144","\uE143","\uE142","\uE141","\uE140","\uE13F","\uE13E","\uE13D","\uE13C","\uE13B","\uE13A","\uE139","\uE138","\uE137","\uE136","\uE135","\uE134","\uE133","\uE132","\uE131","\uE130","\uE12F","\uE12E","\uE12D"};
    private final Map<UUID, String> teamResource2Cache = new ConcurrentHashMap<>();

    private final String teamResourceBackground3 = "\ue156";
    private final String[] teamResource3 = {"\uE17F","\uE17E","\uE17D","\uE17C","\uE17B","\uE17A","\uE179","\uE178","\uE177","\uE176","\uE175","\uE174","\uE173","\uE172","\uE171","\uE170","\uE16F","\uE16E","\uE16D","\uE16C","\uE16B","\uE16A","\uE169","\uE168","\uE167","\uE166","\uE165","\uE164","\uE163","\uE162","\uE161","\uE160","\uE15F","\uE15E","\uE15D","\uE15C","\uE15B","\uE15A","\uE159","\uE158","\uE157"};
    private final Map<UUID, String> teamResource3Cache = new ConcurrentHashMap<>();

    //squad data

    //0-8
    private final String[] squadResource0 = {"\ue1c0","\ue1bf","\ue1be","\ue1bd","\ue1bc","\ue1bb","\ue1ba","\ue1b9","\ue1b8"};
    private final Map<UUID, String> squadResource0Cache = new ConcurrentHashMap<>();
    private final String[] squadResource1 = {"\ue1c9","\ue1c8","\ue1c7","\ue1c6","\ue1c5","\ue1c4","\ue1c3","\ue1c2","\ue1c1"};
    private final Map<UUID, String> squadResource1Cache = new ConcurrentHashMap<>();
    private final String[] squadResource2 = {"\ue1d2","\ue1d1","\ue1d0","\ue1cf","\ue1ce","\ue1cd","\ue1cc","\ue1cb","\ue1ca"};
    private final Map<UUID, String> squadResource2Cache = new ConcurrentHashMap<>();

    public HudManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        bossManager = main.getBossManager();
        damageBoardPlaceholders = new DamageBoardPlaceholders(main);
        mysticaPartyManager = main.getMysticaPartyManager();
        abilityManager = main.getAbilityManager();
        statusEffectManager = main.getStatusEffectManager();
        targetManager = main.getTargetManager();
        bossCastingManager = main.getBossCastingManager();
        bossWarningSender = new BossWarningSender(main);
        gravestoneManager = main.getGravestoneManager();
        skinGrabber = new SkinGrabber();
        aggroManager = main.getAggroManager();
        abilityBarRenderer = new AbilityBarRenderer(main, abilityManager);
        statusEffectRenderer = new StatusEffectRenderer(statusEffectManager);

        //maybe temp?
        charGlyphAtlas = main.getCharGlyphAtlas();
    }

    public DamageBoardPlaceholders getDamageBoardPlaceholders(){
        return damageBoardPlaceholders;
    }

    public BossWarningSender getBossWarnings(){return bossWarningSender;}

    public void innitHud(Player player){

        LivingEntity target = targetManager.getPlayerTarget(player);

        new BukkitRunnable(){
            @Override
            public void run(){
                //bar 1, target
                //comment out to test pixel renderer
                /*BossBar targetBar = Bukkit.createBossBar(getTargetData(target), BarColor.WHITE, BarStyle.SOLID);
                targetBar.addPlayer(player);
                targetBar.setVisible(true);
                profileManager.setPlayerTargetBar(player, targetBar);*/

                //pixel renderer
                BossBar targetBar = Bukkit.createBossBar(charGlyphAtlas.get('e').get(-100), BarColor.WHITE, BarStyle.SOLID);
                //BossBar targetBar = Bukkit.createBossBar(characterRenderer.getCharGlyph('e', -50), BarColor.WHITE, BarStyle.SOLID);
                targetBar.addPlayer(player);
                targetBar.setVisible(true);
                profileManager.setPlayerTargetBar(player, targetBar);

                //bar 2, target's target
                BossBar targetTargetBar = Bukkit.createBossBar(getTargetTargetData(target), BarColor.WHITE, BarStyle.SOLID);
                targetTargetBar.addPlayer(player);
                targetTargetBar.setVisible(true);
                profileManager.setPlayerTargetTargetBar(player, targetTargetBar);

                //bar 3, team
                BossBar teamBar = Bukkit.createBossBar(createTeamDataString(player), BarColor.WHITE, BarStyle.SOLID);
                teamBar.addPlayer(player);
                teamBar.setVisible(true);
                profileManager.setPlayerTeamBar(player, teamBar);

                //bar 4 warnings??

            }
        }.runTaskAsynchronously(main);

    }

    //######################################################################################################

    //reason this has to update is that entities update their *own* bar information
    public void hudTicker(){
        for(Player player : Bukkit.getOnlinePlayers()){
            updateSkillCache(player);
            displayActionBar(player);
            updateTargetData(player);
            updateTargetTargetData(player);
            updateTeamData(player);
        }
    }

    private void displayActionBar(Player player){

        StringBuilder builder = new StringBuilder();
        String bar = getActionBar(player);
        builder.append(bar);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(builder)));

    }

    private String getActionBar(Player player){

        StringBuilder builder = new StringBuilder();

        int width = statusEffectRenderer.getStatusWidth(player);

        for (int i = 0; i < PIXELS.length; i++) {

            while (width >= PIXELS[i]) {
                builder.append(GLYPHS[i]);
                width -= PIXELS[i];
            }

        }

        builder.append(getPlayerHealthBar(player));

        builder.append(ChatColor.RESET);

        builder.append(getPlayerResourceBar(player));

        builder.append(ChatColor.RESET);

        builder.append(getSkillBar(player));

        builder.append(ChatColor.RESET);

        builder.append(getCastBar(player));

        builder.append(ChatColor.RESET);

        builder.append(getStatusEffectString(player));

        return String.valueOf(builder);
    }

    private String getPlayerHealthBar(Player player){

        if(!healthBarCache.containsKey(player.getUniqueId())){
            updateHealthBar(player);
        }

        return healthBarCache.getOrDefault(player.getUniqueId(),"");
    }

    private String getPlayerResourceBar(Player player){

        if(!resourceBarCache.containsKey(player.getUniqueId())){
            updateResourceBar(player);
        }

        return resourceBarCache.getOrDefault(player.getUniqueId(), "");
    }

    public void updateHealthBar(Player player){

        new BukkitRunnable(){
            @Override
            public void run(){
                StringBuilder builder = new StringBuilder();

                Profile playerProfile = profileManager.getAnyProfile(player);
                double actualMaxHealth = playerProfile.getTotalHealth() + statusEffectManager.getHealthBuffAmount(player);
                double actualCurrentHealth = profileManager.getAnyProfile(player).getCurrentHealth();

                double ratio = actualCurrentHealth / actualMaxHealth;

                int amount = (int) Math.ceil(ratio * 40);

                //Bukkit.getLogger().info("player ratio number " + amount);

                if(amount < 0){
                    amount = 0;
                }

                if(actualCurrentHealth <= 0){
                    amount = 0;
                }

                if(amount > 40){
                    amount = 40;
                }

                if(profileManager.getAnyProfile(player).getIfDead()){
                    amount = 0;
                }

                builder.append(actionBarResourceBackground);
                //-104
                builder.append("\uF80B\uF80A\uF808");

                builder.append(ChatColor.GREEN);
                //grab this from the indexer
                builder.append(actionBarResource[amount]);

                builder.append(ChatColor.RESET);

                //-104
                builder.append("\uF80B\uF80A\uF808");



                double shield = statusEffectManager.getTotalShield(player);

                //Bukkit.getLogger().info("player has " + shield);

                double shieldRatio = shield/ actualMaxHealth;

                int shieldAmount = (int) Math.ceil(shieldRatio * 40);

                if(shieldAmount < 0){
                    shieldAmount = 0;
                }

                if(shieldAmount > 40){
                    shieldAmount = 40;
                }

                builder.append(ChatColor.WHITE);
                builder.append(actionBarResource[shieldAmount]);

                healthBarCache.put(player.getUniqueId(), builder.toString());

            }
        }.runTaskAsynchronously(main);


    }

    public void updateResourceBar(Player player){

        //maybe this is more dynamic in the future
        new BukkitRunnable(){
            @Override
            public void run(){

                StringBuilder builder = new StringBuilder();

                Profile profile = profileManager.getAnyProfile(player);

                switch (profile.getPlayerClass()){
                    case Elementalist -> {
                        builder.append(getElementalistResource(player));
                    }
                    case Mystic -> {
                        builder.append(getMysticResourceBar(player));
                    }
                    case Ranger -> {
                        builder.append(getRangerResourceBar(player));
                    }
                    case Warrior -> {
                        builder.append(getWarriorResourceBar(player));
                    }
                    case Paladin -> {
                        builder.append(getPaladinResourceBar(player));
                    }
                    case Shadow_Knight -> {
                        builder.append(getShadowKnightResourceBar(player));
                    }
                    case Assassin -> {
                        builder.append(getAssassinResourceBar(player));
                    }
                }


                resourceBarCache.put(player.getUniqueId(), builder.toString());


            }
        }.runTaskAsynchronously(main);

    }

    private String getElementalistResource(Player player){

        StringBuilder builder = new StringBuilder();

        double currentHeat = abilityManager.getHeat().getHeat(player);
        double maxHeat = 100;
        double ratio = currentHeat/maxHeat;

        int amount = (int) Math.ceil(ratio * 40);

        if(amount < 0){
            amount = 0;
        }

        if(currentHeat <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        builder.append(ChatColor.RESET);
        builder.append(actionBarResourceBackground);
        //-104
        builder.append("\uF80B\uF80A\uF808");

        if(amount == 40){
            builder.append(ChatColor.RED);
        }
        else{
            builder.append(ChatColor.BLUE);
        }

        //builder.append(ChatColor.e);
        //grab this from the indexer
        builder.append(actionBarResource[amount]);

        return String.valueOf(builder);
    }

    private String getMysticResourceBar(Player player){
        StringBuilder builder = new StringBuilder();

        double currentMana = abilityManager.getMana().getCurrentMana(player);
        double maxMana = 100;
        double ratio = currentMana/maxMana;

        int amount = (int) Math.ceil(ratio * 40);

        if(amount < 0){
            amount = 0;
        }

        if(currentMana <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        builder.append(ChatColor.RESET);
        builder.append(actionBarResourceBackground);
        //-104
        builder.append("\uF80B\uF80A\uF808");
        builder.append(ChatColor.BLUE);
        //grab this from the indexer
        builder.append(actionBarResource[amount]);

        return String.valueOf(builder);
    }

    private String getRangerResourceBar(Player player){
        StringBuilder builder = new StringBuilder();

        double currentFocus = abilityManager.getFocus().getFocus(player);
        double maxHeat = 10;
        double ratio = currentFocus/maxHeat;

        int amount = (int) Math.ceil(ratio * 40);

        if(amount < 0){
            amount = 0;
        }

        if(currentFocus <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        builder.append(ChatColor.RESET);
        builder.append(actionBarResourceBackground);
        //-104
        builder.append("\uF80B\uF80A\uF808");

        builder.append(ChatColor.YELLOW);
        //grab this from the indexer
        builder.append(actionBarResource[amount]);

        return String.valueOf(builder);
    }

    private String getWarriorResourceBar(Player player){
        StringBuilder builder = new StringBuilder();

        double currentRage = abilityManager.getRage().getCurrentRage(player);
        double maxRage = 500;
        double ratio = currentRage/maxRage;

        int amount = (int) Math.ceil(ratio * 40);

        if(amount < 0){
            amount = 0;
        }

        if(currentRage <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        builder.append(ChatColor.RESET);
        builder.append(actionBarResourceBackground);
        //-104
        builder.append("\uF80B\uF80A\uF808");

        builder.append(ChatColor.DARK_RED);
        //grab this from the indexer
        builder.append(actionBarResource[amount]);

        return String.valueOf(builder);
    }

    //different for divine and templar
    private String getPaladinResourceBar(Player player){
        StringBuilder builder = new StringBuilder();

        int currentPurity = abilityManager.getPurity().get(player);

        if(abilityManager.getPurity().active(player)){
            currentPurity = 3;
        }

        builder.append(ChatColor.RESET);
        builder.append(actionBarResourceBackground);
        //-104
        builder.append("\uF80B\uF80A\uF808");


        //grab this from the indexer

        switch (currentPurity) {
            case 0 -> {
                builder.append(actionBarResource[0]);
            }
            case 1 -> {
                builder.append(ChatColor.YELLOW);
                builder.append(actionBarResource[14]);
            }
            case 2 -> {
                builder.append(ChatColor.YELLOW);
                builder.append(actionBarResource[28]);
            }
            case 3 -> {
                builder.append(ChatColor.GOLD);
                builder.append(actionBarResource[40]);
            }
        }


        return String.valueOf(builder);
    }

    private String getShadowKnightResourceBar(Player player){
        StringBuilder builder = new StringBuilder();

        double currentEnergy = abilityManager.getEnergy().getCurrentEnergy(player);
        double maxEnergy = 100;
        double ratio = currentEnergy/maxEnergy;

        int amount = (int) Math.ceil(ratio * 40);

        if(amount < 0){
            amount = 0;
        }

        if(currentEnergy <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        builder.append(ChatColor.RESET);
        builder.append(actionBarResourceBackground);
        //-104
        builder.append("\uF80B\uF80A\uF808");
        builder.append(ChatColor.DARK_PURPLE);
        //builder.append(ChatColor.e);
        //grab this from the indexer
        builder.append(actionBarResource[amount]);

        return String.valueOf(builder);
    }

    private String getAssassinResourceBar(Player player){

        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.RESET);

        int currentCombo = abilityManager.getCombo().getComboPoints(player);

        int maxCombo = 4;

        if(profileManager.getAnyProfile(player).getPlayerSubclass().equals(SubClass.Duelist)){
            maxCombo = 5;
        }

        builder.append(ChatColor.RESET);
        if(maxCombo == 4){
            builder.append(comboResource4[currentCombo]);
        }
        else{
            builder.append(comboResource5[currentCombo]);
        }

        return String.valueOf(builder);
    }

    //#####################################################################################################

    public void updateSkillCache(Player player){

        EquipSkills equipSkills = profileManager.getAnyProfile(player).getEquipSkills();
        double haste = statusEffectManager.getHastePercent(player);

        long now = System.currentTimeMillis();

        new BukkitRunnable(){
            @Override
            public void run(){
                abilityBarCache.put(player.getUniqueId(), abilityBarRenderer.render(player, equipSkills, haste, now));
            }
        }.runTaskAsynchronously(main);

    }

    private String getSkillBar(Player player){
        return abilityBarCache.getOrDefault(player.getUniqueId(), "");
    }

    //########################################################################################################

    public void updateCastBar(Player player){

        new BukkitRunnable(){
            @Override
            public void run(){
                StringBuilder castBar = new StringBuilder();

                double percent = abilityManager.getCastPercent(player);

                if(percent==0){
                    castBarCache.put(player.getUniqueId(), String.valueOf(castBar));
                    return;
                }

                //this moves to the front
                //-256
                castBar.append("\uF80D");
                //+49
                castBar.append("\uF82A\uF829\uF821");

                double ratio = percent / 100;

                int amount = (int) Math.ceil(ratio * 40);

                if(amount <=0){
                    amount = 0;
                }

                if(amount>=40){
                    amount = 40;
                }

                //+53, to make it centered
                castBar.append("\uF82A\uF829\uF825");

                //put icon first
                castBar.append(abilityManager.getSkillCurrentlyCasting(player));

                castBar.append(playerCastBarBackground);
                //-83
                castBar.append("\uF80B\uF809\uF803");

                castBar.append(ChatColor.GRAY);

                castBar.append(playerCastBar[amount]);

                //now i need to add padding
                int maxLength = 207 - 53; //bar lengths minus offset spacing
                int padding = maxLength - 83 - 17; //cast bar size plus icon width, 17
                // pad remaining space

                for (int i = 0; i < PIXELS.length; i++) {

                    while (padding >= PIXELS[i]) {
                        castBar.append(GLYPHS[i]);
                        padding -= PIXELS[i];
                    }

                }

                castBarCache.put(player.getUniqueId(), String.valueOf(castBar));

            }
        }.runTaskAsynchronously(main);

    }

    private String getCastBar(Player player){
        return castBarCache.getOrDefault(player.getUniqueId(), "");
    }

    //######################################################################################################

    //should work for bosses and players
    private String getStatusEffectString(LivingEntity entity){
        return statusEffectRenderer.render(entity);
    }


    //#######################################################################################################

    public void updateTargetData(Player player){
        BossBar targetBar = profileManager.getPlayerTargetBar(player);
        //LivingEntity target = targetManager.getPlayerTarget(player);
        //targetBar.setTitle(getTargetData(target));
    }

    private String getTargetData(LivingEntity entity){

        if(entity == null){
            return "";
        }

        if(entityBarData.containsKey(entity.getUniqueId())){

            StringBuilder builder = new StringBuilder();

            //put space before sfx
            int width = statusEffectRenderer.getStatusWidth(entity);

            boolean hasEffects = width != 0;

            if(hasEffects){
                //+17
                builder.append("\uF829\uF821");
            }


            for (int i = 0; i < PIXELS.length; i++) {

                while (width >= PIXELS[i]) {
                    builder.append(GLYPHS[i]);
                    width -= PIXELS[i];
                }

            }

            builder.append(entityBarData.get(entity.getUniqueId()));

            builder.append(ChatColor.RESET);

            if(hasEffects){
                //+17
                builder.append("\uF829\uF821");
            }

            builder.append(getTargetStatusEffects(entity));

            return String.valueOf(builder);
        }

        return "";
    }

    public void updateEntityBarInformation(LivingEntity entity){

        new BukkitRunnable(){
            @Override
            public void run(){
                StringBuilder entityData = new StringBuilder();

                if(bossManager.getIfEntityIsBoss(entity.getUniqueId())){

                    entityData.append(bossHealthBar(entity));

                    //fury bar, left side of health bar
                    //-128
                    entityData.append("\uF80C");
                    //-20
                    entityData.append("\uF809\uF804");
                    entityData.append(bossFuryBar(entity));

                    //cast bar, right side
                    //+16
                    entityData.append("\uF829");
                    entityData.append(bossCastBar(entity));

                    //-128
                    entityData.append("\uF80C");
                    //-16
                    entityData.append("\uF809");



                    int maxNameLength = 141;
                    String name = truncateName(entity.getName(), maxNameLength);
                    int nameWidth = getPixelWidth(name);
                    int padding = maxNameLength - nameWidth;

                    entityData.append(name);

                    // pad remaining space

                    for (int i = 0; i < PIXELS.length; i++) {

                        while (padding >= PIXELS[i]) {
                            entityData.append(GLYPHS[i]);
                            padding -= PIXELS[i];
                        }

                    }

                    entityBarData.put(entity.getUniqueId(), String.valueOf(entityData));

                    return;
                }

                if(profileManager.getAnyProfile(entity).fakePlayer()){


                    entityData.append(profileManager.getCompanionFace(entity.getUniqueId()));

                    entityData.append(targetHealthBar(entity));

                    //-101
                    entityData.append("\uF80B\uF80A\uF805");

                    int maxNameLength = 96;
                    String name = truncateName(entity.getName(), maxNameLength);
                    int nameWidth = getPixelWidth(name);
                    int padding = maxNameLength - nameWidth;
                    entityData.append(name);



                    // pad remaining space
                    for (int i = 0; i < PIXELS.length; i++) {

                        while (padding >= PIXELS[i]) {
                            entityData.append(GLYPHS[i]);
                            padding -= PIXELS[i];
                        }

                    }

                    entityBarData.put(entity.getUniqueId(), String.valueOf(entityData));

                    updateSelfTeamInfo(entity);
                    return;
                }

                if(entity instanceof Player player){


                    entityData.append(skinGrabber.getFace(player));
                    //+16 for alignment
                    entityData.append("\uF829");

                    entityData.append(targetHealthBar(player));

                    //-101
                    entityData.append("\uF80B\uF80A\uF805");

                    int maxNameLength = 96;
                    String name = truncateName(entity.getName(), maxNameLength);
                    entityData.append(name);
                    int nameWidth = getPixelWidth(name);
                    int padding = maxNameLength - nameWidth;

                    // pad remaining space
                    for (int i = 0; i < PIXELS.length; i++) {

                        while (padding >= PIXELS[i]) {
                            entityData.append(GLYPHS[i]);
                            padding -= PIXELS[i];
                        }

                    }

                    entityBarData.put(player.getUniqueId(), String.valueOf(entityData));

                    return;
                }

                //default

                entityData.append(targetHealthBar(entity));

                //-101
                entityData.append("\uF80B\uF80A\uF805");


                int maxNameLength = 96;
                String name = truncateName(entity.getName(), maxNameLength);
                entityData.append(name);
                int nameWidth = getPixelWidth(name);
                int padding = maxNameLength - nameWidth;

                // pad remaining space
                for (int i = 0; i < PIXELS.length; i++) {

                    while (padding >= PIXELS[i]) {
                        entityData.append(GLYPHS[i]);
                        padding -= PIXELS[i];
                    }

                }

                entityBarData.put(entity.getUniqueId(), String.valueOf(entityData));

            }
        }.runTaskAsynchronously(main);


    }

    private String getTargetStatusEffects(LivingEntity entity){


        return statusEffectRenderer.render(entity);
    }

    String truncateName(String name, int maxWidth){

        name = name.replaceAll("§.", "");

        int width = 0;
        StringBuilder result = new StringBuilder();

        for (char c : name.toCharArray()) {

            int charWidth = MINECRAFT_CHAR_WIDTHS.getOrDefault(c, -1) + 1;

            //4 is … length
            if (width + charWidth + 6 > maxWidth) {
                result.append("…");
                return result.toString();
            }

            result.append(c);
            width += charWidth;
        }

        return name;
    }


    int getPixelWidth(String text) {

        text = text.replaceAll("§.", "");

        int width = 0;

        for (char c : text.toCharArray()) {
            width += MINECRAFT_CHAR_WIDTHS.getOrDefault(c, -1)+1;
        }

        return width;
    }

    private String targetHealthBar(LivingEntity entity){

        StringBuilder healthBar = new StringBuilder();

        Profile profile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = profile.getTotalHealth() + statusEffectManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        double ratio = actualCurrentHealth / actualMaxHealth;

        int amount = (int) Math.ceil(ratio * 40);

        //Bukkit.getLogger().info("player ratio number " + amount);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        //reason i am doing this is the potential to call bukkit.getentity async on get if dead
        if(entity instanceof Player || profile.fakePlayer()){
            if(profileManager.getAnyProfile(entity).getIfDead()){
                amount = 0;
            }
        }
        else{
            if(entity.isDead()){
                amount = 0;
            }
        }


        //this needs a different bar type

        healthBar.append(targetResourceBackground);
        //-104
        healthBar.append("\uF80B\uF80A\uF808");


        //not going to check pvp info quite yet
        if(profileManager.getAnyProfile(entity).getIsPassive() || entity instanceof Player){
            healthBar.append(ChatColor.GREEN);
        }
        else{
            healthBar.append(ChatColor.RED);
        }

        //grab this from the indexer
        healthBar.append(targetResource[amount]);

        healthBar.append(ChatColor.RESET);

        //-104
        healthBar.append("\uF80B\uF80A\uF808");


        double shield = statusEffectManager.getTotalShield(entity);

        //Bukkit.getLogger().info("player has " + shield);

        double shieldRatio = shield/ actualMaxHealth;

        int shieldAmount = (int) Math.ceil(shieldRatio * 40);

        if(shieldAmount < 0){
            shieldAmount = 0;
        }

        if(shieldAmount > 40){
            shieldAmount = 40;
        }

        healthBar.append(ChatColor.WHITE);
        healthBar.append(targetResource[shieldAmount]);

        return String.valueOf(healthBar);
    }

    private String bossHealthBar(LivingEntity entity){

        StringBuilder healthBar = new StringBuilder();

        Profile profile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = profile.getTotalHealth() + statusEffectManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        double ratio = actualCurrentHealth / actualMaxHealth;

        int amount = (int) Math.ceil(ratio * 40);

        //Bukkit.getLogger().info("player ratio number " + amount);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        if(profileManager.getAnyProfile(entity).getIfDead()){
            amount = 0;
        }

        healthBar.append(bossResourceBackground);
        //-128
        healthBar.append("\uF80C");
        //-27
        healthBar.append("\uF809\uF808\uF803");

        healthBar.append(ChatColor.RED);
        //grab this from the indexer
        healthBar.append(bossBarResource[amount]);

        healthBar.append(ChatColor.RESET);

        //-128
        healthBar.append("\uF80C");
        //-27
        healthBar.append("\uF809\uF808\uF803");


        double shield = statusEffectManager.getTotalShield(entity);

        //Bukkit.getLogger().info("player has " + shield);

        double shieldRatio = shield/ actualMaxHealth;

        int shieldAmount = (int) Math.ceil(shieldRatio * 40);

        if(shieldAmount < 0){
            shieldAmount = 0;
        }

        if(shieldAmount > 40){
            shieldAmount = 40;
        }

        healthBar.append(ChatColor.WHITE);
        healthBar.append(bossBarResource[shieldAmount]);

        return String.valueOf(healthBar);
    }

    private String bossCastBar(LivingEntity entity){
        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.DARK_RED);

        int amount = 0;

        if(bossCastingManager.bossIsCasting(entity)){
            double max = bossCastingManager.getCastMax(entity);
            double current = bossCastingManager.getCastPercent(entity);

            double ratio = current / max;

            amount = (int) Math.ceil(ratio * 40);

            if(amount < 0){
                amount = 0;
            }

            if(current <= 0){
                amount = 0;
            }
        }


        builder.append(bossCastBar[amount]);


        return String.valueOf(builder);
    }

    private String bossFuryBar(LivingEntity entity){
        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.DARK_RED);

        if(!bossManager.hasFuryTimer(entity.getUniqueId())){
            builder.append(bossCastBar[0]);
            return String.valueOf(builder);
        }

        int amount;

        double max = bossManager.getMaxFuryDuration(entity.getUniqueId());
        double current = bossManager.getCurrentFuryCount(entity.getUniqueId());

        double ratio = current / max;

        amount = 40 - (int) Math.ceil(ratio * 40);

        if(amount < 0){
            amount = 0;
        }

        if(current <= 0){
            amount = 0;
        }

        builder.append(bossCastBar[amount]);


        return String.valueOf(builder);
    }

    //#######################################################################################################

    public void updateTargetTargetData(Player player){
        BossBar targetBar = profileManager.getPlayerTargetTargetBar(player);
        LivingEntity target = targetManager.getPlayerTarget(player);
        targetBar.setTitle(getTargetTargetData(target));
    }


    private String getTargetTargetData(LivingEntity entity){

        if(entity == null){
            return "";
        }

        StringBuilder builder = new StringBuilder();

        //check targets target
        LivingEntity target;

        if(entity instanceof Player || profileManager.getAnyProfile(entity).fakePlayer()){
            target = targetManager.getPlayerTarget(entity);
        }
        else{
            target = aggroManager.getTarget(entity);
        }

        if(target == entity){
            return "";
        }

        if(target != null){

            //perhaps move it right
            if(entityBarData.containsKey(target.getUniqueId())){

                //+256
                builder.append("\uF82D");
                //+16
                builder.append("\uF829");

                int width = statusEffectRenderer.getStatusWidth(target);

                boolean hasEffects = width != 0;

                if(hasEffects){
                    //+17
                    builder.append("\uF829\uF821");
                }

                for (int i = 0; i < PIXELS.length; i++) {

                    while (width >= PIXELS[i]) {
                        builder.append(GLYPHS[i]);
                        width -= PIXELS[i];
                    }

                }

                builder.append(entityBarData.get(target.getUniqueId()));

                builder.append(ChatColor.RESET);

                if(hasEffects){
                    //+17
                    builder.append("\uF829\uF821");
                }

                builder.append(getTargetStatusEffects(target));

                return String.valueOf(builder);
            }

        }

        /*if(entityBarData.containsKey(entity.getUniqueId())){
            return entityBarData.get(entity.getUniqueId());
        }*/

        return String.valueOf(builder);
    }

    //#####################################################################################################

    public void updateTeamData(Player player){
        BossBar teamBar = profileManager.getPlayerTeamBar(player);
        teamBar.setTitle(createTeamDataString(player));
    }

    //update ALL of them
    private void updateSelfTeamInfo(LivingEntity entity){

        StringBuilder builder0 = new StringBuilder();
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        StringBuilder builder3 = new StringBuilder();


        //append face to builders
        if(entity instanceof Player player){
            builder0.append(skinGrabber.getTeamFace(player, 0));
            builder1.append(skinGrabber.getTeamFace(player, 1));
            builder2.append(skinGrabber.getTeamFace(player, 2));
            builder3.append(skinGrabber.getTeamFace(player, 3));
            //+16 for alignment
            builder0.append("\uF829");
            builder1.append("\uF829");
            builder2.append("\uF829");
            builder3.append("\uF829");
        }

        if(profileManager.getAnyProfile(entity).fakePlayer()){
            builder0.append(profileManager.getCompanionTeamFace(entity.getUniqueId(), 0));
            builder1.append(profileManager.getCompanionTeamFace(entity.getUniqueId(), 1));
            builder2.append(profileManager.getCompanionTeamFace(entity.getUniqueId(), 2));
            builder3.append(profileManager.getCompanionTeamFace(entity.getUniqueId(), 3));

            /*Player player = profileManager.getCompanionsPlayer(entity);

            builder0.append(skinGrabber.getTeamFace(player, 0));
            builder1.append(skinGrabber.getTeamFace(player, 1));
            builder2.append(skinGrabber.getTeamFace(player, 2));
            builder3.append(skinGrabber.getTeamFace(player, 3));
            //+16 for alignment
            builder0.append("\uF829");
            builder1.append("\uF829");
            builder2.append("\uF829");
            builder3.append("\uF829");*/
        }

        //append health
        Profile profile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = profile.getTotalHealth() + statusEffectManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        double ratio = actualCurrentHealth / actualMaxHealth;

        int amount = (int) Math.ceil(ratio * 40);

        //Bukkit.getLogger().info("player ratio number " + amount);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 40){
            amount = 40;
        }

        //don't have to do the fucky thing because fake player never used bukkit.getentity
        if(profileManager.getAnyProfile(entity).getIfDead()){
            amount = 0;
        }

        builder0.append(teamResourceBackground0);
        //-104
        builder0.append("\uF80B\uF80A\uF808");
        builder0.append(ChatColor.GREEN);
        builder0.append(teamResource0[amount]);
        builder0.append(ChatColor.RESET);
        //-104
        builder0.append("\uF80B\uF80A\uF808");

        double shield = statusEffectManager.getTotalShield(entity);
        double shieldRatio = shield/ actualMaxHealth;

        int shieldAmount = (int) Math.ceil(shieldRatio * 40);

        if(shieldAmount < 0){
            shieldAmount = 0;
        }

        if(shieldAmount > 40){
            shieldAmount = 40;
        }

        builder0.append(ChatColor.WHITE);
        builder0.append(teamResource0[shieldAmount]);

        teamResource0Cache.put(entity.getUniqueId(),String.valueOf(builder0));


        builder1.append(teamResourceBackground1);
        //-104
        builder1.append("\uF80B\uF80A\uF808");
        builder1.append(ChatColor.GREEN);
        builder1.append(teamResource1[amount]);
        builder1.append(ChatColor.RESET);
        //-104
        builder1.append("\uF80B\uF80A\uF808");

        builder1.append(ChatColor.WHITE);
        builder1.append(teamResource1[shieldAmount]);

        teamResource1Cache.put(entity.getUniqueId(),String.valueOf(builder1));

        builder2.append(teamResourceBackground2);
        //-104
        builder2.append("\uF80B\uF80A\uF808");
        builder2.append(ChatColor.GREEN);
        builder2.append(teamResource2[amount]);
        builder2.append(ChatColor.RESET);
        //-104
        builder2.append("\uF80B\uF80A\uF808");

        builder2.append(ChatColor.WHITE);
        builder2.append(teamResource2[shieldAmount]);

        teamResource2Cache.put(entity.getUniqueId(),String.valueOf(builder2));

        builder3.append(teamResourceBackground3);
        //-104
        builder3.append("\uF80B\uF80A\uF808");
        builder3.append(ChatColor.GREEN);
        builder3.append(teamResource3[amount]);
        builder3.append(ChatColor.RESET);
        //-104
        builder3.append("\uF80B\uF80A\uF808");

        builder3.append(ChatColor.WHITE);
        builder3.append(teamResource3[shieldAmount]);

        teamResource3Cache.put(entity.getUniqueId(),String.valueOf(builder3));


        updateSelfSquadInfo(entity);

    }

    private void updateSelfSquadInfo(LivingEntity entity){

        StringBuilder builder0 = new StringBuilder();
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

        Profile playerProfile = profileManager.getAnyProfile(entity);
        PlayerClass playerClass = profileManager.getAnyProfile(entity).getPlayerClass();

        switch (playerClass) {
            case Assassin -> {
                builder0.append(ChatColor.of(assassinColor));
                builder1.append(ChatColor.of(assassinColor));
                builder2.append(ChatColor.of(assassinColor));
            }
            case Elementalist -> {
                builder0.append(ChatColor.of(elementalistColor));
                builder1.append(ChatColor.of(elementalistColor));
                builder2.append(ChatColor.of(elementalistColor));
            }
            case Ranger -> {
                builder0.append(ChatColor.of(rangerColor));
                builder1.append(ChatColor.of(rangerColor));
                builder2.append(ChatColor.of(rangerColor));
            }
            case Paladin -> {
                builder0.append(ChatColor.of(paladinColor));
                builder1.append(ChatColor.of(paladinColor));
                builder2.append(ChatColor.of(paladinColor));
            }
            case Warrior -> {
                builder0.append(ChatColor.of(warriorColor));
                builder1.append(ChatColor.of(warriorColor));
                builder2.append(ChatColor.of(warriorColor));
            }
            case Shadow_Knight -> {
                builder0.append(ChatColor.of(shadowKnightColor));
                builder1.append(ChatColor.of(shadowKnightColor));
                builder2.append(ChatColor.of(shadowKnightColor));

            }
            case Mystic -> {
                builder0.append(ChatColor.of(mysticColor));
                builder1.append(ChatColor.of(mysticColor));
                builder2.append(ChatColor.of(mysticColor));
            }
            default -> {
                builder0.append(ChatColor.RESET);
                builder0.append(ChatColor.RESET);
                builder0.append(ChatColor.RESET);
            }
        }

        double actualMaxHealth = playerProfile.getTotalHealth() + statusEffectManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();
        double ratio = actualCurrentHealth / actualMaxHealth;
        int amount = (int) Math.ceil(ratio * 8);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 8){
            amount = 8;
        }

        if(playerProfile.getIfDead()){
            amount = 0;
        }

        builder0.append(squadResource0[amount]);
        builder1.append(squadResource1[amount]);
        builder2.append(squadResource2[amount]);

        squadResource0Cache.put(entity.getUniqueId(), String.valueOf(builder0));
        squadResource1Cache.put(entity.getUniqueId(),String.valueOf(builder1));
        squadResource2Cache.put(entity.getUniqueId(),String.valueOf(builder2));

    }


    private String createTeamDataString(Player player) {

        StringBuilder teamData = new StringBuilder();

        List<LivingEntity> mysticaParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        if (mysticaParty.size() == 1) {
            return String.valueOf(teamData);
        }

        if (mysticaParty.size() <= 5) {

            //temp comment out to see full thing
            //-512 space
            teamData.append("\uF80E");
            //-100
            teamData.append("\uF80B\uF80A\uF804");

            int slot = 0;
            for (LivingEntity member : mysticaParty) {

                if (member == player) {
                    continue;
                }

                switch (slot) {
                    case 0 -> {
                        teamData.append(teamResource0Cache.get(member.getUniqueId()));
                    }
                    case 1 -> {
                        teamData.append(teamResource1Cache.get(member.getUniqueId()));
                    }
                    case 2 -> {
                        teamData.append(teamResource2Cache.get(member.getUniqueId()));
                    }
                    case 3 -> {
                        teamData.append(teamResource3Cache.get(member.getUniqueId()));
                    }
                }

                //PROBLEM, faces move *slightly sometimes depending on slot

                slot++;

                if (slot == mysticaParty.size()) {
                    break;
                }

                //-128
                teamData.append("\uF80C");
                //+7
                teamData.append("\uF827");

            }

            return String.valueOf(teamData);
        }


        StringBuilder offset = new StringBuilder();

        //-512 space
        teamData.append("\uF80E");
        //-100space
        teamData.append("\uF80B\uF80A\uF804");

        int slot = 0;
        for (LivingEntity member : mysticaParty) {

            if (member == player) {
                continue;
            }

            //need to test with them unfort
            /*if(!(member instanceof Player memberPlayer)){
                //companions not allowed in squads
                continue;
            }*/

            //for testing purposes
            Player memberPlayer;

            if (member instanceof Player) {
                memberPlayer = (Player) member;
            } else {
                memberPlayer = profileManager.getCompanionsPlayer(member);
            }


            switch (slot) {
                case 0, 1, 2 -> {
                    teamData.append(squadResource0Cache.get(member.getUniqueId()));
                }
                case 3, 4, 5 -> {
                    teamData.append(squadResource1Cache.get(member.getUniqueId()));
                }
                case 6, 8, 7 -> {
                    teamData.append(squadResource2Cache.get(member.getUniqueId()));
                }
            }

            //-32
            teamData.append("\uF80A");
            teamData.append(skinGrabber.getSquadFace(memberPlayer, slot));

            // +36
            teamData.append("\uF82A\uF824");
            //+36
            offset.append("\uF82A\uF824");

            slot++;

            //this code is bad look away
            if (slot >= 3) {
                //+1
                offset.append("\uF821");
            }

            if (slot == 3 || slot == 6) {
                //-111
                teamData.append("\uF80B\uF80A\uF808\uF807");

                //+111
                //offset.append("\uF82B\uF82A\uF828\uF827");

                //-111
                offset.append("\uF80B\uF80A\uF808\uF807");

            }
        }


        offset.append(teamData);

        return String.valueOf(offset);


    }


    private String getBossWarning(Player player){
        return bossWarningSender.getWarning(player);
    }


}
