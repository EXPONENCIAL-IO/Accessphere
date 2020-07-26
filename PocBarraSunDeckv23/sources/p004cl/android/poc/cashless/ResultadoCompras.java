package p004cl.android.poc.cashless;

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
import android.net.ConnectivityManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.p003v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import p004cl.android.poc.cashless.clases.CarroComprasAdapter;
import p004cl.android.poc.cashless.clases.MCrypt;
import p004cl.android.poc.cashless.clases.Producto;
import p004cl.android.poc.cashless.clases.Utiles;

/* renamed from: cl.android.poc.cashless.ResultadoCompras */
public class ResultadoCompras extends ActionBarActivity {
    String TAG = "BARRA POC";
    boolean acercar_nfc = false;
    AlertDialog alertDialog;
    ImageButton btn_delete;
    ImageButton btn_home;
    ImageButton btn_home_qr;
    ImageButton btn_next;
    int cant_productos = 0;
    GridView carro_compras;
    RelativeLayout contedeor_productos;
    RelativeLayout contenedor_respuesta;
    RelativeLayout contenedor_respuesta_no_qr;
    final Context context = this;

    /* renamed from: db */
    private SQLiteDatabase f14db;
    String deviceId = "";
    Editor editor = null;
    GridView gridofertas;
    String hexdump = new String();
    String id_qr = "";
    ArrayList<Producto> itemsCarroCompras;
    List itemsProductos;
    String lista_productos = "";
    String medio_lectura = "";
    TextView monto;
    int monto_compra = 0;
    String monto_nfc = "";
    String monto_pulcera = "";
    int monto_saldo = 0;
    TextView mto_cuenta;
    TextView mto_saldo;
    TextView mto_saldo_final;
    NfcAdapter nfcAdapter;
    boolean nfc_activo = false;
    SharedPreferences prefs;
    ProgressDialog progressDialog;
    String qr_completo = "";
    int response_code = 0;
    boolean soporta_nfc = true;
    String texto_trx = "";
    boolean trx_ok = false;
    TextView txt_saldo_final;
    String url_servicios;
    Verificar_Saldo verificar_saldo = null;

