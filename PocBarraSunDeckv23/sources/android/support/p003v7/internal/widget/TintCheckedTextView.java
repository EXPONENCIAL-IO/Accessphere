package android.support.p003v7.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

/* renamed from: android.support.v7.internal.widget.TintCheckedTextView */
public class TintCheckedTextView extends CheckedTextView {
    private static final int[] TINT_ATTRS = {16843016};
    private final TintManager mTintManager;

    public TintCheckedTextView(Context context) {
        this(context, null);
    }

    public TintCheckedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16843720);
    }

    public TintCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, TINT_ATTRS, defStyleAttr, 0);
        setCheckMarkDrawable(a.getDrawable(0));
        a.recycle();
        this.mTintManager = a.getTintManager();
    }

    public void setCheckMarkDrawable(int resid) {
        setCheckMarkDrawable(this.mTintManager.getDrawable(resid));
    }
}
