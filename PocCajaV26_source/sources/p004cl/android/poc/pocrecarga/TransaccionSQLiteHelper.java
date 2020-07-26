package p004cl.android.poc.pocrecarga;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/* renamed from: cl.android.poc.pocrecarga.TransaccionSQLiteHelper */
public class TransaccionSQLiteHelper extends SQLiteOpenHelper {
    String sqlCreateTrx = "CREATE TABLE Transacciones (id_transaccion INTEGER, device_id TEXT, qr TEXT, monto_saldo INTEGER, monto_compra INTEGER, mto_saldo_final INTEGER, productos TEXT, cant_productos INTEGER, tipo_trx TEXT, fecha TEXT)";
    String sqlCreateTrx2 = "CREATE TABLE Transacciones (id_transaccion INTEGER, device_id TEXT, qr TEXT, monto_saldo INTEGER, monto_compra INTEGER, mto_saldo_final INTEGER, productos TEXT, cant_productos INTEGER, tipo_trx TEXT, fecha TEXT)";

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(this.sqlCreateTrx);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Transacciones");
        db.execSQL(this.sqlCreateTrx);
    }

    public TransaccionSQLiteHelper(Context contexto, String nombre, CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }
}