    /* renamed from: cl.android.poc.cashless.ResultadoCompras$Verificar_Saldo */
    private class Verificar_Saldo extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Verificar_Saldo() {
            this.progressDialog = new ProgressDialog(ResultadoCompras.this.context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.progressDialog.setMessage("Realizando Compra...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            realiza_transaccion();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String unused) {
            this.progressDialog.dismiss();
            TextView texto_error = (TextView) ResultadoCompras.this.findViewById(C0200R.C0202id.txt_qr_not_found);
            ResultadoCompras.this.lista_productos = "";
            ResultadoCompras.this.cant_productos = 0;
            ResultadoCompras.this.editor.putString("texto_ultima_trx", "ERROR");
            ResultadoCompras.this.editor.commit();
            if (ResultadoCompras.this.response_code == 0) {
                Toast.makeText(ResultadoCompras.this.context, "Error al realizar la operacion", 1).show();
                return;
            }
            switch (ResultadoCompras.this.response_code) {
                case 202:
                    ResultadoCompras.this.trx_ok = true;
                    ResultadoCompras.this.contenedor_respuesta.setBackgroundResource(C0200R.color.color_ok);
                    ResultadoCompras.this.mto_saldo.setText(Utiles.formatear_a_pesos(ResultadoCompras.this.monto_saldo));
                    ResultadoCompras.this.mto_cuenta.setText(Utiles.formatear_a_pesos(ResultadoCompras.this.monto_compra));
                    ResultadoCompras.this.mto_saldo_final.setText(Utiles.formatear_a_pesos(ResultadoCompras.this.monto_saldo - ResultadoCompras.this.monto_compra));
                    ResultadoCompras.this.cant_productos = ResultadoCompras.this.itemsCarroCompras.size() + 1;
                    for (int i = 0; i < ResultadoCompras.this.itemsCarroCompras.size(); i++) {
                        Producto prod = (Producto) ResultadoCompras.this.itemsCarroCompras.get(i);
                        ResultadoCompras.this.texto_trx += prod.getNombre_producto() + "\n";
                        ResultadoCompras.this.lista_productos += prod.getNombre_producto() + " -- ";
                    }
                    ResultadoCompras.this.texto_trx += "Monto Compra = " + Utiles.formatear_a_pesos(ResultadoCompras.this.monto_compra) + "\n";
                    ResultadoCompras.this.editor.putString("texto_ultima_trx", ResultadoCompras.this.texto_trx);
                    ResultadoCompras.this.editor.commit();
                    ResultadoCompras.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter_disabled);
                    ResultadoCompras.this.btn_delete.setImageResource(C0200R.C0201drawable.ico_delete_disabled);
                    ResultadoCompras.this.btn_next.setEnabled(false);
                    ResultadoCompras.this.btn_delete.setEnabled(false);
                    ResultadoCompras.this.guardar_carga();
                    if (ResultadoCompras.this.nfc_activo) {
                        ResultadoCompras.this.acercar_nfc = true;
                        ResultadoCompras.this.abrirDialogoTexto("Acercar el Tag al dispositivo");
                        return;
                    }
                    return;
                case 400:
                    ResultadoCompras.this.trx_ok = true;
                    texto_error.setText("ESTE DISPOSITIVO NO ESTA \n REGISTRADO");
                    ResultadoCompras.this.contenedor_respuesta.setVisibility(4);
                    ResultadoCompras.this.contenedor_respuesta_no_qr.setVisibility(0);
                    ResultadoCompras.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter_disabled);
                    ResultadoCompras.this.btn_delete.setImageResource(C0200R.C0201drawable.ico_delete_disabled);
                    ResultadoCompras.this.btn_next.setEnabled(false);
                    ResultadoCompras.this.btn_delete.setEnabled(false);
                    return;
                case 404:
                    ResultadoCompras.this.trx_ok = true;
                    texto_error.setText("ESTE CÓDIGO QR NO ESTÁ \n REGISTRADO");
                    ResultadoCompras.this.contenedor_respuesta.setVisibility(4);
                    ResultadoCompras.this.contenedor_respuesta_no_qr.setVisibility(0);
                    ResultadoCompras.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter_disabled);
                    ResultadoCompras.this.btn_delete.setImageResource(C0200R.C0201drawable.ico_delete_disabled);
                    ResultadoCompras.this.btn_next.setEnabled(false);
                    ResultadoCompras.this.btn_delete.setEnabled(false);
                    return;
                case 406:
                    ResultadoCompras.this.trx_ok = false;
                    ResultadoCompras.this.contenedor_respuesta.setBackgroundResource(C0200R.color.color_nok);
                    ResultadoCompras.this.txt_saldo_final.setText("SALDO FALTANTE");
                    ResultadoCompras.this.mto_saldo.setText(Utiles.formatear_a_pesos(ResultadoCompras.this.monto_saldo));
                    ResultadoCompras.this.mto_cuenta.setText(Utiles.formatear_a_pesos(ResultadoCompras.this.monto_compra));
                    ResultadoCompras.this.mto_saldo_final.setText(Utiles.formatear_a_pesos((ResultadoCompras.this.monto_saldo - ResultadoCompras.this.monto_compra) * -1));
                    ResultadoCompras.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter_disabled);
                    ResultadoCompras.this.btn_delete.setImageResource(C0200R.C0201drawable.ico_delete_disabled);
                    ResultadoCompras.this.btn_next.setEnabled(false);
                    ResultadoCompras.this.btn_delete.setEnabled(false);
                    return;
                case 412:
                    ResultadoCompras.this.trx_ok = true;
                    texto_error.setText("NO PUEDES CANJEAR ESTE \n REGALO");
                    ResultadoCompras.this.contenedor_respuesta.setVisibility(4);
                    ResultadoCompras.this.contenedor_respuesta_no_qr.setVisibility(0);
                    ResultadoCompras.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter_disabled);
                    ResultadoCompras.this.btn_delete.setImageResource(C0200R.C0201drawable.ico_delete_disabled);
                    ResultadoCompras.this.btn_next.setEnabled(false);
                    ResultadoCompras.this.btn_delete.setEnabled(false);
                    return;
                default:
                    Toast.makeText(ResultadoCompras.this.context, "Error (" + ResultadoCompras.this.response_code + ") al realizar la operacion", 1).show();
                    return;
            }
        }

        private void realiza_transaccion() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://" + ResultadoCompras.this.url_servicios + "/event/api/transaction/");
            post.setHeader("content-type", "application/json");
            try {
                JSONObject dato = new JSONObject();
                Log.d("ResultadoWS", "qr_code : " + ResultadoCompras.this.id_qr);
                Log.d("ResultadoWS", "amount : " + ResultadoCompras.this.monto_compra);
                Log.d("ResultadoWS", "device_id : " + ResultadoCompras.this.deviceId);
                dato.put("qr_code", ResultadoCompras.this.id_qr);
                dato.put("amount", ResultadoCompras.this.monto_compra);
                dato.put("device_id", ResultadoCompras.this.deviceId);
                JSONArray product_array = new JSONArray();
                for (int i = 0; i < ResultadoCompras.this.itemsCarroCompras.size(); i++) {
                    JSONObject dato_ident = new JSONObject();
                    Producto prod = (Producto) ResultadoCompras.this.itemsCarroCompras.get(i);
                    dato_ident.put("id", prod.getId_producto());
                    dato_ident.put("name", prod.getNombre_producto());
                    dato_ident.put("price", prod.getMonto());
                    product_array.put(dato_ident);
                }
                dato.put("products", product_array);
                Log.d("ResultadoWS", "Ws creado : " + dato.toString());
                post.setEntity(new StringEntity(dato.toString()));
                HttpResponse resp = httpClient.execute(post);
                Log.d("ResultadoWS", "Recibi del ws : " + EntityUtils.toString(resp.getEntity()));
                ResultadoCompras.this.response_code = resp.getStatusLine().getStatusCode();
                Log.d("ResultadoWS", "Response code : " + ResultadoCompras.this.response_code);
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                ex.printStackTrace();
                ResultadoCompras.this.response_code = 0;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0200R.layout.activity_resultado_compras);
        Bundle extras = getIntent().getExtras();
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService("phone");
        this.deviceId = new UUID((long) ("" + Secure.getString(getContentResolver(), "android_id")).hashCode(), (((long) ("" + tm.getDeviceId()).hashCode()) << 32) | ((long) ("" + tm.getSimSerialNumber()).hashCode())).toString();
        Log.v("ResultadoCompras", "Id dispositivo --> " + this.deviceId);
        this.prefs = getSharedPreferences("Preferencias_barra", 0);
        this.editor = this.prefs.edit();
        this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
        if (extras != null) {
            this.qr_completo = extras.getString("qr_completo");
            this.monto_saldo = extras.getInt("monto_saldo");
            this.monto_compra = extras.getInt("monto_compra");
            this.id_qr = extras.getString("id_qr");
            this.itemsCarroCompras = extras.getParcelableArrayList("carro_compras");
            for (int i = 0; i < this.itemsCarroCompras.size(); i++) {
                Producto producto = (Producto) this.itemsCarroCompras.get(i);
            }
        }
        this.progressDialog = new ProgressDialog(this.context);
        this.gridofertas = (GridView) findViewById(C0200R.C0202id.grilla_ofertas);
        this.carro_compras = (GridView) findViewById(C0200R.C0202id.carro_de_compras);
        this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
        this.contedeor_productos = (RelativeLayout) findViewById(C0200R.C0202id.contedeor_productos);
        this.contenedor_respuesta = (RelativeLayout) findViewById(C0200R.C0202id.contenedor_respuesta);
        this.contenedor_respuesta_no_qr = (RelativeLayout) findViewById(C0200R.C0202id.contenedor_respuesta_qr_no);
        this.monto = (TextView) findViewById(C0200R.C0202id.txt_Monto);
        this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
        this.btn_next = (ImageButton) findViewById(C0200R.C0202id.btn_next);
        this.btn_delete = (ImageButton) findViewById(C0200R.C0202id.btn_delete);
        this.btn_home = (ImageButton) findViewById(C0200R.C0202id.btn_home);
        this.btn_home_qr = (ImageButton) findViewById(C0200R.C0202id.btn_home_qr);
        this.txt_saldo_final = (TextView) findViewById(C0200R.C0202id.txt_saldo_final);
        this.mto_saldo = (TextView) findViewById(C0200R.C0202id.mto_saldo);
        this.mto_cuenta = (TextView) findViewById(C0200R.C0202id.mto_cuenta);
        this.mto_saldo_final = (TextView) findViewById(C0200R.C0202id.mto_saldo_final);
        this.contedeor_productos.setVisibility(4);
        this.contenedor_respuesta.setVisibility(0);
        this.contenedor_respuesta_no_qr.setVisibility(4);
        this.verificar_saldo = new Verificar_Saldo();
        if (checkInternetConnection()) {
            this.verificar_saldo.execute(new String[0]);
        } else {
            Toast.makeText(this.context, "No Dispones de conexion a Internet", 1).show();
        }
        this.btn_home.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ResultadoCompras.this.startActivity(new Intent(ResultadoCompras.this, SeleccionProductos.class));
                ResultadoCompras.this.finish();
            }
        });
        this.btn_home_qr.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ResultadoCompras.this.startActivity(new Intent(ResultadoCompras.this, SeleccionProductos.class));
                ResultadoCompras.this.finish();
            }
        });
        this.carro_compras.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                if (!ResultadoCompras.this.trx_ok) {
                    ResultadoCompras.this.eliminar_producto(position);
                    ResultadoCompras.this.btn_next.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (ResultadoCompras.this.itemsCarroCompras.size() > 0) {
                                ResultadoCompras.this.progressDialog.setMessage("Cargando Lector...");
                                ResultadoCompras.this.progressDialog.setCancelable(false);
                                ResultadoCompras.this.progressDialog.show();
                                Intent intent = new Intent(ResultadoCompras.this, ScannerActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelableArrayList("carro_compras", ResultadoCompras.this.itemsCarroCompras);
                                intent.putExtras(bundle);
                                intent.putExtra("monto_compra", ResultadoCompras.this.monto_compra);
                                ResultadoCompras.this.startActivity(intent);
                                ResultadoCompras.this.finish();
                                return;
                            }
                            Toast.makeText(ResultadoCompras.this.context, "Debe elegir a lo menos un producto", 1).show();
                        }
                    });
                }
            }
        });
        this.f14db = new TransaccionSQLiteHelper(this.context, "DBTransacciones", null, 1).getWritableDatabase();
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

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService("connectivity");
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        Log.v("statusInternet", "No hay internet");
        return false;
    }

    /* access modifiers changed from: private */
    public void eliminar_producto(int position) {
        Producto prod = (Producto) this.itemsCarroCompras.get(position);
        this.itemsCarroCompras.remove(position);
        this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
        this.monto_compra -= prod.getMonto();
        this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
        if (this.monto_compra <= this.monto_saldo) {
            this.contenedor_respuesta.setBackgroundResource(C0200R.color.holo_light_blue);
            this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter);
            this.btn_delete.setImageResource(C0200R.C0201drawable.ico_delete);
            this.btn_next.setEnabled(true);
            this.btn_delete.setEnabled(true);
        }
    }

    private int obtener_id_transaccion() {
        int id_transaccion = 0;
        Cursor c = this.f14db.rawQuery("SELECT max(id_transaccion) + 1 FROM Transacciones", null);
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
        nuevoRegistro.put("monto_saldo", Integer.valueOf(this.monto_saldo));
        nuevoRegistro.put("monto_compra", Integer.valueOf(this.monto_compra));
        nuevoRegistro.put("mto_saldo_final", Integer.valueOf(this.monto_saldo - this.monto_compra));
        nuevoRegistro.put("productos", this.lista_productos);
        nuevoRegistro.put("cant_productos", Integer.valueOf(this.cant_productos));
        nuevoRegistro.put("tipo_trx", "COMPRA BARRA");
        nuevoRegistro.put("fecha", fecha);
        this.f14db.insert("Transacciones", null, nuevoRegistro);
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
        this.nfcAdapter.enableForegroundDispatch(this, PendingIntent.getActivity(this, 0, new Intent(this, ResultadoCompras.class).addFlags(536870912), 0), new IntentFilter[0], null);
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
                    String a_encriptar = this.qr_completo + "@" + (this.monto_saldo - this.monto_compra);
                    String hash = MCrypt.bytesToHex(new MCrypt(this.hexdump).encrypt(a_encriptar));
                    Log.d(this.TAG, "hash " + hash);
                    Log.d(this.TAG, "a encriptar " + a_encriptar);
                    writeNdefMessage(tag, createNdefMessage(hash));
                    if (this.alertDialog.isShowing()) {
                        this.alertDialog.dismiss();
                    }
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
        View vista_config = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0200R.C0202id.texto_cambio)).setText(texto);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Confirmación");
        this.alertDialog = alertDialogBuilder.create();
        this.alertDialog.show();
    }

    private void abrirDialogoNFC() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_cambio_modo, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0200R.C0202id.texto_cambio)).setText("Encender el NFC");
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ResultadoCompras.this.verificarNFC();
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
