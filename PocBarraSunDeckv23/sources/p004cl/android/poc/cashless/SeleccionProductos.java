package p004cl.android.poc.cashless;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.support.p003v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import p004cl.android.poc.cashless.clases.CarroComprasAdapter;
import p004cl.android.poc.cashless.clases.Combo_Producto;
import p004cl.android.poc.cashless.clases.OfertasAdapter;
import p004cl.android.poc.cashless.clases.Producto;
import p004cl.android.poc.cashless.clases.TransaccionesAdapter;
import p004cl.android.poc.cashless.clases.TransaccionesRealizadas;
import p004cl.android.poc.cashless.clases.Utiles;

/* renamed from: cl.android.poc.cashless.SeleccionProductos */
public class SeleccionProductos extends ActionBarActivity {
    String NAME_OF_FOLDER = "Poc_Barra/json_productos";
    ImageButton btn_back_combo;
    Button btn_calculadora;
    ImageButton btn_clear;
    ImageButton btn_next;
    ImageButton btn_next_combo;
    Button btn_ofertas;
    Button btn_productos;
    Button btn_regalos;
    ImageButton btn_ultima_trx;
    ImageButton btn_ultima_trx_2;
    ImageButton btn_ultima_trx_3;
    ImageButton btn_ultima_trx_4;
    GridView carro_compras;
    RelativeLayout contenedor_calculadora;
    RelativeLayout contenedor_ofertas;
    RelativeLayout contenedor_productos;
    RelativeLayout contenedor_regalos;
    final Context context = this;
    boolean creacion_correcta = false;

    /* renamed from: db */
    private SQLiteDatabase f15db;
    String deviceId = "";
    Editor editor = null;
    String error = "";
    GridView gridofertas;
    GridView gridofertas_2;
    GridView grilla_combos;
    GridView grilla_regalos;
    int id_combo = 0;
    ArrayList<Producto> itemsCarroCompras;
    ArrayList<Combo_Producto> itemsComboProducto;
    ArrayList<Producto> itemsCombos;
    ArrayList<Producto> itemsProductos;
    ArrayList<Producto> itemsProductos_2;
    ArrayList<Producto> itemsProductos_3;
    boolean json_creado = false;
    ArrayList<Producto> maestroProductos;
    HashMap<String, Integer> max_min;
    TextView monto;
    int monto_compra = 0;
    String monto_pulcera = "";
    int monto_saldo = 0;
    TextView mto_calculadora;
    TextView mto_saldo_final;
    int numero1 = 0;
    SharedPreferences prefs;
    ProgressDialog progressDialog;
    int quantity = 0;
    int response_code = 0;
    int resultado = 0;
    String url_servicios;

    /* renamed from: cl.android.poc.cashless.SeleccionProductos$Escribir_Json */
    private class Escribir_Json extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Escribir_Json() {
            this.progressDialog = new ProgressDialog(SeleccionProductos.this.context);
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            this.progressDialog.setMessage("Sincronizando Productos...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... arg0) {
            creacion_json();
            return null;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String unused) {
            if (SeleccionProductos.this.creacion_correcta) {
                SeleccionProductos.this.cargar_productos();
                this.progressDialog.dismiss();
                SeleccionProductos.this.editor.putBoolean("json_creado", true);
                SeleccionProductos.this.editor.commit();
                SeleccionProductos.this.limpiar_pantalla();
                if (SeleccionProductos.this.response_code == 400) {
                    Toast.makeText(SeleccionProductos.this.context, SeleccionProductos.this.error, 1).show();
                    return;
                }
                return;
            }
            this.progressDialog.dismiss();
            Toast.makeText(SeleccionProductos.this.context, "Error al cargar los productos, se usara base por defecto", 1).show();
            SeleccionProductos.this.editor.putBoolean("json_creado", false);
            SeleccionProductos.this.editor.commit();
            SeleccionProductos.this.limpiar_pantalla();
            SeleccionProductos.this.cargar_productos();
            if (SeleccionProductos.this.response_code == 400) {
                Toast.makeText(SeleccionProductos.this.context, SeleccionProductos.this.error, 1).show();
            }
        }

