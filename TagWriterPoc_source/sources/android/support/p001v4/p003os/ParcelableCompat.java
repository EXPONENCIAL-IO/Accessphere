package android.support.p001v4.p003os;

import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable.Creator;

/* renamed from: android.support.v4.os.ParcelableCompat */
public class ParcelableCompat {

    /* renamed from: android.support.v4.os.ParcelableCompat$CompatCreator */
    static class CompatCreator<T> implements Creator<T> {
        final ParcelableCompatCreatorCallbacks<T> mCallbacks;

        public CompatCreator(ParcelableCompatCreatorCallbacks<T> callbacks) {
            this.mCallbacks = callbacks;
        }

        public T createFromParcel(Parcel source) {
            return this.mCallbacks.createFromParcel(source, null);
        }

        public T[] newArray(int size) {
            return this.mCallbacks.newArray(size);
        }
    }

    public static <T> Creator<T> newCreator(ParcelableCompatCreatorCallbacks<T> callbacks) {
        if (VERSION.SDK_INT >= 13) {
            return ParcelableCompatCreatorHoneycombMR2Stub.instantiate(callbacks);
        }
        return new CompatCreator(callbacks);
    }
}
