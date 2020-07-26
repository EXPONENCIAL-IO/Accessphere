package android.support.p004v7.widget;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/* renamed from: android.support.v7.widget.AppCompatSeekBarHelper */
class AppCompatSeekBarHelper extends AppCompatProgressBarHelper {
    private static final int[] TINT_ATTRS = {16843074};
    private final SeekBar mView;

    AppCompatSeekBarHelper(SeekBar view, TintManager tintManager) {
        super(view, tintManager);
        this.mView = view;
    }

    /* access modifiers changed from: 0000 */
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        super.loadFromAttributes(attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
        Drawable drawable = a.getDrawableIfKnown(0);
        if (drawable != null) {
            this.mView.setThumb(drawable);
        }
        a.recycle();
    }
}
