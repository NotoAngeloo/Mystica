package me.angeloo.mystica.Utility.ShapeRenderer.Gradient;

import me.angeloo.mystica.Utility.ShapeRenderer.PixelGlyphRegistry;

public abstract class AbstractGradientRenderer implements GradientRenderer{

    protected final PixelGlyphRegistry registry;

    protected AbstractGradientRenderer(PixelGlyphRegistry registry){
        this.registry = registry;
    }

}
