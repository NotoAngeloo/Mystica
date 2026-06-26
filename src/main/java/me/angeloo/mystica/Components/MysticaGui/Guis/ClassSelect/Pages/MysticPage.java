package me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Pages;

import me.angeloo.mystica.Components.CombatSystem.Classes.PlayerClass;
import me.angeloo.mystica.Components.CombatSystem.Classes.SubClass;
import me.angeloo.mystica.Components.MysticaGui.Font.Glyph;
import me.angeloo.mystica.Components.MysticaGui.Font.UiGlyphs;
import me.angeloo.mystica.Components.MysticaGui.GuiButton;
import me.angeloo.mystica.Components.MysticaGui.GuiManager;
import me.angeloo.mystica.Components.MysticaGui.GuiSession;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.ClassSelect;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Panels.ArcanePanel;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Panels.ChaosPanel;
import me.angeloo.mystica.Components.MysticaGui.Guis.ClassSelect.Panels.ShepardPanel;
import me.angeloo.mystica.Components.MysticaGui.Guis.GuiPage;
import me.angeloo.mystica.Components.MysticaGui.Render.GuiRenderContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Set;

public class MysticPage extends GuiPage{

    private final ClassSelect gui;

    private final GuiManager guiManager;

    private final ShepardPanel shepardPanel;
    private final ArcanePanel arcanePanel;
    private final ChaosPanel chaosPanel;

    public MysticPage(
            ClassSelect gui,
            GuiManager guiManager
    ) {

        this.gui = gui;
        this.guiManager = guiManager;
        shepardPanel = new ShepardPanel(gui);
        arcanePanel = new ArcanePanel(gui);
        chaosPanel = new ChaosPanel(gui);
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

        context.drawIcon(0, -105, UiGlyphs.MYSTIC_TITLE);

        List<String> description = PlayerClass.MYSTIC.getDescription();
        context.drawTextContainer(-105, -16, description);

        /*
         * Decorative buttons
         */

        context.drawIcon(6, -108, UiGlyphs.WARRIOR_ICON);

        context.drawIcon(6, -54, UiGlyphs.ASSASSIN_ICON);

        context.drawIcon(6, 0, UiGlyphs.ELEMENTALIST_ICON);

        context.drawIcon(6, 54, UiGlyphs.BUTTON_3x3);

        context.drawIcon(6, 126, UiGlyphs.PALADIN_ICON);

        context.drawIcon(6, 180, UiGlyphs.RANGER_ICON);

        context.drawIcon(6, 234, UiGlyphs.SHADOW_KNIGHT_ICON);


        context.drawIcon(
                0,
                54,
                UiGlyphs.MYSTIC_CHARACTER
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

                        return Set.of(
                                7, 8,
                                16, 17
                        );
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.SHEPARD_ICON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                        if(gui.getSelectedSubclass() != SubClass.SHEPARD){
                            gui.setSelectedSubclass(SubClass.SHEPARD);
                            guiManager.refresh(p);
                            return;
                        }

                        gui.setSelectedSubclass(null);
                        guiManager.refresh(p);

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

                        return UiGlyphs.ARCANE_ICON_2;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                        if(gui.getSelectedSubclass() != SubClass.ARCANE){
                            gui.setSelectedSubclass(SubClass.ARCANE);
                            guiManager.refresh(p);
                            return;
                        }

                        gui.setSelectedSubclass(null);
                        guiManager.refresh(p);

                    }
                }
        );

        /*button(
                context,
                new GuiButton() {

                    @Override
                    public int slot() {
                        return 43;
                    }

                    @Override
                    public Set<Integer> interactionSlots() {

                        return Set.of(
                                43, 44,
                                52, 53
                        );
                    }

                    @Override
                    public Glyph glyph() {

                        return UiGlyphs.CHAOS_ICN;
                    }

                    @Override
                    public void click(
                            Player p,
                            GuiPage page,
                            InventoryClickEvent event
                    ) {

                        if(gui.getSelectedSubclass() != SubClass.ARCANE){
                            gui.setSelectedSubclass(SubClass.ARCANE);
                            guiManager.refresh(p);
                            return;
                        }

                        gui.setSelectedSubclass(null);
                        guiManager.refresh(p);

                    }
                }
        );*/

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


                        session.setCurrentPage(gui.getElementalistPage());
                        gui.setSelectedSubclass(null);
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

                        session.setCurrentPage(gui.getPaladinPage());
                        gui.setSelectedSubclass(null);
                        guiManager.refresh(p);
                    }
                }
        );

        if(shepardPanel.isVisible(player)){
            shepardPanel.build(player, context);
        }

        if(arcanePanel.isVisible(player)){
            arcanePanel.build(player, context);
        }

        if(chaosPanel.isVisible(player)){
            chaosPanel.build(player, context);
        }
    }

}
