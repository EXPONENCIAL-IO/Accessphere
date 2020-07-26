package p004cl.android.poc.pocrecarga;

import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings.Secure;
import android.support.p000v4.app.DialogFragment;
import android.support.p000v4.view.MenuItemCompat;
import android.support.p003v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import p004cl.android.poc.pocrecarga.MessageDialogFragment.MessageDialogListener;
import p004cl.android.poc.pocrecarga.ZXingScannerView.ResultHandler;

/* renamed from: cl.android.poc.pocrecarga.ScannerActivity */
public class ScannerActivity extends ActionBarActivity implements MessageDialogListener, ResultHandler {
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String CAMERA_ID = "CAMERA_ID";
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    String TAG = "Caja POC";
    final Context context = this;

    /* renamed from: db */
    private SQLiteDatabase f25db;
    Editor editor = null;
    private String galleryPath;
    String hexdump = new String();
    String id_qr = "";
    private boolean mAutoFocus;
    private int mCameraId = -1;
    private boolean mFlash;
    private ZXingScannerView mScannerView;
    private ArrayList<Integer> mSelectedIndices;
    NfcAdapter nfcAdapter;
    SharedPreferences prefs;
    String url_servicios;

    /* access modifiers changed from: protected */
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
        this.prefs = getSharedPreferences("Preferencias_Caja", 0);
        this.editor = this.prefs.edit();
        this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
        this.mScannerView = new ZXingScannerView(this);
        setupFormats();
        setContentView((View) this.mScannerView);
        this.f25db = new TransaccionSQLiteHelper(this.context, "DBTransacciones", null, 1).getWritableDatabase();
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
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
            String monto = data_tag_s[1];
            Log.i(this.TAG, "URL " + url_qr + "");
            Log.i(this.TAG, "MONTO " + monto + "");
            String[] qrs = url_qr.split("/");
            if (qrs.length > 2) {
                this.id_qr = qrs[qrs.length - 1];
                Intent intent_p = new Intent(this, PantallaInicial.class);
                intent_p.putExtra("qr_completo", url_qr);
                intent_p.putExtra("id_qr", this.id_qr);
                intent_p.putExtra("monto", monto);
                intent_p.putExtra("medio_lectura", "NFC");
                startActivity(intent_p);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem;
        MenuItem menuItem2;
        if (this.mFlash) {
            menuItem = menu.add(0, C0281R.C0283id.menu_flash, 0, C0281R.string.flash_on);
        } else {
            menuItem = menu.add(0, C0281R.C0283id.menu_flash, 0, C0281R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, 2);
        if (this.mAutoFocus) {
            menuItem2 = menu.add(0, C0281R.C0283id.menu_auto_focus, 0, C0281R.string.auto_focus_on);
        } else {
            menuItem2 = menu.add(0, C0281R.C0283id.menu_auto_focus, 0, C0281R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem2, 2);
        MenuItemCompat.setShowAsAction(menu.add(0, C0281R.C0283id.menu_servidor, 0, C0281R.string.servidor), 2);
        MenuItemCompat.setShowAsAction(menu.add(0, C0281R.C0283id.menu_id, 0, C0281R.string.f23id), 2);
        MenuItemCompat.setShowAsAction(menu.add(0, C0281R.C0283id.menu_trx, 0, C0281R.string.trx), 2);
        MenuItemCompat.setShowAsAction(menu.add(0, C0281R.C0283id.menu_archivo, 0, C0281R.string.archivo), 2);
        MenuItemCompat.setShowAsAction(menu.add(0, C0281R.C0283id.menu_total, 0, C0281R.string.total), 2);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean z = false;
        switch (item.getItemId()) {
            case C0281R.C0283id.menu_archivo /*2131558405*/:
                GenerarArchivo();
                return true;
            case C0281R.C0283id.menu_auto_focus /*2131558406*/:
                if (!this.mAutoFocus) {
                    z = true;
                }
                this.mAutoFocus = z;
                if (this.mAutoFocus) {
                    item.setTitle(C0281R.string.auto_focus_on);
                } else {
                    item.setTitle(C0281R.string.auto_focus_off);
                }
                this.mScannerView.setAutoFocus(this.mAutoFocus);
                return true;
            case C0281R.C0283id.menu_flash /*2131558407*/:
                if (!this.mFlash) {
                    z = true;
                }
                this.mFlash = z;
                if (this.mFlash) {
                    item.setTitle(C0281R.string.flash_on);
                } else {
                    item.setTitle(C0281R.string.flash_off);
                }
                this.mScannerView.setFlash(this.mFlash);
                return true;
            case C0281R.C0283id.menu_id /*2131558408*/:
                abrirDialogoId();
                return true;
            case C0281R.C0283id.menu_servidor /*2131558409*/:
                abrirDialogoServidor();
                return true;
            case C0281R.C0283id.menu_total /*2131558410*/:
                abrirDialogoTotal();
                return true;
            case C0281R.C0283id.menu_trx /*2131558411*/:
                abrirDialogoTransacciones();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void GenerarArchivo() {
        Log.e("Lista de Transacciones", "Generando Archivo...");
        ProgressDialog progressDialog = new ProgressDialog(this.context);
        progressDialog.setMessage("Generando Archivo...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String fecha = "" + calendar.get(1) + (calendar.get(2) + 1) + calendar.get(5) + calendar.get(10) + calendar.get(12) + calendar.get(13);
        try {
            File folderPathByEquipment = new File(Environment.getExternalStorageDirectory(), "Poc_Caja/log_cierre");
            folderPathByEquipment.mkdirs();
            File dir = new File(folderPathByEquipment.getAbsolutePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream stream = new FileOutputStream(new File(dir, "log_" + fecha + ".csv"));
            String str = "";
            Cursor c = this.f25db.rawQuery("SELECT id_transaccion, device_id, qr, monto_saldo, monto_compra, mto_saldo_final, productos, cant_productos, tipo_trx,fecha FROM Transacciones order by id_transaccion desc", null);
            stream.write("ID_TRANSACCION;DEVICE_ID;QR;MONTO_SALDO;MONTO_COMPRA;MTO_SALDO_FINAL;PRODUCTOS;CANT_PRODUCTOS;TIPO_TRX;FECHA\n".getBytes());
            if (c.moveToFirst()) {
                do {
                    stream.write((c.getInt(0) + ";" + c.getString(1) + ";" + c.getString(2) + ";" + c.getInt(3) + ";" + c.getInt(4) + ";" + c.getInt(5) + ";" + c.getString(6) + ";" + c.getInt(7) + ";" + c.getString(8) + ";" + c.getString(9) + "\n").getBytes());
                } while (c.moveToNext());
            }
            progressDialog.dismiss();
            stream.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            progressDialog.dismiss();
        } catch (Exception e2) {
            e2.printStackTrace();
            progressDialog.dismiss();
        }
    }

    private void abrirDialogoTransacciones() {
        View vista_grilla = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_todas_trx, null);
        List items = new ArrayList();
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_grilla);
        try {
            Cursor c = this.f25db.rawQuery("SELECT qr, productos, monto_compra,  mto_saldo_final, fecha FROM Transacciones order by id_transaccion desc", null);
            if (c.moveToFirst()) {
                do {
                    String qr = c.getString(0);
                    String productos = c.getString(1);
                    int monto_compra = c.getInt(2);
                    int monto_saldo_final = c.getInt(3);
                    String fecha = c.getString(4);
                    items.add(new TransaccionesRealizadas(qr, productos, monto_compra, monto_saldo_final, fecha));
                    Log.e("Lista de Transacciones", "Enviando --> qr = " + qr);
                    Log.e("Lista de Transacciones", "Enviando --> monto_compra =  " + monto_compra);
                    Log.e("Lista de Transacciones", "Enviando --> monto_saldo_final =  " + monto_saldo_final);
                    Log.e("Lista de Transacciones", "Enviando --> fecha =  " + fecha);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            items = null;
        }
        ListView listaStatus = (ListView) vista_grilla.findViewById(C0281R.C0283id.lista_trx);
        listaStatus.addHeaderView((ViewGroup) getLayoutInflater().inflate(C0281R.layout.lista_item_header, listaStatus, false));
        if (items != null) {
            listaStatus.setAdapter(new TransaccionesAdapter(this.context, items));
        } else {
            Toast.makeText(this.context, "Error al cargar transacciones", 1).show();
        }
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Transacciones");
        alertDialogBuilder.create().show();
    }

    private void abrirDialogoTotal() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_ultima_trx, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        int suma_total = 0;
        try {
            Cursor c = this.f25db.rawQuery("SELECT sum(monto_compra) FROM Transacciones", null);
            if (c.moveToFirst()) {
                do {
                    suma_total = c.getInt(0);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            suma_total = 0;
        }
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_trx)).setText("Llevas de Ventas \n " + Utiles.formatear_a_pesos(suma_total));
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Total de Ventas");
        alertDialogBuilder.create().show();
    }

    private void abrirDialogoId() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService("phone");
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText("Id dispositivo \n " + new UUID((long) ("" + Secure.getString(getContentResolver(), "android_id")).hashCode(), (((long) ("" + tm.getDeviceId()).hashCode()) << 32) | ((long) ("" + tm.getSimSerialNumber()).hashCode())).toString());
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Confirmación");
        alertDialogBuilder.create().show();
    }

    private void abrirDialogoTexto(String texto) {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText(texto);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Confirmación");
        alertDialogBuilder.create().show();
    }

    public void abrirDialogoServidor() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_configuracion, null);
        Builder alertDialogBuilder = new Builder(this.context);
        this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
        alertDialogBuilder.setView(vista_config);
        final EditText userInput = (EditText) vista_config.findViewById(C0281R.C0283id.InputUrl);
        userInput.setText(this.url_servicios);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String url_ingresada = userInput.getText().toString().trim();
                if (url_ingresada.equalsIgnoreCase("")) {
                    Toast.makeText(ScannerActivity.this.context, "Debe ingresar la informacion", 0).show();
                    return;
                }
                ScannerActivity.this.editor.putString("url_ws", url_ingresada);
                ScannerActivity.this.editor.commit();
                ScannerActivity.this.url_servicios = url_ingresada;
            }
        }).setNegativeButton("Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
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
            Intent intent = new Intent(this, PantallaInicial.class);
            intent.putExtra("qr_completo", url_qr);
            intent.putExtra("id_qr", this.id_qr);
            intent.putExtra("monto", "0");
            intent.putExtra("medio_lectura", "QR");
            startActivity(intent);
            finish();
            return;
        }
        Toast.makeText(this.context, "Formato de QR No valido", 1).show();
        this.mScannerView.startCamera();
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        this.mScannerView.startCamera(this.mCameraId);
        this.mScannerView.setFlash(this.mFlash);
        this.mScannerView.setAutoFocus(this.mAutoFocus);
    }

    public void onBackPressed() {
        startActivity(new Intent(this, PantallaInicial.class));
        finish();
        super.onBackPressed();
    }
}
