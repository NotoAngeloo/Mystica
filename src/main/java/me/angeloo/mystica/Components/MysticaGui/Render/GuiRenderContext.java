package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.Command.DrawCommand;

import java.util.*;

public class GuiRenderContext {

    private final Map<RenderLayer, List<DrawCommand>> layers = new EnumMap<>(RenderLayer.class);

    public void draw(RenderLayer layer, DrawCommand command){

        layers.computeIfAbsent(layer, k-> new ArrayList<>()).add(command);

    }

    public List<DrawCommand> getLayer(RenderLayer layer){
        return layers.getOrDefault(layer, Collections.emptyList());
    }

}
