package android.support.p001v4.widget;

import android.view.View.OnTouchListener;
import android.widget.PopupMenu;

/* renamed from: android.support.v4.widget.PopupMenuCompatKitKat */
class PopupMenuCompatKitKat {
    PopupMenuCompatKitKat() {
    }

    public static OnTouchListener getDragToOpenListener(Object popupMenu) {
        return ((PopupMenu) popupMenu).getDragToOpenListener();
    }
}
