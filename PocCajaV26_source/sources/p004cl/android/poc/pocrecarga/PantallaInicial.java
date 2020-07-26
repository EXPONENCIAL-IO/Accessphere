package p004cl.android.poc.pocrecarga;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.widget.DrawerLayout;
import android.support.p003v7.app.ActionBarDrawerToggle;
import android.support.p003v7.app.AppCompatActivity;
import android.support.p003v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: cl.android.poc.pocrecarga.PantallaInicial */
public class PantallaInicial extends AppCompatActivity implements OnNavigationItemSelectedListener {
    String TAG = "Caja POC";
    boolean acercar_nfc = false;
    AlertDialog alertDialog;
    ImageButton btn_0;
    ImageButton btn_00;
    ImageButton btn_000;
    ImageButton btn_0_l;
    ImageButton btn_1000;
    ImageButton btn_10000;
    ImageButton btn_2000;
    ImageButton btn_20000;
    ImageButton btn_5000;
    ImageButton btn_inciar_scaner;
    ImageButton btn_k;
    ImageButton btn_registrar_carga;
    final Context context = this;

    /* renamed from: db */
    private SQLiteDatabase f17db;
    String deviceId = "";
    Editor editor = null;
    String error = "";
    boolean hay_data = false;
    String hexdump = new String();
    String id_qr = "";
    TextView label_devolucion;
    TextView label_monto;
    TextView label_recarga;
    TextView label_rut;
    TextView label_rut_inv;
    TextView label_total;
    Bitmap mBitmap = null;
    String medio_lectura = "";
    boolean modo_recarga = true;
    String monto = "";
    NfcAdapter nfcAdapter;
    boolean nfc_activo = false;
    int nuevo_saldo = 0;
    Obtener_Usuario obetener_user = null;
    SharedPreferences prefs;
    ProgressDialog progressDialog;
    String qr_completo = "";
    Resgistrar_saldo resgistrar_saldo = null;
    int response_code = 0;
    int saldo = 0;
    boolean soporta_nfc = true;
    Toolbar toolbar = null;

    /* renamed from: tt */
    TextWatcher f18tt = null;
    TextView txt_monto;
    TextView txt_monto_a_recargar;
    TextView txt_monto_devolucion;
    TextView txt_monto_en_sesion;
    TextView txt_monto_total;
    TextView txt_rut;
    String url_servicios;

    /* renamed from: cl.android.poc.pocrecarga.PantallaInicial$Obtener_Usuario */
    private class Obtener_Usuario extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Obtener_Usuario() {
            this.progressDialog = new ProgressDialog(PantallaInicial.this.context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.progressDialog.setMessage("Cargando Datos...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            PantallaInicial.this.datos_ususario();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String unused) {
            this.progressDialog.dismiss();
            if (PantallaInicial.this.response_code == 0) {
                Toast.makeText(PantallaInicial.this.context, "Error al realizar la operacion", 1).show();
                PantallaInicial.this.saldo = -1;
                PantallaInicial.this.modo_recarga();
            } else if (PantallaInicial.this.hay_data) {
                PantallaInicial.this.txt_monto.setText(PantallaInicial.formatear_a_pesos((long) PantallaInicial.this.saldo));
                PantallaInicial.this.txt_monto_devolucion.setText(PantallaInicial.formatear_a_pesos((long) PantallaInicial.this.saldo));
                if (PantallaInicial.this.modo_recarga) {
                    PantallaInicial.this.modo_recarga();
                } else if (PantallaInicial.this.saldo > 0) {
                    PantallaInicial.this.modo_devolucion();
                } else {
                    PantallaInicial.this.modo_recarga();
                }
            } else {
                switch (PantallaInicial.this.response_code) {
                    case 404:
                        PantallaInicial.this.hay_data = false;
                        PantallaInicial.this.txt_monto.setText(PantallaInicial.formatear_a_pesos(0));
                        PantallaInicial.this.txt_monto_a_recargar.setText(PantallaInicial.formatear_a_pesos(0));
                        PantallaInicial.this.txt_monto_total.setText(PantallaInicial.formatear_a_pesos(0));
                        Toast.makeText(PantallaInicial.this.context, "QR no está registrado en el sistema, favor registrarlo", 1).show();
                        PantallaInicial.this.saldo = 0;
                        PantallaInicial.this.modo_recarga();
                        return;
                    default:
                        PantallaInicial.this.hay_data = false;
                        PantallaInicial.this.txt_monto.setText(PantallaInicial.formatear_a_pesos(0));
                        PantallaInicial.this.txt_monto_a_recargar.setText(PantallaInicial.formatear_a_pesos(0));
                        PantallaInicial.this.txt_monto_total.setText(PantallaInicial.formatear_a_pesos(0));
                        Toast.makeText(PantallaInicial.this.context, "Error (" + PantallaInicial.this.response_code + ") al realizar la operacion", 1).show();
                        PantallaInicial.this.saldo = -1;
                        PantallaInicial.this.modo_recarga();
                        return;
                }
            }
        }
    }

