package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.design.C0000R;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p000v4.widget.TextViewCompat;
import android.support.p003v7.internal.view.menu.MenuItemImpl;
import android.support.p003v7.internal.view.menu.MenuView.ItemView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class NavigationMenuItemView extends TextView implements ItemView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private int mIconSize;
    private ColorStateList mIconTintList;
    private MenuItemImpl mItemData;

    public NavigationMenuItemView(Context context) {
        this(context, null);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationMenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIconSize = context.getResources().getDimensionPixelSize(C0000R.dimen.navigation_icon_size);
    }

    public void initialize(MenuItemImpl itemData, int menuType) {
        this.mItemData = itemData;
        setVisibility(itemData.isVisible() ? 0 : 8);
        if (getBackground() == null) {
            setBackgroundDrawable(createDefaultBackground());
        }
        setCheckable(itemData.isCheckable());
        setChecked(itemData.isChecked());
        setEnabled(itemData.isEnabled());
        setTitle(itemData.getTitle());
        setIcon(itemData.getIcon());
    }

    private StateListDrawable createDefaultBackground() {
        TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(C0000R.attr.colorControlHighlight, value, true)) {
            return null;
        }
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(CHECKED_STATE_SET, new ColorDrawable(value.data));
        drawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
        return drawable;
    }

    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public void setTitle(CharSequence title) {
        setText(title);
    }

    public void setCheckable(boolean checkable) {
        refreshDrawableState();
    }

    public void setChecked(boolean checked) {
        refreshDrawableState();
    }

    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            icon = DrawableCompat.wrap(icon.getConstantState().newDrawable()).mutate();
            icon.setBounds(0, 0, this.mIconSize, this.mIconSize);
            DrawableCompat.setTintList(icon, this.mIconTintList);
        }
        TextViewCompat.setCompoundDrawablesRelative(this, icon, null, null, null);
    }

    public boolean prefersCondensedTitle() {
        return false;
    }

    public boolean showsIcon() {
        return true;
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.mItemData != null && this.mItemData.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /* access modifiers changed from: 0000 */
    public void setIconTintList(ColorStateList tintList) {
        this.mIconTintList = tintList;
        if (this.mItemData != null) {
            setIcon(this.mItemData.getIcon());
        }
    }
}
