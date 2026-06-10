package me.angeloo.mystica.Utility.ShapeRenderer.Gradient;

import java.awt.*;

public final class GradientUtil {

    private GradientUtil() {}

    public static Color sample(
            GradientDirection direction,
            Color start,
            Color end,
            int x,
            int y,
            int width,
            int height
    ) {
        float t = switch (direction) {
            case VERTICAL      -> vertical(y, height);
            case HORIZONTAL    -> horizontal(x, width);
            case CENTER        -> center(x, y, width, height);
            case TOP_CORNER    -> topCorner(x, y, width, height);
            case BOTTOM_CORNER -> bottomCorner(x, y, width, height);
        };

        return lerp(start, end, clamp(t));
    }

    public static Color lerp(Color start, Color end, float t) {
        int r = (int)(start.getRed()   + (end.getRed()   - start.getRed())   * t);
        int g = (int)(start.getGreen() + (end.getGreen() - start.getGreen()) * t);
        int b = (int)(start.getBlue()  + (end.getBlue()  - start.getBlue())  * t);

        return new Color(r, g, b);
    }

    private static float clamp(float t) {
        return Math.max(0F, Math.min(1F, t));
    }

    private static float vertical(
            int y,
            int height
    ) {
        if (height <= 1) {
            return 0F;
        }

        return y / (float)(height - 1);
    }

    private static float horizontal(
            int x,
            int width
    ) {
        if (width <= 1) {
            return 0F;
        }

        return x / (float)(width - 1);
    }

    private static float center(
            int x,
            int y,
            int width,
            int height
    ) {
        float cx = (width - 1) / 2F;
        float cy = (height - 1) / 2F;

        float dx = x - cx;
        float dy = y - cy;

        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        float maxDistance = (float)Math.sqrt(cx * cx + cy * cy);

        return distance / maxDistance;
    }

    private static float topCorner(
            int x,
            int y,
            int width,
            int height
    ) {

        float distance = (float)Math.sqrt(
                x * x +
                        y * y
        );

        float maxDistance = (float)Math.sqrt(
                (width - 1) * (width - 1)
                        +
                        (height - 1) * (height - 1)
        );

        return distance / maxDistance;
    }

    private static float bottomCorner(
            int x,
            int y,
            int width,
            int height
    ) {

        float dy = (height - 1) - y;

        float distance = (float)Math.sqrt(
                x * x +
                        dy * dy
        );

        float maxDistance = (float)Math.sqrt(
                (width - 1) * (width - 1)
                        +
                        (height - 1) * (height - 1)
        );

        return distance / maxDistance;
    }

}
