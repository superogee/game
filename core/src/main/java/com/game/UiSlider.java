package com.game;

import com.badlogic.gdx.math.MathUtils;

public class UiSlider extends UiBox {
    public float value;

    public void set(float x, float y, float width, float height, float value) {
        super.set(x, y, width, height);
        this.value = MathUtils.clamp(value, 0.0f, 1.0f);
    }

    public float getHandleCenterX() {
        return x + value * width;
    }

    public void setValueFrom(float px) {
        value = MathUtils.clamp((px - x) / width, 0.0f, 1.0f);
    }
}
