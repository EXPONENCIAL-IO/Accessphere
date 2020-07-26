package android.support.p000v4.app;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* renamed from: android.support.v4.app.BundleCompatDonut */
class BundleCompatDonut {
    private static final String TAG = "BundleCompatDonut";
    private static Method sGetIBinderMethod;
    private static boolean sGetIBinderMethodFetched;
    private static Method sPutIBinderMethod;
    private static boolean sPutIBinderMethodFetched;

    BundleCompatDonut() {
    }

    public static IBinder getBinder(Bundle bundle, String key) {
        if (!sGetIBinderMethodFetched) {
            try {
                sGetIBinderMethod = Bundle.class.getMethod("getIBinder", new Class[]{String.class});
                sGetIBinderMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to retrieve getIBinder method", e);
            }
            sGetIBinderMethodFetched = true;
        }
        if (sGetIBinderMethod != null) {
            try {
                return (IBinder) sGetIBinderMethod.invoke(bundle, new Object[]{key});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
                Log.i(TAG, "Failed to invoke getIBinder via reflection", e2);
                sGetIBinderMethod = null;
            }
        }
        return null;
    }

    public static void putBinder(Bundle bundle, String key, IBinder binder) {
        if (!sPutIBinderMethodFetched) {
            try {
                sPutIBinderMethod = Bundle.class.getMethod("putIBinder", new Class[]{String.class, IBinder.class});
                sPutIBinderMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i(TAG, "Failed to retrieve putIBinder method", e);
            }
            sPutIBinderMethodFetched = true;
        }
        if (sPutIBinderMethod != null) {
            try {
                sPutIBinderMethod.invoke(bundle, new Object[]{key, binder});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
                Log.i(TAG, "Failed to invoke putIBinder via reflection", e2);
                sPutIBinderMethod = null;
            }
        }
    }
}
