package android.support.design.widget;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.design.C0000R;
import android.view.View;

abstract class FloatingActionButtonImpl {
    static final int[] EMPTY_STATE_SET = new int[0];
    static final int[] FOCUSED_ENABLED_STATE_SET = {16842908, 16842910};
    static final int[] PRESSED_ENABLED_STATE_SET = {16842919, 16842910};
    static final int SHOW_HIDE_ANIM_DURATION = 200;
    final ShadowViewDelegate mShadowViewDelegate;
    final View mView;

    /* access modifiers changed from: 0000 */
    public abstract void hide();

    /* access modifiers changed from: 0000 */
    public abstract void jumpDrawableToCurrentState();

    /* access modifiers changed from: 0000 */
    public abstract void onDrawableStateChanged(int[] iArr);

    /* access modifiers changed from: 0000 */
    public abstract void setBackgroundDrawable(Drawable drawable, ColorStateList colorStateList, Mode mode, int i, int i2);

    /* access modifiers changed from: 0000 */
    public abstract void setBackgroundTintList(ColorStateList colorStateList);

    /* access modifiers changed from: 0000 */
    public abstract void setBackgroundTintMode(Mode mode);

    /* access modifiers changed from: 0000 */
    public abstract void setElevation(float f);

    /* access modifiers changed from: 0000 */
    public abstract void setPressedTranslationZ(float f);

    /* access modifiers changed from: 0000 */
    public abstract void setRippleColor(int i);

    /* access modifiers changed from: 0000 */
    public abstract void show();

    FloatingActionButtonImpl(View view, ShadowViewDelegate shadowViewDelegate) {
        this.mView = view;
        this.mShadowViewDelegate = shadowViewDelegate;
    }

    /* access modifiers changed from: 0000 */
    public Drawable createBorderDrawable(int borderWidth, ColorStateList backgroundTint) {
        Resources resources = this.mView.getResources();
        CircularBorderDrawable borderDrawable = newCircularDrawable();
        borderDrawable.setGradientColors(resources.getColor(C0000R.color.fab_stroke_top_outer_color), resources.getColor(C0000R.color.fab_stroke_top_inner_color), resources.getColor(C0000R.color.fab_stroke_end_inner_color), resources.getColor(C0000R.color.fab_stroke_end_outer_color));
        borderDrawable.setBorderWidth((float) borderWidth);
        borderDrawable.setTintColor(backgroundTint.getDefaultColor());
        return borderDrawable;
    }

    /* access modifiers changed from: 0000 */
    public CircularBorderDrawable newCircularDrawable() {
        return new CircularBorderDrawable();
    }
}
