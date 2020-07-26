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
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Date;
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

/* renamed from: cl.android.poc.pocrecarga.PrincipalActivity */
public class PrincipalActivity extends ActionBarActivity {
    String TAG = "Caja POC";
    boolean acercar_nfc = false;
    AlertDialog alertDialog;
    AutoCompleteTextView autocomplete_email;
    ImageButton btn_modo;
    ImageButton btn_registrar_carga;
    ImageButton btn_reiniciar_scan;
    ImageButton btn_ultima_trx;
    Boolean con_voucher = Boolean.valueOf(false);
    final Context context = this;

    /* renamed from: db */
    private SQLiteDatabase f19db;
    String deviceId = "";
    Editor editor = null;
    String email;
    EditText email_1;
    String email_extencion;
    String error = "";
    boolean hay_data = false;
    String hexdump = new String();
    String id_qr = "";
    ImageView img_modo;
    ImageView img_qr;
    Bitmap mBitmap = null;
    String medio_lectura = "";
    boolean modo_recarga = true;
    String monto = "";
    EditText monto_recarga;
    TextView monto_recarga_agrandado;
    NfcAdapter nfcAdapter;
    boolean nfc_activo = false;
    int nuevo_saldo = 0;
    Obtener_Usuario obetener_user = null;
    SharedPreferences prefs;
    ProgressDialog progressDialog;
    String qr_completo = "";
    Resgistrar_saldo resgistrar_saldo = null;
    int response_code = 0;
    String rut;
    int saldo;
    TextView saldo_en_cuenta;
    boolean soporta_nfc = true;

    /* renamed from: t2 */
    TextWatcher f20t2 = null;
    Spinner tipo_pago;
    TextView titulo_modo;
    TextView titulo_monto_recarga;

    /* renamed from: tt */
    TextWatcher f21tt = null;
    EditText txt_rut;
    String url_servicios;
    EditText voucher;

    /* renamed from: cl.android.poc.pocrecarga.PrincipalActivity$Obtener_Usuario */
    private class Obtener_Usuario extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Obtener_Usuario() {
            this.progressDialog = new ProgressDialog(PrincipalActivity.this.context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.progressDialog.setMessage("Cargando Datos...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            PrincipalActivity.this.datos_ususario();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String unused) {
            this.progressDialog.dismiss();
            if (PrincipalActivity.this.response_code == 0) {
                Toast.makeText(PrincipalActivity.this.context, "Error al realizar la operacion", 1).show();
                PrincipalActivity.this.modo_recarga();
            } else if (PrincipalActivity.this.hay_data) {
                PrincipalActivity.this.saldo_en_cuenta.setText(PrincipalActivity.formatear_a_pesos((long) PrincipalActivity.this.saldo));
                Log.d("ResultadoWS", "Rut recibido : " + PrincipalActivity.this.rut);
                PrincipalActivity.this.txt_rut.setText(Rut.formatear(PrincipalActivity.this.rut));
                PrincipalActivity.this.txt_rut.setEnabled(false);
                PrincipalActivity.this.email_1.setText(PrincipalActivity.this.email);
                PrincipalActivity.this.email_1.setEnabled(false);
                PrincipalActivity.this.autocomplete_email.setText(PrincipalActivity.this.email_extencion);
                PrincipalActivity.this.autocomplete_email.setEnabled(false);
                if (PrincipalActivity.this.modo_recarga) {
                    PrincipalActivity.this.modo_recarga();
                } else if (PrincipalActivity.this.saldo > 0) {
                    PrincipalActivity.this.modo_devolucion();
                } else {
                    PrincipalActivity.this.modo_recarga();
                }
            } else {
                switch (PrincipalActivity.this.response_code) {
                    case 404:
                        PrincipalActivity.this.hay_data = false;
                        PrincipalActivity.this.saldo_en_cuenta.setText(PrincipalActivity.formatear_a_pesos(0));
                        PrincipalActivity.this.txt_rut.setText("");
                        PrincipalActivity.this.txt_rut.setEnabled(true);
                        PrincipalActivity.this.email_1.setText("");
                        PrincipalActivity.this.email_1.setEnabled(true);
                        PrincipalActivity.this.autocomplete_email.setText("");
                        PrincipalActivity.this.autocomplete_email.setEnabled(true);
                        Toast.makeText(PrincipalActivity.this.context, "QR no está registrado en el sistema, favor registrarlo", 1).show();
                        PrincipalActivity.this.modo_recarga();
                        return;
                    default:
                        PrincipalActivity.this.hay_data = false;
                        PrincipalActivity.this.saldo_en_cuenta.setText(PrincipalActivity.formatear_a_pesos(0));
                        PrincipalActivity.this.txt_rut.setText("");
                        PrincipalActivity.this.txt_rut.setEnabled(true);
                        PrincipalActivity.this.email_1.setText("");
                        PrincipalActivity.this.email_1.setEnabled(true);
                        PrincipalActivity.this.autocomplete_email.setText("");
                        PrincipalActivity.this.autocomplete_email.setEnabled(true);
                        Toast.makeText(PrincipalActivity.this.context, "Error (" + PrincipalActivity.this.response_code + ") al realizar la operacion", 1).show();
                        PrincipalActivity.this.modo_recarga();
                        return;
                }
            }
        }
    }

