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
