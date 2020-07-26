package android.support.p004v7.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.p001v4.view.ViewCompat;
import android.support.p004v7.appcompat.C0236R;
import android.util.AttributeSet;
import android.widget.RatingBar;

/* renamed from: android.support.v7.widget.AppCompatRatingBar */
public class AppCompatRatingBar extends RatingBar {
    private AppCompatProgressBarHelper mAppCompatProgressBarHelper;
    private TintManager mTintManager;

    public AppCompatRatingBar(Context context) {
        this(context, null);
    }

    public AppCompatRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, C0236R.attr.ratingBarStyle);
    }

    public AppCompatRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTintManager = TintManager.get(context);
        this.mAppCompatProgressBarHelper = new AppCompatProgressBarHelper(this, this.mTintManager);
        this.mAppCompatProgressBarHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Bitmap sampleTile = this.mAppCompatProgressBarHelper.getSampleTime();
        if (sampleTile != null) {
            setMeasuredDimension(ViewCompat.resolveSizeAndState(sampleTile.getWidth() * getNumStars(), widthMeasureSpec, 0), getMeasuredHeight());
        }
    }
}
