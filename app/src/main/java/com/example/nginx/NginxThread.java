/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.example.nginx;

import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;


class NginxThread implements Runnable {

    private static final String TAG = "lizg_test";
    private String[] mArgv;
    private Process mProcess;

    public NginxThread( String[] argv) {
        mArgv = argv;
    }

    private void stopProcess() {
        if(mProcess != null){
            mProcess.destroy();
        }
    }

    @Override
    public void run() {
        try {
            Log.i(TAG, "Starting nginx");
            //startNginxThreadArgs(mArgv);
            startNginxThread();
        } catch (Exception e) {
            Log.e(TAG, "nginx Got " + e.toString());
        } finally {
            int exitvalue = 0;
            try {
                if (mProcess != null)
                    exitvalue = mProcess.waitFor();
            } catch (IllegalThreadStateException ite) {
            } catch (InterruptedException ie) {
            }
            Log.i(TAG, "Exiting");
        }
    }
    private void startNginxThread() {
        NginxNative mNginxNative = new NginxNative();
        mNginxNative.nativeRun();
    }

    private void startNginxThreadArgs(String[] argv) {
        LinkedList<String> argvlist = new LinkedList<>();

        Collections.addAll(argvlist, argv);

        ProcessBuilder pb = new ProcessBuilder(argvlist);
        String lbpath = genLibraryPath(argv, pb);

        pb.environment().put("LD_LIBRARY_PATH", lbpath);
        pb.redirectErrorStream(true);
        BufferedReader br = null;
        try {
            mProcess = pb.start();
            // Close the output, since we don't need it
            mProcess.getOutputStream().close();
            InputStream in = mProcess.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));

            while (true) {
                String logline = br.readLine();
                if (logline == null) {
                    return;
                }
            }

        } catch (IOException e) {
            Log.i(TAG, "Starting nginx error:"+e.toString());
            stopProcess();
        }finally {
            if(br !=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private String genLibraryPath(String[] argv, ProcessBuilder pb) {
        // Hack until I find a good way to get the real library path
        String applibpath = argv[0].replaceFirst("/cache/.*$", "/lib");

        String lbpath = pb.environment().get("LD_LIBRARY_PATH");
        if (lbpath == null)
            lbpath = applibpath;
        else
            lbpath = applibpath + ":" + lbpath;

        //if (!applibpath.equals(mNativeDir)) {
        //    lbpath = mNativeDir + ":" + lbpath;
        //}
        return lbpath;
    }

}
