package me.angeloo.mystica.Components.MysticaGui.Render;

import me.angeloo.mystica.Components.MysticaGui.Font.UiSpacing;

public final class RenderCursor {

    private int position;

    public int position(){
        return position;
    }

    public void seek(
            StringBuilder builder,
            int target
    ) {

        int delta =
                target - position;

        if(delta == 0) {
            return;
        }

        builder.append(
                UiSpacing.offset(delta)
        );

        position = target;
    }

    public void advance(int width) {

        position += width;
    }

}
