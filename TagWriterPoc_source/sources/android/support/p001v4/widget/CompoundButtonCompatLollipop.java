package android.support.p001v4.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.widget.CompoundButton;

/* renamed from: android.support.v4.widget.CompoundButtonCompatLollipop */
class CompoundButtonCompatLollipop {
    CompoundButtonCompatLollipop() {
    }

    static void setButtonTintList(CompoundButton button, ColorStateList tint) {
        button.setButtonTintList(tint);
    }

    static ColorStateList getButtonTintList(CompoundButton button) {
        return button.getButtonTintList();
    }

    static void setButtonTintMode(CompoundButton button, Mode tintMode) {
        button.setButtonTintMode(tintMode);
    }

    static Mode getButtonTintMode(CompoundButton button) {
        return button.getButtonTintMode();
    }
}
