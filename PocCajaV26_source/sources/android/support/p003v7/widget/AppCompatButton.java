package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.p000v4.view.TintableBackgroundView;
import android.support.p003v7.appcompat.C0190R;
import android.support.p003v7.internal.text.AllCapsTransformationMethod;
import android.support.p003v7.internal.widget.ThemeUtils;
import android.support.p003v7.internal.widget.TintInfo;
import android.support.p003v7.internal.widget.TintManager;
import android.support.p003v7.internal.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

/* renamed from: android.support.v7.widget.AppCompatButton */
public class AppCompatButton extends Button implements TintableBackgroundView {
    private static final int[] TINT_ATTRS = {16842964};
    private TintInfo mBackgroundTint;
    private TintInfo mInternalBackgroundTint;
    private TintManager mTintManager;

    public AppCompatButton(Context context) {
        this(context, null);
    }

    public AppCompatButton(Context context, AttributeSet attrs) {
        this(context, attrs, C0190R.attr.buttonStyle);
    }

    public AppCompatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        int disabledTextColor;
        super(context, attrs, defStyleAttr);
        if (TintManager.SHOULD_BE_USED) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, TINT_ATTRS, defStyleAttr, 0);
            if (a.hasValue(0)) {
                ColorStateList tint = a.getTintManager().getTintList(a.getResourceId(0, -1));
                if (tint != null) {
                    setInternalBackgroundTint(tint);
                }
            }
            this.mTintManager = a.getTintManager();
            a.recycle();
        }
        TypedArray a2 = context.obtainStyledAttributes(attrs, C0190R.styleable.AppCompatTextView, defStyleAttr, 0);
        int ap = a2.getResourceId(C0190R.styleable.AppCompatTextView_android_textAppearance, -1);
        a2.recycle();
        if (ap != -1) {
            TypedArray appearance = context.obtainStyledAttributes(ap, C0190R.styleable.TextAppearance);
            if (appearance.hasValue(C0190R.styleable.TextAppearance_textAllCaps)) {
                setAllCaps(appearance.getBoolean(C0190R.styleable.TextAppearance_textAllCaps, false));
            }
            appearance.recycle();
        }
        TypedArray a3 = context.obtainStyledAttributes(attrs, C0190R.styleable.AppCompatTextView, defStyleAttr, 0);
        if (a3.hasValue(C0190R.styleable.AppCompatTextView_textAllCaps)) {
            setAllCaps(a3.getBoolean(C0190R.styleable.AppCompatTextView_textAllCaps, false));
        }
        a3.recycle();
        ColorStateList textColors = getTextColors();
        if (textColors != null && !textColors.isStateful()) {
            if (VERSION.SDK_INT < 21) {
                disabledTextColor = ThemeUtils.getDisabledThemeAttrColor(context, 16842808);
            } else {
                disabledTextColor = ThemeUtils.getThemeAttrColor(context, 16842808);
            }
            setTextColor(ThemeUtils.createDisabledStateList(textColors.getDefaultColor(), disabledTextColor));
        }
    }

    public void setBackgroundResource(int resId) {
        super.setBackgroundResource(resId);
        setInternalBackgroundTint(this.mTintManager != null ? this.mTintManager.getTintList(resId) : null);
    }

    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        setInternalBackgroundTint(null);
    }

    public void setSupportBackgroundTintList(@Nullable ColorStateList tint) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintList = tint;
        this.mBackgroundTint.mHasTintList = true;
        applySupportBackgroundTint();
    }

    @Nullable
    public ColorStateList getSupportBackgroundTintList() {
        if (this.mBackgroundTint != null) {
            return this.mBackgroundTint.mTintList;
        }
        return null;
    }

    public void setSupportBackgroundTintMode(@Nullable Mode tintMode) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintMode = tintMode;
        this.mBackgroundTint.mHasTintMode = true;
        applySupportBackgroundTint();
    }

    @Nullable
    public Mode getSupportBackgroundTintMode() {
        if (this.mBackgroundTint != null) {
            return this.mBackgroundTint.mTintMode;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        applySupportBackgroundTint();
    }

    private void applySupportBackgroundTint() {
        if (getBackground() == null) {
            return;
        }
        if (this.mBackgroundTint != null) {
            TintManager.tintViewBackground(this, this.mBackgroundTint);
        } else if (this.mInternalBackgroundTint != null) {
            TintManager.tintViewBackground(this, this.mInternalBackgroundTint);
        }
    }

    private void setInternalBackgroundTint(ColorStateList tint) {
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

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(Button.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Button.class.getName());
    }

    public void setAllCaps(boolean allCaps) {
        setTransformationMethod(allCaps ? new AllCapsTransformationMethod(getContext()) : null);
    }

    public void setTextAppearance(Context context, int resId) {
        super.setTextAppearance(context, resId);
        TypedArray appearance = context.obtainStyledAttributes(resId, C0190R.styleable.TextAppearance);
        if (appearance.hasValue(C0190R.styleable.TextAppearance_textAllCaps)) {
            setAllCaps(appearance.getBoolean(C0190R.styleable.TextAppearance_textAllCaps, false));
        }
        appearance.recycle();
    }
}
