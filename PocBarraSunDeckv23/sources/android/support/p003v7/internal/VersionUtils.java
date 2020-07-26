package android.support.p003v7.internal;

import android.os.Build.VERSION;

/* renamed from: android.support.v7.internal.VersionUtils */
public class VersionUtils {
    private VersionUtils() {
    }

    public static boolean isAtLeastL() {
        return VERSION.SDK_INT >= 21;
    }
}
