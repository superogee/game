package com.game;

public class UiButton extends UiBox {
    public String label = "";

    public void set(float x, float y, float width, float height, String label) {
        super.set(x, y, width, height);
        this.label = label;
    }
}
