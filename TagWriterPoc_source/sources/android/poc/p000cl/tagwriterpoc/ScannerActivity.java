package android.poc.p000cl.tagwriterpoc;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.poc.p000cl.tagwriterpoc.MessageDialogFragment.MessageDialogListener;
import android.poc.p000cl.tagwriterpoc.ZXingScannerView.ResultHandler;
import android.support.p001v4.app.DialogFragment;
import android.support.p004v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* renamed from: android.poc.cl.tagwriterpoc.ScannerActivity */
public class ScannerActivity extends ActionBarActivity implements MessageDialogListener, ResultHandler {
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String CAMERA_ID = "CAMERA_ID";
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    String TAG = "Caja POC";
    boolean acercar_nfc = false;
    AlertDialog alertDialog;
    final Context context = this;
    String hexdump = new String();
    String id_qr = "";
    private boolean mAutoFocus;
    private int mCameraId = -1;
    private boolean mFlash;
    private ZXingScannerView mScannerView;
    private ArrayList<Integer> mSelectedIndices;
    NfcAdapter nfcAdapter;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mFlash = savedInstanceState.getBoolean(FLASH_STATE, false);
            this.mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
            this.mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS);
            this.mCameraId = savedInstanceState.getInt(CAMERA_ID, 0);
        } else {
            this.mFlash = false;
            this.mAutoFocus = true;
            this.mSelectedIndices = null;
            this.mCameraId = 0;
        }
        this.mScannerView = new ZXingScannerView(this);
        setupFormats();
        setContentView((View) this.mScannerView);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.acercar_nfc = false;
    }

    public void onResume() {
        super.onResume();
        this.mScannerView.setResultHandler(this);
        this.mScannerView.startCamera(this.mCameraId);
        this.mScannerView.setFlash(this.mFlash);
        this.mScannerView.setAutoFocus(this.mAutoFocus);
        if (this.nfcAdapter != null) {
            enableForegroundDispatchSystem();
        }
        Log.d("ScannerActivity", "onResume");
    }

    public void onPause() {
        super.onPause();
        this.mScannerView.stopCamera();
        closeMessageDialog();
        if (this.nfcAdapter != null) {
            disableForegroundDispatchSystem();
        }
        Log.d("ScannerActivity", "onPause");
    }

    private void enableForegroundDispatchSystem() {
        this.nfcAdapter.enableForegroundDispatch(this, PendingIntent.getActivity(this, 0, new Intent(this, ScannerActivity.class).addFlags(536870912), 0), new IntentFilter[0], null);
    }

    public void disableForegroundDispatchSystem() {
        this.nfcAdapter.disableForegroundDispatch(this);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("android.nfc.extra.TAG")) {
            Tag tag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
            byte[] tagId = tag.getId();
            this.hexdump = new String();
            for (byte b : tagId) {
                String x = Integer.toHexString(b & 255);
                if (x.length() == 1) {
                    x = '0' + x;
                }
                this.hexdump += x;
            }
            for (int i = this.hexdump.length(); i < 16; i++) {
                this.hexdump += '0';
            }
            if (this.acercar_nfc && !this.id_qr.equalsIgnoreCase("")) {
                try {
                    MCrypt mcrypt = new MCrypt(this.hexdump);
                    String a_encriptar = this.id_qr;
                    String hash = MCrypt.bytesToHex(mcrypt.encrypt(a_encriptar));
                    Log.d(this.TAG, "hash " + hash);
                    Log.d(this.TAG, "a encriptar " + a_encriptar);
                    writeNdefMessage(tag, createNdefMessage(hash));
                    this.acercar_nfc = false;
                    this.id_qr = "";
                } catch (Exception e) {
                    this.acercar_nfc = false;
                    e.printStackTrace();
                }
            }
        }
    }

    private NdefMessage createNdefMessage(String content) {
        return new NdefMessage(new NdefRecord[]{createTextRecord(content)});
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8");
            int languageSize = language.length;
            int textLength = text.length;
            ByteArrayOutputStream payload = new ByteArrayOutputStream(languageSize + 1 + textLength);
            payload.write((byte) (languageSize & 31));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);
            return new NdefRecord(1, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
            return null;
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        if (tag == null) {
            try {
                Toast.makeText(this, "Tag object cannot be null", 0).show();
            } catch (Exception e) {
                Log.e("writeNdefMessage", e.getMessage());
            }
        } else {
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable!", 0).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Written OK!", 0).show();
            }
            if (this.alertDialog.isShowing()) {
                this.alertDialog.dismiss();
            }
        }
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", 0).show();
                return;
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(this, "Written OK!", 0).show();
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, this.mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, this.mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, this.mSelectedIndices);
        outState.putInt(CAMERA_ID, this.mCameraId);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        Log.d("PrincipalActivity", "INSIDE: onDestroy");
    }

    private void abrirDialogoTexto(String texto) {
        View vista_config = LayoutInflater.from(this.context).inflate(C0001R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0001R.C0003id.texto_cambio)).setText(texto);
        alertDialogBuilder.setCancelable(false).setTitle("Write NFC");
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeDialog(String dialogName) {
        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        if (this.mSelectedIndices == null || this.mSelectedIndices.isEmpty()) {
            this.mSelectedIndices = new ArrayList<>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                this.mSelectedIndices.add(Integer.valueOf(i));
            }
        }
        Iterator i$ = this.mSelectedIndices.iterator();
        while (i$.hasNext()) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(((Integer) i$.next()).intValue()));
        }
        if (this.mScannerView != null) {
            this.mScannerView.setFormats(formats);
        }
    }

    public void handleResult(Result rawResult) {
        String url_qr = rawResult.getText();
        String[] qrs = url_qr.split("/");
        Log.v("ScannerActivity", "Size QR " + qrs.length);
        if (qrs.length > 2) {
            this.id_qr = qrs[qrs.length - 1];
            Toast.makeText(this.context, "Qr = " + rawResult.getText(), 0).show();
            this.id_qr = url_qr;
            this.acercar_nfc = true;
            abrirDialogoTexto("Bring the Tag to Phone");
            return;
        }
        Toast.makeText(this.context, "Format QR not valid", 1).show();
        this.mScannerView.startCamera();
        this.acercar_nfc = false;
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        this.mScannerView.startCamera(this.mCameraId);
        this.mScannerView.setFlash(this.mFlash);
        this.mScannerView.setAutoFocus(this.mAutoFocus);
    }
}
