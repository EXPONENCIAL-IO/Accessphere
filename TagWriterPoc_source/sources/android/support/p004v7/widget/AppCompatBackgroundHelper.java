package android.support.p004v7.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.p001v4.view.ViewCompat;
import android.support.p004v7.appcompat.C0236R;
import android.support.p004v7.graphics.drawable.DrawableUtils;
import android.util.AttributeSet;
import android.view.View;

/* renamed from: android.support.v7.widget.AppCompatBackgroundHelper */
class AppCompatBackgroundHelper {
    private TintInfo mBackgroundTint;
    private TintInfo mInternalBackgroundTint;
    private final TintManager mTintManager;
    private final View mView;

    AppCompatBackgroundHelper(View view, TintManager tintManager) {
        this.mView = view;
        this.mTintManager = tintManager;
    }

    /* access modifiers changed from: 0000 */
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = this.mView.getContext().obtainStyledAttributes(attrs, C0236R.styleable.ViewBackgroundHelper, defStyleAttr, 0);
        try {
            if (a.hasValue(C0236R.styleable.ViewBackgroundHelper_android_background)) {
                ColorStateList tint = this.mTintManager.getTintList(a.getResourceId(C0236R.styleable.ViewBackgroundHelper_android_background, -1));
                if (tint != null) {
                    setInternalBackgroundTint(tint);
                }
            }
            if (a.hasValue(C0236R.styleable.ViewBackgroundHelper_backgroundTint)) {
                ViewCompat.setBackgroundTintList(this.mView, a.getColorStateList(C0236R.styleable.ViewBackgroundHelper_backgroundTint));
            }
            if (a.hasValue(C0236R.styleable.ViewBackgroundHelper_backgroundTintMode)) {
                ViewCompat.setBackgroundTintMode(this.mView, DrawableUtils.parseTintMode(a.getInt(C0236R.styleable.ViewBackgroundHelper_backgroundTintMode, -1), null));
            }
        } finally {
            a.recycle();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onSetBackgroundResource(int resId) {
        setInternalBackgroundTint(this.mTintManager != null ? this.mTintManager.getTintList(resId) : null);
    }

    /* access modifiers changed from: 0000 */
    public void onSetBackgroundDrawable(Drawable background) {
        setInternalBackgroundTint(null);
    }

    /* access modifiers changed from: 0000 */
    public void setSupportBackgroundTintList(ColorStateList tint) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintList = tint;
        this.mBackgroundTint.mHasTintList = true;
        applySupportBackgroundTint();
    }

    /* access modifiers changed from: 0000 */
    public ColorStateList getSupportBackgroundTintList() {
        if (this.mBackgroundTint != null) {
            return this.mBackgroundTint.mTintList;
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void setSupportBackgroundTintMode(Mode tintMode) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintMode = tintMode;
        this.mBackgroundTint.mHasTintMode = true;
        applySupportBackgroundTint();
    }

    /* access modifiers changed from: 0000 */
    public Mode getSupportBackgroundTintMode() {
        if (this.mBackgroundTint != null) {
            return this.mBackgroundTint.mTintMode;
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void applySupportBackgroundTint() {
        Drawable background = this.mView.getBackground();
        if (background == null) {
            return;
        }
        if (this.mBackgroundTint != null) {
            TintManager.tintDrawable(background, this.mBackgroundTint, this.mView.getDrawableState());
        } else if (this.mInternalBackgroundTint != null) {
            TintManager.tintDrawable(background, this.mInternalBackgroundTint, this.mView.getDrawableState());
        }
    }

    /* access modifiers changed from: 0000 */
    public void setInternalBackgroundTint(ColorStateList tint) {
        if (tint != null) {
            if (this.mInternalBackgroundTint == null) {
                this.mInternalBackgroundTint = new TintInfo();
            }
            this.mInternalBackgroundTint.mTintList = tint;
            this.mInternalBackgroundTint.mHasTintList = true;
        } else {
            this.mInternalBackgroundTint = null;
        }
        applySupportBackgroundTint();
    }
}
