package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.design.C0000R;
import android.support.design.widget.CoordinatorLayout.DefaultBehavior;
import android.support.design.widget.CoordinatorLayout.LayoutParams;
import android.support.design.widget.Snackbar.SnackbarLayout;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import java.util.List;

@DefaultBehavior(Behavior.class)
public class FloatingActionButton extends ImageView {
    private static final int SIZE_MINI = 1;
    private static final int SIZE_NORMAL = 0;
    private ColorStateList mBackgroundTint;
    private Mode mBackgroundTintMode;
    private int mBorderWidth;
    /* access modifiers changed from: private */
    public int mContentPadding;
    private final FloatingActionButtonImpl mImpl;
    private int mRippleColor;
    /* access modifiers changed from: private */
    public final Rect mShadowPadding;
    private int mSize;

    public static class Behavior extends android.support.design.widget.CoordinatorLayout.Behavior<FloatingActionButton> {
        private static final boolean SNACKBAR_BEHAVIOR_ENABLED = (VERSION.SDK_INT >= 11);
        private Rect mTmpRect;
        private float mTranslationY;

        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            return SNACKBAR_BEHAVIOR_ENABLED && (dependency instanceof SnackbarLayout);
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            if (dependency instanceof SnackbarLayout) {
                updateFabTranslationForSnackbar(parent, child, dependency);
            } else if (dependency instanceof AppBarLayout) {
                updateFabVisibility(parent, (AppBarLayout) dependency, child);
            }
            return false;
        }

