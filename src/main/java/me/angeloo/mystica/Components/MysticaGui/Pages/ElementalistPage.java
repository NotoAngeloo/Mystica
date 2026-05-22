package me.angeloo.mystica.Components.MysticaGui.Pages;

import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.GuiManager;
import me.angeloo.mystica.Components.MysticaGui.GuiSession;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public class ElementalistPage extends GuiPage{

    private final ClassSelect gui;

    private final GuiManager guiManager;

    public ElementalistPage(
            ClassSelect gui,
            GuiManager guiManager
    ) {

        this.gui = gui;
        this.guiManager = guiManager;
    }

    @Override
    protected void build(
            Player player,
            GuiRenderContext context
    ) {

        /*
         * Background
         */

        context.drawBackground(
                -305,
                UiGlyphs.DEFAULT_BACKGROUND
        );

        /*
         * Decorative buttons
         */

        context.drawIcon(
                6,
                -108,
                UiGlyphs.BUTTON_2x2
        );

        context.drawIcon(
                6,
                -54,
                UiGlyphs.BUTTON_2x2
        );

        context.drawIcon(
                6,
                180,
                UiGlyphs.BUTTON_2x2
        );

        context.drawIcon(
                6,
                234,
                UiGlyphs.BUTTON_2x2
        );

        /*
         * Character art
         */

        context.drawIcon(
                0,
                54,
                UiGlyphs.ELEMENTALIST_CHARACTER
        );

        /*
         * Pyromancer
         */

        button(
                context,
                new GuiButton() {

                    @Override
                    public int slot() {
                        return 7;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {

                        return Set.of(
                                7, 8,
                                16, 17
                        );
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.PYROMANCER_ICON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                        //player.sendMessage("pyromancer");

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

                        return Set.of(
                                25, 26,
                                34, 35
                        );
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.CONJURER_ICON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {


                    }
                }
        );

        /*
         * Bottom Left Button
         */

        button(
                context,
                new GuiButton() {

                    @Override
                    public int slot() {
                        return 54;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {

                        return Set.of(
                                54, 55,
                                45, 46
                        );
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.BUTTON_2x2;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                    }
                }
        );

        /*
         * Bottom Right Button
         */

        button(
                context,
                new GuiButton() {

                    @Override
                    public int slot() {
                        return 61;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {

                        return Set.of(
                                61, 62,
                                70, 71
                        );
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.BUTTON_2x2;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                    }
                }
        );

        /*
         * Center Button
         */

        button(
                context,
                new GuiButton() {

                    @Override
                    public int slot() {
                        return 57;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {

                        return Set.of(
                                57, 58, 59,
                                66, 67, 68,
                                75, 76, 77
                        );
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.BUTTON_3x3;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                    }
                }
        );

        /*
         * Left Arrow
         */

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
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                        GuiSession session =
                                guiManager.getSession(p);

                        session.setCurrentPage(gui.getAssassinPage());
                        guiManager.refresh(p);
                    }
                }
        );

        /*
         * Right Arrow
         */

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
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                        GuiSession session =
                                guiManager.getSession(p);

                        session.setCurrentPage(gui.getAssassinPage());
                        guiManager.refresh(p);
                    }
                }
        );
    }

}
