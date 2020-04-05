/*
 * Copyright (c) 2012-2016
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.example.nginx;

import android.content.Context;
import android.os.Build;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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


    static String[] buildOpenvpnArgv(Context c) {
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


    public static void unZipFile(Context context,String archive, String decompressDir) throws IOException {



        try {
            File cacheDir=context.getCacheDir();
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            File outFile =new File(cacheDir,archive);
            if (!outFile.exists()){
                boolean res=outFile.createNewFile();

            }else {
                if (outFile.length()>10){
                    return;
                }
            }
            InputStream is=context.getAssets().open(archive);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        BufferedInputStream bi;
        ZipFile zf = new ZipFile(archive);
        Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory()) {
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                if (decompressDir.endsWith("zip")) {
                    decompressDir = decompressDir.substring(0, decompressDir.lastIndexOf("zip"));
                }
                File fileDirFile = new File(decompressDir);
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs();
                }
                String substring = entryName.substring(entryName.lastIndexOf("/") + 1, entryName.length());
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + substring));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
    }


}
