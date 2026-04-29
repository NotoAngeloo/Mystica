package me.angeloo.mystica.Components.Hud.Abilties;

import me.angeloo.mystica.Components.CombatSystem.Abilities.Ability;
import me.angeloo.mystica.Components.CombatSystem.Abilities.AbilityManager;
import me.angeloo.mystica.Components.CombatSystem.Abilities.BasicAttacks.BasicAttackDefinition;
import me.angeloo.mystica.Components.CombatSystem.Abilities.Cooldowns.CooldownManager;
import me.angeloo.mystica.Components.ProfileComponents.EquipSkills;
import me.angeloo.mystica.Components.ProfileComponents.ProfileManager;
import me.angeloo.mystica.Mystica;
import me.angeloo.mystica.Utility.Enums.PlayerClass;
import me.angeloo.mystica.Utility.Enums.SubClass;
import org.bukkit.entity.Player;

public class AbilityBarRenderer {

    private final ProfileManager profileManager;
    private final AbilityManager abilityManager;
    private final CooldownManager cooldownManager;

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

    //0-16
    private final String[] RADIAL = new String[] {"\ue1d4","\ue1d5","\ue1d6","\ue1d7","\ue1d8","\ue1d9","\ue1da","\ue1db","\ue1dc","\ue1dd","\ue1de","\ue1df","\ue1e0","\ue1e1","\ue1e2","\ue1e3","\ue1e4"};

    private final String[] KEYBIND1_8 = new String[] {"\ue1e5","\ue1e6","\ue1e7","\ue1e8","\ue1e9","\ue1ea","\ue1eb","\ue1ec"};

    private final String KEYBIND_F = "\ue1ed";

    private final String KEYBIND_LMB = "\ue1ee";

    public AbilityBarRenderer(Mystica main, AbilityManager manager){
        profileManager = main.getProfileManager();
        this.abilityManager = manager;
        this.cooldownManager = main.getCooldownManager();
    }

