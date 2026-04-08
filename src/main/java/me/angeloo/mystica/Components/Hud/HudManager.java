package me.angeloo.mystica.Components.Hud;

import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.BuffsAndDebuffs.StatusEffectManager;
import me.angeloo.mystica.Components.CombatSystem.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.CombatSystem.GravestoneManager;
import me.angeloo.mystica.Components.CombatSystem.TargetManager;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Components.Parties.MysticaPartyManager;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.BossManager;
import me.angeloo.mystica.Utility.Logic.DamageBoardPlaceholders;
import me.angeloo.mystica.Utility.Enums.SubClass;
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


public class HudManager {

    private final Mystica main;

    private final ProfileManager profileManager;
    private final BossManager bossManager;
    private final MysticaPartyManager mysticaPartyManager;
    private final DamageBoardPlaceholders damageBoardPlaceholders;
    private final BossWarningSender bossWarningSender;


    private final AllSkillItems allSkillItems;
    private final AbilityManager abilityManager;
    private final StatusEffectManager statusEffectManager;
    private final GravestoneManager gravestoneManager;
    private final TargetManager targetManager;
    private final BossCastingManager bossCastingManager;
    private final IconCalculator iconCalculator;

    private final SkinGrabber skinGrabber;

    //this is updated when needed. when a player requests data about x entity, grabs it from here
    private final Map<UUID, String> entityBarData = new HashMap<>();


