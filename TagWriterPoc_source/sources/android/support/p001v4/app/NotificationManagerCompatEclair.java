package android.support.p001v4.app;

import android.app.Notification;
import android.app.NotificationManager;

/* renamed from: android.support.v4.app.NotificationManagerCompatEclair */
class NotificationManagerCompatEclair {
    NotificationManagerCompatEclair() {
    }

    static void cancelNotification(NotificationManager notificationManager, String tag, int id) {
        notificationManager.cancel(tag, id);
    }

    public static void postNotification(NotificationManager notificationManager, String tag, int id, Notification notification) {
        notificationManager.notify(tag, id, notification);
    }
}
