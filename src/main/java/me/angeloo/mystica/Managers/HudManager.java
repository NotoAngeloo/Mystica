package me.angeloo.mystica.Managers;

import me.angeloo.mystica.Components.ClassSkillItems.AllSkillItems;
import me.angeloo.mystica.Components.Profile;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Hud.IconCalculator;
import me.angeloo.mystica.Utility.Hud.SkinGrabber;
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


    private final AllSkillItems allSkillItems;
    private final AbilityManager abilityManager;
    private final BuffAndDebuffManager buffAndDebuffManager;
    private final TargetManager targetManager;
    private final BossCastingManager bossCastingManager;
    private final IconCalculator iconCalculator;

    private final SkinGrabber skinGrabber;


    private final Map<UUID, Long> lastTargetBarUpdate = new HashMap<>();
    private final Map<UUID, Long> lastTeamBarUpdate = new HashMap<>();

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

    public HudManager(Mystica main){
        this.main = main;
        profileManager = main.getProfileManager();
        damageBoardPlaceholders = new DamageBoardPlaceholders(main);
        mysticaPartyManager = main.getMysticaPartyManager();
        abilityManager = main.getAbilityManager();
        allSkillItems = main.getAllSkillItems();
        buffAndDebuffManager = main.getBuffAndDebuffManager();
        targetManager = main.getTargetManager();
        bossCastingManager = main.getBossCastingManager();

        iconCalculator = new IconCalculator();
        skinGrabber = new SkinGrabber();
    }

    public DamageBoardPlaceholders getDamageBoardPlaceholders(){
        return damageBoardPlaceholders;
    }

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
                if(!abilityManager.getIfCasting(player)){
                    //Bukkit.getLogger().info("player stop casting");
                    player.sendTitle("", "", 0, 1, 0);
                    return;
                }

                if(abilityManager.getCastPercent(player) == 0){
                    //Bukkit.getLogger().info("player stop casting");
                    player.sendTitle("", "", 0, 1, 0);
                    return;
                }

                StringBuilder castBar = new StringBuilder();

                double percent =  abilityManager.getCastPercent(player);

                double ratio = percent / 100;

                int amount = (int) Math.ceil(ratio * 20);

                //Bukkit.getLogger().info(String.valueOf(amount));

                switch (amount){
                    case 20:{
                        castBar.append("\uE0CD");
                        break;
                    }
                    case 19:{
                        castBar.append("\uE0CE");
                        break;
                    }
                    case 18:{
                        castBar.append("\uE0CF");
                        break;
                    }
                    case 17:{
                        castBar.append("\uE0D0");
                        break;
                    }
                    case 16:{
                        castBar.append("\uE0D1");
                        break;
                    }
                    case 15:{
                        castBar.append("\uE0D2");
                        break;
                    }
                    case 14:{
                        castBar.append("\uE0D3");
                        break;
                    }
                    case 13:{
                        castBar.append("\uE0D4");
                        break;
                    }
                    case 12:{
                        castBar.append("\uE0D5");
                        break;
                    }
                    case 11:{
                        castBar.append("\uE0D6");
                        break;
                    }
                    case 10:{
                        castBar.append("\uE0D7");
                        break;
                    }
                    case 9:{
                        castBar.append("\uE0D8");
                        break;
                    }
                    case 8:{
                        castBar.append("\uE0D9");
                        break;
                    }
                    case 7:{
                        castBar.append("\uE0DA");
                        break;
                    }
                    case 6:{
                        castBar.append("\uE0DB");
                        break;
                    }
                    case 5:{
                        castBar.append("\uE0DC");
                        break;
                    }
                    case 4:{
                        castBar.append("\uE0DD");
                        break;
                    }
                    case 3:{
                        castBar.append("\uE0DE");
                        break;
                    }
                    case 2:{
                        castBar.append("\uE0DF");
                        break;
                    }
                    case 1:{
                        castBar.append("\uE0E0");
                        break;
                    }
                }

                //Bukkit.getLogger().info(String.valueOf(percent));



                player.sendTitle(" ", String.valueOf(castBar), 0, 2, 0);
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
    public void editTargetBar(Player player, boolean force){

        if(!force){
            if(getTimeSinceTarget(player) < 1){
                return;
            }
        }

        BossBar targetBar = profileManager.getPlayerTargetBar(player);

        LivingEntity target = targetManager.getPlayerTarget(player);

        new BukkitRunnable(){
            @Override
            public void run(){
                targetBar.setTitle(createTargetDataString(player, target));
            }
        }.runTaskAsynchronously(main);


        lastTargetBarUpdate.put(player.getUniqueId(), System.currentTimeMillis());
    }
    public void editTeamBar(Player player, boolean force){

        if(!force){
            if(getTimeSinceTeam(player) < 1){
                return;
            }
        }
        BossBar teamBar = profileManager.getPlayerTeamBar(player);

        new BukkitRunnable(){
            @Override
            public void run(){
                teamBar.setTitle(createTeamDataString(player));
            }
        }.runTaskAsynchronously(main);


        lastTeamBarUpdate.put(player.getUniqueId(), System.currentTimeMillis());
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


    private long getTimeSinceTeam(Player player){

        long now = System.currentTimeMillis();

        long last;

        if(!lastTeamBarUpdate.containsKey(player.getUniqueId())){
            lastTeamBarUpdate.put(player.getUniqueId(), now);
        }

        last = lastTeamBarUpdate.get(player.getUniqueId());


        return ((now-last) / 1000);
    }

    private long getTimeSinceTarget(Player player){

        long now = System.currentTimeMillis();

        long last;

        if(!lastTargetBarUpdate.containsKey(player.getUniqueId())){
            lastTargetBarUpdate.put(player.getUniqueId(), now);
        }

        last = lastTargetBarUpdate.get(player.getUniqueId());


        return ((now-last) / 1000);
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

        if(!profileManager.getAnyProfile(player).getIfInCombat()){
            return String.valueOf(status);
        }

        if(profileManager.getAnyProfile(player).getIfDead()){
            return String.valueOf(status);
        }

        StringBuilder offset = new StringBuilder();

        PlayerClass playerClass = profileManager.getAnyProfile(player).getPlayerClass();
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

        offset.append(status);

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

        switch (stacks) {
            case 0, 1 -> {
                stacksString.append("\uE008");
            }
            case 2 -> {
                stacksString.append("\uE009");
            }
            case 3 -> {
                stacksString.append("\uE00A");
            }
            case 4 -> {
                stacksString.append("\uE00B");
            }
            case 5 -> {
                stacksString.append("\uE00C");
            }
            case 6 -> {
                stacksString.append("\uE00D");
            }
            case 7 -> {
                stacksString.append("\uE00E");
            }
            case 8 -> {
                stacksString.append("\uE00F");
            }
            case 9 -> {
                stacksString.append("\uE010");
            }
            case 10 -> {
                stacksString.append("\uE011");
            }
            case 11 -> {
                stacksString.append("\uE012");
            }
            case 12 -> {
                stacksString.append("\uE013");
            }
            case 13 -> {
                stacksString.append("\uE014");
            }
            case 14 -> {
                stacksString.append("\uE015");
            }
            case 15 -> {
                stacksString.append("\uE016");
            }
            case 16 -> {
                stacksString.append("\uE017");
            }
            case 17 -> {
                stacksString.append("\uE018");
            }
            case 18 -> {
                stacksString.append("\uE019");
            }
            case 19 -> {
                stacksString.append("\uE01A");
            }
            case 20 -> {
                stacksString.append("\uE01B");
            }
        }



        return String.valueOf(stacksString);
    }

    private String getDurationString(int time, int max){

        StringBuilder durationString = new StringBuilder();

        int icon = iconCalculator.calculate(time, max);

        //-17
        durationString.append("\uF809\uF801");

        switch (icon) {
            case 8 -> {
                durationString.append("\uE008");
            }
            case 7 -> {
                durationString.append("\uE007");
            }
            case 6 -> {
                durationString.append("\uE006");
            }
            case 5 -> {
                durationString.append("\uE005");
            }
            case 4 -> {
                durationString.append("\uE004");
            }
            case 3 -> {
                durationString.append("\uE003");
            }
            case 2 -> {
                durationString.append("\uE002");
            }
            case 1 -> {
                durationString.append("\uE001");
            }
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
                switch (amount) {
                    case 8 -> {
                        entityBar.append("\uE212");
                    }
                    case 7 -> {
                        entityBar.append("\uE213");
                    }
                    case 6 -> {
                        entityBar.append("\uE214");
                    }
                    case 5 -> {
                        entityBar.append("\uE215");
                    }
                    case 4 -> {
                        entityBar.append("\uE216");
                    }
                    case 3 -> {
                        entityBar.append("\uE217");
                    }
                    case 2 -> {
                        entityBar.append("\uE218");
                    }
                    case 1 -> {
                        entityBar.append("\uE219");
                    }
                    case 0 -> {
                        entityBar.append("\uE21A");
                    }
                }
            }
            case 3, 4, 5 -> {
                switch (amount) {
                    case 8 -> {
                        entityBar.append("\uE21B");
                    }
                    case 7 -> {
                        entityBar.append("\uE21C");
                    }
                    case 6 -> {
                        entityBar.append("\uE21D");
                    }
                    case 5 -> {
                        entityBar.append("\uE21E");
                    }
                    case 4 -> {
                        entityBar.append("\uE21F");
                    }
                    case 3 -> {
                        entityBar.append("\uE220");
                    }
                    case 2 -> {
                        entityBar.append("\uE221");
                    }
                    case 1 -> {
                        entityBar.append("\uE222");
                    }
                    case 0 -> {
                        entityBar.append("\uE223");
                    }
                }
            }
            case 6, 7, 8 -> {
                switch (amount) {
                    case 8 -> {
                        entityBar.append("\uE224");
                    }
                    case 7 -> {
                        entityBar.append("\uE225");
                    }
                    case 6 -> {
                        entityBar.append("\uE226");
                    }
                    case 5 -> {
                        entityBar.append("\uE227");
                    }
                    case 4 -> {
                        entityBar.append("\uE228");
                    }
                    case 3 -> {
                        entityBar.append("\uE229");
                    }
                    case 2 -> {
                        entityBar.append("\uE22A");
                    }
                    case 1 -> {
                        entityBar.append("\uE22B");
                    }
                    case 0 -> {
                        entityBar.append("\uE009");
                    }
                }
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
            switch (playerClass){
                case Assassin:{
                    icon.append(ChatColor.of(assassinColor));
                    break;
                }
                case Elementalist:{
                    icon.append(ChatColor.of(elementalistColor));
                    break;
                }
                case Mystic:{
                    icon.append(ChatColor.of(mysticColor));
                    break;
                }
                case Paladin:{
                    icon.append(ChatColor.of(paladinColor));
                    break;
                }
                case Ranger:{
                    icon.append(ChatColor.of(rangerColor));
                    break;
                }
                case Shadow_Knight:{
                    icon.append(ChatColor.of(shadowKnightColor));
                    break;
                }
                case Warrior:{
                    icon.append(ChatColor.of(warriorColor));
                    break;
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

        if(profileManager.getIfEntityIsBoss(entity.getUniqueId())){

            //get whichever background specificlly, later

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
            //default npc
            icon.append(("\uE127"));
            return String.valueOf(icon);
        }

        //default enemy
        icon.append(("\uE128"));

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

        switch (amount){
            case 20:{
                healthBar.append("\uE0B8");
                break;
            }
            case 19:{
                healthBar.append("\uE0B9");
                break;
            }
            case 18:{
                healthBar.append("\uE0BA");
                break;
            }
            case 17:{
                healthBar.append("\uE0BB");
                break;
            }
            case 16:{
                healthBar.append("\uE0BC");
                break;
            }
            case 15:{
                healthBar.append("\uE0BD");
                break;
            }
            case 14:{
                healthBar.append("\uE0BE");
                break;
            }
            case 13:{
                healthBar.append("\uE0BF");
                break;
            }
            case 12:{
                healthBar.append("\uE0C0");
                break;
            }
            case 11:{
                healthBar.append("\uE0C1");
                break;
            }
            case 10:{
                healthBar.append("\uE0C2");
                break;
            }
            case 9:{
                healthBar.append("\uE0C3");
                break;
            }
            case 8:{
                healthBar.append("\uE0C4");
                break;
            }
            case 7:{
                healthBar.append("\uE0C5");
                break;
            }
            case 6:{
                healthBar.append("\uE0C6");
                break;
            }
            case 5:{
                healthBar.append("\uE0C7");
                break;
            }
            case 4:{
                healthBar.append("\uE0C8");
                break;
            }
            case 3:{
                healthBar.append("\uE0C9");
                break;
            }
            case 2:{
                healthBar.append("\uE0CA");
                break;
            }
            case 1:{
                healthBar.append("\uE0CB");
                break;
            }
            case 0:{
                healthBar.append("\uE0CC");
                break;
            }
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

        if(shieldAmount != 0){
            //-128 space
            healthBar.append("\uF80C");

            //-4 space
            healthBar.append("\uF804");
        }

        switch (shieldAmount){
            case 20:{
                healthBar.append("\uE110");
                break;
            }
            case 19:{
                healthBar.append("\uE111");
                break;
            }
            case 18:{
                healthBar.append("\uE112");
                break;
            }
            case 17:{
                healthBar.append("\uE113");
                break;
            }
            case 16:{
                healthBar.append("\uE114");
                break;
            }
            case 15:{
                healthBar.append("\uE115");
                break;
            }
            case 14:{
                healthBar.append("\uE116");
                break;
            }
            case 13:{
                healthBar.append("\uE117");
                break;
            }
            case 12:{
                healthBar.append("\uE118");
                break;
            }
            case 11:{
                healthBar.append("\uE119");
                break;
            }
            case 10:{
                healthBar.append("\uE11A");
                break;
            }
            case 9:{
                healthBar.append("\uE11B");
                break;
            }
            case 8:{
                healthBar.append("\uE11C");
                break;
            }
            case 7:{
                healthBar.append("\uE11D");
                break;
            }
            case 6:{
                healthBar.append("\uE11E");
                break;
            }
            case 5:{
                healthBar.append("\uE11F");
                break;
            }
            case 4:{
                healthBar.append("\uE120");
                break;
            }
            case 3:{
                healthBar.append("\uE121");
                break;
            }
            case 2:{
                healthBar.append("\uE122");
                break;
            }
            case 1:{
                healthBar.append("\uE123");
                break;
            }
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

        switch (slot){
            case 0:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE14F");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE150");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE151");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE152");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE153");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE154");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE155");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE156");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE157");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE158");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE159");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE15A");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE15B");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE15C");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE15D");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE15E");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE15F");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE160");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE161");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE162");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE163");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE164");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE165");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE166");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE167");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE168");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE169");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE16A");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE16B");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE16C");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE16D");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE16E");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE16F");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE170");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE171");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE172");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE173");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE174");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE175");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE176");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE177");
                        break;
                    }
                }
                break;
            }
            case 1:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE17A");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE17B");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE17C");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE17D");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE17E");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE17F");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE180");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE181");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE182");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE183");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE184");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE185");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE186");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE187");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE188");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE189");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE18A");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE18B");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE18C");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE18D");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE18E");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE18F");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE190");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE191");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE192");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE193");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE194");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE195");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE196");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE197");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE198");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE199");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE19A");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE19B");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE19C");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE19D");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE19E");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE19F");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1A0");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1A1");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1A2");
                        break;
                    }
                }
                break;
            }
            case 3:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE1D0");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1D1");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1D2");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1D3");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1D4");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1D5");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1D6");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1D7");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1D8");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1D9");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1DA");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1DB");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1DC");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1DD");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1DE");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1DF");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1E0");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1E1");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1E2");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1E3");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE1E4");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE1E5");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1E6");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1E7");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1E8");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1E9");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1EA");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1EB");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1EC");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1ED");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1EF");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1F0");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1F1");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1F2");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1F3");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1F4");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1F5");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1F6");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1F7");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1F8");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1F9");
                        break;
                    }
                }
                break;
            }
            case 2:{
                switch (amount){
                    case 20:{
                        healthBar.append("\uE1A5");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1A6");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1A7");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1A8");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1A9");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1AA");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1AB");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1AC");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1AD");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1AE");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1AF");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1B0");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1B1");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1B2");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1B3");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1B4");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1B5");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1B6");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1B7");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1B8");
                        break;
                    }
                    case 0:{
                        healthBar.append("\uE1B9");
                        break;
                    }
                }

                if(shieldAmount != 0){
                    //-67 space
                    healthBar.append("\uF80B\uF803");
                }

                switch (shieldAmount){
                    case 20:{
                        healthBar.append("\uE1BA");
                        break;
                    }
                    case 19:{
                        healthBar.append("\uE1BB");
                        break;
                    }
                    case 18:{
                        healthBar.append("\uE1BC");
                        break;
                    }
                    case 17:{
                        healthBar.append("\uE1BD");
                        break;
                    }
                    case 16:{
                        healthBar.append("\uE1BE");
                        break;
                    }
                    case 15:{
                        healthBar.append("\uE1BF");
                        break;
                    }
                    case 14:{
                        healthBar.append("\uE1C0");
                        break;
                    }
                    case 13:{
                        healthBar.append("\uE1C1");
                        break;
                    }
                    case 12:{
                        healthBar.append("\uE1C2");
                        break;
                    }
                    case 11:{
                        healthBar.append("\uE1C3");
                        break;
                    }
                    case 10:{
                        healthBar.append("\uE1C4");
                        break;
                    }
                    case 9:{
                        healthBar.append("\uE1C5");
                        break;
                    }
                    case 8:{
                        healthBar.append("\uE1C6");
                        break;
                    }
                    case 7:{
                        healthBar.append("\uE1C7");
                        break;
                    }
                    case 6:{
                        healthBar.append("\uE1C8");
                        break;
                    }
                    case 5:{
                        healthBar.append("\uE1C9");
                        break;
                    }
                    case 4:{
                        healthBar.append("\uE1CA");
                        break;
                    }
                    case 3:{
                        healthBar.append("\uE1CB");
                        break;
                    }
                    case 2:{
                        healthBar.append("\uE1CC");
                        break;
                    }
                    case 1:{
                        healthBar.append("\uE1CD");
                        break;
                    }
                }
                break;
            }
        }




        return String.valueOf(healthBar);
    }

    private String resourceBar(LivingEntity entity){

        StringBuilder resourceBar = new StringBuilder();



        if(entity instanceof Player || profileManager.getAnyProfile(entity).fakePlayer()){
            Profile playerProfile = profileManager.getAnyProfile(entity);
            PlayerClass playerClass = playerProfile.getPlayerClass();
            switch (playerClass){
                case Mystic:{

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxMana = 500;
                    double currentMana = abilityManager.getMysticAbilities().getMana().getCurrentMana(entity);

                    double ratio = currentMana / maxMana;

                    int amount = (int) Math.ceil(ratio * 20);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentMana <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 20:{
                            resourceBar.append("\uE0CD");
                            break;
                        }
                        case 19:{
                            resourceBar.append("\uE0CE");
                            break;
                        }
                        case 18:{
                            resourceBar.append("\uE0CF");
                            break;
                        }
                        case 17:{
                            resourceBar.append("\uE0D0");
                            break;
                        }
                        case 16:{
                            resourceBar.append("\uE0D1");
                            break;
                        }
                        case 15:{
                            resourceBar.append("\uE0D2");
                            break;
                        }
                        case 14:{
                            resourceBar.append("\uE0D3");
                            break;
                        }
                        case 13:{
                            resourceBar.append("\uE0D4");
                            break;
                        }
                        case 12:{
                            resourceBar.append("\uE0D5");
                            break;
                        }
                        case 11:{
                            resourceBar.append("\uE0D6");
                            break;
                        }
                        case 10:{
                            resourceBar.append("\uE0D7");
                            break;
                        }
                        case 9:{
                            resourceBar.append("\uE0D8");
                            break;
                        }
                        case 8:{
                            resourceBar.append("\uE0D9");
                            break;
                        }
                        case 7:{
                            resourceBar.append("\uE0DA");
                            break;
                        }
                        case 6:{
                            resourceBar.append("\uE0DB");
                            break;
                        }
                        case 5:{
                            resourceBar.append("\uE0DC");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE0DD");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE0DE");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE0DF");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE0E0");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case Warrior:{
                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxRage = 500;
                    double currentRage = abilityManager.getWarriorAbilities().getRage().getCurrentRage(entity);

                    double ratio = currentRage / maxRage;

                    int amount = (int) Math.ceil(ratio * 20);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentRage <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 20:{
                            resourceBar.append("\uE0E2");
                            break;
                        }
                        case 19:{
                            resourceBar.append("\uE0E3");
                            break;
                        }
                        case 18:{
                            resourceBar.append("\uE0E4");
                            break;
                        }
                        case 17:{
                            resourceBar.append("\uE0E5");
                            break;
                        }
                        case 16:{
                            resourceBar.append("\uE0E6");
                            break;
                        }
                        case 15:{
                            resourceBar.append("\uE0E7");
                            break;
                        }
                        case 14:{
                            resourceBar.append("\uE0E8");
                            break;
                        }
                        case 13:{
                            resourceBar.append("\uE0E9");
                            break;
                        }
                        case 12:{
                            resourceBar.append("\uE0EA");
                            break;
                        }
                        case 11:{
                            resourceBar.append("\uE0EB");
                            break;
                        }
                        case 10:{
                            resourceBar.append("\uE0EC");
                            break;
                        }
                        case 9:{
                            resourceBar.append("\uE0ED");
                            break;
                        }
                        case 8:{
                            resourceBar.append("\uE0EE");
                            break;
                        }
                        case 7:{
                            resourceBar.append("\uE0EF");
                            break;
                        }
                        case 6:{
                            resourceBar.append("\uE0F0");
                            break;
                        }
                        case 5:{
                            resourceBar.append("\uE0F1");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE0F2");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE0F3");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE0F4");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE0F5");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case Shadow_Knight:{
                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxEnergy = 100;
                    double currentEnergy = abilityManager.getShadowKnightAbilities().getEnergy().getCurrentEnergy(entity);

                    double ratio = currentEnergy / maxEnergy;

                    int amount = (int) Math.ceil(ratio * 10);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentEnergy <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 10:{
                            resourceBar.append("\uE0F6");
                            break;
                        }
                        case 9:{
                            resourceBar.append("\uE0F7");
                            break;
                        }
                        case 8:{
                            resourceBar.append("\uE0F8");
                            break;
                        }
                        case 7:{
                            resourceBar.append("\uE0F9");
                            break;
                        }
                        case 6:{
                            resourceBar.append("\uE0FA");
                            break;
                        }
                        case 5:{
                            resourceBar.append("\uE0FB");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE0FC");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE0FD");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE0FE");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE0FF");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case Ranger:{

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double maxFocus = 10;
                    double currentFocus = abilityManager.getRangerAbilities().getFocus().getFocus(entity);

                    double ratio = currentFocus / maxFocus;

                    int amount = (int) Math.ceil(ratio * 3);

                    if(amount < 0){
                        amount = 0;
                    }

                    if(currentFocus <= 0){
                        amount = 0;
                    }

                    switch (amount){
                        case 3:{
                            resourceBar.append("\uE100");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE101");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE102");
                            break;
                        }
                        case 0:{
                            resourceBar.append("\uE0E1");
                            break;
                        }
                    }

                    break;
                }
                case Assassin:{

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    int combo = abilityManager.getAssassinAbilities().getCombo().getComboPoints(entity);

                    if(profileManager.getAnyProfile(entity).getPlayerSubclass().equals(SubClass.Duelist)){



                        switch (combo){
                            case 0:{
                                resourceBar.append("\uE108");
                                break;
                            }
                            case 1:{
                                resourceBar.append("\uE109");
                                break;
                            }
                            case 2:{
                                resourceBar.append("\uE10A");
                                break;
                            }
                            case 3:{
                                resourceBar.append("\uE10B");
                                break;
                            }
                            case 4:{
                                resourceBar.append("\uE10C");
                                break;
                            }
                            case 5:{
                                resourceBar.append("\uE008");
                                break;
                            }
                        }

                        break;
                    }

                    switch (combo){
                        case 0:{
                            resourceBar.append("\uE103");
                            break;
                        }
                        case 1:{
                            resourceBar.append("\uE104");
                            break;
                        }
                        case 2:{
                            resourceBar.append("\uE105");
                            break;
                        }
                        case 3:{
                            resourceBar.append("\uE106");
                            break;
                        }
                        case 4:{
                            resourceBar.append("\uE107");
                            break;
                        }
                    }

                    break;
                }
                case Elementalist:{

                    //-128 space
                    resourceBar.append("\uF80C");

                    //-4 space
                    resourceBar.append("\uF804");

                    double currentHeat = abilityManager.getElementalistAbilities().getHeat().getHeat(entity);

                    if(currentHeat < 50){
                        resourceBar.append("\uE10D");
                        return String.valueOf(resourceBar);
                    }

                    if(currentHeat < 90){
                        resourceBar.append("\uE10E");
                        return String.valueOf(resourceBar);
                    }


                    resourceBar.append("\uE10F");


                    break;
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


        switch (amount){
            case 20:{
                resourceBar.append("\uE0E2");
                break;
            }
            case 19:{
                resourceBar.append("\uE0E3");
                break;
            }
            case 18:{
                resourceBar.append("\uE0E4");
                break;
            }
            case 17:{
                resourceBar.append("\uE0E5");
                break;
            }
            case 16:{
                resourceBar.append("\uE0E6");
                break;
            }
            case 15:{
                resourceBar.append("\uE0E7");
                break;
            }
            case 14:{
                resourceBar.append("\uE0E8");
                break;
            }
            case 13:{
                resourceBar.append("\uE0E9");
                break;
            }
            case 12:{
                resourceBar.append("\uE0EA");
                break;
            }
            case 11:{
                resourceBar.append("\uE0EB");
                break;
            }
            case 10:{
                resourceBar.append("\uE0EC");
                break;
            }
            case 9:{
                resourceBar.append("\uE0ED");
                break;
            }
            case 8:{
                resourceBar.append("\uE0EE");
                break;
            }
            case 7:{
                resourceBar.append("\uE0EF");
                break;
            }
            case 6:{
                resourceBar.append("\uE0F0");
                break;
            }
            case 5:{
                resourceBar.append("\uE0F1");
                break;
            }
            case 4:{
                resourceBar.append("\uE0F2");
                break;
            }
            case 3:{
                resourceBar.append("\uE0F3");
                break;
            }
            case 2:{
                resourceBar.append("\uE0F4");
                break;
            }
            case 1:{
                resourceBar.append("\uE0F5");
                break;
            }
            case 0:{
                resourceBar.append("\uE0E1");
                break;
            }
        }


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

        switch (abilityName.toLowerCase()){

            case "conjuring force":{
                unicode.append("\uE043");
                break;
            }
            case "fiery wing":{
                unicode.append("\uE044");
                break;
            }
            case "wild roar":{
                unicode.append("\uE045");
                break;
            }
            case "star volley":{
                unicode.append("\uE040");
                break;
            }
            case "annihilation":{

                if(abilityManager.getShadowKnightAbilities().getAnnihilation().returnWhichItem(player) == 0){
                    unicode.append("\uE12E");
                }
                else{
                    unicode.append("\uE06D");
                }


                break;
            }
            case "blood shield":{

                if(abilityManager.getShadowKnightAbilities().getBloodShield().returnWhichItem(player) == 0){
                    unicode.append("\uE042");
                }
                else{
                    unicode.append("\uE06E");
                }

                break;
            }
            case "arcane missiles":{
                unicode.append("\uE046");
                break;
            }
            case "enlightenment":{

                if(abilityManager.getMysticAbilities().getEnlightenment().returnWhichItem(player) == 0){
                    unicode.append("\uE047");
                }
                else{
                    unicode.append("\uE06C");
                }

                break;
            }
            case "duelist's frenzy":{


                if(abilityManager.getAssassinAbilities().getDuelistsFrenzy().returnWhichItem(player)==0){
                    unicode.append("\uE049");
                }
                else{
                    unicode.append("\uE04A");
                }


                break;
            }
            case "wicked concoction":{
                unicode.append("\uE04B");
                break;
            }
            case "gladiator heart":{
                unicode.append("\uE058");
                break;
            }
            case "death gaze":{
                unicode.append("\uE059");
                break;
            }
            case "well of light":{
                unicode.append("\uE067");
                break;
            }
            case "shield of sanctity":{
                unicode.append("\uE068");
                break;
            }
            case "representative":{
                unicode.append("\uE069");
                break;
            }
        }

        return String.valueOf(unicode);
    }

    private String ultimateCooldown(Player player){

        StringBuilder unicode = new StringBuilder();


        int percent = iconCalculator.calculate(abilityManager.getPlayerUltimateCooldown(player), abilityManager.getUltimateCooldown(player));

        //Bukkit.getLogger().info("percent " + percent);


        switch (percent){
            case 8:{
                return "\uE12F";
            }
            case 7:{
                return "\uE130";
            }
            case 6:{
                return "\uE131";
            }
            case 5:{
                return "\uE132";
            }
            case 4:{
                return "\uE133";
            }
            case 3:{
                return "\uE134";
            }
            case 2:{
                return "\uE135";
            }
            case 1:{
                return "\uE136";
            }
            case 0:{
                return "\uE137";
            }
        }


        return String.valueOf(unicode);
    }

}
