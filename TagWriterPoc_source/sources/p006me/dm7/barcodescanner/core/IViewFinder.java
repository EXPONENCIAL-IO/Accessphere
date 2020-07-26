package p006me.dm7.barcodescanner.core;

import android.graphics.Rect;

/* renamed from: me.dm7.barcodescanner.core.IViewFinder */
public interface IViewFinder {
    Rect getFramingRect();

    int getHeight();

    int getWidth();

    void setupViewFinder();
}
