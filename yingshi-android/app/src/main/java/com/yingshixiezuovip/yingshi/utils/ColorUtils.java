package com.yingshixiezuovip.yingshi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Resmic on 18/1/24.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

public class ColorUtils {
    public static String rgb2Str(String rgb) {
        String pattern = "rgb\\((\\d*),\\s+(\\d*),\\s+(\\d*)\\)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(rgb);

        String color;
        if (m.find()) {
            try {
                Integer red = Integer.parseInt(m.group(1));
                Integer green = Integer.parseInt(m.group(2));
                Integer blue = Integer.parseInt(m.group(3));

                String redStr = dealStr(red.toHexString(red));
                String greenStr = dealStr(green.toHexString(green));
                String blueStr = dealStr(blue.toHexString(blue));

                color = "#" + redStr + greenStr + blueStr;
            } catch (Exception e) {
                color = "#000000";
            }
        } else {
            color = "#000000";
        }

        return color;
    }

    private static String dealStr(String color) {
        if (color.length() == 1) {
            return "0" + color.toUpperCase();
        } else {
            return color.toUpperCase();
        }
    }

    public static String str2Rgb(String color) {
        String redStr = color.substring(1, 3);
        String greenStr = color.substring(3, 5);
        String blueStr = color.substring(5, 7);
        int red = Integer.parseInt(redStr, 16);
        int green = Integer.parseInt(greenStr, 16);
        int blue = Integer.parseInt(blueStr, 16);
        return "rgb(" + red + ", " + green + ", " + blue + ")";
    }
}
