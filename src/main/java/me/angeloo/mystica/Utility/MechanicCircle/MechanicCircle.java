package me.angeloo.mystica.Utility.MechanicCircle;

import me.angeloo.mystica.Mystica;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;


public class MechanicCircle {

    private final Mystica main;

    private final Location center;
    private final float maxRadiusBlocks;
    private final int durationTicks;
    private final int lingerTicks;

    private final TextDisplay fillDisplay;
    private final TextDisplay outlineDisplay;

    private int tick = 0;
    private BukkitRunnable task;

    private static final float GLYPH_TO_BLOCK_MULTIPLIER = 5.3f;

    public MechanicCircle(Mystica main,
                          Location center,
                          float maxRadiusBlocks,
                          int durationTicks,
                          int lingerTicks,
                          TextDisplay fillDisplay,
                          TextDisplay outlineDisplay){

        this.main = main;
        this.center = center;
        this.maxRadiusBlocks = maxRadiusBlocks;
        this.durationTicks = durationTicks;
        this.lingerTicks = lingerTicks;
        this.fillDisplay = fillDisplay;
        this.outlineDisplay = outlineDisplay;
    }



    public static MechanicCircle spawn(Mystica main,
                                       Location center,
                                       float maxScale,
                                       int durationTicks,
                                       int lingerTicks,
                                       ChatColor color){

        World world = center.getWorld();
        assert world != null;

        TextDisplay outline = (TextDisplay) world.spawnEntity(center, EntityType.TEXT_DISPLAY);
        TextDisplay fill = (TextDisplay) world.spawnEntity(center, EntityType.TEXT_DISPLAY);

        setUpDisplay(outline, "\ue241", color);
        setUpDisplay(fill, "\ue240", color);

        // fill starts at 0
        setScale(fill, 0.01f);
        setScale(outline, maxScale);

        return new MechanicCircle(main, center, maxScale, durationTicks, lingerTicks, fill, outline);
    }

    private static void setUpDisplay(TextDisplay display, String text, ChatColor color){

        display.setText(color + text);

        display.setBillboard(Display.Billboard.FIXED);
        display.setSeeThrough(true);
        display.setShadowed(false);
        display.setDefaultBackground(false);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

        Transformation t = display.getTransformation();
        t.getLeftRotation().set(new Quaternionf().rotateX((float) Math.toRadians(-90)));
        display.setTransformation(t);
    }


    private static void setScale(TextDisplay display, float scale) {
        Transformation t = display.getTransformation();
        scale *= GLYPH_TO_BLOCK_MULTIPLIER;
        t.getScale().set(scale, scale, scale);
        display.setTransformation(t);
    }


    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;

                // Phase 1: Growing
                if (tick <= durationTicks) {

                    float progress = tick / (float) durationTicks;
                    float eased = easeOutCubic(progress);
                    float scale = maxRadiusBlocks * eased;

                    setScale(fillDisplay, scale);
                    return;
                }

                // Phase 2: Linger (do nothing, just exist)
                if (tick <= durationTicks + lingerTicks) {
                    return;
                }

                // Phase 3: Cleanup
                destroy();
                cancel();
            }
        };

        task.runTaskTimer(main, 0, 1);
    }

    private float easeOutCubic(float x) {
        return (float) (1 - Math.pow(1 - x, 3));
    }

    public void destroy() {
        if (task != null) task.cancel();

        fillDisplay.remove();
        outlineDisplay.remove();

        //Bukkit.getLogger().info("clean up circle");
    }

}
