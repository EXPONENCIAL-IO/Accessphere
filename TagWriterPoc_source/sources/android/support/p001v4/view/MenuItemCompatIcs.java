package android.support.p001v4.view;

import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;

/* renamed from: android.support.v4.view.MenuItemCompatIcs */
class MenuItemCompatIcs {

    /* renamed from: android.support.v4.view.MenuItemCompatIcs$OnActionExpandListenerWrapper */
    static class OnActionExpandListenerWrapper implements OnActionExpandListener {
        private SupportActionExpandProxy mWrapped;

        public OnActionExpandListenerWrapper(SupportActionExpandProxy wrapped) {
            this.mWrapped = wrapped;
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            return this.mWrapped.onMenuItemActionExpand(item);
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            return this.mWrapped.onMenuItemActionCollapse(item);
        }
    }

    /* renamed from: android.support.v4.view.MenuItemCompatIcs$SupportActionExpandProxy */
    interface SupportActionExpandProxy {
        boolean onMenuItemActionCollapse(MenuItem menuItem);

        boolean onMenuItemActionExpand(MenuItem menuItem);
    }

    MenuItemCompatIcs() {
    }

    public static boolean expandActionView(MenuItem item) {
        return item.expandActionView();
    }

    public static boolean collapseActionView(MenuItem item) {
        return item.collapseActionView();
    }

    public static boolean isActionViewExpanded(MenuItem item) {
        return item.isActionViewExpanded();
    }

    public static MenuItem setOnActionExpandListener(MenuItem item, SupportActionExpandProxy listener) {
        return item.setOnActionExpandListener(new OnActionExpandListenerWrapper(listener));
    }
}
