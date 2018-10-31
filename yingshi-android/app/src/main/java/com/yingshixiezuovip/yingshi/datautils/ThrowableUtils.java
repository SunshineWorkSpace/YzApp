package com.yingshixiezuovip.yingshi.datautils;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by Resmic on 2017/3/28.
 * Email:xiangyx@wenwen-tech.com
 */

public class ThrowableUtils {
    public static String getThrowableDetailsMessage(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        LineNumberReader reader = new LineNumberReader(new StringReader(
                sw.toString()));
        ArrayList<String> lines = new ArrayList<String>();
        try {
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        } catch (IOException ex) {
            lines.add(ex.toString());
        }
        String[] rep = new String[lines.size()];
        lines.toArray(rep);
        StringBuffer sb = new StringBuffer();
        for (String str : rep) {
            sb.append(str + "\n");
        }
        return sb.toString();
    }
}