    /* renamed from: cl.android.poc.pocrecarga.PrincipalActivity$Resgistrar_saldo */
    private class Resgistrar_saldo extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Resgistrar_saldo() {
            this.progressDialog = new ProgressDialog(PrincipalActivity.this.context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.progressDialog.setMessage("Registrando Saldo...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            if (!PrincipalActivity.this.hay_data) {
                PrincipalActivity.this.registrar_usuario();
            } else if (PrincipalActivity.this.modo_recarga) {
                PrincipalActivity.this.registrar_carga();
            } else {
                PrincipalActivity.this.registrar_devolucion();
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
            if (PrincipalActivity.this.response_code == 0) {
                Toast.makeText(PrincipalActivity.this.context, "Error al realizar la operacion", 1).show();
                return;
            }
            switch (PrincipalActivity.this.response_code) {
                case 200:
                    PrincipalActivity.this.hay_data = true;
                    PrincipalActivity.this.confirmacion_ok();
                    PrincipalActivity.this.guardar_carga();
                    return;
                case 202:
                    PrincipalActivity.this.hay_data = true;
                    PrincipalActivity.this.nuevo_saldo = 0;
                    PrincipalActivity.this.confirmacion_ok_dev();
                    PrincipalActivity.this.guardar_devolucion();
                    return;
                case 404:
                    PrincipalActivity.this.hay_data = false;
                    PrincipalActivity.this.saldo_en_cuenta.setText(PrincipalActivity.formatear_a_pesos(0));
                    PrincipalActivity.this.txt_rut.setText("");
                    PrincipalActivity.this.email_1.setText("");
                    PrincipalActivity.this.autocomplete_email.setText("");
                    Toast.makeText(PrincipalActivity.this.context, "QR no está registrado en el sistema, favor registrarlo", 1).show();
                    return;
                case 500:
                    PrincipalActivity.this.hay_data = false;
                    PrincipalActivity.this.saldo_en_cuenta.setText(PrincipalActivity.formatear_a_pesos(0));
                    PrincipalActivity.this.txt_rut.setText("");
                    PrincipalActivity.this.email_1.setText("");
                    PrincipalActivity.this.autocomplete_email.setText("");
                    Toast.makeText(PrincipalActivity.this.context, "Error interno del servidor", 1).show();
                    return;
                default:
                    if (PrincipalActivity.this.error.equalsIgnoreCase("")) {
                        Toast.makeText(PrincipalActivity.this.context, "Error (" + PrincipalActivity.this.response_code + ") al realizar la operacion", 1).show();
                        return;
                    } else {
                        Toast.makeText(PrincipalActivity.this.context, "Error (" + PrincipalActivity.this.response_code + ") : " + PrincipalActivity.this.error, 1).show();
                        return;
                    }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(8);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView((int) C0281R.layout.activity_principal);
        this.img_qr = (ImageView) findViewById(C0281R.C0283id.img_qr);
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService("phone");
        this.deviceId = new UUID((long) ("" + Secure.getString(getContentResolver(), "android_id")).hashCode(), (((long) ("" + tm.getDeviceId()).hashCode()) << 32) | ((long) ("" + tm.getSimSerialNumber()).hashCode())).toString();
        Log.v("PrincipalActivity", "Id dispositivo --> " + this.deviceId);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.qr_completo = extras.getString("qr_completo");
            this.id_qr = extras.getString("id_qr");
            generateQRCode(this.id_qr);
            this.medio_lectura = extras.getString("medio_lectura");
            this.monto = extras.getString("monto");
        }
        this.progressDialog = new ProgressDialog(this.context);
        this.prefs = getSharedPreferences("Preferencias_Caja", 0);
        this.editor = this.prefs.edit();
        this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
        this.modo_recarga = this.prefs.getBoolean("modo_recarga", true);
        this.btn_reiniciar_scan = (ImageButton) findViewById(C0281R.C0283id.btn_reinicio_scan);
        this.btn_registrar_carga = (ImageButton) findViewById(C0281R.C0283id.btn_recargar);
        this.btn_modo = (ImageButton) findViewById(C0281R.C0283id.btn_modo);
        this.btn_ultima_trx = (ImageButton) findViewById(C0281R.C0283id.btn_documento);
        this.img_modo = (ImageView) findViewById(C0281R.C0283id.img_modo);
        this.titulo_modo = (TextView) findViewById(C0281R.C0283id.titulo_modo);
        this.titulo_monto_recarga = (TextView) findViewById(C0281R.C0283id.titulo_monto_recarga);
        this.email_1 = (EditText) findViewById(C0281R.C0283id.edit_email);
        this.monto_recarga = (EditText) findViewById(C0281R.C0283id.edit_monto);
        this.monto_recarga_agrandado = (TextView) findViewById(C0281R.C0283id.textview_monto_recarga);
        this.voucher = (EditText) findViewById(C0281R.C0283id.edit_tipo_pago);
        this.saldo_en_cuenta = (TextView) findViewById(C0281R.C0283id.txt_monto);
        this.tipo_pago = (Spinner) findViewById(C0281R.C0283id.spinner_tipo_pago);
        this.txt_rut = (EditText) findViewById(C0281R.C0283id.edit_rut);
        this.monto_recarga.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !PrincipalActivity.this.hay_data) {
                    if (PrincipalActivity.this.monto_recarga.getText().toString().length() == 0) {
                        Toast.makeText(PrincipalActivity.this.getApplicationContext(), "Error: Campo vacio", 1).show();
                        return;
                    }
                    PrincipalActivity.this.monto_recarga.setText(PrincipalActivity.formatear_a_pesos_sin_signo(Long.parseLong(PrincipalActivity.this.monto_recarga.getText().toString().replace(".", "").replace(" ", ""))));
                    PrincipalActivity.this.monto_recarga_agrandado.setText(PrincipalActivity.formatear_a_pesos(Long.parseLong(PrincipalActivity.this.monto_recarga.getText().toString().replace(".", "").replace(" ", ""))));
                }
            }
        });
        this.f20t2 = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PrincipalActivity.this.monto_recarga.removeTextChangedListener(PrincipalActivity.this.f20t2);
                if (PrincipalActivity.this.monto_recarga.getText().toString().length() == 0) {
                    PrincipalActivity.this.monto_recarga_agrandado.setText(PrincipalActivity.formatear_a_pesos(0));
                } else if (PrincipalActivity.this.monto_recarga.getText().toString().replace(".", "").replace(" ", "").length() <= 10) {
                    PrincipalActivity.this.monto_recarga.setText(PrincipalActivity.formatear_a_pesos_sin_signo(Long.parseLong(PrincipalActivity.this.monto_recarga.getText().toString().replace(".", "").replace(" ", ""))));
                    PrincipalActivity.this.monto_recarga_agrandado.setText(PrincipalActivity.formatear_a_pesos(Long.parseLong(PrincipalActivity.this.monto_recarga.getText().toString().replace(".", "").replace(" ", ""))));
                } else {
                    PrincipalActivity.this.monto_recarga_agrandado.setText(PrincipalActivity.formatear_a_pesos(0));
                    PrincipalActivity.this.monto_recarga.setText(PrincipalActivity.formatear_a_pesos_sin_signo(0));
                    Toast.makeText(PrincipalActivity.this.context, "Excede el Máximo", 1).show();
                }
                PrincipalActivity.this.monto_recarga.setSelection(PrincipalActivity.this.monto_recarga.getText().toString().length());
                PrincipalActivity.this.monto_recarga.addTextChangedListener(PrincipalActivity.this.f20t2);
            }
        };
        this.monto_recarga.addTextChangedListener(this.f20t2);
        this.f21tt = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                PrincipalActivity.this.txt_rut.removeTextChangedListener(PrincipalActivity.this.f21tt);
                Log.d("TextWatcher", "Ancho de texto antes --> " + PrincipalActivity.this.txt_rut.getText().toString().length());
                PrincipalActivity.this.txt_rut.setText(Rut.formatear(PrincipalActivity.this.txt_rut.getText().toString()));
                Log.d("TextWatcher", "Ancho de texto despues --> " + PrincipalActivity.this.txt_rut.getText().toString().length());
                PrincipalActivity.this.txt_rut.setSelection(PrincipalActivity.this.txt_rut.getText().toString().length());
                PrincipalActivity.this.txt_rut.addTextChangedListener(PrincipalActivity.this.f21tt);
            }
        };
        this.txt_rut.addTextChangedListener(this.f21tt);
        this.txt_rut.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !PrincipalActivity.this.hay_data) {
                    if (PrincipalActivity.this.txt_rut.getText().toString().length() == 0) {
                        Toast.makeText(PrincipalActivity.this.getApplicationContext(), "Error: Campo vacio", 1).show();
                    } else if (!Rut.validar(PrincipalActivity.this.txt_rut.getText().toString())) {
                        Toast.makeText(PrincipalActivity.this.context, "Rut invalido", 1).show();
                        PrincipalActivity.this.txt_rut.setText("");
                    }
                }
            }
        });
        this.autocomplete_email = (AutoCompleteTextView) findViewById(C0281R.C0283id.autocomplete_email);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 17367043, getResources().getStringArray(C0281R.array.emails));
        this.autocomplete_email.setThreshold(1);
        this.autocomplete_email.setAdapter(adapter);
        this.btn_ultima_trx.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PrincipalActivity.this.ultima_trx();
            }
        });
        this.obetener_user = new Obtener_Usuario();
        if (checkInternetConnection()) {
            this.obetener_user.execute(new String[0]);
        } else {
            Toast.makeText(this.context, "No Dispones de conexion a Internet", 1).show();
        }
        this.resgistrar_saldo = new Resgistrar_saldo();
        this.btn_registrar_carga.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (PrincipalActivity.this.monto_recarga.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(PrincipalActivity.this.context, "Debe colocar un monto", 1).show();
                } else if (!PrincipalActivity.this.validarUsuario()) {
                } else {
                    if (PrincipalActivity.this.checkInternetConnection()) {
                        new Resgistrar_saldo().execute(new String[0]);
                    } else {
                        Toast.makeText(PrincipalActivity.this.context, "No Dispones de conexion a Internet", 1).show();
                    }
                }
            }
        });
        this.btn_reiniciar_scan.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PrincipalActivity.this.progressDialog.setMessage("Cargando Lector...");
                PrincipalActivity.this.progressDialog.setCancelable(false);
                PrincipalActivity.this.progressDialog.show();
                PrincipalActivity.this.finish();
            }
        });
        this.tipo_pago.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                if (String.valueOf(PrincipalActivity.this.tipo_pago.getSelectedItem()).equalsIgnoreCase("Transbank")) {
                    PrincipalActivity.this.con_voucher = Boolean.valueOf(true);
                    PrincipalActivity.this.voucher.setEnabled(true);
                    return;
                }
                PrincipalActivity.this.con_voucher = Boolean.valueOf(false);
                PrincipalActivity.this.voucher.setEnabled(false);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.btn_modo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!PrincipalActivity.this.modo_recarga) {
                    PrincipalActivity.this.modal_modo_recarga();
                } else if (PrincipalActivity.this.hay_data) {
                    PrincipalActivity.this.modal_modo_devolucion();
                } else {
                    Toast.makeText(PrincipalActivity.this.context, "QR no está registrado en el sistema, favor registrarlo", 1).show();
                }
            }
        });
        if (this.modo_recarga) {
            modo_recarga();
        } else {
            modo_devolucion();
        }
        this.f19db = new TransaccionSQLiteHelper(this.context, "DBTransacciones", null, 1).getWritableDatabase();
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

    /* access modifiers changed from: private */
    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService("connectivity");
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        Log.v("statusInternet", "No hay internet");
        return false;
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
        if (this.mBitmap != null) {
            this.img_qr.setImageBitmap(this.mBitmap);
        }
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
            this.rut = "";
            if (!respStr.equalsIgnoreCase("")) {
                this.hay_data = true;
                JSONObject respJSON = new JSONObject(respStr);
                this.saldo = respJSON.getInt("balance");
                JSONObject data = respJSON.getJSONObject("party");
                JSONArray identifications = data.getJSONArray("identifications");
                for (int i = 0; i < identifications.length(); i++) {
                    Log.d("Json Productos", "Entre al loop");
                    JSONObject c = identifications.getJSONObject(i);
                    if (c.getString("type").equalsIgnoreCase("rut")) {
                        Log.d("Json Productos", "Entre al loop rut --> " + c.getString("value"));
                        this.rut = c.getString("value");
                    }
                }
                String[] datos_email = data.getJSONObject("user").getString("email").split("@");
                this.email = datos_email[0];
                this.email_extencion = datos_email[1];
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

    public static String formatear_a_pesos(long monto2) {
        String str = "0";
        try {
            return NumberFormat.getCurrencyInstance(new Locale("es", "CL")).format(monto2);
        } catch (Exception e) {
            return "0";
        }
    }

    public static String formatear_a_pesos_sin_signo(long monto2) {
        String str = "0";
        try {
            return NumberFormat.getCurrencyInstance(new Locale("es", "CL")).format(monto2).replace("$", "");
        } catch (Exception e) {
            return "0";
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
                if (PrincipalActivity.this.nfc_activo) {
                    PrincipalActivity.this.acercar_nfc = true;
                    PrincipalActivity.this.abrirDialogoTexto("Acercar el Tag al dispositivo");
                    return;
                }
                PrincipalActivity.this.finish();
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
                if (PrincipalActivity.this.nfc_activo) {
                    PrincipalActivity.this.acercar_nfc = true;
                    PrincipalActivity.this.abrirDialogoTexto("Acercar el Tag al dispositivo");
                    return;
                }
                PrincipalActivity.this.finish();
            }
        }).setTitle("Confirmación");
        alertDialogBuilder.create().show();
    }

    /* access modifiers changed from: private */
    public void modal_modo_devolucion() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText(" Va a pasar al modo DEVOLUCIÓN \n ¿Está seguro?");
        alertDialogBuilder.setCancelable(false).setNegativeButton(" CANCELAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton(" ACEPTAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PrincipalActivity.this.modo_recarga = false;
                PrincipalActivity.this.titulo_modo.setText("DEVOLUCIÓN");
                PrincipalActivity.this.btn_registrar_carga.setImageResource(C0281R.C0282drawable.btn_devolver);
                PrincipalActivity.this.btn_modo.setImageResource(C0281R.C0282drawable.btn_dinero_active);
                PrincipalActivity.this.img_modo.setImageResource(C0281R.C0282drawable.txt_modo_devolucion);
                PrincipalActivity.this.btn_reiniciar_scan.setImageResource(C0281R.C0282drawable.btn_reinicio_devolucion);
                PrincipalActivity.this.titulo_monto_recarga.setText("Monto a devolver");
                PrincipalActivity.this.editor.putBoolean("modo_recarga", false);
                PrincipalActivity.this.editor.commit();
                PrincipalActivity.this.btn_ultima_trx.setVisibility(0);
                PrincipalActivity.this.btn_ultima_trx.setEnabled(true);
                PrincipalActivity.this.voucher.setEnabled(false);
                PrincipalActivity.this.tipo_pago.setEnabled(false);
                PrincipalActivity.this.monto_recarga.setEnabled(false);
                PrincipalActivity.this.monto_recarga.setText(PrincipalActivity.formatear_a_pesos_sin_signo((long) PrincipalActivity.this.saldo));
            }
        }).setTitle("Cambio de Modo");
        alertDialogBuilder.create().show();
    }

    /* access modifiers changed from: private */
    public void modal_modo_recarga() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_cambio)).setText(" Va a pasar al modo RECARGA \n ¿Está seguro?");
        alertDialogBuilder.setCancelable(false).setNegativeButton(" CANCELAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton(" ACEPTAR ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PrincipalActivity.this.modo_recarga = true;
                PrincipalActivity.this.titulo_modo.setText("RECARGA");
                PrincipalActivity.this.btn_registrar_carga.setImageResource(C0281R.C0282drawable.btn_recargar);
                PrincipalActivity.this.btn_modo.setImageResource(C0281R.C0282drawable.btn_dinero);
                PrincipalActivity.this.img_modo.setImageResource(C0281R.C0282drawable.txt_modo_recarga);
                PrincipalActivity.this.btn_reiniciar_scan.setImageResource(C0281R.C0282drawable.btn_reinicio);
                PrincipalActivity.this.titulo_monto_recarga.setText("Monto a recargar");
                PrincipalActivity.this.editor.putBoolean("modo_recarga", true);
                PrincipalActivity.this.editor.commit();
                PrincipalActivity.this.btn_ultima_trx.setVisibility(4);
                PrincipalActivity.this.btn_ultima_trx.setEnabled(false);
                PrincipalActivity.this.voucher.setEnabled(true);
                PrincipalActivity.this.tipo_pago.setEnabled(true);
                PrincipalActivity.this.monto_recarga.setEnabled(true);
                PrincipalActivity.this.monto_recarga.setText(PrincipalActivity.formatear_a_pesos_sin_signo(0));
            }
        }).setTitle("Cambio de Modo");
        alertDialogBuilder.create().show();
    }

    /* access modifiers changed from: private */
    public void modo_devolucion() {
        this.modo_recarga = false;
        this.titulo_modo.setText("DEVOLUCIÓN");
        this.btn_registrar_carga.setImageResource(C0281R.C0282drawable.btn_devolver);
        this.btn_modo.setImageResource(C0281R.C0282drawable.btn_dinero_active);
        this.img_modo.setImageResource(C0281R.C0282drawable.txt_modo_devolucion);
        this.btn_reiniciar_scan.setImageResource(C0281R.C0282drawable.btn_reinicio_devolucion);
        this.titulo_monto_recarga.setText("Monto a devolver");
        this.editor.putBoolean("modo_recarga", false);
        this.editor.commit();
        this.btn_ultima_trx.setVisibility(0);
        this.btn_ultima_trx.setEnabled(true);
        this.voucher.setEnabled(false);
        this.tipo_pago.setEnabled(false);
        this.monto_recarga.setEnabled(false);
        this.monto_recarga.setText(formatear_a_pesos_sin_signo((long) this.saldo));
    }

    /* access modifiers changed from: private */
    public void modo_recarga() {
        this.modo_recarga = true;
        this.titulo_modo.setText("RECARGA");
        this.btn_registrar_carga.setImageResource(C0281R.C0282drawable.btn_recargar);
        this.btn_modo.setImageResource(C0281R.C0282drawable.btn_dinero);
        this.img_modo.setImageResource(C0281R.C0282drawable.txt_modo_recarga);
        this.btn_reiniciar_scan.setImageResource(C0281R.C0282drawable.btn_reinicio);
        this.titulo_monto_recarga.setText("Monto a recargar");
        this.editor.putBoolean("modo_recarga", true);
        this.editor.commit();
        this.btn_ultima_trx.setVisibility(4);
        this.btn_ultima_trx.setEnabled(false);
        this.voucher.setEnabled(true);
        this.tipo_pago.setEnabled(true);
        this.monto_recarga.setEnabled(true);
        this.monto_recarga.setText(formatear_a_pesos_sin_signo(0));
    }

    /* access modifiers changed from: private */
    public void ultima_trx() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0281R.layout.dialogo_ultima_trx, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0281R.C0283id.texto_trx)).setText(this.prefs.getString("texto_ultima_trx", "Sin Historial"));
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Última Transacción");
        alertDialogBuilder.create().show();
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
            dato.put("amount", this.monto_recarga.getText().toString().replace(".", ""));
            dato.put("voucher", this.voucher.getText().toString());
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
            dato.put("amount", this.monto_recarga.getText().toString().replace(".", ""));
            dato.put("device_id", this.deviceId);
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
            if (this.email_1.getText().toString().length() == 0) {
                dato.put("email", "");
            } else {
                dato.put("email", this.email_1.getText().toString() + "@" + this.autocomplete_email.getText().toString());
            }
            dato.put("party_type", "person");
            JSONArray ident = new JSONArray();
            JSONObject dato_ident = new JSONObject();
            dato_ident.put("identification_type", "rut");
            dato_ident.put("identification", this.txt_rut.getText().toString().replace(".", "").replace("-", ""));
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

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 70, stream);
        return Base64.encodeToString(stream.toByteArray(), 2);
    }

    /* access modifiers changed from: private */
    public boolean validarUsuario() {
        String obj = this.txt_rut.getText().toString();
        String obj2 = this.email_1.getText().toString();
        String obj3 = this.autocomplete_email.getText().toString();
        String obj4 = this.voucher.getText().toString();
        return true;
    }

    private int obtener_id_transaccion() {
        int id_transaccion = 0;
        Cursor c = this.f19db.rawQuery("SELECT max(id_transaccion) + 1 FROM Transacciones", null);
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
        nuevoRegistro.put("monto_compra", Integer.valueOf(Integer.parseInt(this.monto_recarga.getText().toString().replace(".", ""))));
        nuevoRegistro.put("mto_saldo_final", Integer.valueOf(this.nuevo_saldo));
        nuevoRegistro.put("productos", "CARGA");
        nuevoRegistro.put("cant_productos", Integer.valueOf(1));
        nuevoRegistro.put("tipo_trx", "CARGA CAJA");
        nuevoRegistro.put("fecha", fecha);
        this.f19db.insert("Transacciones", null, nuevoRegistro);
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
        nuevoRegistro.put("monto_compra", Integer.valueOf(Integer.parseInt(this.monto_recarga.getText().toString().replace(".", "")) * -1));
        nuevoRegistro.put("mto_saldo_final", Integer.valueOf(0));
        nuevoRegistro.put("productos", "DEVOLUCION");
        nuevoRegistro.put("cant_productos", Integer.valueOf(1));
        nuevoRegistro.put("tipo_trx", "DEVOLUCION CAJA");
        nuevoRegistro.put("fecha", fecha);
        this.f19db.insert("Transacciones", null, nuevoRegistro);
    }

    public void onResume() {
        super.onResume();
        if (this.nfcAdapter != null) {
            enableForegroundDispatchSystem();
        }
        Log.d("ScannerActivity", "onResume");
    }

    public void onPause() {
        super.onPause();
        if (this.nfcAdapter != null) {
            disableForegroundDispatchSystem();
        }
        Log.d("ScannerActivity", "onPause");
    }

    private void enableForegroundDispatchSystem() {
        this.nfcAdapter.enableForegroundDispatch(this, PendingIntent.getActivity(this, 0, new Intent(this, PrincipalActivity.class).addFlags(536870912), 0), new IntentFilter[0], null);
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
                    finish();
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
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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
                PrincipalActivity.this.verificarNFC();
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
