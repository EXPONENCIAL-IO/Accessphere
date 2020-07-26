package android.support.p003v7.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import android.view.Window;

/* renamed from: android.support.v7.app.AppCompatDelegateImplV11 */
class AppCompatDelegateImplV11 extends AppCompatDelegateImplV7 {
    AppCompatDelegateImplV11(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    /* access modifiers changed from: 0000 */
    public View callActivityOnCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.callActivityOnCreateView(parent, name, context, attrs);
        if (view != null) {
            return view;
        }
        if (this.mOriginalWindowCallback instanceof Factory2) {
            return ((Factory2) this.mOriginalWindowCallback).onCreateView(parent, name, context, attrs);
        }
        return null;
    }
}
