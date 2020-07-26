package p004cl.android.poc.cashless;

import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.p000v4.app.DialogFragment;
import android.support.p000v4.view.MenuItemCompat;
import android.support.p003v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import p004cl.android.poc.cashless.MessageDialogFragment.MessageDialogListener;
import p004cl.android.poc.cashless.ZXingScannerView.ResultHandler;
import p004cl.android.poc.cashless.clases.MCrypt;
import p004cl.android.poc.cashless.clases.Producto;

/* renamed from: cl.android.poc.cashless.ScannerActivity */
public class ScannerActivity extends ActionBarActivity implements MessageDialogListener, ResultHandler {
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String CAMERA_ID = "CAMERA_ID";
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    String TAG = "Caja POC";
    Cargar_Saldo cargar_saldo = null;
    final Context context = this;
    String hexdump = new String();
    String id_qr = "";
    ArrayList<Producto> itemsCarroCompras;
    private boolean mAutoFocus;
    private int mCameraId = -1;
    private boolean mFlash;
    private ZXingScannerView mScannerView;
    private ArrayList<Integer> mSelectedIndices;
    int monto_compra = 0;
    int monto_saldo = 0;
    NfcAdapter nfcAdapter;
    SharedPreferences prefs;
    String url_qr_qr = "";
    String url_servicios;

