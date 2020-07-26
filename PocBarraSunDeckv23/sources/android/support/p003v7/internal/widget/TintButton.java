package android.support.p003v7.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/* renamed from: android.support.v7.internal.widget.TintButton */
public class TintButton extends Button {
    private static final int[] TINT_ATTRS = {16842964, 16842804};
    private final TintManager mTintManager;

    public TintButton(Context context) {
        this(context, null);
    }

    public TintButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16842824);
    }

    public TintButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, TINT_ATTRS, defStyleAttr, 0);
        if (a.hasValue(0)) {
            setBackgroundDrawable(a.getDrawable(0));
        }
        this.mTintManager = a.getTintManager();
    }

    public void setBackgroundResource(int resid) {
        setBackgroundDrawable(this.mTintManager.getDrawable(resid));
    }
}
