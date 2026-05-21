package me.angeloo.mystica.Components.MysticaGui.Guis;

import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.Gui;
import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Utility.TextRenderer.LineData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Set;

public class TestGui extends Gui {

    @Override
    public void build(
            Player player,
            GuiRenderContext context
    ) {

        //-305 is what shift is needed to center a 768 pixel wide glyph
        //context.drawBackground(-305, UiGlyphs.DEFAULT_BACKGROUND);


        context.drawIcon(6, -108, UiGlyphs.BUTTON_2);
        context.drawIcon(6, -54, UiGlyphs.BUTTON_2);
        context.drawIcon(6, 180, UiGlyphs.BUTTON_2);
        context.drawIcon(6, 234, UiGlyphs.BUTTON_2);

        context.drawIcon(0, 54, UiGlyphs.CLASS_ART);

        List<LineData> data = List.of(
                new LineData("testing text", 16),
                new LineData("with multiple lines", 0)

        );

        context.drawText(0, 0, data);

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

                        p.sendMessage(
                                "left"
                        );
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
                        /*p.sendMessage(
                                "right"
                        );*/
                    }
                }
        );


    }

}
