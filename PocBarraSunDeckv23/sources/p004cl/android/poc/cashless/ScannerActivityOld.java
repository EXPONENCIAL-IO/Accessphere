package p004cl.android.poc.cashless;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.p003v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import p004cl.android.poc.cashless.clases.Producto;
import p006me.dm7.barcodescanner.zbar.Result;
import p006me.dm7.barcodescanner.zbar.ZBarScannerView;
import p006me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;

/* renamed from: cl.android.poc.cashless.ScannerActivityOld */
public class ScannerActivityOld extends ActionBarActivity implements ResultHandler {
    Cargar_Saldo cargar_saldo = null;
    final Context context = this;
    String id_qr = "";
    ArrayList<Producto> itemsCarroCompras;
    private ZBarScannerView mScannerView;
    int monto_compra = 0;
    int monto_saldo = 0;

    /* renamed from: cl.android.poc.cashless.ScannerActivityOld$Cargar_Saldo */
    private class Cargar_Saldo extends AsyncTask<String, Integer, String> {
        final ProgressDialog progressDialog;

        private Cargar_Saldo() {
            this.progressDialog = new ProgressDialog(ScannerActivityOld.this.context);
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
            if (ScannerActivityOld.this.monto_saldo > 0) {
                Intent intent = new Intent(ScannerActivityOld.this, ResultadoCompras.class);
                intent.putExtra("monto_compra", ScannerActivityOld.this.monto_compra);
                intent.putExtra("monto_saldo", ScannerActivityOld.this.monto_saldo);
                intent.putExtra("id_qr", ScannerActivityOld.this.id_qr);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("carro_compras", ScannerActivityOld.this.itemsCarroCompras);
                intent.putExtras(bundle);
                ScannerActivityOld.this.startActivity(intent);
                ScannerActivityOld.this.finish();
                return;
            }
            Toast.makeText(ScannerActivityOld.this.context, "Error o Saldo Menor a 0", 1).show();
            Intent intent2 = new Intent(ScannerActivityOld.this, ResultadoCompras.class);
            intent2.putExtra("monto_compra", ScannerActivityOld.this.monto_compra);
            intent2.putExtra("monto_saldo", ScannerActivityOld.this.monto_saldo);
            intent2.putExtra("id_qr", ScannerActivityOld.this.id_qr);
            Bundle bundle2 = new Bundle();
            bundle2.putParcelableArrayList("carro_compras", ScannerActivityOld.this.itemsCarroCompras);
            intent2.putExtras(bundle2);
            ScannerActivityOld.this.startActivity(intent2);
            ScannerActivityOld.this.finish();
        }

        private void consulta_saldo() {
            HttpClient httpClient = new DefaultHttpClient();
            String url = "http://testing.poc.cl/event/api/account/qr/" + ScannerActivityOld.this.id_qr;
            Log.d("ResultadoWS", "Url de peticion : " + url);
            HttpGet del = new HttpGet(url);
            del.setHeader("content-type", "application/json");
            try {
                String respStr = EntityUtils.toString(httpClient.execute(del).getEntity());
                Log.d("ResultadoWS", "Recibi del ws : " + respStr);
                JSONObject respJSON = new JSONObject(respStr);
                ScannerActivityOld.this.monto_saldo = respJSON.getInt("balance");
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                ex.printStackTrace();
                ScannerActivityOld.this.monto_saldo = 0;
            }
        }
    }

    public void onCreate(Bundle state) {
        super.onCreate(state);
        this.mScannerView = new ZBarScannerView(this);
        setContentView((View) this.mScannerView);
        this.cargar_saldo = new Cargar_Saldo();
        Log.d("ScannerActivity", "onCreate");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.monto_compra = extras.getInt("monto_compra");
            this.itemsCarroCompras = extras.getParcelableArrayList("carro_compras");
        }
        Log.d("ScannerActivity", "Cantidad de Objetos --> " + this.itemsCarroCompras.size());
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

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService("connectivity");
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        Log.v("statusInternet", "No hay internet");
        return false;
    }
}
