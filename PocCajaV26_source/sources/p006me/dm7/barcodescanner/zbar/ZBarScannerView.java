package p006me.dm7.barcodescanner.zbar;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.text.TextUtils;
import android.util.AttributeSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import p006me.dm7.barcodescanner.core.BarcodeScannerView;
import p006me.dm7.barcodescanner.core.DisplayUtils;

/* renamed from: me.dm7.barcodescanner.zbar.ZBarScannerView */
public class ZBarScannerView extends BarcodeScannerView {
    private List<BarcodeFormat> mFormats;
    private ResultHandler mResultHandler;
    private ImageScanner mScanner;

    /* renamed from: me.dm7.barcodescanner.zbar.ZBarScannerView$ResultHandler */
    public interface ResultHandler {
        void handleResult(Result result);
    }

    static {
        System.loadLibrary("iconv");
    }

    public ZBarScannerView(Context context) {
        super(context);
        setupScanner();
    }

    public ZBarScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupScanner();
    }

    public void setFormats(List<BarcodeFormat> formats) {
        this.mFormats = formats;
        setupScanner();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if (this.mFormats == null) {
            return BarcodeFormat.ALL_FORMATS;
        }
        return this.mFormats;
    }

    public void setupScanner() {
        this.mScanner = new ImageScanner();
        this.mScanner.setConfig(0, 256, 3);
        this.mScanner.setConfig(0, 257, 3);
        this.mScanner.setConfig(0, 0, 0);
        for (BarcodeFormat format : getFormats()) {
            this.mScanner.setConfig(format.getId(), 0, 1);
        }
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        Size size = camera.getParameters().getPreviewSize();
        int width = size.width;
        int height = size.height;
        if (DisplayUtils.getScreenOrientation(getContext()) == 1) {
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    rotatedData[(((x * height) + height) - y) - 1] = data[(y * width) + x];
                }
            }
            int tmp = width;
            width = height;
            height = tmp;
            data = rotatedData;
        }
        Image barcode = new Image(width, height, "Y800");
        barcode.setData(data);
        if (this.mScanner.scanImage(barcode) != 0) {
            stopCamera();
            if (this.mResultHandler != null) {
                SymbolSet syms = this.mScanner.getResults();
                Result rawResult = new Result();
                Iterator i$ = syms.iterator();
                while (true) {
                    if (!i$.hasNext()) {
                        break;
                    }
                    Symbol sym = (Symbol) i$.next();
                    String symData = sym.getData();
                    if (!TextUtils.isEmpty(symData)) {
                        rawResult.setContents(symData);
                        rawResult.setBarcodeFormat(BarcodeFormat.getFormatById(sym.getType()));
                        break;
                    }
                }
                this.mResultHandler.handleResult(rawResult);
                return;
            }
            return;
        }
        camera.setOneShotPreviewCallback(this);
    }
}
