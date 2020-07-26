package android.support.p003v7.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/* renamed from: android.support.v7.internal.widget.TintEditText */
public class TintEditText extends EditText {
    private static final int[] TINT_ATTRS = {16842964};

    public TintEditText(Context context) {
        this(context, null);
    }

    public TintEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 16842862);
    }

    public TintEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, TINT_ATTRS, defStyleAttr, 0);
        setBackgroundDrawable(a.getDrawable(0));
        a.recycle();
    }
}
