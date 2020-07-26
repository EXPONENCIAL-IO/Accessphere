package p004cl.android.poc.pocrecarga;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.p003v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import p006me.dm7.barcodescanner.zbar.Result;
import p006me.dm7.barcodescanner.zbar.ZBarScannerView;
import p006me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;

/* renamed from: cl.android.poc.pocrecarga.ScannerActivityZbar */
public class ScannerActivityZbar extends ActionBarActivity implements ResultHandler {
    final Context context = this;
    private String galleryPath;
    String id_qr = "";
    private ZBarScannerView mScannerView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mScannerView = new ZBarScannerView(this);
        setContentView((View) this.mScannerView);
        this.mScannerView.setDrawingCacheEnabled(true);
    }

    public void onResume() {
        super.onResume();
        this.mScannerView.setResultHandler(this);
        this.mScannerView.startCamera();
        Log.d("ScannerActivity", "onResume");
    }

    public void onPause() {
        super.onPause();
        this.mScannerView.stopCamera();
        Log.d("ScannerActivity", "onPause");
    }

    public void handleResult(Result rawResult) {
        String[] qrs = rawResult.getContents().split("/");
        Log.v("ScannerActivity", "Size QR " + qrs.length);
        if (qrs.length > 2) {
            this.id_qr = qrs[qrs.length - 1];
            Toast.makeText(this.context, "Qr = " + rawResult.getContents(), 0).show();
            Intent intent = new Intent(this, PrincipalActivity.class);
            intent.putExtra("id_qr", this.id_qr);
            startActivity(intent);
            return;
        }
        Toast.makeText(this.context, "Formato de QR No valido", 1).show();
        this.mScannerView.startCamera();
    }
}
