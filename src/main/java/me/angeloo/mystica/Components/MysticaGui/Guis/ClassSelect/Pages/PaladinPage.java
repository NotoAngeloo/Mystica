package me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Pages;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.GuiManager;
import me.angeloo.mystica.Components.MysticaGui.GuiSession;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.ClassSelect;
import me.angeloo.mystica.Components.MysticaGui.Guis.GuiPage;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Set;

public class PaladinPage extends GuiPage {

    private final ClassSelect gui;

    private final GuiManager guiManager;

    public PaladinPage(
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

        context.drawIcon(0, -105, UiGlyphs.PALADIN_TITLE);

        List<String> description = PlayerClass.PALADIN.getDescription();
        context.drawTextContainer(-105, -16, description);

        /*
         * Decorative buttons
         */

        context.drawIcon(6, -108, UiGlyphs.ASSASSIN_ICON);

        context.drawIcon(6, -54, UiGlyphs.ELEMENTALIST_ICON);

        context.drawIcon(6, 0, UiGlyphs.MYSTIC_ICON);

        context.drawIcon(6, 54, UiGlyphs.BUTTON_3x3);

        context.drawIcon(6, 126, UiGlyphs.RANGER_ICON);

        context.drawIcon(6, 180, UiGlyphs.SHADOW_KNIGHT_ICON);

        context.drawIcon(6, 234, UiGlyphs.WARRIOR_ICON);

        /*
         * Character art
         */

        context.drawIcon(
                0,
                54,
                UiGlyphs.PALADIN_CHARACTER
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

                        return UiGlyphs.TEMPLAR_ICON_2;
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

                        return UiGlyphs.DAWN_ICON_2;
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

                        GuiSession session = guiManager.getSession(p);


                        session.setCurrentPage(gui.getMysticPage());
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

                        session.setCurrentPage(gui.getRangerPage());
                        guiManager.refresh(p);
                    }
                }
        );
    }

}
