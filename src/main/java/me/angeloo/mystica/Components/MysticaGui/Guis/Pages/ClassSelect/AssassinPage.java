package me.angeloo.mystica.Components.MysticaGui.Guis.Pages.ClassSelect;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.GuiManager;
import me.angeloo.mystica.Components.MysticaGui.GuiSession;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect;
import me.angeloo.mystica.Components.MysticaGui.Guis.Pages.GuiPage;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import me.angeloo.mystica.Utility.TextRenderer.LineData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssassinPage extends GuiPage {

    private final ClassSelect gui;

    private final GuiManager guiManager;

    public AssassinPage(
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

        context.drawIcon(0, -105, UiGlyphs.ASSASSIN_TITLE);


        List<String> description = PlayerClass.ASSASSIN.getDescription();
        context.drawTextContainer(-105, -16, description);


        /*
         * Decorative buttons
         */


        context.drawIcon(6, -108, UiGlyphs.RANGER_ICON);

        context.drawIcon(6, -54, UiGlyphs.SHADOW_KNIGHT_ICON);

        context.drawIcon(6, 0, UiGlyphs.WARRIOR_ICON);

        context.drawIcon(6, 54, UiGlyphs.BUTTON_3x3);

        context.drawIcon(6, 126, UiGlyphs.ELEMENTALIST_ICON);

        context.drawIcon(6, 180, UiGlyphs.MYSTIC_ICON);

        context.drawIcon(6, 234, UiGlyphs.PALADIN_ICON);

        /*
         * Character art
         */

        context.drawIcon(
                0,
                54,
                UiGlyphs.ASSASSIN_CHARACTER
        );



        /*
         * subclasses
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

                        return UiGlyphs.DUELIST_ICON_2;
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

                        return UiGlyphs.ALCHEMIST_ICON_2;
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


                        session.setCurrentPage(gui.getWarriorPage());
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

                        session.setCurrentPage(gui.getElementalistPage());
                        guiManager.refresh(p);
                    }
                }
        );
    }

}
