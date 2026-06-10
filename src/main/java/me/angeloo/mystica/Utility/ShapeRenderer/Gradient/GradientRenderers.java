package me.angeloo.mystica.Utility.ShapeRenderer.Gradient;

import me.angeloo.mystica.Utility.ShapeRenderer.PixelGlyphRegistry;

public class GradientRenderers {

    private final PixelGlyphRegistry registry;

    public GradientRenderers(PixelGlyphRegistry registry){
        this.registry = registry;
    }

    public GradientRenderer get(
            GradientDirection direction
    ) {

        return switch(direction) {

            case VERTICAL ->
                    new VerticalGradientRenderer(registry);

            case HORIZONTAL ->
                    new HorizontalGradientRenderer(registry);

            case CENTER,
                    TOP_CORNER,
                    BOTTOM_CORNER ->
                    new DistanceGradientRenderer(registry);
        };
    }

}
