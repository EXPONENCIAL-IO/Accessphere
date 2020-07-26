package android.support.p003v7.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/* renamed from: android.support.v7.internal.widget.TintAutoCompleteTextView */
public class TintAutoCompleteTextView extends AutoCompleteTextView {
    private static final int[] TINT_ATTRS = {16842964, 16843126};
    private final TintManager mTintManager;

    public TintAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public TintAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842859);
    }

    public TintAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, TINT_ATTRS, defStyleAttr, 0);
        setBackgroundDrawable(a.getDrawable(0));
        if (a.hasValue(1)) {
            setDropDownBackgroundDrawable(a.getDrawable(1));
        }
        a.recycle();
        this.mTintManager = a.getTintManager();
    }

    public void setDropDownBackgroundResource(int id) {
        setDropDownBackgroundDrawable(this.mTintManager.getDrawable(id));
    }
}
