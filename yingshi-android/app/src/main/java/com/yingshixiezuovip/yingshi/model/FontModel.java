package com.yingshixiezuovip.yingshi.model;

import android.content.Intent;

import com.yingshixiezuovip.yingshi.utils.ColorUtils;
import com.yingshixiezuovip.yingshi.utils.L;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Resmic on 18/1/23.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class FontModel {
    private boolean blod = false;
    private boolean center = false;
    private int font = 14;
    private String color = "#000000";
    private String text;

    public boolean isBlod() {
        return blod;
    }

    public void setBlod(boolean blod) {
        this.blod = blod;
    }

    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public int getFont() {
        return font;
    }

    public void setFont(int font) {
        this.font = font;
    }

    public String getColor() {
        return color;
    }

    public String getRGBColor() {
        return ColorUtils.str2Rgb(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setRGBColor(String color) {
        this.color = ColorUtils.rgb2Str(color);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
