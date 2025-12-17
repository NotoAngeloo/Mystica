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
import me.angeloo.mystica.Utility.Logic.DamageBoardPlaceholders;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;
import java.util.List;

import static me.angeloo.mystica.Mystica.*;


public class HudManager {

    private final Mystica main;

    private final ProfileManager profileManager;
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


    private final Map<Character, Integer> MINECRAFT_CHAR_WIDTHS = Map.<Character, Integer>ofEntries(
            Map.entry(' ', 4), Map.entry('!', 2), Map.entry('"', 5), Map.entry('#', 6),
            Map.entry('$', 6), Map.entry('%', 6), Map.entry('&', 6), Map.entry('\'', 3),
            Map.entry('(', 5), Map.entry(')', 5), Map.entry('*', 5), Map.entry('+', 6),
            Map.entry(',', 2), Map.entry('-', 6), Map.entry('.', 2), Map.entry('/', 6),
            Map.entry('0', 6), Map.entry('1', 6), Map.entry('2', 6), Map.entry('3', 6),
            Map.entry('4', 6), Map.entry('5', 6), Map.entry('6', 6), Map.entry('7', 6),
            Map.entry('8', 6), Map.entry('9', 6), Map.entry(':', 2), Map.entry(';', 2),
            Map.entry('<', 5), Map.entry('=', 6), Map.entry('>', 5), Map.entry('?', 6),
            Map.entry('@', 7), Map.entry('A', 6), Map.entry('B', 6), Map.entry('C', 6),
            Map.entry('D', 6), Map.entry('E', 6), Map.entry('F', 6), Map.entry('G', 6),
            Map.entry('H', 6), Map.entry('I', 4), Map.entry('J', 6), Map.entry('K', 6),
            Map.entry('L', 6), Map.entry('M', 6), Map.entry('N', 6), Map.entry('O', 6),
            Map.entry('P', 6), Map.entry('Q', 6), Map.entry('R', 6), Map.entry('S', 6),
            Map.entry('T', 6), Map.entry('U', 6), Map.entry('V', 6), Map.entry('W', 6),
            Map.entry('X', 6), Map.entry('Y', 6), Map.entry('Z', 6), Map.entry('[', 4),
            Map.entry('\\', 6), Map.entry(']', 4), Map.entry('^', 6), Map.entry('_', 6),
            Map.entry('`', 3), Map.entry('a', 6), Map.entry('b', 6), Map.entry('c', 6),
            Map.entry('d', 6), Map.entry('e', 6), Map.entry('f', 5), Map.entry('g', 6),
            Map.entry('h', 6), Map.entry('i', 2), Map.entry('j', 6), Map.entry('k', 5),
            Map.entry('l', 3), Map.entry('m', 6), Map.entry('n', 6), Map.entry('o', 6),
            Map.entry('p', 6), Map.entry('q', 6), Map.entry('r', 5), Map.entry('s', 6),
            Map.entry('t', 4), Map.entry('u', 6), Map.entry('v', 6), Map.entry('w', 6),
            Map.entry('x', 6), Map.entry('y', 6), Map.entry('z', 6), Map.entry('{', 5),
            Map.entry('|', 2), Map.entry('}', 5), Map.entry('~', 7)
    );

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
    private final String[] squadHealth2 = {"\uE009","\uE22B","\uE22A","\uE229","\uE228","\uE227","\uE226","\uE225","\uE224"};

    public HudManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
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
                //bar 1, player resources
                BossBar resourceBar = Bukkit.createBossBar(createPlayerDataString(player), BarColor.WHITE, BarStyle.SOLID);
                resourceBar.addPlayer(player);
                resourceBar.setVisible(true);
                profileManager.setPlayerResourceBar(player, resourceBar);

                //bar 2, target bar
                BossBar targetBar = Bukkit.createBossBar(createTargetDataString(player, target), BarColor.WHITE, BarStyle.SOLID);
                targetBar.addPlayer(player);
                targetBar.setVisible(true);
                profileManager.setPlayerTargetBar(player, targetBar);


                //team data
                BossBar teamBar = Bukkit.createBossBar(createTeamDataString(player), BarColor.WHITE, BarStyle.SOLID);
                teamBar.addPlayer(player);
                teamBar.setVisible(true);
                profileManager.setPlayerTeamBar(player, teamBar);


