package android.support.p003v7.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.p003v7.appcompat.C0140R;
import android.support.p003v7.internal.text.AllCapsTransformationMethod;
import android.util.AttributeSet;
import android.widget.TextView;

/* renamed from: android.support.v7.internal.widget.CompatTextView */
public class CompatTextView extends TextView {
    public CompatTextView(Context context) {
        this(context, null);
    }

    public CompatTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompatTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray style = context.obtainStyledAttributes(attrs, C0140R.styleable.CompatTextView, defStyle, 0);
        boolean allCaps = style.getBoolean(C0140R.styleable.CompatTextView_textAllCaps, false);
        style.recycle();
        if (allCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(context));
        }
    }
}
