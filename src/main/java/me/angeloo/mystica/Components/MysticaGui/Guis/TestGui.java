package me.angeloo.mystica.Components.MysticaGui.Guis;

import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.Gui;
import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Utility.TextRenderer.LineData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;
import java.util.List;
import java.util.Set;

import static me.angeloo.mystica.Mystica.*;

public class TestGui extends Gui {

    @Override
    public void build(
            Player player,
            GuiRenderContext context
    ) {

        context.drawIcon(0, -96, UiGlyphs.ITEM_TOOLTIP_BACKGROUND);

        context.drawIcon(0, -96, UiGlyphs.ITEM_BANNER_RARE);

        //temp art, should be equip icon
        context.drawIcon(0, -92, UiGlyphs.BUTTON_2);

        //scroll up/down item text
        context.drawIcon(7, 36, UiGlyphs.UP_ARROW);
        context.drawIcon(8, 36, UiGlyphs.DOWN_ARROW);

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 81;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(81, 82, 83);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.EQUIP_BUTTON;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        List<LineData> nameData = List.of(
                new LineData(ChatColor.of(rareColor) + "Item Name", 12),
                new LineData(ChatColor.of(new Color(176, 159, 109)) + "Class: " + "Shadow Knight", 12),
                new LineData(ChatColor.of(new Color(176, 159, 109)) + "Level: " + "1", 16)
        );

        context.drawText(-54, -5, nameData);

        List<LineData> metaData = List.of(
                new LineData(ChatColor.WHITE + "Attack +3", 12),
                new LineData(ChatColor.WHITE + "Health +18", 12),
                new LineData(ChatColor.WHITE + "Defense +4", 12),
                new LineData(ChatColor.WHITE + "Magic Defense +4", 16),
                new LineData(ChatColor.of(uncommonColor) + "Attack +20", 12),
                new LineData(ChatColor.of(uncommonColor) + "Crit +10", 16),
                new LineData(ChatColor.of(rareColor) + "Skill 1 +5", 12),
                new LineData(ChatColor.of(rareColor) + "Skill 1 +5", 12)
        );

        context.drawText(-86, -45, metaData);

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 3;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(3, 4, 12, 13);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.BUTTON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 5;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(5, 6, 14, 15);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.BUTTON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 7;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(7, 8, 16, 17);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.BUTTON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );





        //-305 is what shift is needed to center a 768 pixel wide glyph
        //context.drawBackground(-305, UiGlyphs.DEFAULT_BACKGROUND);


        /*context.drawIcon(6, -108, UiGlyphs.BUTTON_2);
        context.drawIcon(6, -54, UiGlyphs.BUTTON_2);
        context.drawIcon(6, 180, UiGlyphs.BUTTON_2);
        context.drawIcon(6, 234, UiGlyphs.BUTTON_2);

        context.drawIcon(0, 54, UiGlyphs.CLASS_ART);

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 7;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(7,8,16,17);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.PYROMANCER_ICON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 25;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(25,26,34,35);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.CONJURER_ICON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );


        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 54;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(54,55,45,46);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.BUTTON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 61;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(61,62,70,71);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.BUTTON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 57;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(57,58,59,66,67,68,75,76,77);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.BUTTON_3;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        button(
                context,
                new GuiButton() {

                    @Override
                    public int slot() {
                        return 81;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(81);
                    }

                    @Override
                    public Glyph glyph() {
                        return UiGlyphs.LEFT_ARROW;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );


        button(
                context,
                new GuiButton() {

                    @Override
                    public int slot() {
                        return 89;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(89);
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.RIGHT_ARROW;
                    }

                    @Override
                    public void click(
                            Player p,
                            Gui gui,
                            InventoryClickEvent event
                    ) {

                        p.closeInventory();

                    }
                }
        );*/


    }

}
