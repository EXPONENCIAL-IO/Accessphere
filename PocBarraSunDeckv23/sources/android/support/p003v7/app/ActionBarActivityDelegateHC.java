package android.support.p003v7.app;

import android.annotation.TargetApi;
import android.support.p003v7.internal.view.SupportActionModeWrapper;
import android.support.p003v7.internal.view.SupportActionModeWrapper.CallbackWrapper;
import android.support.p003v7.internal.widget.NativeActionModeAwareLayout;
import android.support.p003v7.internal.widget.NativeActionModeAwareLayout.OnActionModeForChildListener;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.View;

@TargetApi(11)
/* renamed from: android.support.v7.app.ActionBarActivityDelegateHC */
class ActionBarActivityDelegateHC extends ActionBarActivityDelegateBase implements OnActionModeForChildListener {
    private NativeActionModeAwareLayout mNativeActionModeAwareLayout;

    ActionBarActivityDelegateHC(ActionBarActivity activity) {
        super(activity);
    }

    /* access modifiers changed from: 0000 */
    public void onSubDecorInstalled() {
        this.mNativeActionModeAwareLayout = (NativeActionModeAwareLayout) this.mActivity.findViewById(16908290);
        if (this.mNativeActionModeAwareLayout != null) {
            this.mNativeActionModeAwareLayout.setActionModeForChildListener(this);
        }
    }

    public ActionMode startActionModeForChild(View originalView, Callback callback) {
        android.support.p003v7.view.ActionMode supportActionMode = startSupportActionMode(new CallbackWrapper(originalView.getContext(), callback));
        if (supportActionMode != null) {
            return new SupportActionModeWrapper(this.mActivity, supportActionMode);
        }
        return null;
    }
}
