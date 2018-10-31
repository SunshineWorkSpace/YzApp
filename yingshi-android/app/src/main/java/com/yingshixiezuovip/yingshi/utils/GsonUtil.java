package com.yingshixiezuovip.yingshi.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Type;

public class GsonUtil {

    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static String toJson(Object obj, Type type) {
        return gson.toJson(obj, type);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String str, Type type) {
        return gson.fromJson(str, type);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static Object fromJson(Reader reader, Type type) {
        return gson.fromJson(reader, type);
    }
}
