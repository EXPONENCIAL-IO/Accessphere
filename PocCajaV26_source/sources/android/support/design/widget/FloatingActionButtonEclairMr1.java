package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.design.C0000R;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

class FloatingActionButtonEclairMr1 extends FloatingActionButtonImpl {
    private int mAnimationDuration;
    private Drawable mBorderDrawable;
    /* access modifiers changed from: private */
    public float mElevation;
    /* access modifiers changed from: private */
    public boolean mIsHiding;
    /* access modifiers changed from: private */
    public float mPressedTranslationZ;
    private Drawable mRippleDrawable;
    ShadowDrawableWrapper mShadowDrawable;
    private Drawable mShapeDrawable;
    private StateListAnimator mStateListAnimator = new StateListAnimator();

    private abstract class BaseShadowAnimation extends Animation {
        private float mShadowSizeDiff;
        private float mShadowSizeStart;

        /* access modifiers changed from: protected */
        public abstract float getTargetShadowSize();

        private BaseShadowAnimation() {
        }

        public void reset() {
            super.reset();
            this.mShadowSizeStart = FloatingActionButtonEclairMr1.this.mShadowDrawable.getShadowSize();
            this.mShadowSizeDiff = getTargetShadowSize() - this.mShadowSizeStart;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            FloatingActionButtonEclairMr1.this.mShadowDrawable.setShadowSize(this.mShadowSizeStart + (this.mShadowSizeDiff * interpolatedTime));
        }
    }

    private class ElevateToTranslationZAnimation extends BaseShadowAnimation {
        private ElevateToTranslationZAnimation() {
            super();
        }

        /* access modifiers changed from: protected */
        public float getTargetShadowSize() {
            return FloatingActionButtonEclairMr1.this.mElevation + FloatingActionButtonEclairMr1.this.mPressedTranslationZ;
        }
    }

    private class ResetElevationAnimation extends BaseShadowAnimation {
        private ResetElevationAnimation() {
            super();
        }

        /* access modifiers changed from: protected */
        public float getTargetShadowSize() {
            return FloatingActionButtonEclairMr1.this.mElevation;
        }
    }

    FloatingActionButtonEclairMr1(View view, ShadowViewDelegate shadowViewDelegate) {
        super(view, shadowViewDelegate);
        this.mAnimationDuration = view.getResources().getInteger(17694720);
        this.mStateListAnimator.setTarget(view);
        this.mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, setupAnimation(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, setupAnimation(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(EMPTY_STATE_SET, setupAnimation(new ResetElevationAnimation()));
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundDrawable(Drawable originalBackground, ColorStateList backgroundTint, Mode backgroundTintMode, int rippleColor, int borderWidth) {
        Drawable[] layers;
        this.mShapeDrawable = DrawableCompat.wrap(originalBackground);
        DrawableCompat.setTintList(this.mShapeDrawable, backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, backgroundTintMode);
        }
        GradientDrawable touchFeedbackShape = new GradientDrawable();
        touchFeedbackShape.setShape(1);
        touchFeedbackShape.setColor(-1);
        touchFeedbackShape.setCornerRadius(this.mShadowViewDelegate.getRadius());
        this.mRippleDrawable = DrawableCompat.wrap(touchFeedbackShape);
        DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(rippleColor));
        DrawableCompat.setTintMode(this.mRippleDrawable, Mode.MULTIPLY);
        if (borderWidth > 0) {
            this.mBorderDrawable = createBorderDrawable(borderWidth, backgroundTint);
            layers = new Drawable[]{this.mBorderDrawable, this.mShapeDrawable, this.mRippleDrawable};
        } else {
            this.mBorderDrawable = null;
            layers = new Drawable[]{this.mShapeDrawable, this.mRippleDrawable};
        }
        this.mShadowDrawable = new ShadowDrawableWrapper(this.mView.getResources(), new LayerDrawable(layers), this.mShadowViewDelegate.getRadius(), this.mElevation, this.mElevation + this.mPressedTranslationZ);
        this.mShadowDrawable.setAddPaddingForCorners(false);
        this.mShadowViewDelegate.setBackgroundDrawable(this.mShadowDrawable);
        updatePadding();
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
        DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(rippleColor));
    }

    /* access modifiers changed from: 0000 */
    public void setElevation(float elevation) {
        if (this.mElevation != elevation && this.mShadowDrawable != null) {
            this.mShadowDrawable.setShadowSize(elevation, this.mPressedTranslationZ + elevation);
            this.mElevation = elevation;
            updatePadding();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setPressedTranslationZ(float translationZ) {
        if (this.mPressedTranslationZ != translationZ && this.mShadowDrawable != null) {
            this.mPressedTranslationZ = translationZ;
            this.mShadowDrawable.setMaxShadowSize(this.mElevation + translationZ);
            updatePadding();
        }
    }

    /* access modifiers changed from: 0000 */
    public void onDrawableStateChanged(int[] state) {
        this.mStateListAnimator.setState(state);
    }

    /* access modifiers changed from: 0000 */
    public void jumpDrawableToCurrentState() {
        this.mStateListAnimator.jumpToCurrentState();
    }

    /* access modifiers changed from: 0000 */
    public void hide() {
        if (!this.mIsHiding) {
            Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), C0000R.anim.fab_out);
            anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            anim.setDuration(200);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                public void onAnimationStart(Animation animation) {
                    FloatingActionButtonEclairMr1.this.mIsHiding = true;
                }

                public void onAnimationEnd(Animation animation) {
                    FloatingActionButtonEclairMr1.this.mIsHiding = false;
                    FloatingActionButtonEclairMr1.this.mView.setVisibility(8);
                }
            });
            this.mView.startAnimation(anim);
        }
    }

    /* access modifiers changed from: 0000 */
    public void show() {
        Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), C0000R.anim.fab_in);
        anim.setDuration(200);
        anim.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        this.mView.startAnimation(anim);
    }

    private void updatePadding() {
        Rect rect = new Rect();
        this.mShadowDrawable.getPadding(rect);
        this.mShadowViewDelegate.setShadowPadding(rect.left, rect.top, rect.right, rect.bottom);
    }

    private Animation setupAnimation(Animation animation) {
        animation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        animation.setDuration((long) this.mAnimationDuration);
        return animation;
    }

    private static ColorStateList createColorStateList(int selectedColor) {
        int[][] states = new int[3][];
        int[] colors = new int[3];
        states[0] = FOCUSED_ENABLED_STATE_SET;
        colors[0] = selectedColor;
        int i = 0 + 1;
        states[i] = PRESSED_ENABLED_STATE_SET;
        colors[i] = selectedColor;
        int i2 = i + 1;
        states[i2] = new int[0];
        colors[i2] = 0;
        int i3 = i2 + 1;
        return new ColorStateList(states, colors);
    }
}
