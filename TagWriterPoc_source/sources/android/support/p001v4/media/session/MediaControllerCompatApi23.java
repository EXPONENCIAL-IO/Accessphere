package android.support.p001v4.media.session;

import android.net.Uri;
import android.os.Bundle;

/* renamed from: android.support.v4.media.session.MediaControllerCompatApi23 */
class MediaControllerCompatApi23 {

    /* renamed from: android.support.v4.media.session.MediaControllerCompatApi23$TransportControls */
    public static class TransportControls extends android.support.p001v4.media.session.MediaControllerCompatApi21.TransportControls {
        public static void playFromUri(Object controlsObj, Uri uri, Bundle extras) {
            ((android.media.session.MediaController.TransportControls) controlsObj).playFromUri(uri, extras);
        }
    }

    MediaControllerCompatApi23() {
    }
}
