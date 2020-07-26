package android.support.p001v4.widget;

import android.widget.OverScroller;

/* renamed from: android.support.v4.widget.ScrollerCompatIcs */
class ScrollerCompatIcs {
    ScrollerCompatIcs() {
    }

    public static float getCurrVelocity(Object scroller) {
        return ((OverScroller) scroller).getCurrVelocity();
    }
}
