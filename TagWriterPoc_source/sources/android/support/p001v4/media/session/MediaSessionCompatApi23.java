package android.support.p001v4.media.session;

import android.net.Uri;
import android.os.Bundle;

/* renamed from: android.support.v4.media.session.MediaSessionCompatApi23 */
class MediaSessionCompatApi23 {

    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi23$Callback */
    public interface Callback extends Callback {
        void onPlayFromUri(Uri uri, Bundle bundle);
    }

    /* renamed from: android.support.v4.media.session.MediaSessionCompatApi23$CallbackProxy */
    static class CallbackProxy<T extends Callback> extends CallbackProxy<T> {
        public CallbackProxy(T callback) {
            super(callback);
        }

        public void onPlayFromUri(Uri uri, Bundle extras) {
            ((Callback) this.mCallback).onPlayFromUri(uri, extras);
        }
    }

    MediaSessionCompatApi23() {
    }

    public static Object createCallback(Callback callback) {
        return new CallbackProxy(callback);
    }
}
