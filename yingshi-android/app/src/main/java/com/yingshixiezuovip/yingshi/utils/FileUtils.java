package com.yingshixiezuovip.yingshi.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;


public class FileUtils {
    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }

    public static File getCachePath() {
        return getPath("ImageCache");
    }

    public static File getTakePhotoCachePath() {
        return getPath("Photo");
    }

    public static File getCarshPath() {
        return getPath("carsh");
    }

    public static File getPath(String fileName) {
        File file = Environment.getExternalStorageDirectory();
        File cachePath = new File(file.getPath() + "/" + "yingzhe/" + fileName);
        if (!cachePath.isDirectory())
            cachePath.mkdirs();
        return cachePath;
    }

    public static File getAdPicCache() {
        File ditectoryFile = getPath("adcache");
        File tempFile = new File(ditectoryFile, "yingzhead.jpg.temp");
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tempFile;
    }

    public static void tempToCache(String downloadUrl) {
        String filePath = getAdPicCache().getPath();
        File tempFile = new File(filePath);
        File cacheFile = new File(filePath.substring(0, filePath.lastIndexOf(".temp")));
        tempFile.renameTo(cacheFile);
    }

    public static File getAdPic() {
        File ditectoryFile = getPath("adcache");
        File tempFile = new File(ditectoryFile, "yingzhead.jpg");
        return tempFile;
    }

    public static boolean hasImageExist(String downloadUrl) {
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        File file = new File(getPath("Photo"), fileName);
        return file.exists();
    }

    /**
     * 图片缓存路径
     *
     * @param downloadUrl 下载地址
     * @return
     */
    public static File getImageSaveCache(String downloadUrl) {
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        return getTempCache(getPath("Photo"), fileName);
    }

    public static File getTempCache(File fileDitectory, String fileName) {
        File tempFile = new File(fileDitectory, fileName + ".wtmp");

        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tempFile;
    }

    public static File tempRealCache(String cachePath) {
        File tempFile = new File(cachePath);
        File cacheFile = new File(cachePath.substring(0, cachePath.lastIndexOf(".wtmp")));
        tempFile.renameTo(cacheFile);
        return cacheFile;
    }
}