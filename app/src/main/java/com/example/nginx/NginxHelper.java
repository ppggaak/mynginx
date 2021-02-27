/*
 * Copyright (c) 2012-2016
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.example.nginx;

import android.content.Context;
import android.os.Build;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NginxHelper {
    private static final String NGINXRUN = "nginx";
    private static final String TAG = "lizg_test";
    private static String writeNginx(Context context) {

        File nginxExecutable = new File(context.getCacheDir(), getExecutableName() );
        if ((nginxExecutable.exists() && nginxExecutable.canExecute()) || writeNginxBinary(context, nginxExecutable)) {
            return nginxExecutable.getPath();
        }

        return null;
    }

    private static String getExecutableName() {
        return NGINXRUN;
    }


    static String[] buildNginxArgv(Context c) {
        Vector<String> args = new Vector<>();

        String binaryName = writeNginx(c);
        // Add fixed paramenters
        if (binaryName == null) {
            return null;
        }

        args.add(binaryName);

        args.add(" ");
        //args.add(getConfigFilePath(c));

        return args.toArray(new String[args.size()]);
    }

    private static boolean writeNginxBinary(Context context, File mnginxout) {
        try {
            InputStream mnginx;

            try {
                mnginx = context.getAssets().open(getExecutableName() );
            } catch (IOException errabi) {
                return false;
            }

            FileOutputStream fout = new FileOutputStream(mnginxout);
            byte buf[] = new byte[4096];

            int lenread = mnginx.read(buf);
            while (lenread > 0) {
                fout.write(buf, 0, lenread);
                lenread = mnginx.read(buf);
            }
            fout.close();

            if (!mnginxout.setExecutable(true)) {
                return false;
            }


            return true;
        } catch (IOException e) {
            return false;
        }

    }

    /**
     * 在/data/data/下创建一个file文件夹，存放文件
     */
    public static void copyZipFile(Context context, String fileName) {
        InputStream in = null;
        FileOutputStream out = null;
        String path = "/data/data/" + context.getPackageName() + "/file/";
        File file = new File(path + fileName);

        //创建文件夹
        File filePath = new File(path);
        if (!filePath.exists()){
            filePath.mkdirs();

            try {
                in = context.getAssets().open(fileName); // 从assets目录下复制
                out = new FileOutputStream(file);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                Log.e(TAG,e.toString());
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                } catch (IOException e1) {
                    Log.e(TAG,e1.toString());
                }
            }
        }

        Log.d(TAG, "unzip html file" );
        try {
            upZipFile(file,"/sdcard/");
        } catch (Exception e) {
            Log.e(TAG,e.toString());
        }

    }


    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     * @param zipFile zip文件
     * @param folderPath 解压到的地址
     * @throws IOException
     */
    public static void upZipFile(File zipFile, String folderPath) throws IOException {
        File filePath = new File(folderPath+"nginx");
        if (filePath.exists())
            return;
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d(TAG, "isDirectory: " + ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                //Log.d(TAG, "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d(TAG, "file: " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
//                try {
//                    substr = new String(substr.getBytes("8859_1"), "GB2312");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                ret = new File(ret, substr);

            }
            Log.d(TAG, "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
//            try {
//                substr = new String(substr.getBytes("8859_1"), "GB2312");
//                Log.d(TAG, "substr = " + substr);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            ret = new File(ret, substr);
            // Log.d(TAG, "2ret = " + ret);
            return ret;
        }
        return ret;
    }

}
