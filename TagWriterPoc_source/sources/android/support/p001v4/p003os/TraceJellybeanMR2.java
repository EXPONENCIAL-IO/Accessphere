package android.support.p001v4.p003os;

import android.os.Trace;

/* renamed from: android.support.v4.os.TraceJellybeanMR2 */
class TraceJellybeanMR2 {
    TraceJellybeanMR2() {
    }

    public static void beginSection(String section) {
        Trace.beginSection(section);
    }

    public static void endSection() {
        Trace.endSection();
    }
}
