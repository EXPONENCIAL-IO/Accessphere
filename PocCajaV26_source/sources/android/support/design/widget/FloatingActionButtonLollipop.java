package android.support.design.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p000v4.view.ViewCompat;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

@TargetApi(21)
class FloatingActionButtonLollipop extends FloatingActionButtonHoneycombMr1 {
    private Drawable mBorderDrawable;
    private Interpolator mInterpolator;
    private RippleDrawable mRippleDrawable;
    private Drawable mShapeDrawable;

    FloatingActionButtonLollipop(View view, ShadowViewDelegate shadowViewDelegate) {
        super(view, shadowViewDelegate);
        if (!view.isInEditMode()) {
            this.mInterpolator = AnimationUtils.loadInterpolator(this.mView.getContext(), 17563661);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundDrawable(Drawable originalBackground, ColorStateList backgroundTint, Mode backgroundTintMode, int rippleColor, int borderWidth) {
        Drawable rippleContent;
        this.mShapeDrawable = DrawableCompat.wrap(originalBackground);
        DrawableCompat.setTintList(this.mShapeDrawable, backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, backgroundTintMode);
        }
        if (borderWidth > 0) {
            this.mBorderDrawable = createBorderDrawable(borderWidth, backgroundTint);
            rippleContent = new LayerDrawable(new Drawable[]{this.mBorderDrawable, this.mShapeDrawable});
        } else {
            this.mBorderDrawable = null;
            rippleContent = this.mShapeDrawable;
        }
        this.mRippleDrawable = new RippleDrawable(ColorStateList.valueOf(rippleColor), rippleContent, null);
        this.mShadowViewDelegate.setBackgroundDrawable(this.mRippleDrawable);
        this.mShadowViewDelegate.setShadowPadding(0, 0, 0, 0);
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundTintList(ColorStateList tint) {
        DrawableCompat.setTintList(this.mShapeDrawable, tint);
        if (this.mBorderDrawable != null) {
            DrawableCompat.setTintList(this.mBorderDrawable, tint);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundTintMode(Mode tintMode) {
        DrawableCompat.setTintMode(this.mShapeDrawable, tintMode);
    }

    /* access modifiers changed from: 0000 */
    public void setRippleColor(int rippleColor) {
        this.mRippleDrawable.setColor(ColorStateList.valueOf(rippleColor));
    }

    public void setElevation(float elevation) {
        ViewCompat.setElevation(this.mView, elevation);
    }

    /* access modifiers changed from: 0000 */
    public void setPressedTranslationZ(float translationZ) {
        StateListAnimator stateListAnimator = new StateListAnimator();
        stateListAnimator.addState(PRESSED_ENABLED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", new float[]{translationZ})));
        stateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", new float[]{translationZ})));
        stateListAnimator.addState(EMPTY_STATE_SET, setupAnimator(ObjectAnimator.ofFloat(this.mView, "translationZ", new float[]{0.0f})));
        this.mView.setStateListAnimator(stateListAnimator);
    }

    /* access modifiers changed from: 0000 */
    public void onDrawableStateChanged(int[] state) {
    }

    /* access modifiers changed from: 0000 */
    public void jumpDrawableToCurrentState() {
    }

    private Animator setupAnimator(Animator animator) {
        animator.setInterpolator(this.mInterpolator);
        return animator;
    }

    /* access modifiers changed from: 0000 */
    public CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawableLollipop();
    }
}
