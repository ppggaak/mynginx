package com.example.nginx;

/**
 */

public class NginxNative {
    private static final String TAG = "lizg_test";

    static {
        System.loadLibrary("nginx");
    }

    public native int nativeInit();
    public native int nativeDestroy();
    public native int nativeRun();
}