    public String render(Player player, EquipSkills equipSkills, double haste, long now){

        StringBuilder offset = new StringBuilder();
        StringBuilder bar = new StringBuilder();

        PlayerClass playerClass = profileManager.getAnyProfile(player).getPlayerClass();
        SubClass subClass = profileManager.getAnyProfile(player).getPlayerSubclass();

        long gcdRemaining = cooldownManager.getGlobalRemaining(player.getUniqueId(), now);

        int count = 0;

        //place the ultimate ability first
        Ability ultimate = abilityManager.getAbilityResolver().get(playerClass, subClass, -1);

        if(ultimate != null){
            String icon = ultimate.skillBarIcon(player);
            bar.append(icon);

            long abilityRemaining = cooldownManager.getRemaining(
                    player.getUniqueId(),
                    -1,
                    haste,
                    now
            );

            long abilityBase = ultimate.cooldown() * 1000L;
            long gcdBase = (long) (ultimate.getGlobalCooldownMillis() / (1.0 + haste));

            double abilityPct = abilityRemaining / (double) abilityBase;
            double gcdPct = gcdBase > 0 ? gcdRemaining / (double) gcdBase : 0;

            boolean useGcd = gcdPct > abilityPct;

            long finalRemaining = useGcd ? gcdRemaining : abilityRemaining;
            long finalBase = useGcd ? gcdBase : abilityBase;

            AbilityRenderState state = new AbilityRenderState(finalRemaining, finalBase);

            //-17
            bar.append("\uF809\uF801");

            String glyph = getRadialGlyph(state);
            bar.append(glyph);

            //-17
            bar.append("\uF809\uF801");

            bar.append(KEYBIND_F);

            count++;
        }

        for(int slot = 0;slot<equipSkills.size();slot++){

            int abilityNumber = equipSkills.getSkill(slot);

            if(abilityNumber == EquipSkills.EMPTY){
                continue;
            }

            Ability ability = abilityManager.getAbilityResolver().resolve(playerClass, subClass, abilityNumber);

            if(ability == null){
                continue;
            }

            String icon = ability.skillBarIcon(player);

            bar.append(icon);

            long abilityRemaining = cooldownManager.getRemaining(
                    player.getUniqueId(),
                    abilityNumber,
                    haste,
                    now
            );


            long abilityBase = ability.cooldown() * 1000L;
            long gcdBase = (long) (ability.getGlobalCooldownMillis() / (1.0 + haste));

            double abilityPct = abilityRemaining / (double) abilityBase;
            double gcdPct = gcdBase > 0 ? gcdRemaining / (double) gcdBase : 0;

            boolean useGcd = gcdPct > abilityPct;

            long finalRemaining = useGcd ? gcdRemaining : abilityRemaining;
            long finalBase = useGcd ? gcdBase : abilityBase;

            AbilityRenderState state = new AbilityRenderState(finalRemaining, finalBase);

            //-17
            bar.append("\uF809\uF801");

            String glyph = getRadialGlyph(state);
            bar.append(glyph);

            //-17
            bar.append("\uF809\uF801");

            bar.append(KEYBIND1_8[slot]);

            count++;
        }

        //place basic last

        BasicAttackDefinition basic = abilityManager.getAbilityResolver().resolveBasic(playerClass, subClass);

        if(basic != null){
            String icon = basic.skillBarIcon(player);
            bar.append(icon);
            //basic doesn't have gcd, so im making it 1 sec here to fit in my renderstate
            long gcdBase = (long) (1000 / (1.0 + haste));

            AbilityRenderState state = new AbilityRenderState(gcdRemaining, gcdBase);

            //-17
            bar.append("\uF809\uF801");

            String glyph = getRadialGlyph(state);
            bar.append(glyph);

            //-17
            bar.append("\uF809\uF801");

            bar.append(KEYBIND_LMB);
            count++;
        }

        ////////////////////////////

        //temp for testing
        //when have new targeter, change the logic
        bar.append("\ue1d3");
        //-17
        bar.append("\uF809\uF801");
        bar.append(KEYBIND_LMB);
        count++;

        //////////////////////////
        //anchor -207
        //-256
        offset.append("\uF80D");
        //+49
        offset.append("\uF82A\uF829\uF821");


        int totalWidth = 207;
        int contentWidth = count * 17;
        int remaining = totalWidth - contentWidth;

        int leftPad = remaining / 2;
        int rightPad = remaining - leftPad;

        //try to have equal offset and padding no matter how many "count"

        for(int i = 0; i< PIXELS.length;i++){
            while (leftPad>=PIXELS[i]){
                offset.append(GLYPHS[i]);
                leftPad -= PIXELS[i];
            }
        }

        for(int i = 0; i< PIXELS.length;i++){
            while (rightPad>=PIXELS[i]){
                bar.append(GLYPHS[i]);
                rightPad -= PIXELS[i];
            }
        }

        offset.append(bar);

        return String.valueOf(offset);

        /*//-190, total offset, same as max length
        //-256
        offset.append("\uF80D");
        //+66
        offset.append("\uF82B\uF822");


        /////////////////////////////

        //resources 207 pixel wide. 17x12 = 204, 3 pixel off
        //need to offset -17 per abilityCount

        //max length is 207 - 17 (ability width)
        int maxLength = 190;
        int padding = maxLength - (count * 17);

        for (int i = 0; i < PIXELS.length; i++) {

            while (padding >= PIXELS[i]) {
                bar.append(GLYPHS[i]);
                padding -= PIXELS[i];
            }

        }

        offset.append(bar);

        return String.valueOf(offset);*/
    }

    private String getRadialGlyph(AbilityRenderState state) {

        if (!state.isOnCooldown()) {
            return RADIAL[0];
        }

        double pct = state.getCooldownPercent(); // 0 → 1

        int maxIndex = RADIAL.length - 1;

        int index = (int) Math.round(pct * maxIndex);
        index = Math.max(0, Math.min(maxIndex, index));

        return RADIAL[index];
    }




}