        @TargetApi(19)
        private void creacion_json() {
            SeleccionProductos.this.response_code = 0;
            SeleccionProductos.this.error = "";
            HttpClient httpClient = new DefaultHttpClient();
            String url = "http://" + SeleccionProductos.this.url_servicios + "/event/api/synchronization/";
            Log.d("ResultadoWS", "device_id : " + SeleccionProductos.this.deviceId);
            List<BasicNameValuePair> params = new LinkedList<>();
            params.add(new BasicNameValuePair("device_id", SeleccionProductos.this.deviceId));
            Log.d("ResultadoWS", "Url de peticion : " + url + "?" + URLEncodedUtils.format(params, "utf-8"));
            HttpGet del = new HttpGet(url + "?" + URLEncodedUtils.format(params, "utf-8"));
            del.setHeader("content-type", "application/json");
            try {
                HttpResponse resp = httpClient.execute(del);
                String respStr = EntityUtils.toString(resp.getEntity());
                Log.d("ResultadoWS", "Recibi del ws : " + respStr);
                SeleccionProductos.this.response_code = resp.getStatusLine().getStatusCode();
                JSONObject respJSON = new JSONObject(respStr);
                File folderPathByEquipment = new File(Environment.getExternalStorageDirectory(), SeleccionProductos.this.NAME_OF_FOLDER);
                folderPathByEquipment.mkdirs();
                File dir = new File(folderPathByEquipment.getAbsolutePath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileWriter file = new FileWriter(new File(dir, "json_productos.json"));
                Throwable th = null;
                try {
                    file.write(respJSON.toString());
                    System.out.println("Successfully Copied JSON Object to File...");
                    if (file != null) {
                        if (0 != 0) {
                            try {
                                file.close();
                            } catch (Throwable x2) {
                                null.addSuppressed(x2);
                            }
                        } else {
                            file.close();
                        }
                    }
                    SeleccionProductos.this.creacion_correcta = true;
                    if (SeleccionProductos.this.response_code == 400 && !respStr.equalsIgnoreCase("")) {
                        SeleccionProductos.this.error = respJSON.getString("error");
                        return;
                    }
                    return;
                } catch (Throwable th2) {
                    Throwable th3 = th2;
                    th = r15;
                    th = th3;
                }
                throw th;
                if (file != null) {
                    if (th != null) {
                        try {
                            file.close();
                        } catch (Throwable x22) {
                            th.addSuppressed(x22);
                        }
                    } else {
                        file.close();
                    }
                }
                throw th;
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                ex.printStackTrace();
                SeleccionProductos.this.creacion_correcta = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0200R.layout.activity_seleccion_productos);
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService("phone");
        this.deviceId = new UUID((long) ("" + Secure.getString(getContentResolver(), "android_id")).hashCode(), (((long) ("" + tm.getDeviceId()).hashCode()) << 32) | ((long) ("" + tm.getSimSerialNumber()).hashCode())).toString();
        this.prefs = getSharedPreferences("Preferencias_barra", 0);
        this.editor = this.prefs.edit();
        this.progressDialog = new ProgressDialog(this.context);
        this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
        this.json_creado = this.prefs.getBoolean("json_creado", false);
        this.gridofertas = (GridView) findViewById(C0200R.C0202id.grilla_ofertas);
        this.gridofertas_2 = (GridView) findViewById(C0200R.C0202id.grilla_ofertas_2);
        this.grilla_combos = (GridView) findViewById(C0200R.C0202id.grilla_combos);
        this.grilla_regalos = (GridView) findViewById(C0200R.C0202id.grilla_regalos);
        this.carro_compras = (GridView) findViewById(C0200R.C0202id.carro_de_compras);
        this.contenedor_ofertas = (RelativeLayout) findViewById(C0200R.C0202id.contenedor_ofertas);
        this.contenedor_productos = (RelativeLayout) findViewById(C0200R.C0202id.contenedor_productos);
        this.contenedor_calculadora = (RelativeLayout) findViewById(C0200R.C0202id.contenedor_calculadora);
        this.contenedor_regalos = (RelativeLayout) findViewById(C0200R.C0202id.contenedor_regalos);
        this.monto = (TextView) findViewById(C0200R.C0202id.txt_Monto);
        this.btn_next = (ImageButton) findViewById(C0200R.C0202id.btn_next);
        this.btn_clear = (ImageButton) findViewById(C0200R.C0202id.btn_delete);
        this.btn_ofertas = (Button) findViewById(C0200R.C0202id.btn_ofertas);
        this.btn_productos = (Button) findViewById(C0200R.C0202id.btn_productos);
        this.btn_calculadora = (Button) findViewById(C0200R.C0202id.btn_calculadora);
        this.btn_regalos = (Button) findViewById(C0200R.C0202id.btn_regalos);
        this.btn_ultima_trx = (ImageButton) findViewById(C0200R.C0202id.btn_documento);
        this.btn_ultima_trx_2 = (ImageButton) findViewById(C0200R.C0202id.btn_documento_2);
        this.btn_ultima_trx_3 = (ImageButton) findViewById(C0200R.C0202id.btn_documento_calc);
        this.btn_ultima_trx_4 = (ImageButton) findViewById(C0200R.C0202id.btn_documento_reg);
        this.btn_next_combo = (ImageButton) findViewById(C0200R.C0202id.btn_next_combo);
        this.btn_back_combo = (ImageButton) findViewById(C0200R.C0202id.btn_back_combo);
        this.maestroProductos = new ArrayList<>();
        this.itemsProductos = new ArrayList<>();
        this.itemsProductos_2 = new ArrayList<>();
        this.itemsProductos_3 = new ArrayList<>();
        this.itemsComboProducto = new ArrayList<>();
        this.itemsCarroCompras = new ArrayList<>();
        if (this.json_creado) {
            cargar_productos();
        } else if (checkInternetConnection()) {
            new Escribir_Json().execute(new String[0]);
        } else {
            Toast.makeText(this.context, "No Dispones de conexion a Internet", 1).show();
        }
        this.gridofertas.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                SeleccionProductos.this.agregar_producto(position);
            }
        });
        this.gridofertas_2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                SeleccionProductos.this.agregar_producto_grilla_2(position);
            }
        });
        this.grilla_regalos.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                SeleccionProductos.this.agregar_producto_grilla_3(position);
            }
        });
        this.grilla_combos.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                SeleccionProductos.this.agregar_producto_combo(position);
            }
        });
        this.btn_ultima_trx.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.ultima_trx();
            }
        });
        this.btn_ultima_trx_2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.ultima_trx();
            }
        });
        this.btn_ultima_trx_3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.ultima_trx();
            }
        });
        this.btn_ultima_trx_4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.ultima_trx();
            }
        });
        this.btn_next.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SeleccionProductos.this.itemsCarroCompras.size() > 0) {
                    SeleccionProductos.this.progressDialog.setMessage("Cargando Lector...");
                    SeleccionProductos.this.progressDialog.setCancelable(false);
                    SeleccionProductos.this.progressDialog.show();
                    Intent intent = new Intent(SeleccionProductos.this, ScannerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("carro_compras", SeleccionProductos.this.itemsCarroCompras);
                    intent.putExtras(bundle);
                    intent.putExtra("monto_compra", SeleccionProductos.this.monto_compra);
                    SeleccionProductos.this.startActivity(intent);
                    SeleccionProductos.this.finish();
                    return;
                }
                Toast.makeText(SeleccionProductos.this.context, "Debe elegir a lo menos un producto", 1).show();
            }
        });
        this.btn_clear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.itemsCarroCompras.clear();
                SeleccionProductos.this.carro_compras.setAdapter(new CarroComprasAdapter(SeleccionProductos.this.context, SeleccionProductos.this.itemsCarroCompras));
                SeleccionProductos.this.monto_compra = 0;
                SeleccionProductos.this.monto.setText(Utiles.formatear_a_pesos(0));
                SeleccionProductos.this.contenedor_ofertas.setVisibility(0);
                SeleccionProductos.this.contenedor_ofertas.setClickable(true);
                SeleccionProductos.this.contenedor_productos.setVisibility(4);
                SeleccionProductos.this.contenedor_productos.setClickable(false);
                SeleccionProductos.this.contenedor_calculadora.setVisibility(4);
                SeleccionProductos.this.contenedor_calculadora.setClickable(false);
                SeleccionProductos.this.btn_ofertas.setBackgroundResource(C0200R.color.white);
                SeleccionProductos.this.btn_productos.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_calculadora.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_regalos.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.gridofertas_2.setVisibility(0);
                SeleccionProductos.this.grilla_combos.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setEnabled(false);
                SeleccionProductos.this.btn_back_combo.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setImageResource(C0200R.C0201drawable.ico_next_disabled);
                SeleccionProductos.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter);
                SeleccionProductos.this.btn_next.setEnabled(true);
                SeleccionProductos.this.btn_ofertas.setEnabled(true);
                SeleccionProductos.this.btn_productos.setEnabled(true);
                SeleccionProductos.this.btn_calculadora.setEnabled(true);
                SeleccionProductos.this.btn_regalos.setEnabled(true);
                SeleccionProductos.this.carro_compras.setEnabled(true);
            }
        });
        this.carro_compras.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                SeleccionProductos.this.eliminar_producto(position);
            }
        });
        this.btn_ofertas.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.contenedor_ofertas.setVisibility(0);
                SeleccionProductos.this.contenedor_ofertas.setClickable(true);
                SeleccionProductos.this.contenedor_productos.setVisibility(4);
                SeleccionProductos.this.contenedor_productos.setClickable(false);
                SeleccionProductos.this.contenedor_calculadora.setVisibility(4);
                SeleccionProductos.this.contenedor_calculadora.setClickable(false);
                SeleccionProductos.this.contenedor_regalos.setVisibility(4);
                SeleccionProductos.this.contenedor_regalos.setClickable(false);
                SeleccionProductos.this.btn_ofertas.setBackgroundResource(C0200R.color.white);
                SeleccionProductos.this.btn_productos.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_calculadora.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_regalos.setBackgroundResource(C0200R.color.fondo_botones);
            }
        });
        this.btn_productos.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.contenedor_ofertas.setVisibility(4);
                SeleccionProductos.this.contenedor_ofertas.setClickable(false);
                SeleccionProductos.this.contenedor_productos.setVisibility(0);
                SeleccionProductos.this.contenedor_productos.setClickable(true);
                SeleccionProductos.this.contenedor_calculadora.setVisibility(4);
                SeleccionProductos.this.contenedor_calculadora.setClickable(false);
                SeleccionProductos.this.contenedor_regalos.setVisibility(4);
                SeleccionProductos.this.contenedor_regalos.setClickable(false);
                SeleccionProductos.this.btn_ofertas.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_productos.setBackgroundResource(C0200R.color.white);
                SeleccionProductos.this.btn_calculadora.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_regalos.setBackgroundResource(C0200R.color.fondo_botones);
            }
        });
        this.btn_calculadora.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.contenedor_ofertas.setVisibility(4);
                SeleccionProductos.this.contenedor_ofertas.setClickable(false);
                SeleccionProductos.this.contenedor_productos.setVisibility(4);
                SeleccionProductos.this.contenedor_productos.setClickable(false);
                SeleccionProductos.this.contenedor_calculadora.setVisibility(0);
                SeleccionProductos.this.contenedor_calculadora.setClickable(true);
                SeleccionProductos.this.contenedor_regalos.setVisibility(4);
                SeleccionProductos.this.contenedor_regalos.setClickable(false);
                SeleccionProductos.this.btn_ofertas.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_productos.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_calculadora.setBackgroundResource(C0200R.color.white);
                SeleccionProductos.this.btn_regalos.setBackgroundResource(C0200R.color.fondo_botones);
            }
        });
        this.btn_regalos.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.contenedor_ofertas.setVisibility(4);
                SeleccionProductos.this.contenedor_ofertas.setClickable(false);
                SeleccionProductos.this.contenedor_productos.setVisibility(4);
                SeleccionProductos.this.contenedor_productos.setClickable(false);
                SeleccionProductos.this.contenedor_calculadora.setVisibility(4);
                SeleccionProductos.this.contenedor_calculadora.setClickable(false);
                SeleccionProductos.this.contenedor_regalos.setVisibility(0);
                SeleccionProductos.this.contenedor_regalos.setClickable(true);
                SeleccionProductos.this.btn_ofertas.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_productos.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_calculadora.setBackgroundResource(C0200R.color.fondo_botones);
                SeleccionProductos.this.btn_regalos.setBackgroundResource(C0200R.color.white);
            }
        });
        this.btn_next_combo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeleccionProductos.this.gridofertas_2.setVisibility(0);
                SeleccionProductos.this.grilla_combos.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setEnabled(false);
                SeleccionProductos.this.btn_back_combo.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setImageResource(C0200R.C0201drawable.ico_next_disabled);
                SeleccionProductos.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter);
                SeleccionProductos.this.btn_next.setEnabled(true);
                SeleccionProductos.this.btn_ofertas.setEnabled(true);
                SeleccionProductos.this.btn_productos.setEnabled(true);
                SeleccionProductos.this.btn_calculadora.setEnabled(true);
                SeleccionProductos.this.btn_regalos.setEnabled(true);
                SeleccionProductos.this.carro_compras.setEnabled(true);
            }
        });
        this.btn_back_combo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                for (int i = SeleccionProductos.this.itemsCarroCompras.size() - 1; i >= 0; i--) {
                    Producto prod_carro = (Producto) SeleccionProductos.this.itemsCarroCompras.get(i);
                    if (prod_carro.getId_combo().equalsIgnoreCase(SeleccionProductos.this.id_combo + "")) {
                        SeleccionProductos.this.itemsCarroCompras.remove(i);
                        SeleccionProductos.this.monto_compra -= prod_carro.getMonto();
                        SeleccionProductos.this.monto.setText(Utiles.formatear_a_pesos(SeleccionProductos.this.monto_compra));
                    }
                }
                SeleccionProductos.this.carro_compras.setAdapter(new CarroComprasAdapter(SeleccionProductos.this.context, SeleccionProductos.this.itemsCarroCompras));
                SeleccionProductos.this.gridofertas_2.setVisibility(0);
                SeleccionProductos.this.grilla_combos.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setEnabled(false);
                SeleccionProductos.this.btn_back_combo.setVisibility(4);
                SeleccionProductos.this.btn_next_combo.setImageResource(C0200R.C0201drawable.ico_next_disabled);
                SeleccionProductos.this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter);
                SeleccionProductos.this.btn_next.setEnabled(true);
                SeleccionProductos.this.btn_ofertas.setEnabled(true);
                SeleccionProductos.this.btn_productos.setEnabled(true);
                SeleccionProductos.this.btn_calculadora.setEnabled(true);
                SeleccionProductos.this.btn_regalos.setEnabled(true);
                SeleccionProductos.this.carro_compras.setEnabled(true);
            }
        });
        this.f15db = new TransaccionSQLiteHelper(this.context, "DBTransacciones", null, 1).getWritableDatabase();
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService("connectivity");
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        Log.v("statusInternet", "No hay internet");
        return false;
    }

    /* JADX WARNING: type inference failed for: r32v0, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r0v9, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r32v2, types: [java.io.InputStream] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 2 */
    @android.annotation.TargetApi(19)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cargar_productos() {
        /*
            r43 = this;
            android.content.res.AssetManager r18 = r43.getAssets()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r43
            r0.maestroProductos = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r43
            r0.itemsProductos = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r43
            r0.itemsProductos_2 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r43
            r0.itemsProductos_3 = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r43
            r0.itemsComboProducto = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = r43
            r0.itemsCarroCompras = r2
            r0 = r43
            boolean r2 = r0.json_creado     // Catch:{ Exception -> 0x02b1 }
            if (r2 != 0) goto L_0x0046
            r0 = r43
            boolean r2 = r0.creacion_correcta     // Catch:{ Exception -> 0x02b1 }
            if (r2 == 0) goto L_0x0283
        L_0x0046:
            java.lang.String r2 = "Json Productos"
            java.lang.String r8 = "Abriendo desde archivo"
            android.util.Log.d(r2, r8)     // Catch:{ Exception -> 0x02b1 }
            java.io.File r27 = new java.io.File     // Catch:{ Exception -> 0x02b1 }
            java.io.File r2 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            java.lang.String r8 = r0.NAME_OF_FOLDER     // Catch:{ Exception -> 0x02b1 }
            r0 = r27
            r0.<init>(r2, r8)     // Catch:{ Exception -> 0x02b1 }
            r27.mkdirs()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r28 = r27.getAbsolutePath()     // Catch:{ Exception -> 0x02b1 }
            java.io.File r24 = new java.io.File     // Catch:{ Exception -> 0x02b1 }
            r0 = r24
            r1 = r28
            r0.<init>(r1)     // Catch:{ Exception -> 0x02b1 }
            boolean r2 = r24.exists()     // Catch:{ Exception -> 0x02b1 }
            if (r2 != 0) goto L_0x0075
            r24.mkdirs()     // Catch:{ Exception -> 0x02b1 }
        L_0x0075:
            java.io.File r26 = new java.io.File     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "json_productos.json"
            r0 = r26
            r1 = r24
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x02b1 }
            java.io.FileInputStream r32 = new java.io.FileInputStream     // Catch:{ Exception -> 0x02b1 }
            r0 = r32
            r1 = r26
            r0.<init>(r1)     // Catch:{ Exception -> 0x02b1 }
        L_0x0089:
            int r39 = r32.available()     // Catch:{ Exception -> 0x02b1 }
            r0 = r39
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x02b1 }
            r19 = r0
            r0 = r32
            r1 = r19
            r0.read(r1)     // Catch:{ Exception -> 0x02b1 }
            r32.close()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r41 = new java.lang.String     // Catch:{ Exception -> 0x02b1 }
            r0 = r41
            r1 = r19
            r0.<init>(r1)     // Catch:{ Exception -> 0x02b1 }
            org.json.JSONObject r34 = new org.json.JSONObject     // Catch:{ Exception -> 0x02b1 }
            r0 = r34
            r1 = r41
            r0.<init>(r1)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "products"
            r0 = r34
            org.json.JSONArray r22 = r0.getJSONArray(r2)     // Catch:{ Exception -> 0x02b1 }
            r29 = 0
        L_0x00b9:
            int r2 = r22.length()     // Catch:{ Exception -> 0x02b1 }
            r0 = r29
            if (r0 >= r2) goto L_0x0328
            java.lang.String r2 = "Json Productos"
            java.lang.String r8 = "Entre al loop"
            android.util.Log.d(r2, r8)     // Catch:{ Exception -> 0x02b1 }
            r0 = r22
            r1 = r29
            org.json.JSONObject r20 = r0.getJSONObject(r1)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "id"
            r0 = r20
            java.lang.String r6 = r0.getString(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "name"
            r0 = r20
            java.lang.String r5 = r0.getString(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "price"
            r0 = r20
            int r3 = r0.getInt(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "type"
            r0 = r20
            java.lang.String r7 = r0.getString(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "tab"
            r0 = r20
            int r40 = r0.getInt(r2)     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            android.content.Context r2 = r0.context     // Catch:{ Exception -> 0x02b1 }
            android.content.res.Resources r38 = r2.getResources()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "image_url"
            r0 = r20
            java.lang.String r2 = r0.getString(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = ".png"
            java.lang.String r10 = ""
            java.lang.String r2 = r2.replace(r8, r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = ".PNG"
            java.lang.String r10 = ""
            java.lang.String r2 = r2.replace(r8, r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = "drawable"
            r0 = r43
            android.content.Context r10 = r0.context     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = r10.getPackageName()     // Catch:{ Exception -> 0x02b1 }
            r0 = r38
            int r4 = r0.getIdentifier(r2, r8, r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02b1 }
            r2.<init>()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = "image_url"
            r0 = r20
            java.lang.String r8 = r0.getString(r8)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = ".png"
            java.lang.String r11 = ""
            java.lang.String r8 = r8.replace(r10, r11)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = ".PNG"
            java.lang.String r11 = ""
            java.lang.String r8 = r8.replace(r10, r11)     // Catch:{ Exception -> 0x02b1 }
            java.lang.StringBuilder r2 = r2.append(r8)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = "_off"
            java.lang.StringBuilder r2 = r2.append(r8)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = "drawable"
            r0 = r43
            android.content.Context r10 = r0.context     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = r10.getPackageName()     // Catch:{ Exception -> 0x02b1 }
            r0 = r38
            int r9 = r0.getIdentifier(r2, r8, r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "Json Productos"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02b1 }
            r8.<init>()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = "Dato --> "
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.StringBuilder r8 = r8.append(r6)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = " - "
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.StringBuilder r8 = r8.append(r5)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = " - "
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.StringBuilder r8 = r8.append(r3)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = " - "
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = "image_url"
            r0 = r20
            java.lang.String r10 = r0.getString(r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r11 = ".png"
            java.lang.String r42 = ""
            r0 = r42
            java.lang.String r10 = r10.replace(r11, r0)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r11 = ".PNG"
            java.lang.String r42 = ""
            r0 = r42
            java.lang.String r10 = r10.replace(r11, r0)     // Catch:{ Exception -> 0x02b1 }
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = " - "
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch:{ Exception -> 0x02b1 }
            java.lang.StringBuilder r8 = r8.append(r4)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x02b1 }
            android.util.Log.d(r2, r8)     // Catch:{ Exception -> 0x02b1 }
            r2 = 1
            r0 = r40
            if (r0 != r2) goto L_0x0294
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r0 = r0.itemsProductos     // Catch:{ Exception -> 0x02b1 }
            r42 = r0
            cl.android.poc.cashless.clases.Producto r2 = new cl.android.poc.cashless.clases.Producto     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = ""
            java.lang.String r10 = ""
            java.lang.String r11 = ""
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x02b1 }
            r0 = r42
            r0.add(r2)     // Catch:{ Exception -> 0x02b1 }
        L_0x01da:
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r0 = r0.maestroProductos     // Catch:{ Exception -> 0x02b1 }
            r42 = r0
            cl.android.poc.cashless.clases.Producto r2 = new cl.android.poc.cashless.clases.Producto     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = ""
            java.lang.String r10 = ""
            java.lang.String r11 = ""
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x02b1 }
            r0 = r42
            r0.add(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "combo"
            boolean r2 = r7.equalsIgnoreCase(r2)     // Catch:{ Exception -> 0x02b1 }
            if (r2 == 0) goto L_0x0324
            java.lang.String r2 = "childnodes"
            r0 = r20
            org.json.JSONObject r23 = r0.getJSONObject(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "quantity"
            r0 = r23
            int r12 = r0.getInt(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "items"
            r0 = r23
            org.json.JSONArray r17 = r0.getJSONArray(r2)     // Catch:{ Exception -> 0x02b1 }
            r33 = 0
        L_0x0212:
            int r2 = r17.length()     // Catch:{ Exception -> 0x02b1 }
            r0 = r33
            if (r0 >= r2) goto L_0x0324
            java.lang.String r2 = "Json Productos Combos"
            java.lang.String r8 = "Entre al loop"
            android.util.Log.d(r2, r8)     // Catch:{ Exception -> 0x02b1 }
            r0 = r17
            r1 = r33
            org.json.JSONObject r21 = r0.getJSONObject(r1)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "min"
            r0 = r21
            int r13 = r0.getInt(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "max"
            r0 = r21
            int r14 = r0.getInt(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "tag"
            r0 = r21
            java.lang.String r15 = r0.getString(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "id_productos"
            r0 = r21
            java.lang.String r31 = r0.optString(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "Json Productos Combos"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02b1 }
            r8.<init>()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r10 = "Entre al loop --> id_productos =  "
            java.lang.StringBuilder r8 = r8.append(r10)     // Catch:{ Exception -> 0x02b1 }
            r0 = r31
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x02b1 }
            android.util.Log.d(r2, r8)     // Catch:{ Exception -> 0x02b1 }
            java.util.ArrayList r16 = new java.util.ArrayList     // Catch:{ Exception -> 0x02b1 }
            r16.<init>()     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = ""
            r0 = r31
            boolean r2 = r0.equalsIgnoreCase(r2)     // Catch:{ Exception -> 0x02b1 }
            if (r2 == 0) goto L_0x02ce
            int r12 = r12 - r14
        L_0x0273:
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Combo_Producto> r2 = r0.itemsComboProducto     // Catch:{ Exception -> 0x02b1 }
            cl.android.poc.cashless.clases.Combo_Producto r10 = new cl.android.poc.cashless.clases.Combo_Producto     // Catch:{ Exception -> 0x02b1 }
            r11 = r6
            r10.<init>(r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x02b1 }
            r2.add(r10)     // Catch:{ Exception -> 0x02b1 }
            int r33 = r33 + 1
            goto L_0x0212
        L_0x0283:
            java.lang.String r2 = "Json Productos"
            java.lang.String r8 = "Abriendo desde Assets"
            android.util.Log.d(r2, r8)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "json_productos.json"
            r0 = r18
            java.io.InputStream r32 = r0.open(r2)     // Catch:{ Exception -> 0x02b1 }
            goto L_0x0089
        L_0x0294:
            r2 = 2
            r0 = r40
            if (r0 != r2) goto L_0x02b6
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r0 = r0.itemsProductos_2     // Catch:{ Exception -> 0x02b1 }
            r42 = r0
            cl.android.poc.cashless.clases.Producto r2 = new cl.android.poc.cashless.clases.Producto     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = ""
            java.lang.String r10 = ""
            java.lang.String r11 = ""
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x02b1 }
            r0 = r42
            r0.add(r2)     // Catch:{ Exception -> 0x02b1 }
            goto L_0x01da
        L_0x02b1:
            r25 = move-exception
            r25.printStackTrace()
        L_0x02b5:
            return
        L_0x02b6:
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r0 = r0.itemsProductos_3     // Catch:{ Exception -> 0x02b1 }
            r42 = r0
            cl.android.poc.cashless.clases.Producto r2 = new cl.android.poc.cashless.clases.Producto     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r8 = ""
            java.lang.String r10 = ""
            java.lang.String r11 = ""
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x02b1 }
            r0 = r42
            r0.add(r2)     // Catch:{ Exception -> 0x02b1 }
            goto L_0x01da
        L_0x02ce:
            java.lang.String r2 = "id_productos"
            r0 = r21
            java.lang.String r31 = r0.getString(r2)     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = "-"
            r0 = r31
            java.lang.String[] r37 = r0.split(r2)     // Catch:{ Exception -> 0x02b1 }
            r35 = 0
        L_0x02e0:
            r0 = r37
            int r2 = r0.length     // Catch:{ Exception -> 0x02b1 }
            r0 = r35
            if (r0 >= r2) goto L_0x0273
            r30 = r37[r35]     // Catch:{ Exception -> 0x02b1 }
            r36 = 0
        L_0x02eb:
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r2 = r0.maestroProductos     // Catch:{ Exception -> 0x02b1 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x02b1 }
            r0 = r36
            if (r0 >= r2) goto L_0x0321
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r2 = r0.maestroProductos     // Catch:{ Exception -> 0x02b1 }
            r0 = r36
            java.lang.Object r2 = r2.get(r0)     // Catch:{ Exception -> 0x02b1 }
            cl.android.poc.cashless.clases.Producto r2 = (p004cl.android.poc.cashless.clases.Producto) r2     // Catch:{ Exception -> 0x02b1 }
            java.lang.String r2 = r2.getId_producto()     // Catch:{ Exception -> 0x02b1 }
            r0 = r30
            boolean r2 = r2.equalsIgnoreCase(r0)     // Catch:{ Exception -> 0x02b1 }
            if (r2 == 0) goto L_0x031e
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r2 = r0.maestroProductos     // Catch:{ Exception -> 0x02b1 }
            r0 = r36
            java.lang.Object r2 = r2.get(r0)     // Catch:{ Exception -> 0x02b1 }
            r0 = r16
            r0.add(r2)     // Catch:{ Exception -> 0x02b1 }
        L_0x031e:
            int r36 = r36 + 1
            goto L_0x02eb
        L_0x0321:
            int r35 = r35 + 1
            goto L_0x02e0
        L_0x0324:
            int r29 = r29 + 1
            goto L_0x00b9
        L_0x0328:
            r0 = r43
            android.widget.GridView r2 = r0.gridofertas     // Catch:{ Exception -> 0x02b1 }
            cl.android.poc.cashless.clases.OfertasAdapter r8 = new cl.android.poc.cashless.clases.OfertasAdapter     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r10 = r0.itemsProductos     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            r8.<init>(r0, r10)     // Catch:{ Exception -> 0x02b1 }
            r2.setAdapter(r8)     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            android.widget.GridView r2 = r0.gridofertas_2     // Catch:{ Exception -> 0x02b1 }
            cl.android.poc.cashless.clases.OfertasAdapter r8 = new cl.android.poc.cashless.clases.OfertasAdapter     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r10 = r0.itemsProductos_2     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            r8.<init>(r0, r10)     // Catch:{ Exception -> 0x02b1 }
            r2.setAdapter(r8)     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            android.widget.GridView r2 = r0.grilla_regalos     // Catch:{ Exception -> 0x02b1 }
            cl.android.poc.cashless.clases.OfertasAdapter r8 = new cl.android.poc.cashless.clases.OfertasAdapter     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            java.util.ArrayList<cl.android.poc.cashless.clases.Producto> r10 = r0.itemsProductos_3     // Catch:{ Exception -> 0x02b1 }
            r0 = r43
            r8.<init>(r0, r10)     // Catch:{ Exception -> 0x02b1 }
            r2.setAdapter(r8)     // Catch:{ Exception -> 0x02b1 }
            goto L_0x02b5
        */
        throw new UnsupportedOperationException("Method not decompiled: p004cl.android.poc.cashless.SeleccionProductos.cargar_productos():void");
    }

    /* access modifiers changed from: private */
    public void ultima_trx() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_ultima_trx, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        ((TextView) vista_config.findViewById(C0200R.C0202id.texto_trx)).setText(this.prefs.getString("texto_ultima_trx", "Sin Historial"));
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Última Transacción");
        alertDialogBuilder.create().show();
    }

    public void onResume() {
        super.onResume();
        Log.d("SeleccionProductos", "onResume");
    }

    public void onPause() {
        super.onPause();
        Log.d("SeleccionProductos", "onPause");
    }

    /* access modifiers changed from: private */
    public void eliminar_producto(int position) {
        Producto prod = (Producto) this.itemsCarroCompras.get(position);
        Log.d("eliminar_producto", "Eliminado Item de Posicion " + position);
        if (prod.getEs_combo().equalsIgnoreCase("SI")) {
            this.itemsCarroCompras.remove(position);
            this.monto_compra -= prod.getMonto();
            this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
            for (int i = this.itemsCarroCompras.size() - 1; i >= 0; i--) {
                Producto prod_carro = (Producto) this.itemsCarroCompras.get(i);
                if (prod_carro.getId_combo().equalsIgnoreCase(prod.getId_combo())) {
                    this.itemsCarroCompras.remove(i);
                    this.monto_compra -= prod_carro.getMonto();
                    this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
                }
            }
            this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
            return;
        }
        this.itemsCarroCompras.remove(position);
        this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
        this.monto_compra -= prod.getMonto();
        this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
    }

    /* access modifiers changed from: private */
    public void agregar_producto(int position) {
        Producto prod = (Producto) this.itemsProductos.get(position);
        this.itemsCarroCompras.add(prod);
        this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
        this.monto_compra = prod.getMonto() + this.monto_compra;
        this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
        if (prod.getTipo_producto().equalsIgnoreCase("combo")) {
            Toast.makeText(this.context, "Es un combo...", 1).show();
            cambiar_a_combo(prod);
        }
    }

    /* access modifiers changed from: private */
    public void agregar_producto_grilla_2(int position) {
        Producto prod = (Producto) this.itemsProductos_2.get(position);
        this.itemsCarroCompras.add(prod);
        this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
        this.monto_compra = prod.getMonto() + this.monto_compra;
        this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
        if (prod.getTipo_producto().equalsIgnoreCase("combo")) {
            Toast.makeText(this.context, "Es un combo...", 1).show();
            cambiar_a_combo(prod);
        }
    }

    /* access modifiers changed from: private */
    public void agregar_producto_grilla_3(int position) {
        Producto prod = (Producto) this.itemsProductos_3.get(position);
        this.itemsCarroCompras.add(prod);
        this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
        this.monto_compra = prod.getMonto() + this.monto_compra;
        this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
        if (prod.getTipo_producto().equalsIgnoreCase("combo")) {
            Toast.makeText(this.context, "Es un combo...", 1).show();
            cambiar_a_combo(prod);
        }
    }

    private void cambiar_a_combo(Producto prod) {
        this.gridofertas_2.setVisibility(4);
        this.grilla_combos.setVisibility(0);
        this.btn_next_combo.setVisibility(0);
        this.btn_next_combo.setEnabled(false);
        this.btn_ofertas.setEnabled(false);
        this.btn_productos.setEnabled(false);
        this.btn_calculadora.setEnabled(false);
        this.btn_regalos.setEnabled(false);
        this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter_disabled);
        this.btn_next.setEnabled(false);
        this.btn_back_combo.setVisibility(0);
        this.carro_compras.setEnabled(false);
        this.id_combo++;
        this.itemsCombos = new ArrayList<>();
        this.max_min = new HashMap<>();
        Producto prodPapa = prod;
        prodPapa.setId_combo(this.id_combo + "");
        prodPapa.setEs_combo("SI");
        this.itemsCombos.add(prod);
        this.itemsCombos.add(new Producto(0, C0200R.C0201drawable.slash, "slash", "generico", "slash", "slash", C0200R.C0201drawable.slash, "", ""));
        for (int i = 0; i < this.itemsComboProducto.size(); i++) {
            Log.d("Json Productos", "Entre al loop combo Prod");
            if (((Combo_Producto) this.itemsComboProducto.get(i)).getId_producto_padre().equalsIgnoreCase(prod.getId_producto())) {
                Combo_Producto combo = (Combo_Producto) this.itemsComboProducto.get(i);
                this.quantity = combo.getQuantity();
                this.max_min.put(combo.getTags() + "_min", Integer.valueOf(combo.getMin()));
                this.max_min.put(combo.getTags() + "_max", Integer.valueOf(combo.getMax()));
                this.max_min.put(combo.getTags(), Integer.valueOf(0));
                this.max_min.put("cantidad_total", Integer.valueOf(0));
                Log.d("Json Combos", "Quantiy = " + this.quantity);
                for (int j = 0; j < combo.getProductos_combo().size(); j++) {
                    Producto prod_combo = (Producto) combo.getProductos_combo().get(j);
                    this.itemsCombos.add(new Producto(0, prod_combo.getDrawrable_foto(), prod_combo.getNombre_producto(), prod_combo.getId_producto(), prod_combo.getTipo_producto(), combo.getTags(), prod_combo.getDrawrable_foto_des(), this.id_combo + "", "SI"));
                    Log.d("Json Combos", "Tags = " + combo.getTags());
                }
            }
        }
        this.grilla_combos.setAdapter(new OfertasAdapter(this, this.itemsCombos));
    }

    /* access modifiers changed from: private */
    public void agregar_producto_combo(int position) {
        if (position <= 1) {
            return;
        }
        if (((Integer) this.max_min.get("cantidad_total")).intValue() == this.quantity) {
            Toast.makeText(this.context, "Ha alcanzando el maximo de productos...", 1).show();
            return;
        }
        Producto prod = (Producto) this.itemsCombos.get(position);
        if (this.max_min.get(prod.getTag()) == this.max_min.get(prod.getTag() + "_max")) {
            Toast.makeText(this.context, "Ha alcanzando el maximo de este producto...", 1).show();
            return;
        }
        int max = ((Integer) this.max_min.get(prod.getTag() + "_max")).intValue();
        int min = ((Integer) this.max_min.get(prod.getTag() + "_min")).intValue();
        int cantidad = ((Integer) this.max_min.get(prod.getTag())).intValue() + 1;
        int cantidad_total = ((Integer) this.max_min.get("cantidad_total")).intValue() + 1;
        Log.d("Agregar Prod Combos", "max = " + max);
        Log.d("Agregar Prod Combos", "min = " + min);
        Log.d("Agregar Prod Combos", "llevo " + cantidad + " de " + prod.getTag());
        Log.d("Agregar Prod Combos", "llevo " + cantidad + " del total ");
        this.max_min.put(prod.getTag(), Integer.valueOf(cantidad));
        this.max_min.put("cantidad_total", Integer.valueOf(cantidad_total));
        this.itemsCarroCompras.add(prod);
        this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
        this.monto_compra = prod.getMonto() + this.monto_compra;
        this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
        if (cantidad_total == this.quantity) {
            this.btn_next_combo.setImageResource(C0200R.C0201drawable.ico_next);
            this.btn_next_combo.setEnabled(true);
            Producto prodPapa = (Producto) this.itemsCombos.get(0);
            prodPapa.setId_combo(this.id_combo + "");
            prodPapa.setEs_combo("SI");
            this.itemsCombos = new ArrayList<>();
            this.itemsCombos.add(prodPapa);
            this.itemsCombos.add(new Producto(0, C0200R.C0201drawable.slash, "slash", "generico", "slash", "slash", C0200R.C0201drawable.slash, "", ""));
            for (int i = 0; i < this.itemsComboProducto.size(); i++) {
                Log.d("Json Productos", "Entre al loop combo Prod");
                if (((Combo_Producto) this.itemsComboProducto.get(i)).getId_producto_padre().equalsIgnoreCase(prodPapa.getId_producto())) {
                    Combo_Producto combo = (Combo_Producto) this.itemsComboProducto.get(i);
                    Log.d("Json Combos", "Quantiy = " + this.quantity);
                    for (int j = 0; j < combo.getProductos_combo().size(); j++) {
                        Producto prod_combo = (Producto) combo.getProductos_combo().get(j);
                        this.itemsCombos.add(new Producto(0, prod_combo.getDrawrable_foto_des(), prod_combo.getNombre_producto(), prod_combo.getId_producto(), prod_combo.getTipo_producto(), combo.getTags(), prod_combo.getDrawrable_foto_des(), this.id_combo + "", "SI"));
                    }
                }
            }
            this.grilla_combos.setAdapter(new OfertasAdapter(this, this.itemsCombos));
        } else if (this.max_min.get(prod.getTag()) == this.max_min.get(prod.getTag() + "_max")) {
            Producto prodPapa2 = (Producto) this.itemsCombos.get(0);
            prodPapa2.setId_combo(this.id_combo + "");
            prodPapa2.setEs_combo("SI");
            this.itemsCombos = new ArrayList<>();
            this.itemsCombos.add(prodPapa2);
            this.itemsCombos.add(new Producto(0, C0200R.C0201drawable.slash, "slash", "generico", "slash", "slash", C0200R.C0201drawable.slash, "", ""));
            for (int i2 = 0; i2 < this.itemsComboProducto.size(); i2++) {
                Log.d("Json Productos", "Entre al loop combo Prod");
                if (((Combo_Producto) this.itemsComboProducto.get(i2)).getId_producto_padre().equalsIgnoreCase(prodPapa2.getId_producto())) {
                    Combo_Producto combo2 = (Combo_Producto) this.itemsComboProducto.get(i2);
                    Log.d("Json Combos", "Quantiy = " + this.quantity);
                    for (int j2 = 0; j2 < combo2.getProductos_combo().size(); j2++) {
                        Producto prod_combo2 = (Producto) combo2.getProductos_combo().get(j2);
                        if (prod.getTag().equalsIgnoreCase(combo2.getTags())) {
                            this.itemsCombos.add(new Producto(0, prod_combo2.getDrawrable_foto_des(), prod_combo2.getNombre_producto(), prod_combo2.getId_producto(), prod_combo2.getTipo_producto(), combo2.getTags(), prod_combo2.getDrawrable_foto_des(), this.id_combo + "", "SI"));
                        } else {
                            this.itemsCombos.add(new Producto(0, prod_combo2.getDrawrable_foto(), prod_combo2.getNombre_producto(), prod_combo2.getId_producto(), prod_combo2.getTipo_producto(), combo2.getTags(), prod_combo2.getDrawrable_foto_des(), this.id_combo + "", "SI"));
                        }
                    }
                }
            }
            this.grilla_combos.setAdapter(new OfertasAdapter(this, this.itemsCombos));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0200R.C0203menu.menu_seleccion_productos, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == C0200R.C0202id.action_settings) {
            View vista_config = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_configuracion, null);
            Builder alertDialogBuilder = new Builder(this.context);
            this.url_servicios = this.prefs.getString("url_ws", "www.poc.cl");
            alertDialogBuilder.setView(vista_config);
            final EditText userInput = (EditText) vista_config.findViewById(C0200R.C0202id.InputUrl);
            userInput.setText(this.url_servicios);
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String url_ingresada = userInput.getText().toString().trim();
                    if (url_ingresada.equalsIgnoreCase("")) {
                        Toast.makeText(SeleccionProductos.this.context, "Debe ingresar la informacion", 0).show();
                        return;
                    }
                    SeleccionProductos.this.editor.putString("url_ws", url_ingresada);
                    SeleccionProductos.this.editor.commit();
                    SeleccionProductos.this.url_servicios = url_ingresada;
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alertDialogBuilder.create().show();
            return true;
        } else if (id == C0200R.C0202id.action_json) {
            if (checkInternetConnection()) {
                new Escribir_Json().execute(new String[0]);
                return true;
            }
            Toast.makeText(this.context, "No Dispones de conexion a Internet", 1).show();
            return true;
        } else if (id == C0200R.C0202id.action_id) {
            abrirDialogoId();
            return true;
        } else if (id == C0200R.C0202id.action_trx) {
            abrirDialogoTransacciones();
            return true;
        } else if (id == C0200R.C0202id.action_archivo) {
            GenerarArchivo();
            return true;
        } else if (id != C0200R.C0202id.action_total) {
            return super.onOptionsItemSelected(item);
        } else {
            abrirDialogoTotal();
            return true;
        }
    }

    private void GenerarArchivo() {
        Log.e("Lista de Transacciones", "Generando Archivo...");
        this.progressDialog.setMessage("Generando Archivo...");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String fecha = "" + calendar.get(1) + (calendar.get(2) + 1) + calendar.get(5) + calendar.get(10) + calendar.get(12) + calendar.get(13);
        try {
            File folderPathByEquipment = new File(Environment.getExternalStorageDirectory(), "Poc_Barra/log_cierre");
            folderPathByEquipment.mkdirs();
            File dir = new File(folderPathByEquipment.getAbsolutePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream stream = new FileOutputStream(new File(dir, "log_" + fecha + ".csv"));
            String str = "";
            Cursor c = this.f15db.rawQuery("SELECT id_transaccion, device_id, qr, monto_saldo, monto_compra, mto_saldo_final, productos, cant_productos, tipo_trx,fecha FROM Transacciones order by id_transaccion desc", null);
            stream.write("ID_TRANSACCION;DEVICE_ID;QR;MONTO_SALDO;MONTO_COMPRA;MTO_SALDO_FINAL;PRODUCTOS;CANT_PRODUCTOS;TIPO_TRX;FECHA\n".getBytes());
            if (c.moveToFirst()) {
                do {
                    stream.write((c.getInt(0) + ";" + c.getString(1) + ";" + c.getString(2) + ";" + c.getInt(3) + ";" + c.getInt(4) + ";" + c.getInt(5) + ";" + c.getString(6) + ";" + c.getInt(7) + ";" + c.getString(8) + ";" + c.getString(9) + "\n").getBytes());
                } while (c.moveToNext());
            }
            this.progressDialog.dismiss();
            stream.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            this.progressDialog.dismiss();
        } catch (Exception e2) {
            e2.printStackTrace();
            this.progressDialog.dismiss();
        }
    }

    private void abrirDialogoTransacciones() {
        View vista_grilla = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_todas_trx, null);
        List items = new ArrayList();
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_grilla);
        try {
            Cursor c = this.f15db.rawQuery("SELECT qr, productos, monto_compra,  cant_productos, fecha FROM Transacciones order by id_transaccion desc", null);
            if (c.moveToFirst()) {
                do {
                    String qr = c.getString(0);
                    String productos = c.getString(1);
                    int monto_compra2 = c.getInt(2);
                    int cant_productos = c.getInt(3);
                    String fecha = c.getString(4);
                    items.add(new TransaccionesRealizadas(qr, productos, monto_compra2, cant_productos, fecha));
                    Log.e("Lista de Transacciones", "Enviando --> qr = " + qr);
                    Log.e("Lista de Transacciones", "Enviando --> monto_saldo =  " + this.monto_saldo);
                    Log.e("Lista de Transacciones", "Enviando --> monto_compra =  " + monto_compra2);
                    Log.e("Lista de Transacciones", "Enviando --> cant_productos =  " + cant_productos);
                    Log.e("Lista de Transacciones", "Enviando --> fecha =  " + fecha);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            items = null;
        }
        ListView listaStatus = (ListView) vista_grilla.findViewById(C0200R.C0202id.lista_trx);
        listaStatus.addHeaderView((ViewGroup) getLayoutInflater().inflate(C0200R.layout.lista_item_header, listaStatus, false));
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

    private void abrirDialogoTotal() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_ultima_trx, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        int suma_total = 0;
        try {
            Cursor c = this.f15db.rawQuery("SELECT sum(monto_compra) FROM Transacciones", null);
            if (c.moveToFirst()) {
                do {
                    suma_total = c.getInt(0);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            suma_total = 0;
        }
        ((TextView) vista_config.findViewById(C0200R.C0202id.texto_trx)).setText("Llevas de Ventas \n " + Utiles.formatear_a_pesos(suma_total));
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Total de Ventas");
        alertDialogBuilder.create().show();
    }

    private void abrirDialogoId() {
        View vista_config = LayoutInflater.from(this.context).inflate(C0200R.layout.dialogo_ultima_trx, null);
        Builder alertDialogBuilder = new Builder(this.context);
        alertDialogBuilder.setView(vista_config);
        TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService("phone");
        ((TextView) vista_config.findViewById(C0200R.C0202id.texto_trx)).setText("Id dispositivo \n " + new UUID((long) ("" + Secure.getString(getContentResolver(), "android_id")).hashCode(), (((long) ("" + tm.getDeviceId()).hashCode()) << 32) | ((long) ("" + tm.getSimSerialNumber()).hashCode())).toString());
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        }).setTitle("Confirmación");
        alertDialogBuilder.create().show();
    }

    public void onClickClear(View miView) {
        this.numero1 = 0;
        this.resultado = 0;
        ((TextView) findViewById(C0200R.C0202id.txt_total_suma)).setText("");
        ((TextView) findViewById(C0200R.C0202id.txt_prev_suma)).setText("");
    }

    public void onClickBtnEnter(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        if (!tv.getText().toString().equalsIgnoreCase("")) {
            this.numero1 = Integer.parseInt(tv.getText().toString());
            this.resultado += this.numero1;
            tv.setText("");
            ((TextView) findViewById(C0200R.C0202id.txt_prev_suma)).setText(Utiles.formatear_a_pesos(this.resultado));
        }
        if (this.resultado > 0) {
            r7 = C0200R.C0201drawable.monto_calculadora;
            Producto prod = new Producto(this.resultado, C0200R.C0201drawable.monto_calculadora, "Calculadora", "calculadora_" + this.resultado, "calc", "calc", C0200R.C0201drawable.monto_calculadora, "", "");
            this.itemsCarroCompras.add(prod);
            this.carro_compras.setAdapter(new CarroComprasAdapter(this, this.itemsCarroCompras));
            this.monto_compra = prod.getMonto() + this.monto_compra;
            this.monto.setText(Utiles.formatear_a_pesos(this.monto_compra));
            this.numero1 = 0;
            this.resultado = 0;
            tv.setText("");
            ((TextView) findViewById(C0200R.C0202id.txt_prev_suma)).setText("");
            return;
        }
        Toast.makeText(this.context, "Debe ingresar un Valor", 0).show();
    }

    public void onClickBtnMas(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        if (!tv.getText().toString().equalsIgnoreCase("")) {
            this.numero1 = Integer.parseInt(tv.getText().toString());
            this.resultado += this.numero1;
            tv.setText("");
            ((TextView) findViewById(C0200R.C0202id.txt_prev_suma)).setText(Utiles.formatear_a_pesos(this.resultado));
            return;
        }
        Toast.makeText(this.context, "Debe ingresar un Valor", 0).show();
    }

    public void onClickBtn1(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "1");
    }

    public void onClickBtn2(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "2");
    }

    public void onClickBtn3(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "3");
    }

    public void onClickBtn4(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "4");
    }

    public void onClickBtn5(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "5");
    }

    public void onClickBtn6(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "6");
    }

    public void onClickBtn7(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "7");
    }

    public void onClickBtn8(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "8");
    }

    public void onClickBtn9(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "9");
    }

    public void onClickBtn0(View miView) {
        TextView tv = (TextView) findViewById(C0200R.C0202id.txt_total_suma);
        tv.setText(tv.getText() + "0");
    }

    /* access modifiers changed from: private */
    public void limpiar_pantalla() {
        this.itemsCarroCompras.clear();
        this.carro_compras.setAdapter(new CarroComprasAdapter(this.context, this.itemsCarroCompras));
        this.monto_compra = 0;
        this.monto.setText(Utiles.formatear_a_pesos(0));
        this.contenedor_ofertas.setVisibility(0);
        this.contenedor_ofertas.setClickable(true);
        this.contenedor_productos.setVisibility(4);
        this.contenedor_productos.setClickable(false);
        this.contenedor_calculadora.setVisibility(4);
        this.contenedor_calculadora.setClickable(false);
        this.contenedor_regalos.setVisibility(4);
        this.contenedor_regalos.setClickable(false);
        this.btn_ofertas.setBackgroundResource(C0200R.color.white);
        this.btn_productos.setBackgroundResource(C0200R.color.fondo_botones);
        this.btn_calculadora.setBackgroundResource(C0200R.color.fondo_botones);
        this.btn_regalos.setBackgroundResource(C0200R.color.fondo_botones);
        this.gridofertas_2.setVisibility(0);
        this.grilla_combos.setVisibility(4);
        this.btn_next_combo.setVisibility(4);
        this.btn_next_combo.setEnabled(false);
        this.btn_back_combo.setVisibility(4);
        this.btn_next_combo.setImageResource(C0200R.C0201drawable.ico_next_disabled);
        this.btn_next.setImageResource(C0200R.C0201drawable.ico_enter);
        this.btn_next.setEnabled(true);
        this.btn_ofertas.setEnabled(true);
        this.btn_productos.setEnabled(true);
        this.btn_calculadora.setEnabled(true);
        this.btn_regalos.setEnabled(true);
        this.carro_compras.setEnabled(true);
    }
}