    /* renamed from: cl.android.poc.pocrecarga.PantallaInicial$Resgistrar_saldo */
    private class Resgistrar_saldo extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Resgistrar_saldo() {
            this.progressDialog = new ProgressDialog(PantallaInicial.this.context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.progressDialog.setMessage("Registrando Saldo...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            if (!PantallaInicial.this.hay_data) {
                PantallaInicial.this.registrar_usuario();
            } else if (PantallaInicial.this.modo_recarga) {
                PantallaInicial.this.registrar_carga();
            } else {
                PantallaInicial.this.registrar_devolucion();
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String unused) {
            this.progressDialog.dismiss();
            if (PantallaInicial.this.response_code == 0) {
                Toast.makeText(PantallaInicial.this.context, "Error al realizar la operacion", 1).show();
                return;
            }
            switch (PantallaInicial.this.response_code) {
                case 200:
                    PantallaInicial.this.hay_data = true;
                    PantallaInicial.this.confirmacion_ok();
                    PantallaInicial.this.guardar_carga();
                    return;
                case 202:
                    PantallaInicial.this.hay_data = true;
                    PantallaInicial.this.nuevo_saldo = 0;
                    PantallaInicial.this.confirmacion_ok_dev();
                    PantallaInicial.this.guardar_devolucion();
                    return;
                case 404:
                    PantallaInicial.this.hay_data = false;
                    PantallaInicial.this.txt_monto_a_recargar.setText(PantallaInicial.formatear_a_pesos(0));
                    PantallaInicial.this.txt_monto_total.setText(PantallaInicial.formatear_a_pesos(0));
                    PantallaInicial.this.txt_monto_devolucion.setText(PantallaInicial.formatear_a_pesos(0));
                    Toast.makeText(PantallaInicial.this.context, "QR no está registrado en el sistema, favor registrarlo", 1).show();
                    return;
                case 500:
                    PantallaInicial.this.hay_data = false;
                    PantallaInicial.this.txt_monto_a_recargar.setText(PantallaInicial.formatear_a_pesos(0));
                    PantallaInicial.this.txt_monto_total.setText(PantallaInicial.formatear_a_pesos(0));
                    PantallaInicial.this.txt_monto_devolucion.setText(PantallaInicial.formatear_a_pesos(0));
                    Toast.makeText(PantallaInicial.this.context, "Error interno del servidor", 1).show();
                    return;
                default:
                    if (PantallaInicial.this.error.equalsIgnoreCase("")) {
                        Toast.makeText(PantallaInicial.this.context, "Error (" + PantallaInicial.this.response_code + ") al realizar la operacion", 1).show();
                        return;
                    } else {
                        Toast.makeText(PantallaInicial.this.context, "Error (" + PantallaInicial.this.response_code + ") : " + PantallaInicial.this.error, 1).show();
                        return;
                    }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(8);
        super.onCreate(savedInstanceState);
        setContentView((int) C0281R.layout.activity_pantalla_inicial);
        TransaccionSQLiteHelper transaccionSQLiteHelper = new TransaccionSQLiteHelper(this.context, "DBTransacciones", null, 1);
        this.f17db = transaccionSQLiteHelper.getWritableDatabase();
        this.toolbar = (Toolbar) findViewById(C0281R.C0283id.toolbar);
        this.toolbar.setTitle((CharSequence) getResources().getString(C0281R.string.app_name) + " -> RECARGA");
        DrawerLayout drawer = (DrawerLayout) findViewById(C0281R.C0283id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, this.toolbar, C0281R.string.navigation_drawer_open, C0281R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ((NavigationView) findViewById(C0281R.C0283id.nav_view)).setNavigationItemSelectedListener(this);
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService("phone");
        this.deviceId = new UUID((long) ("" + Secure.getString(getContentResolver(), "android_id")).hashCode(), (((long) ("" + tm.getDeviceId()).hashCode()) << 32) | ((long) ("" + tm.getSimSerialNumber()).hashCode())).toString();
        ((TextView) findViewById(C0281R.C0283id.txt_id_dispositivo)).setText(this.deviceId);
        this.txt_monto_en_sesion = (TextView) findViewById(C0281R.C0283id.txt_monto_en_sesion);
        this.txt_monto_en_sesion.setText(monto_total());
        Log.v("PantallaInicial", "Id dispositivo --> " + this.deviceId);
        this.btn_registrar_carga = (ImageButton) findViewById(C0281R.C0283id.btn_recargar);
        this.label_monto = (TextView) findViewById(C0281R.C0283id.label_monto);
        this.txt_monto = (TextView) findViewById(C0281R.C0283id.txt_monto);
        this.label_recarga = (TextView) findViewById(C0281R.C0283id.label_recarga);
        this.txt_monto_a_recargar = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        this.label_total = (TextView) findViewById(C0281R.C0283id.label_total);
        this.txt_monto_total = (TextView) findViewById(C0281R.C0283id.txt_monto_total);
        this.label_rut = (TextView) findViewById(C0281R.C0283id.label_rut);
        this.txt_rut = (TextView) findViewById(C0281R.C0283id.txt_rut);
        this.label_rut_inv = (TextView) findViewById(C0281R.C0283id.label_rut_inv);
        this.label_devolucion = (TextView) findViewById(C0281R.C0283id.label_devolucion);
        this.txt_monto_devolucion = (TextView) findViewById(C0281R.C0283id.txt_monto_devolucion);
        this.btn_1000 = (ImageButton) findViewById(C0281R.C0283id.btn_1000);
        this.btn_2000 = (ImageButton) findViewById(C0281R.C0283id.btn_2000);
        this.btn_5000 = (ImageButton) findViewById(C0281R.C0283id.btn_5000);
        this.btn_10000 = (ImageButton) findViewById(C0281R.C0283id.btn_10000);
        this.btn_20000 = (ImageButton) findViewById(C0281R.C0283id.btn_20000);
        this.btn_0 = (ImageButton) findViewById(C0281R.C0283id.btn_0);
        this.btn_00 = (ImageButton) findViewById(C0281R.C0283id.btn_00);
        this.btn_000 = (ImageButton) findViewById(C0281R.C0283id.btn_000);
        this.btn_0_l = (ImageButton) findViewById(C0281R.C0283id.btn_0_l);
        this.btn_k = (ImageButton) findViewById(C0281R.C0283id.btn_k);
        this.btn_registrar_carga.setEnabled(false);
        Bundle extras = getIntent().getExtras();
        this.obetener_user = new Obtener_Usuario();
        this.id_qr = "";
        this.prefs = getSharedPreferences("Preferencias_Caja", 0);
        this.editor = this.prefs.edit();
        this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
        this.modo_recarga = this.prefs.getBoolean("modo_recarga", true);
        Log.d("PantallaInicial", "Modo Recarga -->" + this.modo_recarga);
        if (this.modo_recarga) {
            modo_recarga();
        } else {
            modo_devolucion();
        }
        if (extras != null) {
            this.qr_completo = extras.getString("qr_completo");
            this.id_qr = extras.getString("id_qr");
            generateQRCode(this.id_qr);
            this.medio_lectura = extras.getString("medio_lectura");
            this.monto = extras.getString("monto");
            if (checkInternetConnection()) {
                this.obetener_user.execute(new String[0]);
            } else {
                Toast.makeText(this.context, "No Dispones de conexion a Internet", 1).show();
            }
        }
        this.progressDialog = new ProgressDialog(this.context);
        this.btn_inciar_scaner = (ImageButton) findViewById(C0281R.C0283id.btn_inciar_scan);
        this.btn_inciar_scaner.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PantallaInicial.this.progressDialog.setMessage("Cargando Lector...");
                PantallaInicial.this.progressDialog.setCancelable(false);
                PantallaInicial.this.progressDialog.show();
                PantallaInicial.this.startActivity(new Intent(PantallaInicial.this, ScannerActivity.class));
                PantallaInicial.this.finish();
            }
        });
        this.resgistrar_saldo = new Resgistrar_saldo();
        this.btn_registrar_carga.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PantallaInicial.this.txt_monto_a_recargar.getText().toString().equalsIgnoreCase("") && PantallaInicial.this.txt_monto_devolucion.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(PantallaInicial.this.context, "Debe colocar un monto", 1).show();
                } else if (PantallaInicial.this.checkInternetConnection()) {
                    new Resgistrar_saldo().execute(new String[0]);
                } else {
                    Toast.makeText(PantallaInicial.this.context, "No Dispones de conexion a Internet", 1).show();
                }
            }
        });
        this.f18tt = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PantallaInicial.this.txt_rut.removeTextChangedListener(PantallaInicial.this.f18tt);
                Log.d("TextWatcher", "Ancho de texto antes --> " + PantallaInicial.this.txt_rut.getText().toString().length());
                PantallaInicial.this.txt_rut.setText(Rut.formatear(PantallaInicial.this.txt_rut.getText().toString()));
                Log.d("TextWatcher", "Ancho de texto despues --> " + PantallaInicial.this.txt_rut.getText().toString().length());
                PantallaInicial.this.txt_rut.addTextChangedListener(PantallaInicial.this.f18tt);
            }
        };
        this.txt_rut.addTextChangedListener(this.f18tt);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.acercar_nfc = false;
        if (this.nfcAdapter == null) {
            this.soporta_nfc = false;
        } else if (!this.nfcAdapter.isEnabled()) {
            this.nfc_activo = false;
            abrirDialogoNFC();
        } else {
            this.nfc_activo = true;
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(C0281R.C0283id.drawer_layout);
        if (drawer.isDrawerOpen(8388611)) {
            drawer.closeDrawer(8388611);
            return;
        }
        super.onBackPressed();
        this.editor.putBoolean("modo_recarga", true);
        this.editor.commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0281R.C0284menu.pantalla_inicial, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == C0281R.C0283id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == C0281R.C0283id.modo_recarga) {
            modal_modo_recarga();
        } else if (id == C0281R.C0283id.modo_devolucion) {
            modal_modo_devolucion();
        } else if (id == C0281R.C0283id.ver_trx) {
            abrirDialogoTransacciones();
        }
        ((DrawerLayout) findViewById(C0281R.C0283id.drawer_layout)).closeDrawer(8388611);
        return true;
    }

    public void onClickBtn1(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "1";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "1");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn2(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "2";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "2");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn3(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "3";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "3");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn4(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "4";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "4");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn5(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "5";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "5");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn6(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "6";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "6");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn7(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "7";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "7");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn8(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "8";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "8");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn9(View miView) {
        if (this.modo_recarga) {
            TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
            String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "9";
            if (monto_sin_formatear.length() <= 10) {
                tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
                if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                    this.btn_registrar_carga.setEnabled(false);
                    this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                    return;
                }
                this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
                this.btn_registrar_carga.setEnabled(true);
                return;
            }
            tv.setText(formatear_a_pesos(0));
            Toast.makeText(this.context, "Excede el Máximo", 1).show();
            return;
        }
        TextView tv2 = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv2.setText(tv2.getText().toString().replace(".", "").replace(" ", "") + "9");
        if (!Rut.validar(tv2.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn0(View miView) {
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "0";
        if (monto_sin_formatear.length() <= 10) {
            tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
            if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                this.btn_registrar_carga.setEnabled(false);
                this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                return;
            }
            this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
            this.btn_registrar_carga.setEnabled(true);
            return;
        }
        tv.setText(formatear_a_pesos(0));
        Toast.makeText(this.context, "Excede el Máximo", 1).show();
    }

    public void onClickBtn00(View miView) {
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "00";
        if (monto_sin_formatear.length() <= 10) {
            tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
            if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                this.btn_registrar_carga.setEnabled(false);
                this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                return;
            }
            this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
            this.btn_registrar_carga.setEnabled(true);
            return;
        }
        tv.setText(formatear_a_pesos(0));
        Toast.makeText(this.context, "Excede el Máximo", 1).show();
    }

    public void onClickBtn000(View miView) {
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        String monto_sin_formatear = tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "") + "000";
        if (monto_sin_formatear.length() <= 10) {
            tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
            if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                this.btn_registrar_carga.setEnabled(false);
                this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                return;
            }
            this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
            this.btn_registrar_carga.setEnabled(true);
            return;
        }
        tv.setText(formatear_a_pesos(0));
        Toast.makeText(this.context, "Excede el Máximo", 1).show();
    }

    public void onClickBtn0Largo(View miView) {
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv.setText(tv.getText().toString().replace(".", "").replace(" ", "") + "0");
        if (!Rut.validar(tv.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtnK(View miView) {
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_rut);
        tv.setText(tv.getText().toString().replace(".", "").replace(" ", "") + "K");
        if (!Rut.validar(tv.getText().toString()) || this.id_qr.equalsIgnoreCase("")) {
            this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Red));
            this.label_rut_inv.setText("INGRESAR UN RUT VÁLIDO");
            this.btn_registrar_carga.setEnabled(false);
            return;
        }
        this.label_rut_inv.setTextColor(getResources().getColor(C0281R.color.Green));
        this.label_rut_inv.setText("RUT VÁLIDO");
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn1000(View miView) {
        String monto_sin_formatear;
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        if (tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "").equalsIgnoreCase("")) {
            monto_sin_formatear = "1000";
        } else {
            monto_sin_formatear = (Long.parseLong(tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "")) + 1000) + "";
        }
        tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
        if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
            this.btn_registrar_carga.setEnabled(false);
            this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
            return;
        }
        this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn2000(View miView) {
        String monto_sin_formatear;
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        if (tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "").equalsIgnoreCase("")) {
            monto_sin_formatear = "2000";
        } else {
            monto_sin_formatear = (Long.parseLong(tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "")) + 2000) + "";
        }
        tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
        if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
            this.btn_registrar_carga.setEnabled(false);
            this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
            return;
        }
        this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn5000(View miView) {
        String monto_sin_formatear;
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        if (tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "").equalsIgnoreCase("")) {
            monto_sin_formatear = "5000";
        } else {
            monto_sin_formatear = (Long.parseLong(tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "")) + 5000) + "";
        }
        tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
        if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
            this.btn_registrar_carga.setEnabled(false);
            this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
            return;
        }
        this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn10000(View miView) {
        String monto_sin_formatear;
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        if (tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "").equalsIgnoreCase("")) {
            monto_sin_formatear = "10000";
        } else {
            monto_sin_formatear = (Long.parseLong(tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "")) + 10000) + "";
        }
        tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
        if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
            this.btn_registrar_carga.setEnabled(false);
            this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
            return;
        }
        this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtn20000(View miView) {
        String monto_sin_formatear;
        TextView tv = (TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar);
        if (tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "").equalsIgnoreCase("")) {
            monto_sin_formatear = "20000";
        } else {
            monto_sin_formatear = (Long.parseLong(tv.getText().toString().replace(".", "").replace(" ", "").replace("$", "")) + 20000) + "";
        }
        tv.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
        if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
            this.btn_registrar_carga.setEnabled(false);
            this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
            return;
        }
        this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
        this.btn_registrar_carga.setEnabled(true);
    }

    public void onClickBtnLimpiar(View miView) {
        if (this.modo_recarga) {
            String monto_sin_formatear = "0";
            ((TextView) findViewById(C0281R.C0283id.txt_monto_a_recargar)).setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear)));
            if (this.saldo < 0 || this.id_qr.equalsIgnoreCase("")) {
                this.btn_registrar_carga.setEnabled(false);
                this.txt_monto_total.setText(formatear_a_pesos((long) this.saldo));
                return;
            }
            this.txt_monto_total.setText(formatear_a_pesos(Long.parseLong(monto_sin_formatear) + ((long) this.saldo)));
            this.btn_registrar_carga.setEnabled(true);
            return;
        }
        ((TextView) findViewById(C0281R.C0283id.txt_rut)).setText("");
        this.btn_registrar_carga.setEnabled(false);
        this.label_rut_inv.setText("");
    }

    public static String formatear_a_pesos(long monto2) {
        String str = "0";
        try {
            return NumberFormat.getCurrencyInstance(new Locale("es", "CL")).format(monto2);
        } catch (Exception e) {
            return "0";
        }
    }

    /* access modifiers changed from: private */
    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService("connectivity");
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        Log.v("statusInternet", "No hay internet");
        return false;
    }

    /* access modifiers changed from: private */
    public void datos_ususario() {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        String url = "http://" + this.url_servicios + "/event/api/account/detail/qr/" + this.id_qr;
        Log.d("ResultadoWS", "Url de peticion : " + url);
        HttpGet del = new HttpGet(url);
        del.setHeader("content-type", "application/json");
        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            this.response_code = resp.getStatusLine().getStatusCode();
            Log.d("ResultadoWS", "Recibi del ws (" + this.response_code + ") : " + respStr);
            if (!respStr.equalsIgnoreCase("")) {
                this.hay_data = true;
                this.saldo = new JSONObject(respStr).getInt("balance");
            }
        } catch (ConnectTimeoutException e) {
            Log.e("Timeout Exception: ", e.toString());
            e.printStackTrace();
            this.response_code = -999;
        } catch (SocketTimeoutException ste) {
            Log.e("Timeout Exception: ", ste.toString());
            ste.printStackTrace();
            this.response_code = -999;
        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            ex.printStackTrace();
            this.response_code = 0;
        }
    }

    /* access modifiers changed from: private */
    public void confirmacion_ok() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_ok, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_monto)).setText("Nuevo Monto: " + formatear_a_pesos((long) this.nuevo_saldo));
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PantallaInicial.this.txt_monto_en_sesion.setText(PantallaInicial.this.monto_total());
                if (PantallaInicial.this.nfc_activo) {
                    PantallaInicial.this.acercar_nfc = true;
                    PantallaInicial.this.abrirDialogoTexto("Saldo correcto. \n Acercar tag nuevamente para confirmar la carga");
                    return;
                }
                PantallaInicial.this.limpiar_interfaz();
            }
        }).setTitle("Confirmación");
        alertDialogBuilder.create().show();
    }

    /* access modifiers changed from: private */
    public void confirmacion_ok_dev() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText("Se ha realizado la devolución por " + formatear_a_pesos((long) this.saldo));
        this.editor.putString("texto_ultima_trx", "Última devolución realizada fue de " + formatear_a_pesos((long) this.saldo));
        this.editor.commit();
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PantallaInicial.this.txt_monto_en_sesion.setText(PantallaInicial.this.monto_total());
                if (PantallaInicial.this.nfc_activo) {
                    PantallaInicial.this.acercar_nfc = true;
                    PantallaInicial.this.abrirDialogoTexto("Saldo correcto. \n Acercar tag nuevamente para confirmar la devolución");
                    return;
                }
                PantallaInicial.this.limpiar_interfaz();
            }
        }).setTitle("Confirmación");
        alertDialogBuilder.create().show();
    }

    private void generateQRCode(String data) {
        Writer writer = new QRCodeWriter();
        String finaldata = Uri.encode(data, "ISO-8859-1");
        this.mBitmap = null;
        try {
            BitMatrix bm = writer.encode(finaldata, BarcodeFormat.QR_CODE, 250, 250);
            this.mBitmap = Bitmap.createBitmap(250, 250, Config.ARGB_8888);
            for (int i = 0; i < 250; i++) {
                for (int j = 0; j < 250; j++) {
                    this.mBitmap.setPixel(i, j, bm.get(i, j) ? ViewCompat.MEASURED_STATE_MASK : -1);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 70, stream);
        return Base64.encodeToString(stream.toByteArray(), 2);
    }

    /* access modifiers changed from: private */
    public void registrar_usuario() {
        HttpClient httpClient = new DefaultHttpClient();
        String url = "http://" + this.url_servicios + "/api/users/";
        Log.d("ResultadoWS", "Url de peticion : " + url);
        HttpPost post = new HttpPost(url);
        post.setHeader("content-type", "application/json");
        try {
            JSONObject dato = new JSONObject();
            dato.put("first_name", "event");
            dato.put("last_name", "attending");
            dato.put("email", "");
            dato.put("party_type", "person");
            JSONArray ident = new JSONArray();
            JSONObject dato_ident = new JSONObject();
            dato_ident.put("identification_type", "rut");
            dato_ident.put("identification", "");
            ident.put(dato_ident);
            JSONObject dato_ident_2 = new JSONObject();
            dato_ident_2.put("identification_type", "qr");
            dato_ident_2.put("identification", this.id_qr);
            ident.put(dato_ident_2);
            dato.put("identifications", ident);
            dato.put("profile_picture", getEncoded64ImageStringFromBitmap(this.mBitmap));
            dato.put("device_id", this.deviceId);
            Log.d("ResultadoWS", "Ws creado : " + dato.toString());
            post.setEntity(new StringEntity(dato.toString()));
            HttpResponse resp = httpClient.execute(post);
            String respStr = EntityUtils.toString(resp.getEntity());
            this.response_code = resp.getStatusLine().getStatusCode();
            Log.d("ResultadoWS", "Recibi del ws (" + this.response_code + "): " + respStr);
            if (this.response_code == 200 && !respStr.equalsIgnoreCase("")) {
                registrar_carga();
            }
            if (this.response_code == 409 && !respStr.equalsIgnoreCase("")) {
                this.error = new JSONObject(respStr).getString("error");
            }
        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            ex.printStackTrace();
            this.response_code = 0;
        }
    }

    /* access modifiers changed from: private */
    public void registrar_carga() {
        HttpClient httpClient = new DefaultHttpClient();
        String url = "http://" + this.url_servicios + "/event/api/account/qr/" + this.id_qr + "/";
        Log.d("ResultadoWS", "Url de peticion : " + url);
        HttpPut put = new HttpPut(url);
        put.setHeader("content-type", "application/json");
        try {
            JSONObject dato = new JSONObject();
            dato.put("amount", this.txt_monto_a_recargar.getText().toString().replace(".", "").replace("$", ""));
            dato.put("voucher", "");
            dato.put("device_id", this.deviceId);
            put.setEntity(new StringEntity(dato.toString()));
            HttpResponse resp = httpClient.execute(put);
            String respStr = EntityUtils.toString(resp.getEntity());
            this.response_code = resp.getStatusLine().getStatusCode();
            Log.d("ResultadoWS", "Recibi del ws (" + this.response_code + "): " + respStr);
            if (this.response_code == 200 && !respStr.equalsIgnoreCase("")) {
                this.hay_data = true;
                this.nuevo_saldo = new JSONObject(respStr).getInt("balance");
            }
        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            ex.printStackTrace();
            this.response_code = 0;
        }
    }

    /* access modifiers changed from: private */
    public void registrar_devolucion() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://" + this.url_servicios + "/event/api/refund/");
        post.setHeader("content-type", "application/json");
        try {
            JSONObject dato = new JSONObject();
            dato.put("qr_code", this.id_qr);
            dato.put("amount", this.txt_monto_devolucion.getText().toString().replace(".", "").replace(" ", "").replace("$", ""));
            dato.put("device_id", this.deviceId);
            dato.put("rut", this.txt_rut.getText().toString().replace(".", "").replace("-", ""));
            post.setEntity(new StringEntity(dato.toString()));
            HttpResponse resp = httpClient.execute(post);
            Log.d("ResultadoWS", "Recibi del ws : " + EntityUtils.toString(resp.getEntity()));
            this.response_code = resp.getStatusLine().getStatusCode();
        } catch (Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
            ex.printStackTrace();
            this.response_code = 0;
        }
    }

    private void modal_modo_devolucion() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText(" Va a pasar al modo DEVOLUCIÓN \n ¿Está seguro?");
        alertDialogBuilder.setCancelable(false).setNegativeButton(" CANCELAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton(" ACEPTAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PantallaInicial.this.limpiar_interfaz();
                PantallaInicial.this.modo_devolucion();
            }
        }).setTitle("Cambio de Modo");
        alertDialogBuilder.create().show();
    }

    private void modal_modo_recarga() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText(" Va a pasar al modo RECARGA \n ¿Está seguro?");
        alertDialogBuilder.setCancelable(false).setNegativeButton(" CANCELAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton(" ACEPTAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PantallaInicial.this.limpiar_interfaz();
                PantallaInicial.this.modo_recarga();
            }
        }).setTitle("Cambio de Modo");
        alertDialogBuilder.create().show();
    }

    /* access modifiers changed from: private */
    public void modo_devolucion() {
        this.modo_recarga = false;
        this.toolbar.setTitle((CharSequence) getResources().getString(C0281R.string.app_name) + " -> DEVOLUCIÓN");
        this.btn_registrar_carga.setImageResource(C0281R.C0282drawable.btn_realizar_devolucion);
        this.btn_registrar_carga.setEnabled(false);
        this.label_monto.setVisibility(4);
        this.txt_monto.setVisibility(4);
        this.label_recarga.setVisibility(4);
        this.txt_monto_a_recargar.setVisibility(4);
        this.label_total.setVisibility(4);
        this.txt_monto_total.setVisibility(4);
        this.label_rut.setVisibility(0);
        this.txt_rut.setVisibility(0);
        this.label_devolucion.setVisibility(0);
        this.txt_monto_devolucion.setVisibility(0);
        this.label_rut_inv.setVisibility(0);
        this.btn_1000.setVisibility(4);
        this.btn_2000.setVisibility(4);
        this.btn_5000.setVisibility(4);
        this.btn_10000.setVisibility(4);
        this.btn_20000.setVisibility(4);
        this.btn_0.setVisibility(4);
        this.btn_00.setVisibility(4);
        this.btn_000.setVisibility(4);
        this.btn_0_l.setVisibility(0);
        this.btn_k.setVisibility(0);
        this.editor.putBoolean("modo_recarga", false);
        this.editor.commit();
    }

    /* access modifiers changed from: private */
    public void modo_recarga() {
        this.modo_recarga = true;
        this.toolbar.setTitle((CharSequence) getResources().getString(C0281R.string.app_name) + " -> RECARGA");
        this.btn_registrar_carga.setImageResource(C0281R.C0282drawable.btn_realizar_recarga);
        this.btn_registrar_carga.setEnabled(false);
        this.label_monto.setVisibility(0);
        this.txt_monto.setVisibility(0);
        this.label_recarga.setVisibility(0);
        this.txt_monto_a_recargar.setVisibility(0);
        this.label_total.setVisibility(0);
        this.txt_monto_total.setVisibility(0);
        this.label_rut.setVisibility(4);
        this.txt_rut.setVisibility(4);
        this.label_devolucion.setVisibility(4);
        this.txt_monto_devolucion.setVisibility(4);
        this.label_rut_inv.setVisibility(4);
        this.btn_1000.setVisibility(0);
        this.btn_2000.setVisibility(0);
        this.btn_5000.setVisibility(0);
        this.btn_10000.setVisibility(0);
        this.btn_20000.setVisibility(0);
        this.btn_0.setVisibility(0);
        this.btn_00.setVisibility(0);
        this.btn_000.setVisibility(0);
        this.btn_0_l.setVisibility(4);
        this.btn_k.setVisibility(4);
        this.editor.putBoolean("modo_recarga", true);
        this.editor.commit();
    }

    private int obtener_id_transaccion() {
        int id_transaccion = 0;
        Cursor c = this.f17db.rawQuery("SELECT max(id_transaccion) + 1 FROM Transacciones", null);
        if (c.moveToFirst()) {
            do {
                id_transaccion = c.getInt(0);
            } while (c.moveToNext());
        }
        Log.v("Obtener Id Carga", "id_transaccion --> " + id_transaccion);
        return id_transaccion;
    }

    /* access modifiers changed from: private */
    public void guardar_carga() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String fecha = calendar.get(5) + "/" + (calendar.get(2) + 1) + "/" + calendar.get(1) + " " + calendar.get(10) + ":" + calendar.get(12);
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id_transaccion", Integer.valueOf(obtener_id_transaccion()));
        nuevoRegistro.put("device_id", this.deviceId);
        nuevoRegistro.put("qr", this.id_qr);
        nuevoRegistro.put("monto_saldo", Integer.valueOf(0));
        nuevoRegistro.put("monto_compra", Integer.valueOf(Integer.parseInt(this.txt_monto_a_recargar.getText().toString().replace(".", "").replace("$", ""))));
        nuevoRegistro.put("mto_saldo_final", Integer.valueOf(this.nuevo_saldo));
        nuevoRegistro.put("productos", "CARGA");
        nuevoRegistro.put("cant_productos", Integer.valueOf(1));
        nuevoRegistro.put("tipo_trx", "CARGA CAJA");
        nuevoRegistro.put("fecha", fecha);
        this.f17db.insert("Transacciones", null, nuevoRegistro);
    }

    /* access modifiers changed from: private */
    public void guardar_devolucion() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String fecha = calendar.get(5) + "/" + (calendar.get(2) + 1) + "/" + calendar.get(1) + " " + calendar.get(10) + ":" + calendar.get(12);
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id_transaccion", Integer.valueOf(obtener_id_transaccion()));
        nuevoRegistro.put("device_id", this.deviceId);
        nuevoRegistro.put("qr", this.id_qr);
        nuevoRegistro.put("monto_saldo", Integer.valueOf(0));
        nuevoRegistro.put("monto_compra", Integer.valueOf(Integer.parseInt(this.txt_monto_devolucion.getText().toString().replace(".", "").replace("$", "")) * -1));
        nuevoRegistro.put("mto_saldo_final", Integer.valueOf(0));
        nuevoRegistro.put("productos", "DEVOLUCION");
        nuevoRegistro.put("cant_productos", Integer.valueOf(1));
        nuevoRegistro.put("tipo_trx", "DEVOLUCION CAJA");
        nuevoRegistro.put("fecha", fecha);
        this.f17db.insert("Transacciones", null, nuevoRegistro);
    }

    private void abrirDialogoTransacciones() {
        View vista_grilla = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_todas_trx, null);
        List items = new ArrayList();
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_grilla);
        try {
            Cursor c = this.f17db.rawQuery("SELECT qr, productos, monto_compra,  mto_saldo_final, fecha FROM Transacciones order by id_transaccion desc", null);
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
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Transacciones");
        alertDialogBuilder.create().show();
    }

    /* access modifiers changed from: private */
    public String monto_total() {
        int suma_total = 0;
        try {
            Cursor c = this.f17db.rawQuery("SELECT sum(monto_compra) FROM Transacciones", null);
            if (c.moveToFirst()) {
                do {
                    suma_total = c.getInt(0);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            suma_total = 0;
        }
        return Utiles.formatear_a_pesos(suma_total);
    }

    /* access modifiers changed from: private */
    public void limpiar_interfaz() {
        this.btn_registrar_carga.setEnabled(false);
        this.id_qr = "";
        this.txt_monto.setText(formatear_a_pesos(0));
        this.txt_monto_a_recargar.setText(formatear_a_pesos(0));
        this.txt_monto_total.setText(formatear_a_pesos(0));
        this.saldo = 0;
        this.txt_monto_devolucion.setText(formatear_a_pesos(0));
        this.txt_rut.setText("");
        this.label_rut_inv.setText("");
    }

    public void onResume() {
        super.onResume();
        if (this.nfcAdapter != null) {
            enableForegroundDispatchSystem();
        }
        Log.d("PantallaInicial", "onResume");
    }

    public void onPause() {
        super.onPause();
        if (this.nfcAdapter != null) {
            disableForegroundDispatchSystem();
        }
        Log.d("PantallaInicial", "onPause");
    }

    private void enableForegroundDispatchSystem() {
        this.nfcAdapter.enableForegroundDispatch(this, PendingIntent.getActivity(this, 0, new Intent(this, PantallaInicial.class).addFlags(536870912), 0), new IntentFilter[0], null);
    }

    private void disableForegroundDispatchSystem() {
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
            if (this.acercar_nfc) {
                try {
                    Toast.makeText(this, "Grabando Data...", 0).show();
                    String a_encriptar = this.qr_completo + "@" + this.nuevo_saldo;
                    String hash = MCrypt.bytesToHex(new MCrypt(this.hexdump).encrypt(a_encriptar));
                    Log.d(this.TAG, "hash " + hash);
                    Log.d(this.TAG, "a encriptar " + a_encriptar);
                    writeNdefMessage(tag, createNdefMessage(hash));
                    if (this.alertDialog.isShowing()) {
                        this.alertDialog.dismiss();
                    }
                    limpiar_interfaz();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

    private NdefMessage createNdefMessage(String content) {
        return new NdefMessage(new NdefRecord[]{createTextRecord(content)});
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
                return;
            }
            ndef.connect();
            if (!ndef.isWritable()) {
                Toast.makeText(this, "Tag is not writable!", 0).show();
                ndef.close();
                return;
            }
            ndef.writeNdefMessage(ndefMessage);
            ndef.close();
            Toast.makeText(this, "Data Escrita!", 0).show();
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
            Toast.makeText(this, "Data Escrita!", 0).show();
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }

    /* access modifiers changed from: private */
    public void abrirDialogoTexto(String texto) {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText(texto);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PantallaInicial.this.limpiar_interfaz();
            }
        }).setTitle("Confirmación");
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    private void abrirDialogoNFC() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText("Encender el NFC");
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PantallaInicial.this.verificarNFC();
            }
        }).setTitle("Confirmación");
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    /* access modifiers changed from: private */
    public void verificarNFC() {
        if (!this.nfcAdapter.isEnabled()) {
            this.nfc_activo = false;
            abrirDialogoNFC();
            return;
        }
        this.nfc_activo = true;
    }
}