        public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            if (dependency instanceof SnackbarLayout) {
                ViewCompat.animate(child).translationY(0.0f).setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR).setListener(null);
            }
        }

        private boolean updateFabVisibility(CoordinatorLayout parent, AppBarLayout appBarLayout, FloatingActionButton child) {
            if (((LayoutParams) child.getLayoutParams()).getAnchorId() != appBarLayout.getId()) {
                return false;
            }
            if (this.mTmpRect == null) {
                this.mTmpRect = new Rect();
            }
            Rect rect = this.mTmpRect;
            ViewGroupUtils.getDescendantRect(parent, appBarLayout, rect);
            if (rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
                child.hide();
            } else {
                child.show();
            }
            return true;
        }

        private void updateFabTranslationForSnackbar(CoordinatorLayout parent, FloatingActionButton fab, View snackbar) {
            if (fab.getVisibility() == 0) {
                float translationY = getFabTranslationYForSnackbar(parent, fab);
                if (translationY != this.mTranslationY) {
                    ViewCompat.animate(fab).cancel();
                    ViewCompat.setTranslationY(fab, translationY);
                    this.mTranslationY = translationY;
                }
            }
        }

        private float getFabTranslationYForSnackbar(CoordinatorLayout parent, FloatingActionButton fab) {
            float minOffset = 0.0f;
            List<View> dependencies = parent.getDependencies(fab);
            int z = dependencies.size();
            for (int i = 0; i < z; i++) {
                View view = (View) dependencies.get(i);
                if ((view instanceof SnackbarLayout) && parent.doViewsOverlap(fab, view)) {
                    minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - ((float) view.getHeight()));
                }
            }
            return minOffset;
        }

        public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
            List<View> dependencies = parent.getDependencies(child);
            int count = dependencies.size();
            for (int i = 0; i < count; i++) {
                View dependency = (View) dependencies.get(i);
                if ((dependency instanceof AppBarLayout) && updateFabVisibility(parent, (AppBarLayout) dependency, child)) {
                    break;
                }
            }
            parent.onLayoutChild(child, layoutDirection);
            offsetIfNeeded(parent, child);
            return true;
        }

        private void offsetIfNeeded(CoordinatorLayout parent, FloatingActionButton fab) {
            Rect padding = fab.mShadowPadding;
            if (padding != null && padding.centerX() > 0 && padding.centerY() > 0) {
                LayoutParams lp = (LayoutParams) fab.getLayoutParams();
                int offsetTB = 0;
                int offsetLR = 0;
                if (fab.getRight() >= parent.getWidth() - lp.rightMargin) {
                    offsetLR = padding.right;
                } else if (fab.getLeft() <= lp.leftMargin) {
                    offsetLR = -padding.left;
                }
                if (fab.getBottom() >= parent.getBottom() - lp.bottomMargin) {
                    offsetTB = padding.bottom;
                } else if (fab.getTop() <= lp.topMargin) {
                    offsetTB = -padding.top;
                }
                fab.offsetTopAndBottom(offsetTB);
                fab.offsetLeftAndRight(offsetLR);
            }
        }
    }

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mShadowPadding = new Rect();
        TypedArray a = context.obtainStyledAttributes(attrs, C0000R.styleable.FloatingActionButton, defStyleAttr, C0000R.style.Widget_Design_FloatingActionButton);
        Drawable background = a.getDrawable(C0000R.styleable.FloatingActionButton_android_background);
        this.mBackgroundTint = a.getColorStateList(C0000R.styleable.FloatingActionButton_backgroundTint);
        this.mBackgroundTintMode = parseTintMode(a.getInt(C0000R.styleable.FloatingActionButton_backgroundTintMode, -1), null);
        this.mRippleColor = a.getColor(C0000R.styleable.FloatingActionButton_rippleColor, 0);
        this.mSize = a.getInt(C0000R.styleable.FloatingActionButton_fabSize, 0);
        this.mBorderWidth = a.getDimensionPixelSize(C0000R.styleable.FloatingActionButton_borderWidth, 0);
        float elevation = a.getDimension(C0000R.styleable.FloatingActionButton_elevation, 0.0f);
        float pressedTranslationZ = a.getDimension(C0000R.styleable.FloatingActionButton_pressedTranslationZ, 0.0f);
        a.recycle();
        ShadowViewDelegate delegate = new ShadowViewDelegate() {
            public float getRadius() {
                return ((float) FloatingActionButton.this.getSizeDimension()) / 2.0f;
            }

            public void setShadowPadding(int left, int top, int right, int bottom) {
                FloatingActionButton.this.mShadowPadding.set(left, top, right, bottom);
                FloatingActionButton.this.setPadding(FloatingActionButton.this.mContentPadding + left, FloatingActionButton.this.mContentPadding + top, FloatingActionButton.this.mContentPadding + right, FloatingActionButton.this.mContentPadding + bottom);
            }

            public void setBackgroundDrawable(Drawable background) {
                FloatingActionButton.super.setBackgroundDrawable(background);
            }
        };
        int sdk = VERSION.SDK_INT;
        if (sdk >= 21) {
            this.mImpl = new FloatingActionButtonLollipop(this, delegate);
        } else if (sdk >= 12) {
            this.mImpl = new FloatingActionButtonHoneycombMr1(this, delegate);
        } else {
            this.mImpl = new FloatingActionButtonEclairMr1(this, delegate);
        }
        this.mContentPadding = (getSizeDimension() - ((int) getResources().getDimension(C0000R.dimen.fab_content_size))) / 2;
        this.mImpl.setBackgroundDrawable(background, this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
        this.mImpl.setElevation(elevation);
        this.mImpl.setPressedTranslationZ(pressedTranslationZ);
        setClickable(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferredSize = getSizeDimension();
        int d = Math.min(resolveAdjustedSize(preferredSize, widthMeasureSpec), resolveAdjustedSize(preferredSize, heightMeasureSpec));
        setMeasuredDimension(this.mShadowPadding.left + d + this.mShadowPadding.right, this.mShadowPadding.top + d + this.mShadowPadding.bottom);
    }

    public void setRippleColor(int color) {
        if (this.mRippleColor != color) {
            this.mRippleColor = color;
            this.mImpl.setRippleColor(color);
        }
    }

    @Nullable
    public ColorStateList getBackgroundTintList() {
        return this.mBackgroundTint;
    }

    public void setBackgroundTintList(@Nullable ColorStateList tint) {
        this.mImpl.setBackgroundTintList(tint);
    }

    @Nullable
    public Mode getBackgroundTintMode() {
        return this.mBackgroundTintMode;
    }

    public void setBackgroundTintMode(@Nullable Mode tintMode) {
        this.mImpl.setBackgroundTintMode(tintMode);
    }

    public void setBackgroundDrawable(Drawable background) {
        if (this.mImpl != null) {
            this.mImpl.setBackgroundDrawable(background, this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
        }
    }

    public void show() {
        if (getVisibility() != 0) {
            setVisibility(0);
            if (ViewCompat.isLaidOut(this)) {
                this.mImpl.show();
            }
        }
    }

    public void hide() {
        if (getVisibility() == 0) {
            if (!ViewCompat.isLaidOut(this) || isInEditMode()) {
                setVisibility(8);
            } else {
                this.mImpl.hide();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public final int getSizeDimension() {
        switch (this.mSize) {
            case 1:
                return getResources().getDimensionPixelSize(C0000R.dimen.fab_size_mini);
            default:
                return getResources().getDimensionPixelSize(C0000R.dimen.fab_size_normal);
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        this.mImpl.onDrawableStateChanged(getDrawableState());
    }

    @TargetApi(11)
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.mImpl.jumpDrawableToCurrentState();
    }

    private static int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                return Math.min(desiredSize, specSize);
            case 0:
                return desiredSize;
            case 1073741824:
                return specSize;
            default:
                return result;
        }
    }

    static Mode parseTintMode(int value, Mode defaultMode) {
        switch (value) {
            case 3:
                return Mode.SRC_OVER;
            case 5:
                return Mode.SRC_IN;
            case 9:
                return Mode.SRC_ATOP;
            case 14:
                return Mode.MULTIPLY;
            case 15:
                return Mode.SCREEN;
            default:
                return defaultMode;
        }
    }
}
