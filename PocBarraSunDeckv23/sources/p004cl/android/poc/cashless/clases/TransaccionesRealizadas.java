package p004cl.android.poc.cashless.clases;

/* renamed from: cl.android.poc.cashless.clases.TransaccionesRealizadas */
public class TransaccionesRealizadas {
    private int cant_productos;
    private String device_id;
    private String fecha;
    private int id_transaccion;
    private int monto_compra;
    private int monto_saldo;
    private int mto_saldo_final;
    private String productos;

    /* renamed from: qr */
    private String f17qr;
    private String tipo_trx;

    public TransaccionesRealizadas(int id_transaccion2, String device_id2, String qr, int monto_saldo2, int monto_compra2, int mto_saldo_final2, String productos2, int cant_productos2, String tipo_trx2, String fecha2) {
        this.id_transaccion = id_transaccion2;
        this.device_id = device_id2;
        this.f17qr = qr;
        this.monto_saldo = monto_saldo2;
        this.monto_compra = monto_compra2;
        this.mto_saldo_final = mto_saldo_final2;
        this.productos = productos2;
        this.cant_productos = cant_productos2;
        this.tipo_trx = tipo_trx2;
        this.fecha = fecha2;
    }

    public TransaccionesRealizadas(String qr, String productos2, int monto_compra2, int cant_productos2, String fecha2) {
        this.f17qr = qr;
        this.productos = productos2;
        this.monto_compra = monto_compra2;
        this.cant_productos = cant_productos2;
        this.fecha = fecha2;
    }

    public int getId_transaccion() {
        return this.id_transaccion;
    }

    public void setId_transaccion(int id_transaccion2) {
        this.id_transaccion = id_transaccion2;
    }

    public String getDevice_id() {
        return this.device_id;
    }

    public void setDevice_id(String device_id2) {
        this.device_id = device_id2;
    }

    public String getQr() {
        return this.f17qr;
    }

    public void setQr(String qr) {
        this.f17qr = qr;
    }

    public int getMonto_saldo() {
        return this.monto_saldo;
    }

    public void setMonto_saldo(int monto_saldo2) {
        this.monto_saldo = monto_saldo2;
    }

    public int getMonto_compra() {
        return this.monto_compra;
    }

    public void setMonto_compra(int monto_compra2) {
        this.monto_compra = monto_compra2;
    }

    public int getMto_saldo_final() {
        return this.mto_saldo_final;
    }

    public void setMto_saldo_final(int mto_saldo_final2) {
        this.mto_saldo_final = mto_saldo_final2;
    }

    public String getProductos() {
        return this.productos;
    }

    public void setProductos(String productos2) {
        this.productos = productos2;
    }

    public int getCant_productos() {
        return this.cant_productos;
    }

    public void setCant_productos(int cant_productos2) {
        this.cant_productos = cant_productos2;
    }

    public String getTipo_trx() {
        return this.tipo_trx;
    }

    public void setTipo_trx(String tipo_trx2) {
        this.tipo_trx = tipo_trx2;
    }

    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String fecha2) {
        this.fecha = fecha2;
    }
}
