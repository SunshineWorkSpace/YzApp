package com.yingshixiezuovip.yingshi.datautils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Resmic on 2016/11/14.
 */

public class StringUtils {

    public static String istream2Str(InputStream inputStream) {
        BufferedReader bufferReader = null;
        String bf = null;
        StringBuffer sb = new StringBuffer();
        try {
            bufferReader = new BufferedReader(new InputStreamReader(
                    inputStream, "UTF-8"));
            while ((bf = bufferReader.readLine()) != null) {
                sb.append(bf);
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
