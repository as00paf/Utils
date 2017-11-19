package com.pafoid.utils.geo.config;

import com.pafoid.utils.geo.R;

/**
 * Config class used to stylize Markers
 */
public class MarkerConfig {

    private int drawableRes = R.drawable.ic_place_black_24dp;
    private int colorRes = R.color.colorPrimary;
    private int borderColorRes = R.color.colorPrimary;
    private int borderWidth = 2;
    private float anchorX = 0.5f;
    private float anchorY = 0.5f;
    private int offsetX = 6;
    private int offsetY = 6;

    public MarkerConfig() {
    }

    public static MarkerConfig build(){
        return new MarkerConfig();
    }

    //Getters/Setters
    public int getDrawableRes() {
        return drawableRes;
    }

    public MarkerConfig setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
        return this;
    }

    public int getColorRes() {
        return colorRes;
    }

    public MarkerConfig setColorRes(int colorRes) {
        this.colorRes = colorRes;
        return this;
    }

    public int getBorderColorRes() {
        return borderColorRes;
    }

    public MarkerConfig setBorderColorRes(int borderColorRes) {
        this.borderColorRes = borderColorRes;
        return this;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public MarkerConfig setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public float getAnchorX() {
        return anchorX;
    }

    public MarkerConfig setAnchorX(float anchorX) {
        this.anchorX = anchorX;
        return this;
    }

    public float getAnchorY() {
        return anchorY;
    }

    public MarkerConfig setAnchorY(float anchorY) {
        this.anchorY = anchorY;
        return this;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public MarkerConfig setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        return this;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public MarkerConfig setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        return this;
    }
}
