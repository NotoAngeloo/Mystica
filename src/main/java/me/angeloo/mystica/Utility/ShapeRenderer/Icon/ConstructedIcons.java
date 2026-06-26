package me.angeloo.mystica.Utility.ShapeRenderer.Icon;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand.ConstructedIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConstructedIcons {

    private static final Map<String, ConstructedIcon> CACHE = new HashMap<>();

    public static ConstructedIcon DUELIST_ICON = load("MysticaPack/assets/minecraft/textures/icons/assassin/duelist_frenzy.png");
    public static ConstructedIcon ALCHEMIST_ICON = load("MysticaPack/assets/minecraft/textures/icons/assassin/wicked_concoction.png");

    public static ConstructedIcon PYROMANCER_ICON = load("MysticaPack/assets/minecraft/textures/icons/elementalist/fiery_wing.png");
    public static ConstructedIcon CONJURER_ICON = load("MysticaPack/assets/minecraft/textures/icons/elementalist/conjuring_force.png");

    public static ConstructedIcon SHEPARD_ICON = load("MysticaPack/assets/minecraft/textures/icons/mystic/enlightenment.png");
    public static ConstructedIcon ARCANE_ICON = load("MysticaPack/assets/minecraft/textures/icons/mystic/arcane_missiles.png");
    public static ConstructedIcon CHAOS_ICON = load("MysticaPack/assets/minecraft/textures/icons/mystic/evil_spirit.png");

    public static ConstructedIcon TEMPLAR_ICON = load("MysticaPack/assets/minecraft/textures/icons/paladin/sanctity_shield.png");
    public static ConstructedIcon DAWN_ICON = load("MysticaPack/assets/minecraft/textures/icons/paladin/light_well.png");
    public static ConstructedIcon DIVINE_ICON = load("MysticaPack/assets/minecraft/textures/icons/paladin/representative.png");

    public static ConstructedIcon SCOUT_ICON = load("MysticaPack/assets/minecraft/textures/icons/ranger/star_volley.png");
    public static ConstructedIcon TAMER_ICON = load("MysticaPack/assets/minecraft/textures/icons/ranger/wild_roar.png");

    public static ConstructedIcon DOOM_ICON = load("MysticaPack/assets/minecraft/textures/icons/shadow_knight/annihilation.png");
    public static ConstructedIcon BLOOD_ICON = load("MysticaPack/assets/minecraft/textures/icons/shadow_knight/blood_shield.png");

    public static ConstructedIcon GLADIATOR_ICON = load("MysticaPack/assets/minecraft/textures/icons/warrior/gladiator_heart.png");
    public static ConstructedIcon EXECUTIONER_ICON = load("MysticaPack/assets/minecraft/textures/icons/warrior/death_gaze.png");

    public static ConstructedIcon load(String path){

        return CACHE.computeIfAbsent(path, p->{


            try(InputStream in = ConstructedIcons.class.getClassLoader().getResourceAsStream(path)){

                if(in == null){
                    throw new IllegalArgumentException("Missing Icon: " + path);
                }

                BufferedImage image = ImageIO.read(in);

                return new ConstructedIcon(
                        image.getWidth(),
                        image.getHeight(),
                        image
                );

            }
            catch (IOException e){
                throw new RuntimeException("Failed to load icon: " + path, e);
            }
        });



    }
}
