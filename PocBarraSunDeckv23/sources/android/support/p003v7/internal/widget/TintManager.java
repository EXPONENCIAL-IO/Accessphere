package android.support.p003v7.internal.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.util.LruCache;
import android.support.p003v7.appcompat.C0140R;
import android.util.TypedValue;

/* renamed from: android.support.v7.internal.widget.TintManager */
public class TintManager {
    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);
    private static final int[] CONTAINERS_WITH_TINT_CHILDREN = {C0140R.C0141drawable.abc_cab_background_top_material};
    private static final boolean DEBUG = false;
    static final Mode DEFAULT_MODE = Mode.SRC_IN;
    private static final String TAG = TintManager.class.getSimpleName();
    private static final int[] TINT_COLOR_BACKGROUND_MULTIPLY = {C0140R.C0141drawable.abc_popup_background_mtrl_mult, C0140R.C0141drawable.abc_cab_background_internal_bg, C0140R.C0141drawable.abc_menu_hardkey_panel_mtrl_mult};
    private static final int[] TINT_COLOR_CONTROL_ACTIVATED = {C0140R.C0141drawable.abc_textfield_activated_mtrl_alpha, C0140R.C0141drawable.abc_textfield_search_activated_mtrl_alpha, C0140R.C0141drawable.abc_cab_background_top_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_NORMAL = {C0140R.C0141drawable.abc_ic_ab_back_mtrl_am_alpha, C0140R.C0141drawable.abc_ic_go_search_api_mtrl_alpha, C0140R.C0141drawable.abc_ic_search_api_mtrl_alpha, C0140R.C0141drawable.abc_ic_commit_search_api_mtrl_alpha, C0140R.C0141drawable.abc_ic_clear_mtrl_alpha, C0140R.C0141drawable.abc_ic_menu_share_mtrl_alpha, C0140R.C0141drawable.abc_ic_menu_copy_mtrl_am_alpha, C0140R.C0141drawable.abc_ic_menu_cut_mtrl_alpha, C0140R.C0141drawable.abc_ic_menu_selectall_mtrl_alpha, C0140R.C0141drawable.abc_ic_menu_paste_mtrl_am_alpha, C0140R.C0141drawable.abc_ic_menu_moreoverflow_mtrl_alpha, C0140R.C0141drawable.abc_ic_voice_search_api_mtrl_alpha, C0140R.C0141drawable.abc_textfield_search_default_mtrl_alpha, C0140R.C0141drawable.abc_textfield_default_mtrl_alpha, C0140R.C0141drawable.abc_ab_share_pack_mtrl_alpha};
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST = {C0140R.C0141drawable.abc_edit_text_material, C0140R.C0141drawable.abc_tab_indicator_material, C0140R.C0141drawable.abc_textfield_search_material, C0140R.C0141drawable.abc_spinner_mtrl_am_alpha, C0140R.C0141drawable.abc_btn_check_material, C0140R.C0141drawable.abc_btn_radio_material, C0140R.C0141drawable.abc_spinner_textfield_background_material, C0140R.C0141drawable.abc_ratingbar_full_material};
    private ColorStateList mButtonStateList;
    private final Context mContext;
    private ColorStateList mDefaultColorStateList;
    private final Resources mResources;
    private ColorStateList mSwitchThumbStateList;
    private ColorStateList mSwitchTrackStateList;
    private final TypedValue mTypedValue = new TypedValue();

    /* renamed from: android.support.v7.internal.widget.TintManager$ColorFilterLruCache */
    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
        public ColorFilterLruCache(int maxSize) {
            super(maxSize);
        }

        /* access modifiers changed from: 0000 */
        public PorterDuffColorFilter get(int color, Mode mode) {
            return (PorterDuffColorFilter) get(Integer.valueOf(generateCacheKey(color, mode)));
        }

        /* access modifiers changed from: 0000 */
        public PorterDuffColorFilter put(int color, Mode mode, PorterDuffColorFilter filter) {
            return (PorterDuffColorFilter) put(Integer.valueOf(generateCacheKey(color, mode)), filter);
        }

        private static int generateCacheKey(int color, Mode mode) {
            return ((color + 31) * 31) + mode.hashCode();
        }
    }

    public static Drawable getDrawable(Context context, int resId) {
        if (isInTintList(resId)) {
            return new TintManager(context).getDrawable(resId);
        }
        return ContextCompat.getDrawable(context, resId);
    }

    public TintManager(Context context) {
        this.mContext = context;
        this.mResources = new TintResources(context.getResources(), this);
    }

    public Drawable getDrawable(int resId) {
        Drawable drawable = ContextCompat.getDrawable(this.mContext, resId);
        if (drawable == null) {
            return drawable;
        }
        Drawable drawable2 = drawable.mutate();
        if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, resId)) {
            return new TintDrawableWrapper(drawable2, getDefaultColorStateList());
        }
        if (resId == C0140R.C0141drawable.abc_switch_track_mtrl_alpha) {
            return new TintDrawableWrapper(drawable2, getSwitchTrackColorStateList());
        }
        if (resId == C0140R.C0141drawable.abc_switch_thumb_material) {
            return new TintDrawableWrapper(drawable2, getSwitchThumbColorStateList(), Mode.MULTIPLY);
        }
        if (resId == C0140R.C0141drawable.abc_btn_default_mtrl_shape) {
            return new TintDrawableWrapper(drawable2, getButtonColorStateList());
        }
        if (arrayContains(CONTAINERS_WITH_TINT_CHILDREN, resId)) {
            return this.mResources.getDrawable(resId);
        }
        tintDrawable(resId, drawable2);
        return drawable2;
    }

    /* access modifiers changed from: 0000 */
    public void tintDrawable(int resId, Drawable drawable) {
        Mode tintMode = null;
        boolean colorAttrSet = false;
        int colorAttr = 0;
        int alpha = -1;
        if (arrayContains(TINT_COLOR_CONTROL_NORMAL, resId)) {
            colorAttr = C0140R.attr.colorControlNormal;
            colorAttrSet = true;
        } else if (arrayContains(TINT_COLOR_CONTROL_ACTIVATED, resId)) {
            colorAttr = C0140R.attr.colorControlActivated;
            colorAttrSet = true;
        } else if (arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, resId)) {
            colorAttr = 16842801;
            colorAttrSet = true;
            tintMode = Mode.MULTIPLY;
        } else if (resId == C0140R.C0141drawable.abc_list_divider_mtrl_alpha) {
            colorAttr = 16842800;
            colorAttrSet = true;
            alpha = Math.round(40.8f);
        }
        if (colorAttrSet) {
            if (tintMode == null) {
                tintMode = DEFAULT_MODE;
            }
            int color = getThemeAttrColor(colorAttr);
            PorterDuffColorFilter filter = COLOR_FILTER_CACHE.get(color, tintMode);
            if (filter == null) {
                filter = new PorterDuffColorFilter(color, tintMode);
                COLOR_FILTER_CACHE.put(color, tintMode, filter);
            }
            drawable.setColorFilter(filter);
            if (alpha != -1) {
                drawable.setAlpha(alpha);
            }
        }
    }

    private static boolean arrayContains(int[] array, int value) {
        for (int id : array) {
            if (id == value) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInTintList(int drawableId) {
        return arrayContains(TINT_COLOR_BACKGROUND_MULTIPLY, drawableId) || arrayContains(TINT_COLOR_CONTROL_NORMAL, drawableId) || arrayContains(TINT_COLOR_CONTROL_ACTIVATED, drawableId) || arrayContains(TINT_COLOR_CONTROL_STATE_LIST, drawableId) || arrayContains(CONTAINERS_WITH_TINT_CHILDREN, drawableId);
    }

    private ColorStateList getDefaultColorStateList() {
        if (this.mDefaultColorStateList == null) {
            int colorControlNormal = getThemeAttrColor(C0140R.attr.colorControlNormal);
            int colorControlActivated = getThemeAttrColor(C0140R.attr.colorControlActivated);
            int[][] states = new int[7][];
            int[] colors = new int[7];
            states[0] = new int[]{-16842910};
            colors[0] = getDisabledThemeAttrColor(C0140R.attr.colorControlNormal);
            int i = 0 + 1;
            states[i] = new int[]{16842908};
            colors[i] = colorControlActivated;
            int i2 = i + 1;
            states[i2] = new int[]{16843518};
            colors[i2] = colorControlActivated;
            int i3 = i2 + 1;
            states[i3] = new int[]{16842919};
            colors[i3] = colorControlActivated;
            int i4 = i3 + 1;
            states[i4] = new int[]{16842912};
            colors[i4] = colorControlActivated;
            int i5 = i4 + 1;
            states[i5] = new int[]{16842913};
            colors[i5] = colorControlActivated;
            int i6 = i5 + 1;
            states[i6] = new int[0];
            colors[i6] = colorControlNormal;
            int i7 = i6 + 1;
            this.mDefaultColorStateList = new ColorStateList(states, colors);
        }
        return this.mDefaultColorStateList;
    }

    private ColorStateList getSwitchTrackColorStateList() {
        if (this.mSwitchTrackStateList == null) {
            int[][] states = new int[3][];
            int[] colors = new int[3];
            states[0] = new int[]{-16842910};
            colors[0] = getThemeAttrColor(16842800, 0.1f);
            int i = 0 + 1;
            states[i] = new int[]{16842912};
            colors[i] = getThemeAttrColor(C0140R.attr.colorControlActivated, 0.3f);
            int i2 = i + 1;
            states[i2] = new int[0];
            colors[i2] = getThemeAttrColor(16842800, 0.3f);
            int i3 = i2 + 1;
            this.mSwitchTrackStateList = new ColorStateList(states, colors);
        }
        return this.mSwitchTrackStateList;
    }

    private ColorStateList getSwitchThumbColorStateList() {
        if (this.mSwitchThumbStateList == null) {
            int[][] states = new int[3][];
            int[] colors = new int[3];
            states[0] = new int[]{-16842910};
            colors[0] = getDisabledThemeAttrColor(C0140R.attr.colorSwitchThumbNormal);
            int i = 0 + 1;
            states[i] = new int[]{16842912};
            colors[i] = getThemeAttrColor(C0140R.attr.colorControlActivated);
            int i2 = i + 1;
            states[i2] = new int[0];
            colors[i2] = getThemeAttrColor(C0140R.attr.colorSwitchThumbNormal);
            int i3 = i2 + 1;
            this.mSwitchThumbStateList = new ColorStateList(states, colors);
        }
        return this.mSwitchThumbStateList;
    }

    private ColorStateList getButtonColorStateList() {
        if (this.mButtonStateList == null) {
            int[][] states = new int[4][];
            int[] colors = new int[4];
            states[0] = new int[]{-16842910};
            colors[0] = getDisabledThemeAttrColor(C0140R.attr.colorButtonNormal);
            int i = 0 + 1;
            states[i] = new int[]{16842919};
            colors[i] = getThemeAttrColor(C0140R.attr.colorControlHighlight);
            int i2 = i + 1;
            states[i2] = new int[]{16842908};
            colors[i2] = getThemeAttrColor(C0140R.attr.colorControlHighlight);
            int i3 = i2 + 1;
            states[i3] = new int[0];
            colors[i3] = getThemeAttrColor(C0140R.attr.colorButtonNormal);
            int i4 = i3 + 1;
            this.mButtonStateList = new ColorStateList(states, colors);
        }
        return this.mButtonStateList;
    }

    /* access modifiers changed from: 0000 */
    public int getThemeAttrColor(int attr) {
        if (this.mContext.getTheme().resolveAttribute(attr, this.mTypedValue, true)) {
            if (this.mTypedValue.type >= 16 && this.mTypedValue.type <= 31) {
                return this.mTypedValue.data;
            }
            if (this.mTypedValue.type == 3) {
                return this.mResources.getColor(this.mTypedValue.resourceId);
            }
        }
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public int getThemeAttrColor(int attr, float alpha) {
        int color = getThemeAttrColor(attr);
        return (16777215 & color) | (Math.round(((float) Color.alpha(color)) * alpha) << 24);
    }

    /* access modifiers changed from: 0000 */
    public int getDisabledThemeAttrColor(int attr) {
        this.mContext.getTheme().resolveAttribute(16842803, this.mTypedValue, true);
        return getThemeAttrColor(attr, this.mTypedValue.getFloat());
    }
}
