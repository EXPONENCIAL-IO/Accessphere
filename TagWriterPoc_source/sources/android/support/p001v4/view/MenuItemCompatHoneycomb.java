package android.support.p001v4.view;

import android.view.MenuItem;
import android.view.View;

/* renamed from: android.support.v4.view.MenuItemCompatHoneycomb */
class MenuItemCompatHoneycomb {
    MenuItemCompatHoneycomb() {
    }

    public static void setShowAsAction(MenuItem item, int actionEnum) {
        item.setShowAsAction(actionEnum);
    }

    public static MenuItem setActionView(MenuItem item, View view) {
        return item.setActionView(view);
    }

    public static MenuItem setActionView(MenuItem item, int resId) {
        return item.setActionView(resId);
    }

    public static View getActionView(MenuItem item) {
        return item.getActionView();
    }
}
