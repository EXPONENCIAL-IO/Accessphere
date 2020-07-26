package p004cl.android.poc.pocrecarga;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import p006me.dm7.barcodescanner.core.BarcodeScannerView;
import p006me.dm7.barcodescanner.core.DisplayUtils;

/* renamed from: cl.android.poc.pocrecarga.ZXingScannerView */
public class ZXingScannerView extends BarcodeScannerView {
    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList();
    private List<BarcodeFormat> mFormats;
    private MultiFormatReader mMultiFormatReader;
    private ResultHandler mResultHandler;

    /* renamed from: cl.android.poc.pocrecarga.ZXingScannerView$ResultHandler */
    public interface ResultHandler {
        void handleResult(Result result);
    }

    static {
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.QR_CODE);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
    }

    public ZXingScannerView(Context context) {
        super(context);
        initMultiFormatReader();
    }

    public ZXingScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initMultiFormatReader();
    }

    public void setFormats(List<BarcodeFormat> formats) {
        this.mFormats = formats;
        initMultiFormatReader();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        this.mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if (this.mFormats == null) {
            return ALL_FORMATS;
        }
        return this.mFormats;
    }

    private void initMultiFormatReader() {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
        this.mMultiFormatReader = new MultiFormatReader();
        this.mMultiFormatReader.setHints(hints);
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
        Result rawResult = null;
        PlanarYUVLuminanceSource source = buildLuminanceSource(data, width, height);
        if (source != null) {
            try {
                rawResult = this.mMultiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(source)));
            } catch (ReaderException e) {
            } catch (NullPointerException e2) {
            } catch (ArrayIndexOutOfBoundsException e3) {
            } finally {
                this.mMultiFormatReader.reset();
            }
        }
        if (rawResult != null) {
            stopCamera();
            if (this.mResultHandler != null) {
                bundleThumbnail(source, rawResult.getText());
                this.mResultHandler.handleResult(rawResult);
                return;
            }
            return;
        }
        camera.setOneShotPreviewCallback(this);
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        }
        try {
            return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect.height(), false);
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x00f9 A[SYNTHETIC, Splitter:B:20:0x00f9] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0108 A[SYNTHETIC, Splitter:B:26:0x0108] */
    /* JADX WARNING: Removed duplicated region for block: B:38:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void bundleThumbnail(com.google.zxing.PlanarYUVLuminanceSource r20, java.lang.String r21) {
        /*
            int[] r1 = r20.renderThumbnail()
            int r3 = r20.getThumbnailWidth()
            int r5 = r20.getThumbnailHeight()
            r2 = 0
            android.graphics.Bitmap$Config r6 = android.graphics.Bitmap.Config.ARGB_8888
            r4 = r3
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r1, r2, r3, r4, r5, r6)
            java.lang.String r2 = "/"
            r0 = r21
            java.lang.String[] r19 = r0.split(r2)
            java.util.Calendar r9 = java.util.Calendar.getInstance()
            java.util.Date r2 = new java.util.Date
            r2.<init>()
            r9.setTime(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = ""
            java.lang.StringBuilder r2 = r2.append(r4)
            r4 = 1
            int r4 = r9.get(r4)
            java.lang.StringBuilder r2 = r2.append(r4)
            r4 = 2
            int r4 = r9.get(r4)
            int r4 = r4 + 1
            java.lang.StringBuilder r2 = r2.append(r4)
            r4 = 5
            int r4 = r9.get(r4)
            java.lang.StringBuilder r2 = r2.append(r4)
            r4 = 10
            int r4 = r9.get(r4)
            java.lang.StringBuilder r2 = r2.append(r4)
            r4 = 12
            int r4 = r9.get(r4)
            java.lang.StringBuilder r2 = r2.append(r4)
            r4 = 13
            int r4 = r9.get(r4)
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r14 = r2.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r0 = r19
            int r4 = r0.length
            int r4 = r4 + -1
            r4 = r19[r4]
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.StringBuilder r2 = r2.append(r14)
            java.lang.String r18 = r2.toString()
            if (r8 != 0) goto L_0x0094
            java.lang.String r2 = "ScannerActivity"
            java.lang.String r4 = "No Foto!!!"
            android.util.Log.d(r2, r4)
        L_0x0093:
            return
        L_0x0094:
            java.lang.String r7 = "Poc_Caja/resp_fotos_tomadas"
            java.io.File r16 = new java.io.File
            java.io.File r2 = android.os.Environment.getExternalStorageDirectory()
            r0 = r16
            r0.<init>(r2, r7)
            r16.mkdirs()
            java.lang.String r17 = r16.getAbsolutePath()
            java.io.File r10 = new java.io.File
            r0 = r17
            r10.<init>(r0)
            boolean r2 = r10.exists()
            if (r2 != 0) goto L_0x00b8
            r10.mkdirs()
        L_0x00b8:
            java.io.File r15 = new java.io.File
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "qr_"
            java.lang.StringBuilder r2 = r2.append(r4)
            r0 = r18
            java.lang.StringBuilder r2 = r2.append(r0)
            java.lang.String r4 = ".png"
            java.lang.StringBuilder r2 = r2.append(r4)
            java.lang.String r2 = r2.toString()
            r15.<init>(r10, r2)
            r12 = 0
            java.io.FileOutputStream r13 = new java.io.FileOutputStream     // Catch:{ FileNotFoundException -> 0x00f3 }
            r13.<init>(r15)     // Catch:{ FileNotFoundException -> 0x00f3 }
            android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ FileNotFoundException -> 0x0117, all -> 0x0114 }
            r4 = 85
            r8.compress(r2, r4, r13)     // Catch:{ FileNotFoundException -> 0x0117, all -> 0x0114 }
            if (r13 == 0) goto L_0x0093
            r13.flush()     // Catch:{ IOException -> 0x00ee }
            r13.close()     // Catch:{ IOException -> 0x00ee }
            goto L_0x0093
        L_0x00ee:
            r11 = move-exception
            r11.printStackTrace()
            goto L_0x0093
        L_0x00f3:
            r11 = move-exception
        L_0x00f4:
            r11.printStackTrace()     // Catch:{ all -> 0x0105 }
            if (r12 == 0) goto L_0x0093
            r12.flush()     // Catch:{ IOException -> 0x0100 }
            r12.close()     // Catch:{ IOException -> 0x0100 }
            goto L_0x0093
        L_0x0100:
            r11 = move-exception
            r11.printStackTrace()
            goto L_0x0093
        L_0x0105:
            r2 = move-exception
        L_0x0106:
            if (r12 == 0) goto L_0x010e
            r12.flush()     // Catch:{ IOException -> 0x010f }
            r12.close()     // Catch:{ IOException -> 0x010f }
        L_0x010e:
            throw r2
        L_0x010f:
            r11 = move-exception
            r11.printStackTrace()
            goto L_0x010e
        L_0x0114:
            r2 = move-exception
            r12 = r13
            goto L_0x0106
        L_0x0117:
            r11 = move-exception
            r12 = r13
            goto L_0x00f4
        */
        throw new UnsupportedOperationException("Method not decompiled: p004cl.android.poc.pocrecarga.ZXingScannerView.bundleThumbnail(com.google.zxing.PlanarYUVLuminanceSource, java.lang.String):void");
    }
}
