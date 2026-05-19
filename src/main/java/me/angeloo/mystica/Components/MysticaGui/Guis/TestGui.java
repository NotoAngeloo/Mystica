package me.angeloo.mystica.Components.MysticaGui.Guis;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.DrawIconCommand;

import me.angeloo.mystica.Components.MysticaGui.DrawCommand.SlotDrawCommand.DrawSlotIconCommand;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.Gui;
import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Components.MysticaGui.Render.RenderLayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public class TestGui extends Gui {

    @Override
    public void build(
            Player player,
            GuiRenderContext context
    ) {

        context.drawBackground(-256, UiGlyphs.DEFAULT_BACKGROUND);

        /*button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 0;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(0,1,9,10);
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

                        p.sendMessage(
                                "test"
                        );
                    }
                }
        );*/

        /*button(
                context,
                new GuiButton() {
                    @Override
                    public int slot() {
                        return 0;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {
                        return Set.of(0,1,2,9,10,11,18,19,20);
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

                        p.sendMessage(
                                "test"
                        );
                    }
                }
        );*/

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

                        p.sendMessage(
                                "right"
                        );
                    }
                }
        );


    }

}