    /* renamed from: cl.android.poc.cashless.ScannerActivity$Cargar_Saldo */
    private class Cargar_Saldo extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Cargar_Saldo() {
            this.progressDialog = new ProgressDialog(ScannerActivity.this.context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.progressDialog.setMessage("Cargando Saldo...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            consulta_saldo();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String unused) {
            this.progressDialog.dismiss();
            if (ScannerActivity.this.monto_saldo > 0) {
                Intent intent = new Intent(ScannerActivity.this, ResultadoCompras.class);
                intent.putExtra("qr_completo", ScannerActivity.this.url_qr_qr);
                intent.putExtra("monto_compra", ScannerActivity.this.monto_compra);
                intent.putExtra("monto_saldo", ScannerActivity.this.monto_saldo);
                intent.putExtra("id_qr", ScannerActivity.this.id_qr);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("carro_compras", ScannerActivity.this.itemsCarroCompras);
                intent.putExtras(bundle);
                ScannerActivity.this.startActivity(intent);
                ScannerActivity.this.finish();
                return;
            }
            Toast.makeText(ScannerActivity.this.context, "Error o Saldo Menor a 0", 1).show();
            Intent intent2 = new Intent(ScannerActivity.this, ResultadoCompras.class);
            intent2.putExtra("qr_completo", ScannerActivity.this.url_qr_qr);
            intent2.putExtra("monto_compra", ScannerActivity.this.monto_compra);
            intent2.putExtra("monto_saldo", ScannerActivity.this.monto_saldo);
            intent2.putExtra("id_qr", ScannerActivity.this.id_qr);
            Bundle bundle2 = new Bundle();
            bundle2.putParcelableArrayList("carro_compras", ScannerActivity.this.itemsCarroCompras);
            intent2.putExtras(bundle2);
            ScannerActivity.this.startActivity(intent2);
            ScannerActivity.this.finish();
        }

        private void consulta_saldo() {
            HttpClient httpClient = new DefaultHttpClient();
            String url = "http://" + ScannerActivity.this.url_servicios + "/event/api/account/qr/" + ScannerActivity.this.id_qr;
            Log.d("ResultadoWS", "Url de peticion : " + url);
            HttpGet del = new HttpGet(url);
            del.setHeader("content-type", "application/json");
            try {
                String respStr = EntityUtils.toString(httpClient.execute(del).getEntity());
                Log.d("ResultadoWS", "Recibi del ws : " + respStr);
                JSONObject respJSON = new JSONObject(respStr);
                ScannerActivity.this.monto_saldo = respJSON.getInt("balance");
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                ex.printStackTrace();
                ScannerActivity.this.monto_saldo = 0;
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mFlash = savedInstanceState.getBoolean(FLASH_STATE, false);
            this.mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true);
            this.mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS);
            this.mCameraId = savedInstanceState.getInt(CAMERA_ID, -1);
        } else {
            this.mFlash = false;
            this.mAutoFocus = true;
            this.mSelectedIndices = null;
            this.mCameraId = -1;
        }
        this.mScannerView = new ZXingScannerView(this);
        this.prefs = getSharedPreferences("Preferencias_barra", 0);
        this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
        setupFormats();
        setContentView((View) this.mScannerView);
        this.cargar_saldo = new Cargar_Saldo();
        Log.d("ScannerActivity", "onCreate");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.monto_compra = extras.getInt("monto_compra");
            this.itemsCarroCompras = extras.getParcelableArrayList("carro_compras");
        }
        Log.d("ScannerActivity", "Cantidad de Objetos --> " + this.itemsCarroCompras.size());
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        MenuItem menuItem2;
        if (this.mFlash) {
            menuItem = menu.add(0, C0200R.C0202id.menu_flash, 0, C0200R.string.flash_on);
        } else {
            menuItem = menu.add(0, C0200R.C0202id.menu_flash, 0, C0200R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, 2);
        if (this.mAutoFocus) {
            menuItem2 = menu.add(0, C0200R.C0202id.menu_auto_focus, 0, C0200R.string.auto_focus_on);
        } else {
            menuItem2 = menu.add(0, C0200R.C0202id.menu_auto_focus, 0, C0200R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem2, 2);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z = false;
        switch (item.getItemId()) {
            case C0200R.C0202id.menu_auto_focus /*2131492869*/:
                if (!this.mAutoFocus) {
                    z = true;
                }
                this.mAutoFocus = z;
                if (this.mAutoFocus) {
                    item.setTitle(C0200R.string.auto_focus_on);
                } else {
                    item.setTitle(C0200R.string.auto_focus_off);
                }
                this.mScannerView.setAutoFocus(this.mAutoFocus);
                return true;
            case C0200R.C0202id.menu_flash /*2131492870*/:
                if (!this.mFlash) {
                    z = true;
                }
                this.mFlash = z;
                if (this.mFlash) {
                    item.setTitle(C0200R.string.flash_on);
                } else {
                    item.setTitle(C0200R.string.flash_off);
                }
                this.mScannerView.setFlash(this.mFlash);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, this.mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, this.mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, this.mSelectedIndices);
        outState.putInt(CAMERA_ID, this.mCameraId);
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

    public void onDialogPositiveClick(DialogFragment dialog) {
        this.mScannerView.startCamera(this.mCameraId);
        this.mScannerView.setFlash(this.mFlash);
        this.mScannerView.setAutoFocus(this.mAutoFocus);
    }

    public void handleResult(Result rawResult) {
        String url_qr = rawResult.getText();
        this.url_qr_qr = rawResult.getText();
        String[] qrs = url_qr.split("/");
        Log.v("ScannerActivity", "Size QR " + qrs.length);
        if (qrs.length > 2) {
            this.id_qr = qrs[qrs.length - 1];
            if (checkInternetConnection()) {
                this.cargar_saldo.execute(new String[0]);
            } else {
                Toast.makeText(this.context, "No Dispones de conexion a Internet", 1).show();
            }
        } else {
            Toast.makeText(this.context, "Formato de QR No valido", 1).show();
            this.mScannerView.startCamera();
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

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService("connectivity");
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        Log.v("statusInternet", "No hay internet");
        return false;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        String data_tag;
        super.onNewIntent(intent);
        if (intent.hasExtra("android.nfc.extra.TAG")) {
            Toast.makeText(this, "Tag encontrado... Validando Data", 0).show();
            byte[] tagId = ((Tag) intent.getParcelableExtra("android.nfc.extra.TAG")).getId();
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
            Parcelable[] parcelables = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
            Log.i(this.TAG, "Serial " + tagId + "");
            String str = "";
            if (parcelables == null || parcelables.length <= 0) {
                data_tag = "NODATA";
            } else {
                data_tag = readTextFromMessage((NdefMessage) parcelables[0]);
            }
            Log.i(this.TAG, "Data Tag " + data_tag + "");
            if (data_tag.equalsIgnoreCase("NODATA")) {
                abrirDialogoTexto("Tag sin data, favor usar Lector QR");
                return;
            }
            String[] data_tag_s = data_tag.split("@");
            String url_qr = data_tag_s[0];
            this.url_qr_qr = data_tag_s[0];
            String monto = data_tag_s[1];
            Log.i(this.TAG, "URL " + url_qr + "");
            Log.i(this.TAG, "MONTO " + monto + "");
            String[] qrs = url_qr.split("/");
            if (qrs.length > 2) {
                this.id_qr = qrs[qrs.length - 1];
                if (checkInternetConnection()) {
                    this.cargar_saldo.execute(new String[0]);
                    return;
                }
                Intent intent_p = new Intent(this, ResultadoCompras.class);
                intent_p.putExtra("qr_completo", url_qr);
                intent_p.putExtra("monto_compra", this.monto_compra);
                intent_p.putExtra("monto_saldo", monto);
                intent_p.putExtra("id_qr", this.id_qr);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("carro_compras", this.itemsCarroCompras);
                intent.putExtras(bundle);
                startActivity(intent_p);
                finish();
                return;
            }
            Toast.makeText(this.context, "Formato de QR No valido", 1).show();
        }
    }

    private String readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords == null || ndefRecords.length <= 0) {
            return "NODATA";
        }
        try {
            NdefRecord ndefRecord = ndefRecords[0];
            MCrypt mcrypt = new MCrypt(this.hexdump);
            String str = "";
            if (getTextFromNdefRecord(ndefRecord).equalsIgnoreCase("")) {
                return "NODATA";
            }
            String tagContent = new String(mcrypt.decrypt(getTextFromNdefRecord(ndefRecord)));
            Log.d(this.TAG, "a Desencriptar " + getTextFromNdefRecord(ndefRecord));
            Log.d(this.TAG, " Desencriptado " + tagContent);
            return tagContent;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = "";
        try {
            byte[] payload = ndefRecord.getPayload();
            if (payload.length == 0) {
                return "";
            }
            int languageSize = payload[0] & 51;
            tagContent = new String(payload, languageSize + 1, (payload.length - languageSize) - 1, (payload[0] & 128) == 0 ? "UTF-8" : "UTF-16");
            return tagContent;
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
    }

    private void abrirDialogoTexto(String texto) {
        View vista_config = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0200R.C0202id.texto_cambio)).setText(texto);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Confirmación");
        alertDialogBuilder.create().show();
    }
}
