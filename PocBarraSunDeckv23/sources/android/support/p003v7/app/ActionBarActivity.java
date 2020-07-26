package android.support.p003v7.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.ActionBarDrawerToggle.Delegate;
import android.support.p000v4.app.ActionBarDrawerToggle.DelegateProvider;
import android.support.p000v4.app.ActivityCompat;
import android.support.p000v4.app.FragmentActivity;
import android.support.p000v4.app.NavUtils;
import android.support.p000v4.app.TaskStackBuilder;
import android.support.p000v4.app.TaskStackBuilder.SupportParentable;
import android.support.p003v7.view.ActionMode;
import android.support.p003v7.view.ActionMode.Callback;
import android.support.p003v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/* renamed from: android.support.v7.app.ActionBarActivity */
public class ActionBarActivity extends FragmentActivity implements Callback, SupportParentable, DelegateProvider, TmpDelegateProvider {
    private ActionBarActivityDelegate mDelegate;

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    public void setContentView(int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    public void setContentView(View view, LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    public void addContentView(View view, LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    /* access modifiers changed from: protected */
    public void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    public final boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (super.onMenuItemSelected(featureId, item)) {
            return true;
        }
        ActionBar ab = getSupportActionBar();
        if (item.getItemId() != 16908332 || ab == null || (ab.getDisplayOptions() & 4) == 0) {
            return false;
        }
        return onSupportNavigateUp();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        getDelegate().destroy();
    }

    /* access modifiers changed from: protected */
    public void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().onTitleChanged(title);
    }

    public boolean supportRequestWindowFeature(int featureId) {
        return getDelegate().supportRequestWindowFeature(featureId);
    }

    public void supportInvalidateOptionsMenu() {
        getDelegate().supportInvalidateOptionsMenu();
    }

    public void invalidateOptionsMenu() {
        getDelegate().supportInvalidateOptionsMenu();
    }

    public void onSupportActionModeStarted(ActionMode mode) {
    }

    public void onSupportActionModeFinished(ActionMode mode) {
    }

    public ActionMode startSupportActionMode(Callback callback) {
        return getDelegate().startSupportActionMode(callback);
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return getDelegate().onCreatePanelMenu(featureId, menu);
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return getDelegate().onPreparePanel(featureId, view, menu);
    }

    public void onPanelClosed(int featureId, Menu menu) {
        getDelegate().onPanelClosed(featureId, menu);
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        return getDelegate().onMenuOpened(featureId, menu);
    }

    /* access modifiers changed from: protected */
    public boolean onPrepareOptionsPanel(View view, Menu menu) {
        return getDelegate().onPrepareOptionsPanel(view, menu);
    }

    /* access modifiers changed from: 0000 */
    public void superSetContentView(int resId) {
        super.setContentView(resId);
    }

    /* access modifiers changed from: 0000 */
    public void superSetContentView(View v) {
        super.setContentView(v);
    }

    /* access modifiers changed from: 0000 */
    public void superSetContentView(View v, LayoutParams lp) {
        super.setContentView(v, lp);
    }

    /* access modifiers changed from: 0000 */
    public void superAddContentView(View v, LayoutParams lp) {
        super.addContentView(v, lp);
    }

    /* access modifiers changed from: 0000 */
    public boolean superOnCreatePanelMenu(int featureId, Menu frameworkMenu) {
        return super.onCreatePanelMenu(featureId, frameworkMenu);
    }

    /* access modifiers changed from: 0000 */
    public boolean superOnPreparePanel(int featureId, View view, Menu menu) {
        return super.onPreparePanel(featureId, view, menu);
    }

    /* access modifiers changed from: 0000 */
    public boolean superOnPrepareOptionsPanel(View view, Menu menu) {
        return super.onPrepareOptionsPanel(view, menu);
    }

    /* access modifiers changed from: 0000 */
    public void superOnPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    /* access modifiers changed from: 0000 */
    public boolean superOnMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    public void onBackPressed() {
        if (!getDelegate().onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void setSupportProgressBarVisibility(boolean visible) {
        getDelegate().setSupportProgressBarVisibility(visible);
    }

    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getDelegate().setSupportProgressBarIndeterminateVisibility(visible);
    }

    public void setSupportProgressBarIndeterminate(boolean indeterminate) {
        getDelegate().setSupportProgressBarIndeterminate(indeterminate);
    }

    public void setSupportProgress(int progress) {
        getDelegate().setSupportProgress(progress);
    }

    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        builder.addParentStack((Activity) this);
    }

    public void onPrepareSupportNavigateUpTaskStack(TaskStackBuilder builder) {
    }

    public boolean onSupportNavigateUp() {
        Intent upIntent = getSupportParentActivityIntent();
        if (upIntent == null) {
            return false;
        }
        if (supportShouldUpRecreateTask(upIntent)) {
            TaskStackBuilder b = TaskStackBuilder.create(this);
            onCreateSupportNavigateUpTaskStack(b);
            onPrepareSupportNavigateUpTaskStack(b);
            b.startActivities();
            try {
                ActivityCompat.finishAffinity(this);
            } catch (IllegalStateException e) {
                finish();
            }
        } else {
            supportNavigateUpTo(upIntent);
        }
        return true;
    }

    public Intent getSupportParentActivityIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    public boolean supportShouldUpRecreateTask(Intent targetIntent) {
        return NavUtils.shouldUpRecreateTask(this, targetIntent);
    }

    public void supportNavigateUpTo(Intent upIntent) {
        NavUtils.navigateUpTo(this, upIntent);
    }

    public final Delegate getDrawerToggleDelegate() {
        return getDelegate().getDrawerToggleDelegate();
    }

    @Nullable
    public ActionBarDrawerToggle.Delegate getV7DrawerToggleDelegate() {
        return getDelegate().getV7DrawerToggleDelegate();
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return getDelegate().onKeyShortcut(keyCode, event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (getDelegate().dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public final void onContentChanged() {
        getDelegate().onContentChanged();
    }

    public void onSupportContentChanged() {
    }

    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View result = super.onCreateView(name, context, attrs);
        return result != null ? result : getDelegate().createView(name, context, attrs);
    }

    private ActionBarActivityDelegate getDelegate() {
        if (this.mDelegate == null) {
            this.mDelegate = ActionBarActivityDelegate.createDelegate(this);
        }
        return this.mDelegate;
    }
}