    private static final Map<Character, Integer> MINECRAFT_CHAR_WIDTHS = Map.ofEntries(
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


    //old char mappings
    /*

    //0-20, in that order
    private final String[] playerTargetHealthBar = {"\uE0CC", "\uE0CB", "\uE0CA", "\uE0C9", "\uE0C8", "\uE0C7",
            "\uE0C6", "\uE0C5", "\uE0C4", "\uE0C3", "\uE0C2", "\uE0C1", "\uE0C0", "\uE0BF", "\uE0BE", "\uE0BD",
            "\uE0BC", "\uE0BB", "\uE0BA", "\uE0B9", "\uE0B8"};

    //1-20
    private final String[] playerTargetShieldBar = {"\uE123", "\uE122", "\uE121", "\uE120", "\uE11F", "\uE11E",
            "\uE11D", "\uE11C", "\uE11B", "\uE11A", "\uE119", "\uE118", "\uE117", "\uE116", "\uE115", "\uE114",
            "\uE113", "\uE112", "\uE111", "\uE110"};

    //0-20
    private final String[] teamHealthBar0 = {"\uE163", "\uE162", "\uE161", "\uE160", "\uE15F", "\uE15E", "\uE15D",
            "\uE15C", "\uE15B", "\uE15A", "\uE159", "\uE158", "\uE157", "\uE156", "\uE155", "\uE154", "\uE153",
            "\uE152", "\uE151", "\uE150", "\uE14F"};

    //1-20
    private final String[] teamShieldBar0 = {"\uE177", "\uE176", "\uE175", "\uE174", "\uE173", "\uE172", "\uE171",
            "\uE170", "\uE16F", "\uE16E", "\uE16D", "\uE16C", "\uE16B", "\uE16A", "\uE169", "\uE168", "\uE167",
            "\uE166", "\uE165", "\uE164"};

    //0-20
    private final String[] teamHealthBar1 = {"\uE18E", "\uE18D", "\uE18C", "\uE18B", "\uE18A", "\uE189", "\uE188",
            "\uE187", "\uE186", "\uE185", "\uE184", "\uE183", "\uE182", "\uE181", "\uE180", "\uE17F", "\uE17E",
            "\uE17D", "\uE17C", "\uE17B", "\uE17A"};

    //1-20
    private final String[] teamShieldBar1 = {"\uE1A2", "\uE1A1", "\uE1A0", "\uE19F", "\uE19E", "\uE19D", "\uE19C",
            "\uE19B", "\uE19A","\uE199", "\uE198", "\uE197", "\uE196","\uE195", "\uE194", "\uE193", "\uE192",
            "\uE191", "\uE190", "\uE18F"};

    //0-20
    private final String[] teamHealthBar2 = {"\uE1B9", "\uE1B8", "\uE1B7", "\uE1B6", "\uE1B5", "\uE1B4", "\uE1B3",
            "\uE1B2", "\uE1B1", "\uE1B0", "\uE1AF", "\uE1AE", "\uE1AD", "\uE1AC", "\uE1AB", "\uE1AA", "\uE1A9",
            "\uE1A8", "\uE1A7", "\uE1A6", "\uE1A5"};

    //1-20
    private final String[] teamShieldBar2 = {"\uE1CD", "\uE1CC", "\uE1CB", "\uE1CA", "\uE1C9", "\uE1C8", "\uE1C7",
            "\uE1C6", "\uE1C5", "\uE1C4", "\uE1C3", "\uE1C2", "\uE1C1", "\uE1C0", "\uE1BF", "\uE1BE", "\uE1BD",
            "\uE1BC", "\uE1BB", "\uE1BA"};


    //0-20
    private final String[] teamHealthBar3 = {"\uE1E4", "\uE1E3", "\uE1E2", "\uE1E1", "\uE1E0", "\uE1DF", "\uE1DE",
            "\uE1DD", "\uE1DC", "\uE1DB", "\uE1DA", "\uE1D9", "\uE1D8", "\uE1D7", "\uE1D6", "\uE1D5", "\uE1D4",
            "\uE1D3", "\uE1D2", "\uE1D1", "\uE1D0"};

    //1-20
    private final String[] teamShieldBar3 = {"\uE1F9", "\uE1F8","\uE1F7", "\uE1F6", "\uE1F5", "\uE1F4", "\uE1F3",
            "\uE1F2", "\uE1F1", "\uE1F0", "\uE1EF", "\uE1ED", "\uE1EC", "\uE1EB", "\uE1EA", "\uE1E9", "\uE1E8",
            "\uE1E7", "\uE1E6", "\uE1E5"};

    //0-20
    private final String[] manaBar = {"\uE0E1","\uE0E0","\uE0DF","\uE0DE","\uE0DD","\uE0DC","\uE0DB","\uE0DA","\uE0D9",
            "\uE0D8", "\uE0D7","\uE0D6","\uE0D5","\uE0D4","\uE0D3","\uE0D2","\uE0D1","\uE0D0","\uE0CF","\uE0CE","\uE0CD"};

    //0-20
    private final String[] castBar = {"\uE305","\uE304","\uE303","\uE302","\uE301","\uE300","\uE2FF","\uE2FE","\uE2FD",
            "\uE2FC", "\uE2FB","\uE2FA","\uE2F9","\uE2F8","\uE2F7","\uE2F6","\uE2F5","\uE2F4","\uE2F3","\uE2F2","\uE2F1"};

    //0-20
    private final String[] rageBar = {"\uE0E1","\uE0F5","\uE0F4","\uE0F3","\uE0F2","\uE0F1","\uE0F0","\uE0EF","\uE0EE",
            "\uE0ED","\uE0EC","\uE0EB","\uE0EA","\uE0E9","\uE0E8","\uE0E7","\uE0E6","\uE0E5","\uE0E4","\uE0E3","\uE0E2"};

    //0-10
    private final String[] energyBar = {"\uE0E1", "\uE0FF","\uE0FE","\uE0FD","\uE0FC","\uE0FB","\uE0FA","\uE0F9","\uE0F8","\uE0F7","\uE0F6"};

    //0-8
    private final String[] ultimateCooldown = {"\uE137", "\uE136","\uE135","\uE134","\uE133","\uE132","\uE131","\uE130","\uE12F"};

    //0-20, 0 and 1 are same
    private final String[] stackAmount = {"\uE008", "\uE008", "\uE009", "\uE00A", "\uE00B", "\uE00C", "\uE00D", "\uE00E", "\uE00F", "\uE010",
            "\uE011", "\uE012", "\uE013", "\uE014", "\uE015", "\uE016", "\uE017", "\uE018", "\uE019", "\uE01A", "\uE01B"};

    //1-8
    private final String[] duration = {"\uE001","\uE002","\uE003","\uE004","\uE005","\uE006","\uE007","\uE008"};

    //0-8
    private final String[] squadHealth0 = {"\uE21A", "\uE219", "\uE218", "\uE217", "\uE216", "\uE215", "\uE214", "\uE213", "\uE212"};

    //0-8
    private final String[] squadHealth1 = {"\uE223", "\uE222", "\uE221", "\uE220", "\uE21F", "\uE21E", "\uE21D", "\uE21C", "\uE21B"};

    //0-8
    private final String[] squadHealth2 = {"\uE009","\uE22B","\uE22A","\uE229","\uE228","\uE227","\uE226","\uE225","\uE224"};*/

    private final String actionBarResourceBackground = "\uE000";

    //0-40 (action bar)
    private final String[] actionBarResource = {"\uE029","\uE028","\uE027","\uE026","\uE025","\uE024","\uE023","\uE022","\uE021","\uE020","\uE01F","\uE01E","\uE01D","\uE01C","\uE01B","\uE01A","\uE019","\uE018","\uE017","\uE016","\uE015","\uE014","\uE013","\uE012","\uE011","\uE010","\uE00F","\uE00E","\uE00D","\uE00C","\uE00B","\uE00A","\uE009","\uE008","\uE007","\uE006","\uE005","\uE004","\uE003","\uE002","\uE001"};

    private final String[] comboResource4 = {"\uE02a","\uE02b","\uE02c","\uE02d","\uE02e"};

    private final String[] comboResource5 = {"\uE02f","\uE030","\uE031","\uE032","\uE033","\uE034"};

    private final Map<UUID, String> actionBarCache = new ConcurrentHashMap<>();

    private final Map<UUID, String> healthBarCache = new ConcurrentHashMap<>();

    private final Map<UUID, String> resourceBarCache = new ConcurrentHashMap<>();


    //targeting
    private final String bossResourceBackground = "\uE035";
    //0-40, boss
    private final String[] bossBarResource = {"\uE05E","\uE05D","\uE05C","\uE05B","\uE05A","\uE059","\uE058","\uE057","\uE056","\uE055","\uE054","\uE053","\uE052","\uE051","\uE050","\uE04F","\uE04E","\uE04D","\uE04C","\uE04B","\uE04A","\uE049","\uE048","\uE047","\uE046","\uE045","\uE044","\uE043","\uE042","\uE041","\uE040","\uE03F","\uE03E","\uE03D","\uE03C","\uE03B","\uE03A","\uE039","\uE038","\uE037","\uE036"};
    //0-40 boss cast and also fury
    private final String[] bossCastBar = {"\uE087","\uE086","\uE085","\uE084","\uE083","\uE082","\uE081","\uE080","\uE07F","\uE07E","\uE07D","\uE07C","\uE07B","\uE07A","\uE079","\uE078","\uE077","\uE076","\uE075","\uE074","\uE073","\uE072","\uE071","\uE070","\uE06F","\uE06E","\uE06D","\uE06C","\uE06B","\uE06A","\uE069","\uE068","\uE067","\uE066","\uE065","\uE064","\uE063","\uE062","\uE061","\uE060","\uE05F"};

    private final String targetResourceBackground = "\uE088";

    private final String[] targetResource  = {"\uE0B1","\uE0B0","\uE0AF","\uE0AE","\uE0AD","\uE0AC","\uE0AB","\uE0AA","\uE0A9","\uE0A8","\uE0A7","\uE0A6","\uE0A5","\uE0A4","\uE0A3","\uE0A2","\uE0A1","\uE0A0","\uE09F","\uE09E","\uE09D","\uE09C","\uE09B","\uE09A","\uE099","\uE098","\uE097","\uE096","\uE095","\uE094","\uE093","\uE092","\uE091","\uE090","\uE08F","\uE08E","\uE08D","\uE08C","\uE08B","\uE08A","\uE089"};


    public HudManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        bossManager = main.getBossManager();
        damageBoardPlaceholders = new DamageBoardPlaceholders(main);
        mysticaPartyManager = main.getMysticaPartyManager();
        abilityManager = main.getAbilityManager();
        allSkillItems = main.getAllSkillItems();
        statusEffectManager = main.getStatusEffectManager();
        targetManager = main.getTargetManager();
        bossCastingManager = main.getBossCastingManager();
        bossWarningSender = new BossWarningSender(main);
        gravestoneManager = main.getGravestoneManager();
        iconCalculator = new IconCalculator();
        skinGrabber = new SkinGrabber();

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
                BossBar resourceBar = Bukkit.createBossBar(getTargetData(target), BarColor.WHITE, BarStyle.SOLID);
                resourceBar.addPlayer(player);
                resourceBar.setVisible(true);
                profileManager.setPlayerTargetBar(player, resourceBar);

                //bar 2, target's target
                //BossBar targetBar = Bukkit.createBossBar(createTargetDataString(player, target), BarColor.WHITE, BarStyle.SOLID);
                //targetBar.addPlayer(player);
                //targetBar.setVisible(true);
                //profileManager.setPlayerTargetBar(player, targetBar);


                //team data
                //BossBar teamBar = Bukkit.createBossBar(createTeamDataString(player), BarColor.WHITE, BarStyle.SOLID);
                //teamBar.addPlayer(player);
                //teamBar.setVisible(true);
                //profileManager.setPlayerTeamBar(player, teamBar);


                //status
                //BossBar statusBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
                //statusBar.addPlayer(player);
                //statusBar.setVisible(true);
                //profileManager.setPlayerStatusBar(player, statusBar);
            }
        }.runTaskAsynchronously(main);

    }

    public void displayUltimate(Player player){

        /*
        new BukkitRunnable(){
            @Override
            public void run(){
                StringBuilder hotBar = new StringBuilder();

                String statusString = getUltimateStatus(player);

                hotBar.append(statusString);

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(hotBar)));
            }
        }.runTaskAsynchronously(main);*/


    }

    //######################################################################################################

    //reason this has to update is that entities update their *own* bar information
    public void hudTicker(){
        for(Player player : Bukkit.getOnlinePlayers()){
            displayActionBar(player);
            updateTargetData(player);
        }
    }

    private void displayActionBar(Player player){

        StringBuilder builder = new StringBuilder();
        String bar = getActionBar(player);
        builder.append(bar);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(builder)));

    }


    private String getActionBar(Player player){


        if(!actionBarCache.containsKey(player.getUniqueId())){
            updateActionBar(player);
        }

        return actionBarCache.getOrDefault(player.getUniqueId(), "");
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

    public void updateActionBar(Player player){

        new BukkitRunnable(){
            @Override
            public void run(){
                StringBuilder builder = new StringBuilder();

                builder.append(getPlayerHealthBar(player));

                builder.append(ChatColor.RESET);

                builder.append(getPlayerResourceBar(player));

                actionBarCache.put(player.getUniqueId(), String.valueOf(builder));

                displayActionBar(player);
            }
        }.runTaskAsynchronously(main);

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

                updateActionBar(player);
            }
        }.runTaskAsynchronously(main);


    }

    public void updateResourceBar(Player player){

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
                updateActionBar(player);

            }
        }.runTaskAsynchronously(main);

    }

    private String getElementalistResource(Player player){

        StringBuilder builder = new StringBuilder();

        double currentHeat = abilityManager.getElementalistAbilities().getHeat().getHeat(player);
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

        double currentMana = abilityManager.getMysticAbilities().getMana().getCurrentMana(player);
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

        double currentFocus = abilityManager.getRangerAbilities().getFocus().getFocus(player);
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

        double currentRage = abilityManager.getWarriorAbilities().getRage().getCurrentRage(player);
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

        int currentPurity = abilityManager.getPaladinAbilities().getPurity().get(player);

        if(abilityManager.getPaladinAbilities().getPurity().active(player)){
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

        double currentEnergy = abilityManager.getShadowKnightAbilities().getEnergy().getCurrentEnergy(player);
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

        int currentCombo = abilityManager.getAssassinAbilities().getCombo().getComboPoints(player);

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

    //#######################################################################################################

    public void updateTargetData(Player player){
        BossBar targetBar = profileManager.getPlayerTargetBar(player);
        LivingEntity target = targetManager.getPlayerTarget(player);
        targetBar.setTitle(getTargetData(target));
    }

    private String getTargetData(LivingEntity entity){

        if(entity == null){
            return "";
        }

        if(entityBarData.containsKey(entity.getUniqueId())){
            return entityBarData.get(entity.getUniqueId());
        }

        return "";
    }



    public void updateEntityBarInformation(LivingEntity entity){

        new BukkitRunnable(){
            @Override
            public void run(){
                StringBuilder entityData = new StringBuilder();

                if(bossManager.getIfEntityIsBoss(entity.getUniqueId())){

                    String name = entity.getName();

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
                    int nameWidth = getPixelWidth(name);
                    int padding = maxNameLength - nameWidth;

                    entityData.append(name);

                    // pad remaining space
                    for (int i = 0; i < padding; i++) {
                        entityData.append("\uF821"); // +1 pixel
                        //entityData.append("\uF801"); //-1
                        //entityData.append("|");
                        //-1 + | = +1. therefore, | = 2
                    }
                    entityBarData.put(entity.getUniqueId(), String.valueOf(entityData));

                    return;
                }

                if(profileManager.getAnyProfile(entity).fakePlayer()){

                    String name = entity.getName();

                    entityData.append(profileManager.getCompanionFace(entity.getUniqueId()));

                    entityData.append(targetHealthBar(entity));

                    //-101
                    entityData.append("\uF80B\uF80A\uF805");
                    entityData.append(name);

                    int maxNameLength = 96;
                    int nameWidth = getPixelWidth(name);
                    int padding = maxNameLength - nameWidth;

                    // pad remaining space
                    for (int i = 0; i < padding; i++) {
                        entityData.append("\uF821"); // +1 pixel
                        //entityData.append("\uF801"); //-1
                        //entityData.append("|");
                        //-1 + | = +1. therefore, | = 2
                    }

                    entityBarData.put(entity.getUniqueId(), String.valueOf(entityData));

                    return;
                }

                //TODO: player faces using the pixel system

            }
        }.runTaskAsynchronously(main);


    }

    int getPixelWidth(String text) {

        text = text.replaceAll("§.", "");

        int width = 0;

        for (char c : text.toCharArray()) {
            width += MINECRAFT_CHAR_WIDTHS.getOrDefault(c, 0)+1;
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

        if(profileManager.getAnyProfile(entity).getIfDead()){
            amount = 0;
        }

        //this needs a different bar type

        healthBar.append(targetResourceBackground);
        //-104
        healthBar.append("\uF80B\uF80A\uF808");

        healthBar.append(ChatColor.GREEN);
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

    public void displayCastBar(Player player){

        /*

        new BukkitRunnable(){
            @Override
            public void run(){

                if(!player.isOnline()){
                    return;
                }

                if(!abilityManager.getIfCasting(player) && Objects.equals(getBossWarning(player), " ")){
                    //Bukkit.getLogger().info("player stop casting");
                    player.sendTitle("", "", 0, 1, 0);
                    return;
                }

                if(abilityManager.getCastPercent(player) == 0 && Objects.equals(getBossWarning(player), " ")){
                    //Bukkit.getLogger().info("player stop casting");
                    player.sendTitle("", "", 0, 1, 0);
                    return;
                }

                StringBuilder castBarString = new StringBuilder();

                if(abilityManager.getIfCasting(player) && abilityManager.getCastPercent(player) != 0){
                    double percent =  abilityManager.getCastPercent(player);

                    double ratio = percent / 100;

                    int amount = (int) Math.ceil(ratio * 20);

                    if(amount <=0){
                        amount = 0;
                    }

                    if(amount>=20){
                        amount = 20;
                    }

                    //Bukkit.getLogger().info(String.valueOf(amount));

                    castBarString.append(castBar[amount]);
                }

                //warningmessage in first slot later
                player.sendTitle(getBossWarning(player), String.valueOf(castBarString), 0, 5, 0);
            }
        }.runTaskAsynchronously(main);

        */

    }




    public void editTeamBar(Player player){


        BossBar teamBar = profileManager.getPlayerTeamBar(player);

        new BukkitRunnable(){
            @Override
            public void run(){
                teamBar.setTitle(createTeamDataString(player));
            }
        }.runTaskAsynchronously(main);


    }
    public void editStatusBar(Player player){
        BossBar statusBar = profileManager.getPlayerStatusBar(player);

        new BukkitRunnable(){
            @Override
            public void run(){

                statusBar.setTitle(createStatusString(player));
            }
        }.runTaskAsynchronously(main);


    }

    /*private String createPlayerDataString(Player player){

        StringBuilder playerResources = new StringBuilder();

        //-512space
        playerResources.append("\uF80E");

        // +60 space
        playerResources.append("\uF82A\uF829\uF828\uF824");

        playerResources.append(createEntityDataString(player, player));


        return String.valueOf(playerResources);

    }*/



    private String createTeamDataString(Player player){


        StringBuilder teamData = new StringBuilder();


        List<LivingEntity> mysticaParty = new ArrayList<>(mysticaPartyManager.getMysticaParty(player));

        if(mysticaParty.size() == 1){
            return String.valueOf(teamData);
        }


        if(mysticaParty.size() <= 5){

            //temp comment out to see full thing

            //-512 space
            teamData.append("\uF80E");

            //-100
            teamData.append("\uF80B\uF80A\uF804");

            int slot = 0;
            for(LivingEntity member : mysticaParty){

                if(member == player){
                    continue;
                }


                //teamData.append(getTeamMemberDataString(player, slot));
                teamData.append(getTeamMemberDataString(member, slot));


                slot++;

                if(slot== mysticaParty.size()){
                    break;
                }

                //-87
                teamData.append("\uF80B\uF809\uF807");

            }

            return String.valueOf(teamData);
        }



        StringBuilder offset = new StringBuilder();


        //-512 space
        teamData.append("\uF80E");

        //-100space
        teamData.append("\uF80B\uF80A\uF804");


        //loop based on party members

        int slot = 0;
        for(LivingEntity member : mysticaParty){

            if(member == player){
                continue;
            }

            if(!(member instanceof Player memberPlayer)){
                //companions not allowed in squads
                continue;
            }

            teamData.append(getSquadMemberDataString(member, slot));

            //-32
            teamData.append("\uF80A");

            teamData.append(skinGrabber.getSquadFace(memberPlayer, slot));

            // +36
            teamData.append("\uF82A\uF824");

            //+36
            offset.append("\uF82A\uF824");



            //-64
            //teamData.append("\uF80B");


            /*if(slot == mysticaParty.size()){
                break;
            }*/

            slot ++;

            //this code is bad look away
            if(slot >= 3){
                //+1
                offset.append("\uF821");
            }

            if(slot == 3 || slot == 6){

                //-111
                teamData.append("\uF80B\uF80A\uF808\uF807");

                //+111
                //offset.append("\uF82B\uF82A\uF828\uF827");

                //-111
                offset.append("\uF80B\uF80A\uF808\uF807");

                //append ofdset later
            }


            //+64
            //teamData.append("\uF82B");

            //move it back if larger that 3
        }


        offset.append(teamData);

        return String.valueOf(offset);
    }

    private String createStatusString(Player player){
        StringBuilder status = new StringBuilder();


        if(profileManager.getAnyProfile(player).getIfDead()){
            return String.valueOf(status);
        }

        StringBuilder offset = new StringBuilder();

        /*PlayerClass playerClass = profileManager.getAnyProfile(player).getPlayerClass();
        SubClass subClass = profileManager.getAnyProfile(player).getPlayerSubclass();

        //-128
        status.append("\uF80C");

        //class specific buffs
        switch (playerClass) {
            case Elementalist -> {

                if (subClass.equals(SubClass.Pyromancer)) {

                    int inflame = abilityManager.getElementalistAbilities().getFieryWing().getInflame(player);

                    if (inflame > 0) {
                        //+16
                        offset.append("\uF829");

                        status.append("\uE000");
                        status.append(getStackString(inflame));
                    }

                }

                int breathTime = abilityManager.getElementalistAbilities().getElementalBreath().getIfBuffTime(player);
                int duration = abilityManager.getElementalistAbilities().getElementalBreath().getDuration(player);

                if (breathTime > 0) {
                    //+16
                    offset.append("\uF829");

                    status.append("\uE01C");

                    status.append(getDurationString(breathTime, duration));
                }

            }
            case Ranger -> {

                int cry = abilityManager.getRangerAbilities().getRallyingCry().getIfBuffTime(player);
                int duration = abilityManager.getRangerAbilities().getRallyingCry().getDuration();

                if (cry > 0) {
                    //+16
                    offset.append("\uF829");

                    status.append("\uE01D");

                    status.append(getDurationString(cry, duration));
                }

            }
            case Shadow_Knight -> {

                LivingEntity target = targetManager.getPlayerTarget(player);

                if (target != null) {
                    int timeLeft = abilityManager.getShadowKnightAbilities().getInfection().getPlayerInfectionTime(player);

                    if (timeLeft > 0) {

                        //+16
                        offset.append("\uF829");

                        boolean enhanced = abilityManager.getShadowKnightAbilities().getInfection().getIfEnhanced(player);

                        int duration = abilityManager.getShadowKnightAbilities().getInfection().getDuration();

                        if (enhanced) {
                            status.append("\uE021");
                        } else {
                            status.append("\uE020");
                        }

                        status.append(getDurationString(timeLeft, duration));

                    }
                }

                if (subClass.equals(SubClass.Doom)) {

                    int marks = abilityManager.getShadowKnightAbilities().getSoulReap().getSoulMarks(player);

                    switch (marks) {
                        case 1, 2, 3, 4 -> {

                            //+16
                            offset.append("\uF829");

                            status.append("\uE01E");

                            status.append(getStackString(marks));

                        }
                        case 5 -> {

                            //+16
                            offset.append("\uF829");

                            status.append("\uE01F");

                        }
                    }
                }

            }
            case Mystic -> {

                if (abilityManager.getMysticAbilities().getPurifyingBlast().getInstantCast(player)) {

                    //+16
                    offset.append("\uF829");

                    status.append("\uE022");
                }

            }
            case Assassin -> {

                int timeLeft = buffAndDebuffManager.getPierceBuff().getIfBuffTime(player);
                int max = buffAndDebuffManager.getPierceBuff().getDuration();

                if (timeLeft > 0) {
                    //+16
                    offset.append("\uF829");

                    status.append("\uE025");

                    status.append(getDurationString(timeLeft, max));
                }

                if (buffAndDebuffManager.getBladeTempestCrit().getTempestCrit(player) != 0) {
                    //+16
                    offset.append("\uF829");

                    status.append("\uE023");
                }

                if (abilityManager.getAssassinAbilities().getStealth().getIfStealthed(player)) {
                    //+16
                    offset.append("\uF829");

                    status.append("\uE024");
                }

            }
            case Warrior -> {

                if (buffAndDebuffManager.getBurningBlessingBuff().getIfHealthBuff(player)) {

                    //-16
                    offset.append("\uF829");

                    status.append("\uE026");
                }

            }
            case Paladin -> {

                if (abilityManager.getPaladinAbilities().getDecision().getDecision(player)) {

                    //-16
                    offset.append("\uF829");

                    status.append("\uE027");

                }

            }
        }


        //generic buffs/debuffs
        if(buffAndDebuffManager.getArmorBreak().getStacks(player) >= 3){
            int timeLeft = buffAndDebuffManager.getArmorBreak().getTimeLeft(player);
            int max = buffAndDebuffManager.getArmorBreak().getDuration();
            int stacks = buffAndDebuffManager.getArmorBreak().getStacks(player);
            if(timeLeft > 0){

                //-16
                offset.append("\uF829");

                status.append("\uE028");

                status.append(getDurationString(timeLeft, max));
                status.append(getStackString(stacks));

            }
        }

        if(buffAndDebuffManager.getWildRoarBuff().getBuffTime(player) > 0){

            //-16
            offset.append("\uF829");

            status.append("\uE029");

            int max = buffAndDebuffManager.getWildRoarBuff().getDuration();

            status.append(getDurationString(buffAndDebuffManager.getWildRoarBuff().getBuffTime(player), max));
        }

        if(buffAndDebuffManager.getConjuringForceBuff().getIfConjForceBuff(player)){
            //-16
            offset.append("\uF829");

            status.append("\uE02A");
        }

        if(buffAndDebuffManager.getWellCrit().getWellCrit(player) == 10){
            //-16
            offset.append("\uF829");

            status.append("\uE02B");
        }

        if(buffAndDebuffManager.getFlamingSigilBuff().getIfAttackBuff(player) || buffAndDebuffManager.getFlamingSigilBuff().getIfHealthBuff(player)){
            //-16
            offset.append("\uF829");

            status.append("\uE02C");
        }

        if(buffAndDebuffManager.getSpeedUp().getIfSpeedUp(player)){
            //-16
            offset.append("\uF829");

            status.append("\uE02D");
        }

        offset.append(status);*/

        return String.valueOf(offset);
    }

    private String getStackString(int stacks){


        StringBuilder stacksString = new StringBuilder();

        /*

        //-17
        stacksString.append("\uF809\uF801");

        //because i dont care
        if(stacks > 20){
            stacks = 20;
        }

        stacksString.append(stackAmount[stacks]);

         */

        return String.valueOf(stacksString);
    }

    private String getDurationString(int time, int max){

        StringBuilder durationString = new StringBuilder();

        /*

        int icon = iconCalculator.calculate(time, max);

        //-17
        durationString.append("\uF809\uF801");

        if(icon != 0){
            durationString.append(duration[icon-1]);
        }


         */
        return String.valueOf(durationString);
    }

    private String getTeamMemberDataString(LivingEntity entity, int slot){

        StringBuilder entityBar = new StringBuilder();

        /*

        if(entity instanceof Player player){


            entityBar.append(skinGrabber.getTeamFace(player, slot));

            //+19, 16 for face, 3 for offset
            entityBar.append("\uF829\uF823");
        }

        if(profileManager.getAnyProfile(entity).fakePlayer()){


            //+16
            //entityBar.append("\uF829");

            entityBar.append(profileManager.getCompanionTeamFace(entity.getUniqueId(), slot));


            //+3 is only because default steve face.
            entityBar.append("\uF823");
        }

        //change this to get the right value
        entityBar.append(teamHealthBar(entity, slot));


         */
        return String.valueOf(entityBar);
    }

    private String getSquadMemberDataString(LivingEntity entity, int slot){

        StringBuilder entityBar = new StringBuilder();

        /*

        //depending on class
        PlayerClass playerClass = profileManager.getAnyProfile(entity).getPlayerClass();

        switch (playerClass) {
            case Assassin -> {
                entityBar.append(ChatColor.of(assassinColor));
            }
            case Elementalist -> {
                entityBar.append(ChatColor.of(elementalistColor));
            }
            case Ranger -> {
                entityBar.append(ChatColor.of(rangerColor));
            }
            case Paladin -> {
                entityBar.append(ChatColor.of(paladinColor));
            }
            case Warrior -> {
                entityBar.append(ChatColor.of(warriorColor));
            }
            case Shadow_Knight -> {
                entityBar.append(ChatColor.of(shadowKnightColor));
            }
            case Mystic -> {
                entityBar.append(ChatColor.of(mysticColor));
            }
            default -> {
                entityBar.append(ChatColor.RESET);
            }
        }


        //depending on health, get a different unicode
        Profile playerProfile = profileManager.getAnyProfile(entity);
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

        //slot switch here for height
        switch (slot) {
            case 0, 1, 2 -> {
                entityBar.append(squadHealth0[amount]);
            }
            case 3, 4, 5 -> {
                entityBar.append(squadHealth1[amount]);
            }
            case 6, 7, 8 -> {
                entityBar.append(squadHealth2[amount]);
            }
        }



       */

        return String.valueOf(entityBar);
    }




    private String playerAndTargetIcon(LivingEntity entity){

        StringBuilder icon = new StringBuilder();

        /*

        if(entity instanceof Player player){

            PlayerClass playerClass = profileManager.getAnyProfile(player).getPlayerClass();


            //frame color
            switch (playerClass) {
                case Assassin -> {
                    icon.append(ChatColor.of(assassinColor));
                }
                case Elementalist -> {
                    icon.append(ChatColor.of(elementalistColor));
                }
                case Mystic -> {
                    icon.append(ChatColor.of(mysticColor));
                }
                case Paladin -> {
                    icon.append(ChatColor.of(paladinColor));
                }
                case Ranger -> {
                    icon.append(ChatColor.of(rangerColor));
                }
                case Shadow_Knight -> {
                    icon.append(ChatColor.of(shadowKnightColor));
                }
                case Warrior -> {
                    icon.append(ChatColor.of(warriorColor));
                }
            }

            //background
            icon.append("\uE14D");


            //-43
            icon.append("\uF80A\uF808\uF803");

            //frame
            icon.append(ChatColor.RESET);
            icon.append("\uE143");

            //-35
            icon.append("\uF80A\uF803");

            icon.append(skinGrabber.getFace(player));

            //-10
            icon.append("\uF808\uF802");

            icon.append(profileManager.getAnyProfile(player).getStats().getLevel());

            //+38
            icon.append("\uF82A\uF826");


            return String.valueOf(icon);
        }


        if(profileManager.getAnyProfile(entity).fakePlayer()){

            PlayerClass playerClass = profileManager.getAnyProfile(entity).getPlayerClass();


            //frame color
            switch (playerClass) {
                case Assassin -> {
                    icon.append(ChatColor.of(assassinColor));
                }
                case Elementalist -> {
                    icon.append(ChatColor.of(elementalistColor));
                }
                case Mystic -> {
                    icon.append(ChatColor.of(mysticColor));
                }
                case Paladin -> {
                    icon.append(ChatColor.of(paladinColor));
                }
                case Ranger -> {
                    icon.append(ChatColor.of(rangerColor));
                }
                case Shadow_Knight -> {
                    icon.append(ChatColor.of(shadowKnightColor));
                }
                case Warrior -> {
                    icon.append(ChatColor.of(warriorColor));
                }
            }

            //background
            icon.append("\uE14D");


            //-43
            icon.append("\uF80A\uF808\uF803");

            //frame
            icon.append(ChatColor.RESET);
            icon.append("\uE143");

            //-35
            icon.append("\uF80A\uF803");

            String face = profileManager.getCompanionFace(entity.getUniqueId());
            icon.append(face);

            //-36
            icon.append("\uF80A\uF804");

            icon.append(profileManager.getAnyProfile(entity).getStats().getLevel());

            //+40
            icon.append("\uF82A\uF828");

            return String.valueOf(icon);

        }

        if(gravestoneManager.isGravestone(entity)){


            //can't call async
            Player player = gravestoneManager.getPlayer(entity);

            PlayerClass playerClass = profileManager.getAnyProfile(player).getPlayerClass();


            //frame color
            switch (playerClass) {
                case Assassin -> {
                    icon.append(ChatColor.of(assassinColor));
                }
                case Elementalist -> {
                    icon.append(ChatColor.of(elementalistColor));
                }
                case Mystic -> {
                    icon.append(ChatColor.of(mysticColor));
                }
                case Paladin -> {
                    icon.append(ChatColor.of(paladinColor));
                }
                case Ranger -> {
                    icon.append(ChatColor.of(rangerColor));
                }
                case Shadow_Knight -> {
                    icon.append(ChatColor.of(shadowKnightColor));
                }
                case Warrior -> {
                    icon.append(ChatColor.of(warriorColor));
                }
            }

            //background
            icon.append("\uE14D");


            //-43
            icon.append("\uF80A\uF808\uF803");

            //frame
            icon.append(ChatColor.RESET);
            icon.append("\uE143");

            //-35
            icon.append("\uF80A\uF803");

            icon.append(skinGrabber.getFace(player));

            //-10
            icon.append("\uF808\uF802");

            icon.append(profileManager.getAnyProfile(player).getStats().getLevel());

            //+38
            icon.append("\uF82A\uF826");


            return String.valueOf(icon);
        }

        if(!profileManager.getAnyProfile(entity).getIsPassive()){

            icon.append(profileManager.getBossIcon(entity.getUniqueId()));

            //-43
            icon.append("\uF80A\uF808\uF803");

            //frame
            icon.append(ChatColor.RESET);
            icon.append("\uE178");

            //-46
            icon.append("\uF80A\uF808\uF806");

            icon.append(profileManager.getAnyProfile(entity).getStats().getLevel());

            //+40
            icon.append("\uF82A\uF828");


            return String.valueOf(icon);
        }

        if(profileManager.getAnyProfile(entity).getIsPassive()){

            icon.append(profileManager.getPassiveIcon(entity.getUniqueId()));

            //-43
            icon.append("\uF80A\uF808\uF803");

            //frame
            icon.append(ChatColor.RESET);
            icon.append(("\uE143"));

            //-46
            icon.append("\uF80A\uF808\uF806");

            icon.append(profileManager.getAnyProfile(entity).getStats().getLevel());

            //+40
            icon.append("\uF82A\uF828");
            return String.valueOf(icon);
        }


         */

        return String.valueOf(icon);
    }


    private String teamHealthBar(LivingEntity entity, int slot){
        StringBuilder healthBar = new StringBuilder();

        /*

        Profile playerProfile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = playerProfile.getTotalHealth() + statusEffectManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();
        double ratio = actualCurrentHealth / actualMaxHealth;
        int amount = (int) Math.ceil(ratio * 20);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 20){
            amount = 20;
        }

        if(playerProfile.getIfDead()){
            amount = 0;
        }

        double maxHp = profileManager.getAnyProfile(entity).getTotalHealth();
        double shield = statusEffectManager.getTotalShield(entity);
        double shieldRatio = shield/ maxHp;

        int shieldAmount = (int) Math.ceil(shieldRatio * 20);

        if(shieldAmount < 0){
            shieldAmount = 0;
        }

        if(shieldAmount > 20){
            shieldAmount = 20;
        }

        switch (slot) {
            case 0 -> {
                healthBar.append(teamHealthBar0[amount]);

                if (shieldAmount != 0) {
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                    healthBar.append(teamShieldBar0[shieldAmount-1]);
                }

            }
            case 1 -> {
                healthBar.append(teamHealthBar1[amount]);

                if (shieldAmount != 0) {
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                    healthBar.append(teamShieldBar1[shieldAmount-1]);
                }

            }
            case 3 -> {
                healthBar.append(teamHealthBar3[amount]);

                if (shieldAmount != 0) {
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                    healthBar.append(teamShieldBar3[shieldAmount-1]);
                }

            }
            case 2 -> {
                healthBar.append(teamHealthBar2[amount]);

                if (shieldAmount != 0) {
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                    healthBar.append(teamShieldBar2[shieldAmount-1]);
                }

            }
        }



         */
        return String.valueOf(healthBar);
    }

    private String resourceBar(LivingEntity entity){

        StringBuilder resourceBar = new StringBuilder();

        /*

        if(entity instanceof Player || profileManager.getAnyProfile(entity).fakePlayer()){
            Profile playerProfile = profileManager.getAnyProfile(entity);
            PlayerClass playerClass = playerProfile.getPlayerClass();
            switch (playerClass) {
                case Mystic -> {

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxMana = 500;
                    double currentMana = abilityManager.getMysticAbilities().getMana().getCurrentMana(entity);

                    double ratio = currentMana / maxMana;

                    int amount = (int) Math.ceil(ratio * 20);

                    if (amount < 0) {
                        amount = 0;
                    }

                    if (currentMana <= 0) {
                        amount = 0;
                    }
                    resourceBar.append(manaBar[amount]);

                }
                case Warrior -> {
                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxRage = 500;
                    double currentRage = abilityManager.getWarriorAbilities().getRage().getCurrentRage(entity);

                    double ratio = currentRage / maxRage;

                    int amount = (int) Math.ceil(ratio * 20);

                    if (amount < 0) {
                        amount = 0;
                    }

                    if (currentRage <= 0) {
                        amount = 0;
                    }

                    resourceBar.append(rageBar[amount]);

                }
                case Shadow_Knight -> {
                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxEnergy = 100;
                    double currentEnergy = abilityManager.getShadowKnightAbilities().getEnergy().getCurrentEnergy(entity);

                    double ratio = currentEnergy / maxEnergy;

                    int amount = (int) Math.ceil(ratio * 10);

                    if (amount < 0) {
                        amount = 0;
                    }

                    if (currentEnergy <= 0) {
                        amount = 0;
                    }

                    resourceBar.append(energyBar[amount]);

                }
                case Ranger -> {

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxFocus = 10;
                    double currentFocus = abilityManager.getRangerAbilities().getFocus().getFocus(entity);

                    double ratio = currentFocus / maxFocus;

                    int amount = (int) Math.ceil(ratio * 3);

                    if (amount < 0) {
                        amount = 0;
                    }

                    if (currentFocus <= 0) {
                        amount = 0;
                    }

                    switch (amount) {
                        case 3 -> {
                            resourceBar.append("\uE100");
                        }
                        case 2 -> {
                            resourceBar.append("\uE101");
                        }
                        case 1 -> {
                            resourceBar.append("\uE102");
                        }
                        case 0 -> {
                            resourceBar.append("\uE0E1");
                        }
                    }

                }
                case Paladin -> {

                    if(profileManager.getAnyProfile(entity).getPlayerSubclass().equals(SubClass.Dawn)){
                        //-128 space
                        resourceBar.append("\uF80C");

                        //-4 space
                        resourceBar.append("\uF804");

                        int current = abilityManager.getPaladinAbilities().getPurity().get(entity);

                        if(abilityManager.getPaladinAbilities().getPurity().active(entity)){
                            current = 3;
                        }

                        switch (current) {
                            case 3 -> {
                                resourceBar.append("\uE100");
                            }
                            case 2 -> {
                                resourceBar.append("\uE101");
                            }
                            case 1 -> {
                                resourceBar.append("\uE102");
                            }
                            case 0 -> {
                                resourceBar.append("\uE0E1");
                            }
                        }
                    }



                }
                case Assassin -> {

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    int combo = abilityManager.getAssassinAbilities().getCombo().getComboPoints(entity);

                    if (profileManager.getAnyProfile(entity).getPlayerSubclass().equals(SubClass.Duelist)) {


                        switch (combo) {
                            case 0 -> {
                                resourceBar.append("\uE108");
                            }
                            case 1 -> {
                                resourceBar.append("\uE109");
                            }
                            case 2 -> {
                                resourceBar.append("\uE10A");
                            }
                            case 3 -> {
                                resourceBar.append("\uE10B");
                            }
                            case 4 -> {
                                resourceBar.append("\uE10C");
                            }
                            case 5 -> {
                                resourceBar.append("\uE06A");
                            }
                        }

                        break;
                    }

                    switch (combo) {
                        case 0 -> {
                            resourceBar.append("\uE103");
                        }
                        case 1 -> {
                            resourceBar.append("\uE104");
                        }
                        case 2 -> {
                            resourceBar.append("\uE105");
                        }
                        case 3 -> {
                            resourceBar.append("\uE106");
                        }
                        case 4 -> {
                            resourceBar.append("\uE107");
                        }
                    }

                }
                case Elementalist -> {

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double currentHeat = abilityManager.getElementalistAbilities().getHeat().getHeat(entity);

                    if (currentHeat < 50) {
                        resourceBar.append("\uE10D");
                        return String.valueOf(resourceBar);
                    }

                    if (currentHeat < 90) {
                        resourceBar.append("\uE10E");
                        return String.valueOf(resourceBar);
                    }


                    resourceBar.append("\uE10F");


                }
            }



            return String.valueOf(resourceBar);

        }

        //-128 space
        resourceBar.append("\uF80C");

        //-4 space
        resourceBar.append("\uF804");

        int amount = 0;

        if(bossCastingManager.bossIsCasting(entity)){
            double max = bossCastingManager.getCastMax(entity);
            double current = bossCastingManager.getCastPercent(entity);

            double ratio = current / max;

            amount = (int) Math.ceil(ratio * 20);

            if(amount < 0){
                amount = 0;
            }

            if(current <= 0){
                amount = 0;
            }
        }

        resourceBar.append(rageBar[amount]);


         */
        return String.valueOf(resourceBar);
    }



    private String getUltimateStatus(Player player){

        StringBuilder ultimateStatus = new StringBuilder();

        /*

        boolean combatStatus = profileManager.getAnyProfile(player).getIfInCombat();

        if(!combatStatus){
            return " ";
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return " ";
        }

        //-256
        ultimateStatus.append("\uF80D");

        //+37
        ultimateStatus.append("\uF82A\uF825");


        //move it again, if they have
        //slot on hud
        ultimateStatus.append("\uE12D");

        if(!allSkillItems.getUltimate(player).hasItemMeta()){
            return String.valueOf(ultimateStatus);
        }

        ItemStack ultimateItem = allSkillItems.getUltimate(player);

        if(ultimateItem.getType().equals(Material.AIR)){
            return String.valueOf(ultimateStatus);
        }

        if(!ultimateItem.hasItemMeta()){
            return String.valueOf(ultimateStatus);
        }

        ItemMeta ultimateMeta = ultimateItem.getItemMeta();

        assert ultimateMeta != null;
        if(!ultimateMeta.hasDisplayName()){
            return String.valueOf(ultimateStatus);
        }


        //-20
        ultimateStatus.append("\uF809\uF804");

        String abilityName = ultimateMeta.getDisplayName();
        abilityName = abilityName.replaceAll("§.", "");
        ultimateStatus.append(abilityUnicode(abilityName, player));

        //-17
        ultimateStatus.append("\uF809\uF801");

        //get how much cooldown is left, just here for testing
        ultimateStatus.append(ultimateCooldown(player));



         */
        return String.valueOf(ultimateStatus);
    }

    private String abilityUnicode(String abilityName, Player player){

        StringBuilder unicode = new StringBuilder();

        /*

        switch (abilityName.toLowerCase()) {
            case "conjuring force" -> {
                unicode.append("\uE043");
            }
            case "fiery wing" -> {
                unicode.append("\uE044");
            }
            case "wild roar" -> {
                unicode.append("\uE045");
            }
            case "star volley" -> {
                unicode.append("\uE040");
            }
            case "annihilation" -> {

                if (abilityManager.getShadowKnightAbilities().getAnnihilation().returnWhichItem(player) == 0) {
                    unicode.append("\uE12E");
                } else {
                    unicode.append("\uE06D");
                }


            }
            case "blood shield" -> {

                if (abilityManager.getShadowKnightAbilities().getBloodShield().returnWhichItem(player) == 0) {
                    unicode.append("\uE042");
                } else {
                    unicode.append("\uE06E");
                }

            }
            case "arcane missiles" -> {
                unicode.append("\uE046");
            }
            case "enlightenment" -> {

                if (abilityManager.getMysticAbilities().getEnlightenment().returnWhichItem(player) == 0) {
                    unicode.append("\uE047");
                } else {
                    unicode.append("\uE06C");
                }

            }
            case "duelist's frenzy" -> {


                if (abilityManager.getAssassinAbilities().getDuelistsFrenzy().returnWhichItem(player) == 0) {
                    unicode.append("\uE049");
                } else {
                    unicode.append("\uE04A");
                }


            }
            case "wicked concoction" -> {
                unicode.append("\uE04B");
            }
            case "gladiator heart" -> {
                unicode.append("\uE058");
            }
            case "death gaze" -> {
                unicode.append("\uE059");
            }
            case "well of light" -> {
                unicode.append("\uE067");
            }
            case "shield of sanctity" -> {
                unicode.append("\uE068");
            }
            case "representative" -> {
                unicode.append("\uE069");
            }
        }


         */
        return String.valueOf(unicode);
    }

    /*private String ultimateCooldown(Player player){


        int percent = iconCalculator.calculate(abilityManager.getPlayerUltimateCooldown(player), abilityManager.getUltimateCooldown(player));

        return ultimateCooldown[percent];


    }*/

    private String getBossWarning(Player player){
        return bossWarningSender.getWarning(player);
    }


}
