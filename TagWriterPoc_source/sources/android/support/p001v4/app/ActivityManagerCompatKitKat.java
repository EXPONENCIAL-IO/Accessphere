package android.support.p001v4.app;

import android.app.ActivityManager;

/* renamed from: android.support.v4.app.ActivityManagerCompatKitKat */
class ActivityManagerCompatKitKat {
    ActivityManagerCompatKitKat() {
    }

    public static boolean isLowRamDevice(ActivityManager am) {
        return am.isLowRamDevice();
    }
}
