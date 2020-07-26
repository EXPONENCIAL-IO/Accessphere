package android.support.p003v7.internal.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.support.p003v7.appcompat.C0140R;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/* renamed from: android.support.v7.internal.widget.AppCompatPopupWindow */
public class AppCompatPopupWindow extends PopupWindow {
    private final boolean mOverlapAnchor;

    public AppCompatPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, C0140R.styleable.PopupWindow, defStyleAttr, 0);
        this.mOverlapAnchor = a.getBoolean(C0140R.styleable.PopupWindow_overlapAnchor, false);
        setBackgroundDrawable(a.getDrawable(C0140R.styleable.PopupWindow_android_popupBackground));
        a.recycle();
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (VERSION.SDK_INT < 21 && this.mOverlapAnchor) {
            yoff -= anchor.getHeight();
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @TargetApi(19)
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (VERSION.SDK_INT < 21 && this.mOverlapAnchor) {
            yoff -= anchor.getHeight();
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    public void update(View anchor, int xoff, int yoff, int width, int height) {
        if (VERSION.SDK_INT < 21 && this.mOverlapAnchor) {
            yoff -= anchor.getHeight();
        }
        super.update(anchor, xoff, yoff, width, height);
    }
}
