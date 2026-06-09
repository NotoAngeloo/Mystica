package me.angeloo.mystica.Components.MysticaGui.Guis;

import me.angeloo.mystica.Components.MysticaGui.Gui;


public class TestGui extends Gui {

    @Override
    public GuiPage getInitialPage() {
        return null;
    }

    /*@Override
    public void build(
            Player player,
            GuiRenderContext context
    ) {

        //Templating for item display

        /*context.drawIcon(0, -96, UiGlyphs.ITEM_TOOLTIP_BACKGROUND);

        context.drawIcon(0, -96, UiGlyphs.ITEM_BANNER_RARE);

        //temp art, should be equip icon
        context.drawIcon(0, -92, UiGlyphs.BUTTON_2x2);

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
                        return UiGlyphs.BUTTON_1x3;
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

        List<LineData> equipButtonData = List.of(
                new LineData(ChatColor.BLACK + "Equip", 0)
        );

        context.drawText(13, -188, equipButtonData);

        List<LineData> nameData = List.of(
                new LineData(ChatColor.of(rareColor) + "Item Name", 12),
                new LineData(ChatColor.of(menuColor) + "Class: " + "Shadow Knight", 12),
                new LineData(ChatColor.of(menuColor) + "Level: " + "1", 16)
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
                        return UiGlyphs.BUTTON_2x2;
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
                        return UiGlyphs.BUTTON_2x2;
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
                        return UiGlyphs.BUTTON_2x2;
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





        //templating for class select



    }*/

}