                //status
                BossBar statusBar = Bukkit.createBossBar("", BarColor.WHITE, BarStyle.SOLID);
                statusBar.addPlayer(player);
                statusBar.setVisible(true);
                profileManager.setPlayerStatusBar(player, statusBar);
            }
        }.runTaskAsynchronously(main);

    }

    public void displayUltimate(Player player){

        new BukkitRunnable(){
            @Override
            public void run(){
                StringBuilder hotBar = new StringBuilder();

                String statusString = getUltimateStatus(player);

                hotBar.append(statusString);

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(hotBar)));
            }
        }.runTaskAsynchronously(main);


    }


    public void displayCastBar(Player player){

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


    }

    public void editResourceBar(Player player){
        BossBar resourceBar = profileManager.getPlayerResourceBar(player);

        new BukkitRunnable(){
            @Override
            public void run(){
                resourceBar.setTitle(createPlayerDataString(player));
            }
        }.runTaskAsynchronously(main);

    }
    public void editTargetBar(Player player){


        BossBar targetBar = profileManager.getPlayerTargetBar(player);

        LivingEntity target = targetManager.getPlayerTarget(player);

        new BukkitRunnable(){
            @Override
            public void run(){
                targetBar.setTitle(createTargetDataString(player, target));
            }
        }.runTaskAsynchronously(main);


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




    private String createPlayerDataString(Player player){

        StringBuilder playerResources = new StringBuilder();

        //-512space
        playerResources.append("\uF80E");

        // +60 space
        playerResources.append("\uF82A\uF829\uF828\uF824");

        playerResources.append(createEntityDataString(player, player));


        return String.valueOf(playerResources);

    }

    private String createTargetDataString(Player player, LivingEntity target){

        StringBuilder targetData = new StringBuilder();

        if(target != null){
            targetData.append(createEntityDataString(player, target));
        }

        return String.valueOf(targetData);
    }

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

        //-17
        stacksString.append("\uF809\uF801");

        //because i dont care
        if(stacks > 20){
            stacks = 20;
        }

        stacksString.append(stackAmount[stacks]);


        return String.valueOf(stacksString);
    }

    private String getDurationString(int time, int max){

        StringBuilder durationString = new StringBuilder();

        int icon = iconCalculator.calculate(time, max);

        //-17
        durationString.append("\uF809\uF801");

        if(icon != 0){
            durationString.append(duration[icon-1]);
        }


        return String.valueOf(durationString);
    }

    private String getTeamMemberDataString(LivingEntity entity, int slot){

        StringBuilder entityBar = new StringBuilder();


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

        return String.valueOf(entityBar);
    }

    private String getSquadMemberDataString(LivingEntity entity, int slot){

        StringBuilder entityBar = new StringBuilder();

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
        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
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





        return String.valueOf(entityBar);
    }

    private String createEntityDataString(Player barSeer, LivingEntity barEntity){

        StringBuilder entityData = new StringBuilder();

        if(barSeer != barEntity){
            int width = getPixelWidth(barEntity.getName());

            //+1
            entityData.append("\uF821".repeat(width));

        }

        entityData.append(playerAndTargetIcon(barEntity));
        entityData.append(healthBar(barEntity));
        entityData.append(resourceBar(barEntity));
        entityData.append(getNameString(barSeer, barEntity));

        return String.valueOf(entityData);
    }

    int getPixelWidth(String text) {

        int width = 0;
        for (char c : text.toCharArray()) {
            width += this.MINECRAFT_CHAR_WIDTHS.getOrDefault(c, 0);
        }
        return width;
    }

    private String playerAndTargetIcon(LivingEntity entity){

        StringBuilder icon = new StringBuilder();

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


        return String.valueOf(icon);
    }



    private String healthBar(LivingEntity entity){

        StringBuilder healthBar = new StringBuilder();

        Profile playerProfile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
        double actualCurrentHealth = profileManager.getAnyProfile(entity).getCurrentHealth();

        double ratio = actualCurrentHealth / actualMaxHealth;

        int amount = (int) Math.ceil(ratio * 20);

        //Bukkit.getLogger().info("player ratio number " + amount);

        if(amount < 0){
            amount = 0;
        }

        if(actualCurrentHealth <= 0){
            amount = 0;
        }

        if(amount > 20){
            amount = 20;
        }

        if(profileManager.getAnyProfile(entity).getIfDead()){
            amount = 0;
        }

        if(gravestoneManager.isGravestone(entity)){
            amount = 0;
        }

        healthBar.append(playerTargetHealthBar[amount]);

        double maxHp = profileManager.getAnyProfile(entity).getTotalHealth();
        double shield = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(entity) +
                buffAndDebuffManager.getWindWallBuff().getWallHealth(entity);
        double shieldRatio = shield/ maxHp;

        int shieldAmount = (int) Math.ceil(shieldRatio * 20);

        if(shieldAmount < 0){
            shieldAmount = 0;
        }

        if(shieldAmount > 20){
            shieldAmount = 20;
        }

        if(shieldAmount != 0){
            //-128 space
            healthBar.append("\uF80C");

            //-4 space
            healthBar.append("\uF804");

            healthBar.append(playerTargetShieldBar[shieldAmount-1]);

        }

        return String.valueOf(healthBar);
    }

    private String teamHealthBar(LivingEntity entity, int slot){
        StringBuilder healthBar = new StringBuilder();

        Profile playerProfile = profileManager.getAnyProfile(entity);
        double actualMaxHealth = playerProfile.getTotalHealth() + buffAndDebuffManager.getHealthBuffAmount(entity);
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
        double shield = buffAndDebuffManager.getGenericShield().getCurrentShieldAmount(entity) +
                buffAndDebuffManager.getWindWallBuff().getWallHealth(entity);
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


        return String.valueOf(healthBar);
    }

    private String resourceBar(LivingEntity entity){

        StringBuilder resourceBar = new StringBuilder();

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

        return String.valueOf(resourceBar);
    }

    private String getNameString(Player player, LivingEntity entity){

        StringBuilder nameString = new StringBuilder();

        if(player == entity){
            return String.valueOf(nameString);
        }

        //-128 offset
        nameString.append("\uF80C" + entity.getName() + "\uF82C");

        //nameString.append(entity.getName());

        return String.valueOf(nameString);
    }

    private String getUltimateStatus(Player player){

        StringBuilder ultimateStatus = new StringBuilder();

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


        return String.valueOf(ultimateStatus);
    }

    private String abilityUnicode(String abilityName, Player player){

        StringBuilder unicode = new StringBuilder();

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

        return String.valueOf(unicode);
    }

    private String ultimateCooldown(Player player){


        int percent = iconCalculator.calculate(abilityManager.getPlayerUltimateCooldown(player), abilityManager.getUltimateCooldown(player));

        //Bukkit.getLogger().info("percent " + percent);

        return ultimateCooldown[percent];


    }

    private String getBossWarning(Player player){
        return bossWarningSender.getWarning(player);
    }


}
