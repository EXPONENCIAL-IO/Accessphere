package android.support.design.widget;

import android.support.p001v4.view.OnApplyWindowInsetsListener;
import android.view.View;

interface CoordinatorLayoutInsetsHelper {
    void setupForWindowInsets(View view, OnApplyWindowInsetsListener onApplyWindowInsetsListener);
}
