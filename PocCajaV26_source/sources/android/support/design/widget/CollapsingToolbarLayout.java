package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.C0000R;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.OnApplyWindowInsetsListener;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.WindowInsetsCompat;
import android.support.p003v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class CollapsingToolbarLayout extends FrameLayout {
    private static final int SCRIM_ANIMATION_DURATION = 600;
    /* access modifiers changed from: private */
    public final CollapsingTextHelper mCollapsingTextHelper;
    /* access modifiers changed from: private */
    public Drawable mContentScrim;
    /* access modifiers changed from: private */
    public int mCurrentOffset;
    private View mDummyView;
    private int mExpandedMarginBottom;
    private int mExpandedMarginLeft;
    private int mExpandedMarginRight;
    private int mExpandedMarginTop;
    /* access modifiers changed from: private */
    public WindowInsetsCompat mLastInsets;
    private OnOffsetChangedListener mOnOffsetChangedListener;
    private boolean mRefreshToolbar;
    private int mScrimAlpha;
    private ValueAnimatorCompat mScrimAnimator;
    private boolean mScrimsAreShown;
    /* access modifiers changed from: private */
    public Drawable mStatusBarScrim;
    private final Rect mTmpRect;
    private Toolbar mToolbar;
    private int mToolbarId;

    public static class LayoutParams extends android.widget.FrameLayout.LayoutParams {
        public static final int COLLAPSE_MODE_OFF = 0;
        public static final int COLLAPSE_MODE_PARALLAX = 2;
        public static final int COLLAPSE_MODE_PIN = 1;
        private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;
        int mCollapseMode = 0;
        float mParallaxMult = DEFAULT_PARALLAX_MULTIPLIER;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, C0000R.styleable.CollapsingAppBarLayout_LayoutParams);
            this.mCollapseMode = a.getInt(C0000R.styleable.CollapsingAppBarLayout_LayoutParams_layout_collapseMode, 0);
            setParallaxMultiplier(a.getFloat(C0000R.styleable.f1xad49a364, DEFAULT_PARALLAX_MULTIPLIER));
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(android.widget.FrameLayout.LayoutParams source) {
            super(source);
        }

        public void setCollapseMode(int collapseMode) {
            this.mCollapseMode = collapseMode;
        }

        public int getCollapseMode() {
            return this.mCollapseMode;
        }

        public void setParallaxMultiplier(float multiplier) {
            this.mParallaxMult = multiplier;
        }

        public float getParallaxMultiplier() {
            return this.mParallaxMult;
        }
    }

    private class OffsetUpdateListener implements OnOffsetChangedListener {
        private OffsetUpdateListener() {
        }

        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            CollapsingToolbarLayout.this.mCurrentOffset = verticalOffset;
            int insetTop = CollapsingToolbarLayout.this.mLastInsets != null ? CollapsingToolbarLayout.this.mLastInsets.getSystemWindowInsetTop() : 0;
            int scrollRange = layout.getTotalScrollRange();
            int z = CollapsingToolbarLayout.this.getChildCount();
            for (int i = 0; i < z; i++) {
                View child = CollapsingToolbarLayout.this.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                ViewOffsetHelper offsetHelper = CollapsingToolbarLayout.getViewOffsetHelper(child);
                switch (lp.mCollapseMode) {
                    case 1:
                        if ((CollapsingToolbarLayout.this.getHeight() - insetTop) + verticalOffset < child.getHeight()) {
                            break;
                        } else {
                            offsetHelper.setTopAndBottomOffset(-verticalOffset);
                            break;
                        }
                    case 2:
                        offsetHelper.setTopAndBottomOffset(Math.round(((float) (-verticalOffset)) * lp.mParallaxMult));
                        break;
                }
            }
            if (!(CollapsingToolbarLayout.this.mContentScrim == null && CollapsingToolbarLayout.this.mStatusBarScrim == null)) {
                if (CollapsingToolbarLayout.this.getHeight() + verticalOffset < CollapsingToolbarLayout.this.getScrimTriggerOffset() + insetTop) {
                    CollapsingToolbarLayout.this.showScrim();
                } else {
                    CollapsingToolbarLayout.this.hideScrim();
                }
            }
            if (CollapsingToolbarLayout.this.mStatusBarScrim != null && insetTop > 0) {
                ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
            }
            CollapsingToolbarLayout.this.mCollapsingTextHelper.setExpansionFraction(((float) Math.abs(verticalOffset)) / ((float) ((CollapsingToolbarLayout.this.getHeight() - ViewCompat.getMinimumHeight(CollapsingToolbarLayout.this)) - insetTop)));
            if (Math.abs(verticalOffset) == scrollRange) {
                ViewCompat.setElevation(layout, layout.getTargetElevation());
            } else {
                ViewCompat.setElevation(layout, 0.0f);
            }
        }
    }

    public CollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        boolean isRtl = true;
        super(context, attrs, defStyleAttr);
        this.mRefreshToolbar = true;
        this.mTmpRect = new Rect();
        this.mCollapsingTextHelper = new CollapsingTextHelper(this);
        this.mCollapsingTextHelper.setExpandedTextVerticalGravity(80);
        this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
        TypedArray a = context.obtainStyledAttributes(attrs, C0000R.styleable.CollapsingToolbarLayout, defStyleAttr, C0000R.style.Widget_Design_CollapsingToolbar);
        int dimensionPixelSize = a.getDimensionPixelSize(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMargin, 0);
        this.mExpandedMarginBottom = dimensionPixelSize;
        this.mExpandedMarginRight = dimensionPixelSize;
        this.mExpandedMarginTop = dimensionPixelSize;
        this.mExpandedMarginLeft = dimensionPixelSize;
        if (ViewCompat.getLayoutDirection(this) != 1) {
            isRtl = false;
        }
        if (a.hasValue(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart)) {
            int marginStart = a.getDimensionPixelSize(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0);
            if (isRtl) {
                this.mExpandedMarginRight = marginStart;
            } else {
                this.mExpandedMarginLeft = marginStart;
            }
        }
        if (a.hasValue(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd)) {
            int marginEnd = a.getDimensionPixelSize(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0);
            if (isRtl) {
                this.mExpandedMarginLeft = marginEnd;
            } else {
                this.mExpandedMarginRight = marginEnd;
            }
        }
        if (a.hasValue(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop)) {
            this.mExpandedMarginTop = a.getDimensionPixelSize(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0);
        }
        if (a.hasValue(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom)) {
            this.mExpandedMarginBottom = a.getDimensionPixelSize(C0000R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0);
        }
        this.mCollapsingTextHelper.setExpandedTextAppearance(a.getResourceId(C0000R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance, C0000R.style.TextAppearance_AppCompat_Title));
        this.mCollapsingTextHelper.setCollapsedTextAppearance(a.getResourceId(C0000R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance, C0000R.style.TextAppearance_AppCompat_Widget_ActionBar_Title));
        setContentScrim(a.getDrawable(C0000R.styleable.CollapsingToolbarLayout_contentScrim));
        setStatusBarScrim(a.getDrawable(C0000R.styleable.CollapsingToolbarLayout_statusBarScrim));
        this.mToolbarId = a.getResourceId(C0000R.styleable.CollapsingToolbarLayout_toolbarId, -1);
        a.recycle();
        setWillNotDraw(false);
        ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() {
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                CollapsingToolbarLayout.this.mLastInsets = insets;
                CollapsingToolbarLayout.this.requestLayout();
                return insets.consumeSystemWindowInsets();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            if (this.mOnOffsetChangedListener == null) {
                this.mOnOffsetChangedListener = new OffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(this.mOnOffsetChangedListener);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        ViewParent parent = getParent();
        if (this.mOnOffsetChangedListener != null && (parent instanceof AppBarLayout)) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(this.mOnOffsetChangedListener);
        }
        super.onDetachedFromWindow();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        ensureToolbar();
        if (this.mToolbar == null && this.mContentScrim != null && this.mScrimAlpha > 0) {
            this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mContentScrim.draw(canvas);
        }
        this.mCollapsingTextHelper.draw(canvas);
        if (this.mStatusBarScrim != null && this.mScrimAlpha > 0) {
            int topInset = this.mLastInsets != null ? this.mLastInsets.getSystemWindowInsetTop() : 0;
            if (topInset > 0) {
                this.mStatusBarScrim.setBounds(0, -this.mCurrentOffset, getWidth(), topInset - this.mCurrentOffset);
                this.mStatusBarScrim.mutate().setAlpha(this.mScrimAlpha);
                this.mStatusBarScrim.draw(canvas);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        ensureToolbar();
        if (child == this.mToolbar && this.mContentScrim != null && this.mScrimAlpha > 0) {
            this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
            this.mContentScrim.draw(canvas);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mContentScrim != null) {
            this.mContentScrim.setBounds(0, 0, w, h);
        }
    }

    private void ensureToolbar() {
        if (this.mRefreshToolbar) {
            Toolbar fallback = null;
            Toolbar selected = null;
            int i = 0;
            int count = getChildCount();
            while (true) {
                if (i >= count) {
                    break;
                }
                View child = getChildAt(i);
                if (child instanceof Toolbar) {
                    if (this.mToolbarId == -1) {
                        selected = (Toolbar) child;
                        break;
                    } else if (this.mToolbarId == child.getId()) {
                        selected = (Toolbar) child;
                        break;
                    } else if (fallback == null) {
                        fallback = (Toolbar) child;
                    }
                }
                i++;
            }
            if (selected == null) {
                selected = fallback;
            }
            if (selected != null) {
                this.mToolbar = selected;
                this.mDummyView = new View(getContext());
                this.mToolbar.addView(this.mDummyView, -1, -1);
            } else {
                this.mToolbar = null;
                this.mDummyView = null;
            }
            this.mRefreshToolbar = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureToolbar();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int z = getChildCount();
        for (int i = 0; i < z; i++) {
            View child = getChildAt(i);
            if (this.mLastInsets != null && !ViewCompat.getFitsSystemWindows(child)) {
                int insetTop = this.mLastInsets.getSystemWindowInsetTop();
                if (child.getTop() < insetTop) {
                    child.offsetTopAndBottom(insetTop);
                }
            }
            getViewOffsetHelper(child).onViewLayout();
        }
        if (this.mDummyView != null) {
            ViewGroupUtils.getDescendantRect(this, this.mDummyView, this.mTmpRect);
            this.mCollapsingTextHelper.setCollapsedBounds(this.mTmpRect.left, bottom - this.mTmpRect.height(), this.mTmpRect.right, bottom);
            this.mCollapsingTextHelper.setExpandedBounds(this.mExpandedMarginLeft + left, this.mTmpRect.bottom + this.mExpandedMarginTop, right - this.mExpandedMarginRight, bottom - this.mExpandedMarginBottom);
            this.mCollapsingTextHelper.recalculate();
        }
        if (this.mToolbar != null) {
            setMinimumHeight(this.mToolbar.getHeight());
        }
    }

    /* access modifiers changed from: private */
    public static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(C0000R.C0002id.view_offset_helper);
        if (offsetHelper != null) {
            return offsetHelper;
        }
        ViewOffsetHelper offsetHelper2 = new ViewOffsetHelper(view);
        view.setTag(C0000R.C0002id.view_offset_helper, offsetHelper2);
        return offsetHelper2;
    }

    public void setTitle(CharSequence title) {
        this.mCollapsingTextHelper.setText(title);
    }

    /* access modifiers changed from: private */
    public void showScrim() {
        if (!this.mScrimsAreShown) {
            if (!ViewCompat.isLaidOut(this) || isInEditMode()) {
                setScrimAlpha(255);
            } else {
                animateScrim(255);
            }
            this.mScrimsAreShown = true;
        }
    }

    /* access modifiers changed from: private */
    public void hideScrim() {
        if (this.mScrimsAreShown) {
            if (!ViewCompat.isLaidOut(this) || isInEditMode()) {
                setScrimAlpha(0);
            } else {
                animateScrim(0);
            }
            this.mScrimsAreShown = false;
        }
    }

    private void animateScrim(int targetAlpha) {
        ensureToolbar();
        if (this.mScrimAnimator == null) {
            this.mScrimAnimator = ViewUtils.createAnimator();
            this.mScrimAnimator.setDuration(SCRIM_ANIMATION_DURATION);
            this.mScrimAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            this.mScrimAnimator.setUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimatorCompat animator) {
                    CollapsingToolbarLayout.this.setScrimAlpha(animator.getAnimatedIntValue());
                }
            });
        } else if (this.mScrimAnimator.isRunning()) {
            this.mScrimAnimator.cancel();
        }
        this.mScrimAnimator.setIntValues(this.mScrimAlpha, targetAlpha);
        this.mScrimAnimator.start();
    }

    /* access modifiers changed from: private */
    public void setScrimAlpha(int alpha) {
        if (alpha != this.mScrimAlpha) {
            if (!(this.mContentScrim == null || this.mToolbar == null)) {
                ViewCompat.postInvalidateOnAnimation(this.mToolbar);
            }
            this.mScrimAlpha = alpha;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setContentScrim(@Nullable Drawable drawable) {
        if (this.mContentScrim != drawable) {
            if (this.mContentScrim != null) {
                this.mContentScrim.setCallback(null);
            }
            this.mContentScrim = drawable;
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.setCallback(this);
            drawable.mutate().setAlpha(this.mScrimAlpha);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setContentScrimColor(int color) {
        setContentScrim(new ColorDrawable(color));
    }

    public void setContentScrimResource(@DrawableRes int resId) {
        setContentScrim(ContextCompat.getDrawable(getContext(), resId));
    }

    public Drawable getContentScrim() {
        return this.mContentScrim;
    }

    public void setStatusBarScrim(@Nullable Drawable drawable) {
        if (this.mStatusBarScrim != drawable) {
            if (this.mStatusBarScrim != null) {
                this.mStatusBarScrim.setCallback(null);
            }
            this.mStatusBarScrim = drawable;
            drawable.setCallback(this);
            drawable.mutate().setAlpha(this.mScrimAlpha);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setStatusBarScrimColor(int color) {
        setStatusBarScrim(new ColorDrawable(color));
    }

    public void setStatusBarScrimResource(@DrawableRes int resId) {
        setStatusBarScrim(ContextCompat.getDrawable(getContext(), resId));
    }

    public Drawable getStatusBarScrim() {
        return this.mStatusBarScrim;
    }

    public void setCollapsedTitleTextAppearance(int resId) {
        this.mCollapsingTextHelper.setCollapsedTextAppearance(resId);
    }

    public void setCollapsedTitleTextColor(int color) {
        this.mCollapsingTextHelper.setCollapsedTextColor(color);
    }

    public void setExpandedTitleTextAppearance(int resId) {
        this.mCollapsingTextHelper.setExpandedTextAppearance(resId);
    }

    public void setExpandedTitleColor(int color) {
        this.mCollapsingTextHelper.setExpandedTextColor(color);
    }

    /* access modifiers changed from: 0000 */
    public final int getScrimTriggerOffset() {
        return ViewCompat.getMinimumHeight(this) * 2;
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    public android.widget.FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public android.widget.FrameLayout.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }
}
